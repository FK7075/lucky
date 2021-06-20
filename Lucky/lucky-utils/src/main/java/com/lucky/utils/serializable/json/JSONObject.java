package com.lucky.utils.serializable.json;

import com.google.gson.*;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/15 下午9:43
 */
public class JSONObject extends JsonElement {

    private final JsonObject jsonObject;

    public JSONObject(){
        this("{}");
    }

    public JSONObject(String jsonStr){
        jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
    }

    public JSONObject(Reader jsonReader){
        jsonObject = new JsonParser().parse(jsonReader).getAsJsonObject();
    }

    public JSONObject(JsonObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void put(String name,Object value){
        if(value instanceof JsonElement){
            add(name,(JsonElement)value);
        }else if(value instanceof String){
            addProperty(name,(String)value);
        }else if(value instanceof Number){
            addProperty(name,(Number)value);
        }else if(value instanceof Boolean){
            addProperty(name,(Boolean)value);
        }else if(value instanceof Character){
            addProperty(name,(Character)value);
        }else{
            add(name,new JSONObject(new Gson().toJson(value)));
        }
    }

    public void add(String property, JsonElement value){
        jsonObject.add(property, value);
    }

    public JsonElement remove(String property){
        return jsonObject.remove(property);
    }

    public void addProperty(String property, String value){
        jsonObject.addProperty(property, value);
    }

    public void addProperty(String property, Number value){
        jsonObject.addProperty(property, value);
    }

    public void addProperty(String property, Boolean value){
        jsonObject.addProperty(property, value);
    }

    public void addProperty(String property, Character value){
        jsonObject.addProperty(property, value);
    }

    public Set<Map.Entry<String, JsonElement>> entrySet(){
        return jsonObject.entrySet();
    }

    public Set<String> keySet(){
        return jsonObject.keySet();
    }

    public int size(){
        return jsonObject.size();
    }

    public boolean has(String memberName){
        return jsonObject.has(memberName);
    }

    public JsonObject getJsonObject(String memberName){
        return get(memberName).getAsJsonObject();
    }

    public JSONObject getJSONObject(String memberName){
        return new JSONObject(get(memberName).getAsJsonObject());
    }

    public JsonArray getJsonArray(String memberName){
        return get(memberName).getAsJsonArray();
    }

    public JSONArray getJSONArray(String memberName){
        return new JSONArray(get(memberName).getAsJsonArray());
    }


    public String getString(String memberName){
        return get(memberName).getAsString();
    }

    public boolean getBoolean(String memberName){
        return get(memberName).getAsBoolean();
    }

    public int getInt(String memberName){
        return get(memberName).getAsInt();
    }

    public Double getDouble(String memberName){
        return get(memberName).getAsDouble();
    }

    public Number getNumber(String memberName){
        return get(memberName).getAsNumber();
    }

    public float getFloat(String memberName){
        return get(memberName).getAsFloat();
    }

    public long getLong(String memberName){
        return get(memberName).getAsLong();
    }

    public byte getByte(String memberName){
        return get(memberName).getAsByte();
    }

    public char getCharacter(String memberName) {
        return get(memberName).getAsCharacter();
    }

    public BigDecimal getBigDecimal(String memberName){
        return get(memberName).getAsBigDecimal();
    }

    public BigInteger getBigInteger(String memberName){
        return get(memberName).getAsBigInteger();
    }

    public short getShort(String memberName){
        return get(memberName).getAsShort();
    }

    public JsonElement get(String memberName){
        return jsonObject.get(memberName);
    }

    public JsonPrimitive getAsJsonPrimitive(String memberName){
        return jsonObject.getAsJsonPrimitive(memberName);
    }

    public JsonArray getAsJsonArray(String memberName){
        return jsonObject.getAsJsonArray(memberName);
    }

    public JsonObject getAsJsonObject(String memberName){
        return jsonObject.getAsJsonObject(memberName);
    }


    @Override
    public boolean isJsonArray() {
        return jsonObject.isJsonArray();
    }

    @Override
    public boolean isJsonObject() {
        return jsonObject.isJsonObject();
    }

    @Override
    public boolean isJsonPrimitive() {
        return jsonObject.isJsonPrimitive();
    }

    @Override
    public boolean isJsonNull() {
        return jsonObject.isJsonNull();
    }

    @Override
    public JsonObject getAsJsonObject() {
        return jsonObject.getAsJsonObject();
    }

    @Override
    public JsonArray getAsJsonArray() {
        return jsonObject.getAsJsonArray();
    }

    @Override
    public JsonPrimitive getAsJsonPrimitive() {
        return jsonObject.getAsJsonPrimitive();
    }

    @Override
    public JsonNull getAsJsonNull() {
        return jsonObject.getAsJsonNull();
    }

    @Override
    public boolean getAsBoolean() {
        return jsonObject.getAsBoolean();
    }

    @Override
    public Number getAsNumber() {
        return jsonObject.getAsNumber();
    }

    @Override
    public String getAsString() {
        return jsonObject.getAsString();
    }

    @Override
    public double getAsDouble() {
        return jsonObject.getAsDouble();
    }

    @Override
    public float getAsFloat() {
        return jsonObject.getAsFloat();
    }

    @Override
    public long getAsLong() {
        return jsonObject.getAsLong();
    }

    @Override
    public int getAsInt() {
        return jsonObject.getAsInt();
    }

    @Override
    public byte getAsByte() {
        return jsonObject.getAsByte();
    }

    @Override
    public char getAsCharacter() {
        return jsonObject.getAsCharacter();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return jsonObject.getAsBigDecimal();
    }

    @Override
    public BigInteger getAsBigInteger() {
        return jsonObject.getAsBigInteger();
    }

    @Override
    public short getAsShort() {
        return jsonObject.getAsShort();
    }

    @Override
    public JsonElement deepCopy() {
        return new JSONObject(jsonObject.getAsString());
    }

    public boolean equals(Object o){
        return jsonObject.equals(o);
    }

    public int hashCode(){
        return jsonObject.hashCode();
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
