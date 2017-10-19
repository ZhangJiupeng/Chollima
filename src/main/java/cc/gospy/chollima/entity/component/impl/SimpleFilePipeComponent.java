package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.pipeline.Pipeline;
import cc.gospy.ext.SimpleFilePipeline;
import com.google.gson.JsonObject;

public class SimpleFilePipeComponent extends Component<Pipeline> {
    public SimpleFilePipeComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        JsonObject json = parser.parse(configJson).getAsJsonObject();
        if (!json.has("basePath")) {
            throw new RuntimeException("no field 'basePath', please specify the base path of your SimpleFilePipeline");
        }
        this.core = SimpleFilePipeline.custom().setBasePath(json.get("basePath").getAsString()).build();
    }

    public static String getDefaultConfigJson() {
        return "{\"basePath\": \"/path/to/dir\"}";
    }

    public static String getTemplateName() {
        return "Simple File Pipeline";
    }
}
