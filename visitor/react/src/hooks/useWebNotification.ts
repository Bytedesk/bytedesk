/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-02 22:25:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-02 22:36:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
// 获取浏览器通知权限
import { useEffect, useState } from "react";

function useWebNotification() {
  const [isNotificationGranted, setIsNotificationGranted] =
    useState<boolean>(false);

  useEffect(() => {
    if (window.Notification && Notification.permission !== "granted") {
      //
      Notification.requestPermission(function (status) {
        if (status === "granted") {
          console.log("Notification permission granted.");
          setIsNotificationGranted(true);
        } else {
          console.log("notification denied");
          setIsNotificationGranted(false);
        }
      });
    } else {
      console.log("已经授权或浏览器不支持通知");
      setIsNotificationGranted(true);
    }
  }, []);

  return {
    isNotificationGranted,
  };
}

export default useWebNotification;
