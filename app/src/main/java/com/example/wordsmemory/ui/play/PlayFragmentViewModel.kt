package com.example.wordsmemory.ui.play

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.*
import androidx.work.*
import com.example.wordsmemory.BuildConfig
import com.example.wordsmemory.Constants
import com.example.wordsmemory.api.auth.AuthService
import com.example.wordsmemory.database.CloudDbSyncHelper
import com.example.wordsmemory.database.WMDao
import com.example.wordsmemory.model.User
import com.example.wordsmemory.model.vocabulary.VocabularyItem
import com.example.wordsmemory.worker.CloudDbSyncWorker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PlayFragmentViewModel @Inject constructor(
    private val _dbDao: WMDao,
    val signInClient: GoogleSignInClient,
    private var _lastSignedInAccount: GoogleSignInAccount?,
    private val _firestoreDb: FirebaseFirestore,
    private val _workManager: WorkManager
) : ViewModel() {

    private var _categoryId = Constants.defaultCategoryId

    val vocabularyList = _dbDao.getVocabularyItemsAsLiveData()
    val translationText = MutableLiveData<String>()
    val categories = _dbDao.getCategoriesAsLiveData()

    private var _correctAttempts = 0
    val correctAttempts: Int
        get() = _correctAttempts

    private val _isAuthenticated = MutableLiveData(false)
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private val _allAttempts = MutableLiveData(0)
    val allAttempts: LiveData<Int>
        get() = _allAttempts

    private val _vocabularyItem = MutableLiveData<VocabularyItem>()
    val vocabularyItem: LiveData<VocabularyItem>
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
                _vocabularyItem.value = VocabularyItem("", "")
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

    private suspend fun setCategoryId(categoryName: String) {
        return withContext(Dispatchers.IO) {
            _categoryId = _dbDao.getCategoryId(categoryName)
        }
    }

    private fun resetRecentAttempts() {
        _correctAttempts = 0
        _allAttempts.value = 0
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
                            saveUser(User(googleSignInAccount.id!!, accessToken))
                            fetchCloudDbData()
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

    private suspend fun saveUser(user: User) {
        return withContext(Dispatchers.IO) {
            _dbDao.insertUser(user)
            insertCloudDbUser()
        }
    }

    private fun insertCloudDbUser() {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.InsertUser.name))
                .build()
        _workManager.enqueue(workRequest)
    }

    fun signOut() {
        signInClient.signOut()
        _lastSignedInAccount = null
        _isAuthenticated.value = false
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteAllUsers()
        }
    }

    fun checkAuthState(): Boolean {
        if (_lastSignedInAccount != null) {
            fetchCloudDbData()
            _isAuthenticated.value = true
            return true
        }

        return false
    }

    private fun fetchCloudDbData() {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.Fetch.name))
                .build()
        _workManager.enqueue(workRequest)
    }
}