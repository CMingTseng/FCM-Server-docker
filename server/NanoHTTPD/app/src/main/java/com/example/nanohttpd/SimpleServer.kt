package com.example.nanohttpd

import android.content.res.AssetManager
import android.util.Log
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import tw.gov.president.cks.fcm.data.FCMToken
import java.io.IOException

class SimpleServer(hostname: String?="127.0.0.1", port: Int=8890) : NanoHTTPD(hostname, port) {
    @JvmField
    var asset_mgr: AssetManager? = null

    companion object {
        private const val TAG = "SimpleServer"
        private val sGson = Gson()
    }

    override fun serve(session: IHTTPSession?): Response {
        return if (session == null) {
           defaultResponse()
        } else {
            val method = session.method
            processSession(session)

        }
    }

    private fun processSession(session: IHTTPSession): Response {
        val method = session.method
        Log.e(TAG, "Show get http method  : $method")
        var queryParams = ""
        var parameters: Map<String, List<String>>? = HashMap()
        var headers: Map<String?, String> = HashMap()
        var uri = ""
        try {
            session.parseBody(HashMap())
            parameters = session.parameters
            headers = session.headers
            queryParams = session.queryParameterString
            uri = session.uri
            Log.e(TAG, "Show header remote-addr : " + headers["remote-addr"])
            Log.e(TAG, "Show method  queryParams : $queryParams")
            when (method) {
                Method.HEAD -> {
                    return   headResponse(session)
                }
                Method.DELETE ->{
                    return  deleteResponse(session)
                }
                Method.OPTIONS -> {
                    return optionsResponse(session)
                }
                Method.PUT ->{
                    return putResponse(session)
                }
                Method.POST -> {
                    Log.e(TAG, "POST method  queryParams : $queryParams")
                    return  postResponse(headers,parameters,queryParams,uri)
                }
                Method.GET -> {
                    return getResponse(session)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ResponseException) {
            e.printStackTrace()
        }
        return super.serve(session)

    }

    private fun headResponse(session: IHTTPSession): Response {
        session.parseBody(HashMap())
        var parameters = session.parameters
        var headers  = session.headers
        var uri = session.uri
        var queryParams = session.queryParameterString
        return responseJsonString(404, "", "Request not support!")
    }

    private fun getResponse(session: IHTTPSession): Response {
        session.parseBody(HashMap())
        var parameters = session.parameters
        var headers  = session.headers
        var uri = session.uri
        var queryParams = session.queryParameterString
        var len = 0
        var buffer: ByteArray? = null
        var file_name = uri.substring(1)
        if (file_name.equals("", ignoreCase = true)) {
            file_name = "index.html"
            try {
                val `in` = asset_mgr!!.open(file_name, AssetManager.ACCESS_BUFFER)
                buffer = ByteArray(1024 * 1024)
                var temp = 0
                while (`in`.read().also { temp = it } != -1) {
                    buffer[len] = temp.toByte()
                    len++
                }
                `in`.close()
                return super.serve(session)
            } catch (e: IOException) {
                e.printStackTrace()
                return responseJsonString(404, "It's nothing!", "Success！")
            }
        }else{
            var b: Int = parameters["number"]?.get(0).toString().toInt()
            for (i in 0..100){ b += 1 }
            return responseJsonString(200, b, "Success！")
        }
    }

    private fun postResponse(headers: Map<String?, String>,parameters: Map<String, List<String>>?,queryParams:String,uri:String): Response {
        Log.e(TAG, "POST method  queryParams : $queryParams")
        val token = sGson.fromJson(queryParams, FCMToken::class.java)
//        val token = FCMToken();
        Log.e(TAG, "POST method  queryParams get token  : $token")
        return responseJsonString(200, "{}", "请求成功！")
    }

    private fun deleteResponse(session: IHTTPSession): Response {

        var parameters = session.parameters
        var headers  = session.headers
        var uri = session.uri
        var queryParams = session.queryParameterString
        return responseJsonString(404, "", "Request not support!")
    }

    private fun putResponse(session: IHTTPSession): Response {
        session.parseBody(HashMap())
        var parameters = session.parameters
        var headers  = session.headers
        var uri = session.uri
        var queryParams = session.queryParameterString
        return responseJsonString(404, "", "Request not support!")
    }

    private fun optionsResponse(session: IHTTPSession): Response {
        session.parseBody(HashMap())
        var parameters = session.parameters
        var headers  = session.headers
        var uri = session.uri
        var queryParams = session.queryParameterString
        return responseJsonString(404, "", "Request not support!")
    }

    private fun defaultResponse(): Response {
        return responseJsonString(404, "", "Request method not support!")
    }

    private fun <T : Any> responseJsonString(code: Int, data: T, msg: String): Response {
        val responser = Responser<T>(code, data, msg)
        return newFixedLengthResponse(sGson.toJson(responser))
    }

    //    //    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {
//    //        int len = 0;
//    //        byte[] buffer = null;
//    //        Log.e(TAG, "Show header remote-addr : "+header.get("remote-addr"));
//    //        String file_name = uri.substring(1);
//    //        if (file_name.equalsIgnoreCase("")) {
//    //            file_name = "index.html";
//    //        }
//    //        try {
//    //            InputStream in = asset_mgr.open(file_name, AssetManager.ACCESS_BUFFER);
//    //            buffer = new byte[1024 * 1024];
//    //
//    //            int temp = 0;
//    //            while ((temp = in.read()) != -1) {
//    //                buffer[len] = (byte) temp;
//    //                len++;
//    //            }
//    //            in.close();
//    //        } catch (IOException e) {
//    //            // TODO Auto-generated catch block
//    //            e.printStackTrace();
//    //        }
//    //        return new NanoHTTPD.Response(new String(buffer, 0, len));
//    //    }
//    //Ref : https://blog.csdn.net/rookie_wei/article/details/73614493
//    //https://www.796t.com/post/YWt2ZTA=.html
//    override fun serve(session: IHTTPSession): Response {
//        val method = session.method
//        Log.e(TAG, "Show get http method  : $method")
//        var queryParams = ""
//        var parameters: Map<String?, String?>? = HashMap()
//        var headers: Map<String?, String> = HashMap()
//        var uri = ""
//        try {
//            session.parseBody(HashMap())
//            parameters = session.parms
//            headers = session.headers
//            queryParams = session.queryParameterString
//            uri = session.uri
//            Log.e(TAG, "Show header remote-addr : " + headers["remote-addr"])
//            Log.e(TAG, "Show method  queryParams : $queryParams")
//            when (method) {
//                Method.HEAD -> Log.e(TAG, "HEAD method  : $method")
//                Method.DELETE -> Log.e(TAG, "DELETE method  : $method")
//                Method.OPTIONS -> Log.e(TAG, "OPTIONS method  : $method")
//                Method.PUT -> Log.e(TAG, "PUT method  : $method")
//                Method.POST -> {
//                    Log.e(TAG, "POST method  queryParams : $queryParams")
//                    val token = gson.fromJson(queryParams, FCMToken::class.java)
//                    Log.e(TAG, "POST method  queryParams get token  : $token")
//                }
//                Method.GET -> {
//                    Log.e(TAG, "Get method  queryParams : $queryParams")
//                    var len = 0
//                    var buffer: ByteArray? = null
//                    var file_name = uri.substring(1)
//                    if (file_name.equals("", ignoreCase = true)) {
//                        file_name = "index.html"
//                    }
//                    try {
//                        val `in` = asset_mgr!!.open(file_name, AssetManager.ACCESS_BUFFER)
//                        buffer = ByteArray(1024 * 1024)
//                        var temp = 0
//                        while (`in`.read().also { temp = it } != -1) {
//                            buffer[len] = temp.toByte()
//                            len++
//                        }
//                        `in`.close()
//                    } catch (e: IOException) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace()
//                    }
//                }
//                else -> {
//                    Log.e(TAG, "Get method  queryParams : $queryParams")
//                    var len = 0
//                    var buffer: ByteArray? = null
//                    var file_name = uri.substring(1)
//                    if (file_name.equals("", ignoreCase = true)) {
//                        file_name = "index.html"
//                    }
//                    try {
//                        val `in` = asset_mgr!!.open(file_name, AssetManager.ACCESS_BUFFER)
//                        buffer = ByteArray(1024 * 1024)
//                        var temp = 0
//                        while (`in`.read().also { temp = it } != -1) {
//                            buffer[len] = temp.toByte()
//                            len++
//                        }
//                        `in`.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } catch (e: ResponseException) {
//            e.printStackTrace()
//        }
//        return super.serve(session)
//    }

}