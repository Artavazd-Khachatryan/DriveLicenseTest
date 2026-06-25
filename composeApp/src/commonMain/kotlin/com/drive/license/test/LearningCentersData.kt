package com.drive.license.test

import com.drive.license.test.domain.model.LearningCenter

/**
 * Curated list of well-known driving schools in Armenia (static data until map/API integration).
 * One representative school per city. Phone numbers are placeholders — verify before release.
 */
object LearningCentersData {
    val all = listOf(
        LearningCenter(
            name = "Ավտոդպրոց «Ֆ1 Ռեյս»",
            city = "Երևան",
            address = "Տիգրան Մեծի փող., Երևան",
            phone = "+374 10 550050",
            description = "Մեծ փորձ ունեցող ավտոդպրոց Երևանում՝ տեսական և practical դասեր, քննական մեքենաներ։",
            lat = 40.1834,
            lng = 44.5121
        ),
        LearningCenter(
            name = "Ավտոդպրոց «Ալֆա»",
            city = "Գյումրի",
            address = "Գյումրի, Շիրակի մարզ",
            phone = "+374 312 33030",
            description = "Շիրակի մարզի առաջատար ավտոդպրոց՝ տեղական քննության նախապատրաստությամբ։",
            lat = 40.7942,
            lng = 43.8453
        ),
        LearningCenter(
            name = "Ավտոդպրոց «Վանաձոր»",
            city = "Վանաձոր",
            address = "Մ. Մկրտչյան փող., Վանաձոր",
            phone = "+374 322 44040",
            description = "Լոռու մարզի ավտոդպրոց՝ տեսական դասեր և քննական մեքենայով պարապումներ։",
            lat = 40.8093,
            lng = 44.4892
        ),
    )
}
