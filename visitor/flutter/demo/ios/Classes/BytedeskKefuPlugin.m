#import "BytedeskKefuPlugin.h"
#if __has_include(<bytedesk_kefu/bytedesk_kefu-Swift.h>)
#import <bytedesk_kefu/bytedesk_kefu-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "bytedesk_kefu-Swift.h"
#endif

@implementation BytedeskKefuPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBytedeskKefuPlugin registerWithRegistrar:registrar];
}
@end
