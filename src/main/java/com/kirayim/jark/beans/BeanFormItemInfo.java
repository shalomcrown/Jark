package com.kirayim.jark.beans;

import java.beans.PropertyDescriptor;
import java.util.function.Consumer;

public class BeanFormItemInfo {
    String tag;
    PropertyDescriptor pdesc;
    Object bean;
    Consumer<?> updater;

    public BeanFormItemInfo(String tag, PropertyDescriptor pdesc, Object bean, Consumer<?> updater) {
        this.tag = tag;
        this.pdesc = pdesc;
        this.bean = bean;
        this.updater = updater;
    }

    public BeanFormItemInfo(String tag, PropertyDescriptor pdesc, Object bean) {
        this.tag = tag;
        this.pdesc = pdesc;
        this.bean = bean;
    }
}
