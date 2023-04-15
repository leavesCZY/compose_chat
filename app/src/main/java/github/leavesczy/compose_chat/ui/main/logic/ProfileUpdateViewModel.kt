package github.leavesczy.compose_chat.ui.main.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.utils.CompressImageUtils
import github.leavesczy.compose_chat.utils.ContextHolder
import github.leavesczy.compose_chat.utils.showToast
import github.leavesczy.matisse.MediaResource
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ProfileUpdateViewModel : ViewModel() {

    var profileUpdatePageViewStata by mutableStateOf<ProfileUpdatePageViewStata?>(
        value = null
    )
        private set

    init {
        viewModelScope.launch {
            val profile = ComposeChat.accountProvider.getPersonProfile()
            profileUpdatePageViewStata = if (profile == null) {
                null
            } else {
                ProfileUpdatePageViewStata(personProfile = profile)
            }
        }
    }

    fun onNicknameChanged(nickname: String) {
        val mProfileUpdatePageViewStata = profileUpdatePageViewStata
        if (mProfileUpdatePageViewStata != null) {
            profileUpdatePageViewStata = mProfileUpdatePageViewStata.copy(
                personProfile = mProfileUpdatePageViewStata.personProfile.copy(
                    nickname = nickname
                )
            )
        }
    }

    fun onSignatureChanged(signature: String) {
        val mProfileUpdatePageViewStata = profileUpdatePageViewStata
        if (mProfileUpdatePageViewStata != null) {
            profileUpdatePageViewStata = mProfileUpdatePageViewStata.copy(
                personProfile = mProfileUpdatePageViewStata.personProfile.copy(
                    signature = signature
                )
            )
        }
    }

    fun onFaceUrlChanged(imageUrl: String) {
        val mProfileUpdatePageViewStata = profileUpdatePageViewStata
        if (mProfileUpdatePageViewStata != null) {
            profileUpdatePageViewStata = mProfileUpdatePageViewStata.copy(
                personProfile = mProfileUpdatePageViewStata.personProfile.copy(
                    faceUrl = imageUrl
                )
            )
        }
    }

    fun onFaceUrlChanged(mediaResource: MediaResource) {
        viewModelScope.launch {
            val imageFile = CompressImageUtils.compressImage(
                context = ContextHolder.context, mediaResource = mediaResource
            )
            val imagePath = imageFile?.absolutePath
            if (!imagePath.isNullOrBlank()) {
                val result = ComposeChat.messageProvider.uploadImage(
                    chat = Chat.GroupChat(id = ComposeChat.groupIdToUploadAvatar),
                    imagePath = imagePath
                )
                if (result.isBlank()) {
                    showToast(msg = "图片上传失败")
                } else {
                    showToast(msg = "图片已上传")
                    val mProfileUpdatePageViewStata = profileUpdatePageViewStata
                    if (mProfileUpdatePageViewStata != null) {
                        profileUpdatePageViewStata = mProfileUpdatePageViewStata.copy(
                            personProfile = mProfileUpdatePageViewStata.personProfile.copy(
                                faceUrl = result
                            )
                        )
                    }
                }
            }
        }
    }

    fun confirmUpdate() {
        viewModelScope.launch {
            val mProfileUpdatePageViewStata = profileUpdatePageViewStata
            if (mProfileUpdatePageViewStata != null) {
                val profile = mProfileUpdatePageViewStata.personProfile
                val result = ComposeChat.accountProvider.updatePersonProfile(
                    faceUrl = profile.faceUrl,
                    nickname = profile.nickname,
                    signature = profile.signature
                )
                when (result) {
                    is ActionResult.Success -> {
                        showToast(msg = "更新成功")
                    }
                    is ActionResult.Failed -> {
                        showToast(msg = result.reason)
                    }
                }
            }
        }
    }

}