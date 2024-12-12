/*
 * @Author: jack ning github@bytedesk.com
 * @Date: 2024-12-10 11:59:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-10 18:21:35
 * @FilePath: /backend/src/main/java/io/typebot/features/block/model/options/VideoBlockOptions.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.bytedesk.core.workflow.flow.model.block.model.options;

import java.util.Map;

import lombok.Data;

@Data
public class VideoProvider {
    private String name; // 提供商名称
    private String videoId; // 视频ID
    private Map<String, Object> settings; // 提供商特定设置
}
