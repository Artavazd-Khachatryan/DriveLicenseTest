package com.drive.license.test.domain.model

data class TrafficSign(
    val id: Long,
    val code: String,
    val category: String,
    val name: String,
    val description: String,
    val imageName: String?,
)
