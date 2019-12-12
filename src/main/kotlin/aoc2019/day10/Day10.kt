package aoc2019.day10

import java.io.File
import kotlin.math.atan2
import kotlin.math.sqrt

private data class Point(val x: Int, val y: Int) {
    infix fun vectorTo(other: Point) = Vector((other.x - x).toDouble(), (other.y - y).toDouble())
}

private data class Vector(val x: Double, val y: Double) {
    val length = sqrt(x * x + y * y)

    fun normalize() = Vector((x / length).toFloat().toDouble(), (y / length).toFloat().toDouble())
    operator fun unaryMinus() = Vector(-x, -y)
}

private val asteroids = mutableSetOf<Point>().apply {
    File("input/day10.txt").readLines().forEachIndexed { y, line ->
        line.forEachIndexed { x, char -> if (char == '#') add(Point(x, y)) }
    }
}

private data class Connection(val base: Point, val destination: Point, val direction: Vector, val scaleFactor: Double)

fun main() {
    val connections = mutableSetOf<Connection>()

    for (baseAsteroid in asteroids) {
        for (targetAsteroid in asteroids) {
            if (baseAsteroid != targetAsteroid) {
                val normalizedDirectionVector = (baseAsteroid vectorTo targetAsteroid).normalize()

                val scalingFactor = ((targetAsteroid.x - baseAsteroid.x) / normalizedDirectionVector.x).let {
                    if (it.isNaN()) (targetAsteroid.y - baseAsteroid.y) / normalizedDirectionVector.y else it
                }

                if (scalingFactor > 0.0) {
                    connections.add(Connection(baseAsteroid, targetAsteroid, normalizedDirectionVector, scalingFactor))
                } else {
                    connections.add(Connection(baseAsteroid, targetAsteroid, -normalizedDirectionVector, -scalingFactor))
                }
            }
        }
    }

    val connectionsByBase = connections.groupBy { it.base }

    val (bestBase, highestAmountObservableAsteroids) = connectionsByBase
            .mapValues { (_, connections) ->
                connections.groupBy { it.direction }
                        .mapValues { (_, connections) ->
                            if (connections.any()) 1 else 0
                        }
                        .values
                        .sum()
            }
            .maxBy { it.value }!!

    println("Part 1: $highestAmountObservableAsteroids")

    val connectionsFromBestBase: List<Connection> = connectionsByBase.getValue(bestBase)

    val directionVectors: List<Vector> = connectionsFromBestBase
            .map { it.direction }
            .toSet()
            .sortedBy { -atan2(it.x, it.y) }

    val mutableConnectionsByDirection: Map<Vector, MutableSet<Connection>> = connectionsFromBestBase
            .groupBy { it.direction }
            .mapValues { (_, connections) -> connections.toMutableSet() }

    var destroyedAsteroidCount = 0

    destroy@ while (true) {
        for (direction in directionVectors) {
            val asteroidConnectionsInLine = mutableConnectionsByDirection.getValue(direction)
            val firstAsteroidConnectionInSight = asteroidConnectionsInLine.minBy { it.scaleFactor }

            if (firstAsteroidConnectionInSight != null) {
                asteroidConnectionsInLine.remove(firstAsteroidConnectionInSight)
                destroyedAsteroidCount++

                if (destroyedAsteroidCount == 200) {
                    firstAsteroidConnectionInSight.destination.run { println("Part 2: ${x * 100 + y}") }
                    break@destroy
                }
            }
        }
    }
}