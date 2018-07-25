package framework;

import io.appium.java_client.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;

public class AppiumDriverFacade {

    public static AppiumDriver<MobileElement> appiumDriverFacade;

    //region Driver Definition

    /**
     * This method is used to initialize the driver.
     */
    public static void createDriver(String deviceType, String deviceModel, String deviceOSVersion, String serverIp){
        final String URL_STRING = serverIp;
        URL url = null;
        try {
            url = new URL(URL_STRING);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        deviceType = deviceType.toUpperCase();
        switch (deviceType){
            case "ANDROID":
                androidDriverInitialize(url, deviceModel, deviceOSVersion);
                break;
            case "IOS":
                iosDriverInitialize(url, deviceModel, deviceOSVersion);
                break;
            case "SAUCELABS":
                saucelabsInitialize(url, deviceModel, deviceOSVersion);
                break;
            default:
                throw new IllegalArgumentException(String.format("The selected driver %s is not supported", deviceType));
        }
    }

    /**
     * This method is used to initialize the IOS Mobile driver.
     */
    public static void iosDriverInitialize(URL url, String deviceModel, String deviceOSVersion){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", deviceModel);
        capabilities.setCapability("newCommandTimeout", 120);
        capabilities.setCapability("launchTimeout", "100000");
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("platformVersion", deviceOSVersion);
        capabilities.setCapability("autoAcceptAlerts", true);
        capabilities.setCapability("automationName", "XCUITest");
        capabilities.setCapability("bundleId", "com.thenetfirm.mobile.wapicon.WapIcon.adam");
        File srcApp= new File ("app" + separator +"ADAM_FULL.app");
        capabilities.setCapability("app", srcApp.getAbsolutePath());
        appiumDriverFacade = new IOSDriver<>(url, capabilities);
    }

    /**
     * This method is used to initialize the IOS Mobile driver.
     */
    public static void androidDriverInitialize(URL url, String deviceModel, String deviceOSVersion){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        File  srcApp= new File ("app" + separator +"APPCBK-pre-release.apk");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", deviceModel);
        capabilities.setCapability("platformVersion", deviceOSVersion);
        capabilities.setCapability("newCommandTimeout", 120);
        capabilities.setCapability("launchTimeout", 300000);
        capabilities.setCapability("autoGrantPermissions", true);
        capabilities.setCapability("autoAcceptAlerts", true);
        capabilities.setCapability("automationName", "uiautomator2");
        capabilities.setCapability("app", srcApp.getAbsolutePath());
        capabilities.setCapability("appWaitActivity",  "*");
        capabilities.setCapability("autoDismissAlerts", true);
        //capabilities.setCapability("noReset", true);
        appiumDriverFacade = new AndroidDriver<>(url, capabilities);
    }

    /**
     * This method is used to initialize the IOS Mobile driver.
     */
    public static void saucelabsInitialize(URL url, String deviceModel, String deviceOSVersion){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("testobject_api_key", "DF0B0E0B3C5243D1B69687A34CDD3074");
        capabilities.setCapability("platformVersion", deviceOSVersion); // Optional
        capabilities.setCapability("deviceName", deviceModel); // Optional
        capabilities.setCapability("automationName", "uiautomator2");
        capabilities.setCapability("appWaitActivity",  "*");
        capabilities.setCapability("autoDismissAlerts", true);
        capabilities.setCapability("autoGrantPermissions", true);
        capabilities.setCapability("autoAcceptAlerts", true);
        appiumDriverFacade = new AndroidDriver<>(url, capabilities);
    }

    //endregion

    //region Driver Get Methods

    public static MobileElement getElementByText(List<MobileElement> elements, String elementText){
        for (MobileElement element: elements) {
            if(element.getText().contains(elementText)){ return element; }
            }
            throw new IllegalArgumentException(
                    String.format("There couldn't be found any element with the following text: %s", elementText));
    }

    /**
     * This method is used to return the text of the element base on his locator.
     */
    public static String getText(MobileElement element){
        return element.getText();
    }

    /**
     * This method is used to return the text of the element base on his locator and index.
     */
    public static String getText(List<MobileElement> elements, int index){
        return elements.get(index).getText();
    }

    /**
     * This method is used to return the texts of the elements base on his locator.
     */
    public static List<String> getElementsText(List<MobileElement> elements)
    {
        List<String> Texts = new ArrayList<>();
        for (MobileElement element: elements) {
            Texts.add(element.getText());
        }
        return Texts;
    }

    /**
     * This method is used to returns the axis value of an element on the page.
     */
    public static int getElementByAxis(MobileElement element, String axis){
        switch (axis.toUpperCase()){
            case "Y":
                return element.getLocation().y;
            case "X":
                return element.getLocation().x;
            default:
                throw new IllegalArgumentException(String.format("The axis value %s is not supported", axis));
        }
    }

    public static Dimension getElementSize(MobileElement element){
        return element.getSize();
    }

    public static Point getElementCenterPoint(MobileElement element){
        return element.getCenter();
    }

    //endregion

    //region Driver Touch Methods

    /**
     * This method is used to tap on a mobile element base on the locator
     */
    public static void tap(MobileElement element){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.tap(element).perform();
    }

    /**
     * This method is used to tap on a mobile element base on the locator and the index
     */
    public static void tap(List<MobileElement> elements, int index){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.tap(elements.get(index)).perform();
    }

    /**
     * This method is used to tap on a mobile element base on the locator and the element text
     */
    public static void tap(List<MobileElement> elements, String elementText){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.tap(getElementByText(elements, elementText)).perform();
    }

    /**
     * This method is used to tap on a mobile element point base on the locator
     */
    public static void tapByPoint(MobileElement element, int xAxis, int yAxis){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.tap(element.getLocation().x + xAxis, element.getLocation().y + yAxis).perform();
    }

    /**
     * This method is used to tap on a mobile element point base on the locator and the index
     */
    public static void tapByPoint(MobileElement element, int index, int xAxis, int yAxis){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.tap(element.getLocation().x + xAxis, element.getLocation().y + yAxis).perform();
    }

    /**
     * This method is used to tap on a mobile element point base on the locator and the element text
     */
    public static void tapByPoint(List<MobileElement> elements, String elementText, int xAxis, int yAxis){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        MobileElement element = getElementByText(elements, elementText);
        touchAction.tap(element.getLocation().x + xAxis, element.getLocation().y + yAxis).perform();
    }

    /**
     * This method is used to long press on a mobile element point base on the locator
     */
    public static void longPress(MobileElement element){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.longPress(element).perform();
    }

    /**
     * This method is used to long press on a mobile element point base on the locator and the index
     */
    public static void longPress(List<MobileElement> elements, int index){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.longPress(elements.get(index)).perform();
    }

    /**
     * This method is used to long press on a mobile element point base on the locator and the element text
     */
    public static void longPress(List<MobileElement> elements, String elementText){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.longPress(getElementByText(elements, elementText)).perform();
    }

    /**
     * This method is used to drag and drop a mobile element
     */
    public static void mobileDragAndDrop(MobileElement dragElementlements, MobileElement dropElements){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.longPress(dragElementlements).moveTo(dropElements).release().perform();
    }

    /**
     * This method is used to swipe a mobile element
     */
    public static void swipe(MobileElement firstElement, MobileElement swipeToElement){
        TouchAction touchAction = new TouchAction(appiumDriverFacade);
        touchAction.press(firstElement)
                .waitAction(Duration.ofSeconds(2)).moveTo(swipeToElement).release().perform();
    }

    public static void scroll( Integer xStart, Integer yStart, Integer xEnd, Integer yEnd, long duration){
        if(yEnd<0){
            while(yEnd < 0){
                scroll(xStart,yStart,xEnd,0,duration);
                yEnd = yEnd + 900;
            }
        }
        new TouchAction(appiumDriverFacade).press(xStart,yStart)
                .waitAction(Duration.ofMillis(duration))
                .moveTo(xEnd,yEnd)
                .release().
                perform();
    }

    public static void vScroll(Integer xStart, Integer yStart,  Integer yEnd){
        scroll(xStart, yStart, xStart, yEnd, 1000);
    }

    public static void hScroll(Integer xStart, Integer yStart, Integer xEnd){
        scroll(xStart, yStart, xEnd, yStart, 1000);

    }

    public static void scrollToElementByText(String text)
    {
        appiumDriverFacade.findElement(MobileBy.AndroidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView" +
                        "(new UiSelector().textContains(\"" + text + "\"))"));
    }

