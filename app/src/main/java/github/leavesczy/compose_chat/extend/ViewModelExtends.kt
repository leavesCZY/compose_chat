package github.leavesczy.compose_chat.extend

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * @Author: leavesCZY
 * @Date: 2021/10/29 17:40
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
inline fun <reified T : ViewModel> viewModelInstance(crossinline create: () -> T): T {
    return viewModel(factory = object :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create() as T
        }
    })
}

@MainThread
inline fun <reified T : ViewModel> ComponentActivity.viewModelsInstance(crossinline create: () -> T): Lazy<T> {
    return viewModels(factoryProducer = {
        object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return create() as T
            }
        }
    })
}
