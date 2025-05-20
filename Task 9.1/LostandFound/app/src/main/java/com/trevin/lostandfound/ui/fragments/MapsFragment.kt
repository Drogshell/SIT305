package com.trevin.lostandfound.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.trevin.lostandfound.R
import com.trevin.lostandfound.data.model.AdvertItemViewModel
import com.trevin.lostandfound.util.MapHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private val viewModel: AdvertItemViewModel by activityViewModels()
    private var googleMap: GoogleMap? = null
    private var mapFrag: SupportMapFragment? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync { map ->
            googleMap = map
            MapHelper.setUpMapUI(map)
            setUpMarkers()
        }

        val mapTouchLayer = view.findViewById<View>(R.id.map_touch_layer)
        mapTouchLayer.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            false
        }
    }

    private fun setUpMarkers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAdverts().collectLatest { adverts ->
                googleMap?.let { map ->
                    map.clear()
                    var hasMarkers = false
                    val boundsBuilder = LatLngBounds.Builder()

                    for (ad in adverts) {
                        val lat = ad.latitude
                        val long = ad.longitude

                        if (lat != null && long != null && lat != -1.0 && long != -1.0) {
                            MapHelper.addMarker(map, lat, long, ad.title, ad.description)
                            boundsBuilder.include(LatLng(lat, long))
                            hasMarkers = true
                        }
                    }

                    if (hasMarkers) {
                        MapHelper.animateCamBounds(map, boundsBuilder)
                    } else {
                        MapHelper.moveCamToLocation(map, -37.8136, 144.9631, 10f)
                    }
                }
            }
        }
    }
}