    //endregion

    //region Driver Actions

    /**
     * This method is used to write on an element from locator.
     */
    public static void write(MobileElement element, String text, int secondsToWait){
        element.sendKeys(text);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to write on an element from locator and element index.
     */
    public static void write(List<MobileElement> elements, int index, String text, int secondsToWait){
        elements.get(index).sendKeys(text);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to clean the text on an element from locator.
     */
    public static void clean(MobileElement element){
        element.clear();
    }

    /**
     * This method is used to clean the text on an element from locator and element index.
     */
    public static void clean(List<MobileElement> elements, int index){
        elements.get(index).clear();
    }

    /**
     * This method is used to clean the text on an element from locator and write on it.
     */
    public static void cleanAndWrite(MobileElement element, String text, int secondsToWait){
        clean(element);
        write(element, text, secondsToWait);
    }

    /**
     * This method is used to clean the text on an element from locator and element index and write on it.
     */
    public static void cleanAndWrite(List<MobileElement> elements, int index, String text, int secondsToWait){
        clean(elements, index);
        write(elements, index, text, secondsToWait);
    }

    /**
     * This method is used to click on the element at the given locator
     */
    public static void click(MobileElement element, int secondsToWait){
        element.click();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to click on the element at the given locator and element index.
     */
    public static void click(List<MobileElement> elements, int index, int secondsToWait){
        elements.get(index).click();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to click on the element at the given locator and element text.
     */
    public static void click(List<MobileElement> elements, String elementText, int secondsToWait){
        MobileElement element = getElementByText(elements, elementText);
        element.click();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to take a screenshot.
     */
    public static void takeScreenshot(String screenshotName, String saveDirectory){
        File screenshot = (appiumDriverFacade).getScreenshotAs(OutputType.FILE);
        File picture = new File(saveDirectory + screenshotName + ".png");
        if(picture.exists()){
            picture.delete();
        }
        screenshot.renameTo(picture);
    }

    //endregion

    //region Driver Boolean Methods

    /**
     * This method is used to check if an element is enable.
     */
    public static boolean isElementEnabled(MobileElement element) {
        try
        {
            return element.isEnabled();
        }
        catch (NoSuchElementException | IllegalArgumentException | StaleElementReferenceException | IllegalStateException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element with certain index is enable.
     */
    public static boolean isElementEnabled(List<MobileElement> elements, int index){
        try
        {
            return elements.get(index).isEnabled();
        }
        catch (NoSuchElementException | IllegalArgumentException | StaleElementReferenceException | IllegalStateException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element with certain text is enable.
     */
    public static boolean isElementEnabled(List<MobileElement> elements, String elementText){
        try
        {
        return getElementByText(elements, elementText).isEnabled();
        }
        catch (NoSuchElementException | IllegalArgumentException | StaleElementReferenceException | IllegalStateException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element is visible.
     */
    public static boolean isElementVisible(MobileElement element)
    {
        try
        {
            return element.isDisplayed();
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException | StaleElementReferenceException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element with certain index is visible.
     */
    public static boolean isElementVisible(List<MobileElement> elements, int index)
    {
        try
        {
            return elements.get(index).isDisplayed();
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException | StaleElementReferenceException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element with certain text is visible.
     */
    public static boolean isElementVisible(List<MobileElement> elements, String text)
    {
        try
        {
            return getElementByText(elements, text).isDisplayed();
        }
        catch (NoSuchElementException | IllegalArgumentException | StaleElementReferenceException | IllegalStateException e)
        {
            return false;
        }
    }

    //endregion

    //region Driver Android Actions

    /**
     * This method is used to write on an element from locator.
     */
    public static void clickAndroidBackButton(int secondsToWait){
        ((AndroidDriver) appiumDriverFacade).pressKeyCode(AndroidKeyCode.BACK);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to write on an element from locator.
     */
    public static void clickAndroidHomeButton(int secondsToWait){
        ((AndroidDriver) appiumDriverFacade).pressKeyCode(AndroidKeyCode.HOME);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    //endregion
}
