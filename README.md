# Selenium QA Automation Framework

UI test automation built with **Selenium 4**, **TestNG**, **Maven**, and the
**Page Object Model**. Runs locally on Windows, macOS, or Linux, and headless in CI.

---

## Requirements

| Tool | Minimum | Download |
|------|---------|----------|
| JDK | 17 | https://adoptium.net/temurin/releases/ |
| Maven | 3.6.3 | https://maven.apache.org/download.cgi |
| Git | any recent | https://git-scm.com/downloads |
| Chrome | current | https://www.google.com/chrome/ |

You do **not** need to download chromedriver. Selenium Manager resolves and
caches the correct driver binary on first run.

Verify your setup:

```bash
java -version
mvn -v
git --version
```

---

## Quick start

```bash
git clone <your-repo-url>
cd selenium-qa-automation
mvn test
```

First run downloads dependencies and the browser driver, so it takes a minute.
Subsequent runs are fast.

---

## Running tests

```bash
mvn test                                         # everything, visible browser
mvn test -Dheadless=true                         # everything, no window
mvn test -Dbrowser=firefox                       # Firefox instead of Chrome
mvn test -Dbrowser=edge -Dheadless=true          # Edge, headless
mvn test -Dtest=WebFormTest                      # a single class
mvn test -Dtest=WebFormTest#pageTitleIsCorrect   # a single method
mvn clean test                                   # wipe build output first
```

### Configuration

Settings resolve in this order, highest first:

1. Command-line system property — `-Dbrowser=firefox`
2. `src/test/resources/config.properties`
3. Hard-coded defaults in `Config.java`

| Key | Default | Meaning |
|-----|---------|---------|
| `baseUrl` | `https://www.selenium.dev/selenium/web` | Root URL of the app under test |
| `browser` | `chrome` | `chrome`, `firefox`, or `edge` |
| `headless` | `false` | Run without a visible window |
| `explicitWaitSeconds` | `10` | Timeout for element waits |
| `pageLoadTimeoutSeconds` | `30` | Timeout for page navigation |

---

## Project layout

```
selenium-qa-automation/
├── pom.xml                      Dependencies and build config
├── testng.xml                   Suite definition — what runs, how parallel
├── CLAUDE.md                    Conventions for AI-assisted work
├── .github/workflows/ci.yml     GitHub Actions pipeline
└── src/test/
    ├── java/com/example/qa/
    │   ├── config/Config.java
    │   ├── core/DriverFactory.java
    │   ├── core/BaseTest.java
    │   ├── pages/BasePage.java
    │   ├── pages/WebFormPage.java
    │   ├── tests/WebFormTest.java
    │   └── listeners/ScreenshotListener.java
    └── resources/config.properties
```

### Why the layers exist

**`DriverFactory`** holds each thread's browser in a `ThreadLocal`, which is what
makes parallel execution safe. Three tests can run at once without fighting over
one window.

**`BaseTest`** creates a fresh browser before every test method and quits it
after. Per-method isolation costs a few seconds but prevents one test's leftover
state from poisoning the next.

**`BasePage`** wraps every interaction in an explicit wait. No `Thread.sleep`
appears anywhere in this codebase, and none should be added — fixed sleeps are
the primary cause of flaky suites.

**Page objects** hold locators; test classes hold assertions. When the UI changes,
you edit one page object instead of twenty tests.

---

## Reports and debugging

| Output | Location |
|--------|----------|
| TestNG HTML report | `target/surefire-reports/index.html` |
| Plain-text summary | `target/surefire-reports/*.txt` |
| Failure screenshots | `screenshots/` |

`ScreenshotListener` captures a PNG automatically on every failure. In CI, both
screenshots and reports upload as build artifacts.

---

## Parallel execution

`testng.xml` sets `parallel="methods" thread-count="3"`. Lower `thread-count` to
`1` while debugging so you can watch a single browser. Raise it for faster suite
runs, bearing in mind each thread is a real browser process.

---

## Adding a new test

1. Write a page object in `pages/` extending `BasePage`. Locators `private static final`.
2. Write a test class in `tests/` extending `BaseTest`. No locators here.
3. Register the class in `testng.xml`.

```java
public class LoginPage extends BasePage {
    public static final String PATH = "/login.html";
    private static final By USERNAME = By.id("username");

    public LoginPage(WebDriver driver) { super(driver); }

    public LoginPage enterUsername(String value) {
        type(USERNAME, value);
        return this;
    }
}
```

---

## Continuous integration

`.github/workflows/ci.yml` runs the suite headless on every push and pull request
to `main`. GitHub's `ubuntu-latest` runner ships with Chrome preinstalled, so no
browser setup step is needed.

---

## Troubleshooting

**`mvn` is not recognised** — Maven's `bin` directory is not on your `PATH`.
See https://maven.apache.org/install.html

**`SessionNotCreatedException`** — Chrome and the cached driver disagree. Delete
the Selenium Manager cache (`~/.cache/selenium` on Linux/macOS,
`%USERPROFILE%\.cache\selenium` on Windows) and rerun.

**Tests pass locally, fail in CI** — almost always a timing assumption. Check for
a hard-coded wait or an assertion that depends on animation completing.

**Garbled non-ASCII characters on Windows** — set a `MAVEN_OPTS` environment
variable to `-Dfile.encoding=UTF-8`.

---

## References

- Selenium documentation — https://www.selenium.dev/documentation/
- Selenium Manager — https://www.selenium.dev/documentation/selenium_manager/
- Waits — https://www.selenium.dev/documentation/webdriver/waits/
- Locator strategies — https://www.selenium.dev/documentation/webdriver/elements/locators/
- TestNG documentation — https://testng.org/doc/documentation-main.html
- Maven Surefire — https://maven.apache.org/surefire/maven-surefire-plugin/
