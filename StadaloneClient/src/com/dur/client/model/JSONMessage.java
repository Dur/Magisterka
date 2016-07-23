package com.dur.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.json.generators.JSONGenerator;
import com.json.generators.JsonGeneratorFactory;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class JSONMessage {
	
	private String jsonMessage;
	
	private Map<Object, Object> messageContent;
	
	public JSONMessage(){
		messageContent = new HashMap<Object, Object>();
	}
	
	public JSONMessage(String jsonMessage) {
		this.messageContent = JSONMessage.parseJson(jsonMessage.trim());
		this.jsonMessage = jsonMessage;
	}

	public JSONMessage(Map<Object, Object> messageContent) {
		this.jsonMessage = JSONMessage.toJson(messageContent);
		this.messageContent = messageContent;
	}

	public static String toJson(Map<Object, Object> data){
		JSONGenerator generator = JsonGeneratorFactory.getInstance().newJsonGenerator();
		return generator.generateJson(data);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<Object, Object> parseJson(String message){
		JSONParser parser = JsonParserFactory.getInstance().newJsonParser();
		Map<Object, Object> jsonData = parser.parseJson(message.trim());
		return (Map<Object, Object>) ((List) jsonData.get("root")).get(0);
	}
	
	public Object get(String key){
		return messageContent.get(key);
	}
	
	public void addParam(String key, Object value){
		messageContent.put(key,  value);
	}

	public final String getJsonMessage() {
		jsonMessage = JSONMessage.toJson(messageContent);
		return jsonMessage;
	}

	public final void setJsonMessage(String jsonMessage) {
		this.jsonMessage = jsonMessage;
	}

	public final Map<Object, Object> getMessageContent() {
		return messageContent;
	}

	public final void setMessageContent(Map<Object, Object> messageContent) {
		this.messageContent = messageContent;
	}
}
