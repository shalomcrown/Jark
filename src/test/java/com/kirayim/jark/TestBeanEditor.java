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
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class TestBeanEditor {

    private WebDriver driver;
    BeanEditor beanEditor;
    DerivedTestClass beanUnderTest;

    @Before
    public void initializeSelenium() throws Exception {
        driver = new ChromeDriver();

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

        driver.findElement(By.name("beanform")).submit();
        Assert.assertEquals(1234, beanUnderTest.getDerivedItem());
    }
}
