package com.example.CareHeim
//세탁 정보 출력
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main7.*
import java.util.*

class Main7Activity : AppCompatActivity() {
/*“세탁 정보가 추출 되었습니다. 촬영된 정보를 듣고자 하시면 상단을, 넘어가고자 하시면 하단을 눌러주세요.”*/
    lateinit var TTS: TextToSpeech
    var main7 = "세탁 정보가 추출 되었습니다. 촬영된 정보를 듣고자 하시면 상단을, 넘어가고자 하시면 하단을 빠르게 두번 눌러주세요."
    var mLastClickTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main7)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })

        if (TTS.isSpeaking){
            TTS.stop()
        }


        start(main7)
        println(main7)

        var careinfo = "" //안내문
        var sendcare = ArrayList<String>() //서버 전송용
        try{
            if (intent.hasExtra("care")) {
                var getinfo= intent.getStringExtra("care")
                var te = getinfo.toString()
                careinfo = "해당 의류의 세탁정보는 $getinfo 입니다."
                sendcare = te.split(", ") as ArrayList<String>
                /*"해당 의류는 (내용)하여 세탁해 주세요."*/
            } else {
                println("망할레 - care라벨 정보가 잘못 넘어옴")
            }
        }catch(e:Exception){
            println(e)
        }


        main7_button1.setOnClickListener {
            //추출한 세탁 정보 재생
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            start(careinfo)
                            println(careinfo)
                            break
                        }
                    }
                }, 1200)



            }
            else{
                start("세탁 정보 듣기")
                println("세탁 정보 듣기")
            }
            mLastClickTime = SystemClock.elapsedRealtime()


        }
        main7_button1.setOnLongClickListener {
            start(main7)
            println(main7)
            true
        }



        main7_button2.setOnClickListener {//등록 완료 후 재시작으로

            /*if 네트워크에 오류 발생 시
            val intent = Intent(applicationContext, NetworkF::class.java)
            startActivity(intent)
            else Main8Activity
            */

            //오류 없을 때
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main7_button2.isEnabled = false
                main7_button1.isEnabled = false
                //val intent = Intent(applicationContext, Main8Activity::class.java)
                //startActivity(intent)
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val OK = isNetworkAvailable(this)
                            var a = "p"
                            if(OK){
                                Client.Send().ja(MakeJson().QsaveDB(sendcare)) //케어라벨 정보 저장 요청
                                Handler(Looper.getMainLooper()).postDelayed({
                                    Client.Get().run() //받아온 정보 읽기
                                    val b = Client.Get().aa()//서버가 보낸 정보 읽기
                                    a = OpenJson().open(b)
                                    //a="0" //확인용

                                    when(a){
                                        "0" ->{
                                            println("저장 성공")
                                            val intent = Intent(applicationContext, Main8Activity::class.java)
                                            startActivity(intent)
                                        }
                                        "1" ->{
                                            println("저장 실패 - 재요청")
                                            Client.Send().ja(MakeJson().QsaveDB(sendcare)) //케어라벨 정보 저장 요청
                                            Client.Get().run() //받아온 정보 읽기
                                            Client.Get().run() //받아온 정보 읽기

                                            val b = Client.Get().aa()//서버가 보낸 정보 읽기
                                            var aa = OpenJson().open(b)
                                            when(aa){
                                                "0" ->{
                                                    println("저장 성공")
                                                    val intent = Intent(applicationContext, Main8Activity::class.java)
                                                    startActivity(intent)
                                                }
                                                "1" ->{
                                                    println("저장 실패 - 2 - 강종")
                                                    var re = "서버와의 연결이 원할하지 않습니다. 잠시후 어플을 다시 실행해 주세요."
                                                    start(re)
                                                    println(re)
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        while (true) {
                                                            if (!TTS.isSpeaking) {
                                                                ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                                                                System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                                                                System.exit(0) // 현재 액티비티를 종료시킨다
                                                                break
                                                            }

                                                        }
                                                    }, 1000)
                                                }
                                                else->{
                                                    println("답장이 잘못 옴 - 2 - 강종")
                                                    var re = "서버와의 연결이 원할하지 않습니다. 잠시후 어플을 다시 실행해 주세요."
                                                    start(re)
                                                    println(re)
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        while (true) {
                                                            if (!TTS.isSpeaking) {
                                                                ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                                                                System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                                                                System.exit(0) // 현재 액티비티를 종료시킨다
                                                                break
                                                            }

                                                        }
                                                    }, 1000)
                                                }
                                            }
                                        }
                                        else->{
                                            println("답장이 잘못 옴 - 재요청")
                                            Client.Send().ja(MakeJson().QsaveDB(sendcare)) //케어라벨 정보 저장 요청
                                            Client.Get().run() //받아온 정보 읽기

                                            val b = Client.Get().aa()//서버가 보낸 정보 읽기
                                            var aa = OpenJson().open(b)
                                            aa = "0" //강제 저장 성공
                                            when(aa){
                                                "0" ->{
                                                    println("저장 성공")
                                                    val intent = Intent(applicationContext, Main8Activity::class.java)
                                                    startActivity(intent)
                                                }
                                                "1" ->{
                                                    println("저장 실패 - 2 - 강종")
                                                    var re = "서버와의 연결이 원할하지 않습니다. 잠시후 어플을 다시 실행해 주세요."
                                                    start(re)
                                                    println(re)
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        while (true) {
                                                            if (!TTS.isSpeaking) {
                                                                ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                                                                System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                                                                System.exit(0) // 현재 액티비티를 종료시킨다
                                                                break
                                                            }

                                                        }
                                                    }, 1000)
                                                }
                                                else->{
                                                    println("답장이 잘못 옴 - 2 - 강종")
                                                    var re = "서버와의 연결이 원할하지 않습니다. 잠시후 어플을 다시 실행해 주세요."
                                                    start(re)
                                                    println(re)
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        while (true) {
                                                            if (!TTS.isSpeaking) {
                                                                ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                                                                System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                                                                System.exit(0) // 현재 액티비티를 종료시킨다
                                                                break
                                                            }

                                                        }
                                                    }, 1000)
                                                }
                                            }

                                        }

                                    }
                                }, 1000)

                            }
                            else{
                                val intent = Intent(applicationContext, NetworkF::class.java)
                                startActivity(intent)
                            }
                            break
                        }
                    }
                }, 1200)
            }
            else{
                start("다음으로 넘어가기")
                println("다음으로 넘어가기")
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        main7_button2.setOnLongClickListener {
            start(main7)
            println(main7)
            true
        }
    }

    override fun onPause(){ //위에 다른 화면이 생길때
        super.onPause()
        TTS.stop()
        main7_button1.isEnabled = true
        main7_button2.isEnabled = true
    }
    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main7_button1.isEnabled = true
        main7_button2.isEnabled = true
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
}
