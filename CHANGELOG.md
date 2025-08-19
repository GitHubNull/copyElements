# Changelog

All notable changes to the CopyElements project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Comprehensive project documentation (README.md)
- GitHub Actions automated release workflow
- Complete .gitignore configuration
- MIT License file
- Version changelog tracking

### Changed
- Improved project structure and organization
- Enhanced development workflow

## [1.3.0] - 2024-08-19

### Added
- ✨ **Copy formatted request messages** - New context menu option to copy complete HTTP request messages with automatic JSON/XML formatting
- ✨ **Copy formatted response messages** - New context menu option to copy complete HTTP response messages with automatic JSON/XML formatting
- 🔧 **Extended tool support** - Request/response copy functions now work in Repeater and Intruder tools (in addition to Proxy and Logger)
- 🧠 **Smart binary content detection** - Automatic detection and filtering of binary files (images, videos, executables, etc.)
- 🌐 **Enhanced UTF-8 support** - Improved handling of Unicode characters, especially Chinese text
- 📋 **Custom clipboard handling** - Specialized clipboard operations with fallback mechanisms for better compatibility

### Changed
- 🚀 **Improved Content-Type detection** - Priority-based content type checking (HTTP headers first, then binary analysis)
- ⚡ **Optimized message processing** - Better performance for large HTTP messages
- 🎨 **Enhanced user interface** - More informative success/error messages and progress feedback
- 🔧 **Upgraded dependencies** - Updated Jackson to 2.15.4 (security fix), added Maven Shade Plugin

### Fixed
- 🐛 **Chinese character encoding issues** - Resolved garbled text problems when copying messages containing Chinese characters
- 🔧 **Context menu availability** - Fixed menu items not appearing when focus is in text editor boxes
- 🛡️ **Security vulnerabilities** - Updated Jackson library to address CVE-2022-0468
- 📝 **Message formatting edge cases** - Better handling of malformed JSON/XML content

### Technical Details
- Added `MessageFormatter.java` utility class for HTTP message processing
- Added `ClipboardUtils.java` with custom UTF-8 string selection implementation  
- Enhanced `CopyElementsContextMenuItemsProvider.java` with new menu items and tool support
- Implemented comprehensive binary content detection with file signature checking
- Added Maven Shade Plugin for proper dependency packaging

## [1.0.0] - 2024-08-15

### Added
- 🎉 **Initial release** of CopyElements Burp Suite extension
- 🏠 **Copy unique hosts** - Extract and copy unique hostnames from selected HTTP requests
- 🔑 **Copy header keys** - Extract and copy unique HTTP header names
- 📍 **Copy paths without query parameters** - Get clean URL paths from requests
- 🔍 **Copy request parameter values** - Search and copy parameter values by name from request data
- 📊 **Copy response parameter values** - Search and copy parameter values by name from JSON response bodies
- 🎯 **Proxy and Logger support** - Context menu integration in Burp's Proxy and Logger tools
- 🔧 **Parameter name dialog** - Interactive dialog for specifying parameter names to search
- 📋 **System clipboard integration** - Direct copying to system clipboard with automatic sorting

### Technical Implementation
- Built on Burp Suite Montoya API 2024.7
- Java 17 compatibility
- Jackson 2.13.4.1 for JSON processing
- Fastjson2 2.0.51 for response parsing
- Maven build system with proper dependency management
- Swing-based user interface components

### Features
- Automatic deduplication of extracted values
- Sorted output for better readability
- Error handling and user feedback
- Support for both request parameters and JSON response traversal
- Configurable value extraction modes (REQUEST, RESPONSE, BOTH)

---

## Version History Summary

- **v1.3.0** - Major feature update with request/response message copying, multi-tool support, and encoding fixes
- **v1.0.0** - Initial release with basic parameter extraction functionality

## Upgrade Notes

### From v1.0.0 to v1.3.0
- No breaking changes - all existing functionality remains the same
- New context menu items will appear automatically
- Upgraded dependencies may improve performance and security
- Enhanced UTF-8 support may resolve character encoding issues

## Development Notes

This changelog follows the [Keep a Changelog](https://keepachangelog.com/) format. Each version is categorized by:
- **Added** for new features
- **Changed** for changes in existing functionality  
- **Fixed** for any bug fixes
- **Deprecated** for soon-to-be removed features
- **Removed** for now removed features
- **Security** for vulnerabilities and security improvements

For technical implementation details, see the [CLAUDE.md](CLAUDE.md) file.
For usage instructions, see the [README.md](README.md) file.