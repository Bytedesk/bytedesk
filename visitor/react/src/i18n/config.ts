/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-01 22:59:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-01 22:59:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
import i18next from "i18next";
import { initReactI18next } from "react-i18next";
// import LanguageDetector from "i18next-browser-languagedetector";
import ens1 from "./en/ns1.json";
import ens2 from "./en/ns2.json";
import zhns1 from "./zh-cn/ns1.json";
import zhns2 from "./zh-cn/ns2.json";
import { LOCALE } from "../utils/constants";

export const defaultNS = "ns1";

let lng = "zh-cn";
let locale = localStorage.getItem(LOCALE);
if (locale) {
  lng = locale;
}

i18next
  // .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    lng: lng, // if you're using a language detector, do not define the lng option
    fallbackLng: "en",
    debug: true,
    resources: {
      en: {
        ns1: ens1,
        ns2: ens2,
      },
      zh: {
        ns1: zhns1,
        ns2: zhns2,
      },
    },
    defaultNS,
  });

// export default i18next;
