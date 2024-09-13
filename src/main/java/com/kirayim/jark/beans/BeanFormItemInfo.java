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


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public PropertyDescriptor getPdesc() {
        return pdesc;
    }

    public void setPdesc(PropertyDescriptor pdesc) {
        this.pdesc = pdesc;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public BiConsumer<BeanFormItemInfo, Map<String, String>> getUpdater() {
        return updater;
    }

    public void setUpdater(BiConsumer<BeanFormItemInfo, Map<String, String>> updater) {
        this.updater = updater;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}



