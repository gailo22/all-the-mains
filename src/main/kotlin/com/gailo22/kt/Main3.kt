package com.gailo22.kt

import kotlinx.coroutines.*

object Main3 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val result = doWorkAsync("hello")

        runBlocking {
            result.await()
        }

    }

    fun doWorkAsync(msg: String): Deferred<Int> = GlobalScope.async {
        println("working")
        delay(500)
        println("done")
        return@async 42
    }
}