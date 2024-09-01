package com.kirayim.jark.beans;

import com.kirayim.jark.Jark;
import com.kirayim.jark.Request;
import com.kirayim.jark.Response;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class BeanEditor<T> implements Closeable {

    static String defaultPage = """
            <html>
                <link rel="stylesheet" href="test-style.css"/>
            <body>
                <h1>Base file for bean test</h1>

                <form name="beanform" action="/submitBean" method="post" >
                    INPUT_HERE
                     <input type="submit" value="Submit"/>
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
        jark.location("test-style.css", "test-style.css");

        formGenerator = new BeanFormGenerator();

    }

    public BeanEditor (T bean, Consumer<T> onUpdate) throws Exception {
        this(null, bean, null, null, null, null, onUpdate);
    }

    public BeanEditor (T bean) throws Exception {
        this(null, bean, null, null, null, null, null);
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

        String body = request.body();

        if (StringUtils.isNoneBlank(body)) {
            String[] items = StringUtils.split(body, "&");

            for (String item : items) {
                if (StringUtils.isNotBlank(item)) {
                    item = item.trim();
                    String[] parts = item.split("=");

                    if (parts.length >= 2) {
                        BeanFormItemInfo info = formGenerator.elementMap.get(parts[0]);

                        if (info != null) {
                            if (info.updater == null) {
                                BeanUtils.setProperty(info.bean, info.pdesc.getName(), parts[1]);
                            } else {
                                info.updater.accept(info, parts[1]);
                            }
                        } else {
                            // TODO:
                        }

                    } else if (parts.length == 1) {
                        BeanFormItemInfo info = formGenerator.elementMap.get(parts[0]);
                        BeanUtils.setProperty(info.bean, info.pdesc.getName(), null);
                    }
                }
            }
        }

        if (onUpdate != null) {
            onUpdate.accept(bean);
        }

        return mainPage(request, response);
    }

    //=============================================================================================

    @Override
    public void close() throws IOException {
        if (ownJark && jark != null) {
            jark.close();
        }
    }
}
