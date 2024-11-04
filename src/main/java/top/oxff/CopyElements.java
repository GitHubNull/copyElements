package top.oxff;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import top.oxff.ui.CopyElementsContextMenuItemsProvider;

public class CopyElements implements BurpExtension {
    private static final String EXTENSION_NAME = "copyElements";
    private static final String EXTENSION_VERSION = "1.0";
    private static final String EXTENSION_DESCRIPTION = "copy host from history or logger";
    private static final String EXTENSION_AUTHOR = "oxff";
    private static final String EXTENSION_LICENSE = "MIT";

    public static MontoyaApi api;
    public static Logging logger;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        api = montoyaApi;
        logger = montoyaApi.logging();
        montoyaApi.extension().setName(EXTENSION_NAME);

        logger.logToOutput("HttpMocker loaded");
        logger.logToOutput("Version: " + EXTENSION_VERSION);
        logger.logToOutput("Author: " + EXTENSION_AUTHOR);
        logger.logToOutput("License: " + EXTENSION_LICENSE);
        logger.logToOutput("Description: " + EXTENSION_DESCRIPTION);

        api.userInterface().registerContextMenuItemsProvider(new CopyElementsContextMenuItemsProvider(api));
    }
}
