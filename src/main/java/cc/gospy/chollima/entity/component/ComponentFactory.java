package cc.gospy.chollima.entity.component;

import cc.gospy.core.fetcher.Fetcher;
import cc.gospy.core.pipeline.Pipeline;
import cc.gospy.core.processor.Processor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static cc.gospy.chollima.util.Constant.COMPONENTS_JSON_PATH;

public class ComponentFactory {
    private static final Logger logger = LoggerFactory.getLogger(ComponentFactory.class);

    public static JsonElement componentsJson;

    public static Map<Class<? extends Component<Fetcher>>, String> fetcherTemplates = new HashMap<>();
    public static Map<Class<? extends Component<Processor>>, String> processorTemplates = new HashMap<>();
    public static Map<Class<? extends Component<Pipeline>>, String> pipelineTemplates = new HashMap<>();
    public static Map<String, String> formJson = new HashMap<>();

    static {
        initCoreComponents();
    }

    public static void register(String componentClass) throws ClassNotFoundException {
        Class clazz = Class.forName(componentClass);
        if (clazz.getSuperclass() != Component.class) {
            logger.error("Component {} must extends cc.gospy.chollima.entity.component.Component.", componentClass);
            System.exit(-1);
            return;
        }
        ParameterizedType type;
        try {
            type = (ParameterizedType) clazz.getGenericSuperclass();
        } catch (ClassCastException e) {
            logger.error("Component {} must specify its core class type. ({})", componentClass, "class MyComponent extends Component<? extends CoreType>");
            System.exit(-1);
            return;
        }
        Type coreType = type.getActualTypeArguments()[0];
        String configJson;
        try {
            configJson = String.valueOf(clazz.getMethod("getDefaultConfigJson").invoke(null));
        } catch (NoSuchMethodException e) {
            configJson = "{}";
            logger.warn("No default configuration found in {}, using '{}' instead, please add 'public static String getDefaultConfigJson()'" +
                    " to your component if you need specified configuration.", componentClass, "{}");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }
        if (coreType == Fetcher.class) {
            if (fetcherTemplates.containsKey(clazz)) {
                logger.warn("Component {} has already registered, covering origin...", clazz.getName());
            }
            fetcherTemplates.put(clazz, configJson);
        } else if (coreType == Processor.class) {
            if (processorTemplates.containsKey(clazz)) {
                logger.warn("Component {} has already registered, covering origin...", clazz.getName());
            }
            processorTemplates.put(clazz, configJson);
        } else if (coreType == Pipeline.class) {
            if (pipelineTemplates.containsKey(clazz)) {
                logger.warn("Component {} has already registered, covering origin...", clazz.getName());
            }
            pipelineTemplates.put(clazz, configJson);
        } else {
            throw new IllegalArgumentException("unable to handle core type: " + coreType.getTypeName());
        }
        logger.info("Component {} registered.", componentClass);
    }

    public static Collection<Class<? extends Component<Fetcher>>> getFetcherTemplates() {
        return fetcherTemplates.keySet();
    }

    public static Map<String, Class<? extends Component<Fetcher>>> getFetcherTemplatesNameAndClass() {
        Map<String, Class<? extends Component<Fetcher>>> map = new HashMap<>();
        getFetcherTemplates().forEach(componentClazz -> map.put(getDisplayName(componentClazz), componentClazz));
        return map;
    }

    public static Collection<Class<? extends Component<Processor>>> getProcessorTemplates() {
        return processorTemplates.keySet();
    }

    public static Map<String, Class<? extends Component<Processor>>> getProcessorTemplatesNameAndClass() {
        Map<String, Class<? extends Component<Processor>>> map = new HashMap<>();
        getProcessorTemplates().forEach(componentClazz -> map.put(getDisplayName(componentClazz), componentClazz));
        return map;
    }

    public static Collection<Class<? extends Component<Pipeline>>> getPipelineTemplates() {
        return pipelineTemplates.keySet();
    }

    public static Map<String, Class<? extends Component<Pipeline>>> getPipelineTemplatesNameAndClass() {
        Map<String, Class<? extends Component<Pipeline>>> map = new HashMap<>();
        getPipelineTemplates().forEach(componentClazz -> map.put(getDisplayName(componentClazz), componentClazz));
        return map;
    }

    public static Component<Fetcher> getFetchComponent(String name, String fetchComponentClazz) throws Exception {
        return getFetchComponent(name, fetchComponentClazz, getDefaultConfigJson(fetchComponentClazz));
    }

    public static Component<Fetcher> getFetchComponent(String name, String fetchComponentClazz, String configJson) throws Exception {
        if (!fetcherTemplates.containsKey(Class.forName(fetchComponentClazz))) {
            throw new IllegalArgumentException("fetcher " + fetchComponentClazz + " has not registered");
        }
        Class clazz = Class.forName(fetchComponentClazz);
        Component<Fetcher> component = (Component<Fetcher>) clazz.getDeclaredConstructor(String.class).newInstance(name);
        component.loadCore(configJson);
        return component;
    }

