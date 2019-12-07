package aoc2019.day07

import java.io.File
import java.util.*

private val intCode = File("input/day07.txt").readText().split(',').map(String::toInt)

class IntProcessor {
    lateinit var input: Sequence<Int>

    private val mutableIntCode = intCode.toMutableList()
    private val iterator by lazy { input.iterator() }

    fun run() = sequence {
        var pointer = 0

        loop@ while (true) {
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
                3 -> param[0] = iterator.next()
                4 -> yield(param[0])
                7 -> param[2] = if (param[0] < param[1]) 1 else 0
                8 -> param[2] = if (param[0] == param[1]) 1 else 0
                99 -> break@loop
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
}

fun permutations(size: Int): Sequence<List<Int>> = sequence {
    fun heapPermutation(list: MutableList<Int>, size: Int): Sequence<List<Int>> = sequence {
        if (size == 1) {
            yield(list.toList())
        }

        for (i in 0 until size) {
            yieldAll(heapPermutation(list, size - 1))

            if (size % 2 == 1) {
                val temp = list[0]
                list[0] = list[size - 1]
                list[size - 1] = temp
            } else {
                val temp = list[i]
                list[i] = list[size - 1]
                list[size - 1] = temp
            }
        }
    }

    yieldAll(heapPermutation((0 until size).toMutableList(), size))
}


fun main() {
    var max1 = Int.MIN_VALUE

    for (config in permutations(5)) {
        val processors = List(5) { IntProcessor() }
        val (a, b, c, d, e) = processors

        a.input = sequence { yield(config[0]); yield(0) }
        b.input = sequence { yield(config[1]); yieldAll(a.run()) }
        c.input = sequence { yield(config[2]); yieldAll(b.run()) }
        d.input = sequence { yield(config[3]); yieldAll(c.run()) }
        e.input = sequence { yield(config[4]); yieldAll(d.run()) }

        max1 = max1.coerceAtLeast(e.run().asSequence().max()!!)
    }

    println("Part 1: $max1")

    var max2 = Int.MIN_VALUE

    for (config in permutations(5).map { config -> config.map { it + 5 } }) {
        val (a, b, c, d, e) = List(5) { IntProcessor() }

        val feedback = LinkedList<Int>()

        e.input = sequence { yield(config[4]); yieldAll(d.run()) }
        d.input = sequence { yield(config[3]); yieldAll(c.run()) }
        c.input = sequence { yield(config[2]); yieldAll(b.run()) }
        b.input = sequence { yield(config[1]); yieldAll(a.run()) }
        a.input = sequence {
            yield(config[0])
            yield(0)
            while (feedback.isNotEmpty()) {
                yield(feedback.removeAt(0))
            }
        }

        e.run().forEach { output ->
            feedback.add(output)
            max2 = max2.coerceAtLeast(output)
        }
    }

    println("Part 2: $max2")
}