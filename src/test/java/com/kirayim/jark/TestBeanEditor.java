package com.kirayim.jark;

import com.kirayim.jark.beans.BeanEditor;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;

public class TestBeanEditor {


    @Test public void testBeanEditor() throws Exception {
        Object beanUnderTest = new DerivedTestClass();

        BeanEditor beanEditor = new BeanEditor(beanUnderTest, p -> {
            synchronized(beanUnderTest) {
                beanUnderTest.notify();
            }
        });

        URL url = new URL("http://localhost:8085");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(500000);
        con.setReadTimeout(500000);

        con.getResponseCode();

        synchronized (beanUnderTest) {
            beanUnderTest.wait();
        }
    }
}
