package com.example.budgettracking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracking.R
import com.example.budgettracking.models.Expense

class ExpenseAdapter(
    private val expenseList: MutableList<Expense>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(expense: Expense)
        fun onDeleteClick(expense: Expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.bind(expense)
    }

    override fun getItemCount(): Int = expenseList.size

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        private val editImageView: ImageView = itemView.findViewById(R.id.edit)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.delete)

        fun bind(expense: Expense) {
            amountTextView.text = expense.expenseAmount.toString()
            descriptionTextView.text = expense.expenseDescription
            categoryTextView.text = expense.category

            editImageView.setOnClickListener {
                listener.onEditClick(expense)
            }

            deleteImageView.setOnClickListener {
                listener.onDeleteClick(expense)
            }
        }
    }
}
