package com.bytedesk.ui.activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bytedesk.core.util.BDCoreUtils;
import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class QRCodeActivity extends AppCompatActivity {

    QMUITopBarLayout mTopBar;
    ImageView mQRCodeImageView;

    private String mGid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_qrcode);

        mGid = getIntent().getStringExtra(BDUiConstant.EXTRA_GID);

        initTopBar();
        initView();

        createEnglishQRCode();
    }

    private void initTopBar() {
        //
        mTopBar = findViewById(R.id.bytedesk_qrcode_topbarlayout);
        mTopBar.setTitle("二维码");
        mTopBar.addLeftBackImageButton().setOnClickListener(view -> finish());
        //
        QMUIStatusBarHelper.translucent(this);
    }

    /**
     * 界面初始化
     */
    private void initView () {
        //
        mQRCodeImageView = findViewById(R.id.bytedesk_qrcode_imageview);
    }

    /**
     * TODO: 目前仅生成二维码，暂未实现扫码登录逻辑
     */
    private void createEnglishQRCode() {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(BDCoreUtils.getQRCodeGroup(getBaseContext(), mGid),
                        BGAQRCodeUtil.dp2px(QRCodeActivity.this, 200));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mQRCodeImageView.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(QRCodeActivity.this, "生成二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


}
