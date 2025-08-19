package top.oxff.utils;

import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.oxff.CopyElements;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class MessageFormatter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 检查 Content-Type 是否为已知的文本类型
     */
    public static boolean isTextContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }
        
        String lowerContentType = contentType.toLowerCase().trim();
        
        // 移除参数部分 (如: application/json; charset=utf-8 -> application/json)
        int semicolonIndex = lowerContentType.indexOf(';');
        if (semicolonIndex != -1) {
            lowerContentType = lowerContentType.substring(0, semicolonIndex).trim();
        }
        
        return lowerContentType.equals("application/json") ||
               lowerContentType.equals("application/xml") ||
               lowerContentType.equals("application/javascript") ||
               lowerContentType.equals("application/x-javascript") ||
               lowerContentType.equals("application/x-www-form-urlencoded") ||
               lowerContentType.startsWith("text/");
    }

    /**
     * 检查 Content-Type 是否为已知的二进制类型
     */
    public static boolean isBinaryContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }
        
        String lowerContentType = contentType.toLowerCase().trim();
        
        // 移除参数部分
        int semicolonIndex = lowerContentType.indexOf(';');
        if (semicolonIndex != -1) {
            lowerContentType = lowerContentType.substring(0, semicolonIndex).trim();
        }
        
        return lowerContentType.startsWith("image/") ||
               lowerContentType.startsWith("video/") ||
               lowerContentType.startsWith("audio/") ||
               lowerContentType.equals("application/octet-stream") ||
               lowerContentType.equals("application/pdf") ||
               lowerContentType.equals("application/zip") ||
               lowerContentType.startsWith("application/vnd.");
    }

    public static boolean isBinaryContent(byte[] content) {
        if (content == null || content.length == 0) {
            return false;
        }
        
        // 检测常见的二进制文件头
        if (content.length >= 4) {
            // PNG header
            if (content[0] == (byte)0x89 && content[1] == 0x50 && content[2] == 0x4E && content[3] == 0x47) {
                return true;
            }
            // JPEG header
            if (content[0] == (byte)0xFF && content[1] == (byte)0xD8) {
                return true;
            }
            // ZIP/JAR header
            if (content[0] == 0x50 && content[1] == 0x4B) {
                return true;
            }
            // PDF header
            if (content[0] == 0x25 && content[1] == 0x50 && content[2] == 0x44 && content[3] == 0x46) {
                return true;
            }
        }
        
        try {
            // 尝试将内容解析为 UTF-8 字符串
            String text = new String(content, StandardCharsets.UTF_8);
            
            // 检查是否包含替换字符，这表明存在无效的 UTF-8 字节序列
            if (text.contains("\uFFFD")) {
                return true;
            }
            
            // 统计控制字符的数量（排除常见的制表符、换行符、回车符）
            long controlChars = text.chars()
                .filter(c -> c < 0x20 && c != 0x09 && c != 0x0A && c != 0x0D)
                .count();
            
            // 如果控制字符过多，可能是二进制数据
            // 设置阈值：控制字符超过10个且占比超过5%
            return controlChars > 10 && controlChars > text.length() * 0.05;
            
        } catch (Exception e) {
            // 如果转换过程中出现异常，可能是二进制数据
            CopyElements.logger.logToError("Error decoding content as UTF-8: " + e.getMessage());
            return true;
        }
    }

    public static String formatRequestMessage(HttpRequest request) {
        if (request == null) {
            return "";
        }

        // 使用 toString() 方法，Montoya API 会正确处理字符编码
        String requestString = request.toString();
        
        // 优先检查 Content-Type
        String contentType = request.hasHeader("Content-Type") ? request.header("Content-Type").value() : null;
        if (contentType != null) {
            if (isBinaryContentType(contentType)) {
                CopyElements.logger.logToOutput("Request skipped due to binary Content-Type: " + contentType);
                return null;
            }
            if (isTextContentType(contentType)) {
                CopyElements.logger.logToOutput("Request accepted due to text Content-Type: " + contentType);
                // 直接处理，跳过二进制检测
            } else {
                // Content-Type 不明确，进行二进制检测
                byte[] requestBytesCheck = requestString.getBytes(StandardCharsets.UTF_8);
                if (isBinaryContent(requestBytesCheck)) {
                    return null;
                }
            }
        } else {
            // 没有 Content-Type，进行二进制检测
            byte[] requestBytesCheck = requestString.getBytes(StandardCharsets.UTF_8);
            if (isBinaryContent(requestBytesCheck)) {
                return null;
            }
        }
        
        String body = request.bodyToString();
        if (body != null && !body.isEmpty()) {
            // 简单检测JSON和XML格式并格式化
            String formattedBody = null;
            String trimmedBody = body.trim();
            if (trimmedBody.startsWith("{") || trimmedBody.startsWith("[")) {
                // 可能是JSON
                formattedBody = formatJson(body);
            } else if (trimmedBody.startsWith("<")) {
                // 可能是XML
                formattedBody = formatXml(body);
            }
            
            if (formattedBody != null && !formattedBody.equals(body)) {
                int bodyStartIndex = requestString.indexOf("\r\n\r\n");
                if (bodyStartIndex != -1) {
                    String headers = requestString.substring(0, bodyStartIndex + 4);
                    return headers + formattedBody;
                }
            }
        }
        
        return requestString;
    }

    public static String formatResponseMessage(HttpResponse response) {
        if (response == null) {
            return "";
        }

        // 使用 toString() 方法，Montoya API 会正确处理字符编码
        String responseString = response.toString();
        
        // 优先检查 Content-Type
        String contentType = response.hasHeader("Content-Type") ? response.header("Content-Type").value() : null;
        if (contentType != null) {
            if (isBinaryContentType(contentType)) {
                CopyElements.logger.logToOutput("Response skipped due to binary Content-Type: " + contentType);
                return null;
            }
            if (isTextContentType(contentType)) {
                CopyElements.logger.logToOutput("Response accepted due to text Content-Type: " + contentType);
                // 直接处理，跳过二进制检测
            } else {
                // Content-Type 不明确，进行二进制检测
                byte[] responseBytesCheck = responseString.getBytes(StandardCharsets.UTF_8);
                if (isBinaryContent(responseBytesCheck)) {
                    return null;
                }
            }
        } else {
            // 没有 Content-Type，进行二进制检测
            byte[] responseBytesCheck = responseString.getBytes(StandardCharsets.UTF_8);
            if (isBinaryContent(responseBytesCheck)) {
                return null;
            }
        }
        
        String body = response.bodyToString();
        if (body != null && !body.isEmpty()) {
            MimeType mimeType = response.inferredMimeType();
            String formattedBody = formatBody(body, mimeType);
            if (formattedBody != null && !formattedBody.equals(body)) {
                int bodyStartIndex = responseString.indexOf("\r\n\r\n");
                if (bodyStartIndex != -1) {
                    String headers = responseString.substring(0, bodyStartIndex + 4);
                    return headers + formattedBody;
                }
            }
        }
        
        return responseString;
    }

    private static String formatBody(String body, MimeType mimeType) {
        if (body == null || body.isEmpty()) {
            return body;
        }

        if (mimeType == MimeType.JSON) {
            return formatJson(body);
        } else if (mimeType == MimeType.XML) {
            return formatXml(body);
        }
        
        return body;
    }

    public static String formatJson(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            CopyElements.logger.logToError("Failed to format JSON: " + e.getMessage());
            return json;
        }
    }

    public static String formatXml(String xml) {
        if (xml == null || xml.isEmpty()) {
            return xml;
        }
        
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);
            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            CopyElements.logger.logToError("Failed to format XML: " + e.getMessage());
            return xml;
        }
    }
}