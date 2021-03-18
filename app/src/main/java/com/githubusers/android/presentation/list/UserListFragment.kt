package com.githubusers.android.presentation.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.githubusers.android.R
import com.githubusers.android.databinding.FragmentUserListBinding
import com.githubusers.android.domain.model.User
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.presentation.adapter.UserRecyclerAdapter
import com.githubusers.android.presentation.details.UserDetailsFragment
import com.githubusers.android.presentation.util.UiUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserListFragment: Fragment(R.layout.fragment_user_list) {

    private val viewModel: UserListViewModel by  viewModels()

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRecyclerAdapter: UserRecyclerAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initViews()
        // displayInitialProgressBar(true)
        displayShimmer(true)
        viewModel.setStateEvent(UserIntent.GetUserListIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
        val searchViewItem = menu.findItem(R.id.appbar_search)
        val searchView = searchViewItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                showSearchResult(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                showSearchResult(newText)
                return false
            }
        })
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState is DataState.SUCCESS<UserListDataState>) {
                // displayInitialProgressBar(false)
                displayShimmer(false)
                displayLoadMoreProgressBar(false)
                dataState.data?.users?.let { it ->
                    updateList(it)
                }
            }
            else if (dataState is DataState.ERROR) {
                displayLoadMoreProgressBar(false)
                // displayInitialProgressBar(false)
                displayShimmer(false)
                dataState.stateMessage?.message?.let {
                    UiUtil.displayToast(requireContext(), it)
                }
                dataState.data?.users?.let { it ->
                    updateList(it)
                }
            }
        })
    }

    private fun displayInitialProgressBar(isDisplayed: Boolean) {
        if (isDisplayed) {
            displayEmptyMessage(false)
            binding.progressBarInitial.visibility = View.VISIBLE
        }
        else {
            binding.progressBarInitial.visibility = View.GONE
        }
    }

    private fun displayShimmer(isDisplayed: Boolean) {
        if (isDisplayed) {
            displayEmptyMessage(false)
            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.shimmerViewContainer.startShimmerAnimation()
        }
        else {
            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.GONE
        }
    }

    private fun displayLoadMoreProgressBar(isDisplayed: Boolean) {
        if (isDisplayed) {
            binding.linearProgressBarLoadMore.visibility = View.VISIBLE
        }
        else {
            binding.linearProgressBarLoadMore.visibility = View.GONE
        }
    }

    private fun displayEmptyMessage(isDisplayed: Boolean) {
        if (isDisplayed) {
            binding.tvEmptyMsg.visibility = View.VISIBLE
            binding.tvEmptyMsg.text = resources.getString(R.string.user_list_empty)
        }
        else {
            binding.tvEmptyMsg.visibility = View.GONE
        }
    }

    private fun updateList(users: List<User>?) {
        binding.mainRecyclerview.visibility = View.VISIBLE
        users?.let {
            when(users.isEmpty()) {
                true -> displayEmptyMessage(true)
                false -> userRecyclerAdapter.submitList(it)
            }
        }
    }

    private fun showSearchResult(queryText: String?) {
        userRecyclerAdapter.userFilter.filter(queryText)
        if (!queryText.isNullOrBlank() && queryText.isNotEmpty()) {
            viewModel.setStateEvent(UserIntent.ShowUnfilteredItems(false))
        }
        else {
            viewModel.setStateEvent(UserIntent.ShowUnfilteredItems(true))
        }
    }

    private fun initRecyclerview() {
        binding.mainRecyclerview.apply {
            layoutManager = LinearLayoutManager(this.context)

            val itemDecoration: UserRecyclerAdapter.TopSpacingDecoration =
                UserRecyclerAdapter.TopSpacingDecoration(2)
            addItemDecoration(itemDecoration)

            userRecyclerAdapter = UserRecyclerAdapter()
            userRecyclerAdapter.toUserDetailsScreenCb = this@UserListFragment::goToUserDetails
            adapter = userRecyclerAdapter

            addOnScrollListener(object : OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val linearLayoutManager = layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
                    val listItemIndex = userRecyclerAdapter.getListItems().size - 1
                    val loading = viewModel.dataState.value?.loading
                    val isShowUnfilteredList = viewModel.dataStateUI.value?.data?.isShowUnfilteredList ?: true

                    if (loading != true
                            && isShowUnfilteredList
                            && lastVisibleItemPosition == listItemIndex) {
                        displayLoadMoreProgressBar(true)
                        viewModel.setStateEvent(UserIntent.GetUserListIntent)
                    }
                }
            })
        }
    }

    private fun initViews() {
        initRecyclerview()
    }

    private fun goToUserDetails(user: User) {
        parentFragmentManager.saveFragmentInstanceState(this)
        UiUtil.replaceFragment(
            parentFragmentManager,
            R.id.fragmentHomeContainer,
            UiUtil.Screen.USER_DETAILS,
            bundleOf(
                UserDetailsFragment.ARGS_USER to user
            )
        )
    }
}
