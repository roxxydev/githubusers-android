package com.githubusers.android.presentation.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.githubusers.android.R
import com.githubusers.android.databinding.FragmentUserDetailsBinding
import com.githubusers.android.domain.model.User
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.domain.util.Util
import com.githubusers.android.presentation.list.UserListFragment.Companion.ARGS_UPDATED_USER
import com.githubusers.android.presentation.util.DisplayImageOpts
import com.githubusers.android.presentation.util.UiUtil
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserDetailsFragment: Fragment(R.layout.fragment_user_details) {

    private val viewModel: UserDetailsViewModel by  viewModels()

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var user: User

    companion object {
        val ARGS_USER = "username"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = arguments?.getParcelable(ARGS_USER)!!
        _binding = FragmentUserDetailsBinding.bind(view)
        subscribeObservers()
        initViews()
        viewModel.setStateEvent(UserDetailsIntent.GetUserDetails(user.login))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.SUCCESS<UserDetailsDataState> -> {
                    displayProgress(false)
                    dataState.data?.user?.let {
                        displayUserDetails(it)
                        findNavController()
                            .previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(ARGS_UPDATED_USER, it)
                    }
                }

                is DataState.LOADING -> {
                    displayProgress(dataState.loading)
                }

                is DataState.ERROR -> {
                    displayProgress(dataState.loading)
                    dataState.data?.user?.let {
                        displayUserDetails(it)
                    }
                }
            }
        })
    }

    private fun initViews() {
        binding.btnEdit.tag = "edit"
        binding.btnEdit.setOnClickListener {
            if (it.tag != null && it.tag.toString() == "edit") {
                binding.note.visibility = View.VISIBLE
                binding.note.editText?.isEnabled = true
                binding.btnEdit.text = getString(R.string.btn_save_note)
                binding.btnEdit.tag = "save"
                it.requestFocus()
            }
            else if (it.tag != null && it.tag.toString() == "save") {
                val note = binding.note.editText?.text.toString() ?: ""
                viewModel.setStateEvent(UserDetailsIntent.SaveUserNote(user.getUsername(), note))
                binding.note.editText?.isEnabled = false
                binding.btnEdit.text = getString(R.string.btn_edit_note)
                binding.btnEdit.tag = "edit"
            }
        }
    }

    // TODO: Display progressbar if there's an network http request being called
    private fun displayProgress(isDisplayed: Boolean) {
        if (isDisplayed) {
        }
        else {
        }
    }

    private fun setText(textInputLayout: TextInputLayout, value: String?) {
        textInputLayout.editText?.let {
            if (value != null && value.isNotEmpty() && value.isNotBlank()) {
                it.setText(value)
            }
            else {
                textInputLayout.visibility = View.GONE
            }
        }
    }

    private fun displayUserDetails(user: User) {
        UiUtil.displayImage(requireContext(), user.avatarUrl, binding.ivAvatar,
            DisplayImageOpts(
                isDisplayLoading = true,
                isDisplayErrorImage = false,
                isCircleCrop = false
            )
        )

        setText(binding.login, user.getUsername())
        setText(binding.type, user.type)

        val joinedDate = if (user.createdAt != null) {
            Util.dateTimestampStringToDateText(user.createdAt!!, null)
        } else ""
        setText(binding.createdAt, joinedDate)

        setText(binding.name, user.name)
        setText(binding.company, user.company)
        setText(binding.blog, user.blog)
        setText(binding.location, user.location)
        setText(binding.email, user.email)

        val txtYes = getString(R.string.label_yes)
        val txtNo = getString(R.string.label_no)
        val hireable = if (user.hireable != null && user.hireable == true) txtYes else txtNo
        setText(binding.hireable, hireable)

        setText(binding.bio, user.bio)
        setText(binding.twitterUsername, user.twitterUsername)
        setText(binding.publicRepos, user.publicRepos.toString())
        setText(binding.publicGists, user.publicGists.toString())
        setText(binding.followers, user.followers.toString())
        setText(binding.following, user.following.toString())

        if (user.note != null) {
            binding.note.visibility = View.VISIBLE
            binding.note.editText?.setText(user.note)
        }
        else {
            binding.note.visibility = View.GONE
        }
    }
}
