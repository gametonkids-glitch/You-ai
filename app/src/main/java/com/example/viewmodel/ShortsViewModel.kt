package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiGeneratedShortPlan
import com.example.ai.GeminiShortsGenerator
import com.example.data.AppDatabase
import com.example.model.PrivacyStatus
import com.example.model.ScheduledShort
import com.example.model.UploadStatus
import com.example.model.VideoMetadataInfo
import com.example.util.TimeFormatUtil
import com.example.util.VideoThumbnailUtil
import com.example.youtube.UploadResult
import com.example.youtube.YouTubePreferences
import com.example.youtube.YouTubeUploader
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ShortsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val shortDao = db.shortDao()
    private val uploader = YouTubeUploader(application)
    val preferences = YouTubePreferences(application)

    // Observable DB streams
    val activeQueue: StateFlow<List<ScheduledShort>> = shortDao.getActiveQueue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedHistory: StateFlow<List<ScheduledShort>> = shortDao.getPublishedHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Creation Form State
    private val _selectedVideoUri = MutableStateFlow<Uri?>(null)
    val selectedVideoUri = _selectedVideoUri.asStateFlow()

    private val _videoThumbnailPath = MutableStateFlow<String?>(null)
    val videoThumbnailPath = _videoThumbnailPath.asStateFlow()

    private val _videoMetadata = MutableStateFlow(VideoMetadataInfo())
    val videoMetadata = _videoMetadata.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _caption = MutableStateFlow("")
    val caption = _caption.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _tags = MutableStateFlow(preferences.defaultTags)
    val tags = _tags.asStateFlow()

    private val _categoryId = MutableStateFlow("24")
    val categoryId = _categoryId.asStateFlow()

    private val _categoryName = MutableStateFlow("Entertainment")
    val categoryName = _categoryName.asStateFlow()

    private val _privacyStatus = MutableStateFlow(PrivacyStatus.PUBLIC)
    val privacyStatus = _privacyStatus.asStateFlow()

    private val _isPublishImmediately = MutableStateFlow(false)
    val isPublishImmediately = _isPublishImmediately.asStateFlow()

    private val _scheduledEpochMs = MutableStateFlow(TimeFormatUtil.getQuickPreset(TimeFormatUtil.PresetType.PLUS_ONE_HOUR))
    val scheduledEpochMs = _scheduledEpochMs.asStateFlow()

    private val _isProcessingVideo = MutableStateFlow(false)
    val isProcessingVideo = _isProcessingVideo.asStateFlow()

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi = _isGeneratingAi.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    // YouTube Connection & Login States
    private val _isLoggedIn = MutableStateFlow(preferences.isLoggedIn)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _channelTitle = MutableStateFlow(preferences.channelTitle)
    val channelTitle = _channelTitle.asStateFlow()

    private val _channelHandle = MutableStateFlow(preferences.channelHandle)
    val channelHandle = _channelHandle.asStateFlow()

    private val _channelSubscribers = MutableStateFlow(preferences.channelSubscriberCount)
    val channelSubscribers = _channelSubscribers.asStateFlow()

    private val _userEmail = MutableStateFlow(preferences.userEmail)
    val userEmail = _userEmail.asStateFlow()

    // Global active upload state for top progress bar banner
    private val _activeUploadingId = MutableStateFlow<Long?>(null)
    val activeUploadingId = _activeUploadingId.asStateFlow()

    private val _activeUploadProgress = MutableStateFlow(0)
    val activeUploadProgress = _activeUploadProgress.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage = _statusBannerMessage.asStateFlow()

    private var queueTickerJob: Job? = null

    init {
        startQueueSchedulerTicker()
    }

    private fun startQueueSchedulerTicker() {
        queueTickerJob?.cancel()
        queueTickerJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val now = System.currentTimeMillis()
                    val dueShorts = shortDao.getPendingDueShorts(now)
                    for (due in dueShorts) {
                        if (_activeUploadingId.value == null) {
                            executeUpload(due)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(15_000)
            }
        }
    }

    fun onVideoSelected(uri: Uri) {
        _selectedVideoUri.value = uri
        _isProcessingVideo.value = true
        _isGeneratingAi.value = true

        viewModelScope.launch {
            val (thumbPath, metadata) = VideoThumbnailUtil.extractThumbnailAndMetadata(
                getApplication(),
                uri
            )
            _videoThumbnailPath.value = thumbPath
            _videoMetadata.value = metadata
            _isProcessingVideo.value = false

            // Automatically generate Title, Caption, Description, and Tags with AI!
            val plan = GeminiShortsGenerator.autoGenerateMetadata(
                videoHint = "Short vertical video ${metadata.durationSeconds}s",
                category = _categoryName.value
            )
            applyAiPlan(plan)
            _isGeneratingAi.value = false
            _statusBannerMessage.value = "✨ AI Automatically Filled Title, Description & Tags!"
        }
    }

    fun autoGenerateAiMetadata(keyword: String = "") {
        _isGeneratingAi.value = true
        viewModelScope.launch {
            val plan = GeminiShortsGenerator.autoGenerateMetadata(
                videoHint = keyword.ifBlank { _title.value },
                category = _categoryName.value
            )
            applyAiPlan(plan)
            _isGeneratingAi.value = false
            _statusBannerMessage.value = "✨ AI Generated Fresh Title, Description & Tags!"
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun appendShortsHashtag() {
        if (!_title.value.contains("#Shorts", ignoreCase = true)) {
            _title.value = (_title.value.trim() + " #Shorts").trim()
        }
    }

    fun updateCaption(newCaption: String) {
        _caption.value = newCaption
    }

    fun updateDescription(newDesc: String) {
        _description.value = newDesc
    }

    fun updateTags(newTags: String) {
        _tags.value = newTags
    }

    fun updateCategory(id: String, name: String) {
        _categoryId.value = id
        _categoryName.value = name
    }

    fun updatePrivacy(privacy: PrivacyStatus) {
        _privacyStatus.value = privacy
    }

    fun setPublishImmediately(immediate: Boolean) {
        _isPublishImmediately.value = immediate
    }

    fun setScheduledTime(epochMs: Long) {
        _scheduledEpochMs.value = epochMs
    }

    fun applyAiPlan(plan: AiGeneratedShortPlan) {
        _title.value = plan.title
        _caption.value = plan.caption
        _description.value = plan.description
        _tags.value = plan.tags
        _statusBannerMessage.value = plan.recommendedTimeNotice
    }

    fun dismissBannerMessage() {
        _statusBannerMessage.value = null
    }

    fun saveDraft(onSaved: () -> Unit) {
        val uri = _selectedVideoUri.value?.toString() ?: ""
        if (uri.isBlank()) {
            _statusBannerMessage.value = "Please select a video first"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            val short = ScheduledShort(
                videoUri = uri,
                thumbnailPath = _videoThumbnailPath.value,
                title = _title.value.ifBlank { "Untitled Short" },
                caption = _caption.value,
                description = _description.value,
                tags = _tags.value,
                categoryId = _categoryId.value,
                categoryName = _categoryName.value,
                privacyStatus = _privacyStatus.value.name,
                scheduledTimeEpochMs = _scheduledEpochMs.value,
                isPublishImmediately = _isPublishImmediately.value,
                status = UploadStatus.DRAFT.name,
                durationSeconds = _videoMetadata.value.durationSeconds
            )
            shortDao.insertShort(short)
            _isSubmitting.value = false
            clearForm()
            onSaved()
        }
    }

    fun scheduleOrUpload(onComplete: () -> Unit) {
        val uri = _selectedVideoUri.value?.toString() ?: ""
        if (uri.isBlank()) {
            _statusBannerMessage.value = "Please select a video first"
            return
        }
        if (_title.value.isBlank()) {
            _statusBannerMessage.value = "Please enter a title for the Short"
            return
        }

        val immediate = _isPublishImmediately.value
        val scheduledTime = if (immediate) System.currentTimeMillis() else _scheduledEpochMs.value

        viewModelScope.launch {
            _isSubmitting.value = true
            val short = ScheduledShort(
                videoUri = uri,
                thumbnailPath = _videoThumbnailPath.value,
                title = _title.value.trim(),
                caption = _caption.value.trim(),
                description = _description.value.trim(),
                tags = _tags.value.trim(),
                categoryId = _categoryId.value,
                categoryName = _categoryName.value,
                privacyStatus = _privacyStatus.value.name,
                scheduledTimeEpochMs = scheduledTime,
                isPublishImmediately = immediate,
                status = if (immediate) UploadStatus.UPLOADING.name else UploadStatus.SCHEDULED.name,
                durationSeconds = _videoMetadata.value.durationSeconds
            )

            val newId = shortDao.insertShort(short)
            val insertedShort = short.copy(id = newId)

            _isSubmitting.value = false
            clearForm()
            onComplete()

            if (immediate) {
                executeUpload(insertedShort)
            } else {
                _statusBannerMessage.value = "Short scheduled successfully for ${TimeFormatUtil.formatRemainingTime(scheduledTime)}!"
            }
        }
    }

    fun triggerUploadNow(short: ScheduledShort) {
        viewModelScope.launch {
            executeUpload(short.copy(isPublishImmediately = true))
        }
    }

    private suspend fun executeUpload(short: ScheduledShort) {
        _activeUploadingId.value = short.id
        _activeUploadProgress.value = 5
        shortDao.updateStatusAndProgress(short.id, UploadStatus.UPLOADING.name, 5)

        val result = uploader.uploadShort(short) { progress ->
            _activeUploadProgress.value = progress
            shortDao.updateStatusAndProgress(short.id, UploadStatus.UPLOADING.name, progress)
        }

        when (result) {
            is UploadResult.Success -> {
                shortDao.markPublished(short.id, result.youtubeVideoId, result.videoUrl)
                _statusBannerMessage.value = "Uploaded to ${_channelTitle.value}: ${short.title}"
            }
            is UploadResult.Error -> {
                shortDao.updateStatusAndProgress(short.id, UploadStatus.FAILED.name, 0, result.message)
                _statusBannerMessage.value = "Upload error: ${result.message}"
            }
        }

        _activeUploadingId.value = null
        _activeUploadProgress.value = 0
    }

    fun deleteShort(short: ScheduledShort) {
        viewModelScope.launch {
            shortDao.deleteShort(short)
        }
    }

    private fun clearForm() {
        _selectedVideoUri.value = null
        _videoThumbnailPath.value = null
        _videoMetadata.value = VideoMetadataInfo()
        _title.value = ""
        _caption.value = ""
        _description.value = ""
        _tags.value = preferences.defaultTags
        _isPublishImmediately.value = false
        _scheduledEpochMs.value = TimeFormatUtil.getQuickPreset(TimeFormatUtil.PresetType.PLUS_ONE_HOUR)
    }

    fun loginChannel(
        email: String,
        channelName: String,
        handle: String,
        subscribers: String,
        token: String = ""
    ) {
        preferences.login(email, channelName, handle, subscribers, token)
        _isLoggedIn.value = true
        _userEmail.value = email
        _channelTitle.value = channelName
        _channelHandle.value = handle
        _channelSubscribers.value = subscribers
        _statusBannerMessage.value = "Connected to YouTube Channel: $channelName"
    }

    fun logoutChannel() {
        preferences.logout()
        _isLoggedIn.value = false
        _userEmail.value = ""
        _channelTitle.value = "Disconnected"
        _channelHandle.value = ""
        _channelSubscribers.value = ""
        _statusBannerMessage.value = "YouTube Channel disconnected"
    }

    fun saveYouTubeSettings(
        token: String,
        isSimMode: Boolean,
        channelName: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        preferences.accessToken = token
        preferences.isSimulatorMode = isSimMode
        if (channelName.isNotBlank()) {
            preferences.channelTitle = channelName
            _channelTitle.value = channelName
        }

        if (token.isNotBlank()) {
            viewModelScope.launch {
                val check = uploader.verifyChannel(token)
                check.onSuccess { (title, id) ->
                    preferences.channelTitle = title
                    preferences.channelId = id
                    preferences.isSimulatorMode = false
                    _channelTitle.value = title
                    _isLoggedIn.value = true
                    onComplete(true, "Connected to YouTube Channel: $title")
                }.onFailure { err ->
                    onComplete(false, err.localizedMessage ?: "Failed to verify token")
                }
            }
        } else {
            _isLoggedIn.value = true
            onComplete(true, "Settings updated in Simulator/Offline Mode")
        }
    }
}
