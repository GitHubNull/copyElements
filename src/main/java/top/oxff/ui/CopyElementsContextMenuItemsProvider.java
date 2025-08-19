package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import top.oxff.model.ValuesType;
import top.oxff.utils.MessageFormatter;
import top.oxff.utils.ClipboardUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CopyElementsContextMenuItemsProvider implements ContextMenuItemsProvider {
    private final MontoyaApi api;

    public CopyElementsContextMenuItemsProvider(MontoyaApi api) {
        this.api = api;
    }

    /**
     * 尝试获取当前活动的请求/响应项（用于处理文本框焦点情况）
     */
    private HttpRequestResponse getCurrentRequestResponse(ContextMenuEvent event) {
        try {
            // 尝试从消息编辑器获取当前项
            if (event.messageEditorRequestResponse().isPresent()) {
                return event.messageEditorRequestResponse().get().requestResponse();
            }
            
            // TODO: 可以在这里添加其他获取当前项的方法
            // 例如从其他 Burp Suite API 获取当前查看的项目
            
        } catch (Exception e) {
            api.logging().logToError("Error getting current request/response: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<HttpRequestResponse> requestResponseList = event.selectedRequestResponses();
        
        // 如果没有选中项，尝试获取当前活动的项（处理文本框焦点情况）
        if (requestResponseList.isEmpty()) {
            HttpRequestResponse currentItem = getCurrentRequestResponse(event);
            if (currentItem != null) {
                requestResponseList = java.util.Arrays.asList(currentItem);
                api.logging().logToOutput("Using current request/response item for context menu");
            } else {
                return null;
            }
        }
        
        List<Component> menuItemList = new ArrayList<>();
        
        // 原有功能 - 仅在 PROXY 和 LOGGER 下可用
        if (event.isFromTool(ToolType.PROXY, ToolType.LOGGER)) {
            JMenuItem copyHostMenuItem = new JMenuItem("copyHosts");
            JMenuItem copyHeaderKeysMenuItem = new JMenuItem("copyHeaderKeys");
            JMenuItem copyPathWithoutQueryMenuItem = new JMenuItem("copyPathWithoutQuery");

            JMenuItem copyAllRequestParamValuesSet = new JMenuItem("copyAllRequestParamValuesSet");
            copyAllRequestParamValuesSet.setToolTipText("copy all request param values set");

            JMenuItem copyAllResponseParamValuesSet = new JMenuItem("copyAllResponseParamValuesSet");
            copyAllResponseParamValuesSet.setToolTipText("copy all response param values set");

            initCopyHostMenuItemActionListener(copyHostMenuItem, requestResponseList);
            initCopyHeaderKeysMenuItemActionListener(copyHeaderKeysMenuItem, requestResponseList);
            initCopyPathWithoutQueryMenuItemActionListener(copyPathWithoutQueryMenuItem, requestResponseList);
            initCopyAllRequestParamValuesSetMenuItemActionListener(copyAllRequestParamValuesSet, requestResponseList);
            initCopyAllResponseParamValuesSetMenuItemActionListener(copyAllResponseParamValuesSet, requestResponseList);

            menuItemList.add(copyHostMenuItem);
            menuItemList.add(copyHeaderKeysMenuItem);
            menuItemList.add(copyPathWithoutQueryMenuItem);
            menuItemList.add(copyAllRequestParamValuesSet);
            menuItemList.add(copyAllResponseParamValuesSet);
        }
        
        // 新增功能 - 在 PROXY, LOGGER, REPEATER, INTRUDER 下都可用
        if (event.isFromTool(ToolType.PROXY, ToolType.LOGGER, ToolType.REPEATER, ToolType.INTRUDER)) {
            JMenuItem copyRequestMenuItem = new JMenuItem("copyRequest");
            copyRequestMenuItem.setToolTipText("Copy formatted request message");
            
            JMenuItem copyResponseMenuItem = new JMenuItem("copyResponse");
            copyResponseMenuItem.setToolTipText("Copy formatted response message");
            
            initCopyRequestMenuItemActionListener(copyRequestMenuItem, requestResponseList);
            initCopyResponseMenuItemActionListener(copyResponseMenuItem, requestResponseList);
            
            // 如果已有其他菜单项，添加分隔符
            if (!menuItemList.isEmpty()) {
                menuItemList.add(new JSeparator());
            }
            
            menuItemList.add(copyRequestMenuItem);
            menuItemList.add(copyResponseMenuItem);
        }
        
        return menuItemList.isEmpty() ? null : menuItemList;
    }

    private void initCopyAllResponseParamValuesSetMenuItemActionListener(JMenuItem copyAllResponseParamValuesSet, List<HttpRequestResponse> requestResponseList) {
        copyAllResponseParamValuesSet.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            ParamNameConfigDialog paramNameConfigDialog = new ParamNameConfigDialog(requestResponseList, ValuesType.RESPONSE);
            paramNameConfigDialog.setVisible(true);
        }));
    }

    private void initCopyAllRequestParamValuesSetMenuItemActionListener(JMenuItem copyParamValuesSetMenuItem, List<HttpRequestResponse> requestResponseList) {
        copyParamValuesSetMenuItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            ParamNameConfigDialog paramNameConfigDialog = new ParamNameConfigDialog(requestResponseList, ValuesType.REQUEST);
            paramNameConfigDialog.setVisible(true);
        }));
    }

    private void initCopyPathWithoutQueryMenuItemActionListener(JMenuItem copyPathWithoutQueryMenuItem, List<HttpRequestResponse> requestResponseList) {
        copyPathWithoutQueryMenuItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            HashSet<String> pathSet = new HashSet<>();
            for (HttpRequestResponse requestResponse : requestResponseList) {
                pathSet.add(requestResponse.request().pathWithoutQuery());
            }

            // set cover to list and sort
            List<String> pathList = pathSet.stream().sorted().toList();
            StringBuilder sb = new StringBuilder();
            for (String path : pathList){
                sb.append(path).append("\n");
            }
            String content = sb.toString();
            
            if (ClipboardUtils.copyToClipboard(content)) {
                api.logging().logToOutput("Paths copied successfully.");
            } else {
                api.logging().logToError("Failed to copy paths to clipboard.");
            }
        }));
    }

    private void initCopyHeaderKeysMenuItemActionListener(JMenuItem copyHeaderKeysMenuItem, List<HttpRequestResponse> requestResponseList) {
        copyHeaderKeysMenuItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            HashSet<String> headerKeySet = new HashSet<>();
            for (HttpRequestResponse requestResponse : requestResponseList) {
                List<HttpHeader> headers = requestResponse.request().headers();
                for (HttpHeader header : headers) {
                    headerKeySet.add(header.name());
                }
            }

            // set cover to list and sort
            List<String> headerKeyList = headerKeySet.stream().sorted().toList();
            StringBuilder sb = new StringBuilder();
            for (String headerKey : headerKeyList) {
                sb.append(headerKey).append("\n");
            }
            String content = sb.toString();
            // copy header keys to clipboard
            
            if (ClipboardUtils.copyToClipboard(content)) {
                api.logging().logToOutput("Header keys copied successfully.");
            } else {
                api.logging().logToError("Failed to copy header keys to clipboard.");
            }
        }));
    }

    private void initCopyHostMenuItemActionListener(JMenuItem copyHostMenuItem, List<HttpRequestResponse> requestResponseList) {
        copyHostMenuItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            HashSet<String> hostSet = new HashSet<>();
            for (HttpRequestResponse requestResponse : requestResponseList) {
                hostSet.add(requestResponse.request().httpService().host());
            }

            // set cover to list and sort
            List<String> hostList = hostSet.stream().sorted().toList();
            StringBuilder sb = new StringBuilder();
            for (String host : hostList) {
                sb.append(host).append("\n");
            }
            String content = sb.toString();

            // copy host to clipboard
            
            if (ClipboardUtils.copyToClipboard(content)) {
                api.logging().logToOutput("Hosts copied successfully.");
            } else {
                api.logging().logToError("Failed to copy hosts to clipboard.");
            }
        }));
    }

    private void initCopyRequestMenuItemActionListener(JMenuItem copyRequestMenuItem, List<HttpRequestResponse> requestResponseList) {
        copyRequestMenuItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();
            int successCount = 0;
            int failCount = 0;
            
            for (int i = 0; i < requestResponseList.size(); i++) {
                HttpRequestResponse requestResponse = requestResponseList.get(i);
                String formattedRequest = MessageFormatter.formatRequestMessage(requestResponse.request());
                
                if (formattedRequest == null) {
                    failCount++;
                    api.logging().logToError("Request " + (i + 1) + " contains binary data and cannot be copied.");
                    continue;
                }
                
                if (successCount > 0) {
                    sb.append("\n");
                    for (int j = 0; j < 50; j++) {
                        sb.append("=");
                    }
                    sb.append("\n");
                }
                sb.append(formattedRequest);
                successCount++;
            }
            
            if (successCount == 0) {
                JOptionPane.showMessageDialog(null, "所有请求都包含二进制数据，无法复制！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String content = sb.toString();
            
            if (ClipboardUtils.copyToClipboard(content)) {
                String message = successCount + " request(s) copied successfully.";
                if (failCount > 0) {
                    message += " " + failCount + " request(s) skipped due to binary content.";
                }
                api.logging().logToOutput(message);
                JOptionPane.showMessageDialog(null, "请求报文复制成功！\n成功: " + successCount + " 个，跳过: " + failCount + " 个");
            } else {
                JOptionPane.showMessageDialog(null, "请求报文复制失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }));
    }

    private void initCopyResponseMenuItemActionListener(JMenuItem copyResponseMenuItem, List<HttpRequestResponse> requestResponseList) {
        copyResponseMenuItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();
            int successCount = 0;
            int failCount = 0;
            
            for (int i = 0; i < requestResponseList.size(); i++) {
                HttpRequestResponse requestResponse = requestResponseList.get(i);
                
                if (requestResponse.response() == null) {
                    failCount++;
                    api.logging().logToError("Request " + (i + 1) + " has no response.");
                    continue;
                }
                
                String formattedResponse = MessageFormatter.formatResponseMessage(requestResponse.response());
                
                if (formattedResponse == null) {
                    failCount++;
                    api.logging().logToError("Response " + (i + 1) + " contains binary data and cannot be copied.");
                    continue;
                }
                
                if (successCount > 0) {
                    sb.append("\n");
                    for (int j = 0; j < 50; j++) {
                        sb.append("=");
                    }
                    sb.append("\n");
                }
                sb.append(formattedResponse);
                successCount++;
            }
            
            if (successCount == 0) {
                JOptionPane.showMessageDialog(null, "所有响应都包含二进制数据或无响应，无法复制！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String content = sb.toString();
            
            if (ClipboardUtils.copyToClipboard(content)) {
                String message = successCount + " response(s) copied successfully.";
                if (failCount > 0) {
                    message += " " + failCount + " response(s) skipped due to binary content or no response.";
                }
                api.logging().logToOutput(message);
                JOptionPane.showMessageDialog(null, "响应报文复制成功！\n成功: " + successCount + " 个，跳过: " + failCount + " 个");
            } else {
                JOptionPane.showMessageDialog(null, "响应报文复制失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }));
    }
}
