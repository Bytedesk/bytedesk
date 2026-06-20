//#region src/locales/index.ts
var e = {
	"zh-cn": {
		title: "在线客服",
		actions: { continueChat: "继续对话" },
		bubble: {
			title: "需要帮助吗？",
			subtitle: "点击开始对话"
		},
		tabs: {
			messages: "消息",
			thread: "历史会话",
			help: "帮助"
		},
		settings: {
			position: "位置",
			tabs: "标签页",
			bubble: "气泡",
			navbar: "导航栏",
			theme: "主题",
			window: "窗口",
			margins: "边距",
			animation: "动画",
			other: "其他",
			embed: "嵌入代码"
		}
	},
	"zh-tw": {
		title: "線上客服",
		actions: { continueChat: "繼續對話" },
		bubble: {
			title: "需要幫助嗎？",
			subtitle: "點擊開始對話"
		},
		tabs: {
			messages: "消息",
			thread: "歷史會話",
			help: "幫助"
		},
		settings: {
			position: "位置",
			tabs: "標籤頁",
			bubble: "氣泡",
			navbar: "導航欄",
			theme: "主題",
			window: "窗口",
			margins: "邊距",
			animation: "動畫",
			other: "其他",
			embed: "嵌入代碼"
		}
	},
	en: {
		title: "Online Support",
		actions: { continueChat: "Continue chat" },
		bubble: {
			title: "Need help?",
			subtitle: "Click to start chat"
		},
		tabs: {
			messages: "Messages",
			thread: "History",
			help: "Help"
		},
		settings: {
			position: "Position",
			tabs: "Tabs",
			bubble: "Bubble",
			navbar: "Navbar",
			theme: "Theme",
			window: "Window",
			margins: "Margins",
			animation: "Animation",
			other: "Other",
			embed: "Embed Code"
		}
	},
	ja: {
		title: "オンラインサポート",
		actions: { continueChat: "会話を続ける" },
		bubble: {
			title: "お困りですか？",
			subtitle: "クリックして会話を開始"
		},
		tabs: {
			messages: "メッセージ",
			thread: "履歴",
			help: "ヘルプ"
		},
		settings: {
			position: "位置",
			tabs: "タブ",
			bubble: "バブル",
			navbar: "ナビゲーション",
			theme: "テーマ",
			window: "ウィンドウ",
			margins: "余白",
			animation: "アニメーション",
			other: "その他",
			embed: "埋め込みコード"
		}
	},
	"ja-jp": {
		title: "オンラインサポート",
		actions: { continueChat: "会話を続ける" },
		bubble: {
			title: "お困りですか？",
			subtitle: "クリックして会話を開始"
		},
		tabs: {
			messages: "メッセージ",
			thread: "履歴",
			help: "ヘルプ"
		},
		settings: {
			position: "位置",
			tabs: "タブ",
			bubble: "バブル",
			navbar: "ナビゲーション",
			theme: "テーマ",
			window: "ウィンドウ",
			margins: "余白",
			animation: "アニメーション",
			other: "その他",
			embed: "埋め込みコード"
		}
	}
}, t = (t) => {
	let n = (t || "zh-cn").toLowerCase();
	return e[n] || e[n.split("-")[0]] || e["zh-cn"];
};
//#endregion
export { t as getLocaleMessages, e as messages };
