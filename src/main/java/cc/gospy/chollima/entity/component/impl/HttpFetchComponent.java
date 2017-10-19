package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.entity.Page;
import cc.gospy.core.fetcher.Fetcher;
import cc.gospy.core.fetcher.UserAgent;
import cc.gospy.core.fetcher.impl.HttpFetcher;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class HttpFetchComponent extends Component<Fetcher> {
    private static final Logger logger = LoggerFactory.getLogger(HttpFetchComponent.class);

    public HttpFetchComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        JsonObject json = parser.parse(configJson).getAsJsonObject();
        String userAgent = json.has("userAgent") ? json.get("userAgent").getAsString() : UserAgent.Chrome_56_0_2924_87_Win_10_64_bit;
        int timeout = json.has("timeout") ? json.get("timeout").getAsInt() : 3000;
        int maxConnCount = json.has("maxConnCount") ? json.get("maxConnCount").getAsInt() : 200;
        int maxConnPerRoute = json.has("maxConnPerRoute") ? json.get("maxConnPerRoute").getAsInt() : 20;
        int cleanPeriodInSeconds = json.has("cleanPeriodInSeconds") ? json.get("cleanPeriodInSeconds").getAsInt() : 30;
        int connExpireInSeconds = json.has("connExpireInSeconds") ? json.get("connExpireInSeconds").getAsInt() : 10;
        boolean autoKeepAlive = json.has("autoKeepAlive") ? json.get("autoKeepAlive").getAsBoolean() : true;
        boolean redirectsEnabled = json.has("redirectsEnabled") ? json.get("redirectsEnabled").getAsBoolean() : true;
        String cookies = json.has("cookies") ? json.get("cookies").getAsString() : "";
        String[] httpProxy = {};
        if (json.has("httpProxy")) {
            List<String> proxyList = new ArrayList<>();
            json.get("httpProxy").getAsJsonArray().forEach(jsonElement -> {
                proxyList.add(jsonElement.getAsString());
            });
            httpProxy = proxyList.toArray(new String[]{});
        }

        HttpFetcher.setTimeout(timeout);
        String[] finalHttpProxy = httpProxy;
        this.core = HttpFetcher.custom()
                .setUserAgent(userAgent)
                .setMaxConnCount(maxConnCount)
                .setMaxConnPerRoute(maxConnPerRoute)
                .setCleanPeriodSeconds(cleanPeriodInSeconds)
                .setConnExpireSeconds(connExpireInSeconds)
                .setAutoKeepAlive(autoKeepAlive)
                .before(request -> {
                    RequestConfig.Builder reqBuilder = RequestConfig.custom()
                            .setRedirectsEnabled(redirectsEnabled)
                            .setRelativeRedirectsAllowed(false)
                            .setCircularRedirectsAllowed(false)
                            .setConnectionRequestTimeout(timeout)
                            .setConnectTimeout(timeout)
                            .setSocketTimeout(timeout);
                    if (finalHttpProxy.length > 0) {
                        int index = RandomUtils.nextInt(0, finalHttpProxy.length);
                        logger.info("Use proxy [{}]", finalHttpProxy[index]);
                        reqBuilder.setProxy(new HttpHost(
                                finalHttpProxy[index])
                        );
                    }
                    request.setConfig(reqBuilder.build());
                    if (cookies.length() > 0) {
                        request.setHeader("Cookie", cookies);
                    }
                })
                .after(response -> {
                    Page page = new Page();
                    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
                    page.setStatusCode(response.getStatusLine().getStatusCode());
                    HttpEntity entity = response.getEntity();
                    String contentType;
                    if (entity.getContentType() != null && !(contentType = entity.getContentType().getValue()).equals("")) {
                        page.setContentType(contentType.indexOf(';') != -1 ? contentType.substring(0, contentType.indexOf(';')) : contentType);
                    }
                    entity.writeTo(responseBody);
                    page.setContent(responseBody.toByteArray());
                    Map<String, Object> responseHeader = new HashMap<>();
                    for (Header header : response.getAllHeaders()) {
                        responseHeader.put(header.getName(), header.getValue());
                    }
                    page.getExtra().put("responseHeader", responseHeader);
                    if (page.getContentType() == null) {
                        page.setContentType("text/plain"); // DEBUG
                    }
                    return page;
                })
                .build();
    }

    public static String getDefaultConfigJson() {
        return "{" +
                "\"userAgent\":\"Gospy-HttpFetcher/3.10\"" +
                ",\"timeout\":3000" +
                ",\"maxConnCount\":200" +
                ",\"maxConnPerRoute\":20" +
                ",\"cleanPeriodInSeconds\":30" +
                ",\"connExpireInSeconds\":10" +
                ",\"autoKeepAlive\":true" +
                ",\"redirectsEnabled\":true" +
                ",\"httpProxy\":[]" +
                ",\"cookies\":\"\"" +
                "}";
    }

    public static String getTemplateName() {
        return "Http Fetcher";
    }

}
