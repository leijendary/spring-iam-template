package com.leijendary.spring.template.iam.generator

import java.util.*

object RandomGenerator {
    private val RANDOM = Random()
    private const val NUMBERS = "0123456789"

    fun digits(length: Int): IntArray {
        val values = IntArray(length)

        for (i in 0 until length) {
            val index = RANDOM.nextInt(length)
            val current = NUMBERS[index]

            values[i] = Character.getNumericValue(current)
        }

        return values
    }
}
