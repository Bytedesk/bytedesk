import { FeedbackConfig as FeedbackConfig_2 } from '../types';

declare interface Animation_2 {
    enabled?: boolean;
    duration?: number;
    type?: 'ease' | 'linear' | 'ease-in' | 'ease-out' | 'ease-in-out';
}

declare interface BrowseConfig {
    referrer?: string;
    url?: string;
    title?: string;
    [key: string]: string | number | undefined;
}

declare interface BubbleConfig {
    show?: boolean;
    icon?: string;
    title?: string;
    subtitle?: string;
}

declare interface ButtonConfig {
    show?: boolean;
    icon?: string;
    text?: string;
    width?: number;
    height?: number;
    onClick?: () => void;
}

export declare interface BytedeskConfig {
    isDebug?: boolean;
    forceRefresh?: boolean;
    apiUrl?: string;
    htmlUrl?: string;
    placement?: 'bottom-left' | 'bottom-right';
    marginBottom?: number;
    marginSide?: number;
    autoPopup?: boolean;
    autoPopupDelay?: number;
    draggable?: boolean;
    locale?: string;
    inviteConfig?: InviteConfig;
    tabsConfig?: TabsConfig;
    bubbleConfig?: BubbleConfig;
    buttonConfig?: ButtonConfig;
    feedbackConfig?: FeedbackConfig;
    chatConfig?: ChatConfig;
    browseConfig?: BrowseConfig;
    animation?: Animation_2;
    window?: WindowConfig;
    theme?: Theme;
    onInit?: () => void;
    onShowChat?: () => void;
    onHideChat?: () => void;
    onMessage?: (message: string, type: string) => void;
    onConfigChange?: (config: BytedeskConfig) => void;
    onVisitorInfo?: (uid: string, visitorUid: string) => void;
}

declare class BytedeskWeb {
    private config;
    private bubble;
    private window;
    private inviteDialog;
    private contextMenu;
    private hideTimeout;
    private isVisible;
    private isDragging;
    private windowState;
    private loopCount;
    private loopTimer;
    private initVisitorPromise;
    private getUnreadMessageCountPromise;
    private clearUnreadMessagesPromise;
    private feedbackTooltip;
    private feedbackDialog;
    private selectedText;
    private selectionDebounceTimer;
    private isTooltipVisible;
    private lastSelectionText;
    private lastMouseEvent;
    private lastSelectionRect;
    constructor(config: BytedeskConfig);
    private setupApiUrl;
    private getDefaultConfig;
    init(): Promise<void>;
    _initVisitor(): Promise<any>;
    private _browseVisitor;
    private getBrowserInfo;
    private getOSInfo;
    private getDeviceInfo;
    _getUnreadMessageCount(): Promise<any>;
    getUnreadMessageCount(): Promise<any>;
    initVisitor(): Promise<any>;
    browseVisitor(): Promise<void>;
    clearBrowseFailedLimit(): void;
    clearVisitorInfo(): void;
    forceInitVisitor(): Promise<any>;
    private showUnreadBadge;
    private clearUnreadBadge;
    clearUnreadMessages(): Promise<any>;
    private createBubble;
    private createChatWindow;
    private generateChatUrl;
    private setupMessageListener;
    private handleLocalStorageData;
    sendMessageToIframe(message: any): void;
    showChat(config?: Partial<BytedeskConfig>): void;
    hideChat(): void;
    private minimizeWindow;
    private toggleMaximize;
    private setupResizeListener;
    destroy(): void;
    private createInviteDialog;
    showInviteDialog(): void;
    hideInviteDialog(): void;
    handleInviteLoop(): void;
    showButton(): void;
    hideButton(): void;
    showBubble(): void;
    hideBubble(): void;
    private createContextMenu;
    private showContextMenu;
    private hideContextMenu;
    private togglePlacement;
    /**
     * 初始化文档反馈功能
     */
    private initFeedbackFeature;
    /**
     * 设置文本选择监听器
     */
    private setupTextSelectionListener;
    /**
     * 带防抖的文本选择处理
     */
    private handleTextSelectionWithDebounce;
    /**
     * 处理文本选择
     */
    private handleTextSelection;
    /**
     * 创建反馈提示框
     */
    private createFeedbackTooltip;
    /**
     * 显示反馈提示框
     */
    private showFeedbackTooltip;
    /**
     * 隐藏反馈提示框
     */
    private hideFeedbackTooltip;
    /**
     * 创建反馈对话框
     */
    private createFeedbackDialog;
    /**
     * 显示反馈对话框
     */
    private showFeedbackDialog;
    /**
     * 隐藏反馈对话框
     */
    private hideFeedbackDialog;
    /**
     * 生成页面截图并上传到服务器
     * @returns 返回上传后的截图URL，如果失败则返回null
     */
    private generateAndUploadScreenshot;
    /**
     * 生成截图预览（不上传到服务器）
     */
    private generateScreenshotPreview;
    /**
     * 计算选中文本附近的截图区域
     */
    private calculateScreenshotArea;
    /**
     * 动态加载 html2canvas
     */
    private loadHtml2Canvas;
    /**
     * 从CDN加载html2canvas
     */
    private loadHtml2CanvasFromCDN;
    /**
     * 提交反馈
     */
    private submitFeedback;
    /**
     * 提交反馈到服务器
     */
    private submitFeedbackToServer;
    /**
     * 显示反馈成功消息
     */
    private showFeedbackSuccess;
    /**
     * 公共方法：显示反馈对话框
     */
    showDocumentFeedback(selectedText?: string): void;
    /**
     * 公共方法：重新初始化反馈功能
     */
    reinitFeedbackFeature(): void;
    /**
     * 公共方法：强制初始化反馈功能（用于调试）
     */
    forceInitFeedbackFeature(): {
        success: boolean;
        methods: {
            showDocumentFeedback: boolean;
            testTextSelection: boolean;
        };
        elements: {
            tooltip: boolean;
            dialog: boolean;
            tooltipDOM: boolean;
            dialogDOM: boolean;
        };
    };
    /**
     * 公共方法：测试文本选择功能
     */
    testTextSelection(text?: string): void;
    /**
     * 公共方法：获取调试信息
     */
    getDebugInfo(): {
        config: BytedeskConfig;
        feedbackConfig: FeedbackConfig_2 | undefined;
        feedbackTooltip: boolean;
        feedbackDialog: boolean;
        selectedText: string;
        methods: {
            showDocumentFeedback: "string" | "number" | "bigint" | "boolean" | "symbol" | "undefined" | "object" | "function";
            testTextSelection: "string" | "number" | "bigint" | "boolean" | "symbol" | "undefined" | "object" | "function";
            forceInitFeedbackFeature: "string" | "number" | "bigint" | "boolean" | "symbol" | "undefined" | "object" | "function";
        };
    };
    /**
     * 公共方法：销毁反馈功能
     */
    private destroyFeedbackFeature;
}
export default BytedeskWeb;

