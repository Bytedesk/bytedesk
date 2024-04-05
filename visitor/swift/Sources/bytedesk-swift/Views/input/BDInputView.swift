//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit


protocol BDInputViewDelegate: AnyObject {
    //
    func switchVoiceButtonPressed()
    func switchAgentButtonPressed()
    func switchEmotionButtonPressed()
    func switchPlusButtonPressed()
    func sendMessage(_ content: String)
    //
    func recordVoiceButtonTouchDown()
    func recordVoiceButtonTouchUpInside()
    func recordVoiceButtonTouchUpOutside()
    func recordVoiceButtonTouchDragInside()
    func recordVoiceButtonTouchDragOutside()
}

class BDInputView: UIView, UITextViewDelegate {
    
    let INPUTBAR_HEIGHT = 60.0
    let INPUTBAR_MAX_HEIGHT = 200.0
    let INPUTBAR_TOP_MARGIN = 10.0

    let INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT = 36.0

    let INPUTBAR_SWITCH_EMOTION_PLUS_TOP_MARGIN = 10.0
    let INPUTBAR_SWITCH_EMOTION_LEFT_MARGIN = 5.0
    let INPUTBAR_SWITCH_EMOTION_RIGHT_MARGIN = 3.0
    let INPUTBAR_SWITCH_PLUS_RIGHT_MARGIN = 6.0

    let INPUTBAR_SWITCH_VOICE_LEFT_MARGIN = 8.0
    let INPUTBAR_SWITCH_VOICE_TOP_MARGIN  = 10.0
    let INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT = 36.0

    let INPUTBAR_SWITCH_AGENT_LEFT_MARGIN = 8.0
    let INPUTBAR_SWITCH_AGENT_TOP_MARGIN  = 10.0
    let INPUTBAR_SWITCH_AGENT_BUTTON_WIDTH_HEIGHT = 36.0

    let INPUTBAR_INPUT_TEXTVIEW_TOP_MARGIN = 10.0
    let INPUTBAR_INPUT_TEXTVIEW_LEFT_MARGIN = 5.0

    let INPUTBAR_INPUT_TEXTVIEW_HEIGHT = 34.5
    let INPUTBAR_INPUT_TEXTVIEW_MAX_HEIGHT = 188.0

    let INPUTBAR_RECORD_VOICE_BUTTON_TOP_MARGIN = 10.0
    let INPUTBAR_RECORD_VOICE_BUTTON_LEFT_MARGIN = 5.0
    let INPUTBAR_RECORD_VOICE_HEIGHT   = 34.5
    
    weak var delegate: BDInputViewDelegate?
    
