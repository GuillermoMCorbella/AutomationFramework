package framework;

import io.appium.java_client.MobileElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

import static framework.AppiumDriverFacade.appiumDriverFacade;
import static java.io.File.separator;

public class Utils {

    public static final WebDriverWait wait = new WebDriverWait(appiumDriverFacade, 60);

    /**
     * This method is used to generate a pause to the test during a certain period of time.
     */
    public static void pauseSeconds(int seconds){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitForElementVisibility(MobileElement element){
        try{
        wait.until(ExpectedConditions.visibilityOf(element));
        }catch (IndexOutOfBoundsException | NullPointerException e){
            throw new IllegalArgumentException("There couldn't be found any element with the selector used");
        }
    }

    /**
     * This method is used to wait until a file is downloaded.
     */
    public static void waitForFileDownload(String downloadDirectory)
    {
        //falta Implementacion
    }

    public static String firefoxSeleniumDriver(){
        File srcApp= new File ("resources" + separator +"geckodriver.exe");
        return srcApp.getAbsolutePath();
    }

    public static String chromeSeleniumDriver(){
        File srcApp= new File ("resources" + separator +"chromedriver.exe");
        return srcApp.getAbsolutePath();
    }

    public static String edgeSeleniumDriver(){
        File srcApp= new File ("resources" + separator +"MicrosoftWebDriver.exe");
        return srcApp.getAbsolutePath();
    }
}
