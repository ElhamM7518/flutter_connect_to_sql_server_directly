import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'connect_to_sql_server_directly_method_channel.dart';

abstract class ConnectToSqlServerDirectlyPlatform extends PlatformInterface {
  /// Constructs a ConnectToSqlServerDirectlyPlatform.
  ConnectToSqlServerDirectlyPlatform() : super(token: _token);

  static final Object _token = Object();

  static ConnectToSqlServerDirectlyPlatform _instance =
      MethodChannelConnectToSqlServerDirectly();

  /// The default instance of [ConnectToSqlServerDirectlyPlatform] to use.
  ///
  /// Defaults to [MethodChannelConnectToSqlServerDirectly].
  static ConnectToSqlServerDirectlyPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ConnectToSqlServerDirectlyPlatform] when
  /// they register themselves.
  static set instance(ConnectToSqlServerDirectlyPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool> initializeConnection(String serverIp, String database,
      String userName, String password, String instance) async {
    throw UnimplementedError('initializeConnection has not been implemented.');
  }

  Future<dynamic> getRowsOfQueryResult(String queryString) async {
    throw UnimplementedError('getRowsOfQueryResult has not been implemented.');
  }

  Future<dynamic> getStatusOfQueryResult(String queryString) async {
    throw UnimplementedError('getStatusOfQueryResult has not been implemented.');
  }
}
