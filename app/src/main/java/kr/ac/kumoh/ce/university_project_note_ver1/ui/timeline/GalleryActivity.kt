package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.ucrop.UCrop
import com.bumptech.glide.Glide
import kotlinx.coroutines.withContext
import kr.ac.kumoh.ce.university_project_note_ver1.R
import java.io.ByteArrayOutputStream
import java.io.File


class GalleryActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var loadImageButton: Button
    private lateinit var indicateButton: Button
    private lateinit var sendButton: Button
    private lateinit var imageUri: Uri



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        initView()
        initListener()
    }

    private fun initListener() {
        loadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            startActivityForResult(
                intent,
                PICK_IMAGE
            )
        }
        sendButton.setOnClickListener{
            //val intent = Intent(getApplicationContext(), TimelineFragment::class.java)
            //val sendBitmap = BitmapFactory.decodeResource(getResources(), R.id.gallery_iv_image)
            //val stream = ByteArrayOutputStream()
                    //sendBitmap.compress(CompressFormat.JPEG, 100, stream)
            //val byteArray = stream.toByteArray()
            //intent.putExtra("image", byteArray)
            //startActivity(intent)
                //val intent =Intent(Intent.ACTION_PICK)
                //intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                //startActivityForResult(intent, 4)
            //val intent = Intent(getApplicationContext(),TimelineFragment::class.java)
            //intent.putExtra("image",imageUri)
            //startActivityForResult(intent,4)
            val intent = Intent(Intent.ACTION_PICK)
            intent.putExtra("image",imageUri.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
        indicateButton.setOnClickListener{
            Glide.with(this)
                .load(imageUri.toString())
                .fitCenter()
                .override(200,200)
                .into(imageView)
        }
    }


    private fun initView() {
        imageView = findViewById(R.id.gallery_iv_image)
        loadImageButton = findViewById(R.id.gallery_btn_load)
        indicateButton = findViewById(R.id.gallery_btn_indicate)
        sendButton = findViewById(R.id.gallery_btn_send)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                val sourceUri = data!!.data
                if (sourceUri != null) {
                    val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))
                    openCropActivity(sourceUri, destinationUri)
                } else {
                    Toast.makeText(this, getString(R.string.get_img_error_msg), Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                val resultUri = UCrop.getOutput(data!!)
                if (resultUri != null) {
                    Log.d("AAA","AAa")
                    //초기화
                    imageView.setImageDrawable(null)
                    //이미지뷰에 세팅
                    imageUri = resultUri
                    imageView.setImageURI(imageUri)
                    //Glide.with(this).load(imageUri).fitCenter().into(imageView)
                } else {
                    Toast.makeText(this, getString(R.string.get_img_error_msg), Toast.LENGTH_SHORT).show()
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, getString(R.string.get_img_error_msg), Toast.LENGTH_SHORT).show()
        }

    }


    private fun openCropActivity(
        sourceUri: Uri,
        destinationUri: Uri
    ) {
        UCrop.of(sourceUri, destinationUri)
            .start(this)
    }

    companion object {
        const val PICK_IMAGE = 1
    }
}
