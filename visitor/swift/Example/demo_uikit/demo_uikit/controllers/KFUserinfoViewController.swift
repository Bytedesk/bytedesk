//
//  KFUserinfoViewController.swift
//  demo_kefu_uikit
//
//  Created by 宁金鹏 on 2023/9/5.
//
import UIKit
import bytedesk_swift

class KFUserinfoViewController: UITableViewController {
        
    var mUid: String?
    var mNickname: String?
    var mDescription: String?
    var mAvatar: String?
    
    var mTagkey: String?
    var mTagvalue: String?
    
    var mRefreshControl: UIRefreshControl?
    
    //
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "用户信息"
        //
        self.tableView.dataSource = self
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
        self.mRefreshControl = UIRefreshControl(frame: CGRectMake(0, 0, 20, 20))
        self.tableView?.addSubview(self.mRefreshControl!)
        self.mRefreshControl?.addTarget(self, action: #selector(handleRefreshControlPulldown), for: .valueChanged)
        //
        handleRefreshControlPulldown()
    }
    
    // Mark: Table
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        1
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return section == 1 ? "自定义用户信息接口" : "默认用户信息接口，支持自定义昵称和头像";
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (section == 0) {
            return 3;
        }
        return 1;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style:UITableViewCell.CellStyle.value1, reuseIdentifier: "Cell")
        
        if (indexPath.section == 0) {
            if (indexPath.row == 0) {
                cell.textLabel?.text = "昵称"
                cell.detailTextLabel?.text = self.mNickname
            } else if (indexPath.row == 1) {
                cell.textLabel?.text = "描述"
                cell.detailTextLabel?.text = self.mDescription
            } else if (indexPath.row == 2) {
                cell.textLabel?.text = "头像"
                cell.detailTextLabel?.text = self.mAvatar
            }
        } else if (indexPath.section == 1) {
            cell.textLabel?.text = "自定义标签"
        }
        return cell;
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        print("didselect", indexPath.row)
        if (indexPath.section == 0) {
            if (indexPath.row == 0) {
                setNickname()
            } else if (indexPath.row == 1) {
                setDescription()
            } else {
                setAvatar()
            }
        }
    }
    
    func setNickname() {
        debugPrint("\(#function)")
        self.mNickname = "自定义昵称"
        BDCoreApis.setNickname(self.mNickname) { userResult in
            self.mNickname = userResult.data?.nickname
            self.tableView.reloadData()
        } onFailure: { error in
            BDToast.show(message: error)
        }
    }
    
    func setDescription() {
        debugPrint("\(#function)")
        self.mDescription = "自定义APP用户备注信息ios_swift"
        BDCoreApis.setDescription(self.mDescription) { userResult in
            self.mDescription = userResult.data?.description
            self.tableView.reloadData()
        } onFailure: { error in
            BDToast.show(message: error)
        }
    }
    
    func setAvatar() {
        debugPrint("\(#function)")
        self.mAvatar = "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png"
        BDCoreApis.setAvatar(self.mAvatar) { userResult in
            self.mAvatar = userResult.data?.avatar
            self.tableView.reloadData()
        } onFailure: { error in
            BDToast.show(message: error)
        }

    }
    
    @objc func handleRefreshControlPulldown() {
        debugPrint("\(#function)")

        BDCoreApis.getVisitorProfile { userResult in
            self.mUid = userResult.data?.uid
            self.mNickname = userResult.data?.nickname
            self.mAvatar = userResult.data?.avatar
            self.mDescription = userResult.data?.description
            self.tableView.reloadData()
            self.mRefreshControl?.endRefreshing()
        } onFailure: { error in
            BDToast.show(message: error)
            self.mRefreshControl?.endRefreshing()
        }
    }
    
    
}
