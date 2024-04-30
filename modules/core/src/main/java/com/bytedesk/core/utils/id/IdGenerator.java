package com.bytedesk.core.utils.id;

/**
 *
 * ID生成器接口, 用于生成全局唯一的ID流水号
 *
 * @author Ivan.Ma
 */
public interface IdGenerator {

    /**
     * 生成下一个不重复的流水号
     * @return string
     */
    String next();

}
