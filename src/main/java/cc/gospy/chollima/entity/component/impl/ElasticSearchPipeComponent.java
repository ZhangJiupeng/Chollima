package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.pipeline.Pipeline;

public class ElasticSearchPipeComponent extends Component<Pipeline> {
    public ElasticSearchPipeComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {

    }

    public static String getDefaultConfigJson() {
        return "{}";
    }
}
