package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.pipeline.Pipeline;

public class MysqlPipeComponent extends Component<Pipeline> {
    public MysqlPipeComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {

    }

    public static String getDefaultConfigJson() {
        return "{}";
    }
}
