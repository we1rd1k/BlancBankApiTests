package com.blanc.tests

import com.blanc.tests.apis.addCategory
import com.blanc.tests.apis.adjustCategory
import com.blanc.tests.apis.deleteCategory
import com.blanc.tests.assertions.ANY_UUID
import com.blanc.tests.assertions.CURRENT_DATE
import com.blanc.tests.assertions.andVerifyResponseIs
import com.blanc.tests.assertions.andVerifyResponseIsNotEmpty
import com.blanc.tests.models.AddCategoryResponse
import com.blanc.tests.models.CategoryValue
import com.blanc.tests.models.ErrorResponse
import com.blanc.tests.utils.getRandomUUID
import com.blanc.tests.utils.randomizer
import io.qameta.allure.Description
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@Tag("API")
@Feature("API")
@Severity(SeverityLevel.CRITICAL)
class CategoryTests {

    @Description("Позитивный тест добавления категории")
    @DisplayName("Позитивный тест добавления категории")
    @Test
    fun `Add Category - positive test`() {
        addCategory("test", true, "qwe")
            .andVerifyResponseIs(201, AddCategoryResponse(
                value = CategoryValue(
                    id = ANY_UUID,
                    name = "qwe",
                    brand = "test",
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                )))
    }

    @Description("Позитивный тест добавления категории с параметром isVisible: {isVisible}")
    @DisplayName("Позитивный тест добавления категории с параметром isVisible: {isVisible}")
    @Test
    fun `Add Category - positive test - visibility false`() {
        addCategory("test", false, "qwe")
            .andVerifyResponseIs(201, AddCategoryResponse(
                value = CategoryValue(
                    id = ANY_UUID,
                    name = "qwe",
                    brand = "test",
                    isVisible = false,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                )))
    }

    @Description("Тест проверяющий комбинации brand/name на предмет коллизий")
    @DisplayName("Тест проверяющий комбинации brand/name на предмет коллизий")
    @MethodSource("brandNameCombinations")
    @ParameterizedTest
    fun `Add Category - test - brand-name collision check`(brand: Any?, brand1: Any?, name: Any?, name1: Any?,code: Int, response: Any?) {
        addCategory(brand, true, name)
            .andVerifyResponseIs(201, AddCategoryResponse(
                value = CategoryValue(
                    id = ANY_UUID,
                    name = name as String?,
                    brand = brand as String?,
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                )))
        addCategory(brand1, true, name1)
            .andVerifyResponseIs(code, response)
    }

    @Description("Валидационный тест - добавление категории")
    @DisplayName("Проверяем контракты")
    @MethodSource("validationData")
    @ParameterizedTest
    fun `Add Category - negative tests - validation`(brand: Any?, isVisible: Boolean?, name: Any?,code: Int) {
        addCategory(brand, isVisible, name)
            .andVerifyResponseIsNotEmpty(code)
    }

    @Description("Позитивный тест удаления категории")
    @DisplayName("Позитивный тест удаления категории")
    @Test
    fun `Delete Category - positive test`() {
        val id = addCategory("test", true, "qwe")
            .andVerifyResponseIs(201, AddCategoryResponse(
                value = CategoryValue(
                    id = ANY_UUID,
                    name = "qwe",
                    brand = "test",
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                ))).value!!.id
        deleteCategory(id).andVerifyResponseIs(200, AddCategoryResponse("DELETE",
            value = CategoryValue(
                id = id,
                name = "qwe",
                brand = "test",
                isVisible = true,
                created = CURRENT_DATE,
                modified = CURRENT_DATE
            )))
    }

    @Description("Тест проверяющий попытку повторного удаления")
    @DisplayName("Повторное удаление")
    @Test
    fun `Delete Category - negative test - delete twice`() {
        val id = addCategory("test", true, "qwe")
            .andVerifyResponseIs(201, AddCategoryResponse(
                value = CategoryValue(
                    id = ANY_UUID,
                    name = "qwe",
                    brand = "test",
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                ))).value!!.id
        deleteCategory(id).andVerifyResponseIs(200, AddCategoryResponse("DELETE",
            value = CategoryValue(
                id = id,
                name = "qwe",
                brand = "test",
                isVisible = true,
                created = CURRENT_DATE,
                modified = CURRENT_DATE
            )))
        deleteCategory(id).andVerifyResponseIs(200, ErrorResponse("$id is not found"))
    }

    @Description("Тест проверяющий удаление несуществующего UUID")
    @DisplayName("Попытка удаления с несуществующий UUID")
    @Test
    fun `Delete Category - negative test - delete not existing UUID`() {
        val id = getRandomUUID().toString()
        deleteCategory(id).andVerifyResponseIs(200, ErrorResponse("$id is not found"))
    }

    @Description("Удаление категории - валидация UUID")
    @DisplayName("Удаление категории - валидация UUID")
    @Test
    fun `Delete Category - negative test - UUID validation`() {
        val id = randomizer().random.nextInt(1..Int.MAX_VALUE).toString()
        deleteCategory(id).andVerifyResponseIsNotEmpty(400)
    }

