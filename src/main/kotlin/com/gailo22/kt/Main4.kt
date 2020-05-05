package com.gailo22.kt

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.system.measureTimeMillis

object Main4 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val channel = Channel<Int>()

        val job = launch {
            for (x in 1..5) {
                println("sending $x")
                channel.send(x)
            }
            channel.close()
        }

        for(y in channel) {
            println("receive: $y")
        }

        job.join()
    }

}