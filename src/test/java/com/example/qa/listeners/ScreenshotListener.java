package com.example.qa.listeners;

import com.example.qa.core.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures a PNG whenever a test fails.
 *
 * This is the difference between a CI failure you can diagnose and one you have
 * to reproduce locally. Wire it up in testng.xml and forget about it.
 */
public class ScreenshotListener implements ITestListener {

    private static final Path OUTPUT_DIR = Paths.get("screenshots");
    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver;
        try {
            driver = DriverFactory.getDriver();
        } catch (IllegalStateException e) {
            // Failure happened before the browser started; nothing to capture.
            return;
        }

        if (!(driver instanceof TakesScreenshot shooter)) {
            return;
        }

        String name = result.getTestClass().getRealClass().getSimpleName()
                + "_" + result.getMethod().getMethodName()
                + "_" + LocalDateTime.now().format(STAMP) + ".png";

        try {
            Files.createDirectories(OUTPUT_DIR);
            byte[] png = shooter.getScreenshotAs(OutputType.BYTES);
            Path target = OUTPUT_DIR.resolve(name);
            Files.write(target, png);
            System.out.println("[screenshot] " + target.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[screenshot] failed to save: " + e.getMessage());
        }
    }
}
