package cc.gospy.chollima.entity.bean;

import java.io.Serializable;

public class TaskGroup implements Serializable {
    private int id;
    private String name;
    private int status;
    private long createTime;
    private long lastModifyTime;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public String toString() {
        return "TaskGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", lastModifyTime=" + lastModifyTime +
                '}';
    }
}
