//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/8.
//

import UIKit

// 下面函数使用 Gpt3.5 生成
// 示例： BDToast.show(message: "Hello, Toast!")
public class BDToast {
    static public func show(message: String, duration: TimeInterval = 3.0) {
        guard let rootView = UIApplication.shared.windows.first else {
            // debugPrint("can't toast \(message)")
            return
        }
        
        let toastLabel = UILabel(frame: CGRect(x: rootView.bounds.size.width/2 - 75, y: rootView.bounds.size.height-100, width: 150, height: 35))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .center
        toastLabel.font = UIFont.systemFont(ofSize: 14)
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.layer.cornerRadius = 10
        toastLabel.clipsToBounds = true
        
        rootView.addSubview(toastLabel)
        
        UIView.animate(withDuration: duration, delay: 0.1, options: .curveEaseOut, animations: {
            toastLabel.alpha = 0.0
        }, completion: { _ in
            toastLabel.removeFromSuperview()
        })
    }
}

// 显示： BDLoadingIndicator.shared.show()
// 隐藏： BDLoadingIndicator.shared.hide()
class BDLoadingIndicator {
    static let shared = BDLoadingIndicator()
    
    private var containerView: UIView?
    private var activityIndicator: UIActivityIndicatorView?
    
    func show() {
        if containerView == nil {
            let rootView = UIApplication.shared.windows.first
            containerView = UIView(frame: rootView?.bounds ?? CGRect.zero)
            containerView?.backgroundColor = UIColor.black.withAlphaComponent(0.6)
            
            activityIndicator = UIActivityIndicatorView(style: .large)
            activityIndicator?.center = containerView?.center ?? CGPoint.zero
            activityIndicator?.startAnimating()
            
            if let containerView = containerView, let activityIndicator = activityIndicator {
                containerView.addSubview(activityIndicator)
                rootView?.addSubview(containerView)
            }
        }
    }
    
    func hide() {
        activityIndicator?.stopAnimating()
        containerView?.removeFromSuperview()
        
        activityIndicator = nil
        containerView = nil
    }
}
