//
// MQTTCFSocketEncoder.m
// MQTTClient.framework
//
// Copyright © 2013-2017, Christoph Krey. All rights reserved.
//

#import "MQTTCFSocketEncoder.h"

#import "MQTTLog.h"

@interface MQTTCFSocketEncoder()

@property (strong, nonatomic) NSMutableData *buffer;

@end

@implementation MQTTCFSocketEncoder

- (instancetype)init {
    self = [super init];
    self.state = MQTTCFSocketEncoderStateInitializing;
    self.buffer = [[NSMutableData alloc] init];
    self.stream = nil;
    return self;
}

- (void)dealloc {
    [self close];
}

- (void)open {
    (self.stream).delegate = self;
    [self.stream open];
}

- (void)close {
    [self.stream close];
    [self.stream setDelegate:nil];
}

- (void)setState:(MQTTCFSocketEncoderState)state {
    DDLogVerbose(@"[MQTTCFSocketEncoder] setState %ld/%ld", (long)_state, (long)state);
    _state = state;
}

- (void)stream:(NSStream *)sender handleEvent:(NSStreamEvent)eventCode {
    if (eventCode & NSStreamEventOpenCompleted) {
        DDLogVerbose(@"[MQTTCFSocketEncoder] NSStreamEventOpenCompleted");
    }
    if (eventCode & NSStreamEventHasBytesAvailable) {
        DDLogVerbose(@"[MQTTCFSocketEncoder] NSStreamEventHasBytesAvailable");
    }
    if (eventCode & NSStreamEventHasSpaceAvailable) {
        DDLogVerbose(@"[MQTTCFSocketEncoder] NSStreamEventHasSpaceAvailable");
        if (self.state == MQTTCFSocketEncoderStateInitializing) {
            self.state = MQTTCFSocketEncoderStateReady;
            [self.delegate encoderDidOpen:self];
        }
        
        if (self.state == MQTTCFSocketEncoderStateReady) {
            if (self.buffer.length) {
                [self send:nil];
            }
        }
    }
    if (eventCode & NSStreamEventEndEncountered) {
        DDLogVerbose(@"[MQTTCFSocketEncoder] NSStreamEventEndEncountered");
        self.state = MQTTCFSocketEncoderStateInitializing;
        self.error = nil;
        [self.delegate encoderdidClose:self];
    }
    if (eventCode & NSStreamEventErrorOccurred) {
        DDLogVerbose(@"[MQTTCFSocketEncoder] NSStreamEventErrorOccurred");
        self.state = MQTTCFSocketEncoderStateError;
        self.error = self.stream.streamError;
        [self.delegate encoder:self didFailWithError:self.error];
    }
}

- (BOOL)send:(NSData *)data {
    @synchronized(self) {
        if (self.state != MQTTCFSocketEncoderStateReady) {
            NSLog(@"[MQTTCFSocketEncoder] not MQTTCFSocketEncoderStateReady");
            return NO;
        }
        
        if (data) {
            [self.buffer appendData:data];
        }
        
        if (self.buffer.length) {
            DDLogVerbose(@"[MQTTCFSocketEncoder] buffer to write (%lu)=%@...",
                         (unsigned long)self.buffer.length,
                         [self.buffer subdataWithRange:NSMakeRange(0, MIN(256, self.buffer.length))]);
            
            NSInteger n = [self.stream write:self.buffer.bytes maxLength:self.buffer.length];
            
            if (n == -1) {
                DDLogVerbose(@"[MQTTCFSocketEncoder] streamError: %@", self.error);
                self.state = MQTTCFSocketEncoderStateError;
                self.error = self.stream.streamError;
                return NO;
            } else {
                if (n < self.buffer.length) {
                    DDLogVerbose(@"[MQTTCFSocketEncoder] buffer partially written: %ld", (long)n);
                }
                [self.buffer replaceBytesInRange:NSMakeRange(0, n) withBytes:NULL length:0];
            }
        }
        return YES;
    }
}

@end
