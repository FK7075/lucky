package com.lucky.web.httpclient.callcontroller;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/5 下午6:09
 */
public class JSONObject {

    private String jsonObject;

    public JSONObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JSONObject{");
        sb.append("jsonObject='").append(jsonObject).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
