package com.example.drivelicensetest.data

import com.example.drivelicensetest.R
import com.example.drivelicensetest.model.Question

object QuestionGroup7 {
    val questions = listOf(
        Question(
            id = 701,
            questionText = "Ո՞ր դեպքում է արգելվում մեքենայի վարումը:",
            options = listOf(
                "Երբ վարորդը հոգնած է",
                "Երբ վարորդը հարբած է",
                "Երբ վարորդը հիվանդ է"
            ),
            correctAnswerIndex = 1,
            explanation = "Մեքենայի վարումը արգելվում է, երբ վարորդը հարբած է, քանի որ դա կարող է հանգեցնել վթարի:",
            imageResId = R.drawable.drunk_driving,
            book = Book.BOOK_7
        ),
        Question(
            id = 702,
            questionText = "Ո՞րն է անվտանգ արագությունը քաղաքում:",
            options = listOf(
                "40 կմ/ժ",
                "60 կմ/ժ",
                "80 կմ/ժ"
            ),
            correctAnswerIndex = 1,
            explanation = "Քաղաքում անվտանգ արագությունը 60 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.speed_limit,
            book = Book.BOOK_7
        ),
        Question(
            id = 703,
            questionText = "Ի՞նչ է նշանակում կարմիր լույսը:",
            options = listOf(
                "Պատրաստվել",
                "Կանգ առնել",
                "Շարունակել շարժումը"
            ),
            correctAnswerIndex = 1,
            explanation = "Կարմիր լույսը նշանակում է կանգ առնել և սպասել մինչև կանաչ լույսը:",
            imageResId = R.drawable.traffic_light,
            book = Book.BOOK_7
        ),
        Question(
            id = 704,
            questionText = "Ո՞րն է անվտանգ հեռավորությունը մյուս մեքենայից:",
            options = listOf(
                "2 վայրկյան",
                "3 վայրկյան",
                "4 վայրկյան"
            ),
            correctAnswerIndex = 1,
            explanation = "Անվտանգ հեռավորությունը մյուս մեքենայից պետք է լինի առնվազն 3 վայրկյան:",
            imageResId = R.drawable.safe_distance,
            book = Book.BOOK_7
        ),
        Question(
            id = 705,
            questionText = "Ի՞նչ է նշանակում կանաչ լույսը:",
            options = listOf(
                "Պատրաստվել",
                "Կանգ առնել",
                "Շարունակել շարժումը"
            ),
            correctAnswerIndex = 2,
            explanation = "Կանաչ լույսը նշանակում է, որ կարող եք շարունակել շարժումը:",
            imageResId = R.drawable.traffic_light,
            book = Book.BOOK_7
        ),
        Question(
            id = 706,
            questionText = "Ո՞րն է անվտանգ արագությունը մայրուղում:",
            options = listOf(
                "80 կմ/ժ",
                "90 կմ/ժ",
                "110 կմ/ժ"
            ),
            correctAnswerIndex = 2,
            explanation = "Մայրուղում անվտանգ արագությունը 110 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.highway_speed,
            book = Book.BOOK_7
        ),
        Question(
            id = 707,
            questionText = "Ի՞նչ է նշանակում դեղին լույսը:",
            options = listOf(
                "Պատրաստվել",
                "Կանգ առնել",
                "Շարունակել շարժումը"
            ),
            correctAnswerIndex = 0,
            explanation = "Դեղին լույսը նշանակում է պատրաստվել կանգ առնելուն:",
            imageResId = R.drawable.traffic_light,
            book = Book.BOOK_7
        ),
        Question(
            id = 708,
            questionText = "Ո՞րն է անվտանգ արագությունը գյուղական ճանապարհներին:",
            options = listOf(
                "60 կմ/ժ",
                "70 կմ/ժ",
                "90 կմ/ժ"
            ),
            correctAnswerIndex = 2,
            explanation = "Գյուղական ճանապարհներին անվտանգ արագությունը 90 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.rural_road,
            book = Book.BOOK_7
        ),
        Question(
            id = 709,
            questionText = "Ի՞նչ է նշանակում կարմիր եռանկյունը:",
            options = listOf(
                "Նախազգուշացում",
                "Արգելք",
                "Տեղեկատվություն"
            ),
            correctAnswerIndex = 0,
            explanation = "Կարմիր եռանկյունը նշանակում է նախազգուշացում:",
            imageResId = R.drawable.warning_sign,
            book = Book.BOOK_7
        ),
        Question(
            id = 710,
            questionText = "Ո՞րն է անվտանգ արագությունը թունելում:",
            options = listOf(
                "40 կմ/ժ",
                "50 կմ/ժ",
                "60 կմ/ժ"
            ),
            correctAnswerIndex = 1,
            explanation = "Թունելում անվտանգ արագությունը 50 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.tunnel,
            book = Book.BOOK_7
        ),
        Question(
            id = 711,
            questionText = "Ի՞նչ է նշանակում կապույտ շրջանը:",
            options = listOf(
                "Նախազգուշացում",
                "Արգելք",
                "Տեղեկատվություն"
            ),
            correctAnswerIndex = 2,
            explanation = "Կապույտ շրջանը նշանակում է տեղեկատվություն:",
            imageResId = R.drawable.info_sign,
            book = Book.BOOK_7
        ),
        Question(
            id = 712,
            questionText = "Ո՞րն է անվտանգ արագությունը կամրջի վրա:",
            options = listOf(
                "40 կմ/ժ",
                "50 կմ/ժ",
                "60 կմ/ժ"
            ),
            correctAnswerIndex = 1,
            explanation = "Կամրջի վրա անվտանգ արագությունը 50 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.bridge,
            book = Book.BOOK_7
        ),
        Question(
            id = 713,
            questionText = "Ի՞նչ է նշանակում կարմիր շրջանը:",
            options = listOf(
                "Նախազգուշացում",
                "Արգելք",
                "Տեղեկատվություն"
            ),
            correctAnswerIndex = 1,
            explanation = "Կարմիր շրջանը նշանակում է արգելք:",
            imageResId = R.drawable.prohibition_sign,
            book = Book.BOOK_7
        ),
        Question(
            id = 714,
            questionText = "Ո՞րն է անվտանգ արագությունը խաչմերուկում:",
            options = listOf(
                "30 կմ/ժ",
                "40 կմ/ժ",
                "50 կմ/ժ"
            ),
            correctAnswerIndex = 1,
            explanation = "Խաչմերուկում անվտանգ արագությունը 40 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.intersection,
            book = Book.BOOK_7
        ),
        Question(
            id = 715,
            questionText = "Ի՞նչ է նշանակում կանաչ շրջանը:",
            options = listOf(
                "Նախազգուշացում",
                "Արգելք",
                "Տեղեկատվություն"
            ),
            correctAnswerIndex = 2,
            explanation = "Կանաչ շրջանը նշանակում է տեղեկատվություն:",
            imageResId = R.drawable.info_sign,
            book = Book.BOOK_7
        ),
        Question(
            id = 716,
            questionText = "Ո՞րն է անվտանգ արագությունը դպրոցի մոտ:",
            options = listOf(
                "20 կմ/ժ",
                "30 կմ/ժ",
                "40 կմ/ժ"
            ),
            correctAnswerIndex = 1,
            explanation = "Դպրոցի մոտ անվտանգ արագությունը 30 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.school_zone,
            book = Book.BOOK_7
        ),
        Question(
            id = 717,
            questionText = "Ի՞նչ է նշանակում դեղին շրջանը:",
            options = listOf(
                "Նախազգուշացում",
                "Արգելք",
                "Տեղեկատվություն"
            ),
            correctAnswerIndex = 0,
            explanation = "Դեղին շրջանը նշանակում է նախազգուշացում:",
            imageResId = R.drawable.warning_sign,
            book = Book.BOOK_7
        ),
        Question(
            id = 718,
            questionText = "Ո՞րն է անվտանգ արագությունը հիվանդանոցի մոտ:",
            options = listOf(
                "20 կմ/ժ",
                "30 կմ/ժ",
                "40 կմ/ժ"
            ),
            correctAnswerIndex = 1,
            explanation = "Հիվանդանոցի մոտ անվտանգ արագությունը 30 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.hospital_zone,
            book = Book.BOOK_7
        ),
        Question(
            id = 719,
            questionText = "Ի՞նչ է նշանակում սպիտակ շրջանը:",
            options = listOf(
                "Նախազգուշացում",
                "Արգելք",
                "Տեղեկատվություն"
            ),
            correctAnswerIndex = 2,
            explanation = "Սպիտակ շրջանը նշանակում է տեղեկատվություն:",
            imageResId = R.drawable.info_sign,
            book = Book.BOOK_7
        ),
        Question(
            id = 720,
            questionText = "Ո՞րն է անվտանգ արագությունը մանկապարտեզի մոտ:",
            options = listOf(
                "20 կմ/ժ",
                "30 կմ/ժ",
                "40 կմ/ժ"
            ),
            correctAnswerIndex = 1,
            explanation = "Մանկապարտեզի մոտ անվտանգ արագությունը 30 կմ/ժ է, եթե այլ արագություն նշված չէ:",
            imageResId = R.drawable.kindergarten_zone,
            book = Book.BOOK_7
        )
    )
} 