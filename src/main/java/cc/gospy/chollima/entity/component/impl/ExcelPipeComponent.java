package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.pipeline.Pipeline;
import cc.gospy.ext.ExcelPipeline;
import com.google.gson.JsonObject;

public class ExcelPipeComponent extends Component<Pipeline> {
    public ExcelPipeComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        JsonObject json = parser.parse(configJson).getAsJsonObject();
        if (!json.has("path")) {
            throw new RuntimeException("no field 'path', please specify a file path for ExcelPipeline");
        }
        this.core = ExcelPipeline.custom().setPath(json.get("path").getAsString()).build();
    }

    public static String getDefaultConfigJson() {
        return "{\"path\": \"/path/to/dir\"}";
    }

    public static String getTemplateName() {
        return "XLS Pipeline";
    }
}
