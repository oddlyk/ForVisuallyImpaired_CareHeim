package com.example.CareHeim
//등록할 의류 선택 화면
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main3.*
import java.util.*

class Main3Activity : AppCompatActivity() {
/*“방금 기기에서 등록된 의류에 케어라벨을 추가하시려면 상단의 버튼을, 의류를 검색하여 추가하시려면 하단의 버튼을 눌러주세요.”*/

    lateinit var TTS: TextToSpeech
    var main3 = "방금 기기에서 등록된 의류에 세탁 정보를 추가하시려면 상단을, 의류를 검색하여 추가하시려면 하단을 빠르게 두번 눌러주세요."
    var mLastClickTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })
        if (TTS.isSpeaking){
            TTS.stop()
        }
        start(main3)
        println(main3)

        main3_button1.setOnClickListener {//방금 기기에서 등록된 의류에 등록
            /*등록된 의류 정보를 받아오는 중*/
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main3_button1.isEnabled = false
                main3_button2.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val OK = isNetworkAvailable(this)
                            if(OK){
                                sendto()
                                break
                            }
                            else{
                                val intent = Intent(applicationContext, NetworkF::class.java)
                                startActivity(intent)
                                break
                            }

                        }
                    }
                }, 1200)



            }
            else{
                start("방금 기기에서 등록된 의류에 세탁 정보 추가")
                println("방금 기기에서 등록된 의류에 세탁 정보 추가")
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        main3_button1.setOnLongClickListener {
            start(main3)
            println(main3)
            true
        }


        main3_button2.setOnClickListener {//의류를 검색하여 등록
            if (TTS.isSpeaking){
                TTS.stop()
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main3_button1.isEnabled = false
                main3_button2.isEnabled = false
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
                start("의류를 검색하여 추가")
                println("의류를 검색하여 추가")
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        main3_button2.setOnLongClickListener {
            start(main3)
            println(main3)
            true
        }
    }

    override fun onPause(){ //위에 다른 화면이 생길때
        super.onPause()
        TTS.stop()
        main3_button1.isEnabled = true
        main3_button2.isEnabled = true
    }
    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main3_button1.isEnabled = true
        main3_button2.isEnabled = true

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
       Client.Send().ja(MakeJson().QgetCl()) //저장된 의류 정보 요청
        var a = "p"
        Client.Get().run() //받아온 정보 읽기

        Handler(Looper.getMainLooper()).postDelayed({

            val b = Client.Get().aa()//서버가 보낸 정보 읽기
            a = OpenJson().open(b)
            cheak(a)
        }, 2000)






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
                    main3 = "서버와의 연결이 원할하지 않습니다. 잠시후 어플을 다시 실행해 주세요."
                    start(main3)
                    println(main3)
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
                    main3 = "서버와의 연결이 원할하지 않습니다. 잠시후 어플을 다시 실행해 주세요."
                    start(main3)
                    println(main3)
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
                else{
                    sendto()
                }
            }
            "0" ->{
                println("의류없음")
                main3 = "등록 되어 있는 의류가 없습니다. 의류를 등록하고 어플을 사용해주세요. 첫화면으로 돌아갑니다."
                start(main3)
                println(main3)
                //첫화면 으로
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val i = Intent(this, Main2Activity::class.java)
                            i.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                            break
                        }
                    }
                }, 1200)

            }
            "1" ->{
                println("중복 의류 존재")
                //현 단계에서 고려할 필요 없음
            }
            "2" ->{
                println("이미 세탁정보가 등록됨")
                main3 = "최근 등록 의류에 이미 세탁 정보가 저장되어 있습니다. 첫화면으로 돌아갑니다."
                start(main3)
                println(main3)
                //첫화면으로
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) {
                        if (!TTS.isSpeaking) {
                            val i = Intent(this, Main2Activity::class.java)
                            i.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                            break
                        }
                    }
                }, 1200)

            }
            else ->{ //정상적으로 최근 등록된 의류가 넘어옴
                println(st)

                val intent = Intent(applicationContext, Main4Activity::class.java)
                intent.putExtra("info",st)
                startActivity(intent)
            }
        }
    }

}
