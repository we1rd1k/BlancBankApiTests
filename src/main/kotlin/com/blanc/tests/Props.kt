package com.blanc.tests

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.LoadPolicy
import org.aeonbits.owner.Config.Sources

@LoadPolicy(Config.LoadType.MERGE)
@Sources("file:src/main/resources/config.properties", "system:env", "system:properties")
interface Props : Config {

    fun blancBaseUrl(): String

}