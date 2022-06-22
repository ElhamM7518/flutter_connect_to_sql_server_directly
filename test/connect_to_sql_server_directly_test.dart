import 'package:flutter_test/flutter_test.dart';
import 'package:connect_to_sql_server_directly/connect_to_sql_server_directly_platform_interface.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockConnectToSqlServerDirectlyPlatform
    with MockPlatformInterfaceMixin
    implements ConnectToSqlServerDirectlyPlatform {
  @override
  Future getRowsOfQueryResult(String queryString) {
    // TODO: implement getRowsOfQueryResult
    throw UnimplementedError();
  }

  @override
  Future getStatusOfQueryResult(String queryString) {
    // TODO: implement getStatusOfQueryResult
    throw UnimplementedError();
  }

  @override
  Future<bool> initializeConnection(String server, String database,
      String userName, String password, String instance) {
    // TODO: implement initializeConnection
    throw UnimplementedError();
  }
}

void main() {}
