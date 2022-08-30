package com.blanc.tests.apis

import com.blanc.tests.common.CATEGORY_URL
import com.blanc.tests.common.PRODUCTS_URL
import com.blanc.tests.common.PRODUCT_URL
import com.blanc.tests.models.AddProductRequest
import com.blanc.tests.models.CategoryRequest
import com.blanc.tests.utils.delete
import com.blanc.tests.utils.get
import com.blanc.tests.utils.patch
import com.blanc.tests.utils.post
import io.qameta.allure.Step

@Step("Добавление категории с параметрами {brand}, {isVisible}, {name}")
fun addCategory(brand: Any?, isVisible: Boolean?, name: Any?) =
    post(CATEGORY_URL, CategoryRequest(brand, isVisible, name))

@Step("Удаление категории с id: {id}")
fun deleteCategory(id: String?) = delete<String>("$CATEGORY_URL/$id")

@Step("Изменение категории с id: {id} и параметрами {brand}, {isVisible}, {name}")
fun adjustCategory(id: String?, brand: Any?, isVisible: Boolean?, name: Any?) =
    patch("$CATEGORY_URL/$id", CategoryRequest(brand, isVisible, name))

@Step("Добавление продукта")
fun addProduct(
    amount: Any?,
    categories: List<CategoryRequest>,
    discount: Any?,
    isVisibleProduct: Boolean?,
    productName: Any?,
    percentDiscount: Any?
) = post(
    PRODUCT_URL,
    AddProductRequest(
        amount = amount,
        categories = categories,
        discount = discount, isVisible = isVisibleProduct, name = productName, percentDiscount = percentDiscount
    )
)

@Step("Получение продукта по id - {id}")
fun getProduct(id: String?) = get<String>("$PRODUCT_URL/$id")

@Step("Удаление продукта по id - {id}")
fun deleteProduct(id: String?) = delete<String>("$PRODUCT_URL/$id")

@Step("Изменение продукта")
fun adjustProduct(
    id: String?,
    amount: Int?,
    categories: List<CategoryRequest>,
    discount: Int?,
    isVisibleProduct: Boolean?,
    productName: Any?,
    percentDiscount: Int?
) = patch(
    "$PRODUCT_URL/$id",
    AddProductRequest(
        amount = amount,
        categories = categories,
        discount = discount, isVisible = isVisibleProduct, name = productName, percentDiscount = percentDiscount
    )
)

@Step("Получение списка продуктов")
fun getProducts() = get<String>(PRODUCTS_URL)