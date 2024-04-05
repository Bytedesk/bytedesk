/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 11:58:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-19 10:50:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
import { useState, useEffect } from "react";

// https://zh-hans.react.dev/learn/reusing-logic-with-custom-hooks
// https://www.electronjs.org/docs/latest/tutorial/online-offline-events
// Hook 的名称必须永远以 use 开头

// https://lettered.cn/a/130.html
export type Navigator = NavigatorNetworkInformation;
export declare interface NavigatorNetworkInformation {
  readonly connection?: NetworkInformation;
}
type Megabit = number;
type Millisecond = number;
export type EffectiveConnectionType =
  | "2g"
  | "3g"
  | "4g"
  | "slow-2g"
  | "unknown";
export type ConnectionType =
  | "bluetooth"
  | "cellular"
  | "ethernet"
  | "mixed"
  | "none"
  | "other"
  | "unknown"
  | "wifi"
  | "wimax";
export interface NetworkInformation extends EventTarget {
  readonly type?: ConnectionType;
  readonly effectiveType?: EffectiveConnectionType;
  readonly downlinkMax?: Megabit;
  readonly downlink?: Megabit;
  readonly rtt?: Millisecond;
  readonly saveData?: boolean;
  onchange?: EventListener;
}

interface INetworkState {
  online?: boolean; // 网络连接状态
  rtt?: number; // 网络延迟
  type?: string; // 网络类型
  effectiveType?: string; // 活跃的网络烈性
  downlink?: number; // 下载速度
  downlinkMax?: number; // 最大下载速度
  saveData?: boolean; // 是否开启省流开关
}

enum NetworkEventType {
  ONLINE = "online",
  OFFLINE = "offline",
  CHANGE = "change",
}

// https://juejin.cn/post/7220020535299506234
// 获取connect对象
const getConnection = () => {
  const nav = window.navigator as Navigator;
  if (!nav || typeof nav !== "object") return null;
  return nav.connection as Navigator["connection"];
};

const getConnectionInfo = () => {
  return getConnection() || {};
};

/**
 * https://lettered.cn/a/130.html
 * 取得网络类型
 */
export function getNetworkType(): string {
  /* wired 有线
    bluetooth,
    wifi,
    2g,3g,4g,5g...,
    unkown
  */
  const ua = navigator.userAgent;
  const ut = (navigator as Navigator).connection as NetworkInformation;
  let utt = ut
    ? ut.type
      ? ut.type.toLowerCase()
      : ut.effectiveType.toLowerCase()
    : null;
  if (utt) {
    switch (
      utt //bluetooth,
    ) {
      case "cellular":
      case "wimax":
        utt = ut
          ? ut.effectiveType
            ? ut.effectiveType.toLowerCase()
            : null
          : null;
        break;
      case "wifi":
        break;
      case "ethernet":
        utt = "wired";
        break;
      case "none":
      case "other":
      case "unknown":
        utt = null;
        break;
      default:
        break;
    }
  }
  let networkStr = utt
    ? utt
    : ua.match(/NetType\/\w+/)
      ? ua.match(/NetType\/\w+/)[0]
      : "unknown";
  networkStr = networkStr.toLowerCase().replace("nettype/", "");

  return networkStr ? (networkStr === "3gnet" ? "3g" : networkStr) : "unknown";
}

export function useNetworkStatus() {
  const [isNetworkOnline, setIsNetworkOnline] = useState(true);
  //
  useEffect(() => {
    function handleNetworkOnline() {
      console.log("networkStatus online:", navigator.onLine);
      // toast("networkStatus: online");
      if (navigator.onLine) setIsNetworkOnline(true);
    }
    function handleNetworkOffline() {
      console.log("networkStatus offline:", !navigator.onLine);
      // toast("networkStatus: offline");
      setIsNetworkOnline(false);
    }
    window.addEventListener("online", handleNetworkOnline);
    window.addEventListener("offline", handleNetworkOffline);
    //
    return () => {
      window.removeEventListener("online", handleNetworkOnline);
      window.removeEventListener("offline", handleNetworkOffline);
    };
  }, []);

  return isNetworkOnline;
}
