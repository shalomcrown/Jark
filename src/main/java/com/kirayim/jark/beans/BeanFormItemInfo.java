package com.kirayim.jark.beans;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.function.BiConsumer;

public class BeanFormItemInfo {
    String tag;
    PropertyDescriptor pdesc;
    Object bean;
    BiConsumer<BeanFormItemInfo, Map<String, String>> updater;
    Class<?> clazz;
    String displayName;

    public BeanFormItemInfo(String tag, PropertyDescriptor pdesc, Object bean, BiConsumer<BeanFormItemInfo, Map<String, String>> updater) {
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
