package com.example.matala2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.matala2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var lat: Double = 0.0
    private var lon: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lat = arguments?.getDouble("lat") ?: 0.0
        lon = arguments?.getDouble("lng") ?: 0.0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(lat, lon)
        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("שיא שנשמר כאן")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    companion object {
        fun newInstance(lat: Double, lng: Double): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lng", lng)
            fragment.arguments = args
            return fragment
        }
    }
}
