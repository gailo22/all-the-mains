package com.gailo22.kt.fp

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println(formatResult("factorial", 7, ::factorial))
        println(formatResult("absolute value", -42, ::abs))

        findFirst(arrayOf(7, 9, 13)) { i: Int -> i == 9 }
    }

    private fun abs(n: Int): Int =
        if (n < 0) -n
        else n

    fun formatAbs(x: Int): String {
        val msg = "The absolute value of %d is %d"
        return msg.format(x, abs(x))
    }

    fun formatFactorial(x: Int): String {
        val msg = "The factorial of %d is %d"
        return msg.format(x, factorial(x))
    }

    fun formatResult(name: String, n: Int, f: (Int) -> Int): String {
        val msg = "The %s of %d is %d"
        return msg.format(name, n, f(n))
    }

    fun factorial(n: Int): Int {
        fun go(n: Int, acc: Int): Int =
            if (n <= 0) acc
            else go(n-1, n * acc)

        return go(n, 1)
    }

    fun findFirst(ss: Array<String>, key: String): Int {
        tailrec fun loop(n: Int): Int =
            when {
                n >= ss.size -> -1
                ss[n] == key -> n
                else -> loop(n + 1)
            }
            return loop(0)
    }

    fun <A> findFirst(xs: Array<A>, p: (A) -> Boolean): Int {
        tailrec fun loop(n: Int): Int =
            when {
                n >= xs.size -> -1
                p(xs[n]) -> n
                else -> loop(n + 1)
            }
        return loop(0)
    }

    fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C = {a -> { b -> f(a, b) } }

    fun <A, B, C> uncurry(f: (A) -> (B) -> C): (A, B) -> C = { a, b -> (f(a))(b) }

    fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C = { a -> f(g(a)) }

}