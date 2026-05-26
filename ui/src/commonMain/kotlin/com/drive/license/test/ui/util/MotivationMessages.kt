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
fun homeMotivationMessage(stats: UserStatistics): String = when (resolveHomeMotivationKind(stats)) {
    HomeMotivationKind.FirstVisit -> stringResource(Res.string.home_motivation_first_visit)
    HomeMotivationKind.StreakStrong -> stringResource(Res.string.home_motivation_streak_strong, stats.currentStreak)
    HomeMotivationKind.StreakGrowing -> stringResource(Res.string.home_motivation_streak_growing, stats.currentStreak)
    HomeMotivationKind.HighAccuracy -> stringResource(Res.string.home_motivation_high_accuracy)
    HomeMotivationKind.MidAccuracy -> stringResource(Res.string.home_motivation_mid_accuracy)
    HomeMotivationKind.LowAccuracy -> stringResource(Res.string.home_motivation_low_accuracy)
}

@Composable
fun testResultMotivationMessage(score: Float, passed: Boolean): String = when (resolveTestResultMotivationKind(score, passed)) {
    TestResultMotivationKind.Perfect -> stringResource(Res.string.results_motivation_perfect)
    TestResultMotivationKind.Excellent -> stringResource(Res.string.results_motivation_excellent)
    TestResultMotivationKind.Pass -> stringResource(Res.string.results_motivation_pass)
    TestResultMotivationKind.Almost -> stringResource(Res.string.results_motivation_almost)
    TestResultMotivationKind.Fail -> stringResource(Res.string.results_motivation_fail)
}
