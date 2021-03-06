/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

/**
 * fxglTODO: to be implemented.
 * Data structure containing various gameplay related statistics,
 * e.g. time played, enemies killed, etc.
 * This should be designed to be flexible as each game will require
 * specific stats.
 *
 * Also might be a way to integrate this with AchievementManager.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameplayStats {

//    override fun save(profile: UserProfile) {
//        log.debug("Saving data to profile")
//
//        val bundle = Bundle("timer")
//        bundle.put("playtime", playtime.value)
//
//        bundle.log()
//        profile.putBundle(bundle)
//    }
//
//    override fun load(profile: UserProfile) {
//        log.debug("Loading data from profile")
//        val bundle = profile.getBundle("timer")
//        bundle.log()
//
//        playtime.value = bundle.get<Long>("playtime")
//    }

//    /**
//     * Time per frame in seconds.
//     *
//     * @return 0.166(6)
//     */
//    fun tpfSeconds() = 1.0 / 60
//
//    private val TPF_NANOS = 1000000000L / 60
//
//    /**
//     * Timer per frame in nanoseconds.
//     *
//     * @return 16666666
//     */
//    fun tpfNanos() = TPF_NANOS
//
//    /**
//     * Converts seconds to nanoseconds.
//     *
//     * @param seconds value in seconds
//     * @return value in nanoseconds
//     */
//    fun secondsToNanos(seconds: Double) = (seconds * 1000000000L).toLong()
//
//    /**
//     * Converts type Duration to nanoseconds.
//     *
//     * @param duration value as Duration
//     * @return value in nanoseconds
//     */
//    fun toNanos(duration: Duration) = secondsToNanos(duration.toSeconds())

//    fun getPlaytime(): Long {
//        return playtimeProperty().get()
//    }
//
//    fun getPlaytimeHours(): Long {
//        return (getPlaytime().toDouble() / 1000000000.0 / 3600.0).toLong()
//    }
//
//    fun getPlaytimeMinutes(): Long {
//        return (getPlaytime().toDouble() / 1000000000.0 / 60.0).toLong() % 60
//    }
//
//    fun getPlaytimeSeconds(): Long {
//        return (getPlaytime() / 1000000000.0).toLong() % 60
//    }
}