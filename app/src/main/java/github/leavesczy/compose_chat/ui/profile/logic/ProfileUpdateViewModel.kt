package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.provider.ContextProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import github.leavesczy.compose_chat.utils.CompressImageUtils
import github.leavesczy.matisse.MediaResource
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ProfileUpdateViewModel : BaseViewModel() {

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

    fun onAvatarUrlChanged(imageUrl: String) {
        val mProfileUpdatePageViewStata = profileUpdatePageViewStata
        if (mProfileUpdatePageViewStata != null) {
            profileUpdatePageViewStata = mProfileUpdatePageViewStata.copy(
                personProfile = mProfileUpdatePageViewStata.personProfile.copy(
                    faceUrl = imageUrl
                )
            )
        }
    }

    fun onAvatarUrlChanged(mediaResource: MediaResource) {
        viewModelScope.launch {
            showToast(msg = "正在上传图片")
            val imageFile = CompressImageUtils.compressImage(
                context = ContextProvider.context,
                mediaResource = mediaResource
            )
            val imagePath = imageFile?.absolutePath
            val result = if (imagePath.isNullOrBlank()) {
                null
            } else {
                ComposeChat.messageProvider.uploadImage(
                    chat = Chat.GroupChat(id = ComposeChat.groupIdToUploadAvatar),
                    imagePath = imagePath
                )
            }
            if (result.isNullOrBlank()) {
                showToast(msg = "图片上传失败")
            } else {
                showToast(msg = "图片上传成功")
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