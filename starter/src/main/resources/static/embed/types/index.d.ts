declare interface Animation_2 {
    enabled: boolean;
    duration: number;
    type: 'ease' | 'linear' | 'ease-in' | 'ease-out' | 'ease-in-out';
}
export { Animation_2 as Animation }

export declare interface BubbleConfig {
    show: boolean;
    icon: string;
    title: string;
    subtitle: string;
}

export declare interface BytedeskConfig {
    baseUrl: string;
    placement: 'bottom-left' | 'bottom-right';
    marginBottom: number;
    marginSide: number;
    tabsConfig: TabsConfig;
    bubbleConfig: BubbleConfig;
    showSupport: boolean;
    chatParams: ChatParams;
    navbarPreset: string;
    customColor: string;
    navbarColor: string;
    navbarTextColor: string;
    animation: Animation_2;
    window: WindowConfig;
    theme: Theme;
    draggable?: boolean;
    locale?: string;
}

export declare interface ChatParams {
    org: string;
    t: string;
    sid: string;
    [key: string]: string | number;
}

export declare type Language = 'zh-CN' | 'en-US' | 'ja-JP' | 'ko-KR';

export declare interface LocaleMessages {
    [key: string]: {
        title: string;
        settings: {
            position: string;
            tabs: string;
            bubble: string;
            navbar: string;
            theme: string;
            window: string;
            margins: string;
            animation: string;
            other: string;
            embed: string;
        };
        buttons: {
            copy: string;
            reset: string;
            openChat: string;
        };
    };
}

export declare interface NavbarPreset {
    backgroundColor: string;
    textColor: string;
}

export declare interface TabsConfig {
    home: boolean;
    messages: boolean;
    help: boolean;
    news: boolean;
}

export declare interface Theme {
    mode: 'light' | 'dark' | 'system';
    primaryColor: string;
    secondaryColor: string;
    textColor: string;
    backgroundColor: string;
    position?: 'left' | 'right';
    navbar: NavbarPreset;
}

export declare interface WindowConfig {
    width: number;
    height: number;
    title: string;
    position?: 'left' | 'right';
}

export { }
