//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/12.
//

import UIKit

class BDRateViewController: UIViewController {
    
    var upButton: UIButton?
    var downButton: UIButton?

    var downChoiceButton1: UIButton?
    var downChoiceButton2: UIButton?
    var downChoiceButton3: UIButton?
    var downChoiceButton4: UIButton?

    var threadTid: String?
    var isSolved: Bool = false
    var note: String?

    var mIsPush: Bool = false
    var forceEnableBackGesture: Bool = false
    
    let deviceHeight = UIScreen.main.bounds.size.height
    let deviceWidth = UIScreen.main.bounds.size.width
    
    public func initWithThreadTid(_ tid: String, withPush isPush: Bool) {
        print("1.\(#function), threadTid: \(tid)")
        self.threadTid = tid
        self.isSolved = false
        self.note = ""
        self.mIsPush = isPush
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        print("2.\(#function)")
        self.title = "服务评价"
        self.view.backgroundColor = UIColor.white
        if #available(iOS 13.0, *) {
            let mode = UITraitCollection.current.userInterfaceStyle
            if mode == .dark {
                self.view.backgroundColor = UIColor.black
            } else {
                self.view.backgroundColor = UIColor.white
            }
        }
        
        if !self.mIsPush {
            self.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(handleCloseButtonEvent))
        }
        self.forceEnableBackGesture = true
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("3.\(#function)")
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        print("4.\(#function)")
        // Initialize and layout subviews
        initRateRobotView()
        //
        debugPrint("height：\(self.view.frame.size.height)")
    }
    

    
    // 初始化评价机器人界面
    func initRateRobotView() {
        self.view.frame = CGRect(x: self.view.frame.origin.x, y: deviceHeight / 3, width: deviceWidth, height: deviceHeight * 2 / 3)
        self.view.layer.cornerRadius = 20.0
        
        let titleLabel = UILabel(frame: CGRect(x: (deviceWidth - 100) / 2, y: 20, width: 100, height: 20))
        titleLabel.text = "服务评价"
        self.view.addSubview(titleLabel)
        
        let closeButton = UIButton(frame: CGRect(x: deviceWidth - 40, y: 15, width: 30, height: 30))
        closeButton.setImage(UIImage(systemName: "xmark.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        closeButton.setImage(UIImage(systemName: "xmark.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        closeButton.addTarget(self, action: #selector(closeButtonClicked), for: .touchUpInside)
        self.view.addSubview(closeButton)
        BDUtils.setButtonTitleColor(closeButton)
        
        let tipLabel = UILabel(frame: CGRect(x: 20, y: 50, width: deviceWidth - 40, height: 40))
        tipLabel.text = "机器人是否解决您的问题？"
        tipLabel.textAlignment = .center
        self.view.addSubview(tipLabel)
        
        upButton = UIButton(frame: CGRect(x: (deviceWidth - 300) / 3, y: 100, width: 150, height: 40))
        upButton?.setImage(UIImage(systemName: "hand.thumbsup"), for: .normal)
        upButton?.setImage(UIImage(systemName: "hand.thumbsup.fill"), for: .selected)
        upButton?.setImage(UIImage(systemName: "hand.thumbsup.fill"), for: .highlighted)
        upButton?.setTitle("已解决", for: .normal)
        upButton?.setTitle("已解决(✅)", for: .selected)
        upButton?.addTarget(self, action: #selector(upButtonClicked), for: .touchUpInside)
        upButton?.layer.cornerRadius = 20.0
        upButton?.layer.masksToBounds = true
        upButton?.layer.borderColor = UIColor.systemGray.cgColor
        upButton?.layer.borderWidth = 0.5
        self.view.addSubview(upButton!)
        BDUtils.setButtonTitleColor(upButton!)
        
        downButton = UIButton(frame: CGRect(x: (deviceWidth - 300) * 2 / 3 + 150, y: 100, width: 150, height: 40))
        downButton?.setImage(UIImage(systemName: "hand.thumbsdown"), for: .normal)
        downButton?.setImage(UIImage(systemName: "hand.thumbsdown.fill"), for: .selected)
        downButton?.setImage(UIImage(systemName: "hand.thumbsdown.fill"), for: .highlighted)
        downButton?.setTitle("未解决", for: .normal)
        downButton?.setTitle("未解决(✅)", for: .selected)
        downButton?.addTarget(self, action: #selector(downButtonClicked), for: .touchUpInside)
        downButton?.layer.cornerRadius = 20.0
        downButton?.layer.masksToBounds = true
        downButton?.layer.borderColor = UIColor.systemGray.cgColor
        downButton?.layer.borderWidth = 0.5
        self.view.addSubview(downButton!)
        BDUtils.setButtonTitleColor(downButton!)
        
        downChoiceButton1 = UIButton(frame: CGRect(x: (deviceWidth - 300) / 3, y: 155, width: 150, height: 40))
        downChoiceButton1?.tag = 1
        downChoiceButton1?.isHidden = true
        downChoiceButton1?.setTitle("答非所问", for: .normal)
        downChoiceButton1?.setTitle("答非所问(✅)", for: .selected)
        downChoiceButton1?.setTitleColor(UIColor.systemBlue, for: .normal)
        downChoiceButton1?.addTarget(self, action: #selector(selectChoiceButtonClicked), for: .touchUpInside)
        downChoiceButton1?.layer.cornerRadius = 20.0
        downChoiceButton1?.layer.masksToBounds = true
        downChoiceButton1?.layer.borderColor = UIColor.systemGray.cgColor
        downChoiceButton1?.layer.borderWidth = 0.5
        self.view.addSubview(downChoiceButton1!)
        
        downChoiceButton2 = UIButton(frame: CGRect(x: (deviceWidth - 300) * 2 / 3 + 150, y: 155, width: 150, height: 40))
        downChoiceButton2?.tag = 2
        downChoiceButton2?.isHidden = true
        downChoiceButton2?.setTitle("理解能力差", for: .normal)
        downChoiceButton2?.setTitle("理解能力差(✅)", for: .selected)
        downChoiceButton2?.setTitleColor(UIColor.systemBlue, for: .normal)
        downChoiceButton2?.addTarget(self, action: #selector(selectChoiceButtonClicked), for: .touchUpInside)
        downChoiceButton2?.layer.cornerRadius = 20.0
        downChoiceButton2?.layer.masksToBounds = true
        downChoiceButton2?.layer.borderColor = UIColor.systemGray.cgColor
        downChoiceButton2?.layer.borderWidth = 0.5
        self.view.addSubview(downChoiceButton2!)
        
        downChoiceButton3 = UIButton(frame: CGRect(x: (deviceWidth - 300)/3, y: 210, width: 150, height: 40))
        downChoiceButton3?.tag = 3
        downChoiceButton3?.isHidden = true
        downChoiceButton3?.setTitle("问题不能回答", for: .normal)
        downChoiceButton3?.setTitle("问题不能回答(✅)", for: .selected)
        downChoiceButton3?.setTitleColor(.systemBlue, for: .normal)
        downChoiceButton3?.addTarget(self, action: #selector(selectChoiceButtonClicked(_:)), for: .touchUpInside)
        downChoiceButton3?.layer.cornerRadius = 20.0
        downChoiceButton3?.layer.masksToBounds = true
        // downChoiceButton3?.layer.borderColor = UIColor(white: 0.0, alpha: 0.2).cgColor
        downChoiceButton3?.layer.borderColor = UIColor.systemGray.cgColor
        downChoiceButton3?.layer.borderWidth = 0.5
        self.view.addSubview(downChoiceButton3!)

        downChoiceButton4 = UIButton(frame: CGRect(x: (deviceWidth - 300)*2/3 + 150, y: 210, width: 150, height: 40))
        downChoiceButton4?.tag = 4
        downChoiceButton4?.isHidden = true
        downChoiceButton4?.setTitle("不礼貌", for: .normal)
        downChoiceButton4?.setTitle("不礼貌(✅)", for: .selected)
        downChoiceButton4?.setTitleColor(.systemBlue, for: .normal)
        downChoiceButton4?.addTarget(self, action: #selector(selectChoiceButtonClicked(_:)), for: .touchUpInside)
        downChoiceButton4?.layer.cornerRadius = 20.0
        downChoiceButton4?.layer.masksToBounds = true
        // downChoiceButton4?.layer.borderColor = UIColor(white: 0.0, alpha: 0.2).cgColor
        downChoiceButton4?.layer.borderColor = UIColor.systemGray.cgColor
        downChoiceButton4?.layer.borderWidth = 0.5
        self.view.addSubview(downChoiceButton4!)
        
        let submitButton = UIButton(frame: CGRect(x: 10, y: (deviceHeight*2/3 - 140), width: deviceWidth - 20, height: 40))
        submitButton.backgroundColor = UIColor.systemOrange
        submitButton.setTitle("提交评价", for: .normal)
        submitButton.setTitleColor(UIColor.white, for: .normal)
        submitButton.addTarget(self, action: #selector(submitButtonClicked), for: .touchUpInside)
        submitButton.layer.cornerRadius = 20.0
        submitButton.layer.masksToBounds = true
        self.view.addSubview(submitButton)
    }

    // 关闭按钮点击事件
    @objc func closeButtonClicked() {
        self.dismiss(animated: true, completion: nil)
    }
    
    @objc func upButtonClicked() {
        let isSelected = upButton!.isSelected
        upButton!.isSelected = !isSelected
        
        if upButton!.isSelected {
            self.isSolved = true
            
            downButton!.isSelected = false
            
            downChoiceButton1!.isHidden = true
            downChoiceButton2!.isHidden = true
            downChoiceButton3!.isHidden = true
            downChoiceButton4!.isHidden = true
        }
    }

    @objc func downButtonClicked() {
        let isSelected = downButton!.isSelected
        downButton!.isSelected = !isSelected
        
        if downButton!.isSelected {
            self.isSolved = false
            
            upButton!.isSelected = false
            
            downChoiceButton1!.isHidden = false
            downChoiceButton2!.isHidden = false
            downChoiceButton3!.isHidden = false
            downChoiceButton4!.isHidden = false
        } else {
            downChoiceButton1!.isHidden = true
            downChoiceButton2!.isHidden = true
            downChoiceButton3!.isHidden = true
            downChoiceButton4!.isHidden = true
        }
    }

    @objc func selectChoiceButtonClicked(_ button: UIButton) {
        print(#function)
        
        let isSelected = button.isSelected
        button.isSelected = !isSelected
        
        self.note = ""
        if !isSolved {
            if downChoiceButton1!.isSelected {
                self.note = "\(self.note!) 答非所问"
            }
            if downChoiceButton2!.isSelected {
                self.note = "\(self.note!) 理解能力差"
            }
            if downChoiceButton3!.isSelected {
                self.note = "\(self.note!) 问题不能回答"
            }
            if downChoiceButton4!.isSelected {
                self.note = "\(self.note!) 不礼貌"
            }
        }
    }

    // 提交按钮点击事件
    @objc func submitButtonClicked() {
        
        BDUIApis.showTip(with: self, message: "评价提交中，请稍后")
        
        BDCoreApis.rate(tid: threadTid,
                        score: isSolved ? 5 : 0,
                        note: isSolved ? "" : self.note,
                        invite: false) { rateResult in
            
            BDToast.show(message: rateResult.message!)
            self.navigationController?.dismiss(animated: true)
        } onFailure: { error in
            BDToast.show(message: error)
        }
    }

    @objc func handleCloseButtonEvent() {
        self.navigationController?.dismiss(animated: true)
    }
    
    @objc func handleBackButtonEvent() {
        self.navigationController?.popViewController(animated: true)
    }
    
}
