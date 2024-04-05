////
////  SwiftUIView.swift
////  
////
////  Created by 宁金鹏 on 2023/8/17.
////
//
//import SwiftUI
//
//
//public struct BDChatView: View {
//    //
//    private var uid: String
//    private var type = BD_THREAD_REQUEST_TYPE_WORK_GROUP
//    //
//    let messages: [BDMessageModel] = [] //mock(name: "messages")
////
//    public init(uid: String, type: String) {
//        self.uid = uid
//        self.type = type
//        // debugPrint("BDchatview init uid: \(self.uid), type: \(self.type)")
//    }
//
//    public var body: some View {
//        GeometryReader { proxy in
//            VStack {
//                Separator(color: Color("navigation_separator", bundle: .module))
//                
//                List {
//                    Group {
//                        ForEach(self.messages) { message in
//                            if message.timestamp != nil {
//                                Time(date: message.timestamp!)
//                            }
//                            //
////                            BDChatCell(
////                                message: message,
////                                isMe: message.user!.uid == "sen_baby"
////                            )
//                        }
//                    }
//                    .listRowBackground(Color.clear)
////                    .listRowBackground(Color("light_gray", bundle: .module))
//                    .listRowInsets(.zero)
//                }
//
//                Send(proxy: proxy)
//            }
//        }
//        .background(Color.clear)
////        .background(Color("light_gray", bundle: .module))
//        .edgesIgnoringSafeArea(.bottom)
//        .navigationBarTitle("萝卜丝", displayMode: .inline)
//        .onAppear {
//    //        TODO: 请求客服会话
////            if (self.type == BD_THREAD_REQUEST_TYPE_WORK_GROUP) {
////                BDHttpApis.requestThreadWorkgroup(workgroupWid: self.uid) { threadResult in
////                    // debugPrint("requestThreadWorkgroup success: \(threadResult.data!.content!)")
////                } onFailure: { error in
////                    // debugPrint("requestThreadWorkgroup failed: \(error)")
////                }
////            } else {
////                BDHttpApis.requestThreadAgent(agentUid: self.uid) { threadResult in
////                    // debugPrint("requestThreadAgent success: ")
////                } onFailure: { error in
////                    // debugPrint("requestThreadAgent failed: \(error)")
////                }
////            }
//
//    //        TODO: 发送消息
//            
//
//    //        TODO: 加载本地历史会话消息
//        }
//    }
//}
//
//struct SwiftUIView_Previews: PreviewProvider {
//    static var previews: some View {
////        BDChatView(uid: "201807171659201", type: "workgroup")
//    }
//}
//
//private struct Time: View {
////    let date: Date
//    let date: String
//
//    var body: some View {
////        Text(date.formatString)
//        Text(date)
//            .foregroundColor(Color("chat_time", bundle: .module))
//            .font(.system(size: 14, weight: .medium))
//            .frame(maxWidth: .infinity)
//            .padding(.vertical, 4)
//    }
//}
////
//private struct Send: View {
//    let proxy: GeometryProxy
//
//    @State var text: String = ""
//
//    var body: some View {
//        VStack(spacing: 0) {
//            Separator(color: Color("chat_send_line", bundle: .module))
//
//            ZStack {
//                Color("chat_send_background", bundle: .module)
//
//                VStack {
//                    HStack(spacing: 12) {
//                        Image("chat_send_voice", bundle: .module)
//
//                        TextField("", text: $text)
//                            .frame(height: 40)
//                            .background(Color("chat_send_text_background", bundle: .module))
//                            .cornerRadius(4)
//
//                        Image("chat_send_emoji", bundle: .module)
//                        Image("chat_send_more", bundle: .module)
//                    }
//                    .frame(height: 56)
//                    .padding(.horizontal, 12)
//
//                    Spacer()
//                }
//            }
//            .frame(height: proxy.safeAreaInsets.bottom + 56)
//        }
//    }
//}
//
////public struct BDChatView: View {
////
////    @State private var inputText = ""
////    @State private var isSendingText = false
////
////    public init() {
//////        // debugPrint("BDchatview init")
////
//////        BDHttpApis.registerAnonymous()
////    }
////
////    public var body: some View {
////        GeometryReader { proxy in
////            VStack {
////
////
////
////
////                Send(proxy: proxy)
////            }
////        }
////
////    }
////
////    private func sendTextMessage() {
////        isSendingText = true
////// // {\n  \"status_code\" : 200,\n  \"message\" : \"抓取美语VOA状态\",\n  \"data\" : true\n}"
////        BDHttpApis.sendTextMessage(content: inputText) { json in
////            // debugPrint("send success: \(inputText), \(json)")
////            inputText = "";
////        } onFailure: { json in
////            // debugPrint("send failed: \(json)")
////        }
////    }
////
////}
////
////struct SwiftUIView_Previews: PreviewProvider {
////    static var previews: some View {
////        BDChatView()
////    }
////}
//
////                Text("Hello, Bytedesk!")
////                Image("chat_send_more", bundle: .module)
////    //                .resizable()
////                    .frame(width: 50)
//
////                TextField(
////                        "input content",
////                        text: $inputText
////                    )
////                    .disableAutocorrection(true)
//
////                Button(action: sendTextMessage) {
////                    if isSendingText {
////                        Text("发送中")
////                    } else {
////                        Text("发送消息")
////                    }
////                }
//
