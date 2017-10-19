package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.pipeline.Pipeline;
import cc.gospy.ext.HierarchicalFilePipeline;
import com.google.gson.JsonObject;

public class HierarchicalFilePipeComponent extends Component<Pipeline> {
    public HierarchicalFilePipeComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        JsonObject json = parser.parse(configJson).getAsJsonObject();
        if (!json.has("basePath")) {
            throw new RuntimeException("no field 'basePath', please specify the base path of your HierarchicalFilePipeline");
        }
        this.core = HierarchicalFilePipeline.custom().setBasePath(json.get("basePath").getAsString()).build();
    }

    public static String getDefaultConfigJson() {
        return "{\"basePath\": \"/path/to/dir\"}";
    }

    public static String getTemplateName() {
        return "Hierarchical File Pipeline";
    }
}
