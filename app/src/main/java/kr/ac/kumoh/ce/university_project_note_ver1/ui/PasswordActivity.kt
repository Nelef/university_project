package kr.ac.kumoh.ce.university_project_note_ver1.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kr.ac.kumoh.ce.university_project_note_ver1.MainActivity
import kr.ac.kumoh.ce.university_project_note_ver1.R

class PasswordActivity : AppCompatActivity() {

    val passwordInputEditText: EditText by lazy{
        findViewById(R.id.passwordInputEditText)
    }
    val loginButton: Button by lazy {
        findViewById<Button>(R.id.loginButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        val currentPassword = intent.getStringExtra("password")
        Log.d("cP", currentPassword.toString())

        loginButton.setOnClickListener {
            if (currentPassword == passwordInputEditText.text.toString()) {
                intent.putExtra("lock", false)
                setResult(RESULT_OK, intent)
                finish()
            }
            else {
                Toast.makeText(this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
                intent.putExtra("lock", true)
                setResult(RESULT_OK, intent)
            }
        }
    }
}