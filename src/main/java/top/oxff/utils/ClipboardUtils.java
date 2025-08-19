package top.oxff.utils;

import top.oxff.CopyElements;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClipboardUtils {

    /**
     * 将文本内容复制到剪贴板，确保正确处理UTF-8编码
     */
    public static boolean copyToClipboard(String content) {
        if (content == null) {
            return false;
        }

        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            
            // 创建支持UTF-8的自定义Transferable
            UTF8StringSelection selection = new UTF8StringSelection(content);
            clipboard.setContents(selection, null);
            
            CopyElements.logger.logToOutput("Content copied to clipboard successfully (UTF-8)");
            return true;
            
        } catch (Exception e) {
            CopyElements.logger.logToError("Failed to copy content to clipboard: " + e.getMessage());
            
            // 降级到标准StringSelection作为备选方案
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection fallbackSelection = new StringSelection(content);
                clipboard.setContents(fallbackSelection, null);
                
                CopyElements.logger.logToOutput("Content copied using fallback method");
                return true;
                
            } catch (Exception fallbackException) {
                CopyElements.logger.logToError("Fallback copy also failed: " + fallbackException.getMessage());
                return false;
            }
        }
    }

    /**
     * 自定义Transferable实现，确保UTF-8编码正确处理
     */
    private static class UTF8StringSelection implements Transferable, ClipboardOwner {
        private final String data;

        public UTF8StringSelection(String data) {
            this.data = data;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {
                DataFlavor.stringFlavor,
                DataFlavor.plainTextFlavor,
                new DataFlavor("text/plain; charset=utf-8", "Plain Text UTF-8")
            };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor f : flavors) {
                if (f.equals(flavor)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.stringFlavor)) {
                return data;
            } else if (flavor.equals(DataFlavor.plainTextFlavor)) {
                return new java.io.StringReader(data);
            } else if (flavor.getMimeType().contains("text/plain") && flavor.getMimeType().contains("utf-8")) {
                return data.getBytes(StandardCharsets.UTF_8);
            }
            throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
            // 不需要特殊处理
        }
    }
}