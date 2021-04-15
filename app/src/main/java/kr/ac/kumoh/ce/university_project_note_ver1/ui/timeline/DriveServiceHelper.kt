package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Pair
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * REST API를 통해 드라이브 파일에 대한 읽기 / 쓰기 작업을 수행하고
 * Storage Access Framework를 통한 파일 선택기 UI.
 */
class DriveServiceHelper(private val mDriveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    /**
     * 사용자의 내 드라이브 폴더에 텍스트 파일을 생성하고 파일 ID를 반환합니다.
     */
    fun createFile(): Task<String> {
        return Tasks.call(mExecutor, {
            val metadata = File()
                    .setParents(listOf("root"))
                    .setMimeType("text/plain")
                    .setName("Untitled file")
            val googleFile = mDriveService.files().create(metadata).execute()
                    ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        })
    }

    /**
     *`fileId`로 식별 된 파일을 열고 이름과 내용의 [Pair]를 반환합니다.
     */
    fun readFile(fileId: String?): Task<Pair<String, String>> {
        return Tasks.call(mExecutor, {

            // Retrieve the metadata as a File object.
            val metadata = mDriveService.files()[fileId].execute()
            val name = metadata.name
            mDriveService.files()[fileId].executeMediaAsInputStream().use { `is` ->
                BufferedReader(InputStreamReader(`is`)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    val contents = stringBuilder.toString()
                    return@call Pair.create(name, contents)
                }
            }
        })
    }

    /**
     * 주어진`name` 및`content`로`fileId`로 식별되는 파일을 업데이트합니다.
     */
    fun saveFile(fileId: String?, name: String?, content: String?): Task<Void?> {
        return Tasks.call(mExecutor, {

            // Create a File containing any metadata changes.
            val metadata = File().setName(name)

            // Convert content to an AbstractInputStreamContent instance.
            val contentStream = ByteArrayContent.fromString("text/plain", content)

            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, contentStream).execute()
            null
        })
    }

    /**
     * 사용자의 내 드라이브에 표시되는 모든 파일이 포함 된 [FileList]를 반환합니다.
     *
     *
     * 반환 된 목록에는 이 앱에서 볼 수있는 파일만 포함됩니다.
     *이 앱에서 생성했습니다. 앱에서 생성되지 않은 파일에 대한 작업을 수행하려면 프로젝트가
     * [Google 개발자 콘솔] (https://play.google.com/apps/publish) 및 확인을 위해 Google에 제출합니다.
     */
    fun queryFiles(): Task<FileList> {
        return Tasks.call(mExecutor, { mDriveService.files().list().setSpaces("drive").execute() })
    }

    /**
     * Storage Access Framework 파일 선택기를 열기위한 [Intent]를 반환합니다.
     */
    fun createFilePickerIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        return intent
    }

    /**
     * 스토리지 액세스 프레임 워크에서 반환 한`uri`에서 파일을 엽니다 [Intent]
     * 주어진`contentResolver`를 사용하여 [.createFilePickerIntent]에 의해 생성되었습니다.
     */
    fun openFileUsingStorageAccessFramework(
            contentResolver: ContentResolver, uri: Uri?): Task<Pair<String, String>> {
        return Tasks.call(mExecutor, {

            // Retrieve the document's display name from its metadata.
            var name: String
            contentResolver.query(uri!!, null, null, null, null).use { cursor ->
                name = if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.getString(nameIndex)
                } else {
                    throw IOException("Empty cursor returned for file.")
                }
            }

            // Read the document's contents as a String.
            var content: String
            contentResolver.openInputStream(uri).use { `is` ->
                BufferedReader(InputStreamReader(`is`)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    content = stringBuilder.toString()
                }
            }
            Pair.create(name, content)
        })
    }
}