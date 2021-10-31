package com.example.wordsmemory.presentation.play

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.*
import androidx.work.*
import com.example.wordsmemory.BuildConfig
import com.example.wordsmemory.Constants
import com.example.wordsmemory.api.auth.AuthService
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
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PlayFragmentViewModel @Inject constructor(
    val signInClient: GoogleSignInClient,
    private var _lastSignedInAccount: GoogleSignInAccount?,
    private val _interactors: Interactors
) : ViewModel() {

    private var _categoryId = Constants.defaultCategoryId

    val vocabularyList = _interactors.getVocabularyItems() as LiveData<List<VocabularyItemEntity>>
    val translationText = MutableLiveData<String>()
    val categories = _interactors.getCategories() as LiveData<List<CategoryEntity>>

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
    val vocabularyItemEntity: LiveData<VocabularyItemEntity>
        get() = _vocabularyItem

    private val _isTranslationOk = MutableLiveData<Boolean>()
    val isTranslationOk: LiveData<Boolean>
        get() = _isTranslationOk

    fun setPlayWord() {
        if (vocabularyList.value != null) {
            if (vocabularyList.value!!.isNotEmpty()) {
                val filteredList = if (_categoryId == Constants.defaultCategoryId) {
                    vocabularyList.value!!
                } else {
                    vocabularyList.value!!.filter {
                        it.category == _categoryId
                    }
                }

                val randomIndex = Random.nextInt(filteredList.size)
                _vocabularyItem.value = filteredList[randomIndex]
            } else {
                _vocabularyItem.value = VocabularyItemEntity("", "")
            }
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
                        val accessToken = getAccessToken(authCode)
                        if (accessToken.isNotEmpty() && !googleSignInAccount.id.isNullOrEmpty()) {
                            _interactors.addUser(UserEntity(googleSignInAccount.id!!, accessToken))
                            _interactors.fetchCloudDb()
                            _isAuthenticated.value = true
                        }
                    }
                }
            }
            else -> {
                Log.d("AUTH", "AUTH: authentication failed")
            }
        }
    }

    fun isAuthenticated() = _lastSignedInAccount != null

    fun onAuthenticationOk() {
        _isAuthenticated.value = true
        _interactors.fetchCloudDb()
    }

    fun signOut() {
        signInClient.signOut()
        _lastSignedInAccount = null
        _isAuthenticated.value = false
        viewModelScope.launch(Dispatchers.IO) {
            _interactors.removeAllUsers()
        }
    }

    private suspend fun setCategoryId(categoryName: String) {
        return withContext(Dispatchers.IO) {
            _categoryId = categories.value?.find { c -> c.category == categoryName }?.id!!
        }
    }

    private fun resetRecentAttempts() {
        _correctAttempts = 0
        _allAttempts.value = 0
    }

    private suspend fun getAccessToken(authCode: String): String {
        return withContext(Dispatchers.IO) {
            val authResult = AuthService.create().auth(
                Constants.webClientId,
                BuildConfig.CLIENT_SECRET,
                authCode
            )
            if (authResult.isSuccessful) {
                return@withContext authResult.body()?.accessToken ?: ""
            } else return@withContext ""
        }
    }
}