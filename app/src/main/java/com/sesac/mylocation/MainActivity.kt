package com.sesac.mylocation

import android.content.Context
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.sesac.mylocation.databinding.ActivityMainBinding
import com.sesac.mylocation.ui.TMapFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (isNetworkAvailable()) { //현재 단말기의 네트워크 가능여부를 알아내고 시작한다
            availableLocationSetting()
        } else {
            Toast.makeText(
                applicationContext,
                "네트웍이 연결되지 않아 종료합니다", Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        settingToolbar()
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork ?: return false
        val networkCapabilities = cm.getNetworkCapabilities(nw) ?: return false
        return when {
            //현재 단말기의 연결유무(Wifi, Data 통신)
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            //단말기가 아닐 경우(ex:: IoT 장비등)
            //networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //블루투스 인터넷 연결유무
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    private fun availableLocationSetting() {
        /**
         * 위치 정보를 얻기 위한 옵션 값들 설정
         */
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000L).apply {
                setMinUpdateDistanceMeters(30f)
                setGranularity(Granularity.GRANULARITY_FINE) //정확도가 높은 위치 제공자 사용
                setWaitForAccurateLocation(true) //잠시 늦더라도 정확한 위치를 표시
            }.build()

        val lbsSettingsRequest: LocationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()

        val settingClient: SettingsClient = LocationServices.getSettingsClient(this)

        val taskLBSSettingResponse: Task<LocationSettingsResponse> =
            settingClient.checkLocationSettings(lbsSettingsRequest)
        //위치 설정 On
        taskLBSSettingResponse.addOnSuccessListener { taskResponse ->
            if (taskResponse.locationSettingsStates?.isLocationUsable == true) {
                Log.d("MainActivity", "위치 설정 On")
                createMapFragment()
            }
        }
        //위치 설정이 off
        taskLBSSettingResponse.addOnFailureListener { exception ->
            Log.d("MainActivity", "위치 설정 Off")
            Toast.makeText(this, "위치 설정 하지 않음!", Toast.LENGTH_SHORT)
                .show()
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationCallback.launch(intentSenderRequest)
                } catch (sie: IntentSender.SendIntentException) {
                    Toast.makeText(applicationContext, sie.message, Toast.LENGTH_SHORT ).show()
                }
            }
        }
    }
    private val locationCallback = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == RESULT_OK) {
            createMapFragment()
        }
    }

    private fun createMapFragment(){
        with(supportFragmentManager.beginTransaction()){
            replace(R.id.fragmentContainer, TMapFragment())
            commit()
        }
    }

    private fun settingToolbar(){
        with(binding.toolbar){
            title = "화장실이 어디에요"
        }
    }





}