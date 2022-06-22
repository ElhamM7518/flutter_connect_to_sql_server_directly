import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:connect_to_sql_server_directly/connect_to_sql_server_directly_method_channel.dart';

void main() {
  MethodChannelConnectToSqlServerDirectly platform = MethodChannelConnectToSqlServerDirectly();
  const MethodChannel channel = MethodChannel('connect_to_sql_server_directly');

  TestWidgetsFlutterBinding.ensureInitialized();
}
