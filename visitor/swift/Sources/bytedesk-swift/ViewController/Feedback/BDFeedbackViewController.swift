//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/12.
//

import UIKit

class BDFeedbackViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    var mAdminUid: String?
    var mCategoryDict: [AnyHashable: Any]?

    var mCategoryName: String?
    var mCategoryCid: String?
    var mImageUrl: String?

    var contentTextView: UITextView?
    var imageView: UIImageView?
    var mobileTextField: UITextField?

    var mIsPush: Bool = false
    var forceEnableBackGesture: Bool = false

    var mImagePickerController: UIImagePickerController?
    
    let deviceHeight = UIScreen.main.bounds.size.height
    let deviceWidth = UIScreen.main.bounds.size.width
    
    func initWithUid(_ uid: String, withPush isPush: Bool) {
        print("1.\(#function)")
        mAdminUid = uid
        mIsPush = isPush
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        print("2.\(#function)")
        
        title = "意见反馈"
        view.backgroundColor = .white
        
        if #available(iOS 13.0, *) {
            let mode = traitCollection.userInterfaceStyle
            if mode == .dark {
                view.backgroundColor = .black
            } else {
                view.backgroundColor = .white
            }
        }
        
        mCategoryName = ""
        mCategoryCid = ""
        mImageUrl = ""
        
        // TODO: 待上线
        getFeedbackCategories()
        
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "我的反馈", style: .plain, target: self, action: #selector(showFeedbackRecords))
        
        if !mIsPush {
            navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(handleCloseButtonEvent))
        }
        
        forceEnableBackGesture = true
        
        let singleTap = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        singleTap.numberOfTapsRequired = 1
        singleTap.numberOfTouchesRequired = 1
        view.addGestureRecognizer(singleTap)
        
        let doubleTap = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        doubleTap.numberOfTapsRequired = 2
        doubleTap.numberOfTouchesRequired = 1
        view.addGestureRecognizer(doubleTap)
        
        mImagePickerController = UIImagePickerController()
        mImagePickerController?.delegate = self
        mImagePickerController?.allowsEditing = true
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("3.\(#function)")
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        print("4.\(#function)")
        
        contentTextView = UITextView(frame: CGRect(x: 10, y: 120, width: deviceWidth - 20, height: 100))
        contentTextView?.font = UIFont.systemFont(ofSize: 16)
