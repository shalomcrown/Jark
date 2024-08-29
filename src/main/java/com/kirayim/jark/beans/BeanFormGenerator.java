package com.kirayim.jark.beans;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeanFormGenerator {
    Map<String, Object[]> elementMap = new HashMap<>();

    // ===========================================================================

    public static String getFormattedStringForName(String original) {
            return StringUtils.left(
                            StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(original), ' ')), 35);
    }


    // ===========================================================================

    public String generateBeanHtml(Object beanBeingEdited) throws Exception{
        return generateBeanHtml(beanBeingEdited, null);
    }


    public String generateBeanHtml(Object beanBeingEdited, String parent) throws Exception{
        StringBuilder html = new StringBuilder();

        if (beanBeingEdited.getClass().isArray() || Collection.class.isAssignableFrom(beanBeingEdited.getClass())) {
                // todo
//			return collectionEditorComponent(beanBeingEdited, arrayItemClazz, null, parentRestricted);
                return null;
        }

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

//            if (BeanUtilities.isAnnotationPresent(beanBeingEdited, pdesc.getName(), ModelAnnotations.HiddenFromSettingsDialog.class)) {
//                    continue;
//            }
//
//            if (extraInfo != null && ArrayUtils.contains(extraInfo.getHiddenFields(),  pdesc.getName())) {
//                    continue;
//            }

            if (pdesc.getDisplayName().equals("type") || pdesc.getDisplayName().equals("class")) {
                    continue;
            }


            html.append("<tr><td>");
            html.append(getFormattedStringForName(pdesc.getName()));
            html.append("</td><td>");

            String tag = parent + pdesc.getName();
            Field field = BeanUtils.getField(beanBeingEdited, pdesc.getName());
            elementMap.put(tag, new Object[]{pdesc, beanBeingEdited});

            if (Boolean.class.isAssignableFrom(pdesc.getPropertyType())
                            || pdesc.getPropertyType().equals(boolean.class)
                            || pdesc.getPropertyType().equals(Boolean.class)) {

                    html.append("<input type=\"checkbox\" checked=");
                    html.append(pdesc.getReadMethod().invoke(beanBeingEdited));
                    html.append(" name=\"").append(tag).append("\"");
                    html.append("/>");


            } else if (File.class.isAssignableFrom(pdesc.getPropertyType())
                            || field.isAnnotationPresent(ModelAnnotations.FileName.class)
                            || field.isAnnotationPresent(ModelAnnotations.FolderName.class)) {
                html.append("Type not implemented");

                    // TODO
//				editorComponent = GuiUtils.getFileItemPanel(beanBeingEdited, pdesc.getName());

            } else if (pdesc.getPropertyType().isPrimitive()
                            || pdesc.getPropertyType().isAssignableFrom(String.class)
                            || Number.class.isAssignableFrom(pdesc.getPropertyType())) {

                    html.append("<input type=\"text\" value=");
                    html.append(StringEscapeUtils.escapeHtml4(pdesc.getReadMethod().invoke(beanBeingEdited).toString()));
                    html.append(" name=\"").append(tag).append("\"");
                    html.append("/>");

            } else if (Enum.class.isAssignableFrom(pdesc.getPropertyType())) {
//				editorComponent = GuiUtils.getEnumComboBox(beanBeingEdited, pdesc.getName(),
//						(Class<? extends Enum<?>>) pdesc.getPropertyType());
                html.append("Type not implemented");

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
                html.append("Type not implemented");

//				editorComponent = GuiUtils.getDatePicker(beanBeingEdited, pdesc.getName());

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
