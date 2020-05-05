package com.gailo22.kt

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlin.random.Random
import kotlin.system.measureTimeMillis

object Main5 {

    fun produceNumbers() = GlobalScope.produce {
        for (x in 1..5) {
            println("send $x")
            send(x)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val channel = produceNumbers()

        channel.consumeEach {
            println("receive: $it")
        }

    }

}