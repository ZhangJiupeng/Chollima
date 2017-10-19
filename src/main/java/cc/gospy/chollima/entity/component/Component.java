package cc.gospy.chollima.entity.component;

import com.google.gson.JsonParser;

public abstract class Component<T> {
    protected T core;
    protected String name;
    protected JsonParser parser;

    public String getName() {
        return name;
    }

    public Component(String name) {
        if (name == null)
            throw new IllegalArgumentException("component name cannot be null");

        this.name = name;
        this.parser = new JsonParser();
    }

    public abstract void loadCore(String configJson);

    public T getCore() {
        return core;
    }

    @Override
    public String toString() {
        return String.format("Component<%s>{name=\"%s\", core=%s}"
                , core.getClass().getSimpleName(), name, core);
    }

    public static Type getType(int value) {
        for (Type type : Type.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }

    public static Type getType(String componentName) {
        String lowerCaseComponentName = componentName.toLowerCase();
        for (Type type : Type.values()) {
            if (type.name().toLowerCase().equals(lowerCaseComponentName)) {
                return type;
            }
        }
        return null;
    }

    public enum Type {
        Scheduler(1), Fetcher(2), Processor(3), Pipeline(4);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
