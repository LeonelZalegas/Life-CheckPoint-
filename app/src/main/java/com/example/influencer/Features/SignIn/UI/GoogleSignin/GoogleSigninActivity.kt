package com.example.influencer.Features.SignIn.UI.GoogleSignin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.influencer.Core.Data.Preferences.UserPreferences
import com.example.influencer.Core.UI.Home
import com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.OnBoarding.OnBoardingActivity
import com.example.influencer.R
import com.example.influencer.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GoogleSigninActivity : AppCompatActivity() {

    private val googleSigninViewModel: GoogleSigninViewModel by viewModels()
    private lateinit var carga: SweetAlertDialog
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoading()
        initBasicObservers()

        val resultadoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d("error", "firebaseAuthWithGoogle:${account.id}")

                    val profilePictureUrl = account.photoUrl.toString()
                    googleSigninViewModel.handleGoogleSignInResult(account.idToken!!, profilePictureUrl)
                } catch (e: ApiException) {
                    Log.w("error", "Google sign in failed", e)
                    Toast.makeText(this, R.string.error_SignIn_Firestore, Toast.LENGTH_SHORT).show()
                }
            }
        }

        resultadoLauncher.launch(mGoogleSignInClient.signInIntent)

        googleSigninViewModel.userExists.observe(this) { userExists ->
            if (userExists) {
                goToHome()
            }
        }

        googleSigninViewModel.userGetsCreated.observe(this) { userGetsCreated ->
            if (userGetsCreated) {
                goToOnBoarding()
            }
        }
    }

    private fun initBasicObservers() {
        googleSigninViewModel.loading.observe(this) { event ->
            event.getContentIfNotHandled()?.let { isLoading ->
                if (isLoading) carga.show() else carga.dismiss()
            }
        }

        googleSigninViewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initLoading() {
        carga = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).apply {
            progressHelper.barColor = Color.parseColor("#F57E00")
            titleText = getString(R.string.Loading)
            setCancelable(false)
        }
    }

    private fun goToOnBoarding() {
        userPreferences.setSignedIn(true)
        Intent(this, OnBoardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
        finish()
    }

    private fun goToHome() {
        userPreferences.setSignedIn(true)
        Intent(this, Home::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
        finish()
    }
}
