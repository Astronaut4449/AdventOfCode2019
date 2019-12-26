package aoc2019.day13

import java.io.File

private class IntProcessor(intCode: List<Int>) {
    private val intCodeMap = intCode.mapIndexed { index, value -> index to value }.toMap().toMutableMap()
    lateinit var input: Sequence<Int>
    private val iterator by lazy { input.iterator() }

    fun run() = sequence {
        var pointer = 0
        var relativeBase: Int = 0

        process@ while (true) {
            val opCode = intCodeMap.getOrDefault(pointer, 0).toString().padStart(5, '0')
            val instruction = opCode.substring(3).toInt()

            // Helper for position, immediate and relative access
            val ref = object {
                private val params = (1..3).map { intCodeMap.getOrDefault(pointer + it, 0) }
                private val modes = opCode.chunked(1).take(3).map(String::toInt).reversed()

                operator fun get(index: Int): Int = when (modes[index]) {
                    0 -> intCodeMap.getOrDefault(params[index], 0) // position
                    1 -> params[index] // immediate
                    2 -> intCodeMap.getOrDefault(params[index] + relativeBase, 0) // relative
                    else -> error("Unknown read mode ${modes[index]}")
                }

                operator fun set(index: Int, value: Int) {
                    when (modes[index]) {
                        0 -> intCodeMap[params[index]] = value // position
                        2 -> intCodeMap[params[index] + relativeBase] = value // relative
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
                7 -> ref[2] = if (ref[0] < ref[1]) 1 else 0
                8 -> ref[2] = if (ref[0] == ref[1]) 1 else 0
                9 -> relativeBase += ref[0]
                99 -> break@process
            }

            // Move pointer
            when (instruction) {
                1, 2, 7, 8 -> pointer += 4
                3, 4, 9 -> pointer += 2
                5 -> pointer = (if (ref[0] != 0) ref[1] else pointer + 3)
                6 -> pointer = (if (ref[0] == 0) ref[1] else pointer + 3)
            }
        }
    }
}

private enum class Tile {
    EMPTY, WALL, BLOCK, PADDLE, BALL;

    companion object {
        operator fun invoke(int: Int) = values()[int]
    }
}

private data class Point(val x: Int, val y: Int)

fun main() {
    val input = File("input/day13.txt").readText().split(',').map(String::toInt)

    // Part 1
    run {
        val arcadeGame = IntProcessor(input)
        val screen = mutableMapOf<Point, Tile>()

        val output = arcadeGame.run().iterator()

        while (output.hasNext()) {
            val x = output.next()
            val y = output.next()
            val tile = Tile(output.next())

            screen[Point(x, y)] = tile
        }

        val blockCount = screen.values.count { it == Tile.BLOCK }

        println("Part 1: $blockCount")
    }

    // Part 2
    run {
        val playForFree = input.toMutableList().also { it[0] = 2 }

        val arcadeGame = IntProcessor(playForFree)
        val screen = mutableMapOf<Point, Tile>()
        val output = arcadeGame.run().iterator()

        var score = 0

        arcadeGame.input = sequence {
            while (true) {
                val xBall = screen.entries.first { (_, tile) -> tile == Tile.BALL }.key.x
                val xPaddle = screen.entries.first { (_, tile) -> tile == Tile.PADDLE }.key.x

                val move = when {
                    xBall > xPaddle -> 1
                    xBall < xPaddle -> -1
                    else -> 0
                }

                yield(move)
            }
        }

        do {
            val pos = Point(output.next(), output.next())

            if (pos == Point(-1, 0)) {
                score = output.next()
            } else {
                screen[pos] = Tile(output.next())
            }
        } while (output.hasNext())

        println("Part 2: $score")
    }
}
