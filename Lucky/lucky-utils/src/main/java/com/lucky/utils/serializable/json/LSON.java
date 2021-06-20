package com.lucky.utils.serializable.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;


/**
 * pojo对象转json字符串
 * 
 * @author fk7075
 *
 */
public class LSON {

	private static final Logger log= LoggerFactory.getLogger(LSON.class);
	private GsonBuilder gsonBuilder;
	private Gson gson;
	public Gson getGson() {
		return gson;
	}

	public LSON() {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
	}


	/**
	 * 利用Google的GSON将对象转为格式化的Json字符串
	 * @param pojo 实体类对象
	 * @return JSON字符串
	 */
	public String toFormatJson(Object pojo){
		gsonBuilder.setPrettyPrinting();
		gson = gsonBuilder.create();
		return gson.toJson(pojo);
	}

	/**
	 * 利用Google的GSON将对象转为Json字符串
	 * @param pojo 实体类对象
	 * @return JSON字符串
	 */
	public String toJson(Object pojo){
		gson = gsonBuilder.create();
		return gson.toJson(pojo);
	}

	/**
	 * 用Google的GSON将Json格式的Reader转为Json字符串
	 * @param jsonReader
	 * @return
	 */
	public String toJson(Reader jsonReader){
		gson = gsonBuilder.create();
		return gson.toJson(jsonReader);
	}

	/**
	 * 用Google的GSON将Json格式的InputStream转为Json字符串
	 * @param jsonIn
	 * @return
	 */
	public String toJson(InputStream jsonIn){
		gson = gsonBuilder.create();
		return gson.toJson(jsonIn);
	}

	/**
	 * 传入一个Json字符串,返回一个指定类型的对象
	 * @param objectClass 返回对象的类型
	 * @param jsonStr Json字符串
	 * @return
	 */
	public <T> T fromJson(Class<T> objectClass, String jsonStr) {
		gson=gsonBuilder.create();
		return gson.fromJson(jsonStr,objectClass);
	}


	/**
	 * 传入一个Json字符串,返回一个指定类型的对象
	 * @param typeToken TypeToken类型转换对象
	 * @param jsonStr Json字符串
	 * @return
	 */
	public Object fromJson(TypeToken typeToken, String jsonStr){
		gson=gsonBuilder.create();
		return gson.fromJson(jsonStr,typeToken.getType());
	}

	/**
	 * 传入一个Json字符串,返回一个指定类型的对象
	 * @param type type
	 * @param jsonStr Json字符串
	 * @return
	 */
	public Object fromJson(Type type, String jsonStr){
		gson=gsonBuilder.create();
		return gson.fromJson(jsonStr,type);
	}

	public <T> T fromJson(Class<T> pojoClass,Reader reader){
		gson = gsonBuilder.create();
		return gson.fromJson(reader,pojoClass);
	}

	public Object fromJson(Type type,Reader reader){
		gson = gsonBuilder.create();
		return gson.fromJson(reader,type);
	}

	public Object fromJson(TypeToken typeToken,Reader reader){
		gson = gsonBuilder.create();
		return gson.fromJson(reader,typeToken.getType());
	}

	public static boolean validate(String jsonStr){
		JsonElement jsonElement;
		try {
			jsonElement = new JsonParser().parse(jsonStr);
		}catch (Exception e){
			return false;
		}
		if(jsonElement == null){
			return false;
		}
		return jsonElement.isJsonObject();
	}
}
