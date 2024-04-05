/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-01 21:27:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 20:12:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
import "./i18n/config";
import App from "./App";
import { ConfigProvider } from "./components/ChatUI";
// https://www.npmjs.com/package/react-cookie
import { CookiesProvider } from "react-cookie";
// import { StompSessionProvider } from './network/stompjs/hooks';

//
const AppWrapper = () => {
  return (
    <CookiesProvider defaultSetOptions={{ path: "/" }}>
      <ConfigProvider>
        <App />
      </ConfigProvider>
    </CookiesProvider>
  );
};

export default AppWrapper;
