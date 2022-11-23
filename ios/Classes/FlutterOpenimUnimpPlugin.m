#import "FlutterOpenimUnimpPlugin.h"
#if __has_include(<flutter_openim_unimp/flutter_openim_unimp-Swift.h>)
#import <flutter_openim_unimp/flutter_openim_unimp-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_openim_unimp-Swift.h"
#endif

@implementation FlutterOpenimUnimpPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterOpenimUnimpPlugin registerWithRegistrar:registrar];
}
@end
