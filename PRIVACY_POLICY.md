# Գաղտնիության քաղաքականություն · Վարորդական կոդեքս

**Փաթեթ (package):** `com.drive.license.test`
**Վերջին թարմացումը՝** 7 հուլիսի, 2026

> **Ամփոփում.** «Վարորդական կոդեքս» հավելվածն աշխատում է հիմնականում ձեր սարքում՝ ինտերնետից անկախ։ Հաշիվ ստեղծելու կարիք չկա։ Ձեր թեստերի արդյունքները, առաջընթացը և կարգավորումները պահվում են միայն ձեր սարքում։ Ստորև մանրամասն նկարագրված է, թե ինչ սահմանափակ տվյալներ են օգտագործվում որոշ լրացուցիչ գործառույթների համար։

## 1. Ինչ տվյալներ ենք հավաքում

### 1.1 Տվյալներ, որոնք մնում են միայն ձեր սարքում

Հետևյալ տվյալները երբեք չեն ուղարկվում որևէ սերվեր՝ դրանք պահվում են բացառապես ձեր հեռախոսի/պլանշետի տեղական բազայում.

- Թեստերի և պարապունքի արդյունքները, առաջընթացը և վիճակագրությունը
- Նշված (bookmark) հարցերը
- Հավելվածի կարգավորումները (լեզու, թեմա՝ լուսավոր/մուգ, հիշեցման ժամ)

### 1.2 Firebase Analytics և Crashlytics (Google)

Հավելվածն օգտագործում է **Firebase Analytics**՝ հավելվածի հիմնական օգտագործման վիճակագրության համար (օր.՝ բացումների քանակ, սեսիայի տևողություն, սարքի/օպերացիոն համակարգի ընդհանուր տվյալներ), և **Firebase Crashlytics**՝ վթարների զեկույցների համար (խափանման ժամանակ սարքի վիճակի և սխալի տեղեկատվություն)։ Այս տվյալները անանուն են և չեն կապվում որևէ անհատի ինքնության հետ։

### 1.3 AI բացատրություններ (Anthropic)

Երբ դուք սեղմում եք «Բացատրել պատասխանը» կոճակը, հարցի տեքստը, ձեր պատասխանը և ճիշտ պատասխանը ուղարկվում են **Anthropic**-ի (Claude AI) սերվեր՝ բացատրություն ստանալու համար։ Այս գործառույթը կամընտիր է և գործարկվում է միայն ձեր հստակ գործողությամբ։ Ձեր անունը, էլ. հասցեն կամ այլ նույնականացնող տվյալներ չեն ուղարկվում Anthropic-ին։

### 1.4 Ծանուցումներ

Դուք կարող եք միացնել օրական հիշեցում՝ պարապելու համար։ Ծանուցումները մշակվում են ամբողջությամբ ձեր սարքում (local notifications)՝ առանց որևէ տվյալ արտաքին սերվեր ուղարկելու։

### 1.5 Ուսումնական կենտրոնների ցանկ

Հավելվածում առկա է վարորդական դպրոցների ստատիկ ցանկ։ Այս գործառույթը չի հավաքում և չի օգտագործում ձեր տեղադրության (GPS) տվյալները։

## 2. Ինչու ենք հավաքում այս տվյալները

| Նպատակ | Տվյալներ |
|---|---|
| Հավելվածի հիմնական գործառույթներ | Տեղական առաջընթաց, կարգավորումներ |
| Կայունության բարելավում և վրիպակների շտկում | Crashlytics-ի վթարների զեկույցներ |
| Օգտագործման վիճակագրություն | Firebase Analytics-ի անանուն տվյալներ |
| Կամընտիր AI բացատրություն | Հարցի/պատասխանի տեքստ (միայն սեղմելիս) |

## 3. Տվյալների փոխանցում երրորդ կողմերի

Մենք չենք վաճառում և չենք տրամադրում ձեր տվյալները գովազդային ընկերություններին։ Տվյալները մշակվում են միայն հետևյալ ծառայություններով, մեր անունից՝

