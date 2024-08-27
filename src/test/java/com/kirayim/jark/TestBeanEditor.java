package com.kirayim.jark;

import com.kirayim.jark.beans.BeanEditor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBeanEditor {


    @Test public void testBeanEditor() throws Exception {
        Object beanUnderTest = new DerivedTestClass();

        BeanEditor beanEditor = new BeanEditor(beanUnderTest, p -> {
            synchronized(beanUnderTest) {
                beanUnderTest.notify();
            }
        });


        synchronized (beanUnderTest) {
            beanUnderTest.wait();
        }
    }
}
