package com.example.CareHeim


import com.example.CareHeim.Examples.Companion.variable1
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class Examples { //static 변수 선언
    companion object {
        var variable1 : String = ""
    }
}

public class Client : Thread()
{
    companion object{
        var socket = Socket()

        lateinit var output: DataOutputStream

        var ip = "220.70.62.115"
        var port = 8000
        var closed = false //소켓이 닫혔는지 여부
    }

    override fun run()
    {
        try{
            println("서버 연결 시작")
            socket = Socket(ip, port)
            println("success!")
            println("클라이언트 연결 여부:" +socket.isConnected)

        }catch (e: Exception){

            println(e)
            println("서버 연결 오류")
        }
    }


    class Send:Thread(){
        var Jjson = JSONObject()
        fun ja (json:JSONObject){
            try{
                //보내기
                //output = DataOutputStream(socket.getOutputStream())
                //val dataOutputStream = DataOutputStream(output)

                Jjson = json

                println("Client에서$Jjson") //{"device":1,"requestType":0,"body":{"user":"userID"}}  //{\"device\" : 1,\"requestType\" : 0,\"body\" : {\"user\" : \"userID\"}}

                run()

                //dataOutputStream.writeUTF(json.toString())

            }catch(e:Exception){
                println(e)
                println("send error - ja")
            }
        }

        override fun run() {
            println("Send의 Start")
            println("Run에서$Jjson")
            try{
                val thread = Thread { //서브 스레스로 사용
                    output = DataOutputStream(socket.getOutputStream())
                    val dataOutputStream = DataOutputStream(output)
                    dataOutputStream.writeUTF(Jjson.toString()) //UTF로 작성해서 서버로 전송
                }.start()


            }catch(e:Exception){
                println(e)
                println("send error - run")
            }
        }
    }

    class Get:Thread(){
        var c = ""

        override fun run(){ //받기
           // c = ""
            variable1 = "" //static 변수 속 값 초기화
            try{
                Thread { //서브 스레드 사용
                   val input = socket.getInputStream()
                   val dataInputStream = DataInputStream(input)

                    val stringData = dataInputStream.readUTF() //"{\"responseType\" : 0,\"user\" : \"userID\",\"body\" : {\"status\" : 3,\"clothe\" : {\"type\" : 1,\"ptn\" : 1,\"colors\" : [\"흰색\", \"검은색\"],\"features\" : [\"셔츠\"]}}}"
                    println("서버에서 받은 문자열 : $stringData")
                    variable1 = stringData //static 배열 내 저장

                }.start()


                //문자열 받기

            }catch(d:Exception){
                println(d)
                println("get error")
            }
        }

        fun aa():String{
           // var a = this.c
            return variable1 //static 배열 속 서버로 부터 받은 값 return
        }
    }


    class Disconnect:Thread(){ //서버 연결 해제 연락
        override fun run() {
            try{
                println("goto socket close")
                output = DataOutputStream(socket.getOutputStream())
                val dataOutputStream = DataOutputStream(output)

                //정상 종료 확인 전송
                println("{\"device\" : 1,\"requestType\" : 3}") //{"device":1,"requestType":3} //종료시에는 body없어도 됨
                dataOutputStream.writeUTF("{\"device\" : 1,\"requestType\" : 3}")

                //확인 답장 없어도 됨
                socket.close() //종료

            }catch(e:Exception){
                println("close error")
                println(e)
            }
        }
    }



}

