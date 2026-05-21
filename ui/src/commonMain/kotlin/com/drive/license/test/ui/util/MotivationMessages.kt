package com.drive.license.test.ui.util

import androidx.compose.runtime.Composable
import com.drive.license.test.domain.model.UserStatistics
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.home_motivation_first_visit
import drivelicensetest.ui.generated.resources.home_motivation_high_accuracy
import drivelicensetest.ui.generated.resources.home_motivation_low_accuracy
import drivelicensetest.ui.generated.resources.home_motivation_mid_accuracy
import drivelicensetest.ui.generated.resources.home_motivation_streak_growing
import drivelicensetest.ui.generated.resources.home_motivation_streak_strong
import drivelicensetest.ui.generated.resources.results_motivation_almost
import drivelicensetest.ui.generated.resources.results_motivation_excellent
import drivelicensetest.ui.generated.resources.results_motivation_fail
import drivelicensetest.ui.generated.resources.results_motivation_pass
import drivelicensetest.ui.generated.resources.results_motivation_perfect
import org.jetbrains.compose.resources.stringResource

@Composable
fun homeMotivationMessage(stats: UserStatistics): String = when {
    stats.totalAttempts == 0 -> stringResource(Res.string.home_motivation_first_visit)
    stats.currentStreak >= 7 -> stringResource(Res.string.home_motivation_streak_strong, stats.currentStreak)
    stats.currentStreak >= 3 -> stringResource(Res.string.home_motivation_streak_growing, stats.currentStreak)
    stats.overallAccuracy >= 0.8f -> stringResource(Res.string.home_motivation_high_accuracy)
    stats.overallAccuracy >= 0.5f -> stringResource(Res.string.home_motivation_mid_accuracy)
    else -> stringResource(Res.string.home_motivation_low_accuracy)
}

@Composable
fun testResultMotivationMessage(score: Float, passed: Boolean): String = when {
    passed && score >= 1f -> stringResource(Res.string.results_motivation_perfect)
    passed && score >= 0.9f -> stringResource(Res.string.results_motivation_excellent)
    passed -> stringResource(Res.string.results_motivation_pass)
    score >= 0.6f -> stringResource(Res.string.results_motivation_almost)
    else -> stringResource(Res.string.results_motivation_fail)
}
