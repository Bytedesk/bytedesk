import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "이미지가 아닌 객체는 자를 수 없습니다." },
  "image-panel": {
    arrange: {
      title: "정렬",
      forward: "앞으로 가져오기",
      backward: "뒤로 보내기",
      front: "맨 앞으로 가져오기",
      back: "맨 뒤로 보내기"
    },
    transform: {
      title: "변형",
      rotate: "회전 (°)",
      x: "X 좌표 (px)",
      y: "Y 좌표 (px)",
      width: "너비 (px)",
      height: "높이 (px)",
      lock: "비율 고정 (%)"
    },
    crop: {
      title: "자르기",
      start: "자르기 시작",
      mode: "자유 모드"
    },
    group: {
      title: "그룹",
      group: "그룹화",
      unGroup: "그룹 해제"
    },
    align: {
      title: "맞춤",
      default: "맞춤 유형 선택",
      left: "왼쪽 맞춤",
      center: "가운데 맞춤",
      right: "오른쪽 맞춤",
      top: "위쪽 맞춤",
      middle: "가운데 맞춤",
      bottom: "아래쪽 맞춤",
      horizon: "수평 간격 균등 분배",
      vertical: "수직 간격 균등 분배"
    },
    null: "선택된 객체가 없습니다"
  },
  "image-text-wrap": {
    title: "텍스트 감싸기",
    wrappingStyle: "감싸기 스타일",
    square: "사각형",
    topAndBottom: "위아래",
    inline: "텍스트와 같은 줄",
    behindText: "텍스트 뒤",
    inFrontText: "텍스트 앞",
    wrapText: "텍스트 감싸기",
    bothSide: "양쪽",
    leftOnly: "왼쪽만",
    rightOnly: "오른쪽만",
    distanceFromText: "텍스트와 거리",
    top: "위(px)",
    left: "왼쪽(px)",
    bottom: "아래(px)",
    right: "오른쪽(px)"
  },
  "image-popup": {
    replace: "교체",
    delete: "삭제",
    edit: "편집",
    crop: "자르기",
    reset: "크기 초기화"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "이미지",
  upload: {
    float: "플로팅 이미지",
    cell: "셀 내 이미지"
  },
  panel: { title: "이미지 편집" },
  save: {
    title: "셀 이미지 저장",
    menuLabel: "셀 이미지 저장",
    imageCount: "이미지 수",
    fileNameConfig: "파일 이름",
    useRowCol: "셀 주소 사용 (A1, B2...)",
    useColumnValue: "열 값 사용",
    selectColumn: "열 선택",
    cancel: "취소",
    confirm: "저장",
    saving: "저장 중...",
    error: "셀 이미지 저장 실패"
  },
  "image-popup": {
    replace: "바꾸기",
    delete: "삭제",
    edit: "편집",
    crop: "자르기",
    reset: "원래 크기",
    flipH: "가로 뒤집기",
    flipV: "세로 뒤집기"
  },
  "update-status": {
    exceedMaxSize: "이미지 크기가 제한({0}MB)을 초과했습니다",
    invalidImageType: "지원하지 않는 이미지 형식입니다",
    exceedMaxCount: "한 번에 {0}개까지만 업로드할 수 있습니다",
    invalidImage: "유효하지 않은 이미지입니다"
  },
  "drawing-anchor": {
    title: "고정 위치 설정",
    both: "셀과 함께 이동 및 크기 조절",
    position: "셀과 함께 이동하되 크기 변경 안 함",
    none: "셀과 함께 이동 및 크기 조절 안 함"
  },
  "cell-image": {
    pasteTitle: "셀 이미지로 붙여넣기",
    pasteContent: "셀 이미지 붙여넣기는 기존 셀 내용을 덮어씁니다. 계속 진행하시겠습니까?",
    pasteError: "이 단위에서는 시트 셀 이미지 복사 붙여넣기를 지원하지 않습니다"
  },
  permission: { dialog: { editErr: "해당 범위는 보호되어 있어 편집 권한이 없습니다. 편집하려면 작성자에게 문의하세요." } },
  shortcut: {
    "drawing-view": "그리기 보기",
    "drawing-move-down": "그리기 아래로 이동",
    "drawing-move-up": "그리기 위로 이동",
    "drawing-move-left": "그리기 왼쪽으로 이동",
    "drawing-move-right": "그리기 오른쪽으로 이동",
    "drawing-delete": "그리기 삭제"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
