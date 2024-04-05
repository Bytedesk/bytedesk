//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/10.
//

import UIKit
import Kingfisher

protocol BDMsgCommodityViewCellDelegate: AnyObject {
    func sendCommodityButtonClicked(_ messageModel: BDMessageModel)
//    func commodityBackgroundClicked(_ messageModel: BDMessageModel)
}

class BDMsgCommodityViewCell: UITableViewCell {
    
    weak var delegate: BDMsgCommodityViewCellDelegate?
    
    var backgrounView: UIView?
    var timestampLabel: UILabel?

    var commodityImageView: UIImageView?
    var titleLabel: UILabel?
    var priceLabel: UILabel?

    var contentLabel: UILabel?
    var sendButton: UIButton?
    
    var messageModel: BDMessageModel?
    var customDict: [String: String]?

    // used when the view is instantiated in code.
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        //
        self.selectionStyle = UITableViewCell.SelectionStyle.none
        self.backgroundColor = UIColor.systemGroupedBackground
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    func initWithMessageModel(_ msgModel: BDMessageModel?) {
        messageModel = msgModel
        if let contentData = messageModel!.content!.data(using: .utf8) {
            do {
                customDict = try JSONSerialization.jsonObject(with: contentData, options: []) as? [String: String]
            } catch {
                print("JSONSerialization error: \(error.localizedDescription)")
            }
        }
        //
        setupView()
        //
        setNeedsLayout()
    }
    
    func setupView() {
        // debugPrint("\(#function)")
        if (backgrounView != nil) {
            backgrounView?.removeFromSuperview()
            backgrounView = nil
        }
        //
        backgrounView = UIView()
        backgrounView?.layer.cornerRadius = 5.0
        backgrounView?.layer.masksToBounds = true
        backgrounView?.layer.borderColor = UIColor(red: 200.0/255.0, green: 200.0/255.0, blue: 205.0/255.0, alpha: 1.0).cgColor
        backgrounView?.layer.borderWidth = 0.5
        self.contentView.addSubview(backgrounView!)
        //
        timestampLabel = UILabel()
        timestampLabel?.textColor = UIColor.gray
        timestampLabel?.font = UIFont.systemFont(ofSize: 11)
        timestampLabel?.textAlignment = .center
        backgrounView!.addSubview(timestampLabel!)
        //
        commodityImageView = UIImageView()
        commodityImageView?.isUserInteractionEnabled = true
        backgrounView!.addSubview(commodityImageView!)
        //
        titleLabel = UILabel()
        titleLabel?.numberOfLines = 0
        titleLabel?.lineBreakMode = .byWordWrapping
        titleLabel?.textColor = UIColor.gray
        titleLabel?.textAlignment = .center
        backgrounView!.addSubview(titleLabel!)
        //
        priceLabel = UILabel()
        priceLabel?.numberOfLines = 0
        priceLabel?.lineBreakMode = .byWordWrapping
        priceLabel?.textColor = UIColor.red
        priceLabel?.font = UIFont.systemFont(ofSize: 11)
        priceLabel?.textAlignment = .center
        backgrounView!.addSubview(priceLabel!)
        //
        contentLabel = UILabel()
        contentLabel?.numberOfLines = 0
        contentLabel?.lineBreakMode = .byWordWrapping
        contentLabel?.textColor = UIColor.gray
        contentLabel?.font = UIFont.systemFont(ofSize: 11)
        contentLabel?.textAlignment = .center
        backgrounView!.addSubview(contentLabel!)
        //
        sendButton = UIButton()
        sendButton?.setTitle("发送", for: .normal)
        sendButton?.titleLabel?.font = UIFont.systemFont(ofSize: 12)
        sendButton?.layer.cornerRadius = 5.0
        sendButton?.layer.masksToBounds = true
        sendButton?.layer.borderColor = UIColor(red: 200.0/255.0, green: 200.0/255.0, blue: 205.0/255.0, alpha: 1.0).cgColor
        sendButton?.layer.borderWidth = 0.5
        sendButton?.addTarget(self, action: #selector(handleSendButtonClicked), for: .touchUpInside)
        backgrounView!.addSubview(sendButton!)
        BDUtils.setButtonTitleColor(sendButton!)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        backgrounView?.frame = CGRectMake(50, 5, (self.bounds.size.width - 100), 80)
        //
        if let timestampString = messageModel?.createdAt! {
            let timestampSize = timestampString.size(withAttributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 11.0)])
            timestampLabel!.frame = CGRect(x: (self.bounds.size.width - timestampSize.width - 110)/2, y: 0.5, width: timestampSize.width + 10.0, height: timestampSize.height + 1)
            timestampLabel!.text = timestampString
        }
        //
        commodityImageView?.frame = CGRectMake(10, 17, 60, 60)
        let url = URL(string: customDict!["imageUrl"]!)
        commodityImageView!.kf.setImage(with: url)
        //
        titleLabel?.frame = CGRectMake(60, 17, 100, 20)
        titleLabel?.text = customDict!["title"]!
        //
        contentLabel?.frame = CGRectMake(50, 37, 100, 20)
        contentLabel?.text = customDict!["content"]!
        //
        priceLabel?.frame = CGRectMake(50, 55, 100, 20)
        priceLabel?.text = customDict!["price"]!
        //
        sendButton?.frame = CGRectMake(160, 50, 50, 20)
    }
    
//    func setupConstraints() {
//        // debugPrint("\(#function)")
//    }
    
    @objc func handleSendButtonClicked() {
        delegate?.sendCommodityButtonClicked(messageModel!)
    }
    
}
