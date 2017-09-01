package com.puppycrawl.tools.checkstyle.checks;

public class MethodCallInfo {
    final String name;
    final String argCount;

    private MethodCallInfo(String name, String argCount) {
        this.name = name;
        this.argCount = argCount;
    }

    public static MethodCallInfo of(String methodName, String argCount) {
        return new MethodCallInfo(methodName, argCount);
    }

    public static MethodCallInfo of(String methodName, int argCount) {
        return new MethodCallInfo(methodName, Integer.toString(argCount));
    }

    @Override
    public String toString() {
        return "MethodCallInfo{" +
            "name='" + name + '\'' +
            ", argCount=" + argCount +
            '}';
    }
}
