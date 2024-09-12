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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class BeanFormGenerator {


    Map<String, BeanFormItemInfo> elementMap = new HashMap<>();

    public final static DateTimeFormatter standardFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    public final static DateTimeFormatter noTzFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    // ===========================================================================

    public static String getFormattedStringForName(String original) {
            return StringUtils.left(
                            StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(original), ' ')), 35);
    }

    //=============================================================================================

    private static void htmlCheckbox(Object beanBeingEdited, PropertyDescriptor pdesc, StringBuilder html, String tag) throws Exception {
        html.append("<input type=\"checkbox\"");
        if (((Boolean)pdesc.getReadMethod().invoke(beanBeingEdited)) == true) {
            html.append(" checked");
        }
        html.append(" name=\"").append(tag).append("\"");
        html.append(" id=\"").append(pdesc.getDisplayName()).append("\"");
        html.append("/>");
    }

    //=============================================================================================

    private static void htmlValueEdit(Object beanBeingEdited, String propertyName, StringBuilder html, String tag) throws Exception {
        html.append("<input type=\"text\"");
        Object value = BeanUtils.getProperty(beanBeingEdited, propertyName);

        if (value != null) {
            html.append(" value=");
            html.append(StringEscapeUtils.escapeHtml4(Objects.toString(value)));
        }

        html.append(" name=\"").append(tag).append("\"");
        html.append(" id=\"").append(propertyName).append("\"");
        html.append("/>");
    }

    //=============================================================================================

    public static void enumUpdater(BeanFormItemInfo info, Map<String, String> values) {
        try {
            // We can't access the Enum static function valueOf without 'opens' settings on the JVM
            // So need to do it the hard way....

            String value = values.get(info.tag);
            Object enumVal = null;

            if (StringUtils.isNotBlank(value)) {
                Object[] constants = info.pdesc.getPropertyType().getEnumConstants();

                enumVal = Arrays.stream(constants)
                        .filter(p -> p.toString().equals(value))
                        .findFirst()
                        .get();
            }

            BeanUtils.setProperty(info.bean, info.pdesc.getName(), enumVal);

        } catch (Exception e) {
            // Ignoring bad values.
        }
    }

    //=============================================================================================

    private static void htmlEnumEditor(BeanFormItemInfo info, StringBuilder html, Object value) {
        html.append("<select name=\"").append(info.tag).append("\"");
        html.append(" id=\"").append(info.pdesc.getDisplayName()).append("\"");
        html.append(">\n");

        for (Object item : info.pdesc.getPropertyType().getEnumConstants()) {
            html.append("<option value=\"").append(item).append("\"");
            if (item == value) {
                html.append(" selected=\"true\"");
            }
            html.append(">");
            html.append(item.toString());
            html.append("</option>");
        }
        html.append("</select>");

        info.updater = BeanFormGenerator::enumUpdater;
    }

    //=============================================================================================

    public static void temporalUpdater(BeanFormItemInfo info, Map<String, String> values) {
        try {
            // We can't access the Enum static function valueOf without 'opens' settings on the JVM
            // So need to do it the hard way....

            String value = values.get(info.tag);
            Object timeVal = null;
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            if (StringUtils.isNotBlank(value)) {
                LocalDateTime localDateTime = LocalDateTime.parse(value, formatter);
                var time = formatter.parse(value);
                Class<?> objectClass = info.pdesc.getPropertyType();
                timeVal = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

                if (objectClass.isAssignableFrom(Date.class)) {
                    timeVal = Date.from((Instant) timeVal);
                }
            }

            BeanUtils.setProperty(info.bean, info.pdesc.getName(), timeVal);

        } catch (Exception e) {
            // Ignoring bad values.
        }
    }

    //=============================================================================================

    private static void htmlTemporalEditor(BeanFormItemInfo info, StringBuilder html, Object value) {
        html.append("<input type=\"datetime-local\" name=\"").append(info.tag).append("\"");
        html.append(" id=\"").append(info.pdesc.getDisplayName()).append("\"");

        if (value != null) {
            Class<?> objectClass = info.pdesc.getPropertyType();
            String stringValue = null;

            if (objectClass.isAssignableFrom(Date.class)) {
                stringValue = standardFormatter.format(((Date) value).toInstant());

            } else if (objectClass.isAssignableFrom(Instant.class)) {
                stringValue = standardFormatter.format((Instant)value);
            } else {
                stringValue = noTzFormatter.format((TemporalAccessor) value);
            }


            html.append(" value=\"").append(StringEscapeUtils.escapeHtml4(stringValue)).append("\"");
        }

        html.append("/>\n");

        info.updater = BeanFormGenerator::temporalUpdater;
    }

    // =================================================================================

    private static void htmlCollectionEditor(Object beanBeingEdited, String parent, PropertyDescriptor pdesc, StringBuilder html) throws Exception {
        Object obj = pdesc.getReadMethod().invoke(beanBeingEdited);


    }

    // =================================================================================

    private void htmlArrayEditor(Object beanBeingEdited, String parent, PropertyDescriptor pdesc, StringBuilder html) throws Exception {
        Collection obj = (Collection)pdesc.getReadMethod().invoke(beanBeingEdited);

        int index = 0;
        html.append("<table>");

        for (Object item : obj) {
            String tag = String.format("%s[%d]", parent, index);

            html.append("<tr><td>");
            html.append("<input type=\"radio\" id=\"").append(tag).append("_select\"");
            html.append(" name=\"").append(index).append("\"/>");
            html.append("</td><td>");
            generateBeanHtml(item, tag);
            html.append("</td></tr>");

            index++;
        }


        html.append("</table>");

    }


    // ===========================================================================

    public String generateBeanHtml(Object beanBeingEdited) throws Exception{
        return generateBeanHtml(beanBeingEdited, null);
    }

    // =================================================================================

    private void getHtmlForElement(Object beanBeingEdited, String parent, PropertyDescriptor pdesc, StringBuilder html) throws Exception {
        html.append("<tr><td>");
        html.append(getFormattedStringForName(pdesc.getName()));
        html.append("</td><td>");

        String tag = parent + pdesc.getName();
        Field field = JarkBeanUtils.getField(beanBeingEdited, pdesc.getName());

        BeanFormItemInfo formItemInfo = new BeanFormItemInfo(tag, pdesc, beanBeingEdited);

        elementMap.put(tag, formItemInfo);

        if (beanBeingEdited.getClass().isArray()) {
            htmlArrayEditor(beanBeingEdited, tag, pdesc, html);

        } else if (Collection.class.isAssignableFrom(beanBeingEdited.getClass())) {
            htmlCollectionEditor(beanBeingEdited, tag, pdesc, html);

        } else  if (Boolean.class.isAssignableFrom(pdesc.getPropertyType())
                || pdesc.getPropertyType().equals(boolean.class)
                || pdesc.getPropertyType().equals(Boolean.class)) {

            htmlCheckbox(beanBeingEdited, pdesc, html, tag);


        } else if (File.class.isAssignableFrom(pdesc.getPropertyType())
                || (field != null
                    && (field.isAnnotationPresent(ModelAnnotations.FileName.class)
                        || field.isAnnotationPresent(ModelAnnotations.FolderName.class)))) {
            htmlValueEdit(beanBeingEdited, pdesc.getName(), html, tag);

            // TODO
//				editorComponent = GuiUtils.getFileItemPanel(beanBeingEdited, pdesc.getName());

        } else if (pdesc.getPropertyType().isPrimitive()
                || pdesc.getPropertyType().isAssignableFrom(String.class)
                || Number.class.isAssignableFrom(pdesc.getPropertyType())) {

            htmlValueEdit(beanBeingEdited, pdesc.getName(), html, tag);

        } else if (Enum.class.isAssignableFrom(pdesc.getPropertyType()) || pdesc.getPropertyType().isEnum()) {
            Object value = pdesc.getReadMethod().invoke(beanBeingEdited);

            htmlEnumEditor(formItemInfo, html, value);

        } else if (pdesc.getPropertyType().isArray()) {
            // TODO:
            html.append("Type not implemented");

        } else if (Collection.class.isAssignableFrom(pdesc.getPropertyType())) {
            html.append("Type not implemented");

//				Method method = pdesc.getReadMethod();
//				method.setAccessible(true);
//				Object value = method.invoke(beanBeingEdited, (Object[]) null);
//
//				if (value == null) {
//					value = new ArrayList<>();
//					BeanUtils.setProperty(beanBeingEdited, pdesc.getName(), value);
//				}
//
//				Class<?> itemsClazz = null;
//
//				ModelAnnotations.NewClassesForSettingsDialog multipleOptionAnnotation = BeanUtilities.getAnnotation(beanBeingEdited, pdesc.getName(), ModelAnnotations.NewClassesForSettingsDialog.class);
//
//				if (multipleOptionAnnotation == null) {
//					ModelAnnotations.NewClassForSettingsDialog annotation = BeanUtilities.getAnnotation(beanBeingEdited, pdesc.getName(), ModelAnnotations.NewClassForSettingsDialog.class);
//
//					if (annotation != null) {
//						itemsClazz = annotation.value();
//					}
//				}
//
//				editorComponent = collectionEditorComponent(value, itemsClazz, multipleOptionAnnotation, restricted);

        } else if (Temporal.class.isAssignableFrom(pdesc.getPropertyType())) {
            Object value = pdesc.getReadMethod().invoke(beanBeingEdited);
            htmlTemporalEditor(formItemInfo, html, value);

        } else if (Map.class.isAssignableFrom(pdesc.getPropertyType())) {
            html.append("Type not implemented");

//				Method method = pdesc.getReadMethod();
//				method.setAccessible(true);
//				Object value = method.invoke(beanBeingEdited, (Object[]) null);
//
//				if (value == null) {
//					editorComponent = new JLabel("Couldn't get value (empty maps not yet supported)");
//
////					value = new ArrayList<>();
////					BeanUtils.setProperty(beanBeingEdited, pdesc.getName(), value);
//				} else {
//					Class<?> valuesClazz = null;
//					Class<?> keysClazz = null;
//
//
////				ModelAnnotations.NewClassesForSettingsDialog multipleOptionAnnotation = BeanUtilities.getAnnotation(beanBeingEdited, pdesc.getName(), ModelAnnotations.NewClassesForSettingsDialog.class);
////
////				if (multipleOptionAnnotation == null) {
////					ModelAnnotations.NewClassForSettingsDialog annotation = BeanUtilities.getAnnotation(beanBeingEdited, pdesc.getName(), ModelAnnotations.NewClassForSettingsDialog.class);
////
////					if (annotation != null) {
////						itemsClazz = annotation.value();
////					}
////				}
//
//					editorComponent = mapEditorComponent(value, valuesClazz, keysClazz, pdesc, null, restricted);
//				}

//			} else if (Duration.class.isAssignableFrom(pdesc.getPropertyType())) {
//				editorComponent = GuiUtils.getDurationEditor(beanBeingEdited, pdesc.getName());

//			} else if (Quantity.class.isAssignableFrom(pdesc.getPropertyType())) {
//				editorComponent = GuiUtils.getQuantityEditor(beanBeingEdited, pdesc.getName());

        } else {
            Object value = null;
            try {
                Method method = pdesc.getReadMethod();
                method.setAccessible(true);
                value = method.invoke(beanBeingEdited, (Object[]) null);

                if (value != null) {
                    html.append(generateBeanHtml(value, tag));

//						if (restricted != null) {
//							editorComponent.setBackground(RESTRICTED_COLOR);
//							editorComponent.setForeground(Color.WHITE);
//						}
                } else {
                    html.append("<p>Empty value</p>");
                }
            } catch (Exception e) {
                html.append("<p>Couldn't get value</p>");
            }

//				if (value == null && BeanUtilities.isAnnotationPresent(beanBeingEdited, pdesc.getName(),
//						NewClassForSettingsDialog.class)) {
//					var panel = new JPanel();
//					panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
//
//					panel.add(editorComponent);
//					editorComponent = panel;
//
//					var newButton = new JButton("New");
//					panel.add(Box.createHorizontalStrut(10));
//					panel.add(newButton);
//
//					var annotation = BeanUtilities.getAnnotation(beanBeingEdited, pdesc.getName(),
//							ModelAnnotations.NewClassForSettingsDialog.class);
//
//					newButton.addActionListener(new NewFixedButtonAction(pdesc.getWriteMethod(), beanBeingEdited,
//							annotation.value(), this, panel, restricted, authFactory));
//
//				} else if (BeanUtilities.isAnnotationPresent(beanBeingEdited, pdesc.getName(),
//						NewClassesForSettingsDialog.class)) {
//					JPanel panel = new JPanel();
//					panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
//
//					panel.add(editorComponent);
//					editorComponent = panel;
//
//					JButton newButton = new JButton("New");
//					panel.add(newButton);
//
//					Class<?> clazz = (value == null) ? null : value.getClass();
//					var annotation = BeanUtilities.getAnnotation(beanBeingEdited, pdesc.getName(),
//							ModelAnnotations.NewClassesForSettingsDialog.class);
//
//					Consumer<Component> replaceComponent = c -> {
//						panel.removeAll();
//						panel.add(c);
//						panel.add(newButton);
//						panel.revalidate();
//						panel.repaint();
//					};
//
//					newButton.addActionListener(new NewButtonAction(pdesc.getWriteMethod(), beanBeingEdited, clazz,
//							this, replaceComponent, annotation, restricted, authFactory));
        }

        html.append("</td></tr>\n");
    }


    //=============================================================================================

    public String generateBeanHtml(Object beanBeingEdited, String parent) throws Exception {
        StringBuilder html = new StringBuilder();

        if (parent == null) {
                parent = "";
        } else {
                parent = parent + ".";
        }

        BeanInfo info = Introspector.getBeanInfo(beanBeingEdited.getClass());

        PropertyDescriptor[] props = info.getPropertyDescriptors();
        Arrays.sort(props, (p, q) -> p.getName().compareTo(q.getName()));

        html.append("<table>\n");

        for (PropertyDescriptor pdesc : props) {

            if (pdesc.getReadMethod() == null || pdesc.getWriteMethod() == null || pdesc.isHidden()) {
                continue;
            }

            if (JarkBeanUtils.isAnnotationPresent(beanBeingEdited, pdesc.getName(), ModelAnnotations.HiddenFromSettingsDialog.class)) {
                    continue;
            }
//
//            if (extraInfo != null && ArrayUtils.contains(extraInfo.getHiddenFields(),  pdesc.getName())) {
//                    continue;
//            }

            if (pdesc.getDisplayName().equals("type") || pdesc.getDisplayName().equals("class")) {
                    continue;
            }

            getHtmlForElement(beanBeingEdited, parent, pdesc, html);
        }


        // TODO:
//		if (editorComponent != null) {
//			LimitWidgetHeight heightLimit = BeanUtilities.getAnnotation(beanBeingEdited, pdesc.getName(), LimitWidgetHeight.class);
//
//			if (heightLimit != null) {
//				JScrollPane scrollPane = new JScrollPane(editorComponent);
//				scrollPane.setPreferredSize(new Dimension(getPreferredSize().width, heightLimit.value()));
//
//				editorComponent = scrollPane;
//			}
//		}
//
//		if ((editorComponent != null) && (readOnly == true)) {
//			editorComponent.setEnabled(false);
//			if (editorComponent instanceof Container) {
//				modifyComponents((Container) editorComponent, c -> c.setEnabled(false));
//			}
//		}

        html.append("</table>\n");

        return html.toString();
    }

}
