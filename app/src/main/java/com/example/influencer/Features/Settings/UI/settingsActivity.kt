package com.example.influencer.Features.Settings.UI

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.influencer.Features.Login.UI.LoginActivity
import com.example.influencer.Features.ProfileTab.UI.UserProfileViewModel
import com.example.influencer.R
import com.example.influencer.databinding.ActivitySettingsBinding
import com.example.influencer.databinding.ActivityUserProfileBinding
import com.example.influencer.databinding.AlertDialogChangeUsernameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class settingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var carga: SweetAlertDialog
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoingBack()
        setupClickListeners()
        setupObservers()
        initLoading()
    }

    private fun setupGoingBack() {
        binding.GoingBack.setOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        setupChangeUsername()
        setupChangeProfilePicture()
        setupLogoutButton()
    }

    private fun setupChangeUsername() {
        binding.ChangeUsernameButton.setOnClickListener {

        // Inflate the custom layout using ViewBinding
        val binding = AlertDialogChangeUsernameBinding.inflate(layoutInflater)
        val editTextNewUsername = binding.editTextNewUsername
        val textInputLayoutUsername = binding.textInputLayoutUsername

       // Set up TextWatcher to enforce minimum length and no spaces
        editTextNewUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.length < 6) {
                        textInputLayoutUsername.error = "Username must be at least 6 characters"
                    } else if (s.contains(" ")) {
                        textInputLayoutUsername.error = "Username must not contain spaces"
                    } else {
                        textInputLayoutUsername.error = null // Clear error
                    }
                }
            }
        })

       // Create the AlertDialog using MaterialAlertDialogBuilder
        MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            .setTitle("Change Username")
            .setView(binding.root)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                val newUsername = editTextNewUsername.text.toString()
                if (newUsername.isNotBlank() && newUsername.length >= 6 && !newUsername.contains(" ")) {
                    viewModel.updateUserName(newUsername)
                } else {
                    Toast.makeText(this, "Please check the username requirements.", Toast.LENGTH_SHORT).show()
                }
            }
            .show()

        }
    }

    private fun setupChangeProfilePicture() {
        binding.changeProfPictureButton.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfilePicture(it)
        }
    }

    private fun setupLogoutButton() {
        binding.LogOutButton.setOnClickListener{
            MaterialAlertDialogBuilder(this,R.style.CustomAlertDialog)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Yes") { _, _ -> viewModel.logout() }
                .show()
        }
    }

    private fun setupObservers() {
        viewModel.statusMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.loading.observe(this){isLoading->
            if (isLoading) carga.show() else carga.dismiss()
        }

        viewModel.logoutStatus.observe(this) { success ->
            if (success) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initLoading() {
        carga = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        carga.getProgressHelper().setBarColor(android.graphics.Color.parseColor("#F57E00"))
        carga.setTitleText(R.string.Loading)
        carga.setCancelable(false)
    }

}