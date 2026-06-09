package com.drive.license.test

import com.drive.license.test.domain.model.ColorVisionPlate

/**
 * Offline Ishihara-style plate set for color-vision practice (not a medical diagnosis).
 * Images: [ui/.../drawable/color_plate_XX.png]
 */
object ColorVisionTestData {
    val plates = listOf(
        ColorVisionPlate(
            id = 1,
            imageName = "color_plate_01.png",
            correctAnswer = "12",
            options = listOf("12", "15", "21", "82"),
            isDemo = true,
        ),
        ColorVisionPlate(
            id = 2,
            imageName = "color_plate_02.png",
            correctAnswer = "8",
            options = listOf("8", "3", "5", "9"),
        ),
        ColorVisionPlate(
            id = 3,
            imageName = "color_plate_03.png",
            correctAnswer = "6",
            options = listOf("6", "5", "8", "9"),
        ),
        ColorVisionPlate(
            id = 4,
            imageName = "color_plate_04.png",
            correctAnswer = "29",
            options = listOf("29", "70", "24", "92"),
        ),
        ColorVisionPlate(
            id = 5,
            imageName = "color_plate_05.png",
            correctAnswer = "57",
            options = listOf("57", "35", "75", "17"),
        ),
        ColorVisionPlate(
            id = 6,
            imageName = "color_plate_06.png",
            correctAnswer = "74",
            options = listOf("74", "47", "71", "21"),
        ),
        ColorVisionPlate(
            id = 7,
            imageName = "color_plate_07.png",
            correctAnswer = "7",
            options = listOf("7", "1", "4", "9"),
        ),
        ColorVisionPlate(
            id = 8,
            imageName = "color_plate_08.png",
            correctAnswer = "45",
            options = listOf("45", "49", "54", "44"),
        ),
        ColorVisionPlate(
            id = 9,
            imageName = "color_plate_09.png",
            correctAnswer = "5",
            options = listOf("5", "2", "3", "8"),
        ),
        ColorVisionPlate(
            id = 10,
            imageName = "color_plate_10.png",
            correctAnswer = "2",
            options = listOf("2", "7", "4", "9"),
        ),
    )
}
