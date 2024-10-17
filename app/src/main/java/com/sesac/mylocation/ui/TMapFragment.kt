package com.sesac.mylocation.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.R
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sesac.mylocation.BuildConfig
import com.sesac.mylocation.databinding.FragmentTMapBinding
import com.sesac.mylocation.ui.vm.TMapViewModel
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import kotlinx.coroutines.launch


class TMapFragment : BaseFragment<FragmentTMapBinding>(FragmentTMapBinding::inflate) {

    private lateinit var tMapView: TMapView

    private val viewModel:TMapViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 티맵뷰 초기화
        initTMap()
    }

    private fun initTMap(){
        binding.layoutPermission.visibility = View.GONE
        tMapView = TMapView(requireContext())
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_KEY)
        binding.tMapContainerView.addView(tMapView)
        tMapView.setOnMapReadyListener {
            Log.d("initTMap", "로딩 완료")
            // 현재 위치 찾기
            findMyLocation()
        }
    }

    /**
     * Fused Location Provider Api 에서
     * 위치 업데이트를위한 서비스 품질등 다양한요청을
     * 설정하는데 사용하는 객체.
     */
    private lateinit var mLocationRequest: LocationRequest

    /**
     * 현재 위치정보를 나타내는 객체
     */
    private lateinit var mCurrentLocation: Location

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private fun findMyLocation(){
        /**
         * FusedLocationProviderApi 에서
         * 위치 업데이트를위한 서비스 품질등 다양한 요청을
         * 설정하는데 사용하는 데이터객체인
         * LocationRequest 를 획득
         */
        mLocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000L
        ).run {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(3000L)
            setIntervalMillis(3000L) //위치가 update 되는 주기
            setMaxUpdateDelayMillis(3000L)
                .build()
        }
        /**
         * FusedLocationProviderClient 객체를 획득
         */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 위치 권한 체크
        multiplePermissionsLauncher.launch(permissions)
    }

    private fun currentLocationListenerStart(){
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("currentLocationListener","권한 요청 필요")
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )

        // 화장실 위치 마커로 표시
        setMarker()
    }

    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var result = true
        permissions.entries.forEach { (_, isGranted) ->
            when {
                isGranted -> {
                    // 개별 권한이 승인된 경우 처리할 작업
                }
                !isGranted -> {
                    // 권한이 거부된 경우 처리할 작업
                    result = false
                }
                else -> {
                    // 사용자가 "다시 묻지 않음"을 선택한 경우 처리할 작업
                    result = false
                }
            }
        }
        // multiple permission 처리에 대한 선택적 작업
        // - 모두 허용되었을 경우에 대한 code
        // - 허용되지 않은 Permission에 대한 재요청 code
        if(result){
            // 맵 셋팅
            if(binding.layoutPermission.visibility == View.VISIBLE){
                binding.layoutPermission.visibility = View.GONE
                binding.tMapContainerView.addView(tMapView)
            }
            currentLocationListenerStart()
        }else{
            // 권한 허용이 되어있지 않았다는 것을 표시하기
            Toast.makeText(requireActivity(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            showUiPermissionNotGranted()
        }
    }

    private val permissions = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )

    /**
     * 위치 이벤트에 대한 콜백을 제공.
     * 단말기위치정보가 update 되면 자동으로 호출
     * Fused Location Provider API 에 등록된
     * 위치알림을 수신하는 데 사용
     */
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        /**
         *  성공적으로 위치정보와 넘어왔을때를 동작하는 Call back 함수
         */
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            mCurrentLocation = locationResult.locations[0]

            // 카메라를 현재 위치로 이동
            tMapView.setCenterPoint(mCurrentLocation.latitude, mCurrentLocation.longitude, true)
            // 현재 위치를 마커로 표시
            tMapView.removeTMapMarkerItem("myLocation")
            val marker = TMapMarkerItem()
            marker.id = "myLocation"
            marker.setTMapPoint(mCurrentLocation.latitude, mCurrentLocation.longitude)
            marker.icon = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_mylocation)
            tMapView.addTMapMarkerItem(marker)
            // 반경 500m 이내 화장실 불러오기
            viewModel.getToiletList(mCurrentLocation)
        }

        /**
         * 현재 콜백이 동작가능한지에 대한 여부
         */
        override fun onLocationAvailability(availability: LocationAvailability) {
            val message = if (availability.isLocationAvailable) {
                "위치 정보 획득이 가능합니다"
            } else {
                "현재 위치 정보를 가져올 수 없네요! 잠시 후 다시 시도하세요"
            }
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMarker(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toiletList.collect { list ->
                list.forEach { toilet ->
                    val marker = TMapMarkerItem()
                    marker.id = toilet.objectID
                    marker.setTMapPoint(toilet.lat, toilet.lng)
                    tMapView.addTMapMarkerItem(marker)
                }
            }
        }
    }

    private fun showUiPermissionNotGranted(){
        binding.tMapContainerView.removeView(tMapView)
        binding.layoutPermission.visibility = View.VISIBLE
        binding.buttonPermission.setOnClickListener {
            multiplePermissionsLauncher.launch(permissions)
        }
    }

}