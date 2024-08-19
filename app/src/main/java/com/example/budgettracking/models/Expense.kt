package com.example.budgettracking.models

data class Expense(
    var id: String? = null,
    var userId: String? = null,
    var initialBalance: Double? = null,
    var addBalance: Double? = null,
    var expenseAmount: Double? = null,
    var expenseDescription: String? = null,
    var category: String? = null
) {
    constructor() : this(null, null, null, null, null, null, null)
}
