package com.drive.license.test

import com.drive.license.test.domain.model.ColorVisionPlate
import com.drive.license.test.domain.model.ColorVisionPrompt
import com.drive.license.test.domain.model.ColorVisionTestRules

/**
 * Official color-vision plate bank (Central Autoschool / police exam style).
 * Source: https://automotorschool.am/հոգեբանական-թեստեր/
 */
object ColorVisionTestData {
    val allPlates = listOf(
        ColorVisionPlate(
            id = 1,
            imageName = "color_official_01.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "13",
            options = listOf("70", "00", "33", "13"),
        ),
        ColorVisionPlate(
            id = 2,
            imageName = "color_official_02.jpg",
            prompt = ColorVisionPrompt.SHAPE,
            correctAnswer = "շրջան",
            options = listOf("շրջան", "եռանկյուն", "ոչինչ չեմ տեսնում"),
        ),
        ColorVisionPlate(
            id = 3,
            imageName = "color_official_03.png",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "9",
            options = listOf("0", "9", "8"),
        ),
        ColorVisionPlate(
            id = 4,
            imageName = "color_official_04.png",
            prompt = ColorVisionPrompt.SHAPE,
            correctAnswer = "շրջան և եռանկյուն",
            options = listOf("եռանկյուն", "շրջան և եռանկյուն", "շրջան"),
        ),
        ColorVisionPlate(
            id = 5,
            imageName = "color_official_05.png",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "6",
            options = listOf("6", "7", "5"),
        ),
        ColorVisionPlate(
            id = 6,
            imageName = "color_official_06.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "113",
            options = listOf("115", "113", "118", "223"),
        ),
        ColorVisionPlate(
            id = 7,
            imageName = "color_official_07.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "01",
            options = listOf("01", "07", "00", "77"),
        ),
        ColorVisionPlate(
            id = 8,
            imageName = "color_official_08.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "45",
            options = listOf("45", "05", "43", "5"),
        ),
        ColorVisionPlate(
            id = 9,
            imageName = "color_official_09.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "88",
            options = listOf("83", "38", "88", "33"),
        ),
        ColorVisionPlate(
            id = 10,
            imageName = "color_official_10.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "105",
            options = listOf("10", "105", "00", "706"),
        ),
        ColorVisionPlate(
            id = 11,
            imageName = "color_official_11.jpg",
            prompt = ColorVisionPrompt.SHAPE,
            correctAnswer = "շրջան և եռանկյուն",
            options = listOf("շրջան", "շրջան և եռանկյուն", "եռանկյուն"),
        ),
        ColorVisionPlate(
            id = 12,
            imageName = "color_official_12.png",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "13",
            options = listOf("6", "18", "13"),
        ),
        ColorVisionPlate(
            id = 13,
            imageName = "color_official_13.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "96",
            options = listOf("06", "36", "96", "00"),
        ),
        ColorVisionPlate(
            id = 14,
            imageName = "color_official_14.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "09",
            options = listOf("09", "88", "22", "03"),
        ),
        ColorVisionPlate(
            id = 15,
            imageName = "color_official_15.png",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "36",
            options = listOf("66", "86", "36"),
        ),
        ColorVisionPlate(
            id = 16,
            imageName = "color_official_16.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "78",
            options = listOf("18", "8", "78", "70"),
        ),
        ColorVisionPlate(
            id = 17,
            imageName = "color_official_17.jpg",
            prompt = ColorVisionPrompt.NUMBER,
            correctAnswer = "3",
            options = listOf("2", "8", "3", "00"),
        ),
    )

    /** Backwards-compatible alias used by [App]. */
    val plates: List<ColorVisionPlate> = allPlates

    fun platesForExam(): List<ColorVisionPlate> =
        allPlates.shuffled().take(ColorVisionTestRules.EXAM_QUESTION_COUNT)
}
