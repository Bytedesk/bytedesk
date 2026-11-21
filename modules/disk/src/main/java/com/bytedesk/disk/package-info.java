/**
 * 企业网盘模块（modules/disk，开源版）根包。
 * 提供基础能力：空间管理（space）、文件与目录（file）、文件夹级权限（permission）、
 * 关键操作留痕（audit）、用量统计（statistic）。
 * 高级功能（传输任务中心 transfer、外链分享 share）已移至 enterprise/disk，随企业版许可证提供。
 * 分层参照 modules/kbase：DiskPermissions/DiskInitializer 位于 permission/ 包，
 * 模块由 starter 的 @ComponentScan("com.bytedesk") 装配，无需独立配置类。
 *
 * @author bytedesk.com
 */
@NullMarked
package com.bytedesk.disk;

import org.jspecify.annotations.NullMarked;
