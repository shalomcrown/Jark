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

import com.kirayim.jark.Jark;
import com.kirayim.jark.Request;
import com.kirayim.jark.Response;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
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
                      <input type="reset" value="Reset"  align="right"/>
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
        } else {
            this.jark = jark;
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
            Map<String, String> bodyMap = new HashMap<>();
            String[] items = StringUtils.split(body, "&");

            for (String item : items) {
                if (StringUtils.isNotBlank(item)) {
                    item = item.trim();
                    String[] parts = item.split("=");

                    if (parts.length >= 2) {
                        bodyMap.put(parts[0], URLDecoder.decode(parts[1]));
                    } else {
                        bodyMap.put(parts[0], null);
                    }
                }
            }

            for (Map.Entry<String, String> entry : bodyMap.entrySet()) {
                BeanFormItemInfo info = formGenerator.elementMap.get(entry.getKey());

                if (info != null) {
                    if (info.updater == null) {
                        BeanUtils.setProperty(info.bean, info.pdesc.getName(), bodyMap.get(entry.getKey()));
                    } else {
                        info.updater.accept(info, bodyMap);
                    }
                } else {
                    // TODO:
                }
            }
        }

        if (onUpdate != null) {
            onUpdate.accept(bean);
        }

        return mainPage(request, response);
    }

    // =================================================================================

    public void addConverter(Class<?> clazz, IBeanConverter converter) {
        formGenerator.converters.put(clazz, converter);
    }

    //=============================================================================================

    @Override
    public void close() throws IOException {
        if (ownJark && jark != null) {
            jark.close();
        }
    }
}
