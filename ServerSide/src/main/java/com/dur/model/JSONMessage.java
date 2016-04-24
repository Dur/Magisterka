package com.dur.model;

import java.util.List;
import java.util.Map;

import com.codesnippets4all.json.generators.JSONGenerator;
import com.codesnippets4all.json.generators.JsonGeneratorFactory;
import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;

public class JSONMessage {
	
	private String jsonMessage;
	
	private Map<Object, Object> messageContent;
	
	public JSONMessage(String jsonMessage) {
		this.messageContent = JSONMessage.parseJson(jsonMessage);
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
	
	public static Map<Object, Object> parseJson(String message){
		JSONParser parser = JsonParserFactory.getInstance().newJsonParser();
		Map<Object, Object> jsonData = parser.parseJson(message);
		return (Map<Object, Object>) ((List) jsonData.get("root")).get(0);
	}
	
	public Object get(String key){
		return messageContent.get(key);
	}
	
	public void addParam(String key, Object value){
		messageContent.put(key,  value);
		jsonMessage = JSONMessage.toJson(messageContent);
	}

	public final String getJsonMessage() {
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
