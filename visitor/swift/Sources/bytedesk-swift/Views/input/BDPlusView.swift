//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit

protocol BDPlusViewDelegate: AnyObject {
    //
    func sharePickPhotoButtonPressed()
    func shareTakePhotoButtonPressed()
    //
    func shareFileButtonPressed()
    func shareLeaveMsgButtonPressed()
    //
    func shareRateButtonPressed()
    func shareShowFAQButtonPressed()
}

class BDPlusView: UIView {
    
    let SHAREMORE_ITEMS_TOP_MARGIN = 15.0
    let SHAREMORE_ITEMS_WIDTH = 53.0
    let SHAREMORE_ITEMS_HEIGHT = 55.0
    
    weak var delegate: BDPlusViewDelegate?
    
    var topLineView: UIView?
    var sharePickPhotoButton: UIButton?
    var shareTakePhotoButton: UIButton?
    var shareFileButton: UIButton?
    var shareLeaveMsgButton: UIButton?
    var shareRateButton: UIButton?
    
    var mButtonMargin: CGFloat?
    
    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("\(#function)")
        mButtonMargin = (UIScreen.main.bounds.size.width - SHAREMORE_ITEMS_WIDTH*4)/5

        setupView()
        setupConstraints()
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    func setupView() {
        // debugPrint("\(#function)")
        //
        topLineView = UIView()
        addSubview(topLineView!)
        //
        sharePickPhotoButton = UIButton()
        sharePickPhotoButton?.setTitle("相册", for: .normal)
        sharePickPhotoButton?.setTitle("相册", for: .highlighted)
        sharePickPhotoButton?.titleLabel!.adjustsFontSizeToFitWidth = true
        sharePickPhotoButton?.setImage(UIImage(systemName: "photo.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        sharePickPhotoButton?.setImage(UIImage(systemName: "photo.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        sharePickPhotoButton?.imageEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 15, right: -12)
        sharePickPhotoButton?.titleEdgeInsets = UIEdgeInsets.init(top: 25, left: -15, bottom: 0, right: 15)
        sharePickPhotoButton?.addTarget(self, action: #selector(handleSharePickPhotoButtonPressed), for: .touchUpInside)
        sharePickPhotoButton?.layer.cornerRadius = 5.0
        sharePickPhotoButton?.layer.masksToBounds = true
        sharePickPhotoButton?.layer.borderColor = UIColor(red: 200.0/255.0, green: 200.0/255.0, blue: 205.0/255.0, alpha: 1.0).cgColor
        sharePickPhotoButton?.layer.borderWidth = 0.5
        addSubview(sharePickPhotoButton!)
        BDUtils.setButtonTitleColor(sharePickPhotoButton!)
        //
        shareTakePhotoButton = UIButton()
        shareTakePhotoButton?.setTitle("拍照", for: .normal)
        shareTakePhotoButton?.setTitle("拍照", for: .highlighted)
        shareTakePhotoButton?.titleLabel!.adjustsFontSizeToFitWidth = true
        shareTakePhotoButton?.imageEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 15, right: -12)
        shareTakePhotoButton?.titleEdgeInsets = UIEdgeInsets.init(top: 25, left: -15, bottom: 0, right: 15)
        shareTakePhotoButton?.setImage(UIImage(systemName: "camera.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        shareTakePhotoButton?.setImage(UIImage(systemName: "camera.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        shareTakePhotoButton?.addTarget(self, action: #selector(handleShareTakePhotoButtonPressed), for: .touchUpInside)
        shareTakePhotoButton?.layer.cornerRadius = 5.0
        shareTakePhotoButton?.layer.masksToBounds = true
        shareTakePhotoButton?.layer.borderColor = UIColor(red: 200.0/255.0, green: 200.0/255.0, blue: 205.0/255.0, alpha: 1.0).cgColor
        shareTakePhotoButton?.layer.borderWidth = 0.5
        addSubview(shareTakePhotoButton!)
        BDUtils.setButtonTitleColor(shareTakePhotoButton!)
        //
        shareFileButton = UIButton()
        shareFileButton?.setTitle("文件", for: .normal)
        shareFileButton?.setTitle("文件", for: .highlighted)
        shareFileButton?.titleLabel!.adjustsFontSizeToFitWidth = true
        shareFileButton?.imageEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 15, right: -12)
        shareFileButton?.titleEdgeInsets = UIEdgeInsets.init(top: 25, left: -15, bottom: 0, right: 15)
        shareFileButton?.setImage(UIImage(systemName: "folder.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        shareFileButton?.setImage(UIImage(systemName: "folder.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        shareFileButton?.addTarget(self, action: #selector(handleShareFileButtonPressed), for: .touchUpInside)
        shareFileButton?.layer.cornerRadius = 5.0
        shareFileButton?.layer.masksToBounds = true
        shareFileButton?.layer.borderColor = UIColor(red: 200.0/255.0, green: 200.0/255.0, blue: 205.0/255.0, alpha: 1.0).cgColor
        shareFileButton?.layer.borderWidth = 0.5
        addSubview(shareFileButton!)
        BDUtils.setButtonTitleColor(shareFileButton!)
        
        //
        shareLeaveMsgButton = UIButton()
        shareLeaveMsgButton?.setTitle("留言", for: .normal)
        shareLeaveMsgButton?.setTitle("留言", for: .highlighted)
        shareLeaveMsgButton?.titleLabel!.adjustsFontSizeToFitWidth = true
        shareLeaveMsgButton?.imageEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 15, right: -12)
        shareLeaveMsgButton?.titleEdgeInsets = UIEdgeInsets.init(top: 25, left: -15, bottom: 0, right: 15)
        shareLeaveMsgButton?.setImage(UIImage(systemName: "message.badge.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        shareLeaveMsgButton?.setImage(UIImage(systemName: "message.badge.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        shareLeaveMsgButton?.addTarget(self, action: #selector(handleShareLeaveMsgButtonPressed), for: .touchUpInside)
        shareLeaveMsgButton?.layer.cornerRadius = 5.0
        shareLeaveMsgButton?.layer.masksToBounds = true
        shareLeaveMsgButton?.layer.borderColor = UIColor(red: 200.0/255.0, green: 200.0/255.0, blue: 205.0/255.0, alpha: 1.0).cgColor
        shareLeaveMsgButton?.layer.borderWidth = 0.5
        addSubview(shareLeaveMsgButton!)
        BDUtils.setButtonTitleColor(shareLeaveMsgButton!)
        //
        shareRateButton = UIButton()
        shareRateButton?.setTitle("评价", for: .normal)
        shareRateButton?.setTitle("评价", for: .highlighted)
        shareRateButton?.titleLabel!.adjustsFontSizeToFitWidth = true
        shareRateButton?.imageEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 15, right: -12)
        shareRateButton?.titleEdgeInsets = UIEdgeInsets.init(top: 25, left: -15, bottom: 0, right: 15)
        shareRateButton?.setImage(UIImage(systemName: "star.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        shareRateButton?.setImage(UIImage(systemName: "star.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        shareRateButton?.addTarget(self, action: #selector(handleShareRateButtonPressed), for: .touchUpInside)
        shareRateButton?.layer.cornerRadius = 5.0
        shareRateButton?.layer.masksToBounds = true
        shareRateButton?.layer.borderColor = UIColor(red: 200.0/255.0, green: 200.0/255.0, blue: 205.0/255.0, alpha: 1.0).cgColor
        shareRateButton?.layer.borderWidth = 0.5
        addSubview(shareRateButton!)
        BDUtils.setButtonTitleColor(shareRateButton!)
        //
        
    }
    
    func setupConstraints() {
        // debugPrint("\(#function)")
        topLineView?.frame = CGRectMake(0.0, 0.0, self.bounds.size.width, 0.5)
        //
        sharePickPhotoButton?.frame = CGRectMake(mButtonMargin!,
                                                 SHAREMORE_ITEMS_TOP_MARGIN,
                                                 SHAREMORE_ITEMS_WIDTH,
                                                 SHAREMORE_ITEMS_HEIGHT)
        //
        shareTakePhotoButton?.frame = CGRectMake(mButtonMargin! * 2 + SHAREMORE_ITEMS_WIDTH,
                                                 SHAREMORE_ITEMS_TOP_MARGIN,
                                                 SHAREMORE_ITEMS_WIDTH,
                                                 SHAREMORE_ITEMS_HEIGHT)
        //
        shareFileButton?.frame = CGRectMake(mButtonMargin! * 3 + SHAREMORE_ITEMS_WIDTH * 2,
                                            SHAREMORE_ITEMS_TOP_MARGIN,
                                            SHAREMORE_ITEMS_WIDTH,
                                            SHAREMORE_ITEMS_HEIGHT)
        //
        shareLeaveMsgButton?.frame = CGRectMake(mButtonMargin! * 4 + SHAREMORE_ITEMS_WIDTH * 3,
                                                SHAREMORE_ITEMS_TOP_MARGIN,
                                                SHAREMORE_ITEMS_WIDTH,
                                                SHAREMORE_ITEMS_HEIGHT)
        //
        shareRateButton?.frame = CGRectMake(mButtonMargin!,
                                            SHAREMORE_ITEMS_TOP_MARGIN * 2 + SHAREMORE_ITEMS_HEIGHT,
                                            SHAREMORE_ITEMS_WIDTH,
                                            SHAREMORE_ITEMS_HEIGHT)
        // TODO: 暂时隐藏，待完善之后，再发布
        shareFileButton?.isHidden = true
        shareRateButton?.frame = shareLeaveMsgButton!.frame
        shareLeaveMsgButton?.frame = shareFileButton!.frame
    }
    
    // MARK:
    @objc func handleSharePickPhotoButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.sharePickPhotoButtonPressed()
    }
    
    @objc func handleShareTakePhotoButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.shareTakePhotoButtonPressed()
    }
    
    @objc func handleShareFileButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.shareFileButtonPressed()
    }
    
    @objc func handleShareLeaveMsgButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.shareLeaveMsgButtonPressed()
    }
    
    @objc func handleShareRateButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.shareRateButtonPressed()
    }
    
    // MARK:
    
    public func hideRateButton() {
        // debugPrint("\(#function)")
    }
    
    public func hideFAQButton() {
        // debugPrint("\(#function)")
    }
    
    // MARK: 切换机器人 & 人工
    
    public func switchToRobot() {
        // debugPrint("plusview: \(#function)")
        //
        shareLeaveMsgButton?.frame = sharePickPhotoButton!.frame
        shareRateButton?.frame = shareTakePhotoButton!.frame
        //
        sharePickPhotoButton?.isHidden = true
        shareTakePhotoButton?.isHidden = true
        shareFileButton?.isHidden = true
    }
    
    public func switchToAgent() {
        // debugPrint("plusview: \(#function)")
        sharePickPhotoButton?.isHidden = false
        shareTakePhotoButton?.isHidden = false
        
        shareFileButton?.isHidden = true // TODO: 暂时隐藏，待完善之后放开
        shareRateButton?.frame = shareLeaveMsgButton!.frame
        shareLeaveMsgButton?.frame = shareFileButton!.frame
        
    }
    
}


//
extension UIColor {
    
    func fromRGB(rgbValue: Int) -> UIColor {
        return UIColor(red: ((CGFloat)((rgbValue & 0xFF0000) >> 16))/255.0, green: ((CGFloat)((rgbValue & 0xFF00) >> 8))/255.0, blue: ((CGFloat)(rgbValue & 0xFF))/255.0, alpha: 1.0)
    }
}
