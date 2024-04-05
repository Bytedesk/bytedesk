package com.bytedesk.demo.common;

import android.content.Intent;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.demo.R;
import com.bytedesk.ui.activity.BrowserActivity;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 *
 */
public class ScanFragment extends BaseFragment implements QRCodeView.Delegate {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.zxingview) ZXingView mZXingView;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scan, null);
        ButterKnife.bind(this, root);

        initTopBar();
        mZXingView.setDelegate(this);

        return root;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_scan));
    }

    @Override
    public void onStart() {
        super.onStart();

        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别

        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    public void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    /**
     * TODO: 目前仅生成二维码，暂未实现扫码登录逻辑
     */
    @Override
    public void onScanQRCodeSuccess(String result) {
        Logger.i("扫描结果：" + result);
        vibrate();
//        mZXingView.startSpot(); // 开始识别

        if (result.startsWith("http")) {

            Intent intent = new Intent(getContext(), BrowserActivity.class);
            intent.putExtra(BDUiConstant.EXTRA_URL, result);
            startActivity(intent);

        } else {

            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setTitle("扫描结果")
                    .setMessage(result)
                    .addAction("确定", (dialog, index) -> dialog.dismiss())
                    .create(com.qmuiteam.qmui.R.style.QMUI_Dialog)
                    .show();
        }

    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Logger.e("打开相机出错");
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SCALE_TRANSITION_CONFIG;
    }


}
