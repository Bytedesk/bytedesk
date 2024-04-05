//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit
//import AlamofireImage
import Kingfisher

class BDMsgImageContentView: BDMsgBaseContentView {
    
    var imageView: UIImageView?
    
    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("BDMsgImageContentView \(#function)")

        setupView()
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    override func setupView() {
        super.setupView()
        // debugPrint("\(#function)")
        if (imageView != nil) {
            imageView?.removeFromSuperview()
            imageView = nil
        }
        //
        imageView = UIImageView()
        imageView?.contentMode = .scaleAspectFit
        bubbleView!.addSubview(imageView!)
        //
        // Add a tap gesture recognizer to the image view
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(imageTapped))
        imageView?.addGestureRecognizer(tapGesture)
        imageView?.isUserInteractionEnabled = true
    }
    
    override func initWithMessageModel(_ message: BDMessageModel?) {
        super.initWithMessageModel(message)
//        let url = URL(string: message!.imageUrl!)
//        let placeholderImage = UIImage(named: "photo")!
//        imageView.af.setImage(withURL: url, placeholderImage: placeholderImage)
        
//        imageView?.loadFrom(url: message!.imageUrl!)
        
        // https://github.com/onevcat/Kingfisher
        let url = URL(string: message!.imageUrl!)
        // Using Kingfisher to load and cache the image
        imageView!.kf.setImage(with: url)
        //
        setNeedsLayout()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        //
        let contentViewInsets = self.messageModel!.contentViewInsets()
        let contentSize = CGSize(width: 150, height: 200) // realImageViewSize() //
        //
        let width = contentViewInsets.left + contentSize.width + contentViewInsets.right
        let height = contentViewInsets.top + contentSize.height + contentViewInsets.bottom
        
        var imageFrame = CGRect.zero
        var bubbleFrame = CGRect.zero
        var backFrame = CGRect.zero

        if self.messageModel!.isSend() {
            imageFrame = CGRect(x: contentViewInsets.left, y: contentViewInsets.top, width: width - contentViewInsets.left * 2, height: height - contentViewInsets.top * 2)
            bubbleFrame = CGRect(x: 0, y: 0, width: width, height: height)
            backFrame = CGRect(x: self.screenWidth! - width - self.xMargin, y: yTop, width: width, height: height)
        } else {
            imageFrame = CGRect(x: contentViewInsets.left, y: contentViewInsets.top, width: width - contentViewInsets.left * 2, height: height - contentViewInsets.top * 2)
            bubbleFrame = CGRect(x: 0, y: 0, width: width, height: height)
            backFrame = CGRect(x: self.xMargin, y: self.yTop, width: width, height: height)
        }
        self.imageView!.frame = imageFrame
        self.bubbleView!.frame = bubbleFrame
        self.frame = backFrame
        debugPrint("self.imageView.frame \(self.imageView!.frame), self.bubble.frame: \(self.bubbleView!.frame)")
//
//        self.backgroundColor = UIColor.black
        imageView?.backgroundColor = UIColor.red
        bubbleView?.backgroundColor = UIColor.blue
    }
    
    @objc func imageTapped() {
        delegate?.imageViewClicked(imageView!)
    }
    
//    按比例缩放图片
    private func realImageViewSize() -> CGSize {
        guard let image = self.imageView?.image else {
            return CGSize(width: 150, height: 200)
        }
        let imageSize = image.size
        debugPrint("image.size: \(imageSize),")
        return CGSize(width: 150, height: 150 * imageSize.height/imageSize.width)
        
//        let viewSize = bubbleView!.bounds.size
//
//        let widthRatio = viewSize.width / imageSize.width
//        let heightRatio = viewSize.height / imageSize.height
//
//        let scaleFactor = min(widthRatio, heightRatio)
//        let scaledWidth = imageSize.width * scaleFactor
//        let scaledHeight = imageSize.height * scaleFactor
//
//        imageView!.bounds.size = CGSize(width: scaledWidth, height: scaledHeight)
    }
    
}

