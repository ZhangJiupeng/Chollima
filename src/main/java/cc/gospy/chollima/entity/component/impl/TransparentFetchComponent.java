package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.fetcher.Fetcher;
import cc.gospy.core.fetcher.impl.TransparentFetcher;

public class TransparentFetchComponent extends Component<Fetcher> {
    public TransparentFetchComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        this.core = TransparentFetcher.custom().convertHttpTaskToPhantomJs().build();
    }

    public static String getDefaultConfigJson() {
        return "{}";
    }

    public static String getTemplateName() {
        return "Phantom Transfer Fetcher";
    }
}
