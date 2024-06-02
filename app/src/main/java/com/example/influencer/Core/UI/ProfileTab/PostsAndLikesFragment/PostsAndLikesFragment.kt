package com.example.influencer.Core.UI.ProfileTab.PostsAndLikesFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.influencer.Core.UI.ProfileTab.UserProfileViewModel
import com.example.influencer.Core.Utils.CheckpointsCategoriesList
import com.example.influencer.R
import com.example.influencer.databinding.FragmentPostsAndLikesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostsAndLikesFragment : Fragment(), CategoriesAdapter.OnCategoryClickListener {

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

        Log.w("noPosts", "el position es:$tabPosition ")
        setupRecyclerViews()

        viewModel.profileTabUserId.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                if (tabPosition == 0) {
                    viewModel.loadCheckpointsByCategory(checkpointsCategoriesList.categories.first().text)
                } else {
                    viewModel.loadLikes()
                }
            }
        }

        viewModel.checkpointsPosts.observe(viewLifecycleOwner) { checkpoints ->
            if (checkpoints != null && tabPosition == 0) {
                (binding.postsRecyclerView.adapter as PostsAndLikesAdapter).submitList(checkpoints)
            }
        }

        viewModel.likesPosts.observe(viewLifecycleOwner) { likes ->
            if (likes != null && tabPosition == 1) {
                (binding.postsRecyclerView.adapter as PostsAndLikesAdapter).submitList(likes)
            }
        }

//        if (tabPosition == 0) {
//            viewModel.checkpointsPosts.observe(viewLifecycleOwner) { checkpoints ->
//                Log.w("noPosts", "la lista de checkpoints devuelta es:$checkpoints ")
//                if (checkpoints != null) {
//                    (binding.postsRecyclerView.adapter as PostsAndLikesAdapter).submitList(checkpoints)
//                }
//            }
//            // Select the first category by default
//            val firstCategory = checkpointsCategoriesList.categories.first().text
//            viewModel.loadCheckpointsByCategory("Work/Carrer")
//        } else {
//            viewModel.likesPosts.observe(viewLifecycleOwner) { likes ->
//                Log.w("noPosts", "la lista de likes posts devuelta es:$likes ")
//                if (likes != null) {
//                    (binding.postsRecyclerView.adapter as PostsAndLikesAdapter).submitList(likes)
//                }
//            }
//            viewModel.loadLikes()
//        }
    }

    private fun setupRecyclerViews() {
        if (tabPosition == 0) {
            // Checkpoints tab
            binding.categoriesRecyclerView.visibility = View.VISIBLE
            binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.categoriesRecyclerView.adapter = CategoriesAdapter(checkpointsCategoriesList.categories, this)
            binding.postsRecyclerView.adapter = PostsAndLikesAdapter(true)
        } else {
            // Likes tab
            binding.categoriesRecyclerView.visibility = View.GONE
            binding.postsRecyclerView.adapter = PostsAndLikesAdapter(false)
        }

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
    }

    override fun onCategoryClick(category: String) {
        viewModel.loadCheckpointsByCategory(category)
    }
}
