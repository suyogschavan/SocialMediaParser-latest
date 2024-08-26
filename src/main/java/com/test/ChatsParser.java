package com.test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class ChatsParser {
    AppiumDriver driver;
    WebDriverWait wait;

    public ChatsParser(AppiumDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    private String backBtnId = "com.instagram.android:id/action_bar_button_back";

    public void storeScreenshots() {
        System.out.println("Inside storeScreenshots");

        // Navigate to the messages screen
        WebElement messenger = driver.findElement(By.id("com.instagram.android:id/action_bar_inbox_button"));
        messenger.click();

        List<WebElement> users = driver.findElements(By.id("com.instagram.android:id/row_inbox_container"));
        for (WebElement user : users) {
            user.click();
            WebElement detailsContainer = driver
                    .findElement(By.xpath("//android.widget.LinearLayout[@resource-id=\"com.instagram.android:id/action_bar\"]"))
                    .findElement(By.id("com.instagram.android:id/action_bar_textview_custom_title_container"))
                    .findElement(By.xpath("//android.view.ViewGroup[@resource-id=\"com.instagram.android:id/direct_thread_action_bar_left_aligned_container\"]"))
                    .findElement(By.id("com.instagram.android:id/thread_title_container"));
            WebElement title = detailsContainer.findElement(By.id("com.instagram.android:id/thread_title"));

                    // Scroll through the chat until the end and take screenshots
            while (true) {
                // Take a screenshot of the current view
                File screenshot = driver.getScreenshotAs(OutputType.FILE);
                saveScreenshot(screenshot, title.getText());

                scrollUp();

                if (isthisEnd()) {
                    break;
                }
            }

            // Go back to the list of users
            driver.findElement(By.id(backBtnId)).click();
        }
    }

    private static void saveScreenshot(File screenshot, String username) {
        try {
            String sourceFolderPath = System.getProperty("user.dir") + "screenshots/"+username+"/"; // Update this path as needed

            // Save the screenshot in the specified folder
            String filePath = sourceFolderPath + "/messages" + System.currentTimeMillis() + ".png";
            FileUtils.copyFile(screenshot, new File(filePath));
            System.out.println("Screenshot saved: " + filePath);
        } catch (IOException e) {
            System.out.println("Saving error: " + e.getMessage());
        }
    }


    public boolean scrollUp() {
        int startX = driver.manage().window().getSize().width / 2;
        int startY = (int) (driver.manage().window().getSize().height * 0.2); // Start near the top
        int endY = (int) (driver.manage().window().getSize().height * 0.8); // End near the bottom

        // Perform the swipe action
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(swipe));

        // Check if new items are loaded after scrolling
        List<WebElement> newListItems = driver.findElements(By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.instagram.android:id/message_list\"]"));
        return !newListItems.isEmpty(); // Continue scrolling if more items are loaded
    }



    private boolean isthisEnd() {
        boolean ans = false;
        try {
            WebElement ele = driver.findElement(By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.instagram.android:id/message_list\"]/android.widget.LinearLayout"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
