/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-26 10:22:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-29 10:45:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
window.onload = () => {
    if (isElectron) {
        let text = document.getElementById("snap-text");
        let switchtext = document.getElementById("switch");

        fetch('/ip').then((res) => {
            return res.json();
        }).then((data) => {
            let ipaddr = Object.values(data).flat();
            let x = 0;
            text.innerHTML = `在其他电脑或手机浏览器打开网址：<div id=\"ip-text\">http://${ipaddr[0]}:9012</div>`;
            switchtext.addEventListener("click", ((e) => {
                e.preventDefault();
                if (x === ipaddr.length - 1) x = 0;
                else x += 1;
                let iptext = document.getElementById("ip-text");
                iptext.innerText = `http://${ipaddr[x]}:9012`;
            }));
        });
    }
    function isElectron() {

        // Main process
        if (typeof process !== 'undefined' && typeof process.versions === 'object' && !!process.versions.electron) {
            return true;
        }

        // Detect the user agent when the `nodeIntegration` option is set to false
        if (typeof navigator === 'object' && typeof navigator.userAgent === 'string' && navigator.userAgent.indexOf('Electron') >= 0) {
            return true;
        }

        return false;
    }
}