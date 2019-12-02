package aoc2019.day02

import java.io.File
import kotlin.system.exitProcess

private fun processOpCode(opCode: List<Int>, noun: Int, verb: Int): Int {
    val mutableOpCode = opCode.toMutableList()
    mutableOpCode[1] = noun
    mutableOpCode[2] = verb

    loop@ for (i in mutableOpCode.indices step 4) {
        if (mutableOpCode[i] == 99) break@loop

        val (instruction, first, second, destination) = mutableOpCode.subList(i, i + 4)

        when (instruction) {
            1 -> mutableOpCode[destination] = mutableOpCode[first] + mutableOpCode[second]
            2 -> mutableOpCode[destination] = mutableOpCode[first] * mutableOpCode[second]
            else -> error("Unexpected instruction")
        }
    }

    return mutableOpCode[0]
}

fun main() {
    val opCode = File("input/day02.txt").readText().split(',').map { it.toInt() }.toMutableList()

    println("Part 1: ${processOpCode(opCode, 12, 2)}")

    for (noun in 0..99) {
        for (verb in 0..99) {
            if (processOpCode(opCode, noun, verb) == 19690720) {
                println("Part 2: ${100 * noun + verb}")
                exitProcess(0)
            }
        }
    }
}