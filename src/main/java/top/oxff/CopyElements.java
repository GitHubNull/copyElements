package top.oxff;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import top.oxff.ui.CopyElementsContextMenuItemsProvider;

public class CopyElements implements BurpExtension {
    private static final String EXTENSION_NAME = "copyElements";
    private static final String EXTENSION_VERSION = "1.0";
    private static final String EXTENSION_DESCRIPTION = "copy host from history or logger";
    private static final String EXTENSION_AUTHOR = "oxff";
    private static final String EXTENSION_LICENSE = "MIT";

    public static MontoyaApi api;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        api = montoyaApi;
        montoyaApi.extension().setName(EXTENSION_NAME);

        montoyaApi.logging().logToOutput("HttpMocker loaded");
        montoyaApi.logging().logToOutput("Version: " + EXTENSION_VERSION);
        montoyaApi.logging().logToOutput("Author: " + EXTENSION_AUTHOR);
        montoyaApi.logging().logToOutput("License: " + EXTENSION_LICENSE);
        montoyaApi.logging().logToOutput("Description: " + EXTENSION_DESCRIPTION);

        api.userInterface().registerContextMenuItemsProvider(new CopyElementsContextMenuItemsProvider(api));
    }
}
