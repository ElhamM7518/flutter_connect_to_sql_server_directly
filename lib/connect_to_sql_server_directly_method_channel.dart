import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'connect_to_sql_server_directly_platform_interface.dart';

/// An implementation of [ConnectToSqlServerDirectlyPlatform] that uses method channels.
class MethodChannelConnectToSqlServerDirectly
    extends ConnectToSqlServerDirectlyPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('connect_to_sql_server_directly');

  @override
  Future<bool> initializeConnection(String serverIp, String database,
      String userName, String password, String instance) async {
    bool result;
    try {
      result = await methodChannel.invokeMethod(
        'initDbConnection',
        {
          "server": serverIp,
          "database": database,
          "username": userName,
          "password": password,
          "instance": instance
        },
      );
    } on PlatformException catch (e) {
      print(e.message);
      result = false;
    }
    return result;
  }

  @override
  Future<dynamic> getRowsOfQueryResult(String queryString) async {
    List<dynamic> result;
    String errorRes = "";
    try {
      result = json.decode(
        await methodChannel.invokeMethod(
          'getQueryResult',
          queryString,
        ),
      );
      return result;
    } on PlatformException catch (e) {
      errorRes = e.message!;
      print("in middleware exception : ${e.message}");
      return errorRes;
    }
  }

  @override
  Future<dynamic> getStatusOfQueryResult(String queryString) async {
    bool result;
    try {
      result = await methodChannel.invokeMethod(
        'executeQuery',
        queryString,
      );
      return result;
    } on PlatformException catch (e) {
      print("in middleware exception : ${e.message}");
      return e.message;
    }
  }
}