//        contentTextView?.placeholder = "请将您的建议或您遇到的问题告诉我们，我们会认真的听取并及时反馈"
        contentTextView?.layer.cornerRadius = 5.0
        contentTextView?.layer.masksToBounds = true
        contentTextView?.layer.borderColor = UIColor.systemGray.cgColor
        contentTextView?.layer.borderWidth = 0.5
        view.addSubview(contentTextView!)
        
        imageView = UIImageView(frame: CGRect(x: 10, y: 250, width: 100, height: 100))
        imageView?.image = UIImage(named: "AlbumAddBtn.png", in: .module, with: nil)
        imageView?.isUserInteractionEnabled = true
        imageView?.layer.cornerRadius = 5.0
        imageView?.layer.masksToBounds = true
        imageView?.layer.borderColor = UIColor.systemGray.cgColor
        imageView?.layer.borderWidth = 0.5
        view.addSubview(imageView!)
        
        let singleTap = UITapGestureRecognizer(target: self, action: #selector(presentViewController(_:)))
        singleTap.numberOfTapsRequired = 1
        singleTap.numberOfTouchesRequired = 1
        imageView?.addGestureRecognizer(singleTap)
        
        mobileTextField = UITextField(frame: CGRect(x: 10, y: 380, width: deviceWidth - 20, height: 40))
        mobileTextField?.font = UIFont.systemFont(ofSize: 16)
        mobileTextField?.clearsOnBeginEditing = true
        mobileTextField?.clearsOnInsertion = true
        mobileTextField?.placeholder = "联系方式：请输入您的手机号、微信或邮箱"
        mobileTextField?.layer.cornerRadius = 5.0
        mobileTextField?.layer.masksToBounds = true
        mobileTextField?.layer.borderColor = UIColor.systemGray.cgColor
        mobileTextField?.layer.borderWidth = 0.5
        view.addSubview(mobileTextField!)
        
        let submitButton = UIButton(frame: CGRect(x: 10, y: 450, width: deviceWidth - 20, height: 40))
        submitButton.backgroundColor = .systemOrange
        submitButton.setTitle("提交意见反馈", for: .normal)
        submitButton.setTitleColor(.white, for: .normal)
        submitButton.addTarget(self, action: #selector(submitButtonClicked), for: .touchUpInside)
        submitButton.layer.cornerRadius = 20.0
        submitButton.layer.masksToBounds = true
        submitButton.layer.borderColor = UIColor.systemGray.cgColor
        submitButton.layer.borderWidth = 0.5
        view.addSubview(submitButton)
    }
    
    @objc func submitButtonClicked() {
        let content = contentTextView?.text
        let mobile = mobileTextField?.text
        
        if (content!.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).isEmpty ||
            mobile!.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).isEmpty) {
            BDToast.show(message: "反馈内容或手机号不能为空")
            return
        }
        //
        BDUIApis.showTip(with: self, message: "提交反馈中，请稍后")
        BDCoreApis.createFeedback(adminUid: mAdminUid,
                                  categoryCid: "",
                                  mobile: mobile,
                                  content: content,
                                  imageUrl: mImageUrl,
                                  onSuccess: { feedbackResult in
            debugPrint(feedbackResult.message!)
//            BDToast.show(message: feedbackResult.message!)
            BDUIApis.showTip(with: self, message: feedbackResult.message!)
            if (!self.mIsPush) {
                self.navigationController?.dismiss(animated: true)
            } else {
                self.navigationController?.popViewController(animated: true)
            }
        }, onFailure: { error in
            BDToast.show(message: error)
        })
    }
    
    @objc func closeVC() {
        self.dismiss(animated: true)
    }
    
    @objc func showFeedbackRecords() {
        let feedbackHistoryVC = BDFeedbackHistoryViewController()
        self.navigationController?.pushViewController(feedbackHistoryVC, animated: true)
    }
    
    @objc func handleCloseButtonEvent() {
        self.navigationController?.dismiss(animated: true)
    }
    
    @objc func handleBackButtonEvent() {
        self.navigationController?.popViewController(animated: true)
    }
    
    @objc func dismissKeyboard() {
        self.view.endEditing(true)
    }
    
    func getFeedbackCategories() {
        
    }
    
    // MARK:
    
    @objc func presentViewController(_ sender: Any) {
        print("1.\(#function)")
        
        authorizationPresentAlbumViewController()
    }

    func authorizationPresentAlbumViewController() {
        print("2.\(#function)")
        presentAlbumViewController()
    }

    func presentAlbumViewController() {
        print("3.\(#function)")
        
        mImagePickerController!.sourceType = .photoLibrary
        present(mImagePickerController!, animated: true, completion: nil)
    }

    // MARK: - UIImagePickerControllerDelegate

    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        print("4.\(#function)")
        picker.dismiss(animated: true) {
            self.perform(#selector(self.dealWithImage(_:)), with: info)
        }
    }

    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        print("5.\(#function)")
        picker.dismiss(animated: true, completion: nil)
    }

    @objc func dealWithImage(_ info: [UIImagePickerController.InfoKey: Any]) {
        print("6.\(#function)")
        let mediaType = info[.mediaType] as? String
        if mediaType == "public.movie" {
            print("6.1.\(#function)")
            // Selected media is a video
            // [QMUITips showError:@"请选择图片" inView:self.view hideAfterDelay:2];
            // TODO: Upload video
            if let videoURL = info[.mediaURL] as? URL {
                print("videoURL", videoURL.absoluteString)
                
                // [BDVideoCompress compressVideoWithVideoUrl:videoURL withBiteRate:@(1500 * 1024) withFrameRate:@(30) withVideoWidth:@(960) withVideoHeight:@(540) compressComplete:^(id responseObjc) {
                //     //
                //     NSString *filePathStr = [responseObjc objectForKey:@"urlStr"];
                //     NSURL *compressvideourl = [NSURL fileURLWithPath:filePathStr];
                //     //
                //     NSData *videoData = [NSData dataWithContentsOfURL:compressvideourl];
                //     [self uploadVideoData:videoData];
                // }];
            }
        } else if mediaType == "public.image" {
            print("6.2.\(#function)")
            // Get the selected photo
            if var image = info[.originalImage] as? UIImage {
                var imageOrientation = image.imageOrientation
                if imageOrientation != .up {
                    UIGraphicsBeginImageContext(image.size)
                    image.draw(in: CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height))
                    image = UIGraphicsGetImageFromCurrentImageContext() ?? image
                    UIGraphicsEndImageContext()
                }
                if let imageData = image.jpegData(compressionQuality: 0.6) {
                    uploadImageData(imageData)
                }
            }
        }
    }

    func uploadImageData(_ imageData: Data) {
        print("7.\(#function)")
        
        // [BDUIApis showTipWithVC:self withMessage:@"图片上传中"];
        let localId = UUID().uuidString
//        let imageName = "\(BDSettings.getUsername())_\(BDUtils.getCurrentTimeString()).png"
//        BDCoreApis.uploadImageData(imageData, withImageName: imageName, withLocalId: localId, resultSuccess: { dict in
//            print("\(#function), uploadImageData: \(dict)")
//            if let statusCode = dict["status_code"] as? NSNumber, statusCode.intValue == 200 {
//                if let imageUrl = dict["data"] as? String {
//                    self.mImageUrl = imageUrl
//                    self.imageView?.setImage(with: URL(string: self.mImageUrl), placeholder: UIImage(named: "AlbumAddBtn.png", in: Bundle(for: Self.self), compatibleWith: nil))
//                    // self.mFileUrl = imageUrl;
//                    print("imageUrl", self.mImageUrl)
//                } else {
//                    // [BDUIApis showErrorWithVC:self withMessage:@"上传图片错误"];
//                }
//            }
//        }, resultFailed: { error in
//            if let error = error {
//                // [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
//            }
//        })
    }
}
