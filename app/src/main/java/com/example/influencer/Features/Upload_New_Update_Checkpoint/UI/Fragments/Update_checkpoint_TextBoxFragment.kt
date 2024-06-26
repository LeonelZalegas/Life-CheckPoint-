package com.example.influencer.Features.Upload_New_Update_Checkpoint.UI.Fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.influencer.R
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Model.CheckpointThemeItem
import com.example.influencer.Core.UI.Home
import com.example.influencer.Core.Utils.BackgroundAndTextColors
import com.example.influencer.Core.Utils.Serializable.getSerializableCompat
import com.example.influencer.databinding.FragmentUpdateCheckpointTextBoxBinding
import dagger.hilt.android.AndroidEntryPoint

const val MAX_LENGTH = 200

@AndroidEntryPoint
class Update_checkpoint_TextBox : Fragment() {

    private var _binding: FragmentUpdateCheckpointTextBoxBinding? = null
    private val binding get() = _binding!!   //The get() function here is a custom getter. In Kotlin, properties can have custom getters and setters,he custom getter for binding does not store a value itself; instead, it provides the value of _binding each time binding is accessed
    private val viewModel: SharedViewmodel by activityViewModels()
    private lateinit var carga: SweetAlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentUpdateCheckpointTextBoxBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        initLoading()
        setupClickListeners()
        setupObservers()
    }

    private fun initializeUI() {
        viewModel.selectedCategory.observe(viewLifecycleOwner) { selectedCategory ->
            selectedCategory?.let {
                handleSelectedCategory(it.color, it.text)
                setupCharacterCounter(it.color)
                viewModel.getNextUpdateNumber(it.text)
            }
        }
    }

    private fun setupCharacterCounter(selectedCategoryColor: Int) {
        with(binding.remainingCharsChip){
            text = "$MAX_LENGTH Char"
            setChipBackgroundColorResource(selectedCategoryColor)

            // Determine if the background color is dark or light
            val colorInt: Int = ContextCompat.getColor(context, selectedCategoryColor) //transforma Resource ID a un color posta
            val isDark = BackgroundAndTextColors.isColorDark(colorInt)
            setTextColor(if (isDark) Color.WHITE else Color.BLACK)
        }

        // Apply the filter to limit the input length
        val maxLengthFilter = InputFilter.LengthFilter(MAX_LENGTH)
        binding.updateTextInput.filters = arrayOf(maxLengthFilter)

        binding.updateTextInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val remainingChars = MAX_LENGTH - (s?.length ?: 0)
                binding.remainingCharsChip.text = "$remainingChars Char"
            }
        })
    }

    private fun handleSelectedCategory(selectedCategoryColor: Int,selectedCategoryName:String) {
            with(binding.chipSelectedCategory){
                text = selectedCategoryName
                setChipBackgroundColorResource(selectedCategoryColor) //lo mismo q dije en UploadNewCheckpointActivity, pasamos el CheckpointThemeItem directamente por eso funca

                // Determine if the background color is dark or light
                val colorInt: Int = ContextCompat.getColor(context, selectedCategoryColor) //transforma Resource ID a un color posta
                val isDark = BackgroundAndTextColors.isColorDark(colorInt)
                setTextColor(if (isDark) Color.WHITE else Color.BLACK)
            }
    }

    private fun initLoading() {
        carga = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"))
        carga.setTitleText(R.string.Loading)
        carga.setCancelable(false)
    }

    private fun setupClickListeners() {
        binding.close.setOnClickListener{
            val intent = Intent(requireActivity(), Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireActivity().startActivity(intent)  // Start home activity and clear all others
        }

        binding.postButton.setOnClickListener{
            var updateText = binding.updateTextInput.text.toString()
            viewModel.createCheckPointUpdate(updateText)
        }
    }

    private fun setupObservers() {
        viewModel.nextUpdateNumber.observe(viewLifecycleOwner) { nextUpdateNumber ->
            binding.chipNumberUpdate.text = nextUpdateNumber.toString()
        }

        viewModel.updateSaveSuccessLiveData.observe(viewLifecycleOwner){isSuccess ->
            if (isSuccess){
                Toast.makeText(activity, R.string.checkpoint_update_successfully_saved, Toast.LENGTH_SHORT).show()
                val intent = Intent(requireActivity(), Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                requireActivity().startActivity(intent)  // Start home activity and clear all others
            }else{
                Toast.makeText(activity, R.string.checkpoint_update_error_saved, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner){isloading ->
            if (isloading) {
                carga.show()
            } else {
                carga.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clearing the reference to avoid memory leaks l
    }
}