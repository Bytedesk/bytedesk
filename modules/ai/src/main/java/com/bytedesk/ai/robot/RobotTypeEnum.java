package com.bytedesk.ai.robot;

public enum RobotTypeEnum {
    SERVICE(0), // 客服机器人
    MARKETING(1); // 营销机器人

    //
    private final int value;

    // 枚举构造器，每个枚举常量都有一个与之关联的整型值
    RobotTypeEnum(int value) {
        this.value = value;
    }

    // 获取枚举常量的整型值
    public int getValue() {
        return value;
    }

    // 根据整型值查找对应的枚举常量
    public static RobotTypeEnum fromValue(int value) {
        for (RobotTypeEnum type : RobotTypeEnum.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }

    public static RobotTypeEnum fromString(String typeStr) {
        // 使用try-catch处理可能的异常
        try {
            return RobotTypeEnum.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            // 处理错误，例如记录日志或抛出更具体的异常
            throw new IllegalArgumentException("Invalid robot type: " + typeStr, e);
        }
    }

}
