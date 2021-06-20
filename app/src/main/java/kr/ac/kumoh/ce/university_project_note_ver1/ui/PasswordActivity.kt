package kr.ac.kumoh.ce.university_project_note_ver1.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kr.ac.kumoh.ce.university_project_note_ver1.R

class PasswordActivity : AppCompatActivity() {
    var lock  = true
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

        loginButton.setOnClickListener {
            if (currentPassword == passwordInputEditText.text.toString()) {
                lock = false
                intent.putExtra("lock", lock)
                setResult(RESULT_OK, intent)
                finish()
            }
            else {
                lock = true
                intent.putExtra("lock", lock)
                setResult(RESULT_OK, intent)
                Toast.makeText(this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (lock){
            moveTaskToBack(true);						// 태스크를 백그라운드로 이동
            finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid());	// 앱 프로세스 종료
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}