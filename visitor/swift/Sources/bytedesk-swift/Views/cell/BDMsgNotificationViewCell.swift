//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/10.
//

import UIKit

class BDMsgNotificationViewCell: UITableViewCell {
    
    var timestampLabel: UILabel?
    var contentLabel: UILabel?
    var messageModel: BDMessageModel?
        
    // used when the view is instantiated in code.
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        // debugPrint("BDMsgNotificationViewCell \(#function)")
        //
        self.selectionStyle = UITableViewCell.SelectionStyle.none
        self.backgroundColor = UIColor.systemGroupedBackground
        //
        setupView()
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    func initWithMessageModel(_ msgModel: BDMessageModel?) {
        self.messageModel = msgModel
        timestampLabel?.text = msgModel?.createdAt
        contentLabel?.text = msgModel?.content
        //
        setNeedsLayout()
    }
    
    func setupView() {
        // debugPrint("BDMsgNotificationViewCell \(#function)")
        timestampLabel = UILabel()
        timestampLabel?.font = UIFont.systemFont(ofSize: 11)
        timestampLabel?.textColor = UIColor.systemGray
        timestampLabel?.textAlignment = NSTextAlignment.center
        addSubview(timestampLabel!)
        //
        contentLabel = UILabel()
        contentLabel?.numberOfLines = 0
        contentLabel?.lineBreakMode = NSLineBreakMode.byWordWrapping
        contentLabel?.textColor = UIColor.systemGray
        contentLabel?.font = UIFont.systemFont(ofSize: 11)
        contentLabel?.textAlignment = NSTextAlignment.center
        addSubview(contentLabel!)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        // debugPrint("BDMsgNotificationViewCell \(#function)")
        let createdAtSize = timestampLabel!.sizeThatFits(CGSizeMake(200, CGFLOAT_MAX)) //self.messageModel?.createdAtSize()
        timestampLabel?.frame = CGRect(x: (self.bounds.size.width - createdAtSize.width)/2, y: 0.5, width: createdAtSize.width, height: createdAtSize.height)
//        let contentSize = self.messageModel!.contentSize()
        let contentSize = contentLabel!.sizeThatFits(CGSizeMake(300, CGFLOAT_MAX))
        //[contentLabel sizeThatFits:CGSizeMake(200, CGFLOAT_MAX)];
        contentLabel?.frame = CGRect(x: (self.bounds.size.width - contentSize.width - 10)/2, y: 20, width: contentSize.width, height: contentSize.height)
    }
    
}
