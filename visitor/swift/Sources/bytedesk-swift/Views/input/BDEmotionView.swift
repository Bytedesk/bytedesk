//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit

protocol BDEmotionViewDelegate: AnyObject {
    func emotionFaceButtonPressed(_ tag: Int)
    func emotionViewSendButtonPressed()
}

class BDEmotionView: UIView, UIScrollViewDelegate, BDEmotionFaceViewDelegate {
    
    weak var delegate: BDEmotionViewDelegate?
    
    let SCROLLVIEW_HEIGHT = 150.0
    let PAGESCROLL_HEIGHT = 66.0
    let PAGESCROLL_WIDTH  = 80.0
    let SENDBUTTON_WIDTH  = 80.0
    let SENDBUTTON_HEIGHT = 40.0
    let SENDBUTTON_RIGHT_MARGIN = 15.0
    
    var topLineView: UIView?
    var scrollView: UIScrollView?
    var emotionFaceView: BDEmotionFaceView?
    var pageControl: UIPageControl?
    var sendButton: UIButton?
    
    var mScrollViewWidth: CGFloat?
    
    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("BDEmotionView \(#function)")
        mScrollViewWidth = UIScreen.main.bounds.size.width
  
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
        self.addSubview(topLineView!)
        //
        scrollView = UIScrollView()
        scrollView!.isPagingEnabled = true;
        scrollView!.contentSize = CGSizeMake(mScrollViewWidth! * 5, SCROLLVIEW_HEIGHT);
        scrollView!.showsHorizontalScrollIndicator = false;
        scrollView!.showsVerticalScrollIndicator = false;
        scrollView!.delegate = self;
//        scrollView.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;
        
        let faceView = BDEmotionFaceView.init(frame: CGRectMake(0.0, 0.0, mScrollViewWidth! * 5, SCROLLVIEW_HEIGHT))
        faceView.delegate = self
        scrollView?.addSubview(faceView)
        
        self.addSubview(scrollView!)
        //
        pageControl = UIPageControl()
        pageControl!.numberOfPages = 5;
        pageControl!.currentPage = 0;
//        pageControl!.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        pageControl?.addTarget(self, action: #selector(handlePageControlIndicatorPressed), for: .touchUpInside)
        self.addSubview(pageControl!)
        //
        sendButton = UIButton()
        sendButton?.titleLabel?.font = UIFont.systemFont(ofSize: 15)
        sendButton?.setTitleColor(UIColor.black, for: .normal)
        sendButton?.setTitle("发送", for: .normal)
        sendButton?.setBackgroundImage(UIImage(named: "EmotionsSendBtnBlue", in: .module, with: nil), for: .normal)
        sendButton?.setBackgroundImage(UIImage(named: "EmotionsSendBtnBlueHL", in: .module, with: nil), for: .highlighted)
        sendButton?.addTarget(self, action: #selector(handleEmotionViewSendButtonPressed), for: .touchUpInside)
        self.addSubview(sendButton!)
    }
    
    func setupConstraints() {
        // debugPrint("\(#function)")
        topLineView?.frame = CGRectMake(0.0, 0.0, self.bounds.size.width, 0.5)
        //
        scrollView?.frame = CGRectMake(0.0, 0.0, mScrollViewWidth!, SCROLLVIEW_HEIGHT)
        //
        pageControl?.frame = CGRectMake((mScrollViewWidth! - PAGESCROLL_WIDTH)/2,
                                        SCROLLVIEW_HEIGHT,
                                        PAGESCROLL_WIDTH,
                                        PAGESCROLL_HEIGHT)
        //
        sendButton?.frame = CGRectMake(mScrollViewWidth! - SENDBUTTON_WIDTH,
                                       SCROLLVIEW_HEIGHT + (PAGESCROLL_HEIGHT - SENDBUTTON_HEIGHT),
                                       SENDBUTTON_WIDTH,
                                       SENDBUTTON_HEIGHT)
    }
    
    @objc func handlePageControlIndicatorPressed() {
        // debugPrint("\(#function)")
        let currentIndex = pageControl!.currentPage
        scrollView?.scrollRectToVisible(CGRectMake(mScrollViewWidth! * CGFloat(currentIndex), 0, mScrollViewWidth!, SCROLLVIEW_HEIGHT), animated: true)
    }
    
    @objc func handleEmotionViewSendButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.emotionViewSendButtonPressed()
    }
    
    // MARK: UIScrollViewDelegate
    
    func scrollViewDidEndDecelerating(_ scrView: UIScrollView) {
        // debugPrint("\(#function)")
        pageControl?.currentPage = Int(scrView.contentOffset.x/mScrollViewWidth!)
        pageControl?.updateCurrentPageDisplay()
    }
        
    // MARK: BDEmotionFaceViewDelegate
    
    func emotionFaceButtonPressed(_ tag: Int) {
        // debugPrint("emoview: \(#function), tag: \(tag)")
        delegate?.emotionFaceButtonPressed(tag)
    }
    
}
