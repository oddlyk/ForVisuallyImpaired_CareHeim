package com.example.CareHeim
//첫화면
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


class Main2Activity : AppCompatActivity() {
    lateinit var TTS: TextToSpeech


    var main2 = "의류에 세탁 정보를 등록하시려면 상단을, 어플을 종료하시려면 하단을 빠르게 두번 눌러주세요. "


    var mLastClickTime:Long = 0
    /*“의류에 케어 라벨을 등록하시려면 상단의 버튼을 눌러주세요.”*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })

        println(main2)
        start(main2)




        main2_button1.setOnClickListener {
            if (TTS.isSpeaking){
                TTS.stop()
            }

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main2_button1.isEnabled = false
                main2_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {

                            val OK = isNetworkAvailable(this)
                            if(OK){
                                val intent = Intent(applicationContext, Main3Activity::class.java)
                                startActivity(intent)
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
                start("세탁 정보 등록")
                println("세탁 정보 등록")
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        main2_button1.setOnLongClickListener {
            start(main2)
            println(main2)
            true
        }


        main2_button2.setOnClickListener {
            //미구현기능 임시로서 종료 기능을 넣어둠=>talk back 기능 사용시 하단 버튼 사용이 불가하였음

            if (TTS.isSpeaking){
                TTS.stop()
            }

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main2_button1.isEnabled = false
                main2_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            start("이용해주셔서 감사합니다.")
                            println("이용해주셔서 감사합니다.")
                            Client.Disconnect().start() //종료 요청
                            Handler(Looper.getMainLooper()).postDelayed({
                                while (true) {
                                    if (!TTS.isSpeaking) {
                                        ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                                        System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                                        System.exit(0) // 현재 액티비티를 종료시킨다.
                                        break
                                    }
                                }
                            }, 800)
                            break
                        }
                    }
                }, 1200)



            }
            else{
                start("어플 종료")
                println("어플 종료")
            }
            mLastClickTime = SystemClock.elapsedRealtime()





        }
        main2_button2.setOnLongClickListener {
            start(main2)
            println(main2)
            true
        }


    }



    override fun onPause(){ //위에 다른 화면이 생길때
        super.onPause()
        TTS.stop()
        main2_button1.isEnabled = true
        main2_button2.isEnabled = true
    }


    override fun onStop() { //화면이 안보이게 되었을 때 동작 (어플을 나갔다 들어올 때
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main2_button1.isEnabled = true
        main2_button2.isEnabled = true
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