    @Description("Позитивный тест изменения созданной категории")
    @DisplayName("Позитивный тест изменения созданной категории")
    @Test
    fun `Adjust Category - positive test`() {
        val id = addCategory("test", true, "qwe")
            .andVerifyResponseIs(201, AddCategoryResponse(
                value = CategoryValue(
                    id = ANY_UUID,
                    name = "qwe",
                    brand = "test",
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                ))).value!!.id
        adjustCategory(id, "Trevth", false, "qwe").andVerifyResponseIs(200, AddCategoryResponse("UPDATE",
            value = CategoryValue(
                id = id,
                name = "qwe",
                brand = "Trevth",
                isVisible = false,
                created = CURRENT_DATE,
                modified = CURRENT_DATE
            )))
    }


    @Description("Валидационный тест - изменение категории")
    @DisplayName("Проверяем контракты")
    @MethodSource("validationData")
    @ParameterizedTest
    fun `Adjust Category - negative test - validation`(brand: Any?, isVisible: Boolean?, name: Any?,code: Int) {
        val id = addCategory(brand, isVisible, name)
            .andVerifyResponseIs(201, AddCategoryResponse(
                value = CategoryValue(
                    id = ANY_UUID,
                    name = "qwe",
                    brand = "test",
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                ))).value!!.id
        adjustCategory(id, "Trevth", false, "qwe").andVerifyResponseIsNotEmpty(code)
    }

    @Description("Тест проверяющий комбинации brand/name на предмет коллизий при изменении категории")
    @DisplayName("Тест проверяющий комбинации brand/name на предмет коллизий при изменении категории")
    @MethodSource("brandNameCombinations")
    @ParameterizedTest
    fun `Adjust Category - negative test - brand-name collision check`(brand: Any?, brand1: Any?, name: Any?, name1: Any?,code: Int, response: Any?) {
        val id = addCategory(brand, true, name)
            .andVerifyResponseIs(200, AddCategoryResponse(
                value = CategoryValue(
                    id = ANY_UUID,
                    name = name as String,
                    brand = brand as String,
                    isVisible = true,
                    created = CURRENT_DATE,
                    modified = CURRENT_DATE
                ))).value!!.id
        adjustCategory(id, brand1, true, name1).andVerifyResponseIs(code, response)
    }



    companion object {
        @JvmStatic
        fun validationData() = listOf(
            Arguments.of(
                "wretreeyertyrtveyertyrtvytyertynertwretreeyertyrtvey" +
                        "ertyrtvytyertynertnyevrtyrvyertyertyertnyrntywretree" +
                        "yertyrtvynyevrtyrvyertywretreeyertyrtveyertyrtvytyer" +
                        "tynertnyevrtyrvyertyertyertnyrntywretreeyertyrwretreeyertyrtveyertyrtvytyertynertnyevrtyrvyertyertyw",
                true,
                "test",
                400
            ),
            Arguments.of(
                "test",
                true,
                "wretreeyertyrtveyertyrtvytyertynertwretreeyertyrtve" +
                        "yertyrtvytyertynertnyevrtyrvyertyertyertnyrntywretreey" +
                        "ertyrtvynyevrtyrvyertywretreeyertyrtveyertyrtvytyertyne" +
                        "rtnyevrtyrvyertyertyertnyrntywretreeyertyrwretreeyertyrtveyertyrtvytyertynertnyevrtyrvyertyertyw",
                400
            ),
            Arguments.of(
                null,
                true,
                "test",
                400
            ),
            Arguments.of(
                "Nike",
                null,
                "test",
                400
            ),
            Arguments.of(
                "Nike",
                true,
                null,
                400
            ),
            Arguments.of(
                "Nike",
                true,
                23154336,
                400
            ),
            Arguments.of(
                "Nike",
                true,
                "",
                400
            ),
            Arguments.of(
                "",
                true,
                "Qwerty",
                400
            ),
            Arguments.of(
                "   ",
                true,
                "Qwerty",
                400
            )
        )

        @JvmStatic
        fun brandNameCombinations() = listOf(
            Arguments.of(
                "Nike",
                "Nike",
                "Clothes",
                "Clothes",
                200,
                ErrorResponse("brand and name is already exist")
            ),
            Arguments.of(
                "Nike",
                "Nike",
                "Clothes",
                "Sneakers",
                201,
                AddCategoryResponse(
                    value = CategoryValue(
                        id = ANY_UUID,
                        name = "Sneakers",
                        brand = "Nike",
                        isVisible = true,
                        created = CURRENT_DATE,
                        modified = CURRENT_DATE
                    ))
            ),
            Arguments.of(
                "Reebok",
                "Nike",
                "Clothes",
                "Clothes",
                201,
                AddCategoryResponse(
                    value = CategoryValue(
                        id = ANY_UUID,
                        name = "Clothes",
                        brand = "Nike",
                        isVisible = true,
                        created = CURRENT_DATE,
                        modified = CURRENT_DATE
                    ))
            ),
        )
    }

}
