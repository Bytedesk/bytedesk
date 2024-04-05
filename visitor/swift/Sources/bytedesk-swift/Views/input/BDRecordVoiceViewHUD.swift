//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit
import AVFoundation

class BDRecordVoiceViewHUD: UIView, AVAudioRecorderDelegate {
    
    let microphoneWidth: CGFloat = 50.0
    let microphoneHeight: CGFloat = 100.0

    let microwaveWidth: CGFloat = 30.0
    let microwaveHeight: CGFloat = 90.0
    let microwaveLeftMargin: CGFloat = 5.0

    let cancelLeftMargin: CGFloat = 48.0
    let cancelTopMargin: CGFloat = 46.0
    let cancelWidth: CGFloat = 80.0
    let cancelHeight: CGFloat = 90.0

    let hintLabelLeftMargin: CGFloat = 10.0
    let hintLabelTopMargin: CGFloat = 120.0
    let hintLabelWidth: CGFloat = 130.0
    let hintLabelHeight: CGFloat = 20.0
    
    var microphoneImageView: UIImageView?
    var signalWaveImageView: UIImageView?
    var cancelArrowImageView: UIImageView?
    var hintLabel: UILabel?
    var voiceRecordLength: Int? = 0
    
    var voiceRecorder: AVAudioRecorder?
    var voicePathURL: URL?
    var voiceRecorderTimer: Timer?
    
    var workgroupName: String?

    var voiceRecordStartTime: Double = 0.0
    var voiceRecordEndTime: Double = 0.0

    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("\(#function)")
        
//        self.backgroundColor = //UIColorFromRGB(0x525252);
        self.backgroundColor = UIColor.systemGray
        self.layer.opacity = 0.9;
        self.layer.cornerRadius = 10.0
        self.layer.masksToBounds = true
        self.layer.borderColor = UIColor.systemGray.cgColor
        self.layer.borderWidth = 0.5
//        self.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleTopMargin;

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
        
        microphoneImageView = UIImageView()
        microphoneImageView?.image = UIImage(named: "RecordingBkg", in: .module, with: nil)
        addSubview(microphoneImageView!)
        
        signalWaveImageView = UIImageView()
        signalWaveImageView?.image = UIImage(named: "RecordingSignal001", in: .module, with: nil)
        addSubview(signalWaveImageView!)
        
        cancelArrowImageView = UIImageView()
        cancelArrowImageView?.image = UIImage(named: "RecordCancel", in: .module, with: nil)
        addSubview(cancelArrowImageView!)
        cancelArrowImageView?.isHidden = true
        
        hintLabel = UILabel()
        hintLabel!.text = "上滑取消"
        hintLabel!.textColor = UIColor.white
        hintLabel!.font = UIFont.systemFont(ofSize: 14.0)
        hintLabel!.textAlignment = .center
        hintLabel!.layer.cornerRadius = 5.0
        hintLabel!.layer.masksToBounds = true
        addSubview(hintLabel!)
        
        //
        initVoiceRecorder()
        voiceRecorderTimer = Timer.scheduledTimer(timeInterval: 0.05, target: self, selector: #selector(updateSignalWaveMeters), userInfo: nil, repeats: true)
        
    }
    
    func setupConstraints() {
        
//        microphoneImageView?.frame = CGRectMake((self.bounds.size.width - microwaveWidth - microphoneWidth)/2 + microphoneWidth + microwaveLeftMargin,
//                                                (self.bounds.size.height - microwaveHeight - hintLabelHeight)/2,
//                                                microwaveWidth,
//                                                microwaveHeight)
        
        microphoneImageView?.frame =  CGRectMake((self.bounds.size.width - microphoneWidth)/2,
                                                 (self.bounds.size.height - microwaveHeight - hintLabelHeight)/2,
                                                 microphoneWidth,
                                                 microwaveHeight)
        cancelArrowImageView?.frame = CGRectMake((self.bounds.size.width - cancelWidth)/2,
                                                 (self.bounds.size.height - cancelHeight - hintLabelHeight)/2,
                                                 cancelWidth,
                                                 cancelHeight)
        hintLabel?.frame = CGRectMake(hintLabelLeftMargin,
                                      hintLabelTopMargin,
                                      hintLabelWidth,
                                      hintLabelHeight)
        
    }

    // MARK:
    
