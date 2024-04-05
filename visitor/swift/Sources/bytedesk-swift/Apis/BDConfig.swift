//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/19.
//

import Foundation

// key
let BD_MQTT_HOST_KEY                  = "BD_MQTT_HOST_KEY"
let BD_MQTT_PORT_KEY                  = "BD_MQTT_PORT_KEY"
let BD_MQTT_WEBSOCKET_WSS_URL_KEY     = "BD_MQTT_WEBSOCKET_WSS_URL_KEY"

let BD_WEBRTC_STUN_SERVER_KEY         = "BD_WEBRTC_STUN_SERVER_KEY"
let BD_WEBRTC_TURN_SERVER_KEY         = "BD_WEBRTC_TURN_SERVER_KEY"
let BD_WEBRTC_TURN_USERNAME_KEY       = "BD_WEBRTC_TURN_USERNAME_KEY"
let BD_WEBRTC_TURN_PASSWORD_KEY       = "BD_WEBRTC_TURN_PASSWORD_KEY"

let BD_REST_API_HOST_KEY              = "BD_REST_API_HOST_KEY"
let BD_UPLOAD_API_HOST_KEY            = "BD_UPLOAD_API_HOST_KEY"

// value
let BD_WEBRTC_STUN_SERVER               = "turn:turn.bytedesk.com:3478"
let BD_WEBRTC_TURN_SERVER               = "turn:turn.bytedesk.com:3478"
let BD_WEBRTC_TURN_USERNAME             = "jackning"
let BD_WEBRTC_TURN_PASSWORD             = "kX1JiyPGVTtO3y0o"

// 局域网测试
//let BD_IS_DEBUG                         = true
//let BD_TLS_CONNECTION                   = false
//let BD_IS_WEBSOCKET_WSS_CONNECTION      = false
//let BD_MQTT_PORT                        = 3883
//let BD_MQTT_HOST                        = "192.168.110.124"
//let BD_UPLOAD_HOST                      = "192.168.110.124"
//let BD_MQTT_WEBSOCKET_WSS_URL           = String(format: "wss://%@/websocket", BD_MQTT_HOST)
//let BD_REST_API_HOST                    = String(format: "http://%@:8000/", BD_MQTT_HOST)
//let BD_UPLOAD_API_HOST                  = String(format: "http://%@:8000/", BD_UPLOAD_HOST)

// 本地测试
//let BD_IS_DEBUG                         = true
//let BD_TLS_CONNECTION                   = false
//let BD_IS_WEBSOCKET_WSS_CONNECTION      = false
//let BD_MQTT_PORT                        = 3883
//let BD_MQTT_HOST                        = "127.0.0.1"
//let BD_UPLOAD_HOST                      = "127.0.0.1"
//let BD_MQTT_WEBSOCKET_WSS_URL           = String(format: "wss://%@/websocket", BD_MQTT_HOST)
//let BD_REST_API_HOST                    = String(format: "http://%@:8000/", BD_MQTT_HOST)
//let BD_UPLOAD_API_HOST                  = String(format: "http://%@:8000/", BD_UPLOAD_HOST)

// 线上服务器
let BD_IS_DEBUG                         = false
let BD_TLS_CONNECTION                   = true
let BD_IS_WEBSOCKET_WSS_CONNECTION      = true
let BD_MQTT_PORT                        = 3883
let BD_MQTT_HOST                        = "ios.bytedesk.com"
let BD_UPLOAD_HOST                      = "upload.bytedesk.com"
let BD_MQTT_WEBSOCKET_WSS_URL           = String(format: "/websocket")
let BD_REST_API_HOST                    = String(format: "https://%@/", BD_MQTT_HOST)
let BD_UPLOAD_API_HOST                  = String(format: "https://%@/", BD_UPLOAD_HOST)


//Swift 中的类如果要供Objective-C 调用，必须也继承自NSObject
public class BDConfig: NSObject {
    
    static func isDebug() -> Bool? {
        return BD_IS_DEBUG;
    }

    static func isTlsConnection() -> Bool? {
        return BD_TLS_CONNECTION;
    }

    static func isWebSocketWssConnection() -> Bool? {
        return BD_IS_WEBSOCKET_WSS_CONNECTION;
    }

    static func getMqttHost() -> String? {
        return UserDefaults.standard.string(forKey: BD_MQTT_HOST_KEY) ?? BD_MQTT_HOST
    }

    static func getMqttPort() -> Int {
        return BD_MQTT_PORT
//        return UserDefaults.standard.integer(forKey: BD_MQTT_PORT_KEY)
    }

    static func getMqttWebSocketWssURL() -> String? {
        return UserDefaults.standard.string(forKey: BD_MQTT_WEBSOCKET_WSS_URL_KEY) ?? BD_MQTT_WEBSOCKET_WSS_URL
    }

    
    
    
}
