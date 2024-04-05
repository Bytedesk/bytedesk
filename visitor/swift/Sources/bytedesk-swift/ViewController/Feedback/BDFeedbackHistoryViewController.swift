//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/12.
//

import UIKit

class BDFeedbackHistoryViewController: UITableViewController {
    
    var mRefreshControl: UIRefreshControl?
    var mFeedbackArray: [BDFeedbackModel]?

    var page: Int = 0
    var size: Int = 0
    
    func initWithUid(_ uid: String, isPush: Bool) {
        print("1.\(#function)")
//        mAdminUid = uid
//        mIsPush = isPush
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "我的反馈"
        self.page = 0
        self.size = 20
        
        self.mFeedbackArray = []
        
        self.mRefreshControl = UIRefreshControl(frame: CGRect(x: 0, y: 0, width: 20, height: 20))
        tableView.addSubview(self.mRefreshControl!)
        self.mRefreshControl!.addTarget(self, action: #selector(refreshControlSelector), for: .valueChanged)
        
        self.mRefreshControl!.beginRefreshing()
        self.getFeedbackRecords()
    }

    // MARK: - UITableViewDataSource, UITableViewDelegate

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.mFeedbackArray!.count == 0 {
            return 1
        }
        return self.mFeedbackArray!.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "Cell"
        var cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier)
        
        if cell == nil {
            cell = UITableViewCell(style: .subtitle, reuseIdentifier: cellIdentifier)
            cell?.accessoryType = .none
        }
        
        if self.mFeedbackArray!.count == 0 {
            cell?.detailTextLabel?.text = "暂未反馈"
            return cell!
        }
        
        let feedbackModel = self.mFeedbackArray![indexPath.row]
        cell?.textLabel?.text = feedbackModel.content
        
        if let replyContent = feedbackModel.replyContent {
            cell?.detailTextLabel?.text = "暂未回复"
        } else {
            cell?.detailTextLabel?.text = feedbackModel.replyContent
        }
        
        return cell!
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        // let feedbackModel = self.mFeedbackArray[indexPath.row] as! BDFeedbackModel
        //
    }

    @objc func refreshControlSelector() {
        print(#function)
        
        getFeedbackRecords()
    }

    func getFeedbackRecords() {
        
        BDCoreApis.queryFeedback(page: page, size: size) { feedbackResultPage in
            self.mFeedbackArray = feedbackResultPage.data?.content
            self.tableView.reloadData()
            
            BDToast.show(message: feedbackResultPage.message!)
            self.mRefreshControl?.endRefreshing()
        } onFailure: { error in
            self.mRefreshControl?.endRefreshing()
            BDToast.show(message: error)
        }
    }
    
}
