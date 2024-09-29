package com.drive.license.test

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform