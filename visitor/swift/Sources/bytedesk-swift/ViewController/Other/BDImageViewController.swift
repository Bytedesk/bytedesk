//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/13.
//

import UIKit
import Photos

// UIScrollViewDelegate // TODO: 增加图片缩放
class BDImageViewController: UIViewController {
    
//    var scrollView: UIScrollView!
//    var imageView: UIImageView!
    
    var image: UIImage? {
        didSet {
            imageView.image = image
            //
//            imageView.sizeToFit()
//            scrollView.contentSize = imageView.bounds.size
//
//            let scrollViewFrame = scrollView.frame
//            let scaleWidth = scrollViewFrame.size.width / scrollView.contentSize.width
//            let scaleHeight = scrollViewFrame.size.height / scrollView.contentSize.height
//            let minScale = min(scaleWidth, scaleHeight)
//
//            scrollView.minimumZoomScale = minScale
//            scrollView.maximumZoomScale = 1.0
//            scrollView.zoomScale = minScale
        }
    }
    
    private let imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.translatesAutoresizingMaskIntoConstraints = false
        //
        imageView.backgroundColor = UIColor.red
        return imageView
    }()
    
    private let saveButton: UIButton = {
        let button = UIButton(type: .system)
        button.setTitle("保存到相册", for: .normal)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.addTarget(self, action: #selector(saveButtonTapped), for: .touchUpInside)
        return button
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black
        
//        scrollView = UIScrollView(frame: view.bounds)
//        scrollView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
//        scrollView.delegate = self
//        scrollView.showsVerticalScrollIndicator = false
//        scrollView.showsHorizontalScrollIndicator = false
//        view.addSubview(scrollView)
        
        view.addSubview(imageView)
//        scrollView.addSubview(imageView)
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: view.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
        
        view.addSubview(saveButton)
        NSLayoutConstraint.activate([
            saveButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            saveButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -16)
        ])
        
//        let doubleTapGesture = UITapGestureRecognizer(target: self, action: #selector(handleDoubleTap(_:)))
//        doubleTapGesture.numberOfTapsRequired = 2
//        scrollView.addGestureRecognizer(doubleTapGesture)
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissViewController))
        view.addGestureRecognizer(tapGesture)
        
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(dismissViewController))
    }
    
    @objc private func dismissViewController() {
//        dismiss(animated: true, completion: nil)
        self.navigationController?.dismiss(animated: true)
    }
    
    @objc private func saveButtonTapped() {
        guard let imageToSave = image else {
            return
        }
        
        PHPhotoLibrary.requestAuthorization { status in
            DispatchQueue.main.async {
                if status == .authorized {
                    self.saveImageToAlbum(image: imageToSave)
                } else {
                    self.showAuthorizationAlert()
                }
            }
        }
    }
    
    private func saveImageToAlbum(image: UIImage) {
        PHPhotoLibrary.shared().performChanges({
            PHAssetChangeRequest.creationRequestForAsset(from: image)
        }, completionHandler: { success, error in
            DispatchQueue.main.async {
                if success {
                    self.showSaveSuccessAlert()
                } else if let error = error {
                    print("Error saving image to photo album: \(error.localizedDescription)")
                    self.showSaveFailureAlert()
                }
            }
        })
    }
    
    private func showAuthorizationAlert() {
        let alert = UIAlertController(title: "无保存相册权限", message: "请授权保存到相册.", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "确定", style: .default, handler: { _ in
            self.navigationController?.dismiss(animated: true)
        }))
        present(alert, animated: true, completion: nil)
    }
    
    private func showSaveSuccessAlert() {
        let alert = UIAlertController(title: "保存成功", message: "图片已经成功保存到相册.", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "确定", style: .default, handler: { _ in
            self.navigationController?.dismiss(animated: true)
        }))
        present(alert, animated: true, completion: nil)
    }
    
    private func showSaveFailureAlert() {
        let alert = UIAlertController(title: "保存错误", message: "保存图片到相册失败，请重试", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "确定", style: .default, handler: { _ in
            self.navigationController?.dismiss(animated: true)
        }))
        present(alert, animated: true, completion: nil)
    }
    
    // MARK: - Gesture Recognizer
        
//    @objc private func handleDoubleTap(_ gestureRecognizer: UITapGestureRecognizer) {
//        if scrollView.zoomScale > scrollView.minimumZoomScale {
//            scrollView.setZoomScale(scrollView.minimumZoomScale, animated: true)
//        } else {
//            let center = gestureRecognizer.location(in: scrollView)
//            let zoomRect = zoomRectForScale(scrollView.maximumZoomScale, center: center)
//            scrollView.zoom(to: zoomRect, animated: true)
//        }
//    }
//
//    private func zoomRectForScale(_ scale: CGFloat, center: CGPoint) -> CGRect {
//        var zoomRect = CGRect.zero
//        zoomRect.size.height = scrollView.frame.size.height / scale
//        zoomRect.size.width = scrollView.frame.size.width / scale
//        let newCenter = imageView.convert(center, from: scrollView)
//        zoomRect.origin.x = newCenter.x - (zoomRect.size.width / 2.0)
//        zoomRect.origin.y = newCenter.y - (zoomRect.size.height / 2.0)
//        return zoomRect
//    }
//
//    // MARK: - UIScrollViewDelegate
//
//    func viewForZooming(in scrollView: UIScrollView) -> UIView? {
//        return imageView
//    }
}
