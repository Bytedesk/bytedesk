package com.bytedesk.disk.constant;

/**
 * 网盘模块常量
 */
public final class DiskConsts {

    private DiskConsts() {
    }

    /** 空间类型：组织共享空间 */
    public static final String SPACE_TYPE_SHARED = "SHARED";
    /** 空间类型：个人空间 */
    public static final String SPACE_TYPE_PERSONAL = "PERSONAL";

    /** 节点类型：文件夹 */
    public static final String NODE_TYPE_FOLDER = "FOLDER";
    /** 节点类型：文件 */
    public static final String NODE_TYPE_FILE = "FILE";

    /** 存储后端：经 core upload 存储（本地/OSS/MinIO 由全局配置决定） */
    public static final String STORAGE_BACKEND_UPLOAD = "UPLOAD";

    /** 目录权限分隔符 */
    public static final String PERM_SEPARATOR = ",";

    /** 根目录名称：我的文件 */
    public static final String ROOT_NAME_PERSONAL = "我的文件";
    /** 根目录名称：共享空间 */
    public static final String ROOT_NAME_SHARED = "共享空间";

    /** 名称最大长度 */
    public static final int MAX_NAME_LENGTH = 255;

    /** 默认分页大小 */
    public static final int DEFAULT_PAGE_SIZE = 50;
}
