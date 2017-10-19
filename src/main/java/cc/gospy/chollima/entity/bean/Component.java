package cc.gospy.chollima.entity.bean;

import java.io.Serializable;

public class Component implements Serializable {
    private int id;
    private String name;
    private int type;
    private String clazz;
    private String config;
    private int reference;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", clazz='" + clazz + '\'' +
                ", config='" + config + '\'' +
                ", reference=" + reference +
                '}';
    }
}
