package com.example.roomdemo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.databinding.ItemViewBinding

class ItemAdapter(private val item: ArrayList<EmployeeEntity>,
                  private val update:(id: Int) -> Unit,
                 private val delete:(id: Int) -> Unit
    ): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


    class ViewHolder(binding: ItemViewBinding): RecyclerView.ViewHolder(binding.root){

        val linearLayout = binding.linearLayoutMain
        val textViewName = binding.textViewName
        val textViewEmail = binding.textViewEmail
        val imageViewEdit = binding.imageViewEdit
        val imageViewDelete = binding.imageViewDelete

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = item[position]

        holder.textViewName.text = item.name
        holder.textViewEmail.text = item.email

        if(position % 2 == 0){
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGray))
        } else{
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        holder.imageViewEdit.setOnClickListener{
            update.invoke(item.id)
        }

        holder.imageViewDelete.setOnClickListener{
            delete.invoke(item.id)
        }
    }
}