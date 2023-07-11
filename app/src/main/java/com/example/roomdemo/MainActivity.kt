package com.example.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.namespace.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        val employeeDao = (application as EmployeeApp).dataBase.employeeDao()

        binding?.button?.setOnClickListener{

            addUsers(employeeDao)
        }



    }

    private fun addUsers(employeeDao: EmployeeDao){

        val name = binding?.editName?.text.toString()
        val email = binding?.editEmail?.text.toString()

        if(name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_SHORT).show()
                binding?.editName?.text?.clear()
                binding?.editEmail?.text?.clear()
            }
        } else {
            Toast.makeText(applicationContext, "Name or Email cannot be blank", Toast.LENGTH_LONG).show()
        }
    }
}