package com.summerveldhoundresort.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AuthViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory{
    override fun <T2 : ViewModel> create(modelClass: Class<T2>): T2{
        return creator() as T2
    }
}
