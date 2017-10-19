package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.entity.Result;
import cc.gospy.core.entity.Task;
import cc.gospy.core.processor.Processor;
import cc.gospy.core.processor.Processors;
import cc.gospy.core.util.StringHelper;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class WebCapturerProcessComponent extends Component<Processor> {
    private static final Logger logger = LoggerFactory.getLogger(WebCapturerProcessComponent.class);

    public WebCapturerProcessComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        this.core = Processors.JSoupProcessor.custom()
                .setDocumentExtractor((page, document) -> {
                    Task task = page.getTask();
                    if (page.getStatusCode() != 200) {
                        if (page.getStatusCode() == 302 || page.getStatusCode() == 301) {
                            Collection<Task> tasks = new ArrayList<>();
                            tasks.add(new Task(page.getExtra().get("Location").toString()));
                            logger.warn("Redirect to address: {}", page.getExtra().get("Location"));
                            return new Result<>(tasks);
                        } else {
                            logger.warn("Status [{}], when handle [{}]", page.getStatusCode(), task);
                            return new Result<>(null);
                        }
                    }
                    Collection<Task> tasks = new HashSet<>();
                    Collection<Element> elements = document.select("a[href]");
                    elements.addAll(document.select("link[href]"));
                    elements.addAll(document.select("[src]"));
                    for (Element element : elements) {
                        String link = element.hasAttr("href") ? element.attr("href") : element.attr("src");
                        link = link.indexOf('#') != -1 ? link.substring(0, link.indexOf('#')) : link;
                        boolean rootPage = link.endsWith("/");
                        link = rootPage ? link.concat("null") : link;
                        link = StringHelper.toAbsoluteUrl(task.getProtocol(), task.getHost(), task.getUrl(), link);
                        if (link.matches("^https?://((?!javascript:|mailto:| ).)*")) {
                            String url = link;
                            String rUrl = StringHelper.toRelativeUrl(task.getProtocol(), task.getHost(), task.getUrl(), url);
                            if (rUrl == null) {
                                // crawl without outside pages
                                continue;
                            } else {
                                tasks.add(new Task(rootPage ? link.substring(0, link.length() - 4) : link));
                            }
                            String name = rUrl.substring(rUrl.lastIndexOf('/') + 1);
                            name = StringHelper.toEscapedFileName(name);
                            if (element.tagName().equals("a")) {
                                name = name.endsWith(".html") ? name : name.concat(".html");
                            }
                            rUrl = rUrl.substring(0, rUrl.lastIndexOf('/') + 1);
                            String modifiedLink = rUrl.concat(name);
                            element.attr(element.hasAttr("href") ? "href" : "src", modifiedLink);
                        }
                    }
                    String name = StringHelper.toEscapedFileName(task.getUrl().substring(task.getUrl().lastIndexOf('/') + 1));
                    if (page.getContentType().equals("text/html") && !name.endsWith(".html")) {
                        name += ".html";
                    }
                    String dir = StringHelper.cutOffProtocolAndHost(task.getUrl().substring(0, task.getUrl().lastIndexOf('/') + 1));
                    page.getExtra().put("savePath", URLDecoder.decode(dir.concat(name), Charset.defaultCharset().name()));
                    logger.info("Saving {}...", page.getExtra().get("savePath"));
                    Result<byte[]> result = new Result<>(tasks, document.toString().getBytes());
                    return result;
                }).build();
    }

    public static String getDefaultConfigJson() {
        return "{}";
    }

    public static String getTemplateName() {
        return "Web Capturer Processor";
    }
}
