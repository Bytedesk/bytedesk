package com.bytedesk.disk.constant;

import com.bytedesk.core.constant.I18Consts;

/**
 * 网盘模块 i18n 业务异常 key（经 GlobalExceptionHandler 翻译，WARN 摘要不打堆栈）
 */
public final class DiskI18nConsts {

    private DiskI18nConsts() {
    }

    public static final String I18N_DISK_FOLDER_VIEW_FORBIDDEN = I18Consts.I18N_PREFIX + "disk.folder.view.forbidden";

    public static final String I18N_DISK_FOLDER_PERM_FORBIDDEN = I18Consts.I18N_PREFIX + "disk.folder.perm.forbidden";

    public static final String I18N_DISK_FOLDER_NOT_FOUND = I18Consts.I18N_PREFIX + "disk.folder.not.found";

    public static final String I18N_DISK_SPACE_NOT_FOUND = I18Consts.I18N_PREFIX + "disk.space.not.found";

    public static final String I18N_DISK_RECYCLE_FORBIDDEN = I18Consts.I18N_PREFIX + "disk.recycle.forbidden";
}
