package com.kirayim.jark;

import com.kirayim.jark.beans.BeanEditor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class TestBeanEditor {

    private WebDriver driver;
    BeanEditor beanEditor;
    DerivedTestClass beanUnderTest;

    @Before
    public void initializeSelenium() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");

        driver = new ChromeDriver(options);

        beanUnderTest = new DerivedTestClass();

        beanEditor = new BeanEditor(beanUnderTest, p -> {
            synchronized(beanUnderTest) {
                beanUnderTest.notify();
            }
        });

        driver.get("http://localhost:8085");
    }

    //=============================================================================================

    @After
    public void after() throws Exception {
        driver.quit();
        beanEditor.close();
    }

    //=============================================================================================

    @Test
    public void testBeanEditor() throws Exception {
        var element = driver.findElement(By.id("derivedItem"));
        element.clear();
        element.sendKeys("1234");

        new Select(driver.findElement(By.id("testType"))).selectByVisibleText("SMILEY");

        driver.findElement(By.id("valid")).click();

        driver.findElement(By.name("beanform")).submit();
        Assert.assertEquals(1234, beanUnderTest.getDerivedItem());
        Assert.assertEquals(TestSubClass.TestTypes.SMILEY, beanUnderTest.getSub().getTestType());
        Assert.assertEquals(true, beanUnderTest.isValid());
    }

    //=============================================================================================

    /**
     * If you want to run the web page
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        var beanUnderTest = new DerivedTestClass();

        var beanEditor = new BeanEditor(beanUnderTest, p -> {
            synchronized(beanUnderTest) {
                beanUnderTest.notify();
            }
        });

        synchronized (beanUnderTest) {
            beanUnderTest.wait();
        }
    }
}
