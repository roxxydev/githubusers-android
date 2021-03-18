package com.githubusers.android.presentation.list

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.githubusers.android.R
import com.githubusers.android.domain.model.User
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.domain.state.StateMessage
import com.githubusers.android.repository.MainRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class UserListViewModel
@ViewModelInject
constructor(
    @ApplicationContext private val appCtx: Context,
    private val mainRepository: MainRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dataState: MutableLiveData<DataState<UserListDataState>> = MutableLiveData()
    private val _dataStateUI: MutableLiveData<DataState<UserListDataStateUI>> = MutableLiveData()

    val dataState: LiveData<DataState<UserListDataState>>
        get () = _dataState
    val dataStateUI: LiveData<DataState<UserListDataStateUI>>
        get () = _dataStateUI

    fun setStateEvent(intent: UserIntent) {
        viewModelScope.launch {
            when (intent) {
                is UserIntent.GetUserListIntent -> {
                    val lastId = _dataStateUI.value?.data?.lastId ?: 0
                    mainRepository.listUsers(lastId)
                        .onEach { dataState ->
                            when (dataState) {
                                is DataState.LOADING -> {
                                    _dataState.value = DataState.LOADING(dataState.loading)
                                }
                                is DataState.ERROR -> {
                                    val errorTxt = appCtx.getString(R.string.error_message)
                                    val stateMsg = StateMessage(errorTxt)
                                    _dataState.value =
                                        DataState.ERROR(
                                            stateMsg,
                                            dataState.errorType!!,
                                            UserListDataState(dataState.data)
                                        )
                                }
                                is DataState.SUCCESS -> {
                                    val users = dataState.data
                                    val newLastId = if (users != null && users.isNotEmpty()) users.last().id else lastId
                                    _dataState.value = DataState.SUCCESS(UserListDataState(users))
                                    _dataStateUI.value = DataState.SUCCESS(
                                        UserListDataStateUI(
                                            lastId = newLastId,
                                            isShowUnfilteredList = true
                                        )
                                    )
                                }
                            }
                        }
                        .launchIn(viewModelScope)
                }
                is UserIntent.ShowUnfilteredItems -> {
                    _dataStateUI.value = DataState.SUCCESS(
                        UserListDataStateUI(
                            lastId = _dataStateUI.value?.data?.lastId ?: 0,
                            isShowUnfilteredList = intent.showUnfilteredItems
                        )
                    )
                }
                is UserIntent.None -> {
                }
            }
        }
    }
}

sealed class UserIntent {
    object GetUserListIntent: UserIntent()
    data class ShowUnfilteredItems(val showUnfilteredItems: Boolean): UserIntent()
    object None : UserIntent()
}

data class UserListDataState(
    var users: List<User>?
)

data class UserListDataStateUI(
    var lastId: Int = 0,
    var isShowUnfilteredList: Boolean = true
)
