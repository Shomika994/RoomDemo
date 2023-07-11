package com.example.roomdemo

import android.app.Application

class EmployeeApp: Application() {

    val dataBase by lazy {
        EmployeeDatabase.getInstance(this)
    }
}