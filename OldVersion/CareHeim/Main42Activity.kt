package com.example.CareHeim
//검색한 의류 확인
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import kotlinx.android.synthetic.main.activity_main42.*
import java.util.*

class Main42Activity : AppCompatActivity() {
/*"방금 등록된 의류는 (옷 특징) 입니다. 해당 의류에 등록하시려면 상단을 아니라면 하단을 눌러주세요.”*/
    //서버로 부터 옷 특징 받아오기
    lateinit var TTS: TextToSpeech
    var main42 = ""  //"검색된 의류는 옷 특징 입니다. 해당 의류에 등록하시려면 상단을 의류를 다시 검색 하시려면 하단을 눌러주세요."
    var mLastClickTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main42)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })
        if (TTS.isSpeaking){
            TTS.stop()
        }
        try{
            if (intent.hasExtra("info")) {
                var getinfo= intent.getStringExtra("info")
                main42 ="검색된 의류는 "+ getinfo.toString()+"입니다. 해당 의류에 등록하시려면 상단을, 의류를 다시 검색하시려면 하단을 빠르게 두번 눌러주세요."
            } else {
                println("망할레")
            }
        }catch(e:Exception){
            println(e)
        }


        start(main42)
        println(main42)

        main42_button1.setOnClickListener { //촬영 안내 단계로
            /*“케어 라벨 촬영 단계로 이동합니다.”*/
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main42_button1.isEnabled = false
                main42_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val intent = Intent(applicationContext, Main5Activity::class.java)
                            startActivity(intent)
                            break
                        }
                    }
                }, 1200)
            }
            else{
                start("세탁 정보를 등록하려는 의류가 맞다")
                println("세탁 정보를 등록하려는 의류가 맞다")
            }
            mLastClickTime = SystemClock.elapsedRealtime()

        }
        main42_button1.setOnLongClickListener {
            start(main42)
            println(main42)
            true
        }


        main42_button2.setOnClickListener {//의류 재검색 (이전 단계로)
            /*“의류 검색 단계로 돌아갑니다.”*/
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main42_button1.isEnabled = false
                main42_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val intent = Intent(applicationContext, Main41Activity::class.java)
                            startActivity(intent)
                            finish()
                            break
                        }
                    }
                }, 1200)
            }
            else{
                start("의류를 다시 검색한다")
                println("의류를 다시 검색한다")
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        main42_button2.setOnLongClickListener {
            start(main42)
            println(main42)
            true
        }
    }

    override fun onPause(){ //위에 다른 화면이 생길때
        super.onPause()
        TTS.stop()
        main42_button1.isEnabled = true
        main42_button2.isEnabled = true
    }
    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main42_button1.isEnabled = true
        main42_button2.isEnabled = true
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
}
