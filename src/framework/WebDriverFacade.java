package framework;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class WebDriverFacade {

    public static WebDriver webDriverFacade;

    //region Driver Definition

    /**
     * This method is used to initialize the driver.
     */
    public static void createDriver(String device, String size, String downloadDirectory){
        final String URL_STRING = "http://127.0.0.1:4723/wd/hub";
        try {
            URL url = new URL(URL_STRING);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        switch (device.toUpperCase()){
            case "FIREFOX":
                firefoxDriverInitialize();
                break;
            case "CHROME":
                chromeDriverInitialize(downloadDirectory);
                break;
            case "EDGE":
                edgeDriverInitialize();
                break;
            default:
                throw new IllegalArgumentException(String.format("The selected driver %s is not supported", device));
        }
        switch (size.toUpperCase()){
                case "SMALL":
                    resizeWindows(400, 600);
                    break;
                case "MEDIUM":
                    resizeWindows(768, 1024);
                    break;
                case "LARGE":
                    resizeWindows(1280, 1024);
                    break;
                case "FULL":
                    maximizeWindows();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("The size %s is not supported", size));
        }
    }

    /**
     * This method is used to initialize the Firefox driver.
     */
    public static void firefoxDriverInitialize(){
        System.setProperty("webdriver.gecko.driver", Utils.firefoxSeleniumDriver());
        webDriverFacade = new FirefoxDriver();
    }

    /**
     * This method is used to initialize the Chrome driver.
     */
    public static void chromeDriverInitialize(String downloadDirectory){
        ChromeOptions chromeOptions = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", Utils.chromeSeleniumDriver());
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("credentials_enable_service", false);
        if(downloadDirectory != null) chromePrefs.put("download.default_directory", downloadDirectory);
        chromeOptions.setExperimentalOption("prefs", chromePrefs);
        chromeOptions.addArguments("chrome.switches", "--disable-infobars");
        chromeOptions.addArguments("test-type");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("no-sandbox");
        webDriverFacade = new ChromeDriver(chromeOptions);
    }

    /**
     * This method is used to initialize the Microsoft EDGE driver.
     */
    public static void edgeDriverInitialize(){
        webDriverFacade = new EdgeDriver();
    }

    //endregion

    //region Driver Browser Methods

    /**
     * Opens the page at the given URL.
     */
    public static void open(String url, int secondsToWait){
        webDriverFacade.navigate().to(url);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to maximize the browser windows.
     */
    public static void maximizeWindows(){
        webDriverFacade.manage().window().maximize();
    }

    /**
     * This method is used to resize the browser windows.
     */
    public static void resizeWindows(int width, int height){
        Dimension resolution = new Dimension(width, height);
        webDriverFacade.manage().window().setSize(resolution);
    }

    /**
     * This method is used to refresh the current windows.
     */
    public static void refreshCurrentWindow(int secondsToWait){
        webDriverFacade.navigate().refresh();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to navigate to the previous windows.
     */
    public static void clickNavigateBackButton(int secondsToWait){
        webDriverFacade.navigate().back();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to scroll given x and y axis.
     */
    public static void scrollByAxis(int xAxis, int yAxis, int secondsToWait){
        ((JavascriptExecutor) webDriverFacade).executeScript("window.scrollBy({xAxis}, {yAxis})");
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to scroll to an specific element on the page.
     */
    public static void scrollToElement(By locator, int secondsToWait){
        int xAxis = getElementByAxis(locator, "x");
        int yAxis = getElementByAxis(locator, "y");
        scrollByAxis(xAxis, yAxis, 0);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to take a screenshot.
     */
    public static void takeScreenshot(String screenshotName, String saveDirectory){
        File screenshot = ((TakesScreenshot)webDriverFacade).getScreenshotAs(OutputType.FILE);
        screenshot.renameTo(new File(saveDirectory + screenshotName + ".png"));
    }

    /**
     * This method is used to close the current windows.
     */
    public static void closeCurrentWindow(){
        webDriverFacade.close();
    }

    /**
     * This method is used to close the entire driver.
     */
    public static void shutDown(){
        webDriverFacade.manage().deleteAllCookies();
        webDriverFacade.quit();
    }

    //endregion

    //region Driver Get Methods

    /**
     * This method is used to get the current page URL.
     */
    public static String getPageURL() { return webDriverFacade.getCurrentUrl();}

    /**
     * This method is used to get the current page title.
     */
    public static String getPageTitle() { return webDriverFacade.getTitle();}

    /**
     * This method is used to return the element according to the locator.
     */
    public static WebElement getElement(By locator){
        WebElement element;
        try{
            element = webDriverFacade.findElement(locator);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException(
                    String.format("There couldn't be found any element with the following selector: %s", locator));
        }
        return element;
    }

    /**
     * This method is used to return the element according to the locator and an index.
     */
    public static WebElement getElement(By locator, int index){
        if (index < 0) throw new IllegalArgumentException("Index must be greater than or equals zero");
        List<WebElement> elements = getElements(locator);
        try {
            return elements.get(index);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("There couldn't be found any element with the following selector: %s with index %d",
                    locator, index));
        }
    }

    /**
     * This method is used to return the element according to the locator and the element text.
     */
    public static WebElement getElement(By locator, String elementText){
        List<WebElement> elements = getElements(locator);
        for (WebElement e: elements)
        {
            if(e.getText().contains(elementText)){
                return e;
            }
        }
        throw new IllegalArgumentException(
                String.format("There couldn't be found any element with the following selector: %s with text %s",
                        locator, elementText));
    }

    /**
     * This method is used to return the elements according to the locator.
     */
    public static List<WebElement> getElements(By locator){
        List<WebElement> elements;
        try{
            elements = webDriverFacade.findElements(locator);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException(
                    String.format("There couldn't be found any element with the following selector: %s", locator));
        }
        return elements;
    }

    /**
     * This method is used to return the text of the element base on his locator.
     */
    public static String getText(By locator){
        WebElement element = getElement(locator);
        return element.getText();
    }

    /**
     * This method is used to return the text of the element base on his locator and index.
     */
    public static String getText(By locator, int index){
        WebElement element = getElement(locator, index);
        return element.getText();
    }

    /**
     * This method is used to return the texts of the elements base on his locator.
     */
    public static List<String> getElementsText(By locator)
    {
        List<WebElement> elements = getElements(locator);
        List<String> Texts = new ArrayList<>();
        for (WebElement element: elements) {
            Texts.add(element.getText());
        }
        return Texts;
    }

    /**
     * This method is used to return the attribute's value of the element base on his locator.
     */
    public static String getAttributeValue(By locator, String attribute){
        WebElement element = getElement(locator);
        return element.getAttribute(attribute);
    }

    /**
     * This method is used to return the attribute's value of the element base on his locator and index.
     */
    public static String getAttributeValue(By locator, int index, String attribute){
        WebElement element = getElement(locator, index);
        return element.getAttribute(attribute);
    }

    /**
     * This method is used to return the CSS property value of the element base on his locator.
     */
    public static String getCssPropertyValue(By locator, String cssProperty){
        return getElement(locator).getCssValue(cssProperty);
    }

    /**
     * This method is used to return the CSS property value of the element base on his locator and index.
     */
    public static String getCssPropertyValue(By locator, int index, String cssProperty){
        return getElement(locator, index).getCssValue(cssProperty);
    }

    /**
     * This method is used to return the amount of element form locator.
     */
    public int getElementsCount(By locator){
        return getElements(locator).size();
    }

    /**
     * This method is used to return element index according to his text.
     */
    public static int getElementIndex(By locator, String elementText){
        return getElements(locator).indexOf(elementText);
    }

    /**
     * This method is used to returns the axis value of an element on the page.
     */
    public static int getElementByAxis(By locator, String axis){
        switch (axis.toUpperCase()){
            case "Y":
                return getElement(locator).getLocation().y;
            case "X":
                return getElement(locator).getLocation().x;
            default:
                throw new IllegalArgumentException(String.format("The axis value %s is not supported", axis));
        }
    }

    //endregion

    //region Driver Actions

    /**
     * This method is used to write on an element from locator.
     */
    public static void write(By locator, String text, int secondsToWait){
        getElement(locator).sendKeys(text);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to write on an element from locator and element index.
     */
    public static void write(By locator, int index, String text, int secondsToWait){
        getElement(locator, index).sendKeys(text);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to clean the text on an element from locator.
     */
    public static void clean(By locator){
        getElement(locator).clear();
    }

    /**
     * This method is used to clean the text on an element from locator and element index.
     */
    public static void clean(By locator, int index){
        getElement(locator, index).clear();
    }

    /**
     * This method is used to clean the text on an element from locator and write on it.
     */
    public static void cleanAndWrite(By locator, String text, int secondsToWait){
        clean(locator);
        write(locator, text, secondsToWait);
    }

    /**
     * This method is used to clean the text on an element from locator and element index and write on it.
     */
    public static void cleanAndWrite(By locator, int index, String text, int secondsToWait){
        clean(locator, index);
        write(locator, index, text, secondsToWait);
    }

    /**
     * This method is used to click on the element at the given locator
     */
    public static void click(By locator, int secondsToWait){
        WebElement element = getElement(locator);
        element.click();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to click on the element at the given locator and element index.
     */
    public static void click(By locator, int index, int secondsToWait){
        WebElement element = getElement(locator, index);
        element.click();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to click on the element at the given locator and element text.
     */
    public static void click(By locator, String elementText, int secondsToWait){
        WebElement element = getElement(locator, elementText);
        element.click();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to perform a double click on the element at the given locator.
     */
    public static void doubleClick(By locator, int secondsToWait){
        WebElement element = getElement(locator);
        new Actions(webDriverFacade).doubleClick(element).perform();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to perform a double click on the element at the given locator and element index.
     */
    public static void doubleClick(By locator, int index, int secondsToWait){
        WebElement element = getElement(locator, index);
        new Actions(webDriverFacade).doubleClick(element).perform();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to perform a double click on the element at the given locator and element text.
     */
    public static void doubleClick(By locator, String elementText, int secondsToWait){
        WebElement element = getElement(locator, elementText);
        new Actions(webDriverFacade).doubleClick(element).perform();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to perform a right click on the element at the given locator.
     */
    public static void rightClick(By locator, int secondsToWait){
        WebElement element = getElement(locator);
        new Actions(webDriverFacade).contextClick(element).perform();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to perform a right click on the element at the given locator and index.
     */
    public static void rightClick(By locator, int index, int secondsToWait){
        WebElement element = getElement(locator, index);
        new Actions(webDriverFacade).contextClick(element).perform();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to perform a right click on the element at the given locator and element text.
     */
    public static void rightClick(By locator, String elementText, int secondsToWait){
        WebElement element = getElement(locator, elementText);
        new Actions(webDriverFacade).contextClick(element).perform();
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to move the mouse to an element at the given locator.
     */
    public static void moveMouseToElement(By locator){
        WebElement element = getElement(locator);
        new Actions(webDriverFacade).moveToElement(element).perform();
    }

    /**
     * This method is used to move the mouse to an element at the given locator and index.
     */
    public static void moveMouseToElement(By locator, int index){
        WebElement element = getElement(locator, index);
        new Actions(webDriverFacade).moveToElement(element).perform();
    }

    /**
     * This method is used to move the mouse to an element at the given locator and element text.
     */
    public static void moveMouseToElement(By locator, String elementText){
        WebElement element = getElement(locator, elementText);
        new Actions(webDriverFacade).moveToElement(element).perform();
    }

    /**
     * This method is used to download a certain file from the locator link.
     */
    public static void downloadFile(By downloadLinkLocator, String downloadDirectory){
        click(downloadLinkLocator, 10);
        Utils.waitForFileDownload(downloadDirectory);
    }

    /**
     * This method is used to download a certain file from the locator link and index.
     */
    public static void downloadFile(By downloadLinkLocator, int index, String downloadDirectory){
        click(downloadLinkLocator, index, 10);
        Utils.waitForFileDownload(downloadDirectory);
    }

    /**
     * This method is used to download a certain file from the locator link and text.
     */
    public static void downloadFile(By downloadLinkLocator, String elementText, String downloadDirectory){
        click(downloadLinkLocator, elementText, 10);
        Utils.waitForFileDownload(downloadDirectory);
    }

    //endregion

    //region Driver Select Methods

    /**
     * This method is used to return the selected option of a select element by locator.
     */
    public static String getSelectedOption(By locator){
        Select select = new Select(getElement(locator));
        return select.getFirstSelectedOption().getText();
    }

    /**
     * This method is used to return the options of a select by locator.
     */
    public static String getSelectedOption(By locator, int index){
        Select select = new Select(getElement(locator,index));
        return select.getFirstSelectedOption().getText();
    }

    /**
     * This method is used to return the options of a select by locator and index.
     */
    public static List<String> getSelectedOptions(By locator){
        Select select = new Select(getElement(locator));
        List<String> selectedOptionsText = new ArrayList<>();
        for(WebElement e: select.getAllSelectedOptions()){selectedOptionsText.add(e.getText());}
        return selectedOptionsText;
    }

    /**
     * This method is used to return the selected option of a select element by locator and index.
     */
    public static List<String> getSelectedOptions(By locator, int index){
        Select select = new Select(getElement(locator, index));
        List<String> selectedOptionsText = new ArrayList<>();
        for(WebElement e: select.getAllSelectedOptions()){selectedOptionsText.add(e.getText());}
        return selectedOptionsText;
    }

    /**
     * This method is used to select the option from locator whose visible text matches the given value
     */
    public static void  selectOptionByVisibleText(By locator, String text, int secondsToWait){
        Select select = new Select(getElement(locator));
        select.selectByVisibleText(text);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to select the option from locator and index whose visible text matches the given value
     */
    public static void  selectOptionByVisibleText(By locator, int index, String text, int secondsToWait){
        Select select = new Select(getElement(locator, index));
        select.selectByVisibleText(text);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to select the option from locator whose value matches the given value
     */
    public static void  selectOptionByValue(By locator, String value, int secondsToWait){
        Select select = new Select(getElement(locator));
        select.selectByValue(value);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to select the option from locator and index whose value matches the given value
     */
    public static void  selectOptionByValue(By locator, int index, String value, int secondsToWait){
        Select select = new Select(getElement(locator, index));
        select.selectByValue(value);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to select the option from locator and index whose index matches the given value
     */
    public static void  selectOptionByIndex(By locator, int optionIndex, int secondsToWait){
        Select select = new Select(getElement(locator));
        select.selectByIndex(optionIndex);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    /**
     * This method is used to select the option from locator and index whose index matches the given value
     */
    public static void  selectOptionByIndex(By locator, int index, int optionIndex, int secondsToWait){
        Select select = new Select(getElement(locator, index));
        select.selectByIndex(optionIndex);
        if (secondsToWait > 0) Utils.pauseSeconds(secondsToWait);
    }

    //endregion

    //region Driver Alert Methods

    /**
     * This method is used to check if an alert is visible.
     */
    public static boolean isAlertVisible(){
        try{
            webDriverFacade.switchTo().alert();
            return true;
        }catch (NoAlertPresentException e){
            return false;
        }
    }

    /**
     * This method is used to switch to alert.
     */
    public static void switchToAlert(){
        webDriverFacade.switchTo().alert();
    }

    /**
     * This method is used to get alert text.
     */
    public static String getAlertText(){
        return webDriverFacade.switchTo().alert().getText();
    }

    /**
     * This method is used to accept an alert.
     */
    public static void acceptAlert(){
        webDriverFacade.switchTo().alert().accept();
    }

    /**
     * This method is used to dismiss an alert.
     */
    public static void dismissAlert(){
        webDriverFacade.switchTo().alert().dismiss();
    }

    /**
     * This method is used to send keys to an alert.
     */
    public static void sendKeysToAlert(String text){
        webDriverFacade.switchTo().alert().sendKeys(text);
    }

    //endregion

    //region Driver Boolean Methods

    /**
     * This method is used to check if an element is enable.
     */
    public static boolean isElementEnabled(By locator){
        try
        {
            return getElement(locator).isEnabled();
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException | StaleElementReferenceException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element with certain index is enable.
     */
    public static boolean isElementEnabled(By locator, int index){
        try
        {
            return getElement(locator, index).isEnabled();
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException | StaleElementReferenceException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element with certain text is enable.
     */
    public static boolean isElementEnabled(By locator, String elementText){
        try
        {
            return getElement(locator, elementText).isEnabled();
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException | StaleElementReferenceException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element is visible.
     */
    public static boolean isElementVisible(By locator)
    {
        try
        {
            return getElement(locator).isDisplayed();
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException | StaleElementReferenceException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element with certain index is visible.
     */
    public static boolean isElementVisible(By locator, int index)
    {
        try
        {
            return getElement(locator, index).isDisplayed();
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException | StaleElementReferenceException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element with certain text is visible.
     */
    public static boolean isElementVisible(By locator, String text)
    {
        try
        {
            return getElement(locator, text).isDisplayed();
        }
        catch (NoSuchElementException | IllegalArgumentException | IllegalStateException | StaleElementReferenceException e)
        {
            return false;
        }
    }

    /**
     * This method is used to check if an element is selected.
     */
    public static boolean isElementSelected(By locator) { return getElement(locator).isSelected();}

    /**
     * This method is used to check if an element is displayed in the web screen.
     */
    public static boolean isElementOnTheUserScreen(By locator)
    {
        int  yAxisElementLocation = getElementByAxis(locator, "y");
        int windowsHeight = webDriverFacade.manage().window().getSize().height;
        return isElementVisible(locator) && windowsHeight - yAxisElementLocation >= 0;
    }

    /**
     * This method is used to check if an element contain certain text.
     */
    public static boolean containsText(By locator, String text){return getText(locator).contains(text);}

    /**
     * This method is used to check if an element with index contain certain text.
     */
    public static boolean containsText(By locator, int index, String text){
        return getText(locator, index).contains(text);
    }

    /**
     * This method is used to check if a group of elements contains certain text.
     */
    public static boolean elementsContainsText(By locator, String text)
    {
        List<String> texts = getElementsText(locator);
        for(String t: texts){
            if(!t.contains(text)){
                return false;
            }
        }
        return true;
    }

    /**
     * This method is used to check if a checkbox element is checked.
     */
    public static boolean isCheckboxchecked(By locator) {return getElement(locator).isSelected();}

    /**
     * This method is used to check if a group of checkbox elements are checked.
     */
    public static boolean areElementsChecked(By locator)
    {
        List<WebElement> elements = getElements(locator);
        for(WebElement e: elements){
            if(!e.isSelected()){
                return false;
            }
        }
        return true;
    }

    //endregion

    //region Driver Switch Methods

    /**
     * This method is used to switch to the last opened window.
     */
    public static void switchToLastOpenedWindow()
    {
        List<String> windowsHandles = (List<String>) webDriverFacade.getWindowHandles();
        int lastWindowHandle = windowsHandles.size() - 1;
        webDriverFacade.switchTo().window(windowsHandles.get(lastWindowHandle));
    }

    /**
     * This method is used to switch to the main window.
     */
    public static void switchToMainWindow() {
        List<String> windowsHandles = (List<String>) webDriverFacade.getWindowHandles();
        webDriverFacade.switchTo().window(windowsHandles.get(0));
    }

    /**
     * This method is used to switch to a window base on his title.
     */
    public static String SwitchToWindowByTitle(String title, int waitPageSeconds) throws InterruptedException {
        if (waitPageSeconds > 0) Utils.pauseSeconds(waitPageSeconds);
        for (String s: webDriverFacade.getWindowHandles())
        {
            webDriverFacade.switchTo().window(s);
            if (webDriverFacade.getTitle().contains(title))
                return webDriverFacade.getWindowHandle();
        }
        throw new IllegalArgumentException(String.format("There couldn't be found a windows with title: %s", title));
    }

    /**
     * This method is used to switch to a frame.
     */
    public static void switchToFrame(By locator){ webDriverFacade.switchTo().frame(getElement(locator));}

    //endregion
}