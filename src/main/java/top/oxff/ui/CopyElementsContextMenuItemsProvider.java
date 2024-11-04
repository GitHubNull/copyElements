package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import top.oxff.model.ValuesType;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CopyElementsContextMenuItemsProvider implements ContextMenuItemsProvider {
    private final MontoyaApi api;

    public CopyElementsContextMenuItemsProvider(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<HttpRequestResponse> requestResponseList = event.selectedRequestResponses();
        if (!event.isFromTool(ToolType.PROXY, ToolType.LOGGER) || requestResponseList.isEmpty()) {
            return null;
        }
        List<Component> menuItemList = new ArrayList<>();
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
        return menuItemList;
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
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(content);
                clipboard.setContents(selection, null);
                api.logging().logToOutput("Paths copied successfully.");
            } catch (Exception exception) {
                api.logging().logToError(exception.getMessage());
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
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(content);
                clipboard.setContents(selection, null);
                api.logging().logToOutput("Header keys copied successfully.");
            } catch (Exception exception) {
                api.logging().logToError(exception.getMessage());
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
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(content);
                clipboard.setContents(selection, null);
                api.logging().logToOutput("Hosts copied successfully.");
            } catch (Exception exception) {
                api.logging().logToError(exception.getMessage());
            }
        }));
    }
}
