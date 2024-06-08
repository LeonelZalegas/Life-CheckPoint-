package com.example.influencer.Core.UI.ProfileTab.PostsAndLikesFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.influencer.Core.UI.ProfileTab.UserProfileViewModel
import com.example.influencer.Core.Utils.CheckpointsCategoriesList
import com.example.influencer.R
import com.example.influencer.databinding.FragmentPostsAndLikesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostsAndLikesFragment : Fragment(), CategoriesAdapter.OnCategoryClickListener, PostsAndLikesAdapter.OnLikeIconClickListener {

    companion object {
        private const val ARG_TAB_POSITION = "tab_position"

        fun newInstance(tabPosition: Int): PostsAndLikesFragment {
            val fragment = PostsAndLikesFragment()
            val args = Bundle()
            args.putInt(ARG_TAB_POSITION, tabPosition)
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var checkpointsCategoriesList: CheckpointsCategoriesList
    private var tabPosition: Int = 0
    private lateinit var binding: FragmentPostsAndLikesBinding
    private val viewModel: UserProfileViewModel by viewModels({ requireParentFragment()})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabPosition = it.getInt(ARG_TAB_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostsAndLikesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        initObservers()
    }

    private fun setupRecyclerViews() {
        val constraintLayout = binding.constraintLayout
        val constraintSet = ConstraintSet() //This class allows you to programmatically manipulate constraints of the ConstraintLayout. You clone the current layout's constraints, modify them, and then apply them back to the layout.
        constraintSet.clone(constraintLayout)

        if (tabPosition == 0) {
            // Checkpoints tab
            binding.categoriesRecyclerView.visibility = View.VISIBLE
            binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.categoriesRecyclerView.adapter = CategoriesAdapter(checkpointsCategoriesList.categories, this)
            binding.postsRecyclerView.adapter = PostsAndLikesAdapter(true,this)

            // Set postsRecyclerView top constraint to the bottom of the guideline
            constraintSet.connect(binding.postsRecyclerView.id, ConstraintSet.TOP, binding.guideline.id, ConstraintSet.BOTTOM, 0)
        } else {
            // Likes tab
            binding.categoriesRecyclerView.visibility = View.GONE
            binding.postsRecyclerView.adapter = PostsAndLikesAdapter(false,this)

            // Set postsRecyclerView top constraint to the top of the parent
            constraintSet.connect(binding.postsRecyclerView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        }

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        // Apply the constraints
        constraintSet.applyTo(constraintLayout)
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun initObservers() {
       tabLayoutDataLoad()
        postsShowing()

        viewModel.progress.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun tabLayoutDataLoad() {
        viewModel.profileTabUserId.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                if (tabPosition == 0) {
                    viewModel.loadCheckpointsByCategory(checkpointsCategoriesList.categories.first().text)
                } else {
                    viewModel.loadLikes()
                }
            }
        }
    }

    private fun postsShowing() {
        viewModel.checkpointsPosts.observe(viewLifecycleOwner) { checkpoints ->
            if(tabPosition == 0) { //https://www.notion.so/ProfileTabFragment-26cfd73f6c2c4576b680f713ad431eaf?pvs=4#cbd26b4e29de4ee8807bcc0c322cd2d6
                if (checkpoints != null) {
                    binding.postsRecyclerView.visibility = View.VISIBLE
                    binding.NoCheckpointsCategory.visibility = View.GONE

                    (binding.postsRecyclerView.adapter as PostsAndLikesAdapter).submitList(checkpoints)
                } else {
                    binding.postsRecyclerView.visibility = View.GONE
                    binding.NoCheckpointsCategory.visibility = View.VISIBLE
                }
            }
        }

        viewModel.likesPosts.observe(viewLifecycleOwner) { likedPosts ->
            if(tabPosition == 1) { //https://www.notion.so/ProfileTabFragment-26cfd73f6c2c4576b680f713ad431eaf?pvs=4#cbd26b4e29de4ee8807bcc0c322cd2d6
                if (likedPosts != null) {
                    (binding.postsRecyclerView.adapter as PostsAndLikesAdapter).submitList(likedPosts)
                } else {
                    binding.postsRecyclerView.visibility = View.GONE
                    binding.NoCheckpointsLike.visibility = View.VISIBLE
                }
            }
        }
    }


    override fun onCategoryClick(category: String) {
        viewModel.loadCheckpointsByCategory(category)
    }

    override fun onLikeClicked(postId: String) {
        Log.w("dadoLike", "se llama dentro del fragment al like")
        viewModel.likePost(postId)
    }
    override fun onUnlikeClicked(postId: String) {
        Log.w("dadoLike", "se llama dentro del fragment al unlike")
        viewModel.unlikePost(postId)
    }

}
