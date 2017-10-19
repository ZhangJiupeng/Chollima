package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.entity.Page;
import cc.gospy.core.entity.Result;
import cc.gospy.core.entity.Task;
import cc.gospy.core.processor.Processor;
import cc.gospy.core.processor.impl.JSoupProcessor;
import cc.gospy.core.util.StringHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.Xsoup;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/*
 * This is a special component which associate with the PPDesigner.
 */
public class XPathProcessComponent extends Component<Processor> {
    public XPathProcessComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        JsonObject json = parser.parse(configJson).getAsJsonObject();
        JsonArray dataArray = json.get("data").getAsJsonArray();
        JsonArray linkArray = json.get("links").getAsJsonArray();
        String url = json.get("url").getAsString();
        url = url.contains("://") ? url.substring(url.indexOf("://") + 3) : url;
        String host = url.contains("/") ? url.substring(0, url.indexOf("/")) : url;
        JSoupProcessor.Builder builder = JSoupProcessor.custom();
        builder.setDocumentExtractor((page, document) -> {
            List<String> data = new ArrayList<>();
            List<String> xpaths = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            List<Task> tasks = new ArrayList<>();
            Task task = page.getTask();
            dataArray.forEach(jsonElement -> {
                String xpath = jsonElement.getAsString();
                if (xpath.startsWith("//html[1]/body[1]/")) {
                    xpath = "//html/body/" + xpath.substring(18);
                }
                xpath = xpath.concat("/allText()");
                xpaths.add(xpath);
                urls.add(page.getTask().getUrl());
                data.addAll(Xsoup.compile(xpath).evaluate(document).list());
            });
            linkArray.forEach(jsonElement -> {
                String xpath = jsonElement.getAsString();
                if (xpath.startsWith("//html[1]/body[1]/")) {
                    xpath = "//html/body/" + xpath.substring(18);
                }
                if (xpath.substring(xpath.lastIndexOf('/')).contains("img")) {
                    xpath = xpath.concat("/@src");
                } else {
                    xpath = xpath.concat("/@href");
                }
                Xsoup.compile(xpath).evaluate(document).list().forEach(newUrl -> tasks.add(new Task(StringHelper.toAbsoluteUrl(
                        task.getProtocol(),
                        task.getHost(),
                        task.getUrl(),
                        newUrl
                ))));
            });
            return new Result<>(tasks, new List[]{urls, xpaths, data});
        }).setTaskFilter(task -> task.getHost().equals(host));
        this.core = builder.build();
    }

    public String getCharacterEncoding(Page page) {
        if (page.getExtra() == null || page.getExtra().get("Content-Type") == null) {
            return null;
        }
        for (String kv : page.getExtra().get("Content-Type").toString().split(";")) {
            if (kv.trim().startsWith("charset=")) {
                return kv.trim().substring(8);
            }
        }
        return null;
    }

    public Document getDocument(Page page) throws UnsupportedEncodingException {
        String charsetName = getCharacterEncoding(page);
        String html = new String(page.getContent(), charsetName != null ? charsetName : Charset.defaultCharset().name());
        return Jsoup.parse(html);
    }

    public static String getDefaultConfigJson() {
        return "{" +
                "\"name\":\"name\"," +
                "\"url\":\"localhost:8080\"," +
                "\"data\":[]," +
                "\"links\":[]" +
                "}";
    }
}
