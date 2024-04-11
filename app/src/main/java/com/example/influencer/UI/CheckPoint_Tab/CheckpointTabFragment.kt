package com.example.influencer.UI.CheckPoint_Tab


import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.CheckpointThemeChoose.CheckpointThemeChooseActivity
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.CheckpointUpdateThemeChoose.CheckpointUpdateThemeChooseActivity
import com.example.influencer.databinding.FragmentCheckpointTabBinding
import com.yuyakaido.android.cardstackview.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckpointTabFragment : Fragment(), CardStackView_Adapter.CardActionsListener {

    private var _binding: FragmentCheckpointTabBinding? = null
    private val binding get() = _binding!!
    private var isAllFabsVisible: Boolean = false
    @Inject
    lateinit var cardstackviewAdapter: CardStackView_Adapter
    val displayMetrics = Resources.getSystem().displayMetrics
    private val checkpointTabViewModel: CheckpointTabViewModel by viewModels()
    private lateinit var layoutManager: CardStackLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckpointTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initial static display of the animation
        binding.RewindCheckpoint.apply {
            setAnimation(com.example.influencer.R.raw.rewind_checkpoint_post)
            setFrame(0)
            pauseAnimation()
            speed = 1.5f
        }

        binding.addingNewCheckpoint.visibility = View.GONE
        binding.addingNewCheckpointUpdate.visibility = View.GONE

        cardstackviewAdapter.listener = this

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

        binding.RewindCheckpoint.setOnClickListener(){
            if (!checkpointTabViewModel.isLastCard()){
                with(binding.RewindCheckpoint) {
                    setAnimation(com.example.influencer.R.raw.rewind_checkpoint_post)
                    playAnimation()
                }
                    binding.cardStackView.smoothScrollToPosition(0)  // Perform the rewind
                    binding.cardStackView.smoothScrollBy(120, 120)   // Animation of the rewind
                    checkpointTabViewModel.Rewind()

            }else
                Toast.makeText(activity, "Cant rewind anymore", Toast.LENGTH_SHORT).show()
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
            cardstackviewAdapter.setCards(cards)
        }

        checkpointTabViewModel.likeUpdate.observe(viewLifecycleOwner) { (postId, newLikes) ->
            cardstackviewAdapter.updateLikes(postId, newLikes)
        }

        checkpointTabViewModel.loading.observe(viewLifecycleOwner){isloading ->
            if (isloading) {
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
            }
        }
    }


    private fun setupCardStackView() {
         layoutManager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction, ratio: Float) {}
            override fun onCardSwiped(direction: Direction) {

                when (direction) {
                    Direction.Left -> checkpointTabViewModel.swipeNextCard()
                    Direction.Right -> checkpointTabViewModel.swipeNextCard()
                    else -> {}
                }
            }
            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View, position: Int) {}
            override fun onCardDisappeared(view: View, position: Int) {}
        })

        val paddingHorizontal = (displayMetrics.widthPixels * 0.07).toInt() // 7% of screen width for left and right padding
        binding.cardStackView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0)

        layoutManager.setVisibleCount(2)
        layoutManager.setStackFrom(StackFrom.Right)
        layoutManager.setTranslationInterval(8.0f)
        layoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        layoutManager.setMaxDegree(50.0f)
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

    override fun onLikeClicked(postId: String, postOwnerId: String, currentLikes: Int) {
        checkpointTabViewModel.likePost(postId,postOwnerId)
    }

    override fun onUnlikeClicked(postId: String, postOwnerId: String, currentLikes: Int) {
        checkpointTabViewModel.unlikePost(postId,postOwnerId)
    }

    override fun checkPostLiked(postId: String, callback: (Boolean) -> Unit) {
        checkpointTabViewModel.checkIfPostIsLiked(postId, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}