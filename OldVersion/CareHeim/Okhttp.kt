package com.example.CareHeim

import com.example.CareHeim.Example.Companion.variable2
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.net.URI.create
import java.net.URL
import java.util.concurrent.TimeUnit


class Example { //static 변수 선언
    companion object {
        var variable2 : String = ""
    }
}

class Okhttp {
        fun run(uri: ByteArray){ //https://www.techiedelight.com/ko/send-http-get-post-requests-kotlin/ 참고
            val ipAddress = "220.70.62.115" // Replace with the server's IP address
            val port = 8001 // Replace with the server's port number
           // val imageFile = File(uri)
            //println("imageFile: $imageFile")

            val imageFile = uri
            val url = URL("http://$ipAddress:$port/label")
            val postData = "foo1=bar1&foo2=bar2"

            val conn = url.openConnection()
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded") //수정 "" 이미지로
            conn.setRequestProperty("Content-Length", imageFile.size.toString())

            DataOutputStream(conn.getOutputStream()).use { uri } //it.writeBytes(postData)
            BufferedReader(InputStreamReader(conn.getInputStream())).use { bf ->
                var line: String?
                while (bf.readLine().also { line = it } != null) {
                    println(line)
                }
            }
        }
        /*fun run(uri: String){
            //val la = uri.path.toString()
           // println("uri.path: $la")
            //var la = ""
            val imageFile = File(uri)
            println("imageFile: $imageFile")

            var json = JSONObject()
            json.put("image", imageFile)


            val imageRequestBody: RequestBody = imageFile.asRequestBody() //.toRequestBody("image/jpg".toMediaType())

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = json.toString().toRequestBody(mediaType)



            //val ima = byteArray.toRequestBody()

            val ipAddress = "220.70.62.115" // Replace with the server's IP address
            val port = 8001 // Replace with the server's port number

            try{

                val thread = Thread { //서브 스레스로 사용
                    val client = OkHttpClient()

                    val request = Request.Builder()
                        .url("https://$ipAddress:$port/label")
                        .post(imageRequestBody)
                        .build() //GET Request


                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    println("Response: $responseBody")
                    variable2 = responseBody.toString()

                }.start()



            }catch (e: Exception){
                println(e)
                println("http 전송, 받기에서 오류 발생")
            }
        }*/
        fun re():String{
            if(variable2==""){
                return "오류"
            }
            else{
                return variable2
            }
        }
    }