package com.blanc.tests.utils

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.fakerConfig
import java.util.*

fun randomizer(): Faker {
    val config = fakerConfig { locale = "en" }
    return Faker(config)
}

fun getRandomUUID(): UUID {
    return UUID.randomUUID()
}