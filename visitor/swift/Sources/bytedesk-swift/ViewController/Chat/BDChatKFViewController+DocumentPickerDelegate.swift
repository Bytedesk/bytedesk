//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/8.
//

import UIKit

extension BDChatKFViewController: UIDocumentPickerDelegate, UIDocumentInteractionControllerDelegate {
    
    
    func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
         debugPrint("chatkf: \(#function)")
        guard let selectedURL = urls.first else {
            return
        }
        if(iCloudEnable()) {
            // Perform upload operation with the selected document URL
            // Example: Upload(selectedURL)
            print("Selected document URL: \(selectedURL)")
            do {
                let fileData = try Data(contentsOf: selectedURL)
                let fileName = selectedURL.lastPathComponent
                //
                BDCoreApis.uploadFile(fileData: fileData, fileName: fileName) { uploadResult in
                    let fileUrl = uploadResult.data
                    self.sendFileMessage(fileUrl)
                } onFailure: { error in
                    BDToast.show(message: error)
                }
            } catch {
                print("Error reading file: \(error)")
            }
        } else {
            // MARK: 如果报错无法读取文件："开发者首先需要到Apple开发者后台开启iCloud权限，iCloud未启用, 参考：https://www.weikefu.net/assets/spm/icloud-dev.png，或 https://www.weikefu.net/assets/spm/icloud-xcode.png"
            BDToast.show(message: "iCloud未启用，开发者首先需要到Apple开发者后台开启iCloud权限")
        }
        
        //
    }
    
    func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
         debugPrint("chatkf: \(#function)")
        
    }
    
    // MARK:
    
    func iCloudEnable() -> Bool {
        NSLog("%@", #function)
        
        return defaultiCloudURL() != nil
    }

    func defaultiCloudURL() -> URL? {
        NSLog("%@", #function)
        
        let fileManager = FileManager.default
        let url = fileManager.url(forUbiquityContainerIdentifier: nil)
        return url
    }
    
}
