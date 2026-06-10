package com.drive.license.test.domain.model

/** Official Armenian driving-exam color-vision plate (separate from theory questions). */
data class ColorVisionPlate(
    val id: Int,
    val imageName: String,
    val prompt: ColorVisionPrompt,
    val correctAnswer: String,
    val options: List<String>,
)

enum class ColorVisionPrompt {
    /** «Ո՞ր թիվն է պատկերված նկարում» */
    NUMBER,
    /** «Ի՞նչ պատկեր եք տեսնում նկարում» */
    SHAPE,
}
