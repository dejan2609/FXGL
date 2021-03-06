/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay;

import com.almasb.fxgl.annotation.OnUserAction;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.service.NotificationService;
import com.almasb.fxgl.service.ServiceType;
import com.almasb.fxgl.service.impl.notification.FXGLNotificationService;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 * Shows how to use notifications.
 * When app is running press F to show a notification.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class NotificationSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("NotificationSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);

        // 2. by default SlidingNotificationService is used
        // but you can provide your own or use another built-in provider, e.g. FXGLNotificationService
        settings.addServiceType(new ServiceType<NotificationService>() {

            @Override
            public Class<NotificationService> service() {
                return NotificationService.class;
            }

            @Override
            public Class<? extends NotificationService> serviceProvider() {
                return FXGLNotificationService.class;
            }
        });

        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addInputMapping(new InputMapping("Show Notification", KeyCode.F));
    }

    @OnUserAction(name = "Show Notification", type = ActionType.ON_ACTION_BEGIN)
    public void showNotification() {
        // 1. get notification service and push a message
        getNotificationService().pushNotification("Some Message! Tick: " + getTick());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
