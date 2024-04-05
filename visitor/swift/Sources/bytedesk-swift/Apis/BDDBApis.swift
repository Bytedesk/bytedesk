//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/19.
//

import Foundation
import SQLite

// Swift 中的类如果要供Objective-C 调用，必须也继承自NSObject
public class BDDBApis {
    //
    var db: Connection?
    private var isTableCreated = false
    //
    let messageTable = Table("message")
    //
    let midColumn = Expression<String>("mid")
    let typeColumn = Expression<String?>("type")
    //
    let contentColumn = Expression<String?>("content")
    // 所有的URL类型暂时都存放在imageUrl中
    let imageUrlColumn = Expression<String?>("imageUrl")
    let createdAtColumn = Expression<String?>("createdAt")
    //
    let threadTidColumn = Expression<String?>("threadTid")
    let threadTopicColumn = Expression<String?>("threadTopic")
    let threadTypeColumn = Expression<String?>("threadType")
    //
    let statusColumn = Expression<String?>("status")
    let senderUidColumn = Expression<String?>("senderUid")
    //
    let currentUidColumn = Expression<String?>("currentUid")
    //
    init() {
        do {
            let path = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first!
            let bytedeskDBPath = path + "/db-v1.sqlite3"
//            let bytedeskDBPath = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("bytedesk-v1.db").path
             debugPrint("bytedeskDBPath \(bytedeskDBPath)")
            db = try Connection(bytedeskDBPath)
            //
            try createMessageTable()
        } catch {
            // debugPrint("BDDBApis init \(error)")
        }
    }
    //
    class func sharedInstance() -> BDDBApis {
        struct Static {
            static let instance = BDDBApis()
        }
        return Static.instance
    }
    
    // 创建message表
    func createMessageTable() throws {
        guard !isTableCreated else {
            return
        }
        // debugPrint("createMessageTable")
        //
        try db!.run(messageTable.create(ifNotExists: true) { table in
            table.column(midColumn, primaryKey: true)
            table.column(typeColumn)
            //
            table.column(contentColumn)
            table.column(imageUrlColumn)
            table.column(createdAtColumn)
            //
            table.column(threadTidColumn)
            table.column(threadTopicColumn)
            table.column(threadTypeColumn)
            //
            table.column(statusColumn)
            table.column(senderUidColumn)
            //
            table.column(currentUidColumn)
        })
        //
        isTableCreated = true
    }

    // 在message表中插入一条记录
    func insertMessage(_ messageModel: BDMessageModel) {
        // debugPrint("insertMessage \(messageModel.type!)")
        messageModel.currentUid = BDSettings.getUid()
        // TODO: 消息回执和消息预知 没有id字段, 暂不处理此类型，后续处理
        // TODO: 对于访客端暂时忽略连接状态信息
        if messageModel.type == BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT ||
            messageModel.type == BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW ||
            messageModel.type == BD_MESSAGE_TYPE_NOTIFICATION_CONNECT ||
            messageModel.type == BD_MESSAGE_TYPE_NOTIFICATION_DISCONNECT ||
            messageModel.type == BD_MESSAGE_TYPE_NOTIFICATION_KICKOFF ||
            // 弹窗处理
//            messageModel.type == BD_MESSAGE_TYPE_QUESTIONNAIRE ||
            messageModel.type == BD_MESSAGE_TYPE_WORKGROUP {
            return
        }
        if (messageModel.type == BD_MESSAGE_TYPE_ROBOT) {
            // debugPrint("insertMessage count \(messageModel.answers.count)")
            messageModel.content = BDUtils.appendAnswersToContent(messageModel)
        }
        //
        let insert = messageTable.insert(midColumn <- messageModel.mid!,
                                    typeColumn <- messageModel.type!,
                                    contentColumn <- messageModel.content,
                                    imageUrlColumn <- messageModel.imageUrl,
                                    createdAtColumn <- messageModel.createdAt,
                                    threadTidColumn <- messageModel.thread?.tid,
                                    threadTopicColumn <- messageModel.thread?.topic,
                                    threadTypeColumn <- messageModel.thread?.type,
                                    statusColumn <- messageModel.status,
                                    senderUidColumn <- messageModel.user?.uid,
                                    currentUidColumn <- messageModel.currentUid)
        do {
            try db!.run(insert)
        } catch {
            // debugPrint("BDDBApis insert \(error)")
        }
    }

