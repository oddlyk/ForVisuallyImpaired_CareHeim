package com.example.careheim_v2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*
import androidx.core.app.ActivityCompat as ActivityCompat
import android.app.Activity
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.careheim_v2.Http.Companion.ForReturn
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class Main2Activity : AppCompatActivity() {
    lateinit var TTS: TextToSpeech //TTS 기능 추가
    lateinit var speechRecognizer: SpeechRecognizer //STT 기능 추가
    lateinit var recognitionListener: RecognitionListener //STT 기능 추가
    val FLAG_REQ_CAMERA = 101 //카메라 호출 용 val

    val addORout = "의류에 세탁 정보를 등록하시려면 상단을, 어플을 종료하시려면 하단을 빠르게 두번 눌러주세요. " //screenNo = 999
    val addORout_1 = "세탁 정보 등록"
    val addORout_2 = "어플 종료"

    val chooHOWadd = "방금 기기에서 등록된 의류에 세탁 정보를 추가하시려면 상단을, 의류를 검색하여 추가하시려면 하단을 빠르게 두번 눌러주세요. "  //screenNo = 900
    val chooHOWadd_1 = "방금 기기에서 등록된 의류에 세탁 정보 추가"
    val chooHOWadd_2 = "의류를 검색하여 추가"

    val savedCl = "방금 등록된 의류는 (옷 특징) 입니다. 해당 의류에 등록하시려면 상단을 눌러주세요. 첫화면으로 돌아가시려면 하단을 눌러주세요. " //screenNo = 101
    val savedCl_1 = "세탁 정보를 등록하려는 의류가 맞다"
    val savedCl_2 = "의류 검색 화면으로"

    val searchCl = "하단의 버튼을 빠르게 두번 누른 후, 안내에 따라 의류의 특징을 단어로 말해주세요. 특징을 모두 말하셨으면 상단을, 다시 말씀하시려면 하단을 빠르게 두번 눌러주세요." //screenNo = 110
    val searchCl_1 = "다음 단계로 넘어가기"
    val searchCl_2 = "말하기"

    val resultCl = "검색된 의류는 (옷 특징) 입니다. 해당 의류에 등록하시려면 상단을 의류를 다시 검색 하시려면 하단을 눌러주세요." //screenNo = 111
    val resultCl_1 = "세탁 정보를 등록하려는 의류가 맞다"
    val resultCl_2 = "의류를 다시 검색한다"

    val filmingCl = "세탁 정보 촬영을 진행하겠습니다. 대부분 의류의 세탁 정보는 의류의 안쪽 태그에 위치해 있습니다. 태그는 여러장 존재할 수 있으며, 상황에 따라 태그의 여러면을 촬영해야하거나 접힌 면을 촬영하여야 할 수 있습니다. 어둡지 않고 평평한 곳에 놓고, 카메라를 해당 위치에서 한 뼘 정도 띄운 상태로 촬영해주세요. 기본 카메라를 사용하여 촬영합니다."
    val filmingCl_3 = "촬영하러 가기"  //screenNo = 901

    val REfilmingCl = "케어 라벨 촬영을  다시 진행하겠습니다. 화면의 상단을 길게 누르시면 촬영 방법에 대한 안내를 들으실 수 있습니다. 촬영하러 가시려면 상단의 버튼을, 케어 라벨에 대한 촬영을 그만하시려면 하단의 버튼을 눌러주세요."
    val REfilmingCl_1 = "촬영 다시 하기" //screenNo = 902
    val REfilmingCl_2 = "세탁정보 등록 그만하기"

    val cheakLabel = "세탁 정보가 추출 되었습니다. 촬영된 정보를 듣고자 하시면 상단을, 넘어가고자 하시면 하단을 빠르게 두번 눌러주세요." //screenNo = 904
    val cheakLabel_1 = "세탁 정보 듣기"
    var cheakLabel_1_say = "해당 의류의 세탁정보는 (세탁 정보) 입니다." //cheakCare()에서 수정될 예정
    val cheakLabel_2 = "다음으로 넘어가기"


    val endAddCl = "세탁 정보 등록 단계가 완료되었습니다. 새로운 세탁 정보 등록 단계를 시작하려면 상단을, 첫화면으로 돌아가시려면 하단을 빠르게 두번 눌러주세요" //screenNo = 905
    val endAddCl_1 = "의류 선택으로 돌아가기"
    val endAddCl_2 = "첫 화면으로 돌아가기"


    var screenNo = 999
    var maino = addORout
    var btn1 = addORout_1
    var btn2 = addORout_2

    var mLastClickTime:Long = 0 //클릭 타이머

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        //TTS 말하기 속도 및 언어 설정
        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                TTS.language = Locale.KOREAN
            }
        })


        speek(maino) //화면 시작 시 안내
        println(maino)

        /**버튼1*/
        main2_button1.setOnClickListener {
            if (TTS.isSpeaking){ //기존의 speek을 종료
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){ //더블 클릭 시
                main2_button1.isEnabled = false //대기중 버튼 재터치 방지용 비활성화
                main2_button2.isEnabled = false //대기중 버튼 재터치 방지용 비활성화
                Handler(Looper.getMainLooper()).postDelayed({ //원터치시 버튼 이름 말하기가 종료된 후 실행되도록
                    while (true) { //버튼 말하기가 종료될 때까지 루프
                        if (!TTS.isSpeaking) { //버튼 말하기가 종료되었을 때
                            val OK = isNetworkAvailable(this) //네트워크 상태 T/F
                            if(OK){ //네트워크 연결중일 때 (T일때)
                                when (screenNo) {
                                    999 -> { //maino == addORout
                                        addORout_1() //등록할 의류 선택 화면으로 재구성하는 fun
                                    }
                                    900 -> { //maino == chooHOWadd
                                        chooHOWadd_1() //방금 기기에서 등록된 의류에 등록으로 재구성하는 fun
                                    }
                                    110 -> { //maino == searchAdd
                                        searchAdd_1() //옷 검색 결과로 재구성하는 fun
                                    }
                                    101 -> {
                                        main2_button3.isEnabled = true
                                        InfoFilming()//촬영 안내 화면으로 재구성하는 fun
                                    }
                                    111 -> {
                                        main2_button3.isEnabled = true
                                        InfoFilming()//촬영 안내 화면으로 재구성하는 fun
                                    }
                                    903 -> {
                                        val OK = isNetworkAvailable(this)
                                        if(OK){
                                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                            startActivityForResult(intent, FLAG_REQ_CAMERA)
                                        }
                                        else{
                                            val intent = Intent(applicationContext, NetworkF::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                    905 -> {
                                        speek(cheakLabel_1_say)//추출된 세탁정보 안내
                                        println(cheakLabel_1_say)
                                        main2_button1.isEnabled = true //버튼 활성화
                                        main2_button2.isEnabled = true //버튼 활성화
                                    }
                                    1000 ->{
                                        addORout_1() //의류 선택 화면으로
                                    }
                                }
                            }
                            else{ //네트워크 비연결 상태
                                val intent = Intent(applicationContext, NetworkF::class.java) //네트워크 연결 오류 화면으로
                                startActivity(intent)
                            }
                            break
                        }
                    }
                }, 400)
            }
            else{
                speek(btn1)
                println(btn1)
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        main2_button1.setOnLongClickListener {
            if(screenNo==903){
                val howTake = " 대부분 의류의 세탁 정보는 의류의 안쪽 태그에 위치해 있습니다. 태그는 여러장 존재할 수 있으며, 상황에 따라 태그의 여러면을 촬영해야하거나 접힌 면을 촬영하여야 할 수 있습니다. 어둡지 않고 평평한 곳에 놓고, 카메라를 해당 위치에서 한 뼘 정도 띄운 상태로 촬영해주세요. 기본 카메라를 사용하여 촬영합니다."
                speek(howTake)
                println(howTake)
            }
            else{
                speek(maino)
                println(maino)
            }
            true
        }


        /**버튼 2*/
        main2_button2.setOnClickListener {
            //미구현기능 임시로서 종료 기능을 넣어둠=>talk back 기능 사용시 하단 버튼 사용이 불가하였음

            if (TTS.isSpeaking){
                TTS.stop()
            }

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main2_button1.isEnabled = false //대기중 버튼 재터치 방지용 비활성화
                main2_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) { //버튼 말하기가 종료되었을 때
                            val OK = isNetworkAvailable(this) //네트워크 상태 T/F
                            if(OK){ //네트워크 연결중일 때 (T일때)
                                when (screenNo) {
                                    999 -> {
                                        addORout_2() //어플 종료

                                    }
                                    900 -> {
                                        chooHOWadd_2() //검색한 의류에 등록으로 재구성하는 fun
                                    }
                                    101 -> {
                                        chooHOWadd_2() //검색한 의류에 등록으로 재구성하는 fun
                                    }
                                    110 -> {
                                        searchAdd_2() //마이크 활성화 fun
                                    }
                                    111 -> {
                                        chooHOWadd_2() //검색한 의류에 등록으로 재구성하는 fun
                                    }
                                    905 -> {
                                        saveCare()//종료 페이지로 안내
                                    }
                                    1000 ->{
                                        screenNo = 999
                                        maino = addORout
                                        btn1 = addORout_1
                                        btn2 = addORout_2
                                        main2_button1.text=btn1
                                        main2_button2.text = btn2
                                        main2_button1.isEnabled = true //버튼 활성화
                                        main2_button2.isEnabled = true //버튼 활성화
                                        speek(maino)//첫 화면 안내
                                        println(maino)

                                    }
                                }
                            }
                            else{ //네트워크 비연결 상태
                                val intent = Intent(applicationContext, NetworkF::class.java) //네트워크 연결 오류 화면으로
                                startActivity(intent)
                            }
                            break
                        }
                    }
                }, 400)
            }
            else{
                speek(btn2)
                println(btn2)
            }
            mLastClickTime = SystemClock.elapsedRealtime()

        }
        main2_button2.setOnLongClickListener {
            speek(maino)
            println(maino)
            true
        }

        /**버튼 3 - 촬영하러 가기*/
        main2_button3.setOnClickListener {
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main2_button3.isEnabled = false //안내 혹은 연결 중 버튼 클릭 방지
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) { //버튼 이름 말하기의(촬영하러 가기) TTS가 끝나기를 기다리는 문
                        if (!TTS.isSpeaking) {
                            val OK = isNetworkAvailable(this)
                            if(OK){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, FLAG_REQ_CAMERA)
                            }
                            else{
                                val intent = Intent(applicationContext, NetworkF::class.java)
                                startActivity(intent)
                            }
                            break
                        }
                    }
                }, 800) }
            else{
                speek("촬영하러 가기")
                println("촬영하러 가기")
            }
            mLastClickTime = SystemClock.elapsedRealtime()

        }
        main2_button3.setOnLongClickListener {
            speek(maino)
            println(maino)
            true
        }


    }


    //위에 다른 화면이 생길때
    override fun onPause(){
        super.onPause()
        TTS.stop() //TTS 기능 중지
        main2_button1.isEnabled = true
        main2_button2.isEnabled = true
        main2_button3.isEnabled = true
        if (::speechRecognizer.isInitialized) { //마이크 활성화 상태 중지
            speechRecognizer.stopListening()
        }
    }

    //화면이 안보이게 되었을 때 동작 (어플을 나갔다 들어올 때
    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main2_button1.isEnabled = true
        main2_button2.isEnabled = true
        main2_button3.isEnabled = true
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
        }
    }


    /**등록할 의류 선택 화면 OR 어플 종료 screenNo = 999*/
    //등록할 의류 선택 화면으로 재구성
    fun addORout_1(){
        screenNo = 900
        maino = chooHOWadd
        btn1 = chooHOWadd_1
        btn2 = chooHOWadd_2
        speek(maino)
        println(maino)
        main2_button1.text=btn1
        main2_button2.text = btn2
        main2_button1.isEnabled = true //버튼 활성화
        main2_button2.isEnabled = true //버튼 활성화
    }
    //어플 종료
    fun addORout_2(){
        speek("이용해주셔서 감사합니다.")
        println("이용해주셔서 감사합니다.")
        Handler(Looper.getMainLooper()).postDelayed({
            while (true) {//위의 말하기가 끝난 후 진행
                if (!TTS.isSpeaking) {
                    ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                    System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                    System.exit(0) // 현재 액티비티를 종료시킨다.
                    break
                }
            }
        }, 400)
    }

    /**방금 기기에서 등록된 의류에 OR 검색한 의류에 screenNo = 900*/
    //방금 기기에서 등록된 의류에 등록으로 재구성
    fun chooHOWadd_1(){
        screenNo = 101
        speek("저장된 의류를 검색합니다.")
        println("저장된 의류를 검색합니다.")
        var clothData = ""
        val OK = isNetworkAvailable(this)
         if(OK){
             toHttp()//HTTP 요청
            Handler(Looper.getMainLooper()).postDelayed({ //HTTP 요청이 끝나고 진행하도록 강제 딜레이 1000초
                println("On chooHOWadd_1 - Http().ForReturn :"+ForReturn)
                clothData=ForReturn //clothData=re()
                if(clothData.contains("error")){
                    println("에러발생")
                    //의류정보 받아오기 에러 발생 시 처리
                    var ErrInfo = "옷 정보를 불러오는데 오류가 발생했습니다. 잠시 후 어플을 다시 실행해주세요. 어플을 종료합니다."
                    speek(ErrInfo)
                    println(ErrInfo)
                    Handler(Looper.getMainLooper()).postDelayed({
                        while (true) {
                            if (!TTS.isSpeaking) {
                                ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                                System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                                System.exit(0) // 현재 액티비티를 종료시킨다
                                break
                            }

                        }
                    }, 800)
                }
                else{
                    maino = "방금 등록된 의류는 "+ clothData+"입니다. 해당 의류에 등록하시려면 상단을, 검색하여 다른 의류를 등록하시려면 하단을 빠르게 두번 눌러주세요"
                }
            }, 1000)

        }
        else{
            val intent = Intent(applicationContext, NetworkF::class.java)
            startActivity(intent)
        }
        if(!clothData.contains("error")){ //error라서 강종되었다면 필요없어지는 코드이기에 if
            // maino = savedCl //"방금 등록된 의류는 (옷 특징) 입니다. 해당 의류에 등록하시려면 상단을 눌러주세요. 첫화면으로 돌아가시려면 하단을 눌러주세요. "
            Handler(Looper.getMainLooper()).postDelayed({//HTTP 요청및 처리가 끝나고 진행하도록 강제 딜레이 1000초+1000초
                btn1 = savedCl_1
                btn2 = savedCl_2
                speek(maino)
                println(maino)
                main2_button1.text = btn1
                main2_button2.text = btn2
                main2_button1.isEnabled = true //버튼 활성화
                main2_button2.isEnabled = true //버튼 활성화
            }, 2000)
        }
    }
    //검색하여 등록으로 재구성
    fun chooHOWadd_2(){
        screenNo = 111
        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        sayListener()

        maino = searchCl
        btn1 = searchCl_1
        btn2 = searchCl_2
        speek(maino)
        println(maino)
        main2_button1.text = btn1
        main2_button2.text = btn2
        main2_button1.isEnabled = false //한번이라도 마이크가 켜지기 전에는 상단 버튼의 활성화 불가
        main2_button2.isEnabled = true //버튼 활성화
    }

    /**검색하여 등록 중 다음 단계 OR 마이크 활성화 screenNo = 111*/
    //옷 검색 결과로 재구성
    fun searchAdd_1(){
        if(recodeCheak()){ //2개 이상의 특징이 녹음되었을 때에만 진행
            screenNo = 111
            speek("저장된 의류를 검색합니다.")
            println("저장된 의류를 검색합니다.")
            var clothData = ""

            val OK = isNetworkAvailable(this)
            if(OK){
                toHttp()//HTTP 요청
                Handler(Looper.getMainLooper()).postDelayed({ //HTTP 요청이 끝나고 진행하도록 강제 딜레이 1000초
                    println("On searchAdd_1 - Http().ForReturn :$ForReturn")
                    clothData=ForReturn //clothData=re()
                    if(clothData.contains("error")){
                        println("에러발생")
                        //의류정보 받아오기 에러 발생 시 처리
                        var ErrInfo = "옷 정보를 불러오는데 오류가 발생했습니다. 잠시 후 어플을 다시 실행해주세요. 어플을 종료합니다."
                        speek(ErrInfo)
                        println(ErrInfo)
                        Handler(Looper.getMainLooper()).postDelayed({
                            while (true) {
                                if (!TTS.isSpeaking) {
                                    ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                                    System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                                    System.exit(0) // 현재 액티비티를 종료시킨다
                                    break
                                }

                            }
                        }, 800)
                    }
                    else{
                        maino = "검색된 의류는 "+ clothData+"입니다. 해당 의류에 등록하시려면 상단을, 의류를 다시 검색하시려면 하단을 빠르게 두번 눌러주세요."
                    }
                }, 1000)
                if(!clothData.contains("error")){ //error라서 강종되었다면 필요없어지는 코드이기에 if
                    Handler(Looper.getMainLooper()).postDelayed({//HTTP 요청및 처리가 끝나고 진행하도록 강제 딜레이 1000초+1000초
                        btn1 = resultCl_1
                        btn2 = resultCl_2
                        speek(maino)
                        println(maino)
                        main2_button1.text = btn1
                        main2_button2.text = btn2
                        main2_button1.isEnabled = true //버튼 활성화
                        main2_button2.isEnabled = true //버튼 활성화
                    }, 2000)
                }
            }
            else{
                val intent = Intent(applicationContext, NetworkF::class.java)
                startActivity(intent)
            }
        }
    }
    //마이크 활성화로 재구성
    fun searchAdd_2(){
        main2_button1.isEnabled = true
        main2_button2.isEnabled = true
        val re = "3초 후 마이크가 활성화 됩니다. 1, 2, 3"
        speek(re)
        println(re)

        Handler(Looper.getMainLooper()).postDelayed({
            while(true){
                if(!TTS.isSpeaking){
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                    speechRecognizer.setRecognitionListener(recognitionListener)
                    speechRecognizer.startListening(intent)

                    break
                }
            }
        }, 300)
    }
    private fun sayListener() {
        recognitionListener = object: RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                // 말하기 시작할 준비가되면 호출
                println("음성인식을 시작합니다.")
            }

            override fun onBeginningOfSpeech() {
                // 말하기 시작했을 때 호출
            }

            override fun onRmsChanged(rmsdB: Float) {
                // 입력받는 소리의 크기를 알려줌
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // 말을 시작하고 인식이 된 단어를 buffer에 담음
            }

            override fun onEndOfSpeech() {
                // 말하기를 중지하면 호출
            }

            override fun onError(error: Int) {
                // 네트워크 또는 인식 오류가 발생했을 때 호출
                var message: String

                when (error) {
                    SpeechRecognizer.ERROR_AUDIO ->
                        message = "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT ->
                        message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        message = "퍼미션 없음"
                    SpeechRecognizer.ERROR_NETWORK ->
                        message = "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        message = "네트워크 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH ->
                        message = "찾을 수 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                        message = "RECOGNIZER가 바쁨"
                    SpeechRecognizer.ERROR_SERVER ->
                        message = "서버가 이상함"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        message = "말하는 시간초과"
                    else ->
                        message = "알 수 없는 오류"
                }
                println("에러 발생 $message")
            }

            override fun onResults(results: Bundle?) {
                // 인식 결과가 준비되면 호출
                //Toast.makeText(this@Main41Activity, "음성인식 종료", Toast.LENGTH_SHORT).show()
                println("음성인식 종료")
                var matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                println("마이크 인식 결과: $matches") // brave, new, world

                var fortest = ""
                for (i in 0 until matches.size) {
                    fortest = matches[i]
                    println(fortest)
                }
                main2_textView.setText(fortest) //녹음된 text를 textview에 담아 외부로 전달
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // 부분 인식 결과를 사용할 수 있을 때 호출
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // 향후 이벤트를 추가하기 위해 예약
            }

        }
    }
    private fun recodeCheak():Boolean{ //2개 이상의 특징이 녹음되었는지 체크
        //textView.text = "흰색 긴소매 상의" (임시용 - 애뮬레이터 테스트용)
        val fortest = main2_textView.text.toString() //녹음된 텍스트를 가져옴

        try{
            val aarr:ArrayList<String> = fortest.split(" ") as ArrayList<String> //fortest 속 문장을 띄어 쓰기를 기준으로 잘라내어 리스트에 넣음

            print("fortest 자르기 확인(리스트): ")
            for (element in aarr) { //i in 0 until arr.size
                print("[$element]") //element +"/"
            }
            println()
            return true
        }
        catch(e:ClassCastException){
            var sat = "특징을 두가지 이상 말씀해 주시고 버튼을 눌러주세요."
            main2_button1.isEnabled = false
            main2_button2.isEnabled = true
            speek(sat)
            println(sat)
            return false
        }
    }

    /**촬영 안내 screenNo = 901*/
    fun InfoFilming(){
        screenNo = 902
        main2_button3.text = filmingCl_3
        main2_button1.visibility = View.GONE
        main2_button2.visibility = View.GONE
        main2_button3.visibility = View.VISIBLE

        maino = filmingCl //촬영 안내
        speek(maino)
        println(maino)

    }
    //촬영하고 온 이미지 처리 및 서버 전송 수정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //촬영하고 온
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when (requestCode){
                FLAG_REQ_CAMERA -> {
                    if (data?.extras?.get("data") != null){
                        val bitmap = data?.extras?.get("data") as Bitmap
                        val u = saveImageFile(newFileName(), "image/jpg", bitmap)
                        println("uri.u: $u")
                        val uri = getPath(u)


                        ///////////////////////
                        val Outstream = ByteArrayOutputStream() //비트맵을 bytes로 변환
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, Outstream)
                        val gogo = Outstream.toByteArray()
                        // println("{\"image\":$gogo}")
                        val OK = isNetworkAvailable(this)
                        if(OK){
                            screenNo=903 //1회 촬영 완료
                            main2_button3.text = "촬영 정보 분석중"
                            main2_button1.visibility = View.GONE
                            main2_button2.visibility = View.GONE
                            main2_button3.visibility = View.VISIBLE
                            main2_button3.isEnabled = false
                            speek("촬영 정보를 분석중입니다.")
                            println("촬영 정보를 분석중입니다.")
                            Handler(Looper.getMainLooper()).postDelayed({
                                while (true) {
                                    if (!TTS.isSpeaking) {
                                        //labelOp("{\"labels\":[2, 6, 7, 11, 16, 15]}")
                                        //http 연결
                                       // Okhttp().run(gogo)
                                        val label ="{\n" +
                                                "\t\"labels\" : [0, 1, 2]\n" +
                                                "}"  //{"labels" : [0]}

                                        if(label=="오류"){
                                            println("http에서 오류 발생 1")
                                           // Okhttp().run(gogo)
                                           // val label = Okhttp().re()
                                            if(label=="오류"){
                                                println("http에서 오류 발생 2")
                                                speek("촬영 정보 분석 중에 오류가 발생했습니다. 잠시 후 어플을 다시 실행해 주세요. 어플을 종료합니다.")
                                                println("촬영 정보 분석 중에 오류가 발생했습니다. 잠시 후 어플을 다시 실행해 주세요. 어플을 종료합니다.")
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    while (true) {
                                                        if (!TTS.isSpeaking) {
                                                            ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                                                            System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                                                            System.exit(0) // 현재 액티비티를 종료시킨다
                                                            break
                                                        }

                                                    }
                                                }, 400)
                                            }
                                            else{
                                               // labelOp(label)
                                            }
                                        }
                                        else{
                                            labelOp(label)
                                        }
                                        break
                                    }
                                }
                            }, 1000)

                            Outstream.close()
                        }
                        else{
                            val intent = Intent(applicationContext, NetworkF::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }


    }
    private fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap) : Uri? { //이미지 저장용
        val values = ContentValues()
        // values.put(MediaStore.Audio.Media.RELATIVE_PATH, "DCIM/Care-Heim")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try{
            if (uri != null){
                var descriptor = contentResolver.openFileDescriptor(uri, "w")
                if (descriptor != null){
                    val fos = FileOutputStream(descriptor.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos) //비트맵 저장
                    fos.close()

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, values, null, null)
                    }
                }
            }
        } catch (e:java.lang.Exception){
            println("File error=${e.localizedMessage}")
        }
        return uri
    }
    private fun newFileName() : String { //이미지 저장시 이름 편집용
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())

        return "$filename.jpg"
    }
    // 서버 전송 이후 받아온 json 해제
    fun labelOp(la:String){
        val op = JSONObject(la).getJSONArray("labels")
        val cc : List<Int> = (0 until op.length()).map { //받아온 라벨의 값을 리스트에 담기
            op.getInt(it)
        }
        println("받아온 라벨 값 전체: $cc")

        var careinfo = ArrayList<String>()

        if(cc.size>1){ // 받아온 라벨 값이 1개 이상일 때
            val cb = cc.distinct() //라벨 값 속 중복 값 제거
            println("라벨값 중 중복 값 제거: $cb")

            val bab = cb.sorted() //라벨 값 정렬 (오름차순)
            println("라벨값 오름차순 정렬: $bab")

            if(bab.size<3){ //세탁 기호가 3개 미만임
                //추출된 세탁기호가 2개 이하로 인식되었을 경우 해당 면 재촬영 요청 (추가사항: 대기 필요-1. 앞에서의 안내가 끝날때까지, 세탁 기호 추출이 끝날때까지
                val moreT = "세탁 기호가 바르게 인식 되지 않았습니다. 빛이나 접힌 부분에 유의하여 해당 면을 다시 촬영하여 주세요"
                speek(moreT)
                println(moreT)
                Handler(Looper.getMainLooper()).postDelayed({
                    while(true){
                        if(!TTS.isSpeaking){
                            REfilming()//촬영 다시 하기
                            break
                        }
                    }
                }, 400)
            }
            else{
                for(i in bab.size-1 downTo 0){ //가장 큰 굿자부터 역순으로 체크 (2의 손세탁/기계세탁을 뒤의 것들로 체크해야하기에
                    when(bab[i]){
                        17->{
                            println("국제 자연 건조")
                            careinfo.add("자연 건조 가능")
                        }
                        16->{
                            println("한국 자연 건조")
                            careinfo.add("자연 건조 가능")
                        }
                        15->{
                            println("기계 건조 불가능")
                            careinfo.add("기계 건조 불가능")
                        }
                        14->{
                            println("기계 건조 가능")
                            careinfo.add("기계 건조 가능")
                        }
                        13->{
                            println("탈수 불가능")
                            careinfo.add("탈수 불가능")
                        }
                        12->{
                            println("약하게 탈수")
                            careinfo.add("약하게만 탈수 가능")
                        }
                        11->{
                            println("드라이 불가능")
                            careinfo.add("드라이 불가능")
                        }
                        10->{
                            println("국제 드라이 가능")
                            careinfo.add("드라이 가능")
                        }
                        9->{
                            println("한국 드라이 가능")
                            careinfo.add("드라이 가능")
                        }
                        8->{
                            println("다림질 불가능")
                            careinfo.add("다림질 불가능")
                        }
                        7->{
                            println("다림질 가능")
                            careinfo.add("다림질 가능")
                        }
                        6->{
                            println("표백 불가능")
                            careinfo.add("표백 불가능")
                        }
                        5->{
                            println("표백 가능")
                            careinfo.add("표백 가능")
                        }
                        4->{
                            println("물 세탁 불가능")
                            careinfo.add("물 세탁 불가능")
                        }
                        3->{
                            println("국제 손세탁")
                            careinfo.add("손세탁 가능")
                        }
                        2->{
                            println("국제 기계세탁, 국내 손세탁")
                            if(bab.contains(10)){ //드라이 클리닝 기호로 구분 //국제 드라이 가능
                                println("국제 기계 세탁")
                                careinfo.add("기계 세탁 가능")
                            }
                            else if(bab.contains(9)){ //국내 드라이 가능
                                println("국내 손세탁")
                                careinfo.add("손세탁 가능")
                            }
                            else{ //자연건조 기호로 구분
                                if(bab.contains(17)){ //국제 자연건조 가능
                                    println("국제 기계 세탁")
                                    careinfo.add("기계 세탁 가능")
                                }
                                else if(bab.contains(16)){ //국내 자연건조 가능
                                    println("국내 손세탁")
                                    careinfo.add("손세탁 가능")
                                }
                                else{ //기계 건조 가능 기호로 구분
                                    if(bab.contains(14)){ //기계 건조 가능
                                        println("국제 기계 세탁")
                                        careinfo.add("기계 세탁 가능")
                                    }
                                    else{ //끝까지 구분 불가 일 때
                                        println("국내 손세탁")
                                        careinfo.add("손세탁 가능")
                                    }

                                }
                            }
                        }
                        1->{
                            println("한국 기계세탁")
                            careinfo.add("기계 세탁 가능")
                        }

                    }
                }
                //촬영이 제대로 되어 추출이 바르게 된 경우, 세탁 정보 출력으로
                Handler(Looper.getMainLooper()).postDelayed({
                    while(true){
                        if(!TTS.isSpeaking){
                            var caca = ""
                            val casi = careinfo.size-1
                            caca = careinfo[casi]  //가장 마지막 추출된 세탁 정보를 담기
                            for(i in careinfo.size-2 downTo 0){//세탁 정보 list 값을 한 문장으로 만듦 (역순으로 - 손세탁/기계세탁 여부부터 안내하도록
                                caca= caca+", " //끊어서 안내하도록
                                caca = caca+careinfo[i]
                            }
                            screenNo = 904
                            cheakCare(caca)
                            break
                        }
                    }
                }, 1800)

            }

        }
        else {  //촬영이 잘못 된 경우, 안내 후, 촬영 안내 단계로 - 넘어온 인자가 한개도 없음
            println("추출된 기호 없음")
            val woringP = "해당 사진에 인식 가능한 세탁 기호가 없습니다. 사진이 흔들렸거나, 해당 면에 기호가 존재하지 않을 수 있습니다. 다시 촬영을 하거나, 다른 면을 촬영해 주세요."
            //"인식이 되지 않았습니다. 카메라 상태를 확인하시고 다시 촬영해 주시거나 다른 면을 촬영해 주세요."
            speek(woringP)
            println(woringP)
            Handler(Looper.getMainLooper()).postDelayed({
                while(true){
                    if(!TTS.isSpeaking){
                        REfilming()//촬영 다시 하기
                        break
                    }
                }
            }, 1400)
        }
    }
    fun getPath(uri: Uri?): String {
        val cursor: Cursor? = uri?.let { contentResolver.query(it, null, null, null, null ) }

        cursor?.moveToNext()

        val path: String? = cursor?.getString(cursor.getColumnIndex("_data"))

        cursor?.close()

        return path?:""
    }

    /**재촬영 요청화면 screenNo = 903*/
    fun REfilming(){
        screenNo=903 //본인 화면
        btn1 = REfilmingCl_1 //촬영 다시 하기
        btn2 = REfilmingCl_2 //세탁정보 등록 그만하기
        main2_button1.text=btn1
        main2_button2.text = btn2

        main2_button1.visibility = View.VISIBLE
        main2_button2.visibility = View.VISIBLE
        main2_button3.visibility = View.GONE //기존 촬영 안내 화면 지우기
        main2_button1.isEnabled = true //버튼 활성화
        main2_button2.isEnabled = true //버튼 활성화
        maino = REfilmingCl //촬영 재안내
        speek(maino)
        println(maino)
    }

    /**세탁 정보 확인 화면 screenNo = 904*/
    fun cheakCare(info:String){
        screenNo = 905 //종료화면
        btn1 = cheakLabel_1 //세탁 정보 듣기
        btn2 = cheakLabel_2 //다음으로 넘어가기
        main2_button1.text=btn1
        main2_button2.text = btn2
        main2_button1.visibility = View.VISIBLE
        main2_button2.visibility = View.VISIBLE
        main2_button3.visibility = View.GONE
        main2_button1.isEnabled = true //버튼 활성화
        main2_button2.isEnabled = true //버튼 활성화

        cheakLabel_1_say = "해당 의류의 세탁정보는 $info 입니다."

        maino = cheakLabel //세탁정보 안내페이지 안내
        speek(maino)
        println(maino)
    }

    /**종료화면 screenNo = 905*/
    fun saveCare(){
        screenNo = 1000
        btn1 = endAddCl_1 //세탁 정보 듣기
        btn2 = endAddCl_2 //다음으로 넘어가기
        main2_button1.text=btn1
        main2_button2.text = btn2
        main2_button1.isEnabled = true //버튼 활성화
        main2_button2.isEnabled = true //버튼 활성화

        maino = endAddCl
        speek(maino)
        println(maino)
    }


    /**Http 연결*/
    fun toHttp(){
        var queue: RequestQueue? = null
        if(queue == null){ //http 쿼리 초기화
            queue = Volley.newRequestQueue(this)
        }
        //Http(queue)//연결
        if(screenNo == 101){ //최근 저장된 의류의 정보의 다음 페이지
            resentlyHttp(queue)
        }
        else if(screenNo == 111){ //검색한 의류의 정보의 다음 페이지
            val fortest = main2_textView.text.toString() //녹음된 텍스트를 가져옴
            val aarr:ArrayList<String> = fortest.split(" ") as ArrayList<String> //fortest 속 문장을 띄어 쓰기를 기준으로 잘라내어 리스트에 넣음
            searchyHttp(queue,aarr)
        }

        /**요청에 따라 어느쪽으로 연결할지 코딩필요*/

    }

    /**TTS기능용*/
    fun speek(say: String){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Handler(Looper.getMainLooper()).postDelayed({
                //TTS.setSpeechRate(1.0f)
                TTS.speak(say, TextToSpeech.QUEUE_FLUSH, null, null)
            }, 200)

        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                //TTS.setSpeechRate(1.0f)
                TTS.speak(say, TextToSpeech.QUEUE_FLUSH, null)
            }, 200)
        }
    }

}
