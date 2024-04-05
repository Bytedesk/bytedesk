package com.bytedesk.demo.common;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bytedesk.core.util.BDCoreUtils;
import com.bytedesk.demo.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * 
 */
public class QRCodeFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.iv_english) ImageView mEnglishIv;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_qrcode, null);
        ButterKnife.bind(this, root);

        initTopBar();
        createEnglishQRCode();

        return root;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_qrcode));
    }

    /**
     * TODO: 目前仅生成二维码，暂未实现扫码登录逻辑
     */
    private void createEnglishQRCode() {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(BDCoreUtils.getQRCodeLogin(getContext()),
                        BGAQRCodeUtil.dp2px(getContext(), 200));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mEnglishIv.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(getContext(), "生成二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}
