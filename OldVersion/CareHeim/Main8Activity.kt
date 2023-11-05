package com.example.CareHeim
//등록 완료 후 재시작

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main8.*
import java.util.*

class Main8Activity : AppCompatActivity() {
/*“케어 라벨 추가가 완료되었습니다. 새로운 케어라벨 등록 단계를 시작하려면 상단, 첫화면으로 돌아가시려면 하단을 눌러주세요 //새로운 케어라벨 등록 단계를 시작할까요?//”*/
    lateinit var TTS: TextToSpeech
    val main8 = "세탁 정보 등록 단계가 완료되었습니다. 새로운 세탁 정보 등록 단계를 시작하려면 상단을, 첫화면으로 돌아가시려면 하단을 빠르게 두번 눌러주세요"
    var mLastClickTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main8)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })

        if (TTS.isSpeaking){
            TTS.stop()
        }
        start(main8)
        println(main8)
        main8_button1.setOnClickListener {//의류 선택 화면으로
            /*“의류 선택 화면으로 돌아갑니다.”*/
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main8_button1.isEnabled = false
                main8_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            Client.Disconnect().start() //종료 요청
                            val i = Intent(this, Main3Activity::class.java)
                            i.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                            Client().start()
                            break
                        }
                    }
                }, 1200)
            }
            else{
                start("의류 선택으로 돌아가기")
                println("의류 선택으로 돌아가기")
            }
            mLastClickTime = SystemClock.elapsedRealtime()

        }
        main8_button1.setOnLongClickListener {
            start(main8)
            println(main8)
            true
        }



        main8_button2.setOnClickListener { //첫 화면으로
            /*“시작 화면으로 돌아갑니다.”*/
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main8_button1.isEnabled = false
                main8_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            Client.Disconnect().start() //종료 요청
                            val i = Intent(this, Main2Activity::class.java)
                            i.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                            Client().start()
                            break
                        }
                    }
                }, 1200)
            }
            else{
                start("첫화면으로 돌아가기")
                println("첫화면으로 돌아가기")
            }
            mLastClickTime = SystemClock.elapsedRealtime()

        }
        main8_button2.setOnLongClickListener {
            start(main8)
            println(main8)
            true
        }
    }
    override fun onPause(){ //위에 다른 화면이 생길때
        super.onPause()
        TTS.stop()
        main8_button1.isEnabled = true
        main8_button2.isEnabled = true
    }
    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main8_button1.isEnabled = true
        main8_button2.isEnabled = true
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
