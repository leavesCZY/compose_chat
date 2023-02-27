package github.leavesczy.compose_chat.ui.main.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.PersonProfile
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class PersonProfileViewModel : ViewModel() {

    var personProfilePageViewState by mutableStateOf(
        value = PersonProfilePageViewState(personProfile = PersonProfile.Empty)
    )
        private set

    init {
        viewModelScope.launch {
            ComposeChat.accountProvider.personProfile.collect {
                personProfilePageViewState = personProfilePageViewState.copy(personProfile = it)
            }
        }
    }

}