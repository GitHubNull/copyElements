package top.oxff.utils;

import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.oxff.CopyElements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JsonUtils {

    public static Set<String> findValuesByKey(String json, String name) {
        CopyElements.logger.logToOutput("findValuesByKey start....");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            CopyElements.logger.logToOutput("json: " + json);
            root = mapper.readTree(json);
        } catch (Exception e) {
            CopyElements.logger.logToError("error: " + e.getMessage());
            return null;
        }
        Set<String> values = new HashSet<>();
        findValues(root, name, values);
        CopyElements.logger.logToOutput("findValuesByKey end....");
        return values;
    }

    private static void findValues(JsonNode node, String name, Set<String> values) {
        CopyElements.logger.logToOutput("findValuesByKey star ..." );
        if (node == null) {
            return;
        }
        CopyElements.logger.logToOutput("findValuesByKey: " + node);
        if (node.isObject()) {
            Iterator<String> fields = node.fieldNames();
            while (fields.hasNext()) {
                String fieldName = fields.next();
                JsonNode fieldNode = node.get(fieldName);
                if (fieldName.equals(name) && (fieldNode.isTextual() || fieldNode.isNumber())) {
                    values.add(fieldNode.asText());
                } else {
                    findValues(fieldNode, name, values);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                findValues(arrayItem, name, values);
            }
        }
        CopyElements.logger.logToOutput("findValuesByKey end....");
    }

    public static Set<String> findValuesByKey(HttpResponse response, String name) throws IOException {
        CopyElements.logger.logToOutput("findValuesByKey start....");
        MimeType mimeType = response.inferredMimeType();
        if (mimeType == MimeType.JSON) {
            CopyElements.logger.logToOutput("findValuesByKey: JSON");
            return extractValues(response.bodyToString().strip().trim(), name);
        }else{
            CopyElements.logger.logToOutput("findValuesByKey: " + mimeType.description());
            return null;
        }
    }


    public static Set<String> extractValues(String jsonString, String keyName) {
        CopyElements.logger.logToOutput("extractValues start....");
        CopyElements.logger.logToOutput("jsonString: " + jsonString);
        Set<String> resultSet = new HashSet<>();
        if (jsonString == null || jsonString.isEmpty()) {
            CopyElements.logger.logToError("error: jsonString is empty");
            return resultSet; // 如果输入为空，直接返回空集合
        }

        Object json;
        try{
            json = JSON.parse(jsonString);
        }catch (Exception e){
            CopyElements.logger.logToError("error: " + e.getMessage());
            return resultSet;
        }
        if (json == null) {
            CopyElements.logger.logToError("error: json is null");
            return resultSet; // 如果解析结果为空，返回空集合
        }

        extractValuesRecursively(json, keyName, resultSet);
        return resultSet;
    }

    private static void extractValuesRecursively(Object json, String keyName, Set<String> resultSet) {
        CopyElements.logger.logToOutput("extractValuesRecursively start....");
        if (json == null) {
            CopyElements.logger.logToError("error: json is null");
            return; // 如果当前节点为空，直接返回
        }

        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (key.equals(keyName)) {
                    if (value instanceof Number) {
                        resultSet.add(String.valueOf(value));
                    } else if (value instanceof String) {
                        resultSet.add((String) value);
                    }
                }
                // 递归处理值
                extractValuesRecursively(value, keyName, resultSet);
            }
        } else if (json instanceof JSONArray jsonArray) {
            for (Object item : jsonArray) {
                extractValuesRecursively(item, keyName, resultSet);
            }
        }
    }

//    public static void main(String[] args) {
//        String jsonString = "{\"name\":\"John\", \"age\":30, \"details\":{\"name\":\"Doe\", \"height\":180}, \"hobbies\":[{\"name\":\"reading\"}, {\"name\":123}]}";
//        Set<String> values = extractValues(jsonString, "name");
//        System.out.println(values); // 输出: [Doe, John, reading]
//
//        // 测试空输入
//        Set<String> emptyValues = extractValues(null, "name");
//        System.out.println(emptyValues); // 输出: []
//    }

}
