package com.example.qa.core;

import com.example.qa.config.Config;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Every test class extends this. It owns the browser lifecycle so individual
 * tests never have to think about setup or teardown.
 *
 * One browser per test method (rather than per class) costs a few seconds but
 * buys real isolation: a test that leaves a modal open or a cookie set cannot
 * poison the next one. That trade is almost always worth it.
 */
public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    /**
     * Navigates to a path relative to the configured base URL.
     * Keeps environment switching to a single config value.
     */
    protected void open(String path) {
        driver().get(Config.baseUrl() + path);
    }
}
