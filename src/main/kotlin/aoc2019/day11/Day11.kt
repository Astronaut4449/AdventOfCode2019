package aoc2019.day11

import aoc2019.day11.Color.BLACK
import aoc2019.day11.Color.WHITE
import aoc2019.day11.Direction.*
import java.io.File
import java.util.*

private class IntProcessor(intCode: List<Long>) {
    lateinit var input: Sequence<Long>

    private val intCodeMap = intCode.mapIndexed { index, value -> index to value }.toMap().toMutableMap()
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

private data class Point(val x: Int, val y: Int) {
    operator fun plus(direction: Direction) = when (direction) {
        LEFT -> Point(x - 1, y)
        RIGHT -> Point(x + 1, y)
        UP -> Point(x, y + 1)
        DOWN -> Point(x, y - 1)
    }
}

private enum class Direction {
    LEFT, RIGHT, UP, DOWN;

    fun leftTurn() = when (this) {
        LEFT -> DOWN
        DOWN -> RIGHT
        RIGHT -> UP
        UP -> LEFT
    }

    fun rightTurn() = when (this) {
        LEFT -> UP
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
    }

}

private enum class Turn { LEFT, RIGHT }

private enum class Color { BLACK, WHITE }

private class PaintRobot(val whitePanels: MutableSet<Point>) {
    val panelsEverPaintedWhite = mutableSetOf<Point>()

    fun paint() {
        val intProcessor = IntProcessor(intCode)

        val outputQueue = LinkedList<Long>()

        intProcessor.input = sequence {
            yield(if (Point(0, 0) in whitePanels) WHITE.ordinal.toLong() else BLACK.ordinal.toLong())
            while (outputQueue.isNotEmpty()) {
                yield(outputQueue.removeFirst())
            }
        }

        val output = intProcessor.run().iterator()

        var currDirection = UP
        var currPosition = Point(0, 0)

        while (output.hasNext()) {
            // read input
            val paint = Color.values()[output.next().toInt()]
            val turn = Turn.values()[output.next().toInt()]

            // paint
            when (paint) {
                WHITE -> {
                    whitePanels.add(currPosition)
                    panelsEverPaintedWhite.add(currPosition)
                }
                BLACK -> whitePanels.remove(currPosition)
            }

            // move
            currDirection = if (turn == Turn.LEFT) currDirection.leftTurn() else currDirection.rightTurn()
            currPosition += currDirection

            // identify next panel
            outputQueue.add(if (currPosition in whitePanels) WHITE.ordinal.toLong() else BLACK.ordinal.toLong())
        }
    }
}

val intCode = File("input/day11.txt").readText().split(',').map(String::toLong)

fun main() {
    run {
        val robot = PaintRobot(whitePanels = mutableSetOf())

        robot.paint()
        println("Part 1: ${robot.panelsEverPaintedWhite.size}")
    }

    run {
        val robot = PaintRobot(whitePanels = mutableSetOf(Point(0, 0)))
        robot.paint()

        with(robot.whitePanels) {
            val xCoordinates = map { it.x }
            val yCoordinates = map { it.y }

            val xMin = xCoordinates.min()!!
            val xMax = xCoordinates.max()!!

            val yMin = yCoordinates.min()!!
            val yMax = yCoordinates.max()!!

            println("Part 2:")
            println((yMax downTo yMin).joinToString("\n") { y ->
                (xMin..xMax).joinToString(" ") { x -> if (Point(x, y) in this) "#" else " " }
            })
        }
    }
}
