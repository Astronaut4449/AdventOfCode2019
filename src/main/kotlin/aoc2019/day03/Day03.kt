package aoc2019.day03

import java.io.File
import kotlin.math.abs

private data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
}

private typealias Point = Vector

fun main() {
    val (path1: List<Point>, path2: List<Point>) = parseInput()

    val intersections = path1 intersect path2
    val pointClosestToCenter = intersections.map { abs(it.x) + abs(it.y) }.min()

    println("Part 1: $pointClosestToCenter")

    val intersectionDistances = intersections.map { intersection ->
        path1.indexOf(intersection) + path2.indexOf(intersection) + 2
    }

    val distanceFirstIntersection = intersectionDistances.min()

    println("Part 2: $distanceFirstIntersection")
}

private class Instruction(val direction: Vector, val timesToRepeat: Int)

private fun parseInstruction(text: String) = Instruction(
        text.first().let {
            when (it) {
                'L' -> Vector(-1, 0)
                'R' -> Vector(1, 0)
                'D' -> Vector(0, -1)
                'U' -> Vector(0, 1)
                else -> throw IllegalArgumentException("Unknown direction: $it")
            }
        },
        text.substring(1).toInt()
)

private fun parseInput(): List<List<Point>> = File("input/day03.txt")
        .readLines()
        .map { line ->
            val instructions = line
                    .split(',')
                    .map { parseInstruction(it) }

            val directionVectors = instructions
                    .map { instruction -> List(instruction.timesToRepeat) { instruction.direction } }
                    .flatten()

            val path = directionVectors
                    .fold(mutableListOf<Point>()) { points, directionVector ->
                        points.apply {
                            add(if (isEmpty()) directionVector else last() + directionVector)
                        }
                    }

            return@map path
        }
