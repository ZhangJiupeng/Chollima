package cc.gospy.chollima.entity.bean;

import java.io.Serializable;

public class Assembly implements Serializable {
    private int id;
    private int spiderId;
    private int componentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpiderId() {
        return spiderId;
    }

    public void setSpiderId(int spiderId) {
        this.spiderId = spiderId;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    @Override
    public String toString() {
        return "Assembly{" +
                "id=" + id +
                ", spiderId=" + spiderId +
                ", componentId=" + componentId +
                '}';
    }
}
