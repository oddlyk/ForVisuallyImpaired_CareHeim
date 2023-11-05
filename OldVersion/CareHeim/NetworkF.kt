package com.example.CareHeim

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_network_f.*
import java.util.*

class NetworkF : AppCompatActivity() {
    lateinit var TTS: TextToSpeech

    var Network = ""
    var mLastClickTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_f)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })
        if (TTS.isSpeaking){
            TTS.stop()
        }

        Network = "인터넷 연결이 되지 않습니다. 와이파이나 데이터 연결 상태를 확인 후, 화면을 두번 눌러주세요"
        start(Network)

        network_B.setOnClickListener {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val OK = isNetworkAvailable(this)
                            if(OK){
                                //서버와 재연결
                                serverGo()
                            }
                            else{
                                start(Network)
                                //다시안내
                            }
                            break
                        }
                    }
                }, 1200)
            }
            else{
                start("네트워크 연결 확인")
            }
            mLastClickTime = SystemClock.elapsedRealtime()

        }

        network_B.setOnLongClickListener {
            start(Network)
            true
        }
    }

    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()

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

    private fun serverGo(){ //서버 연결 되었을 때 진행, 안되었을 때 강종
        Client().start()
        Handler(Looper.getMainLooper()).postDelayed({ //소켓이 열릴 시간 주기 (1초?
            println("소켓 닫힘 여부:" +Client.socket.isClosed)
            println("클라이언트 연결 여부:" +Client.socket.isConnected)
            if(!Client.socket.isConnected){ //소켓이 닫혀있다 = 연결 안됨   //socket.isConnected  서버 연결 성공 확인으로 바꿀지
                start("서버와의 연결이 불안정 합니다. 잠시 후 어플을 다시 실행해 주세요.")
                println("서버와의 연결이 불안정 합니다. 잠시 후 어플을 다시 실행해 주세요.")
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) { //상단 안내 완료 후 무한루프 탈출
                            println("서버 연결 실패")
                            //상단 안내 완료 후 강종
                            ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                            System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                            System.exit(0) // 현재 액티비티를 종료시킨다
                            break
                        }
                    }

                }, 1000)
            }
            else{
                println("서버 연결 성공")

                //연결성공안내 후 종료
                val BackTo = "어플로 돌아갑니다."
                start(BackTo)

                Handler(Looper.getMainLooper()).postDelayed({
                    while(true){
                        if(!TTS.isSpeaking){
                            finish()
                            break
                        }
                    }
                }, 1000)
            }
        }, 1000)



    }
}
