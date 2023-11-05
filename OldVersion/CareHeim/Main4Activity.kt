package com.example.CareHeim
//방금 기기에서 등록된 의류에 등록
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main4.*
import java.util.*

class Main4Activity : AppCompatActivity() {
/*"방금 등록된 의류는 (옷 특징) 입니다. 해당 의류에 등록하시려면 상단을 눌러주세요. 첫화면으로 돌아가시려면 하단을 눌러주세요”*/

    lateinit var TTS: TextToSpeech



    var main4 = " " //방금 등록된 의류는  입니다. 해당 의류에 등록하시려면 상단을 눌러주세요. 검색하여 다른 의류를 등록하시려면 하단을 눌러주세요

    var mLastClickTime:Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })

        try{
            if (intent.hasExtra("info")) {
                var getinfo= intent.getStringExtra("info")
                main4 ="방금 등록된 의류는 "+ getinfo.toString()+"입니다. 해당 의류에 등록하시려면 상단을, 검색하여 다른 의류를 등록하시려면 하단을 빠르게 두번 눌러주세요"
            } else {
                println("망할레")
            }
        }catch(e:Exception){
            println(e)
        }






        start(main4)
        println(main4)

        main4_button1.setOnClickListener { //촬영 안내 단계로
            /*케어 라벨 촬영 단계로 이동*/
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main4_button1.isEnabled = false
                main4_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val OK = isNetworkAvailable(this)
                            if(OK){
                                val intent = Intent(applicationContext, Main5Activity::class.java)
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
                start("세탁 정보를 등록하려는 의류가 맞다")
                println("세탁 정보를 등록하려는 의류가 맞다")
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        main4_button1.setOnLongClickListener {
            start(main4)
            println(main4)
            true
        }


        main4_button2.setOnClickListener {

            //의류 검색 화면으로
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main4_button1.isEnabled = false
                main4_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val OK = isNetworkAvailable(this)
                            if(OK){
                                val intent = Intent(applicationContext, Main41Activity::class.java)
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
                start("의류 검색 화면으로")
                println("의류 검색 화면으로")
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        main4_button2.setOnLongClickListener {
            start(main4)
            println(main4)
            true
        }
    }
    override fun onPause(){ //위에 다른 화면이 생길때
        super.onPause()
        TTS.stop()
        main4_button1.isEnabled = true
        main4_button2.isEnabled = true
    }
    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main4_button1.isEnabled = true
        main4_button2.isEnabled = true
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
