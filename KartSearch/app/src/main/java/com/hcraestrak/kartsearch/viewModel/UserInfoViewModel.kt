package com.hcraestrak.kartsearch.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserInfoViewModel: ViewModel() {

    val mode = MutableLiveData<String>()

}

