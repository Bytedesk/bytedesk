//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/7.
//

import UIKit
// ElegantEmojiPickerDelegate
extension BDChatKFViewController: BDInputViewDelegate {
    
    
    // MARK: BDInputViewDelegate
    
    func switchVoiceButtonPressed() {
        initRecordVoiceLazy()
        
        // debugPrint("\(#function)")
        if self.mInputView!.recordVoiceButton!.isHidden {
            print("\(#function) 1")
            self.mInputView!.recordVoiceButton!.isHidden = false
            self.mInputView!.inputTextView!.isHidden = true
            self.mInputView!.inputTextView!.resignFirstResponder()
        } else {
            print("\(#function) 2")
            self.mInputView!.recordVoiceButton!.isHidden = true
            self.mInputView!.inputTextView!.isHidden = false
            self.mInputView!.inputTextView!.becomeFirstResponder()
        }

        let emotionViewFrameY = mEmotionView!.frame.origin.y
        let plusViewFrameY = self.mPlusView!.frame.origin.y
        let frameHeight = self.view.frame.size.height

        if emotionViewFrameY != frameHeight {
            print("\(#function) 3")

            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0, options: .curveEaseOut, animations: {
                var tableViewContentInsets = self.mTableView!.contentInset
                tableViewContentInsets.bottom -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mTableView!.contentInset = tableViewContentInsets

                var inputViewFrame = self.mInputView!.frame
                inputViewFrame.origin.y += self.EMOTION_PLUS_VIEW_HEIGHT
                self.mInputView!.frame = inputViewFrame

                var emotionViewFrame = self.mEmotionView!.frame
                emotionViewFrame.origin.y += self.EMOTION_PLUS_VIEW_HEIGHT
                self.mEmotionView!.frame = emotionViewFrame
            }, completion: { finished in

            })
        } else if plusViewFrameY != frameHeight {
            print("\(#function) 4")

            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0, options: .curveEaseOut, animations: {
                var tableViewContentInsets = self.mTableView!.contentInset
                tableViewContentInsets.bottom -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mTableView!.contentInset = tableViewContentInsets

        //        var inputViewFrame = self.kfInputView.frame
        //        inputViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT
        //        self.kfInputView.frame = inputViewFrame

                var plusViewFrame = self.mPlusView!.frame
                plusViewFrame.origin.y += self.EMOTION_PLUS_VIEW_HEIGHT
                self.mPlusView!.frame = plusViewFrame
            }, completion: { finished in

            })
        }
    }
    
    func switchAgentButtonPressed() {
        // debugPrint("\(#function)")
    }
    
    func switchEmotionButtonPressed() {
        // debugPrint("0. \(#function)")
        initEmotionLazy()
        
        //如果输入框目前处于隐藏状态,即：显示录音button状态，则：1.隐藏录音button，2.显示输入框，3.更换switchViewButton image
        if (mInputView!.inputTextView!.isHidden) {
            // debugPrint("1. \(#function)")
            mInputView!.recordVoiceButton!.isHidden = true
            mInputView?.inputTextView?.isHidden = false
            mInputView?.switchVoiceButton?.setImage(UIImage(systemName: "mic.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
            mInputView?.switchVoiceButton?.setImage(UIImage(systemName: "mic.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        }
        //
        let inputViewFrameY = mInputView?.frame.origin.y
        let emotionViewFrameY = mEmotionView?.frame.origin.y
        let plusViewFrameY = mPlusView?.frame.origin.y
        let frameHeight = self.view.frame.size.height
        //
        //当前输入工具栏在会话页面最底部，显示表情
        if (inputViewFrameY == frameHeight - INPUTBAR_HEIGHT) {
            // debugPrint("2. \(#function)")
            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0) {
                //
                var tableViewContentInsets = self.mTableView?.contentInset
                tableViewContentInsets?.bottom += self.EMOTION_PLUS_VIEW_HEIGHT
                self.mTableView?.contentInset = tableViewContentInsets!
                //
                var inputViewFrame = self.mInputView?.frame
                inputViewFrame?.origin.y -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mInputView?.frame = inputViewFrame!
                //
                var emotionViewFrame = self.mEmotionView?.frame
                emotionViewFrame?.origin.y -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mEmotionView?.frame = emotionViewFrame!
            }
        }
        //当前显示表情扩展, 需要显示键盘
        else if (emotionViewFrameY != frameHeight) {
            // debugPrint("3. \(#function)")
            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0) {
                if (self.mInputView!.isFirstResponder) {
                    //输入框设置焦点，显示键盘
                    let _ = self.mInputView?.resignFirstResponder()
                } else {
                    //输入框设置焦点，显示键盘
                    let _ = self.mInputView?.becomeFirstResponder()
                    //隐藏emotionView
                    var emotionViewFrame = self.mEmotionView?.frame
                    emotionViewFrame?.origin.y += self.EMOTION_PLUS_VIEW_HEIGHT
                    self.mEmotionView?.frame = emotionViewFrame!
                }
            }
        }
        //当前显示plus扩展, 需要隐藏plus扩展，显示表情扩展
        else if (plusViewFrameY != frameHeight) {
            // debugPrint("4. \(#function)")
            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0) {
                //
                var plusViewFrame = self.mPlusView?.frame
                plusViewFrame?.origin.y += self.EMOTION_PLUS_VIEW_HEIGHT
                self.mPlusView?.frame = plusViewFrame!
                //
                var emotionViewFrame = self.mEmotionView?.frame
                emotionViewFrame?.origin.y -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mEmotionView?.frame = emotionViewFrame!
            }
        }
        //当前显示键盘, 需要隐藏键盘，显示kfEmotionView
        else {
            // debugPrint("5. \(#function)")
            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0) {
                //
                var tableViewContentInsets = self.mTableView?.contentInset
                tableViewContentInsets?.bottom = self.EMOTION_PLUS_VIEW_HEIGHT + self.INPUTBAR_HEIGHT
                self.mTableView?.contentInset = tableViewContentInsets!
                // 隐藏键盘
                let keyboard = self.mInputView?.inputTextView?.inputAccessoryView?.superview
                var keyboardFrame = keyboard?.frame
                keyboardFrame?.origin.y = frameHeight
                keyboard?.frame = keyboardFrame!
                //
                let _ = self.mInputView?.resignFirstResponder()
                //调整inputViewFrame
                var inputViewFrame = self.mInputView?.frame
                inputViewFrame?.origin.y = frameHeight - self.EMOTION_PLUS_VIEW_HEIGHT - self.INPUTBAR_HEIGHT
                self.mInputView?.frame = inputViewFrame!
                //显示kfPlusView
                var emotionViewFrame = self.mEmotionView?.frame
                emotionViewFrame?.origin.y -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mEmotionView?.frame = emotionViewFrame!
            }
        }
        //
        tableViewScrollToBottom(true)
        //
//        self.present(ElegantEmojiPicker(delegate: self,
//                                        configuration: ElegantConfiguration(showSearch: false, showRandom: false),
////                                        localization: ElegantLocalization(emojiCategoryTitles: ["", "",]),
//                                        sourceView: self.view), animated: true)
    }
    
    //
    func switchPlusButtonPressed() {
        // debugPrint("0. \(#function)")
        initPlusViewLazy()
        //
        if (mInputView!.inputTextView!.isHidden) {
            // debugPrint("1. \(#function)")
            mInputView!.recordVoiceButton!.isHidden = true
            mInputView?.inputTextView?.isHidden = false
            mInputView?.switchVoiceButton?.setImage(UIImage(systemName: "mic.circle", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .normal)
            mInputView?.switchVoiceButton?.setImage(UIImage(systemName: "mic.circle.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 25)), for: .highlighted)
        }
        //
        let inputViewFrameY = mInputView?.frame.origin.y
        let emotionViewFrameY = mEmotionView?.frame.origin.y
        let plusViewFrameY = mPlusView?.frame.origin.y
        let frameHeight = self.view.frame.size.height
        //
        //当前输入工具栏在会话页面最底部，显示plus
        if (inputViewFrameY == frameHeight - INPUTBAR_HEIGHT) {
            // debugPrint("2. \(#function)")
            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0) {
                //
                var tableViewContentInsets = self.mTableView?.contentInset
                tableViewContentInsets?.bottom += self.EMOTION_PLUS_VIEW_HEIGHT
                self.mTableView?.contentInset = tableViewContentInsets!
                //
                var inputViewFrame = self.mInputView?.frame
                inputViewFrame?.origin.y -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mInputView?.frame = inputViewFrame!
                //
                var plusViewFrame = self.mPlusView?.frame
                plusViewFrame?.origin.y -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mPlusView?.frame = plusViewFrame!
            }
        }
        //当前显示Plus扩展, 需要显示键盘
        else if (plusViewFrameY != frameHeight) {
            // debugPrint("3. \(#function)")
            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0) {
                if (self.mInputView!.isFirstResponder) {
                    //输入框设置焦点，显示键盘
                    let _ = self.mInputView?.resignFirstResponder()
                } else {
                    //输入框设置焦点，显示键盘
                    let _ = self.mInputView?.becomeFirstResponder()
                    //隐藏plusView
                    var plusViewFrame = self.mPlusView?.frame
                    plusViewFrame?.origin.y += self.EMOTION_PLUS_VIEW_HEIGHT
                    self.mPlusView?.frame = plusViewFrame!
                }
            }
        }
        //当前显示表情扩展, 需要隐藏表情扩展，显示Plus扩展
        else if (emotionViewFrameY != frameHeight) {
            // debugPrint("4. \(#function)")
            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0) {
                //
                var plusViewFrame = self.mPlusView?.frame
                plusViewFrame?.origin.y -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mPlusView?.frame = plusViewFrame!
                //
                var emotionViewFrame = self.mEmotionView?.frame
                emotionViewFrame?.origin.y += self.EMOTION_PLUS_VIEW_HEIGHT
                self.mEmotionView?.frame = emotionViewFrame!
            }
        }
        //当前显示键盘, 需要隐藏键盘，显示kfPlusView
        else {
            // debugPrint("5. \(#function)")
            UIView.animate(withDuration: VIEW_ANIMATION_DURATION, delay: 0.0) {
                //
                var tableViewContentInsets = self.mTableView?.contentInset
                tableViewContentInsets?.bottom = self.EMOTION_PLUS_VIEW_HEIGHT + self.INPUTBAR_HEIGHT
                self.mTableView?.contentInset = tableViewContentInsets!
                // 隐藏键盘
                let keyboard = self.mInputView?.inputTextView?.inputAccessoryView?.superview
                var keyboardFrame = keyboard?.frame
                keyboardFrame?.origin.y = frameHeight
                keyboard?.frame = keyboardFrame!
                //
                let _ = self.mInputView?.resignFirstResponder()
                //调整inputViewFrame
                var inputViewFrame = self.mInputView?.frame
                inputViewFrame?.origin.y = frameHeight - self.EMOTION_PLUS_VIEW_HEIGHT - self.INPUTBAR_HEIGHT
                self.mInputView?.frame = inputViewFrame!
                //显示kfPlusView
                var plusViewFrame = self.mPlusView?.frame
                plusViewFrame?.origin.y -= self.EMOTION_PLUS_VIEW_HEIGHT
                self.mPlusView?.frame = plusViewFrame!
            }
        }
        //
        tableViewScrollToBottom(true)
    }
    
    func sendMessage(_ content: String) {
        // debugPrint("kfvc \(#function), \(content)")
        
        if (!content.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty) {
            // debugPrint("TODO: send \(content)")
            self.sendTextMessage(content)
            self.mInputView?.inputTextView?.text = ""
        }
    }
    
    // MARK: 录音

    
    func recordVoiceButtonTouchDown() {
        print(#function)
        // 显示录音HUD
        self.mRecordVoiceViewHUD!.isHidden = false
        // 开始录音
        self.mRecordVoiceViewHUD!.startVoiceRecording(self.mUUid!)
        self.mTableView!.reloadData()
        self.tableViewScrollToBottom(true)
    }

    func recordVoiceButtonTouchUpInside() {
        print(#function)

        self.mRecordVoiceViewHUD!.isHidden = true
        let amrVoiceFileName = self.mRecordVoiceViewHUD!.stopVoiceRecording()
//        let voiceLength = Int(self.mRecordVoiceViewHUD!.voiceRecordLength!)
        //
        if amrVoiceFileName == "tooshort" {
            print("tooshort")
        } else if amrVoiceFileName == "toolong" {
            print("toolong")
        } else {
//            self.uploadAmrVoice(amrVoiceFileName, voiceLength: voiceLength)
        }
    }

    func recordVoiceButtonTouchUpOutside() {
        print(#function)

        self.mRecordVoiceViewHUD!.isHidden = true
        self.mRecordVoiceViewHUD!.cancelVoiceRecording()
    }

    func recordVoiceButtonTouchDragInside() {
        print(#function)
        //
        self.mRecordVoiceViewHUD!.microphoneImageView!.isHidden = false
        self.mRecordVoiceViewHUD!.signalWaveImageView!.isHidden = false
        self.mRecordVoiceViewHUD!.cancelArrowImageView!.isHidden = true
        //
        self.mRecordVoiceViewHUD!.hintLabel!.text = "上滑取消"
        self.mRecordVoiceViewHUD!.hintLabel!.backgroundColor = UIColor.clear
    }

    func recordVoiceButtonTouchDragOutside() {
        print(#function)
        //
        self.mRecordVoiceViewHUD!.microphoneImageView!.isHidden = true
        self.mRecordVoiceViewHUD!.signalWaveImageView!.isHidden = true
        self.mRecordVoiceViewHUD!.cancelArrowImageView!.isHidden = false
        //
        self.mRecordVoiceViewHUD!.hintLabel!.text = "松手取消"
        self.mRecordVoiceViewHUD!.hintLabel!.backgroundColor = UIColor.red
    }
    
//    func emojiPicker(_ picker: ElegantEmojiPicker, didSelectEmoji emoji: Emoji?) {
//        if (emoji?.emoji == nil) {
//            // debugPrint("未选表情")
//        } else {
//            // debugPrint("selected emoji: \(emoji!.emoji)")
//            let content = self.mInputView?.inputTextView?.text
//            mInputView?.inputTextView?.text = String(format: "%@%@", content!, emoji!.emoji)
//        }
//    }
}
