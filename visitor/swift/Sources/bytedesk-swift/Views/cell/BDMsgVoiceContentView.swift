//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/10.
//

import UIKit

class BDMsgVoiceContentView: BDMsgBaseContentView {
    
    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("BDMsgVoiceContentView \(#function)")

        setupView()
//        setupConstraints()
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    override func setupView() {
        // debugPrint("\(#function)")
    }
    
//    override func setupConstraints() {
//        // debugPrint("\(#function)")
//    }
    
    
}
