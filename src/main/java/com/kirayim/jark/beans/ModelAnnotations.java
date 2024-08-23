package com.kirayim.jark.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ModelAnnotations {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface NewClassesForSettingsDialog {
		ClassChoice[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ClassChoice {
		Class<?> value();
		String choiceName();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface NewClassForSettingsDialog {
		Class<?> value();
	}

	/**
	 * Mark member as read-only in settings dialog
	 *
	 * @author shalomc
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ReadonlyInSettingsDialog {
	}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DisplayFieldForSelection {
        String value();
    }

	/**
	 * Mark field as hidden from the GUI settings tool
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD})
	public @interface HiddenFromSettingsDialog {
	}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface Description {
        String value();
        String units() default "";
    }

    /**
     * Indicates that the field represents a file name
     * @author shalomc
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FileName {
    }

    /**
     * Indicates that the field represents a folder name
     * @author shalomc
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FolderName {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SettingsTabName {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Restricted {
        String value();
        boolean isReadOnly() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface FieldName {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface LimitWidgetHeight {
        int value();
    }

	public ModelAnnotations() {
		// TODO Auto-generated constructor stub
	}

}
