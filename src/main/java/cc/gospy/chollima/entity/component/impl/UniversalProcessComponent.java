package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.entity.Result;
import cc.gospy.core.processor.Processor;
import cc.gospy.core.processor.impl.UniversalProcessor;

public class UniversalProcessComponent extends Component<Processor> {
    public UniversalProcessComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        this.core = UniversalProcessor.getDefault();
    }

    public static String getDefaultConfigJson() {
        return "{}";
    }

    public static String getTemplateName() {
        return "Universal Processor";
    }
}
