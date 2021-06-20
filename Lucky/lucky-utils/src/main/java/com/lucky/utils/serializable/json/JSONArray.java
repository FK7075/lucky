package com.lucky.utils.serializable.json;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/15 下午10:02
 */
public class JSONArray extends JsonElement implements Iterable<JsonElement>{

    private final JsonArray jsonArray;

    public JSONArray(int capacity){
        jsonArray = new JsonArray(capacity);
    }

    public JSONArray(){
        jsonArray = new JsonArray();
    }

    public JSONArray(JsonArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JsonArray getJsonArray() {
        return jsonArray;
    }

    public JSONArray(String jsonArrayStr){
        this.jsonArray = new JsonParser().parse(jsonArrayStr).getAsJsonArray();
    }

    public JSONArray(Reader jsonReader){
        this.jsonArray = new JsonParser().parse(jsonReader).getAsJsonArray();
    }

    public void add(Boolean bool) {
        this.jsonArray.add(bool);
    }

    public void add(Character character) {
        this.jsonArray.add(character);
    }

    public void add(Number number) {
        this.jsonArray.add(number);
    }

    public void add(String string) {
        this.jsonArray.add(string);
    }

    public void add(JsonElement element) {
        this.jsonArray.add(element);
    }

    public void addAll(JsonArray array) {
        this.jsonArray.addAll(array);
    }

    public void addAll(JSONArray array){
        addAll(array.getJsonArray());
    }

    public JsonElement set(int index, JsonElement element) {
        return this.jsonArray.set(index, element);
    }

    public boolean remove(JsonElement element) {
        return this.jsonArray.remove(element);
    }

    public JsonElement remove(int index) {
        return this.jsonArray.remove(index);
    }

    public boolean contains(JsonElement element) {
        return this.jsonArray.contains(element);
    }

    public int size() {
        return this.jsonArray.size();
    }

    public JsonElement get(int i) {
        return this.jsonArray.get(i);
    }

    public JsonArray getJsonArray(int i){
        return get(i).getAsJsonArray();
    }

    public JSONArray getJSONArray(int i){
        return new JSONArray(getJsonArray(i));
    }

    public JSONObject getJSONObject(int i){
        return new JSONObject(get(i).getAsJsonObject());
    }

    public String getString(int i){
        return get(i).getAsString();
    }

    public boolean getBoolean(int i){
        return get(i).getAsBoolean();
    }

    public int getInt(int i){
        return get(i).getAsInt();
    }

    public Double getDouble(int i){
        return get(i).getAsDouble();
    }

    public Number getNumber(int i){
        return get(i).getAsNumber();
    }

    public float getFloat(int i){
        return get(i).getAsFloat();
    }

    public long getLong(int i){
        return get(i).getAsLong();
    }

    public byte getByte(int i){
        return get(i).getAsByte();
    }

    public char getCharacter(int i) {
        return get(i).getAsCharacter();
    }

    public BigDecimal getBigDecimal(int i){
        return get(i).getAsBigDecimal();
    }

    public BigInteger getBigInteger(int i){
        return get(i).getAsBigInteger();
    }

    public short getShort(int i){
        return get(i).getAsShort();
    }


    @Override
    public boolean isJsonArray() {
        return jsonArray.isJsonArray();
    }

    @Override
    public boolean isJsonObject() {
        return jsonArray.isJsonObject();
    }

    @Override
    public boolean isJsonPrimitive() {
        return jsonArray.isJsonPrimitive();
    }

    @Override
    public boolean isJsonNull() {
        return jsonArray.isJsonNull();
    }

    @Override
    public JsonObject getAsJsonObject() {
        return jsonArray.getAsJsonObject();
    }

    @Override
    public JsonArray getAsJsonArray() {
        return jsonArray.getAsJsonArray();
    }

    @Override
    public JsonPrimitive getAsJsonPrimitive() {
        return jsonArray.getAsJsonPrimitive();
    }

    @Override
    public JsonNull getAsJsonNull() {
        return jsonArray.getAsJsonNull();
    }

    @Override
    public boolean getAsBoolean() {
        return jsonArray.getAsBoolean();
    }

    @Override
    public Number getAsNumber() {
        return jsonArray.getAsNumber();
    }

    @Override
    public String getAsString() {
        return jsonArray.getAsString();
    }

    @Override
    public double getAsDouble() {
        return jsonArray.getAsDouble();
    }

    @Override
    public float getAsFloat() {
        return jsonArray.getAsFloat();
    }

    @Override
    public long getAsLong() {
        return jsonArray.getAsLong();
    }

    @Override
    public int getAsInt() {
        return jsonArray.getAsInt();
    }

    @Override
    public byte getAsByte() {
        return jsonArray.getAsByte();
    }

    @Override
    public char getAsCharacter() {
        return jsonArray.getAsCharacter();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return jsonArray.getAsBigDecimal();
    }

    @Override
    public BigInteger getAsBigInteger() {
        return jsonArray.getAsBigInteger();
    }

    @Override
    public short getAsShort() {
        return jsonArray.getAsShort();
    }

    @Override
    public String toString() {
        return jsonArray.toString();
    }

    @Override
    public int hashCode() {
        return jsonArray.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return jsonArray.equals(obj);
    }

    @Override
    public JsonElement deepCopy() {
        return jsonArray.deepCopy();
    }

    @NotNull
    @Override
    public Iterator<JsonElement> iterator() {
        return jsonArray.iterator();
    }
}
