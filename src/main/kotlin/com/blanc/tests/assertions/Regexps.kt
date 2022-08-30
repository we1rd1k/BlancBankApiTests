package com.blanc.tests.assertions

import com.ultrapartners.api.tests.utils.currentDate

const val ANY_UUID = "regexp: [0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
const val ANY_DIGITS = "regexp: [\\d]+"
const val EMPTY = "regexp: ^\$"
const val EMPTY_OR_NULL = "regexp: ^\$|null"
const val NOT_EMPTY = "regexp: ^(?!\\s*\$).+"
const val ANY_STRING = "regexp: .*"
const val ANY_STRING_OR_NULL = "regexp: .*|null"
const val ANY_URL = "regexp: ^http[s]?:(?!\\s*\$).+"
val TIMESTAMP = "regexp: ${currentDate()}T\\d{2}:\\d{2}:\\d{2}.\\d+Z"
val CURRENT_DATE = "regexp: ${currentDate()} \\d{2}:\\d{2}:\\d{2}"
