package com.githubusers.android.presentation.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.githubusers.android.R
import com.githubusers.android.databinding.FragmentUserListBinding
import com.githubusers.android.domain.model.User
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.domain.util.Util.findUserPositionInList
import com.githubusers.android.presentation.adapter.UserRecyclerAdapter
import com.githubusers.android.presentation.details.UserDetailsFragment
import com.githubusers.android.presentation.util.UiUtil
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserListFragment: Fragment(R.layout.fragment_user_list) {

    private val viewModel: UserListViewModel by  viewModels()

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRecyclerAdapter: UserRecyclerAdapter
    private lateinit var toolbar: MaterialToolbar

    companion object {
        const val ARGS_UPDATED_USER = "updated_user"
        private const val ARGS_USERS = "users_list"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserListBinding.bind(view)
        initViews()
        subscribeObservers()
        checkViewState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        setHasOptionsMenu(true)
        displaySearchViewMenuItem(true)
        initRecyclerview()
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

    private fun displaySearchViewMenuItem(isVisible: Boolean) {
        toolbar = requireActivity().findViewById(R.id.topAppBar)
        toolbar.inflateMenu(R.menu.home_menu)
        val searchViewItem = toolbar.menu.findItem(R.id.appbar_search)
        val searchView = searchViewItem?.actionView as SearchView
        if (isVisible) {
            toolbar.setNavigationIcon(R.drawable.ic_logo_githubusers)
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
            searchView.visibility = View.VISIBLE
        }
        else {
            searchView.clearFocus()
            searchView.visibility = View.GONE
            toolbar.menu.clear()
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState is DataState.SUCCESS<UserListDataState>) {
                displayShimmer(false)
                displayLoadMoreProgressBar(false)
                dataState.data?.users?.let { it ->
                    updateList(it)
                    findNavController().currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(ARGS_USERS, userRecyclerAdapter.items)
                }
            }
            else if (dataState is DataState.ERROR) {
                displayLoadMoreProgressBar(false)
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

    private fun checkViewState() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        val existingUserList = savedStateHandle?.get<List<User>>(ARGS_USERS)

        existingUserList?.let {
            displayShimmer(false)
            displayLoadMoreProgressBar(false)
            updateList(it)
        }

        if (existingUserList == null) {
            displayShimmer(true)
            viewModel.setStateEvent(UserIntent.GetUserListIntent)
        }

        val updatedUser = savedStateHandle?.get<User>(ARGS_UPDATED_USER)

        updatedUser?.let {
            val updatedUserPosition = findUserPositionInList(userRecyclerAdapter.items, updatedUser)
            updatedUserPosition.let { pos ->
                if (pos > -1) {
                    userRecyclerAdapter.items[pos] = updatedUser
                    userRecyclerAdapter.notifyItemChanged(pos)
                }
            }
            savedStateHandle.remove<User>(ARGS_UPDATED_USER)
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
        userRecyclerAdapter.itemFilter.filter(queryText)
        if (!queryText.isNullOrBlank() && queryText.isNotEmpty()) {
            viewModel.setStateEvent(UserIntent.ShowUnfilteredItems(false))
        }
        else {
            viewModel.setStateEvent(UserIntent.ShowUnfilteredItems(true))
        }
    }

    private fun goToUserDetails(user: User, position: Int?) {
        displaySearchViewMenuItem(false)
        parentFragmentManager.saveFragmentInstanceState(this)
        UiUtil.navigateTo(
            findNavController(),
            R.id.action_userListFragment_to_userDetailsFragment,
            bundleOf(
                UserDetailsFragment.ARGS_USER to user
            )
        )
    }
}
