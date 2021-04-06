package com.githubusers.android.presentation.details

import android.content.Context
import androidx.lifecycle.*
import com.githubusers.android.R
import com.githubusers.android.domain.model.User
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.domain.state.StateMessage
import com.githubusers.android.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class UserDetailsViewModel
@Inject
constructor(
    @ApplicationContext private val appCtx: Context,
    private val mainRepository: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dataState: MutableLiveData<DataState<UserDetailsDataState>> = MutableLiveData()

    val dataState: LiveData<DataState<UserDetailsDataState>>
        get () = _dataState

    fun setStateEvent(intent: UserDetailsIntent) {
        viewModelScope.launch {
            when (intent) {

                is UserDetailsIntent.GetUserDetails -> {

                    mainRepository.getUser(intent.username)
                        .onEach { dataState ->
                            when (dataState) {
                                is DataState.LOADING -> {
                                    _dataState.value = DataState.LOADING(dataState.loading)
                                }
                                is DataState.ERROR -> {
                                    val errorTxt = appCtx.getString(R.string.error_message)
                                    val stateMsg = StateMessage(errorTxt)
                                    _dataState.value = DataState.ERROR(
                                        stateMsg,
                                        dataState.errorType!!,
                                        UserDetailsDataState(dataState.data)
                                    )
                                }
                                is DataState.SUCCESS -> {
                                    val newDataState = UserDetailsDataState(dataState.data)
                                    _dataState.value = DataState.SUCCESS(newDataState)
                                }
                            }
                        }
                        .launchIn(viewModelScope)
                }
                is UserDetailsIntent.SaveUserNote -> {

                    mainRepository.saveNote(intent.username, intent.note)
                        .onEach { dataState ->
                            if (dataState is DataState.SUCCESS) {
                                val newDataState = UserDetailsDataState(dataState.data)
                                _dataState.value = DataState.SUCCESS(newDataState)
                            }
                        }
                        .launchIn(viewModelScope)
                }

                is UserDetailsIntent.None -> {
                }
            }
        }
    }
}

sealed class UserDetailsIntent {

    data class GetUserDetails(
        val username: String
    ): UserDetailsIntent()

    data class SaveUserNote(
        val username: String,
        val note: String
    ): UserDetailsIntent()

    object None : UserDetailsIntent()
}

data class UserDetailsDataState(
    var user: User?
)

