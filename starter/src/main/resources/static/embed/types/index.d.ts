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
    extra?: string;
    goodsInfo?: string;
    orderInfo?: string;
    vipLevel?: string;
    [key: string]: string | number | undefined;
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
