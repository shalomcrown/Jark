package com.kirayim.jark.beans;

import java.beans.PropertyDescriptor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BeanFormItemInfo {
    String tag;
    PropertyDescriptor pdesc;
    Object bean;
    BiConsumer<BeanFormItemInfo, String> updater;

    public BeanFormItemInfo(String tag, PropertyDescriptor pdesc, Object bean, BiConsumer<BeanFormItemInfo, String> updater) {
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
