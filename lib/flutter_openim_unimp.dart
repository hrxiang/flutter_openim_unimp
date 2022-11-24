import 'package:flutter/services.dart';

class FlutterOpenimUnimp {
  static const _channel = MethodChannel('flutter_openim_unimp');

  FlutterOpenimUnimp._();

  static final FlutterOpenimUnimp _instance = FlutterOpenimUnimp._();

  factory FlutterOpenimUnimp() => _instance;

  Future<bool?> isInitialize() {
    return _channel.invokeMethod<bool>('isInitialize');
  }

  Future releaseWgtToRunPath({
    required String appID,
    required String wgtPath,
    String? password,
    Map<String, dynamic>? extraData,
    Map<String, dynamic>? arguments,
    String? redirectPath,
    String? path,
    String? splashClassName,
  }) {
    return _channel.invokeMethod(
      'releaseWgtToRunPath',
      {
        'appID': appID,
        'wgtPath': wgtPath,
        'password': password,
        'extraData': extraData,
        'arguments': arguments,
        'redirectPath': redirectPath,
        'path': path,
        'splashClassName': splashClassName,
      },
    );
  }

  Future openUniMP({
    required String appID,
    Map<String, dynamic>? extraData,
    Map<String, dynamic>? arguments,
    String? redirectPath,
    String? path,
    String? splashClassName,
  }) {
    return _channel.invokeMethod(
      'openUniMP',
      {
        'appID': appID,
        'extraData': extraData,
        'arguments': arguments,
        'redirectPath': redirectPath,
        'path': path,
        'splashClassName': splashClassName,
      },
    );
  }

  Future getAppVersionInfo({
    required String appID,
  }) {
    return _channel.invokeMethod(
      'getAppVersionInfo',
      {
        'appID': appID,
      },
    );
  }

  Future sendUniMPEvent({
    required String appID,
    required String event,
    String? data,
  }) {
    return _channel.invokeMethod(
      'sendUniMPEvent',
      {
        'appID': appID,
        'event': event,
        'data': data,
      },
    );
  }

  void setUniMPListener({
    Function()? onInitFinished,
    Function(String appid, String id)? onDefMenuButtonClick,
    Function(String appid, String event, dynamic data)? onUniMPEvent,
  }) {
    _channel.setMethodCallHandler((call) {
      if (call.method == "initialize") {
        onInitFinished?.call();
      } else if (call.method == "setDefMenuButtonClickCallBack") {
        String appid = call.arguments['appid'];
        String id = call.arguments['id'];
        onDefMenuButtonClick?.call(appid, id);
      } else if (call.method == "setOnUniMPEventCallBack") {
        String appid = call.arguments['appid'];
        String event = call.arguments['event'];
        dynamic data = call.arguments['data'];
        onUniMPEvent?.call(appid, event, data);
      }
      return Future.value(null);
    });
  }
}
