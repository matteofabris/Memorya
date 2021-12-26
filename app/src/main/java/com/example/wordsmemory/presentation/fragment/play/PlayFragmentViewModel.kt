package com.example.wordsmemory.presentation.fragment.play

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.Constants
import com.example.wordsmemory.framework.Interactors
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import com.example.wordsmemory.framework.room.entities.UserEntity
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PlayFragmentViewModel @Inject constructor(
    val signInClient: GoogleSignInClient,
    private var _lastSignedInAccount: GoogleSignInAccount?,
    private val _interactors: Interactors
) : ViewModel() {

    private var _categoryId = Constants.defaultCategoryId
    private var _setPlayWordTimer: Timer? = null

    val vocabularyList =
        _interactors.getVocabularyItemsAsLiveData() as LiveData<List<VocabularyItemEntity>>
    val translationText = MutableLiveData<String>()
    val categories =
        _interactors.getNotEmptyCategoriesAsLiveData() as LiveData<List<CategoryEntity>>

    private var _correctAttempts = 0
    val correctAttempts: Int
        get() = _correctAttempts

    private val _isAuthenticated = MutableLiveData(false)
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private val _allAttempts = MutableLiveData(0)
    val allAttempts: LiveData<Int>
        get() = _allAttempts

    private val _vocabularyItem = MutableLiveData<VocabularyItemEntity>()
    val vocabularyItem: LiveData<VocabularyItemEntity>
        get() = _vocabularyItem

    private val _isTranslationOk = MutableLiveData<Boolean>()
    val isTranslationOk: LiveData<Boolean>
        get() = _isTranslationOk

    private val _isLoadingCompleted = MutableLiveData(false)
    val isLoadingCompleted: LiveData<Boolean>
        get() = _isLoadingCompleted

    fun setPlayWord(afterCorrectTranslation: Boolean = false) {
        fun action() {
            try {
                if (vocabularyList.value == null) return

                if (vocabularyList.value!!.isNotEmpty()) {
                    val filteredList = if (_categoryId == Constants.defaultCategoryId) {
                        vocabularyList.value!!
                    } else {
                        vocabularyList.value!!.filter {
                            it.category == _categoryId
                        }
                    }

                    val randomIndex = Random.nextInt(filteredList.size)
                    _vocabularyItem.postValue(filteredList[randomIndex])
                } else {
                    _vocabularyItem.postValue(VocabularyItemEntity("", ""))
                }
                _isLoadingCompleted.postValue(true)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        if (afterCorrectTranslation) {
            action()
        } else {
            val timerTask = object : TimerTask() {
                override fun run() {
                    action()
                }
            }

            _isLoadingCompleted.postValue(false)

            _setPlayWordTimer?.cancel()
            _setPlayWordTimer = Timer()
            _setPlayWordTimer?.schedule(timerTask, 800)
        }
    }

    fun onCheckTranslationButtonClicked() {
        _isTranslationOk.value =
            translationText.value!!.equals(_vocabularyItem.value!!.itWord, ignoreCase = true)

        if (_isTranslationOk.value!!) {
            _correctAttempts++
            setPlayWord()
            translationText.value = ""
        }

        _allAttempts.value = _allAttempts.value?.plus(1)
    }

    fun resetGamePlay(categoryName: String) {
        viewModelScope.launch {
            setCategoryId(categoryName)
            resetRecentAttempts()
            setPlayWord()
        }
    }

    fun manageAuthResult(activityResult: ActivityResult) {
        when (activityResult.resultCode) {
            Activity.RESULT_OK -> {
                val googleSignInAccount =
                    GoogleSignIn.getSignedInAccountFromIntent(activityResult.data).result
                val authCode = googleSignInAccount?.serverAuthCode

                viewModelScope.launch {
                    if (!authCode.isNullOrEmpty()) {
                        val authTokens = _interactors.getAuthTokens(authCode)
                        if (authTokens == null || authTokens.accessToken.isEmpty() || googleSignInAccount.id.isNullOrEmpty()) return@launch

                        _interactors.addUser(
                            UserEntity(
                                googleSignInAccount.id!!,
                                authTokens.accessToken,
                                authTokens.refreshToken
                            )
                        )
                        _interactors.fetchCloudDb()
                        _isAuthenticated.value = true
                    }
                }
            }
            else -> {
                Timber.d("AUTH: authentication failed")
            }
        }
    }

    fun isAuthenticated() = _lastSignedInAccount != null

    fun onAuthenticationOk() {
        _interactors.fetchCloudDb()
        _isAuthenticated.value = true
    }

    fun signOut() {
        signInClient.signOut()
        _lastSignedInAccount = null
        _isAuthenticated.value = false
        viewModelScope.launch(Dispatchers.IO) {
            _interactors.removeAllUsers()
        }
    }

    private suspend fun setCategoryId(categoryName: String) = withContext(Dispatchers.IO) {
        _categoryId = categories.value?.find { c -> c.category == categoryName }?.id!!
    }

    private fun resetRecentAttempts() {
        _correctAttempts = 0
        _allAttempts.value = 0
    }
}