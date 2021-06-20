package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import kr.ac.kumoh.ce.university_project_note_ver1.R

class ImageGPSActivity : AppCompatActivity(), OnMapReadyCallback {
    // ------------------ 지도 코드 ---------------------
    // 코드 전체 복붙하셈
    private lateinit var COORD:LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map_fragment)

        var intent = getIntent()

        var LATITUDE = intent.getDoubleExtra("LATITUDE", 0.0)
        var LONGITUDE = intent.getDoubleExtra("LONGITUDE", 0.0)

        COORD = LatLng(LATITUDE, LONGITUDE)
//        Toast.makeText(this, COORD.toString(), Toast.LENGTH_LONG).show()

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment2) as MapFragment?
            ?: MapFragment.newInstance(
                NaverMapOptions().camera(
                    CameraPosition(
                        NaverMap.DEFAULT_CAMERA_POSITION.target,
                        NaverMap.DEFAULT_CAMERA_POSITION.zoom,
                        0.0,
                        0.0
                    )
                )
            ).also {
                supportFragmentManager.beginTransaction().add(R.id.map_fragment2, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    override fun onMapReady(naverMap: NaverMap) {
        naverMap.moveCamera(CameraUpdate.scrollTo(COORD))
        Marker().apply {
            position = COORD
            icon = MarkerIcons.BLACK
            iconTintColor = Color.RED
            map = naverMap
        }
    }
}