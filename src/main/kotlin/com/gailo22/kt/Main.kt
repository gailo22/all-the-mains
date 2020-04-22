package com.gailo22.kt

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val job = launch {
        delay(1000)
        println("world")
    }

    print("Hello, ")
    job.join()

}