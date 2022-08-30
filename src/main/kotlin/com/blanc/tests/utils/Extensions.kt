package com.ultrapartners.api.tests.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

val mapper: ObjectMapper = ObjectMapper()
    .findAndRegisterModules()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

fun Any.deepCopy(): Any = when(this) {
    is String -> this
    else -> mapper.readValue(this.toJson(), this::class.java)
}

fun Any.toJson(): String = when(this) {
    is String -> this
    else -> {
        val mapper = mapper
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
    }
}

fun <T : Any?> String.fromJson(classType: Class<T>): T =
    mapper.readValue(this, classType)

fun <T : Any?> String.fromJsonToList(classType: Class<T>): List<T> =
    mapper.readValue(this, mapper.typeFactory.constructCollectionType(List::class.java, classType))