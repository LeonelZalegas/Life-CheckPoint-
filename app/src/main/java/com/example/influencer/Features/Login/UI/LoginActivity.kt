package com.example.influencer.Features.Login.UI


import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatActivity
import com.example.influencer.Features.Login.UI.LoginViewModel
import javax.inject.Inject
import com.example.influencer.Core.Data.Preferences.UserPreferences
import cn.pedant.SweetAlert.SweetAlertDialog
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.KeepOnScreenCondition
import android.content.Intent
import android.graphics.Color
import com.example.influencer.Core.UI.Home
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.influencer.R
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.influencer.Features.Login.Domain.Model.UsuarioLogin
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.influencer.Core.Data.Network.NetworkConnectivity.ConnectivityObserver
import com.example.influencer.Core.Data.Network.NetworkConnectivity.NetworkConnectivityObserver
import com.example.influencer.Core.Utils.Event
import com.example.influencer.Features.SignIn.UI.GoogleSignin.GoogleSigninActivity
import com.example.influencer.Features.SignIn.UI.AppSignIn.SignInActivity
import com.example.influencer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private val connectivityObserver: ConnectivityObserver by lazy { networkConnectivityObserver }

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    private var noInternetDialog: AlertDialog? = null

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        super.onCreate(savedInstanceState)

        connectivityObserver.observe().onEach { isOnline ->
            splashScreen.setKeepOnScreenCondition { false }
            if (!isOnline) {
                showNoInternetDialog()
            } else {
                if (userPreferences.isSignedIn) {
                    navigateToHomeSignedIn()
                }else{
                    setupBinding()
                    setupBackgroundAnimation()
                    initLoading()
                    initUI()
                }
            }
        }.launchIn(lifecycleScope)

    }

    private fun setupBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupBackgroundAnimation() {
        val gradientLogin = findViewById<ConstraintLayout>(R.id.gradiente_login)
        val animationLogin = gradientLogin.background as AnimationDrawable
        animationLogin.apply {
            setEnterFadeDuration(2500)
            setExitFadeDuration(5000)
            start()
        }
    }

    private fun initLoading() {
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).apply {
            progressHelper.barColor = Color.parseColor("#F57E00")
            titleText = getString(R.string.Loading)
            setCancelable(false)
        }
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        with(binding) {
            buttonLogIn.setOnClickListener {
                val email = ponerEmail.text.toString()
                val password = ponerContrasena.text.toString()
                if (loginViewModel.validateLogin(email, password)) {
                    loginViewModel.onLoginSelected(UsuarioLogin(email, password))
                }
            }
            buttomSignInGoogle.setOnClickListener { loginViewModel.onGoogleSignInSelected() }
            signIn.setOnClickListener { loginViewModel.onSignInSelected() }
        }
    }

    private fun initObservers() {
        loginViewModel.apply {
            navigateToHome.observe(this@LoginActivity) { event ->
                event.getContentIfNotHandled()?.let { navigateToHome() }
            }

            navigateToGoogleSignIn.observe(this@LoginActivity) { event ->
                event.getContentIfNotHandled()?.let { navigateToGoogleSignIn() }
            }

            navigateToSignIn.observe(this@LoginActivity) { event ->
                event.getContentIfNotHandled()?.let { navigateToSignIn() }
            }

            isLoading.observe(this@LoginActivity) { event ->
                event.getContentIfNotHandled()?.let { isLoading ->
                    if (isLoading) loadingDialog.show() else loadingDialog.dismiss()
                }
            }

            toastMessage.observe(this@LoginActivity) { message ->
                message?.let { Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun navigateToHome() {
        userPreferences.setSignedIn(true)
        val intent = Intent(this, Home::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToHomeSignedIn() {
        userPreferences.setSignedIn(true)
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToGoogleSignIn() {
        startActivity(Intent(this, GoogleSigninActivity::class.java))
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
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
