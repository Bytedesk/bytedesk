/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-21 23:20:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 14:34:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
//
import { THEME_MODE_TYPE, THEME_NAME_TYPE } from "../utils/constants";
import { useEffect, useState } from "react";

export type ThemeName = "light" | "dark";
export type ThemeMode = "system" | "light" | "dark";

function useTheme() {
  //
  const name = localStorage.getItem(THEME_NAME_TYPE) || "light";
  const [themeName, setThemeName] = useState<ThemeName>(name as ThemeName);
  // 客户端主题模式
  const mode = localStorage.getItem(THEME_MODE_TYPE) || "system";
  const [themeMode, setThemeMode] = useState<ThemeMode>(mode as ThemeMode);
  //
  useEffect(() => {
    // 只有系统模式下才能够根据系统变化设置皮肤
    if (mode === "system") {
      // 设置初始皮肤
      if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
        // console.log('init Dark Mode');
        setThemeName("dark");
      } else {
        // console.log('init Light Mode');
        setThemeName("light");
      }
      // 监听系统颜色切换
      window
        .matchMedia("(prefers-color-scheme: dark)")
        .addEventListener("change", (event) => {
          if (event.matches) {
            // console.log('change Dark Mode');
            setThemeName("dark");
          } else {
            // console.log('change Light Mode');
            setThemeName("light");
          }
        });
    }
  }, []);

  useEffect(() => {
    localStorage.setItem(THEME_MODE_TYPE, themeMode);
    // console.log('themeMode changed: ', themeMode);
    if (themeMode === "light") {
      setThemeName("light");
    } else if (themeMode === "dark") {
      setThemeName("dark");
    } else if (themeMode === "system") {
      // 设置初始皮肤
      if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
        setThemeName("dark");
      } else {
        setThemeName("light");
      }
    }
  }, [themeMode]);

  useEffect(() => {
    localStorage.setItem(THEME_NAME_TYPE, themeName);
    // console.log('themeName changed: ', themeName);
  }, [themeName]);

  //
  return {
    // 操作系统当前主题
    themeName,
    setThemeName,
    // 客户端当前设置
    themeMode,
    setThemeMode,
    // 操作系统当前模式
    isDarkMode: themeName === "dark",
    isLightMode: themeName === "light",
  };
}

export default useTheme;
