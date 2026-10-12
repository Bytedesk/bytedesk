import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "无法裁剪非图片元素" },
  "image-panel": {
    arrange: {
      title: "排列",
      forward: "上移一层",
      backward: "下移一层",
      front: "置于顶层",
      back: "置于底层"
    },
    transform: {
      title: "变换",
      rotate: "旋转 (°)",
      x: "X (px)",
      y: "Y (px)",
      width: "宽度 (px)",
      height: "高度 (px)",
      lock: "锁定比例 (%)"
    },
    crop: {
      title: "裁剪",
      start: "开始裁剪",
      mode: "自由比例裁剪"
    },
    group: {
      title: "组合",
      group: "组合",
      unGroup: "取消组合"
    },
    align: {
      title: "对齐方式",
      default: "选择对齐方式",
      left: "左对齐",
      center: "水平居中",
      right: "右对齐",
      top: "顶部对齐",
      middle: "垂直居中",
      bottom: "底部对齐",
      horizon: "水平分布",
      vertical: "垂直分布"
    },
    null: "未选中任何对象"
  },
  "image-text-wrap": {
    title: "文字环绕",
    wrappingStyle: "环绕方式",
    square: "四周型",
    topAndBottom: "上下型",
    inline: "嵌入型",
    behindText: "衬于文字下方",
    inFrontText: "浮于文字上方",
    wrapText: "自动换行",
    bothSide: "两侧",
    leftOnly: "左侧",
    rightOnly: "右侧",
    distanceFromText: "距正文",
    top: "上（px）",
    left: "左（px）",
    bottom: "下（px）",
    right: "右（px）"
  },
  "image-popup": {
    replace: "替换",
    delete: "删除",
    edit: "编辑",
    crop: "裁剪",
    reset: "重置大小"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "图片",
  upload: {
    float: "浮动图片",
    cell: "单元格图片"
  },
  panel: { title: "编辑图片" },
  save: {
    title: "保存单元格图片",
    menuLabel: "保存单元格图片",
    imageCount: "图片数量",
    fileNameConfig: "文件名",
    useRowCol: "使用单元格地址 (A1, B2...)",
    useColumnValue: "使用某列的值",
    selectColumn: "选择列",
    cancel: "取消",
    confirm: "保存",
    saving: "保存中...",
    error: "保存单元格图片失败"
  },
  "image-popup": {
    replace: "替换",
    delete: "删除",
    edit: "编辑",
    crop: "裁剪",
    reset: "重置大小",
    flipH: "水平翻转",
    flipV: "垂直翻转"
  },
  "update-status": {
    exceedMaxSize: "图片大小超过限制, 限制为{0}M",
    invalidImageType: "图片类型错误",
    exceedMaxCount: "图片只能一次上传{0}张",
    invalidImage: "无效图片"
  },
  "drawing-anchor": {
    title: "锚点属性",
    both: "与单元格一起移动和调整大小",
    position: "移动但不调整大小与单元格",
    none: "不要移动或调整大小与单元格"
  },
  "cell-image": {
    pasteTitle: "粘贴为单元格图片",
    pasteContent: "粘贴单元格图片将覆盖单元格中的现有内容，继续粘贴",
    pasteError: "此单元中不支持单元格图片复制粘贴"
  },
  permission: { dialog: { editErr: "该范围已被保护，目前无编辑权限。如需编辑，请联系创建者。" } },
  shortcut: {
    "drawing-view": "绘图视图",
    "drawing-move-down": "下移绘图",
    "drawing-move-up": "上移绘图",
    "drawing-move-left": "左移绘图",
    "drawing-move-right": "右移绘图",
    "drawing-delete": "删除绘图"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
