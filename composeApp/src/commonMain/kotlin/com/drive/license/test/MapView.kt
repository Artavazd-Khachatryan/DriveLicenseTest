package com.drive.license.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.drive.license.test.domain.model.LearningCenter

@Composable
expect fun MapView(
    centers: List<LearningCenter>,
    modifier: Modifier = Modifier
)
