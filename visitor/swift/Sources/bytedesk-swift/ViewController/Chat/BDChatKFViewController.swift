//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit

class BDChatKFViewController: UIViewController {
    //
    let INPUTBAR_HEIGHT                    = 60.0
    let EMOTION_PLUS_VIEW_HEIGHT           = 216.0
    let VIEW_ANIMATION_DURATION            = 0.25
    let TEXTBUBBLE_MAX_TEXT_WIDTH          = 180.0
    let RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT = 150.0
    let TIMESTAMP_HEIGHT                   = 30.0
    //
    var mTitle: String? = "Chat"
    var mUUid: String? = ""
    var mTid: String? = ""
    var mIsPush: Bool? = true
    var mIsRobot: Bool? = false
    var mThreadType: String?
    //
    var inputViewY: CGFloat?
    var keyboardY: CGFloat?
    var keyboardHeight: CGFloat?
    //
    var isEmotionPlusPressedToHideKeyboard: Bool?
    var isViewControllerClosed: Bool?
    //
    var mTableView: UITableView?
    var mInputView: BDInputView?
    var mPlusView: BDPlusView?
    var mEmotionView: BDEmotionView?
    var mRecordVoiceViewHUD: BDRecordVoiceViewHUD?
    var mRefreshControl: UIRefreshControl?
    var mImagePickerController: UIImagePickerController?
    //
    var mWithCustomDict: Bool = false
    var mCustomDict: [String: Any]? = nil
    //
    var mEmotionToTextDictionary: NSDictionary?
    var mMessageArray: [BDMessageModel] = [] //[BDMessageModel]?
    var mThreadModel: BDThreadModel?
    
    var notifyIdentifier: String? = "notifyCell"
    var commodityIdentifier: String? = "commodityCell"
    var msgIdentifier: String? = "msgCell"
    
    // 请求技能组会话
    func initWithWorkGroupWid(wid: String?, _ title: String?, _ isPush: Bool?) {
        self.mUUid = wid
        self.mTitle = title
        self.mIsPush = isPush
        self.navigationItem.title = title
//        self.mThreadModel = BDThread()
        self.mThreadType = BD_THREAD_TYPE_WORKGROUP
        // 请求会话
        requestThread()
    }
    
    func initWithWorkGroupWid(wid: String?, _ title: String?, _ isPush: Bool?, custom: [String: Any]) {
        self.mWithCustomDict = true
        self.mCustomDict = custom
        //
        self.initWithWorkGroupWid(wid: wid, title, isPush)
    }
    
    // 请求指定客服会话
    func initWithAgentUid(uid: String?, _ title: String?, _ isPush: Bool?) {
        self.mUUid = uid
        self.mTitle = title
        self.mIsPush = isPush
        self.navigationItem.title = title
//        self.mThreadModel = BDThread()
        self.mThreadType = BD_THREAD_TYPE_APPOINTED
        // 请求会话
        requestThread()
    }
    
    func initWithAgentUid(uid: String?, _ title: String?, _ isPush: Bool?, custom: [String: Any]) {
        self.mWithCustomDict = true
        self.mCustomDict = custom
        //
        self.initWithAgentUid(uid: uid, title, isPush)
    }
    
    //
    override func loadView() {
        super.loadView()
        debugPrint("1.\(#function)")
        //
        mTableView = UITableView()
    }
    
