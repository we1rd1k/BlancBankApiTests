package com.blanc.tests

import com.blanc.tests.apis.*
import com.blanc.tests.assertions.ANY_UUID
import com.blanc.tests.assertions.CURRENT_DATE
import com.blanc.tests.assertions.andVerifyResponseIs
import com.blanc.tests.assertions.andVerifyResponseIsNotEmpty
import com.blanc.tests.models.*
import com.blanc.tests.utils.randomizer
import io.qameta.allure.Description
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.MethodSource

@Tag("API")
@Feature("API")
@Severity(SeverityLevel.CRITICAL)
class ProductTests {

    private var ids = mutableListOf<String>()

    @AfterEach
    fun tearDown() {
        ids.forEach { deleteProduct(it) }

    }

    @Description("Позитивный тест добавления продукта - категория добавляется вместе с продуктом")
    @DisplayName("Позитивный тест добавления продукта - категория добавляется вместе с продуктом")
    @Test
    fun `Add Product - positive test`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        ids.add(addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse(
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0].id.toString()
        )
    }

    @Description("Позитивный тест добавления продукта - категория была добавлена отдельно")
    @DisplayName("Позитивный тест добавления продукта - категория была добавлена отдельно")
    @Test
    fun `Add Product - positive test - category already exist`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        ids.add(addCategory(brand, true, categoryName)
            .andVerifyResponseIs(
                201, AddCategoryResponse(
                    value = CategoryValue(
                        id = ANY_UUID,
                        name = categoryName,
                        brand = brand,
                        isVisible = true,
                        created = CURRENT_DATE,
                        modified = CURRENT_DATE
                    )
                )
            ).value!!.id.toString()
        )
        addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse(
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ids[0],
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            )
    }

    @Description("Позитивный тест добавления нескольких продуктов в одну категорию")
    @DisplayName("Позитивный тест добавления нескольких продуктов в одну категорию")
    @Test
    fun `Add Product - positive test - add another product with the same category`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        ids.add(addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse(
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0].id.toString()
        )
        val newProductName = randomizer().book.title()
        ids.add(addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = newProductName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse(
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = newProductName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0].id.toString()
        )
    }

    @Description("Позитивный тест добавления уже существующего в категории продукта")
    @DisplayName("Позитивный тест добавления уже существующего в категории продукта")
    @Test
    fun `Add Product - negative test - add same product with the same category`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        ids.add(addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse(
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0].id.toString()
        )
        addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                200, ErrorResponse("$productName in that category already exist")
            )
    }

    @Description("Валидационный тест - добавление продукта")
    @DisplayName("Проверяем контракты")
    @MethodSource("validationData")
    @ParameterizedTest
    fun `Add Product - negative test - validation tests`(amount: Any?, discount: Any?, percentDiscount: Any?) {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        addProduct(
            amount = amount,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = discount,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = percentDiscount
        )
            .andVerifyResponseIsNotEmpty(400)
    }

    @Description("Попытка удаления категории с привязанным к ней продуктом")
    @DisplayName("Попытка удаления категории с привязанным к ней продуктом")
    @Test
    fun `Add Product - negative test - delete category with product`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        val product = addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse(
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0]
        deleteCategory(product.categories!![0].id.toString())
            .andVerifyResponseIs(200, ErrorResponse("Category can't be deleted when its related to product"))
    }

    @Description("Позитивный тест получения продукта по id")
    @DisplayName("Позитивный тест получения продукта по id")
    @Test
    fun `Get Product - positive test`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        val id = addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse(
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0].id.toString()
        getProduct(id).andVerifyResponseIs(
            200, GetProductResponse(
                Product(
                    id = id,
                    productName = productName,
                    categories = mutableListOf(
                        CategoryValue(
                            id = ANY_UUID,
                            name = categoryName,
                            brand = brand,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    ),
                    amount = 1,
                    discount = 10,
                    percentDiscount = 10,
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                )
            )
        )
    }

    @Description("Валидационный тест - получение продукта")
    @DisplayName("Проверяем контракты")
    @CsvFileSource(resources = ["/invalidPath.csv"])
    @ParameterizedTest
    fun `Get Product - negative test - invalid path`(id: String) {
        getProduct(id).andVerifyResponseIsNotEmpty(400)
    }


    @Description("Позитивный тест удаления продукта")
    @DisplayName("Позитивный тест удаления продукта")
    @Test
    fun `Delete Product - positive test`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        val id = addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse(
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0].id.toString()
        deleteProduct(id).andVerifyResponseIs(
            200, UpdateDeleteProductResponse(
                "REMOVE",
                value = Product(
                    id = id,
                    productName = productName,
                    categories = mutableListOf(
                        CategoryValue(
                            id = ANY_UUID,
                            name = categoryName,
                            brand = brand,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    ),
                    amount = 1,
                    discount = 10,
                    percentDiscount = 10,
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                )
            )
        )
    }

    @Description("Валидационный тест - удаления продукта")
    @DisplayName("Проверяем контракты")
    @CsvFileSource(resources = ["/invalidPath.csv"])
    @ParameterizedTest
    fun `Delete Product - negative test - invalid path`(id: String) {
        deleteProduct(id).andVerifyResponseIsNotEmpty(400)
    }

    @Description("Позитивный тест изменения созданного продукта")
    @DisplayName("Позитивный тест изменения созданного продукта")
    @Test
    fun `Adjust Product - positive test`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        ids.add(addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse("ADD",
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0].id.toString()
        )
        adjustProduct(
            id = ids[0],
            amount = 4,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 15,
            isVisibleProduct = false,
            productName = productName,
            percentDiscount = 10)
            .andVerifyResponseIs(
            200, UpdateDeleteProductResponse("EDIT",
                value =
                    Product(
                        id = ANY_UUID,
                        productName = productName,
                        categories = mutableListOf(
                            CategoryValue(
                                id = ANY_UUID,
                                name = categoryName,
                                brand = brand,
                                isVisible = true,
                                created = CURRENT_DATE,
                                modified = CURRENT_DATE
                            )
                        ),
                        amount = 4,
                        discount = 15,
                        percentDiscount = 10,
                        isVisible = false,
                        created = CURRENT_DATE,
                        modified = CURRENT_DATE
                    )
            )
        )
    }

    @Description("Позитивный тест изменения созданного продукта с теми же значениями")
    @DisplayName("Позитивный тест изменения созданного продукта с теми же значениями")
    @Test
    fun `Adjust Product - positive test - same values`() {
        val brand = randomizer().book.author()
        val categoryName = randomizer().book.genre()
        val productName = randomizer().book.title()
        ids.add(addProduct(
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10
        )
            .andVerifyResponseIs(
                201, ProductResponse("ADD",
                    values = listOf(
                        Product(
                            id = ANY_UUID,
                            productName = productName,
                            categories = mutableListOf(
                                CategoryValue(
                                    id = ANY_UUID,
                                    name = categoryName,
                                    brand = brand,
                                    isVisible = true,
                                    created = CURRENT_DATE,
                                    modified = CURRENT_DATE
                                )
                            ),
                            amount = 1,
                            discount = 10,
                            percentDiscount = 10,
                            isVisible = true,
                            created = CURRENT_DATE,
                            modified = CURRENT_DATE
                        )
                    )
                )
            ).values[0].id.toString()
        )
        adjustProduct(
            id = ids[0],
            amount = 1,
            categories = listOf(CategoryRequest(brand = brand, isVisible = true, name = categoryName)),
            discount = 10,
            isVisibleProduct = true,
            productName = productName,
            percentDiscount = 10)
            .andVerifyResponseIs(
                200, UpdateDeleteProductResponse("EDIT",
                    value =
                    Product(
                        id = ANY_UUID,
                        productName = productName,
                        categories = mutableListOf(
                            CategoryValue(
                                id = ANY_UUID,
                                name = categoryName,
                                brand = brand,
                                isVisible = true,
                                created = CURRENT_DATE,
                                modified = CURRENT_DATE
                            )
                        ),
                        amount = 1,
                        discount = 10,
                        percentDiscount = 10,
                        isVisible = true,
                        created = CURRENT_DATE,
                        modified = CURRENT_DATE
                    )
                )
            )
    }

    @Description("Позитивный тест получения списка продуктов")
    @DisplayName("Позитивный тест получения списка продуктов")
    @Test
    fun `Get Products - positive test`() {
        getProducts().andVerifyResponseIsNotEmpty(200)
    }


    companion object {
        @JvmStatic
        fun validationData() = listOf(
            Arguments.of(
                null,
                10,
                5
            ),
            Arguments.of(
                1,
                null,
                3
            ),
            Arguments.of(
                1,
                4,
                null
            ),
            Arguments.of(
                -1,
                4,
                3
            ),
            Arguments.of(
                "1",
                5,
                3
            ),
            Arguments.of(
                1,
                "5",
                3
            ),
            Arguments.of(
                1,
                5,
                "3"
            ),
            Arguments.of(
                "werw",
                5,
                3
            ),
            Arguments.of(
                4,
                "sdf",
                3
            ),
        )
    }
}