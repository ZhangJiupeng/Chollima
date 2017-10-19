package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.fetcher.Fetcher;
import cc.gospy.core.fetcher.UserAgent;
import cc.gospy.core.fetcher.impl.PhantomJSFetcher;
import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import org.openqa.selenium.Cookie;

import java.util.Iterator;

public class PhantomFetchComponent extends Component<Fetcher> {
    public PhantomFetchComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        JsonObject json = parser.parse(configJson).getAsJsonObject();
        String userAgent = json.has("userAgent") ? json.get("userAgent").getAsString() : UserAgent.Chrome_56_0_2924_87_Win_10_64_bit;
        int timeout = json.has("timeout") ? json.get("timeout").getAsInt() : 3000;
        String cookies = json.has("cookies") ? json.get("cookies").getAsString() : "";
        if (!json.has("basePath")) {
            throw new RuntimeException("no field 'basePath', please specify the path to phantomjs before fetch");
        }
        String basePath = json.get("basePath").getAsString();
        boolean loadImage = json.has("loadImage") ? json.get("loadImage").getAsBoolean() : true;

        PhantomJSFetcher.Builder builder = PhantomJSFetcher.custom()
                .setPhantomJsBinaryPath(basePath)
                .setUserAgent(userAgent)
                .setTimeout(timeout)
                .setLoadImages(loadImage);
        if (cookies.length() > 0) {
            Splitter.on(",").trimResults().split(cookies).forEach(s -> {
                Iterator<String> iterator = Splitter.on("=").limit(2).trimResults().split("=").iterator();
                builder.addCookie(new Cookie(iterator.next(), iterator.next()));
            });
        }
        this.core = builder.build();
    }

    public static String getDefaultConfigJson() {
        return "{" +
                "\"userAgent\":\"Gospy-HttpFetcher/3.10\"" +
                ",\"timeout\":3000" +
                ",\"cookies\":\"\"" +
                ",\"basePath\":\"/path/to/phantomjs\"" +
                ",\"loadImage\":true" +
                "}";
    }

    public static String getTemplateName() {
        return "PhantomJS Fetcher";
    }
}
