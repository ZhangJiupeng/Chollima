package cc.gospy.chollima.entity;

import com.google.gson.Gson;

public class Message {
    private static Gson gson = new Gson();

    private String status;
    private Object data;

    public Message(Object data) {
        this(true, data);
    }

    public Message(boolean success, Object data) {
        this(success ? "success" : "failure", data);
    }

    public Message(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public Message setStatus(String status) {
        this.status = status;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Message setData(Object data) {
        this.data = data;
        return this;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}