    func initVoiceRecorder() {
        let audioSession = AVAudioSession.sharedInstance()
        do {
            try audioSession.setCategory(.record)
        } catch {
            print("Init AudioSession Error: \(error)")
            return
        }

        do {
            try audioSession.setActive(true)
        } catch {
            print("AudioSession setActive Error: \(error)")
            return
        }

        if voiceRecorder == nil {
            let tempVoicePath = "\(NSHomeDirectory())/Documents/tempVoice.wav"
            let tempVoicePathURL = URL(fileURLWithPath: tempVoicePath)
            
            var voiceRecorderSettings: [String: Any] = [:]
            voiceRecorderSettings[AVFormatIDKey] = kAudioFormatLinearPCM
            voiceRecorderSettings[AVSampleRateKey] = 8000.00
            voiceRecorderSettings[AVNumberOfChannelsKey] = 1
            voiceRecorderSettings[AVLinearPCMBitDepthKey] = 16
            voiceRecorderSettings[AVEncoderAudioQualityKey] = AVAudioQuality.high.rawValue
            
            do {
                voiceRecorder = try AVAudioRecorder(url: tempVoicePathURL, settings: voiceRecorderSettings)
            } catch {
                print("Create VoiceRecorder Error: \(error)")
                return
            }
            
            voiceRecorder!.isMeteringEnabled = true
            voiceRecorder!.delegate = self
        }
    }
    
    func startVoiceRecording(_ username: String) {
        voiceRecorder!.prepareToRecord()
        voiceRecorder!.record()
        
        voiceRecorderTimer = Timer.scheduledTimer(timeInterval: 0.05, target: self, selector: #selector(updateSignalWaveMeters), userInfo: nil, repeats: true)
        workgroupName = username
        voiceRecordStartTime = NSDate.timeIntervalSinceReferenceDate
    }

    func stopVoiceRecording() -> String {
        voiceRecorder!.stop()
        voiceRecorderTimer!.invalidate()
        voiceRecorderTimer = nil
        voiceRecordEndTime = NSDate.timeIntervalSinceReferenceDate
        let voiceRecordLength = voiceRecordEndTime - voiceRecordStartTime
        
        if voiceRecordLength < 1 {
            return "tooshort"
        } else if voiceRecordLength > 60 {
            return "toolong"
        }
        
        let tempVoicePath = "\(NSHomeDirectory())/Documents/tempVoice.wav"
        let wavVoiceFileName = "\(BDSettings.getUsername())_to_\(workgroupName)_\(BDUtils.getCurrentTimeString())_\(Int(voiceRecordLength)).wav"
        let amrVoiceFileName = "\(BDSettings.getUsername())_to_\(workgroupName)_\(BDUtils.getCurrentTimeString())_\(Int(voiceRecordLength)).amr"
        let wavVoicePath = "\(NSHomeDirectory())/Documents/\(wavVoiceFileName)"
        let fileManager = FileManager.default
        do {
            try fileManager.moveItem(atPath: tempVoicePath, toPath: wavVoicePath)
        } catch {
            print("rename file Error: \(error)")
        }

        return amrVoiceFileName
    }

    func cancelVoiceRecording() {
        voiceRecorder!.stop()
        voiceRecorder!.deleteRecording()
        
        voiceRecorderTimer!.invalidate()
        voiceRecorderTimer = nil
    }
    
    // VoiceRecord Update Meters
    @objc func updateSignalWaveMeters() {
        voiceRecorder!.updateMeters()
        let meter = voiceRecorder!.averagePower(forChannel: 0)
        //
        if -5.0 <= meter {
            signalWaveImageView!.image = UIImage(named: "RecordingSignal008", in: .module, with: nil)
        } else if -10.0 <= meter && meter < -5.0 {
            signalWaveImageView!.image = UIImage(named: "RecordingSignal007", in: .module, with: nil)
        } else if -20.0 <= meter && meter < -10.0 {
            signalWaveImageView!.image = UIImage(named: "RecordingSignal006", in: .module, with: nil)
        } else if -30.0 <= meter && meter < -20.0 {
            signalWaveImageView!.image = UIImage(named: "RecordingSignal005", in: .module, with: nil)
        } else if -40.0 <= meter && meter < -30.0 {
            signalWaveImageView!.image = UIImage(named: "RecordingSignal004", in: .module, with: nil)
        } else if -50.0 <= meter && meter < -40.0 {
            signalWaveImageView!.image = UIImage(named: "RecordingSignal003", in: .module, with: nil)
        } else if -55.0 <= meter && meter < -50.0 {
            signalWaveImageView!.image = UIImage(named: "RecordingSignal002", in: .module, with: nil)
        } else if -60.0 <= meter && meter < -55.0 {
            signalWaveImageView!.image = UIImage(named: "RecordingSignal001", in: .module, with: nil)
        }
    }

    // AVAudioRecorderDelegate
    func audioRecorderBeginInterruption(_ recorder: AVAudioRecorder) {
        print(#function)
    }

    func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool) {
        // print(#function)
    }

    func audioRecorderEncodeErrorDidOccur(_ recorder: AVAudioRecorder, error: Error?) {
        print(#function)
    }
    
}
