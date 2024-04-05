/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bytedesk.ui.manager;

import android.content.Context;
import android.widget.Toast;

import com.bytedesk.ui.R;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

public class UpgradeTipTask implements UpgradeTask {

    public UpgradeTipTask() {
    }

    @Override
    public void upgrade() {
        throw new RuntimeException("please call upgrade(Activity activity)");
    }

    public void upgrade(Context context, String tip, String url) {

        new QMUIDialog.MessageDialogBuilder(context)
                .setTitle("升级提示")
                .setMessage(tip)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, (dialog, index) -> {
                    dialog.dismiss();
                    Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                    // TODO: 下载、安装
                })
                .create(R.style.QMUI_Dialog) // FIXME: W/System.err: java.lang.NullPointerException at android.widget.TextView.setTextColor(TextView.java:4352)
                .show();
    }


}
