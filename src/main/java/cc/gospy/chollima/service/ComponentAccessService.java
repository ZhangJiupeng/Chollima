package cc.gospy.chollima.service;

import cc.gospy.chollima.entity.Message;
import cc.gospy.chollima.entity.component.ComponentFactory;
import com.google.gson.*;
import org.springframework.stereotype.Service;

@Service
public class ComponentAccessService {
    private Gson gson;
    private JsonParser parser;

    public ComponentAccessService() {
        gson = new GsonBuilder().registerTypeAdapter(Class.class, (JsonSerializer<Class>)
                (aClass, type, jsonSerializationContext) -> new JsonPrimitive(aClass.getName())).create();
        parser = new JsonParser();
    }

    public String getFetcherTemplatesJson() {
        return gson.toJson(new Message(ComponentFactory.getFetcherTemplatesNameAndClass()));
    }

    public String getProcessorTemplatesJson() {
        return gson.toJson(new Message(ComponentFactory.getProcessorTemplatesNameAndClass()));
    }

    public String getPipelineTemplatesJson() {
        return gson.toJson(new Message(ComponentFactory.getPipelineTemplatesNameAndClass()));
    }

    public String getDefaultConfigJsonFromFetcherTemplates(String componentClass) {
        try {
            return new Message(parser.parse(ComponentFactory.getDefaultConfigJsonFromFetcherTemplates(componentClass)).getAsJsonObject()).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    public String getDefaultConfigJsonFromProcessorTemplates(String componentClass) {
        try {
            return new Message(parser.parse(ComponentFactory.getDefaultConfigJsonFromProcessorTemplates(componentClass)).getAsJsonObject()).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    public String getDefaultConfigJsonFromPipelineTemplates(String componentClass) {
        try {
            return new Message(parser.parse(ComponentFactory.getDefaultConfigJsonFromPipelineTemplates(componentClass)).getAsJsonObject()).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    public String getComponentForm(String componentClass) {
        String formJson = ComponentFactory.getFormJson(componentClass);
        if (formJson != null) {
            return new Message(parser.parse(formJson)).toJson();
        } else {
            return new Message(false, "form not found").toJson();
        }
    }
}
