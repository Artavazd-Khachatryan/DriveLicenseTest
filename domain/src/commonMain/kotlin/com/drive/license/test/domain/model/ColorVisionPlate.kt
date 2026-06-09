package com.drive.license.test.domain.model

/**
 * One Ishihara-style color-vision screening plate (separate from driving theory questions).
 */
data class ColorVisionPlate(
    val id: Int,
    val imageName: String,
    val correctAnswer: String,
    val options: List<String>,
    /** Demo plate — everyone with normal vision should pass (typically "12"). */
    val isDemo: Boolean = false,
)
