/**
 * 网盘留痕包：DiskLogEntity 记录授权变更与彻底删除等关键事件。
 * 一期仅内部记录，不对外提供查询；不记录文件内容、提取码或签名 URL。
 *
 * @author bytedesk.com
 */
@NullMarked
package com.bytedesk.disk.audit;

import org.jspecify.annotations.NullMarked;
