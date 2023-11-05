package com.example.CareHeim
//의류를 검색하여 등록
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.android.synthetic.main.activity_main41.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class Main41Activity : AppCompatActivity() {
/*“의류의 특징을 한 단어씩 끊어 말해주세요. 특징을 모두 말하셨으면 상단을, 다시 말씀하시려면 하단을 눌러주세요. 3초 후 마이크가 활성화 됩니다. 1, 2, 3"*/

    lateinit var speechRecognizer: SpeechRecognizer
    lateinit var recognitionListener: RecognitionListener

    lateinit var TTS: TextToSpeech
    var main41 = "하단의 버튼을 빠르게 두번 누른 후, 안내에 따라 의류의 특징을 단어로 말해주세요. 특징을 모두 말하셨으면 상단을, 다시 말씀하시려면 하단을 빠르게 두번 눌러주세요."
    var mLastClickTime:Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main41)
        main41_button1.isEnabled = false //한번이라도 마이크가 켜지기 전에는 상단 버튼의 활성화 불가

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })
        if (TTS.isSpeaking){
            TTS.stop()
        }
        start(main41)
        println(main41)

        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        setListener()

        main41_button1.setOnClickListener { //검색한 의류 확인으로

            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main41_button1.isEnabled = false
                main41_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                while (true) {
                    if (!TTS.isSpeaking) {
                        start("저장된 의류를 검색합니다.")
                        println("저장된 의류를 검색합니다.")
                        Handler(Looper.getMainLooper()).postDelayed({
                            while (true) {
                                if (!TTS.isSpeaking) {
                                    val OK = isNetworkAvailable(this)
                                    if(OK){
                                        sendto()
                                    }
                                    else{
                                        val intent = Intent(applicationContext, NetworkF::class.java)
                                        startActivity(intent)
                                    }
                                    break
                                }
                            }
                        }, 1200)
                        break
                    }
                }
            }, 1000)

            }
            else{
                start("다음 단계로 넘어가기")
                println("다음 단계로 넘어가기")
            }
            mLastClickTime = SystemClock.elapsedRealtime()


        }
        main41_button1.setOnLongClickListener {
            start(main41)
            println(main41)
            true
        }



        main41_button2.setOnClickListener {
            //의류 특징 말하기
            /*“3초 후 마이크가 활성화 됩니다. 1, 2, 3”*/
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main41_button1.isEnabled = true
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val re = "3초 후 마이크가 활성화 됩니다. 1, 2, 3"
                            start(re)
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
                            }, 1000)
                            break
                        }
                    }
                }, 700)


            }
            else{
                start("말하기")
                println("말하기")
            }
            mLastClickTime = SystemClock.elapsedRealtime()




        }
        main41_button2.setOnLongClickListener {
            start(main41)
            println(main41)
            true
        }
    }
    override fun onPause(){ //위에 다른 화면이 생길때
        super.onPause()
        TTS.stop()
        main41_button1.isEnabled = true
        main41_button2.isEnabled = true
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
        }
    }

    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main41_button1.isEnabled = true
        main41_button2.isEnabled = true

        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
        }
    }



    private fun setListener() {
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
                textView.setText(fortest) //녹음된 text를 textview에 담아 외부로 전달
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // 부분 인식 결과를 사용할 수 있을 때 호출
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // 향후 이벤트를 추가하기 위해 예약
            }

        }
    }

    fun start(say: String){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Handler(Looper.getMainLooper()).postDelayed({
                //TTS.setSpeechRate(1.0f)
                TTS.speak(say, TextToSpeech.QUEUE_FLUSH, null, null)
            }, 800)

        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                //TTS.setSpeechRate(1.0f)
                TTS.speak(say, TextToSpeech.QUEUE_FLUSH, null)
            }, 800)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false

            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }


    fun sendto(){
        //textView.text = "흰색 긴소매 상의" //제거 (임시용 - 애뮬레이터 테스트용)
        val fortest = textView.text.toString() //녹음된 텍스트를 가져옴

        try{
            val aarr:ArrayList<String> = fortest.split(" ") as ArrayList<String>
            for (element in aarr) { //i in 0 until arr.size
                print("$element/") //element +"/"
            }
            println()

            var re = MakeJson().make(aarr)
            var a = "p"
            Client.Send().ja(re)  //서버로 전송
            Client.Get().run()

            Handler(Looper.getMainLooper()).postDelayed({
                //a = OpenJson().open(Client.Get().aa()) //서버가 보낸 정보 읽기

                val b = Client.Get().aa()//서버가 보낸 정보 읽기
                a = OpenJson().open(b)
                a = "2"

                cheak(a)
            }, 2000)


        }
        catch(e:ClassCastException){
            var sat = "특징을 두가지 이상 말씀해 주시고 버튼을 눌러주세요."
            main41_button1.isEnabled = true
            main41_button2.isEnabled = true
            start(sat)
            println(sat)
        }
    }
    var count = 1
    fun cheak(st:String){

        when(st){
            "p" ->{
                println("이전에 보낸 신호를 재전송하기")
                //재전송
                count+=1
                if (count>=3){
                    println("잘못된 정보가 계속 넘어와 종료")
                    main41 = "서버와의 연결이 원할하지 않습니다. 잠시후 어플을 다시 실행해 주세요."
                    start(main41)
                    println(main41)
                    Handler(Looper.getMainLooper()).postDelayed({
                        while (true) {
                            if (!TTS.isSpeaking) {
                                break
                            }
                            ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                            System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                            System.exit(0) // 현재 액티비티를 종료시킨다
                        }
                    }, 1000)
                }
                else{
                    sendto()
                }


            }
            "-1" ->{
                println("status오류 - 이전에 보낸 신호를 재전송하기")
                //재전송
                count+=1
                if (count>=3){
                    println("잘못된 정보가 계속 넘어와 종료")
                    main41 = "서버와의 연결이 원할하지 않습니다. 잠시후 어플을 다시 실행해 주세요."
                    start(main41)
                    println(main41)
                    Handler(Looper.getMainLooper()).postDelayed({
                        while (true) {
                            if (!TTS.isSpeaking) {
                                break
                            }
                            ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                            System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                            System.exit(0) // 현재 액티비티를 종료시킨다
                        }
                    }, 1000)
                }
                else{
                    sendto()
                }
            }
            "0" ->{
                println("의류없음") //검색되지 않은 의류
                var reS = "검색된 의류가 존재하지 않습니다. 의류의 특징을 다시 말해주세요"
                start(reS)
                println(reS)
            }
            "1" ->{
                println("중복 의류 존재")
                if (TTS.isSpeaking){
                    TTS.stop()
                }
                val OK = isNetworkAvailable(this)
                if(OK){
                    val intent = Intent(applicationContext, Main43Activity::class.java)
                    val forsend = textView.text.toString()
                    intent.putExtra("have",forsend)
                    startActivity(intent)
                    //finish()
                }
                else{
                    val intent = Intent(applicationContext, NetworkF::class.java)
                    startActivity(intent)
                }
            }
            "2" ->{
                println("이미 세탁정보가 등록됨")
                main41 = "해당 의류에 이미 세탁 정보가 저장되어 있습니다. 첫화면으로 돌아갑니다."
                start(main41)
                println(main41)
                //첫화면으로
                val i = Intent(this, Main2Activity::class.java)
                i.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
            else ->{ //정상적으로 최근 등록된 의류가 넘어옴
                println(st)
                val intent = Intent(applicationContext, Main42Activity::class.java)
                intent.putExtra("info",st)
                startActivity(intent)
            }
        }
    }

}
