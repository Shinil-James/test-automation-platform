# Project context for Claude

## What this is

A UI test automation suite: Selenium 4 + TestNG + Maven, using the Page Object Model.
Target under test is Selenium's own practice form at
`https://www.selenium.dev/selenium/web/web-form.html`.

## Commands

```bash
mvn test                                  # full suite, visible browser
mvn test -Dheadless=true                  # full suite, headless
mvn test -Dbrowser=firefox                # different browser
mvn test -Dtest=WebFormTest               # one class (bypasses testng.xml)
mvn test -Dtest=WebFormTest#pageTitleIsCorrect   # one method
mvn clean                                 # wipe target/
```

Reports land in `target/surefire-reports/`. Failure screenshots land in `screenshots/`.

## Layout

```
src/test/java/com/example/qa/
├── config/     Config.java          — system property > config.properties > default
├── core/       DriverFactory.java   — ThreadLocal<WebDriver>, browser options
│               BaseTest.java        — @BeforeMethod / @AfterMethod lifecycle
├── pages/      BasePage.java        — waits and shared interactions
│               WebFormPage.java     — locators + actions for one page
├── tests/      WebFormTest.java     — assertions only, no locators
└── listeners/  ScreenshotListener.java
```

## Conventions — follow these

- **Never use `Thread.sleep`.** Use the wait helpers in `BasePage`. This is the
  single biggest source of flaky suites.
- **Locators belong in page objects, never in test classes.** If you find
  yourself writing `By.id(...)` inside a `@Test` method, it goes in the page
  object instead.
- **Locator preference order:** `id` > `name` > `data-*` attribute > CSS selector.
  Avoid XPath that walks the DOM structure (`//div[3]/span`) — it breaks on any
  layout change.
- **Page object actions return `this`** when they leave you on the same page, so
  tests can chain. Return a new page object when navigation occurs.
- **One assertion concept per test method.** Use a `@DataProvider` for breadth
  rather than stuffing five scenarios into one method.
- **Do not add WebDriverManager.** Selenium Manager (built in since 4.6) already
  handles driver binaries.

## When adding a new page

1. Create the page object in `pages/`, extending `BasePage`.
2. Add a `public static final String PATH` constant for its route.
3. Create a matching test class in `tests/`, extending `BaseTest`.
4. Register the test class in `testng.xml`.

## Environment

Developed against JDK 17, Maven 3.8+, Windows 11. CI runs on ubuntu-latest with
JDK 17 headless.
