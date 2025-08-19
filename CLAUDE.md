# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Burp Suite extension (Montoya API) that adds context menu functionality to copy various HTTP request/response elements to the clipboard. The extension provides utilities to extract hosts, header keys, paths, and parameter values from selected HTTP traffic in Burp Suite's Proxy and Logger tabs.

## Build and Development Commands

### Build the project
```bash
mvn clean package
```

### Compile without packaging
```bash
mvn compile
```

### Clean build artifacts
```bash
mvn clean
```

## Architecture

### Core Components

1. **CopyElements.java** - Main extension entry point that registers with Burp Suite's Montoya API. Initializes the extension and registers the context menu provider.

2. **CopyElementsContextMenuItemsProvider.java** - Implements the context menu functionality. Creates menu items for:
   - Copying unique hosts from selected requests
   - Copying unique header keys 
   - Copying paths without query parameters
   - Copying request parameter values by name (opens dialog)
   - Copying response parameter values by name (opens dialog)

3. **ParamNameConfigDialog.java** - Swing dialog that allows users to specify a parameter name to extract values from either request parameters or JSON response bodies.

4. **JsonUtils.java** - Utility class with two JSON parsing implementations:
   - Jackson-based parser for general JSON traversal
   - Fastjson2-based parser for response body extraction
   - Recursively searches JSON structures to find all values for a given key name

5. **ValuesType.java** - Enum to differentiate between REQUEST, RESPONSE, or BOTH parameter extraction modes.

## Key Technical Details

- **Java Version**: 17
- **Build Tool**: Maven
- **Main Dependencies**:
  - Burp Suite Montoya API (2024.7)
  - Jackson Databind (2.13.4.1) 
  - Fastjson2 (2.0.51)
- **UI Framework**: Swing for dialogs
- **Clipboard Operations**: Uses Java AWT Toolkit for system clipboard access

## Development Notes

- The extension only activates in Burp's Proxy and Logger tools
- All extracted values are deduplicated using HashSet before copying
- Dialog text is in Chinese (参数名 = parameter name)
- Extensive logging via `CopyElements.logger` for debugging
- All clipboard operations are wrapped in try-catch with user feedback via JOptionPane