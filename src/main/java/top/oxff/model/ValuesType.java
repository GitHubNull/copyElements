package top.oxff.model;

public enum ValuesType {
    REQUEST,
    RESPONSE,
    BOTH;

    public static ValuesType getValuesType(String type) {
        return ValuesType.valueOf(type);
    }

    public static String getValuesType(ValuesType type) {
        return type.name();
    }
}
