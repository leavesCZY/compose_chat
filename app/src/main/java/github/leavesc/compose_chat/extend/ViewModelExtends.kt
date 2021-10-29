package github.leavesc.compose_chat.extend

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * @Author: leavesC
 * @Date: 2021/10/29 17:40
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
inline fun <reified VM : ViewModel> viewModelInstance(crossinline create: () -> VM): VM =
    viewModel(factory = object :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create() as T
        }
    })