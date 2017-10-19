package cc.gospy.chollima.entity.bean;

import com.google.common.base.Objects;

import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private int groupId;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                groupId == task.groupId &&
                Objects.equal(url, task.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, groupId, url);
    }
}
