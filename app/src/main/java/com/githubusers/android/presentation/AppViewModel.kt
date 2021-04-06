package com.githubusers.android.presentation

import android.content.Context
import androidx.lifecycle.*
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.presentation.util.ConnectionLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AppViewModel
@Inject
constructor(
    @ApplicationContext private val appCtx: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dataState: MutableLiveData<DataState<MainDataState>> = MutableLiveData()
    private val _connectionLiveData = ConnectionLiveData(appCtx)

    val dataState: LiveData<DataState<MainDataState>>
        get () = _dataState

    fun setStateEvent(intent: MainIntent) {
        viewModelScope.launch {
            when (intent) {
                is MainIntent.InitHomeIntent -> {
                    _dataState.value = DataState.SUCCESS(
                        // TODO Setup any initialization config here
                        MainDataState(
                            true,
                            null
                        )
                    )
                }
                is MainIntent.None -> {
                }
            }
        }
    }

    fun registerConnectionObserver(lifecycleOwner: LifecycleOwner){
        _connectionLiveData.observe(owner = lifecycleOwner, onChanged = { isConnected ->
            isConnected.let {
                val newDataState = MainDataState(
                    _dataState.value?.data?.isHomeLoaded, it
                )
                _dataState.value = DataState.SUCCESS(newDataState)
            }
        })
    }

    fun unregisterConnectionObserver(lifecycleOwner: LifecycleOwner){
        _connectionLiveData.removeObservers(lifecycleOwner)
    }
}

sealed class MainIntent {
    object InitHomeIntent: MainIntent()
    object None : MainIntent()
}

data class MainDataState(
    var isHomeLoaded: Boolean?,
    var hasNetwork: Boolean?
)

