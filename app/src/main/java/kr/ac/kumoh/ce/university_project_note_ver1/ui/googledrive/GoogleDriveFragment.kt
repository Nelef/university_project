package kr.ac.kumoh.ce.university_project_note_ver1.ui.googledrive

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Pair
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.ammarptn.gdriverest.DriveServiceHelper
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import com.google.gson.Gson
import kr.ac.kumoh.ce.university_project_note_ver1.MainActivity
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.TimelineFragment
import org.json.JSONObject
import java.io.File

class GoogleDriveFragment : Fragment() {
    lateinit var root:View
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mDriveServiceHelper: DriveServiceHelper? = null

    lateinit var email: TextView

    val SECRET_PASSWORD = "verySecretEncryptionKey" // 암호화 키 설정

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_google_drive, container, false)

        val file_id = this.context?.getSharedPreferences("file_id", Context.MODE_PRIVATE)

        root.findViewById<View>(R.id.btn_backup).setOnClickListener {
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
                        Log.d(TAG, "success: $success, message: $message")
                        Toast.makeText(context, "success: $success, message: $message", Toast.LENGTH_LONG).show()
                        if (success) restartApp(Intent(context, MainActivity::class.java))
                    }
                }
                .backup()
        }
        root.findViewById<View>(R.id.btn_restore).setOnClickListener {
            RoomBackup()
                .context(root.context)
                .database(TimelineFragment.db)
                .enableLogDebug(true)
                .backupIsEncrypted(true)
                .customEncryptPassword(SECRET_PASSWORD)
                .useExternalStorage(true)
                .apply {
                    onCompleteListener { success, message ->
                        Log.d(TAG, "success: $success, message: $message")
                        Toast.makeText(context, "success: $success, message: $message", Toast.LENGTH_LONG).show()
                        if (success) restartApp(Intent(context, MainActivity::class.java))
                    }

                }
                .restore()
        }

        root.findViewById<View>(R.id.btn_drive_backup).setOnClickListener {
            if (mDriveServiceHelper == null) {
                return@setOnClickListener
            }

            val tempRoot = ArrayList<String>()

            File(root.context.getExternalFilesDir(null)!!.absolutePath+"/backup").walk().forEach {
                Log.d("테스트4", it.canonicalPath)
                tempRoot.add(it.absolutePath)
            }
            val splitArray = tempRoot[1].split("files")
            Log.d("테스트5", splitArray[1])

            Log.d(TAG, "테스트 uploadFile Log: " + tempRoot[1])

            mDriveServiceHelper!!.uploadFile(File(root.context.getExternalFilesDir(null), splitArray[1]), "text/plain", null)
                    .addOnSuccessListener { googleDriveFileHolder ->
                        val gson = Gson()
                        Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder))
                        val temp = JSONObject(gson.toJson(googleDriveFileHolder))

                        file_id?.edit {
                            putString("file_id", temp.getString("id"))
                        }
                    }
                    .addOnFailureListener { e -> Log.d(TAG, "onFailure: " + e.message) }
        }

        root.findViewById<View>(R.id.btn_drive_restore).setOnClickListener {
            if (mDriveServiceHelper == null) {
                return@setOnClickListener
            }
            mDriveServiceHelper!!.downloadFile(File(root.context.getExternalFilesDir(null), "backup/drive.aes"), file_id?.getString("file_id", "").toString())
                    .addOnSuccessListener { Log.d(TAG, "테스트77onSuccess: ") }
                    .addOnFailureListener { e -> Log.d(TAG, "테스트77onFailure: " + e.message)}
        }
        email = root.findViewById(R.id.email)

        return root
    }


    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account == null) {
            signIn()
        } else {
            email.text = account.email
            mDriveServiceHelper = DriveServiceHelper(DriveServiceHelper.getGoogleDriveService(context, account, "appName"))
        }
    }

    private fun signIn() {
        mGoogleSignInClient = buildGoogleSignInClient()
        startActivityForResult(mGoogleSignInClient!!.signInIntent, REQUEST_CODE_SIGN_IN)
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
            REQUEST_CODE_SIGN_IN -> if (resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                handleSignInResult(resultData)
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleSignInAccount ->
                    Log.d(TAG, "Signed in as " + googleSignInAccount.email)
                    email!!.text = googleSignInAccount.email
                    mDriveServiceHelper = DriveServiceHelper(DriveServiceHelper.getGoogleDriveService(context, googleSignInAccount, "appName"))
                    Log.d(TAG, "handleSignInResult: $mDriveServiceHelper")
                }
                .addOnFailureListener { e -> Log.e(TAG, "Unable to sign in.", e) }
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 100
        private const val TAG = "GoogleDriveFragment"
    }
}