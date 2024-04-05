//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/6.
//

import UIKit

protocol BDEmotionFaceViewDelegate: AnyObject {
    func emotionFaceButtonPressed(_ tag: Int);
}

class BDEmotionFaceView: UIView {
    
    weak var delegate: BDEmotionFaceViewDelegate?
    
    let EMOTION_LEFT_MARGIN = 13.5
    let EMOTION_TOP_MARGIN = 19.0
    let EMOTION_FACE_WIDTH = 30.0
    let EMOTION_FACE_HEIGHT = 30.0
    
    var mScrollViewWidth: CGFloat?
    var mEmotionFaceMargin: CGFloat?
    
    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("BDEmotionFaceView \(#function)")

        setupView()
        setupConstraints()
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    func setupView() {
        // debugPrint("BDEmotionFaceView \(#function)")
        mScrollViewWidth = UIScreen.main.bounds.size.width
        mEmotionFaceMargin = (mScrollViewWidth! - EMOTION_FACE_WIDTH*7)/8
        
        // FIXME: 加载大量图片容易引起界面卡顿，待优化
        //横向旋转之后，表情分布需重新调整
        for page in 0..<5 {
            for i in 0..<3 {
                for j in 0..<7 {
//                    // debugPrint("page:\(page), i:\(i), j:\(j)")
                    let a = mScrollViewWidth! * CGFloat(page)
                    let b = a + mEmotionFaceMargin!
                    let c = (EMOTION_FACE_WIDTH + mEmotionFaceMargin!) * CGFloat(j)
                    let x = b + c
                    //
                    let d = (EMOTION_FACE_HEIGHT + EMOTION_TOP_MARGIN) * CGFloat(i)
                    let y = EMOTION_TOP_MARGIN + d
                    let button = UIButton(frame: CGRectMake(x, y, EMOTION_FACE_WIDTH, EMOTION_FACE_HEIGHT))
//                    // debugPrint("button.frame: \(button.frame)")
                    let tag = 21*page + i*7 + (j+1)
                    button.setBackgroundImage(UIImage(named: String(format: "Expression_%d", tag), in: .module, with: nil), for: .normal)
                    button.tag = tag
                    self.addSubview(button)
                    //
                    if (i*7 + (j+1) == 21) {
                        button.setBackgroundImage(UIImage(named: "DeleteEmoticonBtn_ios7", in: .module, with: nil), for: .normal)
                        button.setBackgroundImage(UIImage(named: "DeleteEmoticonBtnHL_ios7", in: .module, with: nil), for: .highlighted)
                    }
                    button.addTarget(self, action: #selector(handleEmotionFacePressed(_:)), for: .touchUpInside)
                }
            }
        }
    }
    
    func setupConstraints() {
        // debugPrint("\(#function)")
    }
    
    @objc func handleEmotionFacePressed(_ sender: UIButton) {
        // debugPrint("faceview: \(#function), tag: \(sender.tag)")
        
        delegate?.emotionFaceButtonPressed(sender.tag)
    }
    
}
