package ns.fajnet.android.geobreadcrumbs.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <T : ViewModel, A> singleArgViewModelFactory(constructor: (A) -> T):
            (A) -> ViewModelProvider.NewInstanceFactory {
    return { arg: A ->
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <V : ViewModel> create(modelClass: Class<V>): V {
                return constructor(arg) as V
            }
        }
    }
}

// TODO_faja: REMOVE, this is not needed, use default NewInstanceFactory when activity/fragment is
//  not implementing HasDefaultViewModelProviderFactory interface
fun <T : ViewModel> noArgViewModelFactory(constructor: () -> T):
            () -> ViewModelProvider.NewInstanceFactory {
    return {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <V : ViewModel?> create(modelClass: Class<V>): V {
                return constructor() as V
            }
        }
    }
}
