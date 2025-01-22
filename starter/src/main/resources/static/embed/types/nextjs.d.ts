import { ComponentType } from 'react';

declare interface Animation_2 {
    enabled?: boolean;
    duration?: number;
    type?: 'ease' | 'linear' | 'ease-in' | 'ease-out' | 'ease-in-out';
}

declare interface BrowseParams {
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

declare interface BytedeskConfig {
    isDebug?: boolean;
    isPreload?: boolean;
    baseUrl?: string;
    placement?: 'bottom-left' | 'bottom-right';
    marginBottom?: number;
    marginSide?: number;
    autoPopup?: boolean;
    autoPopupDelay?: number;
    inviteParams?: InviteParams;
    tabsConfig?: TabsConfig;
    bubbleConfig?: BubbleConfig;
    showSupport?: boolean;
    chatParams?: ChatParams;
    browseParams?: BrowseParams;
    animation?: Animation_2;
    window?: WindowConfig;
    theme?: Theme;
    draggable?: boolean;
    locale?: string;
    onInit?: () => void;
    onShowChat?: () => void;
    onHideChat?: () => void;
    onMessage?: (message: string, type: string) => void;
}

export declare const BytedeskNextjs: ComponentType<BytedeskNextjsProps>;

declare interface BytedeskNextjsProps extends BytedeskConfig {
    onInit?: () => void;
}

declare interface ChatParams {
    org: string;
    t: string;
    sid: string;
    [key: string]: string | number | undefined;
}

declare interface InviteParams {
    show?: boolean;
    text?: string;
    icon?: string;
    delay?: number;
    loop?: boolean;
    loopDelay?: number;
    loopCount?: number;
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
