// swift-tools-version: 5.8
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "bytedesk-swift",
    platforms: [
        .iOS(.v13),
        .macOS(.v13)
    ],
    products: [
        // Products define the executables and libraries a package produces, and make them visible to other packages.
        .library(
            name: "bytedesk-swift",
            targets: ["bytedesk-swift"]),
    ],
    dependencies: [
        // 添加完dependencies之后，到项目跟目录底下执行: swift build
        .package(url: "https://gitee.com/270580156/Alamofire", from: "5.7.1"),
        .package(url: "https://gitee.com/270580156/Kingfisher", .upToNextMajor(from: "7.0.0")),
        .package(url: "https://gitee.com/270580156/CocoaMQTT", from: "2.1.6"),
        .package(url: "https://gitee.com/270580156/SQLite.swift", from: "0.14.1"),
        .package(url: "https://gitee.com/270580156/swift-protobuf", from: "1.22.1")
    ],
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages this package depends on.
        .target(
            name: "bytedesk-swift",
            dependencies: [
                "CocoaMQTT",
                .product(name: "CocoaMQTTWebSocket", package: "CocoaMQTT"),
                "Alamofire",
                "Kingfisher",
                .product(name: "SwiftProtobuf", package: "swift-protobuf"),
                .product(name: "SQLite", package: "SQLite.swift"),
            ],
            resources: [
                .process("Resources/Media.xcassets"),
                .process("Resources/EmotionToText.plist"),
                .process("Resources/TextToEmotion.plist")
            ]
        ),
        .testTarget(
            name: "bytedesk-swiftTests",
            dependencies: ["bytedesk-swift"]),
    ]
)