- **Google Firebase** (Analytics, Crashlytics) — [Firebase-ի գաղտնիության քաղաքականությունը](https://firebase.google.com/support/privacy)
- **Anthropic** (Claude AI) — [Anthropic-ի գաղտնիության քաղաքականությունը](https://www.anthropic.com/legal/privacy), միայն «Բացատրել պատասխանը» գործառույթի համար

## 4. Տվյալների պահպանում և ջնջում

Տեղական տվյալները (առաջընթաց, կարգավորումներ) ջնջվում են հավելվածն ապատեղադրելիս կամ Android/iOS կարգավորումներից հավելվածի տվյալները մաքրելիս։ Analytics և Crashlytics տվյալները անանուն են և ինքնաբերաբար ջնջվում են Google-ի կողմից սահմանված ժամկետներում։ AI բացատրության հարցումները չեն պահվում մեր կողմից. դրանք ուղարկվում են ուղղակիորեն Anthropic-ին։

## 5. Երեխաների գաղտնիություն

Հավելվածը նախատեսված է վարորդական իրավունքի քննությանը նախապատրաստվող անձանց համար և միտումնավոր կերպով չի հավաքում 13 տարեկանից ցածր երեխաների անձնական տվյալներ։

## 6. Ձեր իրավունքները

Քանի որ հիմնական տվյալները պահվում են ձեր սարքում, դրանց վերահսկումն ամբողջությամբ ձեր ձեռքում է (կարող եք ցանկացած պահի ջնջել՝ ապատեղադրելով հավելվածը)։ Հարցերի կամ Analytics/Crashlytics տվյալների վերաբերյալ հարցումների համար կարող եք կապ հաստատել ստորև նշված էլ. հասցեով։

## 7. Փոփոխություններ այս քաղաքականության մեջ

Այս էջը կարող է թարմացվել հավելվածում փոփոխություններ կատարելիս։ Կարևոր փոփոխությունների դեպքում «Վերջին թարմացումը» ամսաթիվը վերևում կթարմացվի։

## 8. Կապ մեզ հետ

Հարցերի դեպքում գրեք՝ **PRIVACY_CONTACT_EMAIL**

---

## English version

**Last updated:** July 7, 2026

> **Summary.** The "Driving Code" (Վարորդական կոդեքս) app works mostly on your device, offline. No account is required. Your test results, progress, and settings are stored only on your device. Below is a detailed description of the limited data used for a few optional features.

### 1. What data we collect

**1.1 Data that stays on your device** — the following never leaves your device, stored only in the app's local database:
- Test/practice results, progress and statistics
- Bookmarked questions
- App settings (language, light/dark theme, daily reminder time)

**1.2 Firebase Analytics & Crashlytics (Google)** — the app uses Firebase Analytics for basic usage statistics (app opens, session length, general device/OS info) and Firebase Crashlytics for crash reports (device state and error details at the time of a crash). This data is anonymous and is not linked to your identity.

**1.3 AI explanations (Anthropic)** — when you tap "Explain answer", the question text, your answer, and the correct answer are sent to Anthropic's (Claude AI) servers to generate an explanation. This feature is optional and only triggered by your explicit action. Your name, email, or other identifying information is never sent to Anthropic.

**1.4 Notifications** — you may enable a daily practice reminder. Notifications are scheduled entirely on-device (local notifications); no data is sent to any server for this feature.

**1.5 Driving school directory** — the app includes a static list of driving schools. This feature does not collect or use your GPS/location data.

### 2. Why we collect this data

| Purpose | Data |
|---|---|
| Core app functionality | Local progress, settings |
| Stability & bug fixing | Crashlytics crash reports |
| Usage analytics | Firebase Analytics anonymous data |
| Optional AI explanation | Question/answer text (only when requested) |

### 3. Third-party data processors

We do not sell your data or share it with advertisers. Data is processed only by the following service providers, on our behalf:
- **Google Firebase** (Analytics, Crashlytics) — [Firebase Privacy & Security](https://firebase.google.com/support/privacy)
- **Anthropic** (Claude AI) — [Anthropic Privacy Policy](https://www.anthropic.com/legal/privacy), only for the "Explain answer" feature

### 4. Data retention & deletion

Local data (progress, settings) is deleted when you uninstall the app, or when you clear the app's data from Android/iOS system settings. Analytics and Crashlytics data is anonymous and automatically deleted per Google's standard retention periods. AI explanation requests are not stored by us — they are sent directly to Anthropic.

### 5. Children's privacy

The app is designed for people preparing for a driving license exam and does not knowingly collect personal data from children under 13.

### 6. Your rights

Because your core data lives on your device, you are always in control of it (you can delete it at any time by uninstalling the app). For questions about Analytics/Crashlytics data, contact us at the email below.

### 7. Changes to this policy

This page may be updated as the app changes. The "Last updated" date at the top will reflect any meaningful changes.

### 8. Contact us

For any questions, email: **PRIVACY_CONTACT_EMAIL**
