package com.example.influencer.Features.CheckPoint_Tab.UI


import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.TextViewCompat

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.influencer.R
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.UI.CheckpointThemeChoose.CheckpointThemeChooseActivity
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.UI.CheckpointUpdateThemeChoose.CheckpointUpdateThemeChooseActivity
import com.example.influencer.databinding.FragmentCheckpointTabBinding
import com.yuyakaido.android.cardstackview.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CheckpointTabFragment : Fragment(), CardStackView_Adapter.CardActionsListener {

    private var _binding: FragmentCheckpointTabBinding? = null
    private val binding get() = _binding!!
    private var isAllFabsVisible: Boolean = false

    @Inject
    lateinit var cardstackviewAdapter: CardStackView_Adapter

    private val checkpointTabViewModel: CheckpointTabViewModel by viewModels()
    private lateinit var layoutManager: CardStackLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckpointTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialUI()
        initListeners()
        initObservers()
        setupCardStackView()

        // Setup menu icon listener
        binding.menuIcon.setOnClickListener {
            toggleDrawer()
        }


        setupNavigationDrawer()
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun updateMenuItemAppearance(menuItem: MenuItem) {
        val spannableTitle = SpannableString(menuItem.title.toString())
        val color = context?.let { ContextCompat.getColor(it, R.color.rojo_pastel) }
        if (menuItem.isChecked) {
            // Set text color to red and make it bold when checked
            spannableTitle.setSpan(color?.let { ForegroundColorSpan(it) }, 0, spannableTitle.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, spannableTitle.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        } else {
            // Set text color to default (black) and make it normal when unchecked
            spannableTitle.setSpan(ForegroundColorSpan(Color.BLACK), 0, spannableTitle.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            spannableTitle.setSpan(StyleSpan(Typeface.NORMAL), 0, spannableTitle.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        menuItem.title = spannableTitle
    }

    private fun setupNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            // Toggle the selection state of the item
            menuItem.isChecked = !menuItem.isChecked

            updateMenuItemAppearance(menuItem)

            true // Return true to display the item as the selected item
        }
    }

    private fun setupInitialUI() {
        setupAnimation()
        hideFabButtons()
        cardstackviewAdapter.listener = this //the CardActionsListener interface is implemented in this Fragment
    }

    private fun setupAnimation() {
        binding.RewindCheckpoint.apply {
            setAnimation(com.example.influencer.R.raw.rewind_checkpoint_post)
            setFrame(0)
            pauseAnimation()
            speed = 1.5f
        }
    }

    private fun hideFabButtons() {
        binding.addingNewCheckpoint.visibility = View.GONE
        binding.addingNewCheckpointUpdate.visibility = View.GONE
    }

    private fun initListeners() {
        setupFabListener()
        setupCheckpointListeners()
        setupRewindListener()
    }

    private fun setupFabListener() {
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
    }

    private fun setupCheckpointListeners() {
        binding.addingNewCheckpoint.setOnClickListener {
            checkpointTabViewModel.onAddingNewCheckpointSelected()
        }
        binding.addingNewCheckpointUpdate.setOnClickListener {
            checkpointTabViewModel.onAddingNewCheckpointUpdateSelected()
        }
    }

    private fun setupRewindListener() {
        binding.RewindCheckpoint.setOnClickListener {
            if (!checkpointTabViewModel.isLastCard()) {
                //Perform Rewind Animation
                with(binding.RewindCheckpoint) {
                    setAnimation(com.example.influencer.R.raw.rewind_checkpoint_post)
                    playAnimation()
                }
                binding.cardStackView.smoothScrollToPosition(0)
                binding.cardStackView.smoothScrollBy(120, 120)
                checkpointTabViewModel.rewind()
            } else {
                Toast.makeText(activity, R.string.NoRewind, Toast.LENGTH_SHORT).show()
            }
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
        checkpointTabViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupCardStackView() {
        layoutManager = CardStackLayoutManager(requireContext(), CardStackListenerImpl())
        configureCardStackView()
    }

    private fun configureCardStackView() {
        val displayMetrics = Resources.getSystem().displayMetrics
        val paddingHorizontal = (displayMetrics.widthPixels * 0.07).toInt()
        binding.cardStackView.apply {
            setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
            layoutManager = this@CheckpointTabFragment.layoutManager
            adapter = cardstackviewAdapter
        }
        layoutManager.apply {
            setVisibleCount(2)
            setStackFrom(StackFrom.Right)
            setTranslationInterval(8.0f)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setMaxDegree(50.0f)
            setCanScrollVertical(false)
        }
    }

    private inner class CardStackListenerImpl : CardStackListener {
        override fun onCardDragging(direction: Direction, ratio: Float) {}
        override fun onCardSwiped(direction: Direction) {
            if (direction == Direction.Left || direction == Direction.Right) {
                checkpointTabViewModel.swipeNextCard()
            }
        }
        override fun onCardRewound() {}
        override fun onCardCanceled() {}
        override fun onCardAppeared(view: View, position: Int) {}
        override fun onCardDisappeared(view: View, position: Int) {}
    }

    private fun goToAddingNewCheckpointUpdate() {
        val intent = Intent(context, CheckpointUpdateThemeChooseActivity::class.java)
        startActivity(intent)
    }
    private fun goToAddingNewCheckpoint() {
        val intent = Intent(context, CheckpointThemeChooseActivity::class.java)
        startActivity(intent)
    }

    //This code is the implementation functions of the "CardActionsListener" from the CardStackView_Adapter
    override fun onLikeClicked(postId: String,currentLikes: Int) {
        checkpointTabViewModel.likePost(postId)
    }
    override fun onUnlikeClicked(postId: String,currentLikes: Int) {
        checkpointTabViewModel.unlikePost(postId)
    }
    override fun checkPostLiked(postId: String, callback: (Boolean) -> Unit) {
        checkpointTabViewModel.checkIfPostIsLiked(postId, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
