package com.example.CareHeim

import org.json.JSONArray
import org.json.JSONObject
import java.lang.Byte.toString
import java.util.Arrays.toString
import java.util.Objects.toString

class OpenJson {

    fun open(stringData: String) :String{
        println("OpenJson: $stringData")
        if(stringData=="false"){
            //get에서 문제 발생
            println("get 문제 - get error의 리턴")
            return "p"
        }
        else if(stringData==""){
            println("get 문제 - 공란")
            return "p"
        }
        else{
            try{

                val user = JSONObject(stringData).getString("user") //추후에 고려해야 함
                val responseType = JSONObject(stringData).getInt("responseType")

                println("responseType: $responseType")
                when (responseType){
                    0 -> {
                        println("의류 정보가 담겨져 있음")
                        return RQ1(stringData) //RQ1의 값에 따라 activity에서 나올 음성이 달라야 함 //0: 의류없음, 1: 중복 존재, 2: 이미 완료, ""의류정보"", -1: 오류
                    }
                    1 -> {
                        println("DB 저장 성공 여부가 담겨져 있음")
                        return RQ2(stringData) //"0"=저장 성공 "1"=저장 실패
                    }
                    else -> {
                        println("오류 발생, 재전송 요망 신호")
                        return "p"
                    }
                }
            }catch (e: Exception){
                println(e)
                println("get error - OpenJson_Open")
                return "p"
            }

        }
    }

    fun RQ1(stringData : String) :String{
        val body = JSONObject(stringData).getJSONObject("body")
        val status = body.getInt("status")
        println("status: $status")

        when (status){
            0-> {
                println("해당 의류가 없음")
                return status.toString() //"해당 의류가 서버에 존재하지 않습니다."
                //없음을 안내하도록 return
            }
            1-> {
                println("중복 의류가 존재함")
                return status.toString() //"정보가 중복되는 의류가 존재합니다."
                ////중복의류가 있음을 안내하도록 return ++추가 정보 요청 화면으로 넘어가야함
            }
            2-> {
                println("이미 세탁 정보가 등록된 의류")
                return status.toString() //"이미 세탁정보가 등록 되어있는 의류입니다."
                //세탁 정보가 이미 등록된 의류임을 안내하도록 return ++다른 의류를 선택하도록 의류 검색 41 재시작 (43의 경우 41에서 말한 정보가 같이 등록됨
            }
            3-> {
                println("의류 정보가 담겨있음 열어야 함")

                try{
                    val clothe = body.getJSONObject("clothe")
                    val type = clothe.getInt("type")
                    val ptn = clothe.getInt("ptn")
                    if(clothe.has("colors")){

                    }
                    val colors = clothe.getJSONArray("colors")


                    val colorss : List<String> = (0 until colors.length()).map {
                        colors.getString(it).toString()
                    }

                    val features = clothe.getJSONArray("features")
                    val featuress : List<String> = (0 until features.length()).map {
                        features.getString(it).toString()
                    }
                    val tell = clothes().clothes(type, ptn, colorss, featuress) //최종적으로 사용자에 전달되어야할 정보
                    return tell
                }catch(e:Exception){
                    println(e)
                    println("담긴 의류 정보를 오픈하는데 문제가 생김 혹은 전달된 JSON에 문제 발생")
                    return "p"
                }


            }
            else->{
                println("엥 여기에도 else가 있음 오류인데용")
                return "-1"
            }

        }
    }

    fun RQ2(stringData : String) :String{
        val body = JSONObject(stringData).getJSONObject("body")
        val success = body.getBoolean("success")
        when(success){
            true ->{
                println("DB 저장 성공")
                return "0"
            }
            false ->{
                val v = success.toString()
                return "1"
            }
            else ->{return "ee"}
        }

    }



    class clothes(){
        fun clothes(a:Int, b: Int, c:List<String>, d:List<String>) : String{
            val type = type(a)
            val ptn = ptn(b)
            val colors= c
            val features= d
            println("의류 정보: $features " + " $colors"+" $ptn $type" ) //배열 끼리 붙어있으면 그 사이가 붙어서 출력 되길래 강제로 띄어 봄
            if (features[0]==" "){
                return("$colors $ptn $type") //$type, $ptn, $colors, $features
            }
            else{
                return("$features 특징을 가진  $colors $ptn $type") //$type, $ptn, $colors, $features
            }


        }

        private fun type(tp:Int):String{
            when(tp){
                0 -> {
                    println("반소매 상의")
                    return "반소매 상의"
                }
                1 -> {
                    println("긴소매 상의")
                    return "긴소매 상의"
                }
                2 -> {
                    println("반소매 외투")
                    return "반소매 외투"
                }
                3 -> {
                    println("긴소매 외투")
                    return "긴소매 외투"
                }
                4 -> {
                    println("조끼")
                    return "조끼"
                }
                5 -> {
                    println("민소매")
                    return "민소매"
                }
                6 -> {
                    println("반바지")
                    return "반바지"
                }
                7 -> {
                    println("긴바지")
                    return "긴바지"
                }
                8 -> {
                    println("치마")
                    return "치마"
                }
                9 -> {
                    println("반소매 원피스")
                    return "반소매 원피스"
                }
                10 -> {
                    println("긴소매 원피스")
                    return "긴소매 원피스"
                }
                11 -> {
                    println("민소매 원피스")
                    return "민소매 원피스"
                }
                else -> {
                    println("의류 타입 없음") //실질적으로 오류임
                    return "" //비워보내기
                }
            }

        }
        fun ptn(pt:Int):String{
            when(pt){
                0 ->{
                    println("동물 얼룩 무늬")
                    return "동물 얼룩 무늬"
                }
                1 ->{
                    println("체크 무늬")
                    return "체크 무늬"
                }
                2 ->{
                    println("지그재그 무늬")
                    return "지그재그 무늬"
                }
                3 ->{
                    println("마름모 무늬")
                    return "마름모 무늬"
                }
                4 ->{
                    println("꽃무늬")
                    return "꽃무늬"
                }
                5 ->{
                    println("그림이 그려져 있는")
                    return "그림이 그려져 있는"
                }
                6 ->{
                    println("글씨가 쓰여 있는")
                    return "글씨가 쓰여 있는"
                }
                7 ->{
                    println("민무늬")
                    return "민무늬"
                }
                8 ->{
                    println("땡땡이 무늬")
                    return "땡땡이 무늬"
                }
                9 ->{
                    println("줄무늬")
                    return "줄무늬"
                }
                else ->{
                    println("패턴 정보가 없음") //실질적 오류
                    return "" //비워보내기
                }
            }
        }
    }
}