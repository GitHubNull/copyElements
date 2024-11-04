package top.oxff.ui;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import top.oxff.CopyElements;
import top.oxff.model.ValuesType;
import top.oxff.utils.JsonUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ParamNameConfigDialog extends JDialog {
    List<HttpRequestResponse> requestResponseList;
    ValuesType valuesType;
    private final JTextField paramNameField;

    public ParamNameConfigDialog(List<HttpRequestResponse> requestResponseList, ValuesType valuesType) {
        this.requestResponseList = requestResponseList;
        this.valuesType = valuesType;
        setLayout(new BorderLayout());
        setTitle("参数名配置");

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerPanel.add(new JLabel("参数名:"));
        paramNameField = new JTextField("", 20);
        centerPanel.add(paramNameField);

        add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton buttonOK = new JButton("确定");
        buttonOK.setFocusable(false);

        JButton buttonCancel = new JButton("取消");

        buttonOK.addActionListener(e -> {
            CopyElements.logger.logToOutput("OK button clicked start.");
            String name = paramNameField.getText();
            if (name.strip().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "参数名不能为空！");
                return;
            }
            CopyElements.logger.logToOutput("paramName: " + name);
            name = name.strip().trim();
            if (valuesType == ValuesType.REQUEST) {
                copyAllRequestParamValuesToClipboardByName(name);
            } else if (valuesType == ValuesType.RESPONSE) {
                CopyElements.logger.logToOutput("valuesType == ValuesType.RESPONSE start....");
                copyAllResponseParamValuesToClipboardByName(name);
                CopyElements.logger.logToOutput("valuesType == ValuesType.RESPONSE end....");
            }else if (valuesType == ValuesType.BOTH) {
                copyAllRequestParamValuesToClipboardByName(name);
                copyAllResponseParamValuesToClipboardByName(name);
            }
            CopyElements.logger.logToOutput("OK button clicked end.");

            dispose();
        });

        buttonCancel.addActionListener(e -> dispose());

        southPanel.add(buttonOK);
        southPanel.add(buttonCancel);

        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setModal(true);
        setLocationRelativeTo(null);
    }

    private static void copyToClipBoard(HashSet<String> valueSet) {
        CopyElements.logger.logToOutput("copyToClipBoard start....");
        CopyElements.logger.logToOutput("valueSet.size(): " + valueSet.size());
        StringBuilder sb = new StringBuilder();
        for (String value : valueSet) {
            sb.append(value).append("\n");
        }
        String content = sb.toString();
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(content);
            clipboard.setContents(selection, null);
            JOptionPane.showMessageDialog(null, "参数值复制成功！");
        } catch (Exception exception) {
            CopyElements.logger.logToError("error: " + exception.getMessage());
            JOptionPane.showMessageDialog(null, "参数值复制失败！");
        }
        CopyElements.logger.logToOutput("copyToClipBoard end....");
    }

    private void copyAllResponseParamValuesToClipboardByName(String name) {
        CopyElements.logger.logToOutput("copyAllResponseParamValuesToClipboardByName start....");
        HashSet<String> valueSet = new HashSet<>();
        CopyElements.logger.logToOutput("requestResponseList.size(): " + requestResponseList.size());
        for (HttpRequestResponse requestResponse : requestResponseList) {
            try {
                Set<String> tmpValuesSet = JsonUtils.findValuesByKey(requestResponse.response(), name);
                if (tmpValuesSet != null){
                    valueSet.addAll(tmpValuesSet);
                }else{
                    CopyElements.logger.logToError("error: " + requestResponse.request().url().trim());
                }
//                valueSet.addAll(Objects.requireNonNull(JsonUtils.findValuesByKey(requestResponse.response(), name)));
            } catch (Exception e) {
                CopyElements.logger.logToError("error: " + e.getMessage());
            }
        }
        CopyElements.logger.logToOutput("valueSet.size(): " + valueSet.size());

        if (valueSet.isEmpty()) {
            JOptionPane.showMessageDialog(null, "未找到参数名：" + name);
        }
        copyToClipBoard(valueSet);
        CopyElements.logger.logToOutput("copyAllResponseParamValuesToClipboardByName end....");
    }

    private void copyAllRequestParamValuesToClipboardByName(String name) {
        HashSet<String> valueSet = new HashSet<>();
        for (HttpRequestResponse requestResponse : requestResponseList) {
            List<ParsedHttpParameter> parameters = requestResponse.request().parameters();
            for (ParsedHttpParameter parameter : parameters) {
                if (parameter.name().equals(name)) {
                    valueSet.add(parameter.value());
                }
            }
        }
        if (valueSet.isEmpty()) {
            JOptionPane.showMessageDialog(null, "未找到参数名：" + name);
            return;
        }

        copyToClipBoard(valueSet);
    }
}
