package kr.ac.kumoh.ce.university_project_note_ver1.ui.googledrive

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Pair
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import kr.ac.kumoh.ce.university_project_note_ver1.MainActivity
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.TimelineFragment

class GoogleDriveFragment : Fragment() {
    lateinit var root:View

    val SECRET_PASSWORD = "verySecretEncryptionKey" // 암호화 키 설정

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_google_drive, container, false)

        root.findViewById<View>(R.id.btn_backup).setOnClickListener {
            RoomBackup()
                .context(root.context)
                .database(TimelineFragment.db)
                .enableLogDebug(false)
                .backupIsEncrypted(true)
                .customEncryptPassword(SECRET_PASSWORD)
                .useExternalStorage(true)
                //maxFileCount: else 1000 because i cannot surround it with if condition
                .maxFileCount(5)
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
                .enableLogDebug(false)
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

        return root
    }

    companion object {
        private const val TAG = "GoogleDriveFragment"
    }


}