    //
    override func viewDidLoad() {
        super.viewDidLoad()
        title = mTitle;
         debugPrint("2.\(#function)")
        //
        let singleFingerTap = UITapGestureRecognizer(target: self, action: #selector(handleSingleTap))
        self.mTableView?.addGestureRecognizer(singleFingerTap)
        //
        self.registerNotifications()
        //
        if (self.mIsPush == false) {
            self.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(handleCloseButtonEvent))
        }
        //
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(systemName: "ellipsis"),
                                                                 style: .plain,
                                                                 target: self,
                                                                 action: #selector(handleRightBarButtonItemClicked(_:)))
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("3.\(#function)")
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        print("4.\(#function)")
        // Initialize and layout subviews
//        mTableView = UITableView(frame: CGRectMake(0, 0, CGRectGetWidth(self.view.bounds), CGRectGetHeight(self.view.bounds)))
        mTableView?.frame = CGRectMake(0, 0, CGRectGetWidth(self.view.bounds), CGRectGetHeight(self.view.bounds))
        mTableView?.separatorColor = UIColor.clear
        mTableView?.backgroundColor = UIColor.systemGroupedBackground
        mTableView?.delegate = self
        mTableView?.dataSource = self
        var tableViewInsets = mTableView!.contentInset;
        tableViewInsets.bottom = INPUTBAR_HEIGHT * 2;
        mTableView?.contentInset = tableViewInsets;
        mTableView?.scrollIndicatorInsets = tableViewInsets;
        //
        mTableView?.register(BDMsgNotificationViewCell.self, forCellReuseIdentifier: notifyIdentifier!)
        mTableView?.register(BDMsgCommodityViewCell.self, forCellReuseIdentifier: commodityIdentifier!)
        mTableView?.register(BDMsgTableViewCell.self, forCellReuseIdentifier: msgIdentifier!)
//        mTableView?.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
        self.view.addSubview(mTableView!);
        //
//        let windowHeight = getWindowHeight()
        mInputView = BDInputView(frame: CGRectMake(0.0, self.view.frame.size.height - INPUTBAR_HEIGHT, self.view.frame.size.width, INPUTBAR_HEIGHT))
        mInputView?.delegate = self
        self.view.addSubview(mInputView!)
        //
        initEmotionLazy()
        initPlusViewLazy()
        initRecordVoiceLazy()
        initImagePickerLazy()
        //
        self.mRefreshControl = UIRefreshControl(frame: CGRectMake(0, 0, 20, 20))
        self.mTableView?.addSubview(self.mRefreshControl!)
        self.mRefreshControl?.addTarget(self, action: #selector(handleRefreshControlPulldown), for: .valueChanged)
        
    }
    
    //
    func loadEmotionToTextDictionary() -> NSDictionary? {
        guard let plistPath = Bundle.module.path(forResource: "EmotionToText", ofType: "plist"),
              let plistData = FileManager.default.contents(atPath: plistPath) else {
            return nil
        }
        
        do {
            let plist = try PropertyListSerialization.propertyList(from: plistData, options: [], format: nil)
            if let dictionary = plist as? NSDictionary {
                return dictionary
            }
        } catch {
            print("Error reading EmotionToText.plist: \(error)")
        }
        
        return nil
    }
    
    //
    func tableViewScrollToBottom(_ animated: Bool) {
        let rows = self.mTableView?.numberOfRows(inSection: 0)
        if (rows! > 0) {
            self.mTableView?.scrollToRow(at: IndexPath(row: rows! - 1, section: 0), at: .bottom, animated: animated)
        }
    }
    
    // MARK: 点击页面
    
    @objc func handleSingleTap() {
        // debugPrint("\(#function)")
        let _ = self.mInputView!.resignFirstResponder()
        
    }
    
    // MARK: UIScrollViewDelegate
    
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        // debugPrint("\(#function)")
        self.view.endEditing(true)
        //
        UIView.animate(withDuration: 0.0) {
            let screenSize = self.view.frame.size
            //
            var tableViewInsets = self.mTableView?.contentInset
            tableViewInsets?.bottom = self.INPUTBAR_HEIGHT * 2
            self.mTableView?.contentInset = tableViewInsets!
            self.mTableView?.scrollIndicatorInsets = tableViewInsets!
            //
            self.mInputView?.frame = CGRectMake(0.0,
                                               screenSize.height - self.INPUTBAR_HEIGHT,
                                               screenSize.width,
                                               self.INPUTBAR_HEIGHT);
            
            self.mEmotionView?.frame = CGRectMake(0.0,
                                                  screenSize.height,
                                                  screenSize.width,
                                                 self.EMOTION_PLUS_VIEW_HEIGHT);
            
            self.mPlusView?.frame = CGRectMake(0.0,
                                               screenSize.height,
                                               screenSize.width,
                                              self.EMOTION_PLUS_VIEW_HEIGHT);
        }
    }
    
    
    
