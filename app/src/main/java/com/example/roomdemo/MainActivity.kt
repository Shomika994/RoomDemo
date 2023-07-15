package com.example.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdemo.databinding.ActivityMainBinding
import com.example.roomdemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        val employeeDao = (application as EmployeeApp).dataBase.employeeDao()

        binding?.button?.setOnClickListener {
            addUsers(employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect {
                val list = ArrayList(it)
                setupListOfItemIntoRecycleView(list, employeeDao)
            }
        }

    }

    private fun setupListOfItemIntoRecycleView(employeeList: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao) {

        if (employeeList.isNotEmpty()) {
            val itemAdapter =
                ItemAdapter(employeeList, { updateId -> updateRecordDialog(updateId, employeeDao) })
                { deleteId ->
                    lifecycleScope.launch {
                            employeeDao.fetchEmployeeById(deleteId).collect {
                                deleteRecordDialog(deleteId, employeeDao, it)
                            }
                        }
                }
            binding?.recycleView?.layoutManager = LinearLayoutManager(this)
            binding?.recycleView?.adapter = itemAdapter
            binding?.recycleView?.visibility = View.VISIBLE
            binding?.textViewNoRecordsAvailable?.visibility = View.GONE
        } else {
            binding?.recycleView?.visibility = View.GONE
            binding?.textViewNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

    private fun addUsers(employeeDao: EmployeeDao) {

        val name = binding?.editName?.text.toString()
        val email = binding?.editEmail?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()) {
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_SHORT).show()
                binding?.editName?.text?.clear()
                binding?.editEmail?.text?.clear()
            }
        } else {
            Toast.makeText(applicationContext, "Name or Email cannot be blank", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao) {
        val updateDialog = Dialog(this, androidx.appcompat.R.style.Base_Theme_AppCompat_Light)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                if(it != null){
                    binding.editTextUpdateName.setText(it.name)
                    binding.editTextUpdateEmail.setText(it.email)
                }
            }
        }
        binding.textViewUpdate.setOnClickListener {
            val name = binding.editTextUpdateName.text.toString()
            val email = binding.editTextUpdateEmail.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(applicationContext, "Record updated", Toast.LENGTH_SHORT).show()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.textViewCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    private fun deleteRecordDialog(id: Int, employeeDao: EmployeeDao, employeeEntity: EmployeeEntity) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete record")

        builder.setMessage("Do you want to delete this ${employeeEntity.name}?")

        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
                lifecycleScope.launch {
                    employeeDao.delete(EmployeeEntity(id))
                    Toast.makeText(applicationContext, "Record deleted successfully.", Toast.LENGTH_LONG).show()
                    dialogInterface.dismiss()
                }
            }
            builder.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

            val createDialog: AlertDialog = builder.create()
            createDialog.setCancelable(false)
            createDialog.show()
        }
    }

