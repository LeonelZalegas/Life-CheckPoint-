package com.example.influencer.UI.CheckPoint_Tab


import dagger.hilt.android.AndroidEntryPoint
import com.example.influencer.UI.CheckPoint_Tab.CheckpointTabViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.influencer.Core.Event
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.CheckpointUpdateThemeChoose.CheckpointUpdateThemeChooseActivity
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.CheckpointThemeChoose.CheckpointThemeChooseActivity
import com.example.influencer.databinding.FragmentCheckpointTabBinding

@AndroidEntryPoint
class CheckpointTabFragment : Fragment() {

    private var _binding: FragmentCheckpointTabBinding? = null
    private val binding get() = _binding!!
    private var isAllFabsVisible: Boolean = false
    private val checkpointTabViewModel: CheckpointTabViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckpointTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addingNewCheckpoint.visibility = View.GONE
        binding.addingNewCheckpointUpdate.visibility = View.GONE

        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.addFab.setOnClickListener {
            if (!isAllFabsVisible) {
                binding.addingNewCheckpoint.show()
                binding.addingNewCheckpointUpdate.show()
                isAllFabsVisible = true
            } else {
                binding.addingNewCheckpoint.hide()
                binding.addingNewCheckpointUpdate.hide()
                isAllFabsVisible = false
            }
        }

        binding.addingNewCheckpoint.setOnClickListener {
            checkpointTabViewModel.onAddingNewCheckpointSelected()
        }

        binding.addingNewCheckpointUpdate.setOnClickListener {
            checkpointTabViewModel.onAddingNewCheckpointUpdateSelected()
        }
    }

    private fun initObservers() {
        checkpointTabViewModel.navigateToAddingNewCheckpoint.observe(requireActivity()) { event ->
            event.contentIfNotHandled?.let {
                goToAddingNewCheckpoint()
            }
        }

        checkpointTabViewModel.navigateToAddingNewCheckpointUpdate.observe(requireActivity()) { event ->
            event.contentIfNotHandled?.let {
                goToAddingNewCheckpointUpdate()
            }
        }
    }

    private fun goToAddingNewCheckpointUpdate() {
        val intentAddingNewCheckpointUpdate = Intent(context, CheckpointUpdateThemeChooseActivity::class.java)
        startActivity(intentAddingNewCheckpointUpdate)
    }

    private fun goToAddingNewCheckpoint() {
        val intentAddingNewCheckpoint = Intent(context, CheckpointThemeChooseActivity::class.java)
        startActivity(intentAddingNewCheckpoint)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}