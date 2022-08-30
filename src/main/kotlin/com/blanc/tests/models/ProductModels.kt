package com.blanc.tests.models

data class AddProductRequest(
    var amount: Any?,
    var categories: List<CategoryRequest>?,
    var discount: Any?,
    var isVisible: Boolean?,
    var name: Any?,
    var percentDiscount: Any?
)

data class Product(
    var id: String?,
    var productName: String?,
    var categories: List<CategoryValue>?,
    var amount: Int?,
    var discount: Int?,
    var percentDiscount: Int?,
    var isVisible: Boolean?,
    var created: String?,
    var modified: String?
)


data class ProductResponse(
    var action: String? = "ADD",
    var values: List<Product>
)

data class GetProductResponse(
    var value: Product
)

data class UpdateDeleteProductResponse(
    var action: String? = "REMOVE",
    var value: Product

)

data class ProductsListResponse(
    var values: List<Product>
)
