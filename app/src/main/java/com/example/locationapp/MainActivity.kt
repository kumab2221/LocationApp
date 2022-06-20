package com.example.locationapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    // 0以外の任意の値
    private val PERMISSION_REQUEST_CODE = 1192

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var countText : TextView? = null
    private var latText : TextView? = null
    private var lonText : TextView? = null
    private var addressText : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        countText = findViewById(R.id.count_text)
        latText = findViewById(R.id.lat_text)
        lonText = findViewById(R.id.lon_text)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var updatedCount = 0
        var lat = 0.0
        var lon = 0.0
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations){
                    updatedCount++
                    lat = location.latitude
                    lon = location.longitude
                }
                countText?.text = "count : " + updatedCount.toString()
                latText?.text = "count : " + lat.toString()
                lonText?.text = "count : " + lon.toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "位置情報の権限がありません0", Toast.LENGTH_SHORT).show()
                }
                if ((grantResults.isNotEmpty() && grantResults[1] != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "位置情報の権限がありません1", Toast.LENGTH_SHORT).show()
                }
                if ((grantResults.isNotEmpty() && grantResults[2] != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "常に許可の権限がありません", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return
    }

    private fun checkPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun startLocationUpdates() {
        if (!checkPermission()) {
            requestPermission()
            return
        }
        val locationRequest = createLocationRequest() ?: return
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}