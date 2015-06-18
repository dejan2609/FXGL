/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.almasb.fxgl.FXGLLogger;

public class Client {

    private static final Logger log = FXGLLogger.getLogger("FXGL.Client");

    private TCPConnectionThread tcpThread = new TCPConnectionThread();
    private UDPConnectionThread udpThread = new UDPConnectionThread();

    private CountDownLatch latch;

    private String serverIP;
    private InetAddress serverAddress;
    private int tcpPort, udpPort;

    private Map<Class<?>, DataParser<? super Serializable> > parsers = new HashMap<>();

    public Client(String serverIP) {
        this(serverIP, NetworkConfig.DEFAULT_TCP_PORT, NetworkConfig.DEFAULT_UDP_PORT);
    }

    public Client(String serverIP, int tcpPort, int udpPort) {
        this.serverIP = serverIP;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;

        tcpThread.setDaemon(true);
        udpThread.setDaemon(true);
    }

    /**
     * Performs an actual connection to the server
     *
     * @return true if connected and all is OK, false if something failed
     * @throws Exception
     */
    public boolean connect() throws Exception {
        serverAddress = InetAddress.getByName(serverIP);

        latch = new CountDownLatch(2);
        tcpThread.start();
        udpThread.start();
        return latch.await(10, TimeUnit.SECONDS);
    }

    public void disconnect() {
        disconnectTCP();
        disconnectUDP();
    }

    private void disconnectTCP() {
        try {
            send(ConnectionMessage.CLOSING, NetworkProtocol.TCP);
        }
        catch (Exception e) {
            log.warning("Client already disconnected");
        }
        tcpThread.running = false;
    }

    private void disconnectUDP() {
        try {
            send(ConnectionMessage.CLOSING, NetworkProtocol.UDP);
        }
        catch (Exception e) {
            log.warning("Client already disconnected");
        }
        udpThread.running = false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> void addParser(Class<T> cl, DataParser<T> parser) {
        parsers.put(cl, (DataParser<? super Serializable>) parser);
    }

    public void send(Serializable data) throws Exception {
        send(data, NetworkProtocol.UDP);
    }

    public void send(Serializable data, NetworkProtocol protocol) throws Exception {
        if (protocol == NetworkProtocol.TCP)
            sendTCP(data);
        else
            sendUDP(data);
    }

    private void sendTCP(Serializable data) throws Exception {
        if (tcpThread.running) {
            tcpThread.outputStream.writeObject(data);
        }
        else {
            throw new IllegalStateException("Client TCP is not connected");
        }
    }

    private void sendUDP(Serializable data) throws Exception {
        if (udpThread.running) {
            byte[] buf = toByteArray(data);
            udpThread.outSocket.send(new DatagramPacket(buf, buf.length, serverAddress, udpPort));
        }
        else {
            throw new IllegalStateException("Client UDP is not connected");
        }
    }

    private static byte[] toByteArray(Serializable data) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutput oo = new ObjectOutputStream(baos)) {
            oo.writeObject(data);
        }

        return baos.toByteArray();
    }

    private class TCPConnectionThread extends Thread {
        private ObjectOutputStream outputStream;
        private boolean running = false;

        @Override
        public void run() {
            try (Socket socket = new Socket(serverIP, tcpPort);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                outputStream = out;
                socket.setTcpNoDelay(true);
                latch.countDown();
                running = true;

                while (running) {
                    Object data = in.readObject();
                    if (data == ConnectionMessage.CLOSE) {
                        running = false;
                        break;
                    }
                    if (data == ConnectionMessage.CLOSING) {
                        sendTCP(ConnectionMessage.CLOSE);
                        running = false;
                        break;
                    }

                    parsers.getOrDefault(data.getClass(), d -> {}).parse((Serializable)data);
                }
            }
            catch (Exception e) {
                log.warning("Exception during TCP connection execution: " + e.getMessage());
                running = false;
                return;
            }

            log.info("TCP connection closed normally");
        }
    }

    private class UDPConnectionThread extends Thread {
        private DatagramSocket outSocket;
        private boolean running = false;

        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket()) {
                outSocket = socket;
                latch.countDown();
                running = true;

                sendUDP(ConnectionMessage.OPEN);

                while (running) {
                    byte[] buf = new byte[16384];
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    socket.receive(datagramPacket);

                    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()))) {
                        Object data = in.readObject();
                        if (data == ConnectionMessage.CLOSE) {
                            running = false;
                            break;
                        }
                        if (data == ConnectionMessage.CLOSING) {
                            sendUDP(ConnectionMessage.CLOSE);
                            running = false;
                            break;
                        }

                        parsers.getOrDefault(data.getClass(), d -> {}).parse((Serializable)data);
                    }
                }
            }
            catch (Exception e) {
                log.warning("Exception during UDP connection execution: " + e.getMessage());
                running = false;
                return;
            }

            log.info("UDP connection closed normally");
        }
    }
}
