package com.example.influencer.UI.Upload_New_Update_Checkpoint.Fragments

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
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.influencer.Core.Serializable.getSerializableCompat
import com.example.influencer.R
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem
import com.example.influencer.UI.Home
import com.example.influencer.databinding.FragmentUpdateCheckpointTextBoxBinding
import dagger.hilt.android.AndroidEntryPoint

const val MAX_LENGTH = 200

@AndroidEntryPoint
class Update_checkpoint_TextBox : Fragment() {

    private var _binding: FragmentUpdateCheckpointTextBoxBinding? = null
    private val binding get() = _binding!!   //The get() function here is a custom getter. In Kotlin, properties can have custom getters and setters,he custom getter for binding does not store a value itself; instead, it provides the value of _binding each time binding is accessed
    private val viewModel: Checkpoint_TextBox_Viewmodel by viewModels()
    private var selectedCategoryColor: Int = 0
    private lateinit var selectedCategoryName:String
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
        handleSelectedCategory()
        setupCharacterCounter()
        viewModel.getNextUpdateNumber(selectedCategoryName)
    }

    private fun setupCharacterCounter() {
        with(binding.remainingCharsChip){
            text = "$MAX_LENGTH Char"
            setChipBackgroundColorResource(selectedCategoryColor)
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

    private fun handleSelectedCategory() {
        val selectedCategory = arguments?.getSerializableCompat("SELECTED_CATEGORY", CheckpointThemeItem::class.java)
        selectedCategory?.let {
            selectedCategoryColor = it.color
            selectedCategoryName = it.text
            with(binding.chipSelectedCategory){
                text = selectedCategoryName
                setChipBackgroundColorResource(selectedCategoryColor)
            }
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
        _binding = null // Clearing the reference to avoid memory leaks
    }
}