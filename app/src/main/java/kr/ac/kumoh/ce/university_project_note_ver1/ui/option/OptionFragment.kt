package kr.ac.kumoh.ce.university_project_note_ver1.ui.option

import android.content.Context
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
import kr.ac.kumoh.ce.university_project_note_ver1.R
import java.text.SimpleDateFormat
import java.util.*

class OptionFragment : Fragment() {
    lateinit var optionPassword: TextView
    lateinit var changePasswordLayout: ConstraintLayout
    lateinit var changePasswordButton: Button
    lateinit var currentPassword: EditText
    lateinit var newPassword: EditText
    lateinit var confirmPassword: EditText

    var passwordSharedPreferences: SharedPreferences? = this.context?.getSharedPreferences("password", Context.MODE_PRIVATE)

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


        optionPassword.setOnClickListener {
            if (changePasswordLayout.visibility == View.GONE)
                changePasswordLayout.visibility = View.VISIBLE
            else
                changePasswordLayout.visibility = View.GONE
        }

        changePasswordButton.setOnClickListener {
            val cP = passwordSharedPreferences?.getString("password", "123").toString()
            if (currentPassword.text.toString() == cP && newPassword.text.toString() == confirmPassword.text.toString()){
                passwordSharedPreferences?.edit {
                    putString("password", newPassword.text.toString())
                }
                Toast.makeText(this.context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this.context, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }
}