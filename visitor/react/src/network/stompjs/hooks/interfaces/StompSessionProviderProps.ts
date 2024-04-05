import { StompConfig } from "@stomp/stompjs";

export interface StompSessionProviderProps extends StompConfig {
  url: string;
  children: any;
  /**
   * @deprecated
   */
  stompClientOptions?: any;
}
