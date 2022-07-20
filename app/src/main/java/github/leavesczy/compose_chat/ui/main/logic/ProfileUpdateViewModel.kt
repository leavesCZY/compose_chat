package github.leavesczy.compose_chat.ui.main.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.GroupChat
import github.leavesczy.compose_chat.common.model.PersonProfile
import github.leavesczy.compose_chat.model.ProfileUpdatePageViewStata
import github.leavesczy.compose_chat.utils.CompressImageUtils
import github.leavesczy.compose_chat.utils.ContextHolder
import github.leavesczy.compose_chat.utils.showToast
import github.leavesczy.matisse.MediaResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Author: CZY
 * @Date: 2022/7/17 17:38
 * @Desc:
 */
class ProfileUpdateViewModel : ViewModel() {

    private val _profileUpdatePageViewStata =
        MutableStateFlow(ProfileUpdatePageViewStata(personProfile = PersonProfile.Empty))

    val profileUpdatePageViewStata: StateFlow<ProfileUpdatePageViewStata> =
        _profileUpdatePageViewStata

    init {
        viewModelScope.launch {
            ComposeChat.accountProvider.personProfile.collect {
                _profileUpdatePageViewStata.emit(
                    value = _profileUpdatePageViewStata.value.copy(
                        personProfile = it
                    )
                )
            }
        }
    }

    fun updateProfile(faceUrl: String, nickname: String, signature: String) {
        viewModelScope.launch {
            val result = ComposeChat.accountProvider.updatePersonProfile(
                faceUrl = faceUrl,
                nickname = nickname,
                signature = signature
            )
            when (result) {
                is ActionResult.Success -> {
                    showToast("更新成功")
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    suspend fun uploadImage(mediaResources: MediaResources): String {
        return withContext(Dispatchers.IO) {
            val imageFile = CompressImageUtils.compressImage(
                context = ContextHolder.context,
                mediaResources = mediaResources
            )
            val imagePath = imageFile?.absolutePath
            if (!imagePath.isNullOrBlank()) {
                return@withContext ComposeChat.messageProvider.uploadImage(
                    chat = GroupChat(id = ComposeChat.groupIdToUploadAvatar),
                    imagePath = imagePath
                )
            }
            return@withContext ""
        }
    }

}