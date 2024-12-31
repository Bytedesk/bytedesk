import { BytedeskConfig } from '../types';
interface BytedeskReactProps extends BytedeskConfig {
    onInit?: () => void;
    locale?: string;
}
export declare const BytedeskReact: React.FC<BytedeskReactProps>;
export {};
