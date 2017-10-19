package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.fetcher.Fetcher;
import cc.gospy.core.fetcher.impl.FileFetcher;

public class FileFetchComponent extends Component<Fetcher> {
    public FileFetchComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        this.core = FileFetcher.getDefault();
    }

    public static String getDefaultConfigJson() {
        return "{}";
    }

    public static String getTemplateName() {
        return "File Fetcher";
    }
}
