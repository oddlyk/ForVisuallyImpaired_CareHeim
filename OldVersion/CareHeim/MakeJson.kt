package com.example.CareHeim

import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class MakeJson {
    fun QgetCl(): JSONObject { //저장된 의류의 정보 요청  //사용 시: Client.Send().ja(MakeJson().QgetCl())
        println("저장된 의류 정보 받아오기")

        //json 형태
        var json = JSONObject()
        json.put("device", 1)
        json.put("requestType", 0)
        json.put("user", "userID")

        //println("M"+json.toString()) //{"device":1,"requestType":0,"user":"userID"}
        //json 형태 끝

        return json
    }


    fun QfindCl(type:Int, ptn:Int, col:ArrayList<String>, fea:ArrayList<String>):JSONObject{ //특정 의류 정보 요청
        var json = JSONObject()
        json.put("device", 1)
        json.put("requestType", 1)
        json.put("user", "userID")

        var jsonB = JSONObject()
        var clothe = JSONObject()

        if (type != -1){ //type 값이 녹음되지 않은 경우 type input 제거
            clothe.put("type", type) //의류 종류가 녹음되지 않은 경우 "
        }
        if(ptn != -1){ //ptn 값이 잘못 된 경우
            clothe.put("ptn", ptn) //페턴 정보
        }


        if(col.size>0){ //색 배열
            var colors = JSONArray()
            for(i in 0 until col.size){
                colors.put(col[i]) //col 리스트가 들어감
            }
            clothe.put("colors",colors)
        }


        if(fea.size>0){ //특징 배열
            var features = JSONArray()
            for(i in 0 until fea.size){
                features.put(fea[i])//특징 개수 만큼 반복
            }
            clothe.put("features",features)
        }

        jsonB.put("clothe", clothe)
        json.put("body", jsonB)

        println("Make: "+json.toString())
        return json
    }

    fun make(speak:ArrayList<String>):JSONObject{
        var test = speak//말한것 저장 //arrayOf("반소매 상의", "동물 얼룩 무늬", "검은색","흰색","별","자수")


        //띄어쓰기 제거
        val len = test.size
        for(i in 0 until len){ //0~len-1까지 수행됨
            test[i] = test[i].replace(" ","") //띄어쓰기를 없애서 다시 넣음
        }

        val tyC = arrayOf("상의", "외투", "조끼", "민소매", "바지", "치만", "원피스")
        val ptC = "무늬"
        val coC = "색"

        var type = -1
        var ptn = -1
        var colors =  ArrayList<String>()
        var features= ArrayList<String>()


        var index = len-1
        loop1@ while(index >= 0) { //뒤에서 부터 앞쪽으로 돌리면서 의류 특징 추출
            var has = test[index]
            // 의류 종류 확인
            if(has.contains(tyC[0])){ //상의를 포함하는 단어일때
                when(test[index]){
                    "반소매상의" -> {
                        println("타입: 반소매 상의")
                        type = 0
                    }
                    "긴소매상의" -> {
                        println("타입: 긴소매 상의")
                        type = 1
                    }
                }
                when(test[index-1]){ //상의의 앞단어가 ** 일때
                    "반소매" -> {
                        println("타입: 반소매 상의")
                        type = 0
                        index=index-2
                        continue@loop1
                    }
                    "긴소매" -> {
                        println("타입: 긴소매 상의")
                        type = 1
                        index=index-2
                        continue@loop1
                    }

                    else ->{
                        println("타입: 추출 불가 - 상의 포함된 단어")
                    }
                }
            }
            else if(has.contains(tyC[1])){ //외투를 포함하는 단어일때
                when(test[index]){
                    "반소매외투" -> {
                        println("타입: 반소매 외투")
                        type = 2
                    }
                    "긴소매외투" -> {
                        println("타입: 긴소매 외투")
                        type = 3
                    }

                }
                when(test[index-1]){ //외투의 앞단어가 **일 때
                    "반소매" -> {
                        println("타입: 반소매 외투")
                        type = 2
                        index=index-2
                        continue@loop1
                    }
                    "긴소매" -> {
                        println("타입: 긴소매 외투")
                        type = 3
                        index=index-2
                        continue@loop1
                    }
                }
            }
            else if(has.contains(tyC[2])){ //조끼를 포함하는 단어일때
                println("타입: 조끼")
                type = 4 //type이 조끼로
            }
            else if(has.contains(tyC[3])){ //민소매를 포함하는 단어일때
                println("타입: 민소매")
                type = 5
            }
            else if(has.contains(tyC[4])){ //바지를 포함하는 단어일때
                if(has == "반바지"){ //반바지, 긴바지 구분
                    println("타입: 반바지")
                    type = 6
                }
                else if(has == "긴바지"){
                    println("타입: 긴바지")
                    type = 7
                }
                else{
                    println("타입: 추출 불가 - 바지 포함된 단어")
                }

            }
            else if(has.contains(tyC[5])){ //치마를 포함하는 단어일때
                println("타입: 치마")
                type = 8 //type이 치마로
            }
            else if(has.contains(tyC[6])){ //원피스를 포함하는 단어일때
                when(test[index]){
                    "반소매원피스" -> {
                        println("타입: 반소매 원피스")
                        type = 9
                    }
                    "긴소매원피스" -> {
                        println("타입: 긴소매 원피스")
                        type = 10
                    }
                    "민소매원피스" -> {
                        println("타입: 민소매 원피스")
                        type = 11
                    }
                }
                when(test[index-1]){ //원피스 앞이 ** 일 때
                    "반소매" -> {
                        println("타입: 반소매 원피스")
                        type = 9
                        index=index-2
                        continue@loop1
                    }
                    "긴소매" -> {
                        println("타입: 긴소매 원피스")
                        type = 10
                        index=index-2
                        continue@loop1
                    }
                    "민소매" -> {
                        println("타입: 긴소매 원피스")
                        type = 11
                        index=index-2
                        continue@loop1
                    }
                    else ->{
                        println("타입: 추출 불가 - 원피스 포함된 단어")
                    }
                }
            }

            else if(has.contains(ptC)){ //~무늬를 포함하고 있다면
                //어떤 무늬인지 구분
                when(test[index]){
                    "동물얼룩무늬" ->{
                        println("무늬: 동물 얼룩 무늬")
                        ptn = 0
                        index=index-2
                        continue@loop1
                    }
                    "얼룩무늬" ->{
                        if(test[index-1]=="동물"){
                            println("무늬: 동물 얼룩 무늬")
                            ptn = 0
                            index=index-2
                            continue@loop1
                        }
                    }
                    "체크무늬" ->{
                        println("무늬: 체크 무늬")
                        ptn = 1
                    }
                    "지그재그무늬" ->{
                        println("무늬: 지그재그 무늬")
                        ptn = 2
                    }
                    "마름모무늬" ->{
                        println("무늬: 마름모 무늬")
                        ptn = 3
                    }
                    "꽃무늬" ->{
                        println("무늬: 꽃무늬")
                        ptn = 4
                    }
                    "민무늬" ->{
                        println("무늬: 민무늬")
                        ptn = 7
                    }
                    "땡땡이무늬" ->{
                        println("무늬: 땡땡이 무늬")
                        ptn = 8
                    }
                    "줄무늬" ->{
                        println("무늬: 줄무늬")
                        ptn = 9
                    }
                    "무늬" ->{ //무늬의 앞 단어가 **일때
                        when(test[index-1]){
                            "동물얼룩" ->{
                                println("무늬: 동물 얼룩 무늬")
                                ptn = 0
                                index=index-2
                                continue@loop1
                            }
                            "얼룩" ->{
                                if(test[index-2]=="동물"){
                                    println("무늬: 동물 얼룩 무늬")
                                    ptn = 0
                                    index=index-3
                                    continue@loop1
                                }
                            }
                            "체크" ->{
                                println("무늬: 체크 무늬")
                                ptn = 1
                                index=index-2
                                continue@loop1
                            }
                            "지그재그" ->{
                                println("무늬: 지그재그 무늬")
                                ptn = 2
                                index=index-2
                                continue@loop1
                            }
                            "재그"->{
                                if(test[index-2]=="지그"){
                                    println("무늬: 지그재그 무늬")
                                    ptn = 2
                                    index=index-3
                                    continue@loop1
                                }
                            }
                            "마름모" ->{
                                println("무늬: 마름모 무늬")
                                ptn = 3
                                index=index-2
                                continue@loop1
                            }

                            "땡땡이" ->{
                                println("무늬: 땡땡이 무늬")
                                ptn = 8
                                index=index-2
                                continue@loop1
                            }
                            else ->{
                                println("무늬: 추출 불가 - 무늬 포함된 단어")
                            }
                        }
                    }
                }

            }
            else if(has.contains("있는")){
                if(test[index]=="그림이그려져있는"){
                    println("무늬: 그림이 그려져 있는")
                    ptn = 5
                }
                else{
                    when(test[index-1]){ //있는의 앞단어가 **일때
                        "그림이그려져" ->{
                            println("무늬: 그림이 그려져 있는")
                            ptn = 5
                            index=index-2
                            continue@loop1
                        }
                        "그려져" ->{
                            if(test[index-2]=="그림이"){
                                println("무늬: 그림이 그려져 있는")
                                ptn = 5
                                index=index-3
                                continue@loop1
                            }
                        }
                        "글씨가가쓰여" ->{
                            println("무늬: 글씨가 쓰여 있는")
                            ptn = 6
                            index=index-2
                            continue@loop1
                        }
                        "쓰여" ->{
                            if(test[index-2]=="글씨가"){
                                println("무늬: 글씨가 쓰여 있는")
                                ptn = 6
                                index=index-3
                                continue@loop1
                            }
                        }
                    }
                }

            }
            else if(has.contains(coC)){ //~색을 포함하고 있다면
                println("색: $has")
                colors.add(has)
                //color 배열 안으로
            }
            else {
                //features 배열 안으로
                println("기타 특징: $has")
                features.add(has)
            }
            index--
        }

        return QfindCl(type, ptn, colors, features)
    }


    fun QsaveDB(ca:ArrayList<String>):JSONObject{//DB 저장 요청
        var json = JSONObject()
        json.put("device", 1)
        json.put("requestType", 2)
        json.put("user", "userID")

        var jsonB = JSONObject()
        var jsonInfo = JSONArray()    //서버로 부터 받아온 세탁 정보 여기에

        //jsonInfo에 서버로 부터 받아온 세탁 정보 넣는 방법 생각해보기
            //1 위처럼 ArrayList를 만들어서 넣기넣기?
        // 서버가 세탁 정보를 어떻게 주는가 = json으로

        jsonB.put("careInfos", ca)
        json.put("body", jsonB)

        println("Make: "+json.toString())
        return json
    }


    fun QcloseS(): JSONObject { //연결 종료 요청
        var json = JSONObject()
        json.put("device", 1)
        json.put("requestType", 3)
        return json
    }

    fun run(a:Int){
        println(a)
        try{

        }catch(e:Exception){
        }
    }

}