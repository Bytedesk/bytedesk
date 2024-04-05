/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-25 11:08:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 11:41:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
// import { useAuthStore } from "@/stores/auth";
// import { useSettingsStore } from "@/stores/setting";
import { ConfigProviderProps, RadioChangeEvent } from "antd";
import {
  createContext,
  Dispatch,
  SetStateAction,
  useEffect,
  useMemo,
  useState,
} from "react";
import enUS from "antd/locale/en_US";
import zhCN from "antd/locale/zh_CN";
import { useTranslation } from "react-i18next";
import {
  LOCALE,
  THEME_MODE_TYPE,
  THEME_MODE_TYPE_DARK,
  THEME_MODE_TYPE_SYSTEM,
  THEME_NAME_TYPE_DARK,
} from "@/utils/constants";
import useTheme, { ThemeMode, ThemeName } from "@/hooks/useTheme";

type Locale = ConfigProviderProps["locale"];

interface AppContextState {
  isLoggedIn: boolean;
  isMqttConnected: boolean;
  setMqttConnectStatus: (isConnectd: boolean) => void;
  //
  isDarkMode: boolean;
  // themeName: string;
  // setThemeName: Dispatch<SetStateAction<ThemeName>>;
  themeMode: string;
  setThemeMode: Dispatch<SetStateAction<ThemeMode>>;
  //
  // login: () => Promise<void>;
  // logout: () => void;
  // settings: SETTING.State;
  locale: Locale;
  changeLocale: (event: RadioChangeEvent) => void;
}

export const AppContext = createContext<Partial<AppContextState>>({});

//
export const AppProvider = ({ children }: { children: React.ReactNode }) => {
  // const [account, setAccount] = useState<AUTH.AuthState>(defaultAccount)
  // const { accessToken, isMqttConnected, setMqttConnectStatus } = useAuthStore(state => {
  //     return {
  //         accessToken: state.accessToken,
  //         isMqttConnected: state.isMqttConnected,
  //         setMqttConnectStatus: state.setMqttConnectStatus
  //     }
  // })
  // const settings = useSettingsStore((state) => state.settings)
  // const [setting, setSetting] = useState<SETTING.State>(defaultSetting)

  // const isLoggedIn = useMemo(() => {
  //     // return !!account.token
  //     return !!accessToken && accessToken.trim().length > 0
  // }, [accessToken])

  // const login = useCallback(async () => {
  // }, [])

  const { themeMode, setThemeMode, isDarkMode } = useTheme();
  // const isDarkMode = useMemo(() => {
  //     console.log('AppContext isDarkMode themeMode:', themeMode)
  //     if (themeMode === THEME_MODE_TYPE_SYSTEM) {
  //         return isSystemDarkMode
  //     } else if (themeMode === THEME_MODE_TYPE_DARK) {
  //         return true
  //     } else if (themeName === THEME_NAME_TYPE_DARK) {
  //         return true
  //     }
  //     return false
  // }, [themeMode, themeName])

  // 国际化
  const { i18n } = useTranslation();
  const [locale, setLocal] = useState<Locale>(zhCN);
  //
  const changeLocale = (e: RadioChangeEvent) => {
    const localeValue: Locale = e.target.value;
    console.log("localeValue:", localeValue);
    // 修改antd组件语言
    setLocal(localeValue);
    // 修改自定义文字语言
    i18n.changeLanguage(localeValue.locale);
    // 本地存储
    localStorage.setItem(LOCALE, localeValue.locale);
    if (!localeValue) {
      // dayjs.locale('en');
    } else {
      // dayjs.locale('zh-cn');
    }
  };

  useEffect(() => {
    let llocal = localStorage.getItem(LOCALE);
    if (llocal === "zh-cn") {
      setLocal(zhCN);
    } else {
      setLocal(enUS);
    }
  }, []);

  return (
    <AppContext.Provider
      value={{
        // isLoggedIn,
        // settings,
        // isMqttConnected,
        // setMqttConnectStatus,
        isDarkMode,
        // themeName,
        // setThemeName,
        themeMode,
        setThemeMode,
        locale,
        changeLocale,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};
