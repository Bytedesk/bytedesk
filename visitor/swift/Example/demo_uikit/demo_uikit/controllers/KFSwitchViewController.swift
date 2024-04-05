//
//  KFSwitchViewController.swift
//  demo_kefu_uikit
//
//  Created by 宁金鹏 on 2023/9/5.
//
import UIKit
import bytedesk_swift

class KFSwitchViewController: UITableViewController {
    
    //开发文档：https://github.com/pengjinning/bytedesk-ios
    //获取appkey：登录后台->渠道->APP->appkey列
    //获取subDomain，也即企业号：登录后台->客服->账号->企业号列
    // 需要替换为真实的
    let DEFAULT_TEST_APPKEY = "a3f79509-5cb6-4185-8df9-b1ce13d3c655"
    let DEFAULT_TEST_SUBDOMAIN = "vip"
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "切换用户"
        //
        self.tableView.dataSource = self
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
    }
    
    // Mark: Table
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 4
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "切换用户需要先退出之前的登录用户"
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        if (indexPath.row == 0) {
            cell.accessoryType = UITableViewCell.AccessoryType.disclosureIndicator
            cell.textLabel?.text = "用户信息"
        } else if (indexPath.row == 1) {
            cell.textLabel?.text = "用户1男"
        } else if (indexPath.row == 2) {
            cell.textLabel?.text = "用户2女"
        } else if (indexPath.row == 3) {
            cell.textLabel?.text = "退出登录"
        }
        return cell;
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        print("didselect", indexPath.row)
        
        if (indexPath.row == 0) {
            userInfo()
        } else if (indexPath.row == 1) {
            userBoyLogin()
        } else if (indexPath.row == 2) {
            userGirlLogin()
        } else if (indexPath.row == 3) {
            logout()
        }
    }
    
    func userInfo() {
        let viewController = KFUserinfoViewController()
        navigationController?.pushViewController(viewController, animated: true)
    }
    
    func userBoyLogin() {
        if (BDSettings.isAlreadyLogin()!) {
            BDToast.show(message: "请先退出登录")
            return
        }
        
        BDCoreApis.initBytedesk(username: "myiosswiftuserboy",
                                nickname: "我是帅哥iosSwift",
                                avatar: "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/boy.png",
                                appkey: DEFAULT_TEST_APPKEY,
                                subDomain: DEFAULT_TEST_SUBDOMAIN) { loginResult in
            BDToast.show(message: "初始化成功")
        } onFailure: { error in
            BDToast.show(message: error)
        }
    }
    
    func userGirlLogin() {
        if (BDSettings.isAlreadyLogin()!) {
            BDToast.show(message: "请先退出登录")
            return
        }
        
        BDCoreApis.initBytedesk(username: "myiosswiftusergirl",
                                nickname: "我是美女iosSwift",
                                avatar: "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png",
                                appkey: DEFAULT_TEST_APPKEY,
                                subDomain: DEFAULT_TEST_SUBDOMAIN) { loginResult in
            BDToast.show(message: "初始化成功")
        } onFailure: { error in
            BDToast.show(message: error)
        }
    }
    
    func logout() {
        
        BDCoreApis.logout { statusResult in
            BDToast.show(message: "退出登录成功")
        } onFailure: { error in
            BDToast.show(message: error)
        }

    }
    
}
