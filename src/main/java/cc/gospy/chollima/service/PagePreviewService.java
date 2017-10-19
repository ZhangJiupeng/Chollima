package cc.gospy.chollima.service;

import cc.gospy.chollima.entity.Message;
import cc.gospy.core.entity.Page;
import cc.gospy.core.entity.Result;
import cc.gospy.core.entity.Task;
import cc.gospy.core.fetcher.FetchException;
import cc.gospy.core.fetcher.Fetcher;
import cc.gospy.core.fetcher.Fetchers;
import cc.gospy.core.fetcher.UserAgent;
import cc.gospy.core.fetcher.impl.HttpFetcher;
import cc.gospy.core.processor.Extractor;
import cc.gospy.core.processor.impl.JSoupProcessor;
import cc.gospy.core.util.StringHelper;
import org.apache.http.client.config.RequestConfig;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PagePreviewService {
    private static final Logger logger = LoggerFactory.getLogger(PagePreviewService.class);
    private Fetchers fetchers;
    private JSoupProcessor linkFormatter;

    public PagePreviewService() {
        fetchers = new Fetchers();
        fetchers.register(Fetchers.FileFetcher.getDefault());
        fetchers.register(Fetchers.HttpFetcher.custom()
                .before(request -> request.setConfig(RequestConfig.custom()
                        .setRedirectsEnabled(true)
                        .setRelativeRedirectsAllowed(false)
                        .setCircularRedirectsAllowed(false).build()))
                .setAutoKeepAlive(false)
                .setUserAgent(UserAgent.Chrome_56_0_2924_87_Win_10_64_bit)
                .build());
        linkFormatter = JSoupProcessor.custom().setDocumentExtractor(new PagePreviewExtractor()).build();
    }

    public Message getDocument(String url) {
        return getDocument(url, null);
    }

    public Message getDocument(String url, String cookie) {
        try {
            Task task = new Task(url.contains("://") ? url : "http://".concat(url));
            Fetcher fetcher = fetchers.get(task.getProtocol());
            String result;
            if (fetcher.getClass() == HttpFetcher.class && cookie != null) {
                task.getExtra().put("cookie", cookie);
                logger.info("Cookie set for {}: {}", task.getHost(), cookie);
            }
            Page page = fetcher.fetch(task);
            if (page.getContentType().equals("text/html")) {
                result = linkFormatter.process(task, page).getData().toString();
            } else {
                result = new String(fetcher.fetch(task).getContent());
            }
            return new Message(result);
        } catch (Throwable e) {
            String errorMessage;
            if (e.getClass() == FetchException.class && e.getCause() != null) {
                errorMessage = e.getCause().getClass().getSimpleName();
            } else {
                errorMessage = e.getMessage();
            }
            return new Message(false, errorMessage);
        }
    }

    class PagePreviewExtractor implements Extractor<Document, String> {

        @Override
        public Result handle(Page page, Document document) throws Throwable {
            // modify <a>, <link>, etc.
            for (Element element : document.select("[href]")) {
                String link = element.attr("href");
                if (StringHelper.isNotNull(link)) {
                    String target = StringHelper.toAbsoluteUrl(page.getTask(), link);
                    element.attr("href", target);
                    element.attr("c-link-target", target);
                    if (element.tagName().equals("a")) {
                        element.attr("href", "javascript:void(0);");
                        element.attr("target", "_self");
                        element.attr("title", target);
                    }
                }
                element.addClass("c-link");
            }
            // modify <script>, <iframe>, etc.
            for (Element element : document.select("[src]")) {
                String link = element.attr("src");
                if (StringHelper.isNotNull(link)) {
                    String target = StringHelper.toAbsoluteUrl(page.getTask(), link);
                    element.attr("src", target);
                    element.attr("c-link-target", target);
                }
                element.addClass("c-link");
            }
            // remove scripts
            if (needsScriptMasking(page.getTask().getHost())) {
                document.select("script").forEach(element -> element.remove());
            }
            return new Result<>(null, document.toString());
        }
    }

    public boolean needsScriptMasking(String url) {
        Set<String> blackList = new HashSet<String>() {{
            add("github.com");
            add("www.github.com");
            add("sina.cn");
        }};
        return blackList.contains(url);
    }
}
