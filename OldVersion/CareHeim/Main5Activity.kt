package com.example.CareHeim
//촬영 안내 단계
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main5.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Main5Activity : AppCompatActivity() {

    lateinit var TTS: TextToSpeech

    var main5 = ""
    var mLastClickTime:Long = 0


    val FLAG_REQ_CAMERA = 101 //카메라 호출
/*“케어 라벨 촬영을 진행하겠습니다. 대부분 의류의 케어라벨은 의류의 안쪽, 왼쪽 태그에 위치해 있거나, 목 뒷쪽, 허리 뒷쪽에 위치해 있습니다.
의류를 평평한 곳에 놓고, 카메라를 해당 위치에서 한 뼘 정도 띄운 상태로 촬영해주세요. 기본 카메라를 사용하여 촬영합니다. “
+) 누르면 안내 중에도 촬영으로 넘어감*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        TTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                TTS.language = Locale.KOREAN
            }
        })

        if (TTS.isSpeaking){
            TTS.stop()
        }
        main5 = "세탁 정보 촬영을 진행하겠습니다. 대부분 의류의 세탁 정보는 의류의 안쪽 태그에 위치해 있습니다. 태그는 여러장 존재할 수 있으며, 상황에 따라 태그의 여러면을 촬영해야하거나 접힌 면을 촬영하여야 할 수 있습니다. 어둡지 않고 평평한 곳에 놓고, 카메라를 해당 위치에서 한 뼘 정도 띄운 상태로 촬영해주세요. 기본 카메라를 사용하여 촬영합니다."
        // 위치해 있거나, 목 뒷쪽, 허리 뒷쪽
        start(main5)
        println(main5)
        main5_button1.setOnClickListener { //카메라로 넘어갔다가 촬영 완료 후 자동으로 촬영 정보 분석 화면으로
            if (TTS.isSpeaking){
                TTS.stop()
            }

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                main5_button1.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    while (true) { //make5의 TTS가 끝나기를 기다리는 문
                        if (!TTS.isSpeaking) {
                            val OK = isNetworkAvailable(this)
                            if(OK){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, FLAG_REQ_CAMERA)

                                //val intent2 = Intent(applicationContext, Main6Activity::class.java)
                                //startActivity(intent2)
                            }
                            else{
                                val intent = Intent(applicationContext, NetworkF::class.java)
                                startActivity(intent)
                            }
                            break
                        }
                    }
                }, 1200) }
            else{
                start("촬영하러 가기")
                println("촬영하러 가기")
            }
            mLastClickTime = SystemClock.elapsedRealtime()

        }
        main5_button1.setOnLongClickListener {
            start(main5)
            println(main5)
            true
        }
    }

    override fun onPause(){ //위에 다른 화면이 생길때
        super.onPause()
        TTS.stop()
        main5_button1.isEnabled = true
    }
    override fun onStop() {
        // call the superclass method first
        super.onStop()
        TTS.stop()
        main5_button1.isEnabled = true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //촬영하고 온
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when (requestCode){
                FLAG_REQ_CAMERA -> {
                    if (data?.extras?.get("data") != null){
                        val bitmap = data?.extras?.get("data") as Bitmap
                        val u = saveImageFile(newFileName(), "image/jpg", bitmap)
                        println("uri.u: $u")
                        val uri = getPath(u)


                        ///////////////////////
                        val Outstream = ByteArrayOutputStream() //비트맵을 bytes로 변환
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, Outstream)
                        val gogo = Outstream.toByteArray()
                       // println("{\"image\":$gogo}")
                        val OK = isNetworkAvailable(this)
                        if(OK){
                            main5_button1.text = "촬영 정보 분석중"
                            main5_button1.isEnabled = false
                            start("촬영 정보를 분석중입니다.")
                            println("촬영 정보를 분석중입니다.")
                            Handler(Looper.getMainLooper()).postDelayed({
                                while (true) {
                                    if (!TTS.isSpeaking) {
                                        //labelOp("{\"labels\":[2, 6, 7, 11, 16, 15]}")
                                        //http 연결
                                        Okhttp().run(gogo)
                                        val label =Okhttp().re()

                                            if(label=="오류"){
                                            println("http에서 오류 발생 1")
                                                Okhttp().run(gogo)
                                                val label = Okhttp().re()
                                                if(label=="오류"){
                                                    println("http에서 오류 발생 2")
                                                    start("촬영 정보 분석 중에 오류가 발생했습니다. 잠시 후 어플을 다시 실행해 주세요.")
                                                    println("촬영 정보 분석 중에 오류가 발생했습니다. 잠시 후 어플을 다시 실행해 주세요.")
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
                                                labelOp(label)
                                            }
                                        }
                                        else{
                                            labelOp(label)
                                        }
                                        break
                                    }
                                }
                            }, 1000)

                            Outstream.close()
                        }
                        else{
                            val intent = Intent(applicationContext, NetworkF::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }


    }


    private fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap) : Uri? {
        val values = ContentValues()
       // values.put(MediaStore.Audio.Media.RELATIVE_PATH, "DCIM/Care-Heim")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try{
            if (uri != null){
                var descriptor = contentResolver.openFileDescriptor(uri, "w")
                if (descriptor != null){
                    val fos = FileOutputStream(descriptor.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos) //비트맵 저장
                    fos.close()

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, values, null, null)
                    }
                }
            }
        } catch (e:java.lang.Exception){
            println("File error=${e.localizedMessage}")
        }
        return uri
    }

    private fun newFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())

        return "$filename.jpg"
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

    fun labelOp(la:String){
        val op = JSONObject(la).getJSONArray("labels")
        val cc : List<Int> = (0 until op.length()).map { //받아온 라벨의 값을 리스트에 담기
            op.getInt(it)
        }
        println("받아온 라벨 값 전체: $cc")


        var careinfo = ArrayList<String>()
        if(cc.size>1){ // 받아온 라벨 값이 1개 이상일 때
            val cb = cc.distinct() //라벨 값 속 중복 값 제거
            println("라벨값 중 중복 값 제거: $cb")

            val bab = cb.sorted() //라벨 값 정렬 (오름차순)
            println("라벨값 오름차순 정렬: $bab")

            if(bab.size<3){ //세탁 기호가 3개 미만임
                //추출된 세탁기호가 2개 이하로 인식되었을 경우 해당 면 재촬영 요청 (추가사항: 대기 필요-1. 앞에서의 안내가 끝날때까지, 세탁 기호 추출이 끝날때까지
                val moreT = "세탁 기호가 바르게 인식 되지 않았습니다. 빛이나 접힌 부분에 유의하여 해당 면을 다시 촬영하여 주세요"
                start(moreT)
                println(moreT)
                Handler(Looper.getMainLooper()).postDelayed({
                    while(true){
                        if(!TTS.isSpeaking){
                            startActivity(Intent(this, Main51Activity::class.java)) //재촬영 요청 화면으로
                            finish()
                            break
                        }
                    }
                }, 1400)
            }
            else{
                for(i in bab.size-1 downTo 0){ //가장 큰 굿자부터 역순으로 체크 (2의 손세탁/기계세탁을 뒤의 것들로 체크해야하기에
                    when(bab[i]){
                        17->{
                            println("국제 자연 건조")
                            careinfo.add("자연 건조 가능")
                        }
                        16->{
                            println("한국 자연 건조")
                            careinfo.add("자연 건조 가능")
                        }
                        15->{
                            println("기계 건조 불가능")
                            careinfo.add("기계 건조 불가능")
                        }
                        14->{
                            println("기계 건조 가능")
                            careinfo.add("기계 건조 가능")
                        }
                        13->{
                            println("탈수 불가능")
                            careinfo.add("탈수 불가능")
                        }
                        12->{
                            println("약하게 탈수")
                            careinfo.add("약하게만 탈수 가능")
                        }
                        11->{
                            println("드라이 불가능")
                            careinfo.add("드라이 불가능")
                        }
                        10->{
                            println("국제 드라이 가능")
                            careinfo.add("드라이 가능")
                        }
                        9->{
                            println("한국 드라이 가능")
                            careinfo.add("드라이 가능")
                        }
                        8->{
                            println("다림질 불가능")
                            careinfo.add("다림질 불가능")
                        }
                        7->{
                            println("다림질 가능")
                            careinfo.add("다림질 가능")
                        }
                        6->{
                            println("표백 불가능")
                            careinfo.add("표백 불가능")
                        }
                        5->{
                            println("표백 가능")
                            careinfo.add("표백 가능")
                        }
                        4->{
                            println("물 세탁 불가능")
                            careinfo.add("물 세탁 불가능")
                        }
                        3->{
                            println("국제 손세탁")
                            careinfo.add("손세탁 가능")
                        }
                        2->{
                            println("국제 기계세탁, 국내 손세탁")
                            if(bab.contains(10)){ //드라이 클리닝 기호로 구분 //국제 드라이 가능
                                println("국제 기계 세탁")
                                careinfo.add("기계 세탁 가능")
                            }
                            else if(bab.contains(9)){ //국내 드라이 가능
                                println("국내 손세탁")
                                careinfo.add("손세탁 가능")
                            }
                            else{ //자연건조 기호로 구분
                                if(bab.contains(17)){ //국제 자연건조 가능
                                    println("국제 기계 세탁")
                                    careinfo.add("기계 세탁 가능")
                                }
                                else if(bab.contains(16)){ //국내 자연건조 가능
                                    println("국내 손세탁")
                                    careinfo.add("손세탁 가능")
                                }
                                else{ //기계 건조 가능 기호로 구분
                                    if(bab.contains(14)){ //기계 건조 가능
                                        println("국제 기계 세탁")
                                        careinfo.add("기계 세탁 가능")
                                    }
                                    else{ //끝까지 구분 불가 일 때
                                        println("국내 손세탁")
                                        careinfo.add("손세탁 가능")
                                    }

                                }
                            }
                        }
                        1->{
                            println("한국 기계세탁")
                            careinfo.add("기계 세탁 가능")
                        }

                    }
                }
                //촬영이 제대로 되어 추출이 바르게 된 경우, 세탁 정보 출력으로
                Handler(Looper.getMainLooper()).postDelayed({
                    while(true){
                        if(!TTS.isSpeaking){
                            var caca = ""
                            val casi = careinfo.size-1
                            caca = careinfo[casi]  //가장 마지막 추출된 세탁 정보를 담기
                            for(i in careinfo.size-2 downTo 0){//세탁 정보 list 값을 한 문장으로 만듦 (역순으로 - 손세탁/기계세탁 여부부터 안내하도록
                                caca= caca+", " //끊어서 안내하도록
                                caca = caca+careinfo[i]
                            }
                            val intent = Intent(this, Main7Activity::class.java)
                            intent.putExtra("care", caca ) //다음 화면으로 세탁정보 string 전달
                            startActivity(intent)
                            finish()
                            break
                        }
                    }
                }, 1800)

            }

        }
        else {  //촬영이 잘못 된 경우, 안내 후, 촬영 안내 단계로 - 넘어온 인자가 한개도 없음
            println("추출된 기호 없음")
            val woringP = "해당 사진에 인식 가능한 세탁 기호가 없습니다. 사진이 흔들렸거나, 해당 면에 기호가 존재하지 않을 수 있습니다. 다시 촬영을 하거나, 다른 면을 촬영해 주세요."
            //"인식이 되지 않았습니다. 카메라 상태를 확인하시고 다시 촬영해 주시거나 다른 면을 촬영해 주세요."
            start(woringP)
            println(woringP)
            Handler(Looper.getMainLooper()).postDelayed({
                while(true){
                    if(!TTS.isSpeaking){
                        startActivity(Intent(this, Main51Activity::class.java)) //재촬영 요청 화면으로
                        finish()
                        break
                    }
                }
            }, 1400)
        }
    }

    fun getPath(uri: Uri?): String {
        val cursor: Cursor? = uri?.let { contentResolver.query(it, null, null, null, null ) }

        cursor?.moveToNext()

        val path: String? = cursor?.getString(cursor.getColumnIndex("_data"))

        cursor?.close()

        return path?:""
    }
}
