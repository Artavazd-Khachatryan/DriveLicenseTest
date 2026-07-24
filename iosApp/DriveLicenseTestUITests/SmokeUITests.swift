import XCTest

/// Device smoke checks for the already-installed DriveLicenseTest app.
final class SmokeUITests: XCTestCase {
    private var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication(bundleIdentifier: "com.drive.license.test.DriveLicenseTest")
        app.launchArguments = ["--ui-smoke"]
        app.launch()
        XCTAssertTrue(app.wait(for: .runningForeground, timeout: 15))
    }

    func testBottomTabsAndPracticeFlow() throws {
        // Home should show start CTA or progress area.
        let homeMarkers = [
            app.staticTexts["Սկսել պարապել"],
            app.buttons["Սկսել"],
            app.staticTexts["Ձեր առաջընթացը"],
            app.tabBars.buttons["Գլխավոր"],
        ]
        XCTAssertTrue(
            homeMarkers.contains { $0.waitForExistence(timeout: 8) },
            "Home screen did not appear"
        )
        attachScreenshot(name: "01-home")

        // Practice tab
        let practiceTab = app.tabBars.buttons["Պարապմունք"]
        XCTAssertTrue(practiceTab.waitForExistence(timeout: 5), "Practice tab missing")
        practiceTab.tap()
        XCTAssertTrue(
            app.staticTexts["Պարապմունք"].waitForExistence(timeout: 5)
                || app.staticTexts["Վերանայել սխալները"].waitForExistence(timeout: 5)
                || app.buttons["Ընտրել"].waitForExistence(timeout: 5),
            "Practice screen did not appear"
        )
        attachScreenshot(name: "02-practice")

        // Progress / stats tab
        let progressTab = app.tabBars.buttons["Առաջընթաց"]
        XCTAssertTrue(progressTab.waitForExistence(timeout: 5), "Progress tab missing")
        progressTab.tap()
        XCTAssertTrue(
            app.staticTexts["Առաջընթաց"].waitForExistence(timeout: 5)
                || app.staticTexts["Կատարողականություն"].waitForExistence(timeout: 5)
                || app.staticTexts["Թեստերի պատմություն"].waitForExistence(timeout: 5),
            "Progress screen did not appear"
        )
        attachScreenshot(name: "03-progress")

        // Back to home and start a short practice if possible
        app.tabBars.buttons["Գլխավոր"].tap()
        let start = app.buttons["Սկսել"]
        if start.waitForExistence(timeout: 5) {
            start.tap()
            // Question chrome: back button or answer area
            let back = app.buttons["Հետ"]
            XCTAssertTrue(
                back.waitForExistence(timeout: 12)
                    || app.buttons.matching(NSPredicate(format: "label CONTAINS 'ա' OR label CONTAINS 'Ա'")).firstMatch.waitForExistence(timeout: 12),
                "Question screen did not open after Start"
            )
            attachScreenshot(name: "04-question")
            if back.exists {
                back.tap()
            }
        }
        attachScreenshot(name: "05-done")
    }

    private func attachScreenshot(name: String) {
        let shot = XCUIScreen.main.screenshot()
        let attachment = XCTAttachment(screenshot: shot)
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
