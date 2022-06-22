import 'connect_to_sql_server_directly_platform_interface.dart';

class ConnectToSqlServerDirectly {

  /// Pass the sql server info to this function to initialize connection
  /// Returns a boolean value whether the connection has been initialized
  ///
  /// [serverIp] => Sql Server Ip Address like "10.192.2.99"
  /// [database] => Sql Server Database Name like "SampleDB"
  /// [userName] => Sql Server Username to connect to the database like "admin"
  /// [password] => Sql Server Password to connect to the database like "1234"
  /// [instance] => Sql Server Instance Name like "node", not Required
  Future<bool> initializeConnection(
      String serverIp, String database, String userName, String password,
      {String instance = ""}) async {
    return ConnectToSqlServerDirectlyPlatform.instance
        .initializeConnection(serverIp, database, userName, password, instance);
  }

  /// Use this function to execute queries that return the results as a table
  /// Returns the Rows of Result Table as a List of dynamic values if query was executed correctly / Returns a String Value that has error message
  ///
  /// Sample : "select * from table1"
  ///
  /// [queryString] => Sql server Query as a String value
  Future<dynamic> getRowsOfQueryResult(String queryString) async {
    return ConnectToSqlServerDirectlyPlatform.instance
        .getRowsOfQueryResult(queryString);
  }

  /// Use this function to execute queries that affect on tables only
  /// Returns a True boolean value if query has affected on a table correctly / Returns a String Value that has error message
  ///
  /// Sample1 : "Insert Into table1 Values('id','name')"
  /// Sample2 : "Update table1 Set id = '1', name = 'elham'"
  /// Sample3 : "Delete From table1"
  ///
  /// [queryString] => Sql server Query as a String value
  Future<dynamic> getStatusOfQueryResult(String queryString) async {
    return ConnectToSqlServerDirectlyPlatform.instance
        .getStatusOfQueryResult(queryString);
  }
}
