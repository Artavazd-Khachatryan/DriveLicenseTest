package com.drive.license.test.ui.util

import com.drive.license.test.domain.model.UserStatistics

enum class HomeMotivationKind {
    FirstVisit,
    StreakStrong,
    StreakGrowing,
    HighAccuracy,
    MidAccuracy,
    LowAccuracy,
}

enum class TestResultMotivationKind {
    Perfect,
    Excellent,
    Pass,
    Almost,
    Fail,
}

fun resolveHomeMotivationKind(stats: UserStatistics): HomeMotivationKind = when {
    stats.totalAttempts == 0 -> HomeMotivationKind.FirstVisit
    stats.currentStreak >= 7 -> HomeMotivationKind.StreakStrong
    stats.currentStreak >= 3 -> HomeMotivationKind.StreakGrowing
    stats.overallAccuracy >= 0.8f -> HomeMotivationKind.HighAccuracy
    stats.overallAccuracy >= 0.5f -> HomeMotivationKind.MidAccuracy
    else -> HomeMotivationKind.LowAccuracy
}

fun resolveTestResultMotivationKind(score: Float, passed: Boolean): TestResultMotivationKind = when {
    passed && score >= 1f -> TestResultMotivationKind.Perfect
    passed && score >= 0.9f -> TestResultMotivationKind.Excellent
    passed -> TestResultMotivationKind.Pass
    score >= 0.6f -> TestResultMotivationKind.Almost
    else -> TestResultMotivationKind.Fail
}
