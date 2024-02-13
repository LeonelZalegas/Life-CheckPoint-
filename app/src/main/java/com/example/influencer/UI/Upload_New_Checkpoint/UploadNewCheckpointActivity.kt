package com.example.influencer.UI.Upload_New_Checkpoint

import android.Manifest.permission.CAMERA
import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.influencer.BuildConfig


import com.example.influencer.R
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem
import com.example.influencer.UI.Home
import com.example.influencer.UI.Upload_New_Checkpoint.Adapter.TempImageAdapter
import com.example.influencer.UI.Upload_New_Checkpoint.Adapter.TempImageAdapterFactory
import com.example.influencer.databinding.ActivityUploadNewCheckpointBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// Extension function to handle getSerializableExtra deprecation
//https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#405018c8e80444e091a91d15c9bd434d
inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}
@AndroidEntryPoint
class UploadNewCheckpoint : AppCompatActivity() {
    private val viewModel: UploadCheckpointViewModel by viewModels()
    private lateinit var binding: ActivityUploadNewCheckpointBinding

/*aca existia 1 problema, y es q no podemos injectar TempImageAdapter de 1, porq para instanciarlo necesitamos pasar un parametro
que se procesa en tiempo de ejecucion (un runtime parameter) que seria el onDelete lambda, en especial el viewmodel.onCameraIconClicked
por ende se creo TempImageAdapterFactory (This approach is particularly useful when you need to pass values that cannot be determined until runtime, such as UI events or configuration data.) */
    @Inject
    lateinit var tempImageAdapterFactory: TempImageAdapterFactory
    private lateinit var tempImageAdapter: TempImageAdapter
    private lateinit var photoURI: Uri

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onCameraIconClicked(photoURI) //https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#69c77e740ffd417bae63e50308fc5219
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadNewCheckpointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
        setupClickListeners()
        setupRecyclerView()
        setupObservers()
    }


    private fun initializeUI() {
        handleSelectedCategory()
        setupSeekBar()
    }

    private fun setupClickListeners(){
        binding.cameraSelection.setOnClickListener {
            handleCameraSelection()
        }

        binding.postButton.setOnClickListener {
            handlePostButton()
        }

        binding.close.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent) // Start home activity and clear all others
        }
    }

    private fun handleSelectedCategory(){
        val selectedCategory = intent.getSerializableExtraCompat<CheckpointThemeItem>("SELECTED_CATEGORY")
        //https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#4636dc4be413463f92462daa45fa3048
        selectedCategory?.let {
            with(binding.chip) {
                text = it.text
                setChipBackgroundColorResource(it.color)
            }
            selectedCategoryText = it.text  //guardamos en variable para pasar x parametro al savepost
            selectedCategoryColor = it.color
        }
    }

    private fun setupSeekBar() {
        val startColor = Color.RED
        val endColor = ContextCompat.getColor(this, R.color.verde_seekBar)
        val colorEvaluator = ArgbEvaluator()

        // Initialize the SeekBar and set the listener
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Calculate the color
                val color = colorEvaluator.evaluate(progress / 100f, startColor, endColor) as Int

                // Update the TextView with the current progress and color
                binding.percentageBar.apply {
                    text = progress.toString()
                    setTextColor(color)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {/* Optional: Respond to the user starting to drag the SeekBar*/}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {/* Optional: Respond to the user finishing dragging the SeekBar*/}
        })
    }

    private fun handleCameraSelection(){
        if (allPermissionsGranted()) {
            if (viewModel.canTakeMorePictures()) {
                takePicture()
            } else{
                Toast.makeText(this, R.string.till_2_images_uploaded_only, Toast.LENGTH_SHORT).show()
            }
        } else {
            //this code pop ups the window for asking the camera permission
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun handlePostButton(){
        val text = binding.postTextInput.text.toString()
        val satisfactionLevel = binding.percentageBar.text.toString().toInt()
        if (text.isNotEmpty() && text.length > 20){
            viewModel.savePost(text, satisfactionLevel, selectedCategoryText, selectedCategoryColor)
        }else
            Toast.makeText(this, R.string.toast_text_cant_empty, Toast.LENGTH_SHORT).show()
    }

    private fun takePicture() {
        // Create an intent to launch the camera app
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        intent.resolveActivity(packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                 photoURI = FileProvider.getUriForFile(
                    this,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureLauncher.launch(intent)
           }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = externalCacheDir // Use cache directory
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupRecyclerView(){
        // Use the factory to create the TempImageAdapter with the onDelete lambda
        tempImageAdapter = tempImageAdapterFactory.create { position ->
            tempImageAdapter.deleteItem(position)
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.removeImageAt(position)
            },  300)
            // Optionally delay this operation or use a callback to ensure the animation completes
            // Assuming the animation takes less than 300ms to complete
        }

        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.TemporalImagesRV.layoutManager = layoutManager
        binding.TemporalImagesRV.adapter = tempImageAdapter

        viewModel.imagesLiveData.observe(this){uris ->
            tempImageAdapter.updateUriList(uris)
        }
    }

    private fun setupObservers() {
        viewModel.postSaveSuccessLiveData.observe(this){ isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Post saved successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent) // Start home activity and clear all others
            } else {
                Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        var selectedCategoryText: String = ""
        var selectedCategoryColor:Int = 0
    }

}