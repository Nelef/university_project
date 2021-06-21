package kr.ac.kumoh.ce.university_project_note_ver1.ui.option

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import com.ammarptn.gdriverest.DriveServiceHelper
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import kr.ac.kumoh.ce.university_project_note_ver1.MainActivity
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.TimelineFragment
import org.json.JSONObject
import java.io.File


class OptionFragment : Fragment() {
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

    lateinit var backup_id: EditText
    lateinit var backup_help_id: TextView
    lateinit var email: TextView

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mDriveServiceHelper: DriveServiceHelper? = null

    val SECRET_PASSWORD = "verySecretEncryptionKey" // 암호화 키 설정

    var passwordSharedPreferences: SharedPreferences? = this.context?.getSharedPreferences("password", Context.MODE_PRIVATE)

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

        button_backup.setOnClickListener {
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

        button_restore.setOnClickListener {
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

        button_drive_backup.setOnClickListener {
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

        button_drive_restore.setOnClickListener {
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

        auto_image_read.setOnClickListener {
            val tempRoot = ArrayList<String>()

            File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).absolutePath+"/camera").walk().forEach {
                Log.d("테스트 사진 불러오기", it.canonicalPath)
                tempRoot.add(it.absolutePath)
            }

            val builder = AlertDialog.Builder(root.context)
            builder.setTitle("최신 사진을 불러오시겠습니까?")
            builder.setMessage("${tempRoot.size}개의 사진이 있습니다.")
            builder.setPositiveButton(
                    "확인",
                    { dialoginterface:DialogInterface?, i:Int ->
                        Log.d(OptionFragment.TAG, "테스트 확인버튼 누름.")
                    })
            builder.setNegativeButton(
                    "취소",
                    { dialoginterface:DialogInterface?, i:Int ->
                        Log.d(OptionFragment.TAG, "테스트 취소버튼 누름.")
                    })
            builder.show()


//            val splitArray = tempRoot[1].split("files")
//            Log.d("테스트5", splitArray[1])

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
}