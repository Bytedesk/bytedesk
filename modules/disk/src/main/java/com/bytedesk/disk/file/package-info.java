/**
 * 网盘文件包（模块核心子域）：DiskFileEntity（文件/文件夹统一单表）及
 * CRUD/移动/复制/回收站/搜索/下载/ZIP。
 * active_key 维持同级名称唯一（软删除置 NULL 释放名称）；物理存储复用 core upload。
 *
 * @author bytedesk.com
 */
@NullMarked
package com.bytedesk.disk.file;

import org.jspecify.annotations.NullMarked;
