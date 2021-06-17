package kr.ac.kumoh.ce.university_project_note_ver1.ui.map

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kr.ac.kumoh.ce.university_project_note_ver1.R


class LocationTrackingActivity : AppCompatActivity(), OnMapReadyCallback {
    val lat = 35.799503 //LATITUDE.toDouble()
    val lng = 128.587006 //LONGITUDE.toDouble()

    private val COORD = LatLng(lat, lng)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map_fragment)

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