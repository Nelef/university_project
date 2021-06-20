package kr.ac.kumoh.ce.university_project_note_ver1.ui.option

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.ebner.roomdatabasebackup.core.RoomBackup
import kr.ac.kumoh.ce.university_project_note_ver1.MainActivity
import kr.ac.kumoh.ce.university_project_note_ver1.R
import kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline.TimelineFragment


class OptionFragment : Fragment() {
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

    val SECRET_PASSWORD = "verySecretEncryptionKey" // 암호화 키 설정

    var passwordSharedPreferences: SharedPreferences? = this.context?.getSharedPreferences("password", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "GoogleDriveFragment"
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_option, container, false)

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
                .enableLogDebug(false)
                .backupIsEncrypted(true)
                .customEncryptPassword(SECRET_PASSWORD)
                .useExternalStorage(true)
                //maxFileCount: else 1000 because i cannot surround it with if condition
                .maxFileCount(5)
                .apply {
                    onCompleteListener { success, message ->
                        Log.d(OptionFragment.TAG, "success: $success, message: $message")
                        Toast.makeText(context, "success: $success, message: $message", Toast.LENGTH_LONG).show()
                        if (success) restartApp(Intent(context, MainActivity::class.java))
                    }
                }
                .backup()
        }
        button_restore.setOnClickListener {
            RoomBackup()
                .context(root.context)
                .database(TimelineFragment.db)
                .enableLogDebug(false)
                .backupIsEncrypted(true)
                .customEncryptPassword(SECRET_PASSWORD)
                .useExternalStorage(true)
                .apply {
                    onCompleteListener { success, message ->
                        Log.d(OptionFragment.TAG, "success: $success, message: $message")
                        Toast.makeText(context, "success: $success, message: $message", Toast.LENGTH_LONG).show()
                        if (success) restartApp(Intent(context, MainActivity::class.java))
                    }

                }
                .restore()
        }
        button_drive_backup.setOnClickListener {
            Toast.makeText(root.context, "테스트", Toast.LENGTH_LONG).show()
        }
        return root
    }
}