package com.drive.license.test

import com.drive.license.test.domain.model.LearningCenter

/**
 * Curated list of well-known driving schools in Armenia (static data until map/API integration).
 * One representative school per city. Sourced from RALLY.am, 2GIS, and official school websites.
 */
object LearningCentersData {
    val all = listOf(
        LearningCenter(
            name = "BestStart ավտոդպրոց",
            city = "Երևան",
            address = "Տիգրան Մեծի պող., 46, Երևան",
            phone = "+374 99 141455",
            description = "Տեսական և գործնական վարման դասընթացներ, քննական թեստերի նախապատրաստում։",
            lat = 40.1395,
            lng = 44.5270,
        ),
        LearningCenter(
            name = "X-Drive ավտոդպրոց",
            city = "Գյումրի",
            address = "Խաչատուր Աբովյան փող., 218/1, Գյումրի",
            phone = "+374 44 572222",
            description = "Շիրակի մարզի մասնաճյուղ՝ տեսական դասեր և քննական մեքենայով պարապումներ։",
            lat = 40.7895,
            lng = 43.8485,
        ),
        LearningCenter(
            name = "BestStart ավտոդպրոց",
            city = "Վանաձոր",
            address = "Տիգրան Մեծի պող., 18, Վանաձոր",
            phone = "+374 55 730010",
            description = "Լոռու մարզի ավտոդպրոց՝ տեսական և գործնական դասընթացներ, քննության նախապատրաստում։",
            lat = 40.8128,
            lng = 44.4883,
        ),
    )
}
