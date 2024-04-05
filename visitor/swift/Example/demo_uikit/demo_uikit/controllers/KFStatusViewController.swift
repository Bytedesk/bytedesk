//
//  KFStatusViewController.swift
//  demo_kefu_uikit
//
//  Created by 宁金鹏 on 2023/9/5.
//
import UIKit
import bytedesk_swift

class KFStatusViewController: UITableViewController {
    //
    var defaultWorkgroupWid: String? = "201807171659201"
    var defaultAgentUid: String? = "201808221551193"
    //
    var workGroupStatus: String? = ""
    var agentStatus: String? = ""
    
    var mRefreshControl: UIRefreshControl?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "在线状态"
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
        2
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return section == 1 ? "客服账号在线状态接口" : "工作组在线状态接口";
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style:UITableViewCell.CellStyle.value1, reuseIdentifier: "Cell")

        if (indexPath.section == 0) {
            cell.textLabel?.text = String(format: "技能组: %@", defaultWorkgroupWid!)
            cell.detailTextLabel?.text = workGroupStatus!
        } else {
            cell.textLabel?.text = String(format: "客服账号: %@", defaultAgentUid!)
            cell.detailTextLabel?.text = agentStatus!
        }
        return cell;
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        print("didselect", indexPath.row)
    }
    
    
    @objc func handleRefreshControlPulldown() {
        debugPrint("\(#function)")
        
        // 查询技能组在线状态
        BDCoreApis.getWorkGroupStatus(workgroupWid: defaultWorkgroupWid) { statusResult in
            
            self.workGroupStatus = statusResult.data!.status
            self.tableView.reloadData()
            
            debugPrint("workgroup status \(statusResult.data!.status!), wid: \(statusResult.data!.wid!)")
            self.mRefreshControl?.endRefreshing()
        } onFailure: { error in
            debugPrint("workgroup status error \(error)")
            self.mRefreshControl?.endRefreshing()
            BDToast.show(message: error)
        }

        // 查询指定客服在线状态
        BDCoreApis.getAgentStatus(agentUid: defaultAgentUid) { statusResult in
            
            self.agentStatus = statusResult.data!.status
            self.tableView.reloadData()
            
            debugPrint("agent status \(statusResult.data!.status!), uid: \(statusResult.data!.uid!)")
            self.mRefreshControl?.endRefreshing()
        } onFailure: { error in
            debugPrint("agent status error \(error)")
            self.mRefreshControl?.endRefreshing()
            BDToast.show(message: error)
        }

    }
    
}