    // 查询message表
    func queryMessage() -> [BDMessageModel] {
        // debugPrint("queryMessage")
        //
        var messages: [BDMessageModel] = []
        do {
            for row in try db!.prepare(messageTable) {
                let mid = row[midColumn]
                let type = row[typeColumn]
                let content = row[contentColumn]
                let imageUrl = row[imageUrlColumn]
                let createdAt = row[createdAtColumn]
                let threadTid = row[threadTidColumn]
                let threadTopic = row[threadTopicColumn]
                let threadType = row[threadTypeColumn]
                let status = row[statusColumn]
                let senderUid = row[senderUidColumn]
                let currentUid = row[currentUidColumn]
                //
                let message = BDMessageModel()
                message.mid = mid
                message.type = type
                message.content = content
                message.imageUrl = imageUrl
                message.createdAt = createdAt
                message.thread?.tid = threadTid
                message.thread?.topic = threadTopic
                message.thread?.type = threadType
                message.status = status
                message.user?.uid = senderUid
                message.currentUid = currentUid
                messages.append(message)
            }
        } catch {
            // debugPrint("BDDBApis query \(error)")
        }
        return messages
    }
    
    //
    func queryMessagesByThreadTid(_ threadTid: String) -> [BDMessageModel] {
        let query = messageTable.filter(threadTidColumn == threadTid && currentUidColumn == BDSettings.getUid())
        //
        var messages: [BDMessageModel] = []
        do {
            messages = try db!.prepare(query).map { row in
                let mid = row[midColumn]
                let type = row[typeColumn]
                let content = row[contentColumn]
                let imageUrl = row[imageUrlColumn]
                let createdAt = row[createdAtColumn]
                let threadTid = row[threadTidColumn]
                let threadTopic = row[threadTopicColumn]
                let threadType = row[threadTypeColumn]
                let status = row[statusColumn]
                let senderUid = row[senderUidColumn]
                let currentUid = row[currentUidColumn]
                //
                let message = BDMessageModel()
                message.mid = mid
                message.type = type
                message.content = content
                message.imageUrl = imageUrl
                message.createdAt = createdAt
                message.thread?.tid = threadTid
                message.thread?.topic = threadTopic
                message.thread?.type = threadType
                message.status = status
                message.user?.uid = senderUid
                message.currentUid = currentUid
                return message
            }
        } catch {
            // debugPrint("BDDBApis query \(error)")
        }
        return messages
    }
    
    //
    func queryMessagesByThreadTopic(_ threadTopic: String) -> [BDMessageModel] {
        let query = messageTable.filter(threadTopicColumn == threadTopic && currentUidColumn == BDSettings.getUid())
        //
        var messages: [BDMessageModel] = []
        do {
            messages = try db!.prepare(query).map { row in
                let mid = row[midColumn]
                let type = row[typeColumn]
                let content = row[contentColumn]
                let imageUrl = row[imageUrlColumn]
                let createdAt = row[createdAtColumn]
                let threadTid = row[threadTidColumn]
                let threadTopic = row[threadTopicColumn]
                let threadType = row[threadTypeColumn]
                let status = row[statusColumn]
                let senderUid = row[senderUidColumn]
                let currentUid = row[currentUidColumn]
                //
                let message = BDMessageModel()
                message.mid = mid
                message.type = type
                message.content = content
                message.imageUrl = imageUrl
                message.createdAt = createdAt
                message.thread?.tid = threadTid
                message.thread?.topic = threadTopic
                message.thread?.type = threadType
                message.status = status
                message.user?.uid = senderUid
                message.currentUid = currentUid
                return message
            }
        } catch {
            // debugPrint("BDDBApis query \(error)")
        }
        return messages
    }
    
    func clearMessageTable() {
        let messageTable = Table("message")
        let deleteQuery = messageTable.delete()
        do {
            try db!.run(deleteQuery)
            print("表message中的记录已成功清空")
        } catch {
            print("清空表message中的记录时出现错误: \(error)")
        }
    }
    
    func deleteMessagesByThreadTid(_ threadTid: String) {
        let query = messageTable.filter(threadTidColumn == threadTid)
        do {
            try db!.run(query.delete())
        } catch {
            print("清空表message中的记录时出现错误: \(error)")
        }
    }
    
    func deleteMessagesByThreadTopic(_ threadTopic: String) {
        let query = messageTable.filter(threadTopicColumn == threadTopic)
        do {
            try db!.run(query.delete())
        } catch {
            print("清空表message中的记录时出现错误: \(error)")
        }
    }
    
    func deleteMessageByMid(_ mid: String) {
        let query = messageTable.filter(midColumn == mid)
        do {
            try db!.run(query.delete())
        } catch {
            print("清空表message中的记录时出现错误: \(error)")
        }
    }
    
    
}
