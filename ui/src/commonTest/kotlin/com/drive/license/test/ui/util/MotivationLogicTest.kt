package com.drive.license.test.ui.util

import com.drive.license.test.domain.model.UserStatistics
import kotlin.test.Test
import kotlin.test.assertEquals

class MotivationLogicTest {

    @Test
    fun homeMotivation_firstVisit_whenNoAttempts() {
        val stats = UserStatistics(totalAttempts = 0)
        assertEquals(HomeMotivationKind.FirstVisit, resolveHomeMotivationKind(stats))
    }

    @Test
    fun homeMotivation_streakStrong_whenSevenOrMoreDays() {
        val stats = UserStatistics(totalAttempts = 10, totalCorrect = 3, totalIncorrect = 7, currentStreak = 7)
        assertEquals(HomeMotivationKind.StreakStrong, resolveHomeMotivationKind(stats))
    }

    @Test
    fun homeMotivation_highAccuracy_whenAboveEightyPercent() {
        val stats = UserStatistics(totalAttempts = 10, totalCorrect = 9, totalIncorrect = 1)
        assertEquals(HomeMotivationKind.HighAccuracy, resolveHomeMotivationKind(stats))
    }

    @Test
    fun testResult_perfect_whenFullScoreAndPassed() {
        assertEquals(TestResultMotivationKind.Perfect, resolveTestResultMotivationKind(1f, passed = true))
    }

    @Test
    fun testResult_almost_whenCloseButFailed() {
        assertEquals(TestResultMotivationKind.Almost, resolveTestResultMotivationKind(0.65f, passed = false))
    }

    @Test
    fun inTestMotivation_showsAtEveryFifthAnswer() {
        assertEquals(false, shouldShowInTestMotivation(4))
        assertEquals(true, shouldShowInTestMotivation(5))
        assertEquals(false, shouldShowInTestMotivation(6))
        assertEquals(true, shouldShowInTestMotivation(10))
    }

    @Test
    fun inTestMotivation_strong_whenEightyPercentOrMoreInSession() {
        assertEquals(InTestMotivationKind.Strong, resolveInTestMotivationKind(5, 4))
        assertEquals(InTestMotivationKind.Steady, resolveInTestMotivationKind(5, 3))
        assertEquals(InTestMotivationKind.KeepGoing, resolveInTestMotivationKind(5, 2))
    }

    @Test
    fun estimatedMinutes_matchesStandardLengths() {
        assertEquals(3, estimatedTestMinutes(10))
        assertEquals(6, estimatedTestMinutes(20))
        assertEquals(9, estimatedTestMinutes(30))
    }
}
