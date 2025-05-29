package com.example.matala2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.matala2.model.ScoreRecord
import com.google.android.material.textview.MaterialTextView
import com.example.matala2.utilities.Constants
import com.example.matala2.utilities.SharedPreferencesManagerV3
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.util.Log



class ScoreActivity : AppCompatActivity() {

    private lateinit var score_LBL_status:MaterialTextView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0
    private lateinit var locationCallback: com.google.android.gms.location.LocationCallback
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var saveButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        SharedPreferencesManagerV3.putHighScores(emptyList())

        findViews()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        checkAndRequestLocationPermission(saveButton)

//        checkAndRequestLocationPermission()

    }
    private fun findViews() {
        score_LBL_status = findViewById(R.id.score_LBL_status)
    }
    private fun initViews() {
        val bundle: Bundle? = intent.extras

        val message = bundle?.getString(Constants.BundleKeys.MESSAGE_KEY, "🤷‍♂️ Unknown Status")
        val scoreStr = bundle?.getString(Constants.BundleKeys.SCORE_KEY, "0")
        val scoreInt = scoreStr?.toIntOrNull() ?: 0

        score_LBL_status.text = buildString {
            append(message)
            append("\n")
            append(scoreStr)
        }

        val section: View = findViewById(R.id.score_LAYOUT_enter_name_section)
        val nameInput: EditText = findViewById(R.id.score_EDT_name)
        saveButton = findViewById(R.id.score_BTN_save)

        if (SharedPreferencesManagerV3.isEligibleForTop10(scoreInt)) {
            section.visibility = View.VISIBLE
            saveButton.isEnabled = false // ✅ שורה שנוספה — הכפתור מושבת בהתחלה

            Log.d("debug", "📍 Trying to get last known location...")



            val backToMenuButton: Button = findViewById(R.id.score_BTN_back_to_menu)
            backToMenuButton.setOnClickListener {
                finish() // חוזר למסך הקודם (התפריט הראשי)
            }

            saveButton.setOnClickListener {
                val name = nameInput.text.toString().trim()
                if (name.length >= 2) {
                    val lat = currentLat
                    val lon = currentLon
                    val timestamp = System.currentTimeMillis()

                    val newRecord = ScoreRecord(name, scoreInt, lat, lon, timestamp)
                    SharedPreferencesManagerV3.addHighScoreIfTop10(newRecord)

                    finish()
                }
            }
        }
    }

    private fun checkAndRequestLocationPermission(saveButton: Button) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            requestCurrentLocation()
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {
        val hasFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            Log.d("debug", "❌ Permissions not granted - cannot request location updates")
            return
        }

        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
            numUpdates = 1
        }

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    currentLat = location.latitude
                    currentLon = location.longitude
                    Log.d("debug", "📍 Got LIVE location: $currentLat , $currentLon")
                    saveButton.isEnabled = true
                } else {
                    Log.d("debug", "⚠️ Live location is still null")
                }

                // אפשר להפסיק מאזינים מיידית
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()

            }
        }
    }

    private fun getLastLocation() {
        val hasFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        Log.d("debug", "🔒 Permissions - Fine: $hasFine, Coarse: $hasCoarse")

        if (!hasFine && !hasCoarse) {
            Log.d("debug", "❌ Permissions not granted - cannot get location")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Log.d("debug", "⚠️ Location is null - perhaps emulator didn't receive GPS yet")
            } else {
                currentLat = location.latitude
                currentLon = location.longitude
                Log.d("debug", "✅ Got location: $currentLat , $currentLon")
            }
        }.addOnFailureListener { e ->
            Log.d("debug", "❌ Failed to get location: ${e.message}")
        }
    }

}