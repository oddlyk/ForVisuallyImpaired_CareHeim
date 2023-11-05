package com.example.CareHeim

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainmainActivity : AppCompatActivity() {
    lateinit var TTS: TextToSpeech

    var Hello = "안녕하세요, 케어하임입니다. 노란색의 상단, 보라색의 하단 두개의 버튼으로 구성되어 있으며, 버튼을 한번 누르면 버튼의 이름이 나오고, 빠르게 두번 누르면 안내가 종료됨과 동시에 버튼이 동작합니다. 언제든지 안내를 다시 듣고자 하시면 화면을 길게 눌러주세요. "
    /*“안녕하세요, 케어하임입니다.  의류 등록을 위한 케어 라벨을 촬영하기 위해 사용되는 어플입니다. 노란색의 상단, 보라색의 하단 두개의 버튼으로 구성되어 있으며, 언제든지 안내를 다시 듣고자 하시면 화면을 길게 눌러주세요
    노란색의 상단, 보라색의 하단 두개의 버튼으로 구성되어 있으며, 버튼을 한번 터치 시에는 버튼의 이름이 나오고, 두번 터치 시에 안내가 종료됨과 동시에 버튼이 동작합니다. 언제든지 안내를 다시 듣고자 하시면 화면을 길게 눌러주세요.
    ”*/
    var mLastClickTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })

        var count = 0
        println(Hello)
        start(Hello)
        //서버 연결 확인 안되면 안내하고 강제 종료
        /*Client().start()
        if(Client.socket.isClosed){
            start("서버와의 연결이 불안정 합니다. 인터넷 상태를 확인하시고 어플을 다시 실행해 주세요.")
            println("서버와의 연결이 불안정 합니다. 인터넷 상태를 확인하시고 어플을 다시 실행해 주세요.")
            Handler(Looper.getMainLooper()).postDelayed({
                while (true) {
                    if (!TTS.isSpeaking) {
                        ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
                        System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
                        System.exit(0) // 현재 액티비티를 종료시킨다
                    }
                }
            }, 1000)
        }
        else{
            println("서버 연결 성공")
        }*/

        /*start("서버와의 연결이 불안정 합니다. 인터넷 상태를 확인하시고 어플을 다시 실행해 주세요.")
        ActivityCompat.finishAffinity(this) //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
        System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
        System.exit(0) // 현재 액티비티를 종료시킨다*/

        Handler(Looper.getMainLooper()).postDelayed({ //버튼 클릭 없이 안내가 끝났을 때
            while (true) {
                if (!TTS.isSpeaking) {

                    if (button.isEnabled) { //button.isEnabled 버튼이 계속 활성화된 상태일 때 진행
                        println("Openpage - Nobutton")
                        //val intent = Intent(applicationContext, Main2Activity::class.java)  //진행을 위한 임시 강제 넘김 코드
                        //startActivity(intent)
                        //finish()
                        watchNet() //네트워크 상태 확인 후 연결 되었을 때 서버 연결, 안되었을 때 강종
                    }
                    else{
                    }
                    break
                }
            }
        }, 4000)



        button.setOnClickListener { //버튼 클릭 시 안내 중단
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){ //더블 클릭 시
                println("button Double clicked")
                button.isEnabled = false
                TTS.stop()
                count+=1

                println("Openpage - Yesbutton")
                //val intent = Intent(applicationContext, Main2Activity::class.java) //진행을 위한 임시 강제 넘김 코드
                //startActivity(intent)
                //finish()

                watchNet() //네트워크 상태 확인 후 연결 되었을 때 서버 연결, 안되었을 때 강종
                //finish()
            }
            else{
                count+=1
                println("button One clicked")
                //json 테스트


            }
            mLastClickTime = SystemClock.elapsedRealtime()
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
                TTS.speak(say,
                    TextToSpeech.QUEUE_FLUSH,
                    null/*, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED*/)
            }, 1000)

        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                //TTS.setSpeechRate(1.0f)
                TTS.speak(say, TextToSpeech.QUEUE_FLUSH, null)
            }, 1000)
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
    private fun watchNet(){  //네트워크 상태 확인 후 연결 되었을 때 서버 연결, 안되었을 때 강종
        val OK = isNetworkAvailable(this)
        if(OK){//네트워크 연결이 되어있다면 서버 연결 시작
            serverGo()  //서버 연결 되었을 때 진행, 안되었을 때 강종

        }
        else{ //네트워크 연결 안되면 강종
            start("인터넷 연결이 불안정 합니다. 와이파이 및 데이터 상태를 확인하시고 어플을 다시 실행해 주세요.")
            println("인터넷 연결이 불안정 합니다. 와이파이 및 데이터 상태를 확인하시고 어플을 다시 실행해 주세요.")
            //val intent = Intent(applicationContext, NetworkF::class.java)
            //startActivity(intent)
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

                Handler(Looper.getMainLooper()).postDelayed({

                }, 1000)

                //서버연결, 전송, 받기 후 바로 종료 (테스트)

                /*Handler(Looper.getMainLooper()).postDelayed({
                    Client.Send().ja(MakeJson().QgetCl()) //최근 등록 의류 요청 전송
                    Client.Get().run() //받아온 정보 읽기
                    Handler(Looper.getMainLooper()).postDelayed({
                        val b = Client.Get().aa()
                        println("G: $b")
                        val a = OpenJson().open(b)
                        println("G: $a")
                    }, 2000)

                }, 1000)*/

                Handler(Looper.getMainLooper()).postDelayed({
                    if(!Client.socket.isClosed){ //소켓이 열려있을 때
                        /*Client.Send().ja(MakeJson().QcloseS())
                        Client.Get().start()
                        if(Get에서 답이 없거나 잘못되었을 때){ //이부분을 Get의 종료 확인에서
                            종료 요청 재전송 혹은 어쩌구
                        }
                        else{
                            Client.Disconnect().start() //종료 요청
                        }*/

                    }else{
                        println("서버와 연결이 되어있지 않습니다.")
                    }
                }, 3000)


                val intent = Intent(applicationContext, Main2Activity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1000)

    }
}
