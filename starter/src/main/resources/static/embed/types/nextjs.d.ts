import { ComponentType } from 'react';

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

declare interface BytedeskConfig {
    isDebug?: boolean;
    forceRefresh?: boolean;
    baseUrl?: string;
    apiUrl?: string;
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
    showSupport?: boolean;
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

export declare const BytedeskNextjs: ComponentType<BytedeskNextjsProps>;

declare interface BytedeskNextjsProps extends BytedeskConfig {
    onInit?: () => void;
}

declare interface ChatConfig {
    org: string;
    t: string;
    sid: string;
    uid?: string;
    visitorUid?: string;
    nickname?: string;
    avatar?: string;
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
