package com.kirayim.jark.beans;

import com.kirayim.jark.Jark;
import com.kirayim.jark.Request;
import com.kirayim.jark.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.function.Consumer;

public class BeanEditor<T> {

    static String defaultPage = """
            <html>
            <body>
                <h1>Base file for bean test</h1>

                <form>
                    INPUT_HERE
                    <input type="submit" formaction="/submitBean">
                </form>
            </body>
            </html>
            """;

    Jark jark;
    T bean;
    String basePage;
    String formTag;
    String submitFunction;
    Consumer<T> onUpdate;
    boolean ownJark = false;

    BeanFormGenerator formGenerator;

    // ===========================================================================

    public BeanEditor (Jark jark, T bean, String baseResource, String basePage, String formTag, String submitFunction, Consumer<T> onUpdate) throws Exception {

        if (jark == null) {
            jark = new Jark();
            jark.setPort(8085);
            jark.start();
            ownJark = true;
        }

        this.basePage = basePage;
        this.bean = bean;
        this.formTag = formTag;
        this.submitFunction = submitFunction;
        this.onUpdate = onUpdate;


        if (StringUtils.isBlank(baseResource)) {
            baseResource = "/";
        }

        if (StringUtils.isBlank(basePage) || StringUtils.isBlank(this.formTag)) {
            this.formTag = "INPUT_HERE";
        }

        if (StringUtils.isBlank(basePage) || StringUtils.isBlank(this.submitFunction)) {
            this.submitFunction = "/submitBean";
        }

        jark.get(baseResource, this::mainPage);
        jark.post(this.submitFunction, this::postForm);

        formGenerator = new BeanFormGenerator();

    }

    public BeanEditor (T bean, Consumer<T> onUpdate) throws Exception {
        this(null, bean, null, null, null, null, onUpdate);
    }

    // =============================================================================

    public String mainPage(Request request, Response response) throws Exception {
        InputStream inputStream;


        if (StringUtils.isBlank(basePage)) {
            inputStream = new ByteArrayInputStream(defaultPage.getBytes());
        } else {
            inputStream = jark.loadResourceAsStream(basePage);
        }

        byte[] data = inputStream.readAllBytes();
        String page = new String(data);

        String formData = formGenerator.generateBeanHtml(bean);

        int tagIndex = page.indexOf(formTag);

        if (tagIndex == -1) {
            return "Insertion point not found";
        }

        page = page.substring(0, tagIndex) + formData + page.substring(tagIndex + formTag.length());

        return page;
    }

    // =============================================================================


    public String postForm(Request request, Response response) throws Exception {


        if (onUpdate != null) {
            onUpdate.accept(bean);
        }

        return null;
    }

}
