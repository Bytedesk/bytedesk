//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/8.
//

import UIKit

extension BDChatKFViewController: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    //
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        // debugPrint("chatkf: \(#function)")
        picker.dismiss(animated: true) {
            self.perform(#selector(self.dealWithImage(_:)), with: info)
        }
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        // debugPrint("chatkf: \(#function)")
        picker.dismiss(animated: true)
    }
    
    @objc func dealWithImage(_ info: [UIImagePickerController.InfoKey: Any]) {
        // debugPrint("chatkf: \(#function)")
        
        if let mediaType = info[.mediaType] as? String {
            if mediaType == "public.image" {
                if var image = info[.originalImage] as? UIImage {
                    // Handle the selected image for uploading
                    let imageOrientation = image.imageOrientation
                    if imageOrientation != .up {
                        UIGraphicsBeginImageContext(image.size)
                        image.draw(in: CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height))
                        image = UIGraphicsGetImageFromCurrentImageContext()!
                        UIGraphicsEndImageContext()
                    }
                    let imageData = image.jpegData(compressionQuality: 0.6)
                    let imageWidth = image.size.width
                    let imageHeight = image.size.height
                    let fileName = String(format: "%@_%d_%d_%@.png", BDUtils.getCurrentTimeString(), imageWidth, imageHeight, BDSettings.getUsername()!)
                    BDCoreApis.uploadImage(imageData: imageData, fileName: fileName) { uploadResult in
                        // debugPrint("url: \(uploadResult.data!)")
                        let imageUrl = uploadResult.data
                        self.sendImageMessage(imageUrl)
                    } onFailure: { error in
                        BDToast.show(message: error)
                    }
                }
            } else if mediaType == "public.movie" {
                if let videoURL = info[.mediaURL] as? URL {
                    // Handle the selected video for uploading
                    // debugPrint("videoUrl \(videoURL)")
//                    uploadVideo(videoURL)
                }
            }
        }
        

    }
}
