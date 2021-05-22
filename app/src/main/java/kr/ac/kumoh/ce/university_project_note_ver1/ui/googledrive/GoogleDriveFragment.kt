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
import androidx.appcompat.app.AppCompatActivity
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
import kr.ac.kumoh.ce.university_project_note_ver1.R
import java.util.ArrayList

class GoogleDriveFragment : Fragment() {
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var mOpenFileId: String? = null
    private var mFileTitleEditText: EditText? = null
    private var mDocContentEditText: EditText? = null
    lateinit var root:View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_google_drive, container, false)

        //파일 열기 / 생성 / 수정시 업데이트 할 EditText 상자를 저장합니다.
        mFileTitleEditText = root.findViewById(R.id.file_title_edittext)
        mDocContentEditText = root.findViewById(R.id.doc_content_edittext)

        // Set the onClick listeners for the button bar.
        root.findViewById<View>(R.id.create_btn).setOnClickListener { view: View? -> createFile() }
        root.findViewById<View>(R.id.save_btn).setOnClickListener { view: View? -> saveFile() }
        root.findViewById<View>(R.id.query_btn).setOnClickListener { view: View? -> query() }

        // 사용자를 인증합니다. 대부분의 앱의 경우 사용자가
        // onCreate가 아닌 드라이브 액세스가 필요한 작업입니다.
        requestSignIn()

        return root
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            GoogleDriveFragment.Companion.REQUEST_CODE_SIGN_IN -> if (resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                handleSignInResult(resultData)
            }
            GoogleDriveFragment.Companion.REQUEST_CODE_OPEN_DOCUMENT -> if (resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                val uri = resultData.data
                uri?.let { openFileFromFilePicker(it) }
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    /**
     * [.REQUEST_CODE_SIGN_IN]을 사용하여 로그인 활동을 시작합니다.
     */
    private fun requestSignIn() {
        Log.d(GoogleDriveFragment.Companion.TAG, "Requesting sign-in")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(root.context, signInOptions)

        // 로그인 인 텐트의 결과는 onActivityResult에서 처리됩니다.
        startActivityForResult(client.signInIntent, GoogleDriveFragment.Companion.REQUEST_CODE_SIGN_IN)
    }

    /**
     * [][.requestSignIn]에서 시작된 완료된 로그인 활동의 '결과'를 처리합니다.
     */
    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                Log.d(GoogleDriveFragment.Companion.TAG, "Signed in as " + googleAccount.email)

                // Use the authenticated account to sign in to the Drive service.
                val credential = GoogleAccountCredential.usingOAuth2(
                    root.context, setOf(DriveScopes.DRIVE_FILE))
                credential.selectedAccount = googleAccount.account
                val googleDriveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential)
                    .setApplicationName("Drive API Migration")
                    .build()

                // DriveServiceHelper는 모든 REST API 및 SAF 기능을 캡슐화합니다.
                // onClick 작업을 처리하기 전에 인스턴스화가 필요합니다.
                mDriveServiceHelper =
                    DriveServiceHelper(
                        googleDriveService
                    )
            }
            .addOnFailureListener { exception: Exception? -> Log.e(GoogleDriveFragment.Companion.TAG, "Unable to sign in.", exception) }
    }

    /**
     * Storage Access Framework 파일 선택기에서 반환 된 'uri'에서 파일을 엽니다.
     * [.openFilePicker]에 의해 시작되었습니다.
     */
    private fun openFileFromFilePicker(uri: Uri) {
        if (mDriveServiceHelper != null) {
            Log.d(GoogleDriveFragment.Companion.TAG, "Opening " + uri.path)
            mDriveServiceHelper!!.openFileUsingStorageAccessFramework(root.context.contentResolver, uri)
                .addOnSuccessListener { nameAndContent: Pair<String, String> ->
                    val name = nameAndContent.first
                    val content = nameAndContent.second
                    mFileTitleEditText!!.setText(name)
                    mDocContentEditText!!.setText(content)

                    // SAF를 통해 열린 파일은 수정할 수 없습니다.
                    setReadOnlyMode()
                }
                .addOnFailureListener { exception: Exception? -> Log.e(GoogleDriveFragment.Companion.TAG, "Unable to open file from picker.", exception) }
        }
    }

    /**
     * Drive REST API를 통해 새 파일을 만듭니다.
     */
    private fun createFile() {
        if (mDriveServiceHelper != null) {
            Log.d(GoogleDriveFragment.Companion.TAG, "Creating a file.")
            mDriveServiceHelper!!.createFile()
                .addOnSuccessListener { fileId: String -> readFile(fileId) }
                .addOnFailureListener { exception: Exception? -> Log.e(GoogleDriveFragment.Companion.TAG, "Couldn't create file.", exception) }
        }
    }

    /**
     * 'fileId'로 식별되는 파일의 제목과 콘텐츠를 검색하고 UI를 채웁니다.
     */
    private fun readFile(fileId: String) {
        if (mDriveServiceHelper != null) {
            Log.d(GoogleDriveFragment.Companion.TAG, "Reading file $fileId")
            mDriveServiceHelper!!.readFile(fileId)
                .addOnSuccessListener { nameAndContent: Pair<String, String> ->
                    val name = nameAndContent.first
                    val content = nameAndContent.second
                    mFileTitleEditText!!.setText(name)
                    mDocContentEditText!!.setText(content)
                    setReadWriteMode(fileId)
                }
                .addOnFailureListener { exception: Exception? -> Log.e(GoogleDriveFragment.Companion.TAG, "Couldn't read file.", exception) }
        }
    }

    /**
     * [.createFile]을 통해 생성 된 현재 열려있는 파일이있는 경우 저장합니다.
     */
    private fun saveFile() {
        if (mDriveServiceHelper != null && mOpenFileId != null) {
            Log.d(GoogleDriveFragment.Companion.TAG, "Saving $mOpenFileId")
            val fileName = mFileTitleEditText!!.text.toString()
            val fileContent = mDocContentEditText!!.text.toString()
            mDriveServiceHelper!!.saveFile(mOpenFileId, fileName, fileContent)
                .addOnFailureListener { exception: Exception? -> Log.e(GoogleDriveFragment.Companion.TAG, "Unable to save file via REST.", exception) }
        }
    }

    /**
     * 이 앱에 표시되는 파일에 대해 Drive REST API를 쿼리하고 콘텐츠보기에 나열합니다.
     */
    private fun query() {
        if (mDriveServiceHelper != null) {
            Log.d(GoogleDriveFragment.Companion.TAG, "Querying for files.")
            mDriveServiceHelper!!.queryFiles()
                .addOnSuccessListener { fileList: FileList ->
                    val builder = StringBuilder()
                    for (file in fileList.files) {
                        builder.append(file.name).append("\n")
                    }
                    val fileNames = builder.toString()
                    mFileTitleEditText!!.setText("File List")
                    mDocContentEditText!!.setText(fileNames)
                    setReadOnlyMode()
                }
                .addOnFailureListener { exception: Exception? -> Log.e(GoogleDriveFragment.Companion.TAG, "Unable to query files.", exception) }
        }
    }

    /**
     * UI를 읽기 전용 모드로 업데이트합니다.
     */
    private fun setReadOnlyMode() {
        mFileTitleEditText!!.isEnabled = false
        mDocContentEditText!!.isEnabled = false
        mOpenFileId = null
    }

    /**
     * 'fileId'로 식별 된 문서에서 UI를 읽기 / 쓰기 모드로 업데이트합니다.
     */
    private fun setReadWriteMode(fileId: String) {
        mFileTitleEditText!!.isEnabled = true
        mDocContentEditText!!.isEnabled = true
        mOpenFileId = fileId
    }

    companion object {
        private const val TAG = "GoogleDriveFragment"
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
    }

}