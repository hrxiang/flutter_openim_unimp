import Flutter
import UIKit

public class SwiftFlutterOpenimUnimpPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_openim_unimp", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterOpenimUnimpPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
