//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/12.
//

import UIKit

class BDLeaveHistoryViewController: UITableViewController {
    
    var mRefreshControl: UIRefreshControl?
    var mLeaveArray: [BDLeaveMsgModel]?

    var page: Int = 0
    var size: Int = 0
    
    func initWithUid(_ uid: String, isPush: Bool) {
        print("1.\(#function)")
//        mAdminUid = uid
//        mIsPush = isPush
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        self.title = "我的留言"
        self.page = 0
        self.size = 20
        
        self.mLeaveArray = []
        
        self.mRefreshControl = UIRefreshControl()
        tableView.addSubview(self.mRefreshControl!)
        self.mRefreshControl!.addTarget(self, action: #selector(refreshControlSelector), for: .valueChanged)
        
        self.mRefreshControl!.beginRefreshing()
        getLeaveRecords()
    }

    // UITableViewDataSource, UITableViewDelegate methods

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if mLeaveArray!.count == 0 {
            // 内容为空
            return 1
        }
        return mLeaveArray!.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "Cell"
        var cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier)
        
        // Configure the cell...
        if cell == nil {
            cell = UITableViewCell(style: .subtitle, reuseIdentifier: cellIdentifier)
            cell?.accessoryType = .none
        }
        
        if mLeaveArray!.count == 0 {
            cell?.detailTextLabel?.text = "暂未留言"
            return cell!
        }
        
        let leaveModel = mLeaveArray![indexPath.row]
        cell?.textLabel?.text = leaveModel.content
        if let reply = leaveModel.reply {
            cell?.detailTextLabel?.text = reply
        } else {
            cell?.detailTextLabel?.text = "暂未回复"
        }
        
        return cell!
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        // BDLeaveModel *feedbackModel = [self.mLeaveArray objectAtIndex:indexPath.row];
    }

    // Other methods

    @objc func refreshControlSelector() {
        print(#function)
        
        getLeaveRecords()
    }
    
    @objc func getLeaveRecords() {
        print(#function)
        
        BDCoreApis.queryLeaveMessage(page: 0, size: 20) { leaveMsgResultPage in
            
            self.mLeaveArray = leaveMsgResultPage.data?.content
            self.tableView.reloadData()
            
            BDToast.show(message: leaveMsgResultPage.message!)
            self.mRefreshControl?.endRefreshing()
        } onFailure: { error in
            self.mRefreshControl?.endRefreshing()
        }


        
    }
}
