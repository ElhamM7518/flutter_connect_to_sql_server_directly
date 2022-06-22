package com.example.connect_to_sql_server_directly

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.sql.*
import java.util.concurrent.*
import kotlin.coroutines.*

/** ConnectToSqlServerDirectlyPlugin */
class ConnectToSqlServerDirectlyPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var server: String? = ""
    private var instance: String? = ""
    private var username: String? = ""
    private var password: String? = ""
    private var database: String? = ""

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel =
            MethodChannel(flutterPluginBinding.binaryMessenger, "connect_to_sql_server_directly")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "initDbConnection") {
            val initRes = initDbConnection(
                call.argument<String>("server"),
                call.argument<String>("database"),
                call.argument<String>("username"),
                call.argument<String>("password"),
                call.argument<String>("instance")
            )
            if (initRes) {
                result.success(initRes)
            } else {
                result.error("initFailed", "Failed to init connection.", null)
            }
        } else if (call.method == "getQueryResult") {
            val queryResult = getQueryResult(
                call.arguments as String
            )
            if (queryResult.first == null && queryResult.second != null) {
                result.success(queryResult.second)
            } else {
                result.error("failed", queryResult.first, null)
            }
        } else if (call.method == "executeQuery") {
            val res = runBlocking {
                async {
                    executeQuery(
                        call.arguments as String
                    )
                }.await()
            }
            if (res == Pair(true, "Success!")) {
                result.success(true)
            } else {
                result.error("failed", res.second, null)
            }
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun initDbConnection(
        server: String?,
        database: String?,
        username: String?,
        password: String?,
        instance: String?
    ): Boolean {
        this.server = server
        this.database = database
        this.username = username
        this.password = password
        this.instance = instance
        if (server == null || server.trim() == "") {
            return false
        } else if (database == null || database.trim() == "") {
            return false
        } else if (instance == null) {
            return false
        } else if (username == null) {
            return false
        } else if (password == null) {
            return false
        } else {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
            } catch (ex: ClassNotFoundException) {
                println("catch : $ex");
                return false
            }
        }
        return true
    }

    private suspend fun connect(
        jdbcConnectionString: String,
        username: String?,
        password: String?
    ): Connection? {
        return GlobalScope.async {
            return@async DriverManager.getConnection(
                jdbcConnectionString,
                username,
                password
            )
        }.await();
    }

    private fun getQueryResult(
        queryString: String
    ): Pair<String?, String?> {
        var connection: Connection? = null
        var result: Pair<String?, JSONArray?> = Pair("Error!", null)
        try {
            val jdbcConnectionString =
                "jdbc:jtds:sqlserver://" + server.toString() + "/" + database.toString() + ";instance=" + instance
            runBlocking {
                connection = async { connect(jdbcConnectionString, username, password) }.await()
                if (connection != null) {
                    runBlocking {
                        result = async { getQuery(connection, queryString) }.await()!!
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            println("catch : $ex")
            connection?.close()
            return Pair("Error: $ex", null);
        } finally {
            try {
                if (connection != null) {
                    if (!connection!!.isClosed()) {
                        connection!!.close();
                    }
                }
            } catch (ex: ClassNotFoundException) {
                ex.printStackTrace();
            }
        }
        connection?.close()
        return Pair(null, result.second.toString())
    }

    private suspend fun getQuery(
        connection: Connection?,
        queryString: String
    ): Pair<String?, JSONArray?> {
        val json = JSONArray()
        var status: String? = null
        var stmt: Statement? = null
        var rs: ResultSet? = null
        GlobalScope.async {
            try {
                stmt = connection!!.createStatement()
                rs = stmt!!.executeQuery(queryString)
                val rsmd = rs!!.metaData
                while (rs!!.next()) {
                    val numColumns: Int = rsmd.getColumnCount()
                    val obj = JSONObject()
                    for (i in 1..numColumns) {
                        val column_name: String = rsmd.getColumnName(i)
                        if (rsmd.getColumnType(i) == 2005) {
                            obj.put(column_name, rs!!.getString(column_name))
                        } else {
                            obj.put(column_name, rs!!.getObject(column_name))
                        }
                    }
                    json.put(obj)
                }
            } catch (ex: ClassNotFoundException) {
                println("catch : $ex")
                status = "Error: $ex"
            }
        }.await()
        return Pair(status, json)
    }

    private suspend fun executeQuery(
        queryString: String
    ): Pair<Boolean, String?> {
        var result: Pair<Boolean, String?> = Pair(false, "Failed!")
        var connection: Connection? = null
        var stmt: Statement? = null
        GlobalScope.async {
            try {
                val jdbcConnectionString =
                    "jdbc:jtds:sqlserver://" + server.toString() + "/" + database.toString() + ";instance=" + instance
                runBlocking {
                    connection = async { connect(jdbcConnectionString, username, password) }.await()
                    if (connection != null) {
                        runBlocking {
                            if (connection != null) {
                                stmt = connection!!.createStatement()
                                var res = stmt!!.executeUpdate(queryString)
                                result =
                                    if (res >= 1) Pair(true, "Success!") else Pair(false, "Failed!")
                            }
                        }
                    }
                }
            } catch (ex: java.lang.Exception) {
                println("catch : $ex")
                result = Pair(false, "$ex")
            } finally {
                try {
                    if (stmt != null) {
                        stmt!!.close();
                    }
                    if (connection != null) {
                        if (!connection!!.isClosed()) {
                            connection!!.close();
                        }
                    }
                } catch (ex: ClassNotFoundException) {
                    ex.printStackTrace();
                    result = Pair(false, "$ex")
                }
            }
        }.await()
        connection?.close()
        return result
    }
}
