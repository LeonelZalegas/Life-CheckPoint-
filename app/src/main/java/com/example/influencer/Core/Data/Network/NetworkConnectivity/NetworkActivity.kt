package com.example.influencer.Core.Data.Network.NetworkConnectivity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.influencer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
abstract class NetworkActivity : AppCompatActivity() {

    private val connectivityObserver: ConnectivityObserver by lazy { networkConnectivityObserver }

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    private var noInternetDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityObserver.observe().onEach { isOnline ->
            if (!isOnline) {
                showNoInternetDialog()
            } else {
                noInternetDialog?.dismiss()
            }
        }.launchIn(lifecycleScope)
    }

    private fun showNoInternetDialog() {
        if (noInternetDialog?.isShowing != true) {
            noInternetDialog = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Close App") { _, _ ->
                    finishAffinity()
                }
                .setCancelable(false)
                .show()
        }
    }
}