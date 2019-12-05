package aoc2019.day05

import java.io.File

fun main() {
    val intCode = File("input/day05.txt").readText().split(',').map(String::toInt)

    println("Part 1: ${processIntCode(intCode, 1)}")
    println("Part 2: ${processIntCode(intCode, 5)}")
}

private fun processIntCode(intCode: List<Int>, input: Int): Int {
    val mutableIntCode = intCode.toMutableList()

    var output = -1
    var pointer = 0

    while (true) {
        val opCode = mutableIntCode[pointer].toString().padStart(5, '0')
        val instruction = opCode.substring(3).toInt()

        // Helper for position or immediate access
        val param = object {
            private val params = mutableIntCode.subList(pointer + 1, (pointer + 4).coerceAtMost(mutableIntCode.size))
            private val immediacies = opCode.toCharArray().take(3).map { it == '1' }.reversed()

            operator fun get(index: Int) = if (immediacies[index]) params[index] else mutableIntCode[params[index]]
            operator fun set(index: Int, value: Int) {
                mutableIntCode[params[index]] = value
            }
        }

        // Execute instruction
        when (instruction) {
            1 -> param[2] = param[0] + param[1]
            2 -> param[2] = param[0] * param[1]
            3 -> param[0] = input
            4 -> output = param[0]
            7 -> param[2] = if (param[0] < param[1]) 1 else 0
            8 -> param[2] = if (param[0] == param[1]) 1 else 0
            99 -> return output
        }

        // Move pointer
        when (instruction) {
            1, 2, 7, 8 -> pointer += 4
            3, 4 -> pointer += 2
            5 -> pointer = if (param[0] != 0) param[1] else pointer + 3
            6 -> pointer = if (param[0] == 0) param[1] else pointer + 3
        }
    }
}
