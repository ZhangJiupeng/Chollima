package cc.gospy.chollima.entity.bean;

import java.io.Serializable;

public class Spider implements Serializable {
    private int id;
    private String name;
    private int groupId;

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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Spider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
