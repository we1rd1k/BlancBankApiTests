@file:Suppress("UNCHECKED_CAST")

package com.blanc.tests.utils

import com.blanc.tests.Props
import com.blanc.tests.models.HttpResponse
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Response
import com.ultrapartners.api.tests.utils.toJson
import mu.KotlinLogging
import org.aeonbits.owner.ConfigFactory
import org.slf4j.Logger

val log: Logger = KotlinLogging.logger { }
private val props = ConfigFactory.create(Props::class.java)

fun post(
    path: String,
    request: Any?,
    headers: Map<String, Any> = mapOf("Content-Type" to "application/json"),
    parameters: List<Pair<String, String>>? = null
) = sendRequest<String>(RequestType.POST, path, requestBody = request, headers = headers, parameters = parameters)

fun patch(
    path: String,
    request: Any?,
    headers: Map<String, Any> = mapOf("Content-Type" to "application/json"),
    parameters: List<Pair<String, String>>? = null
) = sendRequest<String>(RequestType.PATCH, path, requestBody = request, headers = headers, parameters = parameters)

inline fun <reified T> get(path: String, headers: Map<String, Any> = emptyMap()): HttpResponse<T> =
    sendRequest(RequestType.GET, path, headers = headers)

inline fun <reified T> delete(path: String, headers: Map<String, Any> = emptyMap()): HttpResponse<T> =
    sendRequest(RequestType.DELETE, path, headers = headers)

fun <E> List<E>.toUrlEncoded(): String {
    var stringBody = ""
    this.forEach {
        it as Pair<String, String>
        stringBody += it.first + "=" + it.second + "&"
    }
    return stringBody
}

@Suppress("ComplexMethod")
inline fun <reified T> sendRequest(
    requestType: RequestType,
    path: String,
    requestBody: Any? = null,
    headers: Map<String, Any> = emptyMap(),
    allowRedirects: Boolean = false,
    parameters: List<Pair<String, String>>? = null
): HttpResponse<T> {
    val url = url(path)
    log.info("Sending $requestType request to $url\nheaders - $headers")
    val request = when (requestType) {
        RequestType.GET -> Fuel.get(url).allowRedirects(allowRedirects)
        RequestType.POST -> {
            when (requestBody) {
                is DataPart -> {
                    Fuel.upload(url, Method.POST, parameters)
                        .add(requestBody)
                        .allowRedirects(allowRedirects)
                        .also {
                            log.info(requestBody.toString())
                        }
                }
                null -> {
                    val contentType = headers["Content-Type"] as String?
                    if (contentType == "multipart/form-data; boundary=<calculated when request is sent>") {
                        Fuel.upload(url, Method.POST, parameters)
                            .allowRedirects(allowRedirects)
                    } else {
                        Fuel.post(url, parameters).allowRedirects(allowRedirects)
                    }
                }

                is List<*> -> {
                    val body = requestBody.toUrlEncoded()
                    Fuel.post(url).body(body).allowRedirects(allowRedirects).also {
                        log.info(requestBody.toString())
                    }
                }
                else -> {
                    val json = requestBody.toJson()
                    log.info(json)
                    Fuel.post(url).body(json).allowRedirects(allowRedirects)
                }
            }
        }
        RequestType.PUT -> Fuel.put(url).body(requestBody as ByteArray).allowRedirects(allowRedirects)
        RequestType.OPTIONS -> Fuel.request(Method.OPTIONS, url).allowRedirects(allowRedirects)
        RequestType.DELETE -> Fuel.delete(url).allowRedirects(allowRedirects)
        RequestType.PATCH -> {
            FuelManager.instance.forceMethods = true
            Fuel.patch(url).body(requestBody!!.toJson()).allowRedirects(allowRedirects)
        }
    }


    val (_, response, _) = request.header(headers).timeoutRead(80000).response()

    val responseBody = when (T::class) {
        String::class -> response.body()
            .takeIf { !it.isEmpty() }
            ?.asString("application/json; CHARSET=UTF-8")
            ?: ""
        ByteArray::class -> response.body().toByteArray()
        else -> RuntimeException("Unsupported type ${T::class}")
    } as T

    log.info("Received headers: ${response.headers}")
    log.info("Received response: ${response.statusCode}\n${responseBody}")
    if (response.statusCode == -1) {
        throw RuntimeException("Failed to connect to $url, please verify if service is available")
    }

    return HttpResponse(
        response.statusCode, responseBody, extractCookie(response),
        response.headers
    )
}

fun extractCookie(response: Response): String =
    response["Set-Cookie"].toString()
        .substringAfter("[").substringBefore("]").ifEmpty { "" }

enum class RequestType {
    GET, POST, PUT, OPTIONS, DELETE, PATCH
}

fun url(path: String) = when (path.startsWith("http")) {
    true -> path
    false -> "${props.blancBaseUrl()}$path"
}