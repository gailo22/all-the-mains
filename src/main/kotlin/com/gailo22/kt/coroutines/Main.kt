package com.gailo22.kt.coroutines

import kotlinx.coroutines.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            println("This is thread: ${Thread.currentThread().name}")
            delay(1000L)
            val data = async { loadData() }
            println(data.await())
        }

        runBlocking { job.join() }

        val aa = a("X") { "A " + it }

        println(aa)

        println("This is thread: ${Thread.currentThread().name}")
    }

    suspend fun loadData(): String {
        delay(3000L)
        val data = networkRequest()
        show(data)
        return data
    }

    fun show(data: String) {
        println("Data is $data")
    }

    fun networkRequest(): String {
        return "data"
    }

}

object A

fun <T, X> a(x: X, block: (x: X) -> T): T =
    block(x)
