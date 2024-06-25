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
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.influencer.Features.Login.Domain.Model.UsuarioLogin
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.influencer.Core.Utils.Event
import com.example.influencer.Features.SignIn.UI.GoogleSignin.GoogleSigninActivity
import com.example.influencer.Features.SignIn.UI.AppSignIn.SignInActivity
import com.example.influencer.databinding.ActivityMainBinding

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        handleSplashScreen()
        super.onCreate(savedInstanceState)

        if (userPreferences.isSignedIn) {
            navigateToHome()
            return
        }

        setupBinding()
        setupBackgroundAnimation()
        initLoading()
        initUI()
    }

    private fun handleSplashScreen() {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
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
                if (loginViewModel.validatingLogin(email, password)) {
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

            Loading.observe(this@LoginActivity) { event ->
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

    private fun navigateToGoogleSignIn() {
        startActivity(Intent(this, GoogleSigninActivity::class.java))
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
    }
}
