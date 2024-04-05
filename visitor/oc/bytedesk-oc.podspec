#
#  Be sure to run `pod spec lint bytedesk-oc.podspec' to ensure this is a
#  valid spec and to remove all comments including this before submitting the spec.
#
#  To learn more about Podspec attributes see https://guides.cocoapods.org/syntax/podspec.html
#  To see working Podspecs in the CocoaPods repo see https://github.com/CocoaPods/Specs/
#

Pod::Spec.new do |spec|
  spec.name         = "bytedesk-oc"
  spec.version      = "2.9.2"
  spec.summary      = "bytedesk.com helpdesk chat kefu lib."
  spec.description  = <<-DESC
  Online chat kefu Lib for ios, Helpdesk system
                   DESC
  spec.homepage     = "https://www.bytedesk.com"
  spec.license      = { :type => "MIT", :file => "LICENSE" }
  spec.author       = { "jack ning" => "270580156@qq.com" }
  #  When using multiple platforms
  spec.ios.deployment_target = "13.0"
  # spec.osx.deployment_target = "10.7"
  # spec.watchos.deployment_target = "2.0"
  # spec.tvos.deployment_target = "9.0"
  spec.source       = { :git => "https://github.com/Bytedesk/bytedesk-oc.git", :tag => "#{spec.version}" }
  spec.source_files  = "Classes", "bytedesk-oc/**/*.{h,m}"
  # spec.exclude_files = "Classes/Exclude"
  spec.public_header_files = "bytedesk-oc/*.h"
  # 
  spec.requires_arc = true
  # https://www.jianshu.com/p/9de438f8f406
  # https://github.com/CocoaPods/CocoaPods/issues/10104
  # spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
  # spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
  # 
  spec.dependency "AFNetworking", "~> 4.0.1"
  spec.dependency "Protobuf", "~> 3.21.11"

end