    @objc func handleRefreshControlPulldown() {
        // debugPrint("\(#function)")
        // TODO: 调用接口从服务器拉取历史消息
        
        self.mRefreshControl?.endRefreshing()
    }
    
    func reloadTableData(_ animated: Bool) {
        // debugPrint("\(#function)")
//        mMessageArray = BDCoreApis.getMessagesWithThread(mTid!)
        mMessageArray = BDCoreApis.getMessagesWithWorkGroup(mUUid!)
        for i in 0..<mMessageArray.count {
            let messageModel = mMessageArray[i]
            if (messageModel.type == BD_MESSAGE_TYPE_ROBOT) {
                messageModel.content = BDUtils.appendAnswersToContent(messageModel)
                //
                messageModel.contentAttr = BDUtils.transformContentToContentAttr(messageModel.content!)
                mMessageArray[i] = messageModel
            }
        }
        mTableView?.reloadData()
        tableViewScrollToBottom(animated)
    }
    
    @objc func handleRightBarButtonItemClicked(_ sender: UIBarButtonItem) {
        // Handle right bar button item click event
        // debugPrint("\(#function)")
        
        let actionSheet = UIAlertController(title: "提示", message: "", preferredStyle: .actionSheet)
        actionSheet.addAction(UIAlertAction(title: "取消", style: .cancel) { _ in
            self.dismiss(animated: true, completion: nil)
        })
        actionSheet.addAction(UIAlertAction(title: "清空聊天记录", style: .default) { _ in
            // Handle "清空聊天记录" action
            BDCoreApis.deleteMessagesByWorkGroup(self.mUUid!)
            //
            self.reloadTableData(true)
        })
        self.present(actionSheet, animated: true, completion: nil)
    }

    
    // MARK: 延迟初始化
    
    func initEmotionLazy() {
        if (mEmotionView == nil) {
            mEmotionView = BDEmotionView(frame: CGRectMake(0.0, self.view.frame.size.height, self.view.frame.size.width, EMOTION_PLUS_VIEW_HEIGHT))
            mEmotionView?.delegate = self
            self.view.addSubview(mEmotionView!)
            self.mEmotionToTextDictionary = self.loadEmotionToTextDictionary()
        }
    }
    
    func initPlusViewLazy() {
        if (mPlusView == nil) {
            mPlusView = BDPlusView(frame: CGRectMake(0.0, self.view.frame.size.height, self.view.frame.size.width, EMOTION_PLUS_VIEW_HEIGHT))
            mPlusView?.delegate = self
            self.view.addSubview(mPlusView!)
        }
    }
    
    func initRecordVoiceLazy() {
        if (mRecordVoiceViewHUD == nil) {
            mRecordVoiceViewHUD = BDRecordVoiceViewHUD(frame: CGRectMake((self.view.frame.size.width - RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT)/2,
                                                                     (self.view.frame.size.height - RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT)/2,
                                                                     RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT,
                                                                     RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT))
            self.view.addSubview(mRecordVoiceViewHUD!)
            mRecordVoiceViewHUD?.isHidden = true
        }
    }
    
    func initImagePickerLazy() {
        if (mImagePickerController == nil) {
            self.mImagePickerController = UIImagePickerController()
            self.mImagePickerController?.delegate = self
        }
    }
    
    @objc func handleCloseButtonEvent() {
        self.navigationController?.dismiss(animated: true)
    }
    
}
