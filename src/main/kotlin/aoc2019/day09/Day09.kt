package aoc2019.day09

import java.io.File

private val intCode = File("input/day09.txt").readText().split(",").map(String::toLong)

private class IntProcessor(intCode: List<Long>) {
    lateinit var input: Sequence<Long>

    private val intCodeMap = mutableMapOf<Int, Long>().also { it.putAll(intCode.mapIndexed { i, value -> i to value }) }
    private val iterator by lazy { input.iterator() }

    fun run() = sequence {
        var pointer = 0
        var relativeBase = 0

        process@ while (true) {
            val opCode = intCodeMap.getOrDefault(pointer, 0).toString().padStart(5, '0')
            val instruction = opCode.substring(3).toInt()

            // Helper for position, immediate and relative access
            val ref = object {
                private val params = (1..3).map { intCodeMap.getOrDefault(pointer + it, 0) }
                private val modes = opCode.chunked(1).take(3).map(String::toInt).reversed()

                operator fun get(index: Int): Long = when (modes[index]) {
                    0 -> intCodeMap.getOrDefault(params[index].toInt(), 0) // position
                    1 -> params[index] // immediate
                    2 -> intCodeMap.getOrDefault(params[index].toInt() + relativeBase, 0) // relative
                    else -> error("Unknown read mode ${modes[index]}")
                }

                operator fun set(index: Int, value: Long) {
                    when (modes[index]) {
                        0 -> intCodeMap[params[index].toInt()] = value // position
                        2 -> intCodeMap[params[index].toInt() + relativeBase] = value // relative
                        else -> error("Unknown write mode ${modes[index]}")
                    }
                }
            }

            // Execute instruction
            when (instruction) {
                1 -> ref[2] = ref[0] + ref[1]
                2 -> ref[2] = ref[0] * ref[1]
                3 -> ref[0] = iterator.next()
                4 -> yield(ref[0])
                7 -> ref[2] = if (ref[0] < ref[1]) 1L else 0L
                8 -> ref[2] = if (ref[0] == ref[1]) 1L else 0L
                9 -> relativeBase += ref[0].toInt()
                99 -> break@process
            }

            // Move pointer
            when (instruction) {
                1, 2, 7, 8 -> pointer += 4
                3, 4, 9 -> pointer += 2
                5 -> pointer = (if (ref[0] != 0L) ref[1].toInt() else pointer + 3)
                6 -> pointer = (if (ref[0] == 0L) ref[1].toInt() else pointer + 3)
            }
        }
    }
}

fun main() {
    run {
        val intProcessor = IntProcessor(intCode)
        intProcessor.input = sequenceOf(1)
        val result = intProcessor.run().first()
        println("Part 1: $result")
    }

    run {
        val intProcessor = IntProcessor(intCode)
        intProcessor.input = sequenceOf(2)
        val result = intProcessor.run().first()
        println("Part 2: $result")
    }
}