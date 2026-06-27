package com.drive.license.test

import com.drive.license.test.domain.model.LearningCenter

/**
 * Curated list of driving schools in Armenia (static data until map/API integration).
 * One local school per city with a unique brand — smaller schools over national chains.
 * Sourced from RALLY.am, 2GIS, and official school websites.
 */
object LearningCentersData {
    val all = listOf(
        LearningCenter(
            name = "N1 ավտոդպրոց",
            city = "Երևան",
            address = "Տիգրան Մեծի պող., 67Ա, Երևան",
            phone = "+374 41 202303",
            description = "Տեսական և գործնական վարման դասընթացներ, համակարգչային թեստավորում և քննության նախապատրաստում։",
            lat = 40.1382,
            lng = 44.5148,
        ),
        LearningCenter(
            name = "Զեբրա ավտոդպրոց",
            city = "Գյումրի",
            address = "Գարեգին Նժդեհի պող., 8, Գյումրի",
            phone = "+374 94 992992",
            description = "Գյումրու տեղական ավտոդպրոց՝ տեսական և գործնական վարման դասընթացներ։",
            lat = 40.7922,
            lng = 43.8456,
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
        LearningCenter(
            name = "Art Town Drive ավտոդպրոց",
            city = "Արտաշատ",
            address = "Օգոստոսի 23-րդ փող., 64, Արտաշատ",
            phone = "+374 99 134353",
            description = "Արտաշատի տեղական ավտոդպրոց՝ տեսական և գործնական վարման դասընթացներ։",
            lat = 39.9596,
            lng = 44.5410,
        ),
    )
}
