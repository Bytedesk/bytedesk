type Messages = {
    [key in 'zh-CN' | 'en-US']: {
        title: string;
        bubble: {
            title: string;
            subtitle: string;
        };
        tabs: {
            home: string;
            messages: string;
            help: string;
            news: string;
        };
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
    };
};
export declare const messages: Messages;
export {};
