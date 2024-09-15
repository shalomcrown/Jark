/*
 * Copyright notice
 * This code is not covered by any copyright
 *
 * In no event shall the author(s) be liable for any special, direct, indirect, consequential,
 *  or incidental damages or any damages whatsoever, whether in an action of contract,
 *  negligence or other tort, arising out of or in connection with the use of the code or the
 *  contents of the code
 *
 *  All information in the code is provided "as is" with no guarantee of completeness, accuracy,
 *   timeliness or of the results obtained from the use of this code, and without warranty of any
 *   kind, express or implied, including, but not limited to warranties of performance,
 *   merchantability and fitness for a particular purpose.
 *
 *  The author(s) will not be liable to You or anyone else for any decision made or action
 *  taken in reliance on the information given by the code or for any consequential, special
 *  or similar damages, even if advised of the possibility of such damages.
 *
 *
 */

package com.kirayim.jark;

import com.kirayim.jark.beans.BeanEditor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

public class TestBeanEditor {

    private WebDriver driver;
    BeanEditor<?> beanEditor;
    DerivedTestClass beanUnderTest;

    @Before
    public void initializeSelenium() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");

        driver = new ChromeDriver(options);

        beanUnderTest = new DerivedTestClass();

        beanEditor = new BeanEditor<>(beanUnderTest, p -> {
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

        // This tests HTML encoding of page and URL decoding of form submission
        element = driver.findElement(By.id("fileName"));
        element.clear();
        String newFileName = "/this/is/another/file.txt";
        element.sendKeys(newFileName);


        driver.findElement(By.name("beanform")).submit();
        Assert.assertEquals(1234, beanUnderTest.getDerivedItem());
        Assert.assertEquals(TestSubClass.TestTypes.SMILEY, beanUnderTest.getSub().getTestType());
        Assert.assertEquals(true, beanUnderTest.isValid());
        Assert.assertEquals(newFileName, beanUnderTest.getFileName());
    }

    //=============================================================================================

    /**
     * If you want to run the web page
     * @param args Main args
     * @throws Exception on something
     */
    public static void main(String[] args) throws Exception {
        var beanUnderTest = new DerivedTestClass();

        new BeanEditor<>(beanUnderTest, p -> {
            synchronized(beanUnderTest) {
                beanUnderTest.notify();
            }
        });

        synchronized (beanUnderTest) {
            beanUnderTest.wait();
        }
    }
}
