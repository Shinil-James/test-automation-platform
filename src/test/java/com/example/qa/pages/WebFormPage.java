package com.example.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for Selenium's official practice form:
 * https://www.selenium.dev/selenium/web/web-form.html
 *
 * The pattern: locators are private, actions are public, and every action that
 * keeps you on the same page returns `this` so tests can chain. A test should
 * read like a description of user behaviour, never like a list of CSS selectors.
 *
 * When the markup changes, you fix it here once instead of in twenty tests.
 */
public class WebFormPage extends BasePage {

    public static final String PATH = "/web-form.html";

    private static final By TEXT_INPUT    = By.name("my-text");
    private static final By PASSWORD      = By.name("my-password");
    private static final By TEXTAREA      = By.name("my-textarea");
    private static final By DISABLED      = By.name("my-disabled");
    private static final By DROPDOWN      = By.name("my-select");
    private static final By DATALIST      = By.name("my-datalist");
    private static final By CHECKBOX_ONE  = By.id("my-check-1");
    private static final By RADIO_ONE     = By.id("my-radio-1");
    private static final By COLOR_PICKER  = By.name("my-colors");
    private static final By DATE_PICKER   = By.name("my-date");
    private static final By RANGE_SLIDER  = By.name("my-range");
    private static final By SUBMIT        = By.cssSelector("button");
    private static final By MESSAGE       = By.id("message");

    public WebFormPage(WebDriver driver) {
        super(driver);
    }

    public WebFormPage enterText(String value) {
        type(TEXT_INPUT, value);
        return this;
    }

    public WebFormPage enterPassword(String value) {
        type(PASSWORD, value);
        return this;
    }

    public WebFormPage enterTextarea(String value) {
        type(TEXTAREA, value);
        return this;
    }

    public WebFormPage selectDropdownByVisibleText(String label) {
        new Select(visible(DROPDOWN)).selectByVisibleText(label);
        return this;
    }

    public WebFormPage checkCheckbox() {
        if (!visible(CHECKBOX_ONE).isSelected()) {
            click(CHECKBOX_ONE);
        }
        return this;
    }

    public WebFormPage selectRadio() {
        if (!visible(RADIO_ONE).isSelected()) {
            click(RADIO_ONE);
        }
        return this;
    }

    public WebFormPage pickColor(String hex) {
        // Colour inputs ignore sendKeys in most drivers, so set the value
        // directly and fire the change event the app would expect.
        visible(COLOR_PICKER);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + "arguments[0].dispatchEvent(new Event('change'));",
                driver.findElement(COLOR_PICKER), hex);
        return this;
    }

    public WebFormPage setDate(String value) {
        type(DATE_PICKER, value);
        return this;
    }

    public WebFormPage submit() {
        click(SUBMIT);
        return this;
    }

    public String confirmationMessage() {
        return textOf(MESSAGE);
    }

    public String selectedDropdownValue() {
        return new Select(visible(DROPDOWN)).getFirstSelectedOption()
                .getAttribute("value");
    }

    public String rangeValue() {
        return visible(RANGE_SLIDER).getAttribute("value");
    }

    public boolean isDisabledFieldEnabled() {
        return visible(DISABLED).isEnabled();
    }

    public String datalistValue() {
        return visible(DATALIST).getAttribute("value");
    }
}
