//
//  MQTTPersistence.h
//  MQTTClient
//
//  Created by Christoph Krey on 22.03.15.
//  Copyright © 2015-2017 Christoph Krey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MQTTMessage.h"

static BOOL const MQTT_PERSISTENT = NO;
static NSInteger const MQTT_MAX_SIZE = 64 * 1024 * 1024;
static NSInteger const MQTT_MAX_WINDOW_SIZE = 16;
static NSInteger const MQTT_MAX_MESSAGES = 1024;

/** MQTTFlow is an abstraction of the entity to be stored for persistence */
 
@protocol MQTTFlow
/** The clientID of the flow element */
@property (strong, nonatomic) NSString *clientId;

/** The flag indicating incoming or outgoing flow element */
@property (strong, nonatomic) NSNumber *incomingFlag;

/** The flag indicating if the flow element is retained*/
@property (strong, nonatomic) NSNumber *retainedFlag;

/** The MQTTCommandType of the flow element, might be MQTT_None for offline queueing */
@property (strong, nonatomic) NSNumber *commandType;

/** The MQTTQosLevel of the flow element */
@property (strong, nonatomic) NSNumber *qosLevel;

/** The messageId of the flow element */
@property (strong, nonatomic) NSNumber *messageId;

/** The topic of the flow element */
@property (strong, nonatomic) NSString *topic;

/** The data of the flow element */
@property (strong, nonatomic) NSData *data;

/** The deadline of the flow elelment before (re)trying transmission */
@property (strong, nonatomic) NSDate *deadline;

@end

/** The MQTTPersistence protocol is an abstraction of persistence classes for MQTTSession */

@protocol MQTTPersistence

/** The maximum Window Size for outgoing inflight messages per clientID. Defaults to 16 */
@property (nonatomic) NSUInteger maxWindowSize;

/** The maximum number of messages kept per clientID and direction. Defaults to 1024 */
@property (nonatomic) NSUInteger maxMessages;

/** Indicates if the persistence implementation should make the information permannent. Defaults to NO */
@property (nonatomic) BOOL persistent;

/** The maximum size of the storage used for persistence in total in bytes. Defaults to 1024*1024 bytes */
@property (nonatomic) NSUInteger maxSize;

/** The current Window Size for outgoing inflight messages per clientID.
 * @param clientId identifying the session
 * @return the current size of the outgoing inflight window
 */
- (NSUInteger)windowSize:(NSString *)clientId;

/** Stores one new message
 * @param clientId identifying the session
 * @param topic the topic of the message
 * @param data the message's data
 * @param retainFlag the retain flag of the message
 * @param qos the quality of service of the message
 * @param msgId the id of the message or zero for qos zero
 * @param incomingFlag the direction of the message
 * @param commandType the command of the message
 * @param deadline the deadline of the message for repetitions
 * @return the created MQTTFlow element or nil if the maxWindowSize has been exceeded
 */
- (id<MQTTFlow>)storeMessageForClientId:(NSString *)clientId
                                  topic:(NSString *)topic
                                   data:(NSData *)data
                             retainFlag:(BOOL)retainFlag
                                    qos:(MQTTQosLevel)qos
                                  msgId:(UInt16)msgId
                           incomingFlag:(BOOL)incomingFlag
                            commandType:(UInt8)commandType
                               deadline:(NSDate *)deadline;

/** Deletes an MQTTFlow element
 * @param flow the MQTTFlow to delete
 */
- (void)deleteFlow:(id<MQTTFlow>)flow;

/** Deletes all MQTTFlow elements of a clientId
 * @param clientId the client Id identifying all MQTTFlows to be deleted
 */
- (void)deleteAllFlowsForClientId:(NSString *)clientId;

/** Retrieves all MQTTFlow elements of a clientId and direction
 * @param clientId whos MQTTFlows should be retrieved
 * @param incomingFlag specifies the wether incoming or outgoing flows should be retrieved
 * @return an NSArray of the retrieved MQTTFlow elements
 */
- (NSArray *)allFlowsforClientId:(NSString *)clientId
                    incomingFlag:(BOOL)incomingFlag;

/** Retrieves an MQTTFlow element
 * @param clientId to which the MQTTFlow belongs to
 * @param incomingFlag specifies the direction of the flow
 * @param messageId specifies the message Id of the flow
 * @return the retrieved MQTTFlow element or nil if the elememt was not found
 */
- (id<MQTTFlow>)flowforClientId:(NSString *)clientId
                   incomingFlag:(BOOL)incomingFlag
                      messageId:(UInt16)messageId;

/** sync is called to allow the MQTTPersistence implemetation to save data permanently */
- (void)sync;

@end
