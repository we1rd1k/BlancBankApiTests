package com.blanc.tests.models

data class CategoryRequest(
    var brand: Any?,
    var isVisible: Boolean?,
    var name: Any?
)
data class AddCategoryResponse(
    var action: String? = "ADD",
    var value: CategoryValue?,
)
data class CategoryValue(
    var id: String?,
    var name: String?,
    var brand: String?,
    var isVisible: Boolean?,
    var created: String?,
    var modified: String?
)

data class ErrorResponse(var errorMessage: String?)