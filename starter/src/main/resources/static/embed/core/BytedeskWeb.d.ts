import { BytedeskConfig } from '../types';
export default class BytedeskWeb {
    private config;
    private bubble;
    private window;
    private isVisible;
    private isDragging;
    private windowState;
    constructor(config: BytedeskConfig);
    private getDefaultConfig;
    init(): void;
    private createBubble;
    private createChatWindow;
    private generateChatUrl;
    private setupMessageListener;
    showChat(): void;
    hideChat(): void;
    private minimizeWindow;
    private toggleMaximize;
    private setupResizeListener;
    destroy(): void;
}
