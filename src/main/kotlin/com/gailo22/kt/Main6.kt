package com.gailo22.kt

import okhttp3.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession


object Main6 {

    val counter: AtomicInteger = AtomicInteger(0)

    @JvmStatic
    fun main(args: Array<String>) {
        val webSocket = connectionWebSocket("localhost", 8082)
        webSocket.send(
            "{\n" +
                    "  \"jsonrpc\": \"2.0\",\n" +
                    "  \"method\": \"login\",\n" +
                    "  \"id\": 1,\n" +
                    "  \"params\": {\n" +
                    "    \"login\": \"62555555\",\n" +
                    "    \"passwd\": \"3Nbv4ab27xnHlNB\",\n" +
                    "    \"loginParams\": {\n" +
                    "      \"token\": \"eyJraWQixxx\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
        )

        val executor = Executors.newSingleThreadScheduledExecutor()
        val pingInterval = Runnable {
            println("send empty message.." + counter.incrementAndGet())
            val isSuccess = webSocket.send("")
            if (!isSuccess) {
                webSocket.cancel()
            }
        }
        executor.scheduleAtFixedRate(pingInterval, 5, 10, TimeUnit.SECONDS)

        Thread.sleep(60_000)
        executor.shutdown()
    }

    fun connectionWebSocket(hostName: String, port: Int): WebSocket {
        val hostnameVerifier: HostnameVerifier = object : HostnameVerifier {
            override fun verify(hostname: String, session: SSLSession?): Boolean {
                return true
            }
        }
        val httpClient = OkHttpClient.Builder()
//            .pingInterval(10, TimeUnit.SECONDS) // set ping frame sending interval
//            .connectionPool(ConnectionPool(15, 3600L, TimeUnit.SECONDS)) // TCP Keep-Alive
            .hostnameVerifier(hostnameVerifier)
            .build()
        val webSocketUrl = "wss://${hostName}:${port}/"
        val request = Request.Builder()
            .url(webSocketUrl)
            .build()

        val mWebSocket = httpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                println("onOpen: $response")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                println("onMessage: $text")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                println("onClosing: $code -> $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                println("onClosed: $code -> $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                println("onFailure: $response")
                t.printStackTrace()
            }
        })
        return mWebSocket
    }
}
