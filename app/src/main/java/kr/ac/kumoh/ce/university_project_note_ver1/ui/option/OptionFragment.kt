package kr.ac.kumoh.ce.university_project_note_ver1.ui.option

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DCIM
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.ammarptn.gdriverest.DriveServiceHelper
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import kr.ac.kumoh.ce.university_project_note_ver1.MainActivity
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.AppDatabase
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.NoteAdapter
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.TimelineFragment
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.model.Note
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OptionFragment : Fragment() {
    private var noteCount:Int = 0                               // DB에 저장된 노트의 개수
    private var noteList: MutableList<Note> = mutableListOf()   // DB에 저장된 노트의 리스트

    lateinit var root:View
    lateinit var optionPassword: TextView
    lateinit var changePasswordLayout: ConstraintLayout
    lateinit var changePasswordButton: Button
    lateinit var currentPassword: EditText
    lateinit var newPassword: EditText
    lateinit var confirmPassword: EditText

    lateinit var backupAndRestore: TextView
    lateinit var backupAndRestoreLayout: ConstraintLayout

    lateinit var button_backup:Button
    lateinit var button_restore:Button
    lateinit var button_drive_backup:Button
    lateinit var button_drive_restore:Button
    lateinit var auto_image_read:Button

    lateinit var progressBar:ProgressBar
    lateinit var progressBar_text:TextView

    lateinit var backup_id: EditText
    lateinit var backup_help_id: TextView
    lateinit var email: TextView

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mDriveServiceHelper: DriveServiceHelper? = null

    val SECRET_PASSWORD = "verySecretEncryptionKey" // 암호화 키 설정

    var passwordSharedPreferences: SharedPreferences? = this.context?.getSharedPreferences("password", Context.MODE_PRIVATE)
    var last_image_name_SharedPreferences: SharedPreferences? = this.context?.getSharedPreferences("last_image_name", Context.MODE_PRIVATE)

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 100
        private const val TAG = "GoogleDriveFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_option, container, false)

        val file_id = this.context?.getSharedPreferences("file_id", Context.MODE_PRIVATE)
        last_image_name_SharedPreferences = this.context?.getSharedPreferences("last_image_name", Context.MODE_PRIVATE)

        optionPassword = root.findViewById(R.id.optionPassword)
        changePasswordLayout = root.findViewById(R.id.changePasswordLayout)
        changePasswordButton = root.findViewById(R.id.changePasswordButton)
        currentPassword = root.findViewById(R.id.currentPassword)
        newPassword = root.findViewById(R.id.newPassword)
        confirmPassword = root.findViewById(R.id.confirmPassword)
        passwordSharedPreferences = this.context?.getSharedPreferences("password", Context.MODE_PRIVATE)
        backupAndRestore = root.findViewById(R.id.backupAndRestore)
        backupAndRestoreLayout = root.findViewById(R.id.backupAndRestoreLayout)

        button_backup = root.findViewById(R.id.btn_backup)
        button_restore = root.findViewById(R.id.btn_restore)
        button_drive_backup = root.findViewById(R.id.btn_drive_backup)
        button_drive_restore = root.findViewById(R.id.btn_drive_restore)
        auto_image_read = root.findViewById(R.id.auto_image_read)

        progressBar = root.findViewById(R.id.progressBar)
        progressBar_text = root.findViewById(R.id.progressBar_text)

        backup_id = root.findViewById(R.id.backup_id)
        backup_help_id = root.findViewById(R.id.backup_help_id)
        email = root.findViewById(R.id.email)

        optionPassword.setOnClickListener {
            if (changePasswordLayout.visibility == View.GONE)
                changePasswordLayout.visibility = View.VISIBLE
            else
                changePasswordLayout.visibility = View.GONE
        }
        backupAndRestore.setOnClickListener {
            if(backupAndRestoreLayout.visibility == View.GONE)
                backupAndRestoreLayout.visibility = View.VISIBLE
            else
                backupAndRestoreLayout.visibility = View.GONE
        }

        changePasswordButton.setOnClickListener {
            val cP = passwordSharedPreferences?.getString("password", "").toString()
            if (currentPassword.text.toString() == cP && newPassword.text.toString() == confirmPassword.text.toString()){
                passwordSharedPreferences?.edit {
                    putString("password", newPassword.text.toString())
                }
                Toast.makeText(this.context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this.context, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
            }
        }

        button_backup.setOnClickListener { // 로컬 백업 버튼
            RoomBackup()
                .context(root.context)
                .database(TimelineFragment.db)
                .enableLogDebug(true)
                .backupIsEncrypted(true)
                .customEncryptPassword(SECRET_PASSWORD)
                .useExternalStorage(true)
                //maxFileCount: else 1000 because i cannot surround it with if condition
                .maxFileCount(1)
                .apply {
                    onCompleteListener { success, message ->
                        Log.d(OptionFragment.TAG, "success: $success, message: $message")
                        Toast.makeText(context, "success: $success, message: $message", Toast.LENGTH_LONG).show()
                        //if (success) restartApp(Intent(context, MainActivity::class.java))
                        backup_id.hint = ""
                        backup_help_id.text = "로컬 백업이 완료되었습니다!"
                    }
                }
                .backup()
        }

        button_restore.setOnClickListener { // 로컬 복원 버튼
            RoomBackup()
                .context(root.context)
                .database(TimelineFragment.db)
                .enableLogDebug(true)
                .backupIsEncrypted(true)
                .customEncryptPassword(SECRET_PASSWORD)
                .useExternalStorage(true)
                .apply {
                    onCompleteListener { success, message ->
                        Log.d(OptionFragment.TAG, "success: $success, message: $message")
                        Toast.makeText(context, "success: $success, message: $message", Toast.LENGTH_LONG).show()
                        //if (success) restartApp(Intent(context, MainActivity::class.java))
                    }
                    backup_id.hint = ""
                    backup_help_id.text = "로컬 복원이 완료되었습니다!"
                }
                .restore()
        }

        button_drive_backup.setOnClickListener { // 구글 드라이브 백업 버튼
            if (mDriveServiceHelper == null) {
                return@setOnClickListener
            }

            val tempRoot = ArrayList<String>()

            File(root.context.getExternalFilesDir(null)!!.absolutePath+"/backup").walk().forEach {
                Log.d("테스트4", it.canonicalPath)
                tempRoot.add(it.absolutePath)
            }
            if (tempRoot.toString() == "[]") {
                backup_id.hint = ""
                backup_help_id.text = "클라우드 백업이 실패하였습니다.\n로컬 백업을 먼저 실행해주세요!"
                return@setOnClickListener
            }
            val splitArray = tempRoot[1].split("files")
            Log.d("테스트5", splitArray[1])

            Log.d(OptionFragment.TAG, "테스트 uploadFile Log: " + tempRoot[1])

            mDriveServiceHelper!!.uploadFile(File(root.context.getExternalFilesDir(null), splitArray[1]), "text/plain", null)
                .addOnSuccessListener { googleDriveFileHolder ->
                    val gson = Gson()
                    Log.d(OptionFragment.TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder))
                    val temp = JSONObject(gson.toJson(googleDriveFileHolder))

                    file_id?.edit {
                        putString("file_id", temp.getString("id"))
                    }

                    backup_id.setText(temp.getString("id").toString())
                    backup_help_id.text = "클라우드 백업이 완료되었습니다. \n 키를 복사하세요!"
                }
                .addOnFailureListener {
                        e -> Log.d(OptionFragment.TAG, "onFailure: " + e.message)
                    backup_id.hint = ""
                    backup_help_id.text = "클라우드 백업이 실패하였습니다."
                }
        }

        button_drive_restore.setOnClickListener { // 구글 드라이브 복원 버튼
            if (mDriveServiceHelper == null) {
                return@setOnClickListener
            }

//            mDriveServiceHelper!!.downloadFile(File(root.context.getExternalFilesDir(null), "backup/drive.aes"), file_id?.getString("file_id", "").toString())
            mDriveServiceHelper!!.downloadFile(File(root.context.getExternalFilesDir(null), "backup/drive.aes"), backup_id.text.toString())
                .addOnSuccessListener {
                    //Log.d(OptionFragment.TAG, "onSuccess: " + file_id?.getString("file_id", "").toString())
                    backup_id.setText("")
                    backup_help_id.text = "클라우드 복원이 완료되었습니다.\n로컬 복원에서 drive.aes 를 선택하여 복원해주세요!"
                }
                .addOnFailureListener {
                        e -> Log.d(OptionFragment.TAG, "onFailure: " + e.message)
                    backup_id.hint = ""
                    backup_help_id.text = "클라우드 복원이 실패하였습니다.\n올바른 키 값을 입력해주세요."
                }
        }

        auto_image_read.setOnClickListener { // 사진 자동 불러오기 버튼
            val tempRoot = ArrayList<String>()
            val Root = ArrayList<String>()

            File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).absolutePath+"/camera").walk().forEach {
                if (it.extension.equals("jpg")) {
                    //Log.d("", it.name)
                    tempRoot.add(it.absolutePath)
                }

            } // 배열로 Camera 폴더 안의 모든 것들을 가지고 옴.
            tempRoot.sortDescending() //내림차순 정렬

            var last_image_time = last_image_name_SharedPreferences?.getString("last_image_name", "") // 전역변수 마지막이미지 이름(숫자만 있으니 마지막 이미지 시간과 동일)
            var max_size = 0

            for(i in tempRoot.indices){
                val absolutePath = tempRoot[i]

                val current_image_time_array = absolutePath?.split("camera") // Camera 폴더 안에 있는 이미지의 last_image_name 추출.
                if(current_image_time_array?.size!! > 1){
                    val current_image_time = current_image_time_array[1].replace(("[^0-9]").toRegex(), "").substring(0, 14)
                    // 현재 이름을 숫자로 변환(ex. 20210603_132728 -> 20210603132728)
                    // .substring(0, 14) 을 통해 뒤에 쓸대없는 값 제거.
                    if (last_image_time != null) {
                        if(last_image_time == "")
                            last_image_time = "0"
                        if(last_image_time.toDouble() < current_image_time.toDouble()){
                            max_size++
                            Root.add(tempRoot[i])
                        }
                    }
                }
            }
            // - - - - - - - - - - - - - - - - - - - - -
            val builder = AlertDialog.Builder(root.context)
            builder.setTitle("최신 사진을 불러오시겠습니까?")
            builder.setMessage("${max_size}개의 사진을 불러 올 수 있습니다.\n'${last_image_time}' 이후 기준")
            builder.setPositiveButton("확인") {
                    dialoginterface: DialogInterface?, i: Int ->
                Thread(Runnable {
                    auto(max_size, Root)
                }).start()
            }
            builder.setNegativeButton("취소") {
                    dialoginterface:DialogInterface?, i:Int ->
                Log.d(OptionFragment.TAG, "테스트 취소버튼 누름.")

            }
            builder.show()
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account == null) {
            signIn()
        } else {
            email.text = "연결된 계정 : " + account.email
            mDriveServiceHelper = DriveServiceHelper(DriveServiceHelper.getGoogleDriveService(context, account, "appName"))
        }
    }

    private fun signIn() {
        mGoogleSignInClient = buildGoogleSignInClient()
        startActivityForResult(mGoogleSignInClient!!.signInIntent,
            OptionFragment.REQUEST_CODE_SIGN_IN
        )
    }

    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(com.google.android.gms.drive.Drive.SCOPE_FILE)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(root.context, signInOptions)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            OptionFragment.REQUEST_CODE_SIGN_IN -> if (resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                handleSignInResult(resultData)
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleSignInAccount ->
                Log.d(OptionFragment.TAG, "Signed in as " + googleSignInAccount.email)
                email!!.text = "연결된 계정 : " + googleSignInAccount.email
                mDriveServiceHelper = DriveServiceHelper(DriveServiceHelper.getGoogleDriveService(context, googleSignInAccount, "appName"))
                Log.d(OptionFragment.TAG, "handleSignInResult: $mDriveServiceHelper")
            }
            .addOnFailureListener { e -> Log.e(OptionFragment.TAG, "Unable to sign in.", e) }
    }

    private fun auto(max_size:Int, Root:ArrayList<String>){
        progressBar.max = max_size
        progressBar.progress = 0
        progressBar_text.text = "0 / " + (max_size).toString()
        for (i in 0..max_size-1){
            progressBar.progress = i+1
            progressBar_text.text = (i+1).toString() + " / " + (max_size).toString()

            if(Root[i] != null){
                val absolutePath = Root[i]

                var exif: ExifInterface? = null
                lateinit var tempNote: Note

                val current_image_time_array = absolutePath?.split("camera") // Camera 폴더 안에 있는 이미지의 last_image_name 추출.
                if(current_image_time_array?.size!! > 1){
                    var last_image_time = last_image_name_SharedPreferences?.getString("last_image_name", "") // 전역변수 마지막이미지 이름(숫자만 있으니 마지막 이미지 시간과 동일)
                    val current_image_time = current_image_time_array[1].replace(("[^0-9]").toRegex(), "").substring(0, 14)
                    // 현재 이름을 숫자로 변환(ex. 20210603_132728 -> 20210603132728)
                    // .substring(0, 14) 을 통해 뒤에 쓸대없는 값 제거.
                    if (last_image_time != null) {
                        if(last_image_time == "")
                            last_image_time = "0"
                        if(last_image_time!!.toDouble() < current_image_time.toDouble()){
                            last_image_name_SharedPreferences?.edit {
                                putString("last_image_name", current_image_time) // 전역변수에 마지막이미지 이름을 집어넣음.
                            }
                        }
                    }
                }

                try {
                    Log.d("ExifPath", absolutePath.toString())
                    exif = ExifInterface(File(absolutePath).toString())
                }catch (e: IOException){
                    e.printStackTrace()
                }

                var temp = exif?.getAttribute(ExifInterface.TAG_DATETIME)
                Log.d("exif_r", temp.toString())

                // ------------------ 지도 코드 ---------------------

                var LATITUDE = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                var LONGITUDE = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)

                var lat_result = 0.0
                var lng_result = 0.0

                if(LATITUDE != null && LONGITUDE != null) {
                    var latArray = LATITUDE.split(",", "/")
                    var lngArray = LONGITUDE.split(",", "/")
                    lat_result = latArray[0].toDouble()/latArray[1].toDouble() + latArray[2].toDouble()/latArray[3].toDouble() / 60 + latArray[4].toDouble()/latArray[5].toDouble() / 3600
                    lng_result = lngArray[0].toDouble()/lngArray[1].toDouble() + lngArray[2].toDouble()/lngArray[3].toDouble() / 60 + lngArray[4].toDouble()/lngArray[5].toDouble() / 3600
                }

                if (temp == null || LATITUDE == null || LONGITUDE == null){
                    tempNote = Note(null, false, "",  SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(System.currentTimeMillis()).toInt(), System.currentTimeMillis(), absolutePath.toString(), 0.0, 0.0)
                } else {
                    var date: Date = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault()).parse(temp)
                    tempNote = Note(null, false, "",  SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date).toInt(), date.time, absolutePath.toString(), lat_result, lng_result)
                }

                Thread(Runnable {
                    var tid = TimelineFragment.db.noteDao().insertNote(tempNote).toInt()
                    tempNote = Note(tid, tempNote.image_b, tempNote.content, tempNote.ymd, tempNote.time, tempNote.image, tempNote.LATITUDE, tempNote.LONGITUDE)
                    noteList.add(tempNote)
                    noteCount++
                }).start()

                val adapter = NoteAdapter(noteList, TimelineFragment.db, TimelineFragment())
                TimelineFragment.recyclerView.adapter = adapter
            }
        }
        progressBar_text.text = "불러오기 완료"
        MainActivity.lock = false
    }
}