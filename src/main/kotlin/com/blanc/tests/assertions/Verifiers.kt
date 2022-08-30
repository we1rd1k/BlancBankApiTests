package com.blanc.tests.assertions

import com.blanc.tests.models.HttpResponse
import com.ultrapartners.api.tests.utils.deepCopy
import com.ultrapartners.api.tests.utils.fromJson
import com.ultrapartners.api.tests.utils.fromJsonToList
import io.qameta.allure.Step
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.slf4j.Logger
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

val log: Logger = KotlinLogging.logger { }

@Step("Verify response")
fun <T : Any?> HttpResponse<String>.andVerifyResponseIs(code: Any?, expectedResponse: T): T {
    val actualResponse: Any
    try {
        assertEquals(code, this.code)
        val expected: Any
        if (expectedResponse !is String) {
            actualResponse = this.body.fromJson(expectedResponse!!::class.java)
            expected = processRegexp(expectedResponse, actualResponse)
        } else {
            actualResponse = this.body
            expected = expectedResponse
        }

        assertEquals(expected, actualResponse)
    } catch (assertionError: AssertionError) {
        log.warn("Assertion failed for response with code ${this.code}\nand body: ${this.body}")
        throw assertionError
    }

    @Suppress("UNCHECKED_CAST")
    return actualResponse as T
}

@Step("Verify list response")
fun <T : Any?> HttpResponse<String>.andVerifyListResponseIs(code: Any?, expectedResponse: T): T {
    val actualResponse: Any
    try {
        assertEquals(code, this.code)
        val expected = mutableListOf<Any>()
        if (expectedResponse !is String) {
            actualResponse = this.body.fromJsonToList(expectedResponse!!::class.java)
            for (i in actualResponse.indices) {
                expected.add(processRegexp(expectedResponse, actualResponse[i]))
            }
        } else {
            actualResponse = this.body
            for (i in actualResponse.indices) {
                expected.add(expectedResponse[i])
            }
        }

        assertEquals(expected, actualResponse)
    } catch (assertionError: AssertionError) {
        log.warn("Assertion failed for response with code ${this.code}\nand body: ${this.body}")
        throw assertionError
    }

    @Suppress("UNCHECKED_CAST")
    return actualResponse as T
}

fun HttpResponse<String>.andVerifyResponseIsEmptyWithCode(code: Int) {
    assertEquals(code, this.code)
    assertEquals("", this.body)
}

fun HttpResponse<String>.andVerifyResponseContains(code: Int, expectedResponse: String) {
    assertEquals(code, this.code)
    assertTrue(this.body.contains(expectedResponse))
}

fun HttpResponse<String>.andVerifyResponseIsNotEmpty(code: Int): HttpResponse<String> {
    assertEquals(code, this.code)
    assertNotEquals("", this.body)
    return HttpResponse(code, body)
}

fun HttpResponse<String>.andCheckCodeIs(expectedCode: Any?): Any {
    assertEquals(expectedCode, this.code)
    return this.body
}

fun HttpResponse<ByteArray>.andCheckCodeIs(expectedCode: Any?) =
    assertEquals(expectedCode, this.code)


/**
 * Matches regexp in [initialExpected] data model with values in according field in [actual] data model.
 * If values are matched - field in [initialExpected] is overwritten with value from actual.
 * If not - nothing happens & comparison should be failed on the further steps.
 * E.g.
 * expected - {"firstValue": 1, "secondValue": "regexp: [\d+]"}, actual -  {"firstValue": 1, "secondValue": "3"}
 * result of this function will be {"firstValue": 1, "secondValue": "3"} (as 3 matches regexp in expected data)
 *
 * @param initialExpected - some data model.
 * @param actual - data model of the same type as [initialExpected].
 * @return updated expected data model
 */

private fun processRegexp(initialExpected: Any, actual: Any): Any {
    val expected = initialExpected.deepCopy()
    val nestedModels = mutableListOf<Triple<String, Any, Any>>()
    expected::class.memberProperties.map { prop ->
        val expectedProp = prop.call(expected)
        val actualProp = prop.call(actual)
        if (expectedProp is String && (
                    expectedProp.startsWith("regexp: ") || expectedProp.startsWith("traceId=regexp: ")
                    )
        ) {
            val isPrefixMatched = expectedProp
                .removePrefix("traceId=regexp: ").toRegex().matches(actualProp.toString())
            val isMatched = expectedProp.removePrefix("regexp: ").toRegex().matches(actualProp.toString())
            if (isMatched || isPrefixMatched) {
                val property = getPropertyValue(prop.name, expected)
                setProperty(property, expected, actualProp)
            }
        }

        if (expectedProp is ArrayList<*> && actualProp is ArrayList<*> &&
            expectedProp.size == actualProp.size
        ) {
            val updatedProperties = mutableListOf<Any>()
            expectedProp.forEachIndexed { index, element ->
                updatedProperties.add(processRegexp(element, actualProp.get(index)))
            }

            setProperty(getPropertyValue(prop.name, expected), expected, updatedProperties)
        }

        if (prop.returnType.toString().startsWith("com.blanc.tests.models") &&
            expectedProp != null && actualProp != null
        ) {
            nestedModels.add(Triple(prop.name, expectedProp, actualProp))
        }
    }

    nestedModels.forEach {
        val updatedProperty = processRegexp(it.second, it.third)
        setProperty(getPropertyValue(it.first, expected), expected, updatedProperty)
    }

    return expected
}

private fun getPropertyValue(propName: String, classInstance: Any): KProperty1<out Any, *>? =
    classInstance::class.memberProperties.find { it.name == propName }

private fun setProperty(property: KProperty1<out Any, *>?, classInstance: Any, propertyValue: Any?) {
    if (property is KMutableProperty<*>) {
        property.setter.call(classInstance, propertyValue)
    }
}