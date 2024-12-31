declare interface Animation_2 {
    enabled: boolean;
    duration: number;
    type: 'ease' | 'linear' | 'ease-in' | 'ease-out' | 'ease-in-out';
}

declare interface BubbleConfig {
    show: boolean;
    icon: string;
    title: string;
    subtitle: string;
}

declare interface BytedeskConfig {
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

export declare const BytedeskReact: React.FC<BytedeskReactProps>;

declare interface BytedeskReactProps extends BytedeskConfig {
    onInit?: () => void;
    locale?: string;
}

declare interface ChatParams {
    org: string;
    t: string;
    sid: string;
    [key: string]: string | number;
}

declare interface NavbarPreset {
    backgroundColor: string;
    textColor: string;
}

declare interface TabsConfig {
    home: boolean;
    messages: boolean;
    help: boolean;
    news: boolean;
}

declare interface Theme {
    mode: 'light' | 'dark' | 'system';
    primaryColor: string;
    secondaryColor: string;
    textColor: string;
    backgroundColor: string;
    position?: 'left' | 'right';
    navbar: NavbarPreset;
}

declare interface WindowConfig {
    width: number;
    height: number;
    title: string;
    position?: 'left' | 'right';
}

export { }
