/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2023-11-14 16:59:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-03 17:06:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
import React from "react";
import "./App.css";
import { ChatBox } from "./pages/ChatBox";
import { Route, Routes } from "react-router-dom";
import Layout from "./pages/layout/Layout";
import Blogs from "./pages/layout/Blogs";
import Contact from "./pages/layout/Contact";
import NoPage from "./pages/layout/NoPage";
import Home from "./pages/layout/Home";
import Gestures from "./pages/gesture/Gesture";
import { ChatPro } from "./pages/ChatPro";
import { ChatAi } from "./pages/ChatAi";

//
function App() {
  return (
    <div className="App">
      <Routes>
        {/* 主要入口 */}
        {/* http://localhost:9006/?t=1&id=c293374081784c0e9dd1d57020048e37& */}
        <Route path="/" element={<ChatBox />}>
          <Route index element={<ChatBox />} />
        </Route>
        {/* 下面仅用于测试 */}
        {/* http://localhost:9006/ai */}
        <Route path="/ai" element={<ChatAi />} />
        {/* http://localhost:9006/pro */}
        <Route path="/pro" element={<ChatPro />} />
        {/* http://localhost:9006/g */}
        <Route path="/g" element={<Gestures />}>
          <Route index element={<Gestures />} />
        </Route>
        {/* http://localhost:9006/layout */}
        <Route path="/layout" element={<Layout />}>
          <Route index element={<Home />} />
          {/* http://localhost:9006/layout/blogs */}
          <Route path="blogs" element={<Blogs />} />
          {/* http://localhost:9006/layout/contact */}
          <Route path="contact" element={<Contact />} />
          <Route path="*" element={<NoPage />} />
        </Route>
      </Routes>
    </div>
  );
}

export default App;
