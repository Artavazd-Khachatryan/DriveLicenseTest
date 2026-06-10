package com.drive.license.test.ui.util

import androidx.compose.runtime.Composable
import com.drive.license.test.domain.model.ColorVisionPrompt
import drivelicensetest.ui.generated.resources.Res
import drivelicensetest.ui.generated.resources.color_vision_plate_prompt_number
import drivelicensetest.ui.generated.resources.color_vision_plate_prompt_shape
import org.jetbrains.compose.resources.stringResource

@Composable
fun colorVisionPromptText(prompt: ColorVisionPrompt): String = when (prompt) {
    ColorVisionPrompt.NUMBER -> stringResource(Res.string.color_vision_plate_prompt_number)
    ColorVisionPrompt.SHAPE -> stringResource(Res.string.color_vision_plate_prompt_shape)
}
