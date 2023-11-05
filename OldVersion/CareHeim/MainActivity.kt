package com.example.CareHeim
//splash 화면

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var TTS:TextToSpeech

    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val AUDIO_PERMISSION = arrayOf(Manifest.permission.RECORD_AUDIO)
    val INTERNET_PERMISSION = arrayOf(Manifest.permission.INTERNET)
    val WIFI_PERMISSION = arrayOf(Manifest.permission.ACCESS_WIFI_STATE)

    val FLAG_PERM_WIFI = 95
    val FLAG_PERM_INTERNET = 96
    val FLAG_PERM_AUDIO = 97
    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE = 99

    var info = "안녕하세요, 케어하임입니다. 의류 등록을 위한 케어 라벨을 촬영하기 위해 사용되는 어플로 어플을 사용하시려면 저장소, 카메라, 마이크에 대한 접근 권한을 허용해주세요"
    /*“안녕하세요, 케어하임입니다.  의류 등록을 위한 케어 라벨을 촬영하기 위해 사용되는 어플입니다. 노란색의 상단, 보라색의 하단 두개의 버튼으로 구성되어 있으며, 언제든지 안내를 다시 듣고자 하시면 화면을 길게 눌러주세요”*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)
        //checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)
        //checkPermission(AUDIO_PERMISSION, FLAG_PERM_AUDIO)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })

        val pref = getSharedPreferences("isFirst", MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", false)
        if (first == false) {
            Log.d("Is first Time?", "first")
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.commit()
            //앱 최초 실행시 하고 싶은 작업
            start(info)
            println("최초 실행 - $info")
        } else {
            //start(Hello)
            Log.d("Is first Time?", "not first")
        }


        if(checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)){
            if(checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)){
                if(checkPermission(AUDIO_PERMISSION, FLAG_PERM_AUDIO)){
                    while (true) {
                        if (!TTS.isSpeaking) {
                            startActivity(Intent(this, MainmainActivity::class.java)) //첫 화면으로
                            finish()
                            break
                        }
                    }
                    /*Handler(Looper.getMainLooper()).postDelayed({
                        while (true) {
                            if (!TTS.isSpeaking) {
                                startActivity(Intent(this, MainmainActivity::class.java)) //첫 화면으로
                                finish()
                                break
                            }
                        }
                    }, 2000)*/
                }
            }
        }
    }

    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()

    }

    fun checkPermission(permissions: Array<out String>, flag: Int): Boolean { //권한 허용

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (permission in permissions){
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, permissions, flag)
                    return false
                }
            }
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, //요청 주체 확인
        permissions: Array<out String>, //요청 권한 목록
        grantResults: IntArray //권한에 대한 승인 미승인 값 (결과값)
    ){
        when (requestCode){ //요청 시 입력했던 값인지 확인 (승인되었는지 확인)
            FLAG_PERM_STORAGE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        val HelloG = "저장소 권한을 승인해야지만 앱을 사용할 수 있습니다."
                        start(HelloG)
                        println("저장소 권한을 승인해야지만 앱을 사용할 수 있습니다.")
                        finish()
                        return
                    }
                }
                checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)
            }
            FLAG_PERM_CAMERA -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        val HelloC = "카메라 권한을 승인해야지만 앱을 사용할 수 있습니다."
                        start(HelloC)
                        println("카메라 권한을 승인해야지만 앱을 사용할 수 있습니다.")
                        finish()
                        return
                    }
                }
                checkPermission(AUDIO_PERMISSION, FLAG_PERM_AUDIO)
            }
            FLAG_PERM_AUDIO -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        val HelloM = "마이크 권한을 승인해야지만 앱을 사용할 수 있습니다."
                        start(HelloM)
                        println("마이크 권한을 승인해야지만 앱을 사용할 수 있습니다.")
                        finish()
                        return
                    }
                }
                checkPermission(INTERNET_PERMISSION, FLAG_PERM_INTERNET)
                checkPermission(WIFI_PERMISSION, FLAG_PERM_WIFI)
                if (checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE) && checkPermission(CAMERA_PERMISSION,FLAG_PERM_CAMERA) && checkPermission(AUDIO_PERMISSION, FLAG_PERM_AUDIO)
                ) {
                    //start(Hello)
                    while (true) {
                        if (!TTS.isSpeaking) {
                            startActivity(Intent(this, MainmainActivity::class.java)) //첫 화면으로
                            finish()
                            break
                        }
                    }
                    /*Handler(Looper.getMainLooper()).postDelayed({
                        while (true) {
                            if (!TTS.isSpeaking) {
                                startActivity(Intent(this, MainmainActivity::class.java)) //첫 화면으로
                                finish()
                                break
                            }
                        }
                    }, 2000)*/
                    finish()
                }

            }
        }
    }




    fun start(say: String){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Handler(Looper.getMainLooper()).postDelayed({
                //TTS.setSpeechRate(1.0f)
                TTS.speak(say,
                    TextToSpeech.QUEUE_FLUSH,
                    null/*, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED*/)
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
