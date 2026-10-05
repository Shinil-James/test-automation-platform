package com.example.qa.tests;

import com.example.qa.core.BaseTest;
import com.example.qa.pages.WebFormPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Functional tests against the practice form.
 *
 * Notice what is NOT in here: no By locators, no WebDriverWait, no driver
 * plumbing. All of that lives in the page object and the base classes. These
 * methods describe intent, which is what makes them survive UI redesigns.
 */
public class WebFormTest extends BaseTest {

    private WebFormPage page;

    @BeforeMethod(alwaysRun = true)
    public void openForm() {
        open(WebFormPage.PATH);
        page = new WebFormPage(driver());
    }

    @Test(description = "Page loads with the expected title")
    public void pageTitleIsCorrect() {
        assertEquals(page.title(), "Web form", "Unexpected page title");
    }

    @Test(description = "Submitting the form shows the confirmation message")
    public void submittingFormShowsConfirmation() {
        page.enterText("Shinto")
            .enterPassword("hunter2")
            .enterTextarea("Submitted by the automation suite")
            .submit();

        assertEquals(page.confirmationMessage(), "Received!",
                "Form did not confirm submission");
    }

    @Test(description = "Dropdown selection is reflected in the element value")
    public void dropdownSelectionWorks() {
        page.selectDropdownByVisibleText("Two");
        assertEquals(page.selectedDropdownValue(), "2",
                "Dropdown did not hold the selected value");
    }

    @Test(description = "Checkbox and radio button can be toggled on")
    public void checkboxAndRadioCanBeSelected() {
        page.checkCheckbox().selectRadio().submit();
        assertEquals(page.confirmationMessage(), "Received!");
    }

    @Test(description = "A disabled input is reported as disabled")
    public void disabledFieldIsNotEnabled() {
        assertFalse(page.isDisabledFieldEnabled(),
                "The disabled input should not be enabled");
    }

    @Test(description = "Range slider starts at its default midpoint")
    public void rangeSliderHasDefaultValue() {
        assertEquals(page.rangeValue(), "5", "Unexpected default slider value");
    }

    /**
     * Data-driven testing: one method, many inputs. TestNG runs this once per
     * row, and each row reports as its own pass or fail. This is how you get
     * breadth without copy-pasting test methods.
     */
    @DataProvider(name = "textInputs")
    public Object[][] textInputs() {
        return new Object[][] {
                {"simple text"},
                {"1234567890"},
                {"special !@#$%^&*()"},
                {"unicode: äöü 日本語"},
                {"a string that is considerably longer than the others to check wrapping"}
        };
    }

    @Test(dataProvider = "textInputs",
          description = "Form accepts a range of text input shapes")
    public void formAcceptsVariousTextInputs(String input) {
        page.enterText(input).submit();
        assertTrue(page.currentUrl().contains("my-text="),
                "Submitted value did not reach the query string");
        assertEquals(page.confirmationMessage(), "Received!");
    }
}