    var inputToolbar: UIToolbar?
    var switchVoiceButton: UIButton?
    var switchAgentButton: UIButton?
    var recordVoiceButton: UIButton?
    var inputTextView: UITextView?
    var switchEmotionButton: UIButton?
    var switchPlusButton: UIButton?
    //
    var inputTextViewMaxHeight: CGFloat?
    var inputTextViewMaxLinesCount: CGFloat?
    var inputTextViewPreviousTextHeight: CGFloat?
    
    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("\(#function)")
        //
        inputTextViewMaxHeight = 0
        inputTextViewMaxLinesCount = 0
        inputTextViewPreviousTextHeight = 0
        //
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
        inputToolbar = UIToolbar()
//        inputToolbar?.backgroundColor = UIColor.red
        addSubview(inputToolbar!)
        //
        switchVoiceButton = UIButton()
        switchVoiceButton?.setImage(UIImage(systemName: "mic.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        switchVoiceButton?.setImage(UIImage(systemName: "mic.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        switchVoiceButton?.addTarget(self, action: #selector(handleSwitchVoiceButtonClicked), for: .touchUpInside)
        addSubview(switchVoiceButton!)
        // TODO: 暂时隐藏，待录音完善之后，再发布
        switchVoiceButton?.isHidden = true
        //
        switchAgentButton = UIButton()
        switchAgentButton?.setImage(UIImage(systemName: "person.and.arrow.left.and.arrow.right", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        switchAgentButton?.setImage(UIImage(systemName: "person.and.arrow.left.and.arrow.right.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
//        switchAgentButton?.backgroundColor = UIColor.blue
        switchAgentButton?.addTarget(self, action: #selector(handleSwitchAgentButtonPressed), for: .touchUpInside)
        addSubview(switchAgentButton!)
        switchAgentButton?.isHidden = true
        //
        recordVoiceButton = UIButton()
        recordVoiceButton?.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        recordVoiceButton?.titleLabel?.font = UIFont.systemFont(ofSize: 14.0)
        recordVoiceButton?.setTitle("按住 说话", for: .normal)
        recordVoiceButton?.setTitle("松开 取消", for: .highlighted)
        recordVoiceButton?.layer.cornerRadius = 5.0
        recordVoiceButton?.layer.masksToBounds = true
        recordVoiceButton?.layer.borderColor = UIColor.systemGray.cgColor
        recordVoiceButton?.layer.borderWidth = 0.5
        recordVoiceButton?.addTarget(self, action: #selector(handleRecordVoiceButtonTouchDown), for: .touchDown)
        recordVoiceButton?.addTarget(self, action: #selector(handleRecordVoiceButtonTouchUpInside), for: .touchUpInside)
        recordVoiceButton?.addTarget(self, action: #selector(handleRecordVoiceButtonTouchUpOutside), for: .touchUpOutside)
        recordVoiceButton?.addTarget(self, action: #selector(handleRecordVoiceButtonTouchDragInside), for: .touchDragInside)
        recordVoiceButton?.addTarget(self, action: #selector(handleRecordVoiceButtonTouchDragOutside), for: .touchDragOutside)
        BDUtils.setButtonTitleColor(recordVoiceButton!)
        addSubview(recordVoiceButton!)
        recordVoiceButton?.isHidden = true
        //
        inputTextView = UITextView()
        inputTextView!.contentInset = UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: 0.0);
        inputTextView!.isScrollEnabled = true;
        inputTextView!.scrollsToTop = false;
        inputTextView!.isUserInteractionEnabled = true;
//        inputTextView.textColor = [UIColor blackColor];
        inputTextView!.keyboardAppearance = UIKeyboardAppearance.default
        inputTextView!.keyboardType = UIKeyboardType.default
        inputTextView!.returnKeyType = UIReturnKeyType.send
        inputTextView!.font = UIFont.systemFont(ofSize: 17)
        //
        inputTextView!.layer.cornerRadius = 5.0
        inputTextView!.layer.borderWidth = 0.7
        inputTextView!.layer.borderColor = UIColor.init(cgColor: CGColor(red: 200.0/255.0, green: 200.0/255.0, blue: 205.0/255.0, alpha: 1.0)).cgColor
        inputTextView?.delegate = self
        addSubview(inputTextView!)
        //
        switchEmotionButton = UIButton()
        switchEmotionButton?.setImage(UIImage(systemName: "face.smiling", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        switchEmotionButton?.setImage(UIImage(systemName: "face.smiling.inverse", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        switchEmotionButton?.addTarget(self, action: #selector(handleSwitchEmotionButtonPressed), for: .touchUpInside)
        addSubview(switchEmotionButton!)
        //
        switchPlusButton = UIButton()
        switchPlusButton?.setImage(UIImage(systemName: "plus.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
        switchPlusButton?.setImage(UIImage(systemName: "plus.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
//        switchPlusButton?.backgroundColor = UIColor.cyan
        switchPlusButton?.addTarget(self, action: #selector(handleSwitchPlusButtonPressed), for: .touchUpInside)
        addSubview(switchPlusButton!)
    }
    
    func setupConstraints() {
        // debugPrint("\(#function)")
        //
//        inputToolbar!.translatesAutoresizingMaskIntoConstraints = false
        var frame = self.bounds
        frame.origin.y = 0.5
        frame.size.height = INPUTBAR_HEIGHT
        inputToolbar?.frame = frame
        //
        switchVoiceButton?.frame = CGRectMake(INPUTBAR_SWITCH_VOICE_LEFT_MARGIN,
                                              INPUTBAR_SWITCH_VOICE_TOP_MARGIN,
                                              INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT,
                                              INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT)
        
        //
        switchAgentButton?.frame = CGRectMake(INPUTBAR_SWITCH_AGENT_LEFT_MARGIN,
                                              INPUTBAR_SWITCH_AGENT_TOP_MARGIN,
                                              INPUTBAR_SWITCH_AGENT_BUTTON_WIDTH_HEIGHT,
                                              INPUTBAR_SWITCH_AGENT_BUTTON_WIDTH_HEIGHT)

        //
        inputTextView?.frame = CGRectMake( (INPUTBAR_SWITCH_VOICE_LEFT_MARGIN)
                                            + INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT + INPUTBAR_INPUT_TEXTVIEW_LEFT_MARGIN * 1.5,
                                            INPUTBAR_INPUT_TEXTVIEW_TOP_MARGIN,
                                            self.bounds.size.width - INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT - INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT*3,
                                            INPUTBAR_INPUT_TEXTVIEW_HEIGHT)
        var recordVoiceButtonFrame = inputTextView!.frame;
        recordVoiceButtonFrame.origin.x -= 5.0;
        recordVoiceButtonFrame.origin.y -= 1.0;
        recordVoiceButton?.frame = recordVoiceButtonFrame
        //
        switchEmotionButton?.frame = CGRectMake(self.bounds.size.width - INPUTBAR_SWITCH_PLUS_RIGHT_MARGIN - INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT * 2 - INPUTBAR_SWITCH_EMOTION_RIGHT_MARGIN * 2,
                                                INPUTBAR_SWITCH_EMOTION_PLUS_TOP_MARGIN,
                                                INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT,
                                                INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT)
        
        //
        switchPlusButton?.frame = CGRectMake(self.bounds.size.width - INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT - INPUTBAR_SWITCH_PLUS_RIGHT_MARGIN * 2,
                                             INPUTBAR_SWITCH_EMOTION_PLUS_TOP_MARGIN,
                                             INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT,
                                             INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT)
    }
    
    override func becomeFirstResponder() -> Bool {
        return self.inputTextView!.becomeFirstResponder()
    }
    
    override func resignFirstResponder() -> Bool {
        return self.inputTextView!.resignFirstResponder()
    }
    

    // MARK: selectors
    
    @objc func handleSwitchVoiceButtonClicked() {
        // debugPrint("\(#function)")
        delegate?.switchVoiceButtonPressed()
        //
        if self.inputTextView!.isFirstResponder {
            self.switchVoiceButton!.setImage(UIImage(systemName: "mic.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
            self.switchVoiceButton!.setImage(UIImage(systemName: "mic.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        } else {
            self.switchVoiceButton!.setImage(UIImage(systemName: "keyboard"), for: .normal)
            self.switchVoiceButton!.setImage(UIImage(systemName: "keyboard.fill"), for: .highlighted)
            ////
            self.switchEmotionButton!.setImage(UIImage(systemName: "face.smiling", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
            self.switchEmotionButton!.setImage(UIImage(systemName: "face.smiling.inverse", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        }
    }
    
    @objc func handleSwitchAgentButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.switchAgentButtonPressed()
    }
    
    @objc func handleSwitchEmotionButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.switchEmotionButtonPressed()
    }
    
    @objc func handleSwitchPlusButtonPressed() {
        // debugPrint("\(#function)")
        delegate?.switchPlusButtonPressed()
    }
    
    @objc func handleRecordVoiceButtonTouchDown() {
        // debugPrint("\(#function)")
        delegate?.recordVoiceButtonTouchDown()
    }
    
    @objc func handleRecordVoiceButtonTouchUpInside() {
        // debugPrint("\(#function)")
        delegate?.recordVoiceButtonTouchUpInside()
    }
    
    @objc func handleRecordVoiceButtonTouchUpOutside() {
        // debugPrint("\(#function)")
        delegate?.recordVoiceButtonTouchUpOutside()
    }
    
    @objc func handleRecordVoiceButtonTouchDragInside() {
        // debugPrint("\(#function)")
        delegate?.recordVoiceButtonTouchDragInside()
    }
    
    @objc func handleRecordVoiceButtonTouchDragOutside() {
        // debugPrint("\(#function)")
        delegate?.recordVoiceButtonTouchDragOutside()
    }
    
    // MARK: UITextViewDelegate
    
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        // debugPrint("\(#function)")
        
        if (!textView.hasText && text.isEmpty) {
            return false
        }
        
        if (text == "\n") {
            let content = textView.text.trimmingCharacters(in: .whitespacesAndNewlines)
            if (content.isEmpty) {
                return false
            }
            delegate?.sendMessage(content)
//            textViewDidChange(textView);
            return false
        }
        return true
    }
    
    func textViewDidChange(_ textView: UITextView) {
        // debugPrint("\(#function)")
        
        let previewHeight = getInputTextViewPreviousTextHeight()
        let textViewHeight = getInputTextViewHeight()
        if (textViewHeight > 150) {
            return
        }
        let deltaHeight = textViewHeight - previewHeight
        inputTextViewPreviousTextHeight = textViewHeight
        //
        UIView.animate(withDuration: 0.20) {
            var barFrame = self.inputToolbar?.frame
            barFrame?.size.height += deltaHeight
            if (barFrame!.size.height < CGFloat(self.INPUTBAR_HEIGHT)) {
                barFrame!.size.height = self.INPUTBAR_HEIGHT
                barFrame!.origin.y = (self.switchVoiceButton?.frame.origin.y)! - self.INPUTBAR_SWITCH_VOICE_TOP_MARGIN
            }
            else if (barFrame!.size.height > self.INPUTBAR_MAX_HEIGHT) {
                barFrame!.size.height = self.INPUTBAR_MAX_HEIGHT
                barFrame!.origin.y = self.switchVoiceButton!.frame.origin.y - 161.0
            }
            else if (previewHeight == textViewHeight
                     && textViewHeight > self.INPUTBAR_MAX_HEIGHT
                     && deltaHeight == 0.0) {
                barFrame!.size.height = self.INPUTBAR_MAX_HEIGHT
                barFrame!.origin.y = self.switchVoiceButton!.frame.origin.y - 161.0
            }
            else {
                barFrame!.origin.y -= deltaHeight
            }
            self.inputToolbar!.frame = barFrame!
            
            ///
            var textViewFrame = self.inputTextView?.frame
            textViewFrame?.size.height += deltaHeight
            
            if (textViewFrame!.size.height < self.INPUTBAR_INPUT_TEXTVIEW_HEIGHT) {
                textViewFrame!.size.height = self.INPUTBAR_INPUT_TEXTVIEW_HEIGHT
                textViewFrame!.origin.y = self.recordVoiceButton!.frame.origin.y + 1
            }
            else if(textViewFrame!.size.height > self.INPUTBAR_INPUT_TEXTVIEW_MAX_HEIGHT) {
                textViewFrame!.size.height = self.INPUTBAR_INPUT_TEXTVIEW_MAX_HEIGHT;
                textViewFrame!.origin.y = self.recordVoiceButton!.frame.origin.y - 155.0;
            }
            else if (previewHeight == textViewHeight
                     && textViewHeight > self.INPUTBAR_MAX_HEIGHT
                      && deltaHeight == 0.0) {
                textViewFrame!.size.height = self.INPUTBAR_INPUT_TEXTVIEW_MAX_HEIGHT;
                textViewFrame!.origin.y = self.recordVoiceButton!.frame.origin.y - 155.0;
            }
            else {
                 textViewFrame!.origin.y -= deltaHeight;
            }
            self.inputTextView!.frame = textViewFrame!
        }
    }
    
    func getInputTextViewPreviousTextHeight() -> CGFloat {
        if (inputTextViewPreviousTextHeight! == 0) {
            inputTextViewPreviousTextHeight = getInputTextViewHeight()
        }
        return inputTextViewPreviousTextHeight!
    }
    
    func getInputTextViewHeight() -> CGFloat {
        let height = inputTextView?.sizeThatFits(CGSizeMake((inputTextView?.frame.size.width)!, CGFloat(Float.greatestFiniteMagnitude))).height
        return CGFloat(height!)
    }
    
    // MARK: 切换机器人 & 人工
    
    func switchToRobot() {
        // debugPrint("inputview \(#function)")
        switchVoiceButton?.isHidden = true
        switchAgentButton?.isHidden = false
        switchEmotionButton?.isHidden = true
        //
        var textFrame = self.inputTextView?.frame
        textFrame?.size.width += INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT
        self.inputTextView?.frame = textFrame!
    }
    
    func switchToAgent() {
        // debugPrint("inputview \(#function)")
        switchVoiceButton?.isHidden = false
        switchAgentButton?.isHidden = true
        switchEmotionButton?.isHidden = false
        //
        var textFrame = self.inputTextView?.frame
        textFrame?.size.width -= INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT
        self.inputTextView?.frame = textFrame!
    }
    
}