    public static Component<Processor> getProcessComponent(String name, String processComponentClazz) throws Exception {
        return getProcessComponent(name, processComponentClazz, getDefaultConfigJson(processComponentClazz));
    }

    public static Component<Processor> getProcessComponent(String name, String processComponentClazz, String configJson) throws Exception {
        if (!processorTemplates.containsKey(Class.forName(processComponentClazz))) {
            throw new IllegalArgumentException("processor " + processComponentClazz + " has not registered");
        }
        Class clazz = Class.forName(processComponentClazz);
        Component<Processor> component = (Component<Processor>) clazz.getDeclaredConstructor(String.class).newInstance(name);
        component.loadCore(configJson);
        return component;
    }

    public static Component<Pipeline> getPipeComponent(String name, String pipelineComponentClazz) throws Exception {
        return getPipeComponent(name, pipelineComponentClazz, getDefaultConfigJson(pipelineComponentClazz));
    }

    public static Component<Pipeline> getPipeComponent(String name, String pipelineComponentClazz, String configJson) throws Exception {
        if (!pipelineTemplates.containsKey(Class.forName(pipelineComponentClazz))) {
            throw new IllegalArgumentException("pipeline " + pipelineComponentClazz + " has not registered");
        }
        Class clazz = Class.forName(pipelineComponentClazz);
        Component<Pipeline> component = (Component<Pipeline>) clazz.getDeclaredConstructor(String.class).newInstance(name);
        component.loadCore(configJson);
        return component;
    }

    public static String getDefaultConfigJson(String componentClazz) {
        try {
            Class clazz = Class.forName(componentClazz);
            if (fetcherTemplates.containsKey(clazz)) {
                return fetcherTemplates.get(clazz);
            } else if (processorTemplates.containsKey(clazz)) {
                return processorTemplates.get(clazz);
            } else if (pipelineTemplates.containsKey(clazz)) {
                return pipelineTemplates.get(clazz);
            } else {
                throw new IllegalArgumentException("component " + componentClazz + " has not registered");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDefaultConfigJsonFromFetcherTemplates(String componentClazz) throws ClassNotFoundException {
        String result = fetcherTemplates.get(Class.forName(componentClazz));
        if (result != null) {
            return result;
        } else {
            throw new IllegalArgumentException("fetcher " + componentClazz + " has not registered");
        }
    }

    public static String getDefaultConfigJsonFromProcessorTemplates(String componentClazz) throws ClassNotFoundException {
        String result = processorTemplates.get(Class.forName(componentClazz));
        if (result != null) {
            return result;
        } else {
            throw new IllegalArgumentException("processor " + componentClazz + " has not registered");
        }
    }

    public static String getDefaultConfigJsonFromPipelineTemplates(String componentClazz) throws ClassNotFoundException {
        String result = pipelineTemplates.get(Class.forName(componentClazz));
        if (result != null) {
            return result;
        } else {
            throw new IllegalArgumentException("pipeline " + componentClazz + " has not registered");
        }
    }

    public static String getDisplayName(Class componentClass) {
        try {
            return String.valueOf(componentClass.getMethod("getTemplateName").invoke(componentClass));
        } catch (Exception e) {
            return componentClass.getSimpleName();
        }
    }

    public static String getFormJson(String componentClazz) {
        return formJson.get(componentClazz);
    }

    public static void initCoreComponents() {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(COMPONENTS_JSON_PATH);
        if (inputStream == null) {
            throw new RuntimeException("configuration file \"" + COMPONENTS_JSON_PATH + "\" not found");
        }
        logger.info("Registering build-in components...");
        try {
            JsonParser parser = new JsonParser();
            componentsJson = parser.parse(new InputStreamReader(inputStream));
            componentsJson.getAsJsonArray().forEach(componentJsonElement -> {
                JsonObject componentJson = componentJsonElement.getAsJsonObject();
                String componentClassName = componentJson.get("name").getAsString();
                formJson.put(componentClassName, componentJson.get("form").getAsJsonObject().toString());
                try {
                    ComponentFactory.register(componentClassName);
                } catch (ClassNotFoundException e) {
                    logger.error("Build-in component {} not found.", e.getMessage());
                    e.printStackTrace();
                    System.exit(-1);
                }
            });
        } catch (NullPointerException e) {
            logger.error("Lack of parameter, component must has both [name] and [form] field.");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            logger.error("Configuration file [{}] parse failure.", COMPONENTS_JSON_PATH);
            e.printStackTrace();
            System.exit(-1);
        }
        logger.info("All build-in components are successfully registered.");
    }
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.HttpFetchComponent");
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.PhantomFetchComponent");
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.FileFetchComponent");
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.UniversalProcessComponent");
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.XPathProcessComponent");
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.HierarchicalFilePipeComponent");
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.SimpleFilePipeComponent");

//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.ExcelPipeComponent");
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.MysqlPipeComponent");
//        ComponentFactory.register("cc.gospy.chollima.entity.component.impl.ElasticSearchPipeComponent");
}