declare interface ChatConfig {
    org: string;
    t: string;
    sid: string;
    uid?: string;
    visitorUid?: string;
    nickname?: string;
    avatar?: string;
    mobile?: string;
    email?: string;
    note?: string;
    goodsInfo?: string;
    orderInfo?: string;
    extra?: string;
    vipLevel?: string;
    debug?: boolean;
    settingsUid?: string;
    loadHistory?: boolean;
    [key: string]: string | number | boolean | undefined;
}

export declare interface FeedbackConfig {
    enabled?: boolean;
    trigger?: 'selection' | 'button' | 'both';
    showOnSelection?: boolean;
    selectionText?: string;
    buttonText?: string;
    dialogTitle?: string;
    placeholder?: string;
    submitText?: string;
    cancelText?: string;
    successMessage?: string;
    categoryNames?: string[];
    requiredTypes?: boolean;
    typesSectionTitle?: string;
    typesDescription?: string;
    submitScreenshot?: boolean;
    onSubmit?: (feedback: FeedbackData) => void;
    onCancel?: () => void;
}

export declare interface FeedbackData {
    selectedText: string;
    images?: string[];
    content: string;
    categoryNames?: string;
    url: string;
    title: string;
    userAgent: string;
    visitorUid?: string;
    orgUid?: string;
}

declare interface InviteConfig {
    show?: boolean;
    text?: string;
    icon?: string;
    delay?: number;
    loop?: boolean;
    loopDelay?: number;
    loopCount?: number;
    acceptText?: string;
    rejectText?: string;
    onAccept?: () => void;
    onReject?: () => void;
    onClose?: () => void;
    onOpen?: () => void;
}

declare interface TabsConfig {
    home?: boolean;
    messages?: boolean;
    help?: boolean;
    news?: boolean;
}

declare interface Theme {
    mode?: 'light' | 'dark' | 'system';
    textColor?: string;
    backgroundColor?: string;
}

declare interface WindowConfig {
    width?: number;
    height?: number;
}

export { }
