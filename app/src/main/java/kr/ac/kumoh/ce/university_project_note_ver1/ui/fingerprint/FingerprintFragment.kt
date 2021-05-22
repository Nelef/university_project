package kr.ac.kumoh.ce.university_project_note_ver1.ui.fingerprint

import android.hardware.biometrics.BiometricManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kr.ac.kumoh.ce.university_project_note_ver1.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FingerprintFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FingerprintFragment : Fragment() {
    //생체정보 사용할 수 있는지 확인하는 변수
    private var status: Int? = null
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var fingerprintButton: Button

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        status = androidx.biometric.BiometricManager.from(this.requireContext())
            .canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)

        if (status == BiometricManager.BIOMETRIC_SUCCESS) {
            //사용가능
            biometricPrompt = createBiometricPrompt()
            promptInfo = createPromptInfo()
        } else {
            //사용 불가능
            Log.d("TAG", "사용 불가")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_fingerprint, container, false)
        fingerprintButton = root.findViewById(R.id.fingerprintButton)
        fingerprintButton.setOnClickListener {
            Log.d("TAG", "사용 rs")
            biometricPrompt.authenticate(promptInfo)
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo{
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("생체정보 인식")
            .setSubtitle("서브타이틀")
            .setDescription("설명")
            .setConfirmationRequired(false)
            //.setNegativeButtonText(getString(R.string.fingerprint_error_hw_not_available))
            .build()
        return promptInfo
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this.requireContext())

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d("TAG", "$errorCode :: $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("TAG", "인증 실패")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d("TAG", "인증 성공")
                //TODO 인증 성공 과정
            }
        }

        //The API requires the client/Activity context for displaying the prompt view
        val biometricPrompt = BiometricPrompt(this, executor, callback)
        return biometricPrompt
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FingerprintFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FingerprintFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}