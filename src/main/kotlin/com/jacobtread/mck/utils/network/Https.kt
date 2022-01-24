package com.jacobtread.mck.utils.network

import com.jacobtread.mck.logger.Logger
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * concatURL Concatenates the provided [query] string onto the
 * end of the provided url
 *
 * @param url The url to add onto
 * @param query The query to add
 * @return The resulting URL
 */
fun concatURL(url: URL, query: String): URL {
    try {
        return if (url.query != null && url.query.isNotEmpty()) {
            URL(url.protocol, url.host, url.port, "${url.file}&$query")
        } else {
            URL(url.protocol, url.host, url.port, "${url.file}?$query")
        }
    } catch (e: MalformedURLException) {
        throw IllegalArgumentException("Failed to concat URL with given GET arguments", e)
    }
}

/**
 * urlEncode URL encodes the provided value
 *
 * @param value The value to url encode
 * @return The url encoded value or an empty string on failure
 */
fun urlEncode(value: String): String {
    return try {
        URLEncoder.encode(value, Charsets.UTF_8)
    } catch (e: UnsupportedEncodingException) {
        ""
    }
}

object Https {
    private val LOGGER: Logger = Logger.get()

    /**
     * PROXY The proxy that should be used when making
     * networking requests. This is set on startup
     */
    var PROXY: Proxy = Proxy.NO_PROXY

    /**
     * networkThreadId The id that the next worker thread from
     * [EXECUTOR] will have
     */
    val networkThreadId = AtomicInteger(1)

    /**
     * EXECUTOR The executor for executing networking jobs in a pool
     * of 10 threads
     */
    private val EXECUTOR: ExecutorService = Executors.newFixedThreadPool(10) {
        Thread(it, "NetWorker#${networkThreadId.getAndIncrement()}").apply {
            isDaemon = true
        }
    }

    @Throws(IOException::class)
    private fun createConnection(url: URL): HttpURLConnection {
        val connection = url.openConnection(PROXY)
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.useCaches = false
        return connection as HttpURLConnection
    }

    /**
     * post Performs an HTTP POST request and sends the provided [data]
     * to the provided [url] with the provided [contentType]
     *
     * @param url The url to send the POST request
     * @param data The data of the post request
     * @param contentType The Content-Type header of the request e.g. application/json
     * @return The raw text response from the server
     * @throws IOException If the request was a failure
     */
    fun post(url: URL, data: String, contentType: String): String {
        val connection = createConnection(url)
        val bytes = data.toByteArray(Charsets.UTF_8)
        connection.apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "$contentType; charset=utf-8")
            setRequestProperty("Content-Length", "${bytes.size}")
            doOutput = true
            outputStream.use { it.write(bytes) }
            try {
                inputStream.use { return String(it.readAllBytes(), Charsets.UTF_8) }
            } catch (e: IOException) {
                errorStream?.use { return String(it.readAllBytes(), Charsets.UTF_8) }
                throw e
            }
        }
    }

    /**
     * get Performs an HTTP GET request to the provided [url]
     * with the optional value [authorization] for the
     * Authorization header returns the plain text response
     * from the server
     *
     * @param url The url to send the GET request
     * @param authorization The authorization header value or null
     * @return The plain text response from the server
     * @throws IOException If the request was a failure
     */
    @Throws(IOException::class)
    fun get(url: URL, authorization: String? = null): String {
        val connection = createConnection(url)
        connection.apply {
            if (authorization != null) {
                setRequestProperty("Authorization", authorization)
            }
            try {
                inputStream.use { return String(it.readAllBytes(), Charsets.UTF_8) }
            } catch (e: IOException) {
                errorStream?.use { return String(it.readAllBytes(), Charsets.UTF_8) }
                throw e
            }
        }
    }

    /**
     * buildQuery Builds a query string from the provided
     * map of key -> value pairs e.g.
     *
     * key=value&key2=value2
     *
     * @param query The query key -> value pairs
     * @return The resulting query string
     */
    fun buildQuery(query: Map<String, Any?>): String {
        val builder = StringBuilder()
        query.entries.forEach { (key, value) ->
            if (builder.isNotEmpty()) builder.append('&')
            try {
                builder.append(URLEncoder.encode(key, Charsets.UTF_8))
            } catch (e: UnsupportedEncodingException) {
                LOGGER.error("Unexpected exception building query string", e)
            }
            if (value != null) {
                builder.append('=')
                try {
                    builder.append(URLEncoder.encode(value.toString(), Charsets.UTF_8))
                } catch (e: UnsupportedEncodingException) {
                    LOGGER.error("Unexpected exception building query string", e)
                }
            }
        }
        return builder.toString()
    }
}