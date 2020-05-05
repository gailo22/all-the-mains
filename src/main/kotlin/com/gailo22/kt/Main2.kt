package com.gailo22.kt

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.system.measureTimeMillis

object Main2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val job = launch {
            val time = measureTimeMillis {
                val workOne = async { doWorkOne() }
                val workTwo = async { doWorkTwo() }
                println("total: ${workOne.await() + workTwo.await()}")

            }
            println("time: ${time}")
        }

        job.join()

    }

    suspend fun doWorkOne(): Int {
        delay(100)
        println("working 1")
        return Random(System.currentTimeMillis()).nextInt(42)
    }

    suspend fun doWorkTwo(): Int {
        delay(200)
        println("working 2")
        return Random(System.currentTimeMillis()).nextInt(42)
    }
}