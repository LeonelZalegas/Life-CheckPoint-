package com.example.influencer.Features.Upload_New_Checkpoint.UI


import android.Manifest
import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.influencer.BuildConfig
import com.example.influencer.Core.Data.Network.NetworkConnectivity.NetworkActivity
import com.example.influencer.R
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Model.CheckpointThemeItem
import com.example.influencer.Core.UI.Home
import com.example.influencer.Core.Utils.BackgroundAndTextColors
import com.example.influencer.Core.Utils.Serializable.getSerializableExtraCompat
import com.example.influencer.Features.Upload_New_Checkpoint.UI.Adapter.TempImageAdapter
import com.example.influencer.Features.Upload_New_Checkpoint.UI.Adapter.TempImageAdapterFactory
import com.example.influencer.databinding.ActivityUploadNewCheckpointBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UploadNewCheckpoint : NetworkActivity() {
    private val viewModel: UploadCheckpointViewModel by viewModels()
    private lateinit var binding: ActivityUploadNewCheckpointBinding

/*aca existia 1 problema, y es q no podemos injectar TempImageAdapter de 1, porq para instanciarlo necesitamos pasar un parametro
que se procesa en tiempo de ejecucion (un runtime parameter) que seria el onDelete lambda, en especial el viewmodel.onCameraIconClicked
por ende se creo TempImageAdapterFactory (This approach is particularly useful when you need to pass values that cannot be determined until runtime, such as UI events or configuration data.) */
    @Inject
    lateinit var tempImageAdapterFactory: TempImageAdapterFactory
    private lateinit var tempImageAdapter: TempImageAdapter
    private lateinit var photoURI: Uri
    private lateinit var carga: SweetAlertDialog

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.uploadImageRecyclerView(photoURI) //https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#69c77e740ffd417bae63e50308fc5219
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadNewCheckpointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
        initLoading()
        setupClickListeners()
        setupRecyclerView()
        setupObservers()
    }


    private fun initializeUI() {
        handleSelectedCategory()
        setupSeekBar()
        loadLastThreeImages()
        setupProfilePic()
    }

    private fun initLoading() {
        carga = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"))
        carga.setTitleText(R.string.Loading)
        carga.setCancelable(false)
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

        listOf(binding.ImageSelection1, binding.ImageSelection2, binding.ImageSelection3).forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                viewModel.lastThreeImagesLiveData.value?.get(index)?.let { uri ->
                    if (viewModel.canTakeMorePictures()) {
                        viewModel.uploadImageRecyclerView(uri)                       //aca no controlamos q pasa si hay menos de 3 iamgenes y el user clickea el cuadrado gris que no hay nada porq no creo q pase nunca posta
                    } else{
                        Toast.makeText(this, R.string.till_2_images_uploaded_only, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.GallerySelection.setOnClickListener {
            if (viewModel.canTakeMorePictures()) {
                pickImage.launch("image/*")
            } else {
                Toast.makeText(this, R.string.till_2_images_uploaded_only, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSelectedCategory(){
        val selectedCategory = intent.getSerializableExtraCompat<CheckpointThemeItem>("SELECTED_CATEGORY")
        //https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#4636dc4be413463f92462daa45fa3048
        selectedCategory?.let {
            with(binding.chip) {
                text = it.text
                setChipBackgroundColorResource(it.color) // en este caso pasamos el CheckpointThemeItem directamente y por ende el Resource ID del color es puro y es el mismo que el del tiempo de compilacion original

                // Determine if the background color is dark or light
                val colorInt: Int = ContextCompat.getColor(context, it.color) //transforma Resource ID a un color posta
                val isDark = BackgroundAndTextColors.isColorDark(colorInt)
                setTextColor(if (isDark) Color.WHITE else Color.BLACK)
            }
            selectedCategoryText = it.text  //guardamos en variable para pasar x parametro al savepost (guardar Firebase)
            val intColor = ContextCompat.getColor(this, it.color)
            selectedCategoryColor = String.format("#%06X", (0xFFFFFF and intColor)) //Convert Color Resource to Hexadecimal String
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

    private fun loadLastThreeImages() {    //necesario pedir permiso para mostrar 3 ultoms imagenes, luego inicializar la funcion donde esta el livedata, asi el observer comience a funcar
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val requestCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUEST_CODE_IMAGES_PERMISSION
        } else {
            REQUEST_CODE_STORAGE_PERMISSION
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchLastThreeImagesUris()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun setupProfilePic() {
        viewModel.fetchUserProfilePicture()
    }

    private fun handleCameraSelection(){
        if (ContextCompat.checkSelfPermission(baseContext,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (viewModel.canTakeMorePictures()) {
                takePicture()
            } else{
                Toast.makeText(this, R.string.till_2_images_uploaded_only, Toast.LENGTH_SHORT).show()
            }
        } else {
            //this code pop ups the window for asking the camera permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA) , REQUEST_CODE_CAMERA_PERMISSIONS)
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

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.uploadImageRecyclerView(it)
        }
    }

    private fun setupRecyclerView(){
        // Use the factory to create the TempImageAdapter with the onDelete lambda
        tempImageAdapter = tempImageAdapterFactory.create { position ->
            tempImageAdapter.deleteItem(position)
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.removeImageAt(position)
            },  300)
            // Optionally delay this operation to ensure the animation completes (ponemos deleteItem(position) por la animacion nomas )
            // tenemos 2 Listas, la del Recyclerview (utiliza deleteItem y updateUriList)q se reflejan en real time y la del viewmodel (q es images y List en uploadImageRecyclerView y removeImageAt respectivamente)
        }

        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.TemporalImagesRV.layoutManager = layoutManager
        binding.TemporalImagesRV.adapter = tempImageAdapter
    }

    private fun setupObservers() {
        viewModel.postSaveSuccessLiveData.observe(this){ isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, R.string.checkpoint_successfully_saved, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent) // Start home activity and clear all others
            } else {
                Toast.makeText(this, R.string.checkpoint_error_saved, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(this){isloading ->
            if (isloading) {
                carga.show()
            } else {
                carga.dismiss()
            }
        }

        viewModel.recyclerViewLiveData.observe(this){ uris ->     //actualizamos RecyclerView en timepo real
            tempImageAdapter.updateUriList(uris)
        }

        viewModel.lastThreeImagesLiveData.observe(this) { uris ->                   //actualizamos en real time las imagenes que se muestran en el imageview de las 3 ultimas imagenes
            uris.getOrNull(0)?.let { binding.ImageSelection1.setImageURI(it) }
            uris.getOrNull(1)?.let { binding.ImageSelection2.setImageURI(it) }
            uris.getOrNull(2)?.let { binding.ImageSelection3.setImageURI(it) }
        }

        viewModel.profilePictureUrl.observe(this) { url ->
            Glide.with(this)
                .load(url)
                .into(binding.profilePicture)
        }
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

    //esta funcion se activa una vez el usuario clickea una de las 3 opciones (usar, solo 1 vez, nunca) en el pop up de preguntar si se puede usar la camara/acceder storage
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // Check if the request code matches the REQUEST_CODE_PERMISSIONS for camera
            REQUEST_CODE_CAMERA_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (viewModel.canTakeMorePictures()) {
                        takePicture()
                    } else{
                        Toast.makeText(this, R.string.till_2_images_uploaded_only, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            // Check if the request code matches the REQUEST_CODE_STORAGE_PERMISSION for storage
            REQUEST_CODE_STORAGE_PERMISSION -> {
                // Check if the request code matches the REQUEST_CODE_STORAGE_PERMISSION for storage
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.fetchLastThreeImagesUris()
                }else{
                    finish()
                }
            }

            REQUEST_CODE_IMAGES_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.fetchLastThreeImagesUris()
                } else {
                    finish()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 10
        private const val REQUEST_CODE_STORAGE_PERMISSION = 101
        private const val REQUEST_CODE_IMAGES_PERMISSION = 102 // para versiones de android 11 pa adelante
        var selectedCategoryText: String = ""
        var selectedCategoryColor:String = ""
    }
}