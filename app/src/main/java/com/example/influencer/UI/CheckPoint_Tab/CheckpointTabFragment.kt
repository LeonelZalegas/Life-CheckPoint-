package com.example.influencer.UI.CheckPoint_Tab


import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.CheckpointThemeChoose.CheckpointThemeChooseActivity
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.CheckpointUpdateThemeChoose.CheckpointUpdateThemeChooseActivity
import com.example.influencer.databinding.FragmentCheckpointTabBinding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckpointTabFragment : Fragment() {

    private var _binding: FragmentCheckpointTabBinding? = null
    private val binding get() = _binding!!
    private var isAllFabsVisible: Boolean = false
    @Inject
    lateinit var cardstackviewAdapter: CardStackView_Adapter
    val displayMetrics = Resources.getSystem().displayMetrics
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
        setupCardStackView()
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

        checkpointTabViewModel.cards.observe(viewLifecycleOwner) { cards ->
            Toast.makeText(activity, "el Observer si se ejecuta parece", Toast.LENGTH_SHORT).show()
            cardstackviewAdapter.setCards(cards)
        }
    }

    private fun setupCardStackView() {
        val layoutManager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction, ratio: Float) {}
            override fun onCardSwiped(direction: Direction) {
                when (direction) {
                    Direction.Left -> checkpointTabViewModel.swipeLeft()
                    Direction.Right -> checkpointTabViewModel.swipeRight()
                    else -> {}
                }
            }
            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View, position: Int) {}
            override fun onCardDisappeared(view: View, position: Int) {}
        })

//        val paddingHorizontal = (displayMetrics.widthPixels * 0.10).toInt() // 10% of screen width for left and right padding
//        val paddingVerticalTop = (displayMetrics.heightPixels * 0.10).toInt() // 10% of screen height for top padding
//        val paddingVerticalBottom = (displayMetrics.heightPixels * 0.15).toInt() // 15% of screen height for bottom padding
//
//        binding.cardStackView.setPadding(paddingHorizontal, paddingVerticalTop, paddingHorizontal, paddingVerticalBottom)

        layoutManager.setCanScrollVertical(false)
        binding.cardStackView.layoutManager = layoutManager
        binding.cardStackView.adapter = cardstackviewAdapter
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