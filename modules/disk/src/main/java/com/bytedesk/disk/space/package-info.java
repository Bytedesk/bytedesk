/**
 * 网盘空间包：DiskSpaceEntity 及仓储/响应。
 * 空间是网盘的顶层容器：每组织一个共享空间（SHARED）+ 每成员一个个人空间（PERSONAL，懒创建），
 * 根目录随空间初始化创建。
 *
 * @author bytedesk.com
 */
@NullMarked
package com.bytedesk.disk.space;

import org.jspecify.annotations.NullMarked;
