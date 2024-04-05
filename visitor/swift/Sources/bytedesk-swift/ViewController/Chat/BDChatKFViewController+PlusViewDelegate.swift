//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/7.
//

import UIKit
import AVFoundation

extension BDChatKFViewController: BDPlusViewDelegate {
    
    // MARK: BDPlusViewDelegate
    
    func sharePickPhotoButtonPressed() {
        // debugPrint("照片：\(#function)")
        initImagePickerLazy()
        
        self.mImagePickerController!.sourceType = .photoLibrary
        self.mImagePickerController!.mediaTypes = ["public.image", "public.movie"]
        self.present(self.mImagePickerController!, animated: true)
    }
    
    func shareTakePhotoButtonPressed() {
        // debugPrint("\(#function)")
        initImagePickerLazy()
        
        let authStatus = AVCaptureDevice.authorizationStatus(for: .video)
        if authStatus == .denied || authStatus == .restricted {
            // The user has explicitly denied permission for media capture.
            let alert = UIAlertController(
                title: "",
                message: "请在iPhone的‘设置-隐私-相机’选项中，允许访问你的相机",
                preferredStyle: .alert
            )
            let yesButton = UIAlertAction(
                title: "确定",
                style: .default,
                handler: { action in
                    // Handle your yes button action here
                }
            )
            alert.addAction(yesButton)
            present(alert, animated: true, completion: nil)
        } else if authStatus == .authorized {
            // 允许访问
            mImagePickerController!.sourceType = .camera
            mImagePickerController!.mediaTypes = ["public.image", "public.movie"]
            present(mImagePickerController!, animated: true, completion: nil)
        } else if authStatus == .notDetermined {
            // Explicit user permission is required for media capture, but the user has not yet granted or denied such permission.
            AVCaptureDevice.requestAccess(for: .video) { granted in
                if granted {
                    // 点击允许访问时调用
                    // 用户明确许可与否，媒体需要捕获，但用户尚未授予或拒绝许可。
                    // NSLog(@"Granted access to %@", AVMediaTypeVideo);
                    DispatchQueue.main.async {
                        self.mImagePickerController!.sourceType = .camera
                        self.present(self.mImagePickerController!, animated: true, completion: nil)
                    }
                } else {
                    // NSLog(@"Not granted access to %@", AVMediaTypeVideo);
                }
            }
        } else {
            // debugPrint("Unknown authorization status")
        }
    }
    
    func shareFileButtonPressed() {
        // debugPrint("\(#function)")
        let documentPickerViewController = UIDocumentPickerViewController(documentTypes: BDUtils.documentTypes(), in: .open)
        documentPickerViewController.delegate = self
        present(documentPickerViewController, animated: true, completion: nil)
    }
    
    func shareLeaveMsgButtonPressed() {
        // debugPrint("\(#function)")
        let leaveMessageVC = BDLeaveMessageViewController()
        leaveMessageVC.initWithType(type: self.mThreadType!, uid: self.mUUid!, isPush: false)
        let leavenavigationController = UINavigationController(rootViewController: leaveMessageVC)
        self.navigationController?.present(leavenavigationController, animated: true, completion: nil)
    }
    
    func shareRateButtonPressed() {
        // debugPrint("\(#function)")
        let ratevc = BDRateViewController()
        ratevc.initWithThreadTid(self.mTid!, withPush: false)
        self.navigationController?.present(ratevc, animated: true, completion: nil)
    }
    
    func shareShowFAQButtonPressed() {
        // debugPrint("\(#function)")
        let faqvc = BDFaqViewController()
        let faqnavigationController = UINavigationController(rootViewController: faqvc)
        self.navigationController?.present(faqnavigationController, animated: true, completion: nil)
    }
    
}
