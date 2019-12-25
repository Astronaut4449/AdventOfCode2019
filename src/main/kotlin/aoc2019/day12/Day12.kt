package aoc2019.day12

import java.io.File
import kotlin.math.abs

fun main() {
    run {
        var planets = planetsFromInput()

        repeat(1000) {
            planets = planets.move()
        }

        val totalEnergy = planets.sumBy { it.energy }

        println("Part 1: $totalEnergy")
    }

    run {
        val planets = planetsFromInput()

        val x = planets.map { SingleDimensionMovement(it.position.x, it.velocity.x) }
        val y = planets.map { SingleDimensionMovement(it.position.y, it.velocity.y) }
        val z = planets.map { SingleDimensionMovement(it.position.z, it.velocity.z) }

        val repetition = listOf(x, y, z).map(::findRepetition).lowestCommonMultiple()

        println("Part 2: $repetition")
    }
}

private data class Vector(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y, z - other.z)
}

private typealias Position = Vector
private typealias Velocity = Vector

private class Planet(val position: Position, val velocity: Velocity) {
    val energy by lazy {
        val potentialEnergy = abs(position.x) + abs(position.y) + abs(position.z)
        val kineticEnergy = abs(velocity.x) + abs(velocity.y) + abs(velocity.z)
        potentialEnergy * kineticEnergy
    }
}

private data class SingleDimensionMovement(val position: Int, val velocity: Int)

private fun List<Planet>.move(): List<Planet> = map { planet ->
    val otherPlanets = filterNot { it === planet }
    val newVelocity = otherPlanets.fold(planet.velocity) { velocity, otherPlanet ->
        velocity + gravity(planet, otherPlanet)
    }
    val newPosition = planet.position + newVelocity
    Planet(newPosition, newVelocity)
}

private fun gravity(a: Int, b: Int): Int = if (b > a) 1 else if (a > b) -1 else 0

private fun gravity(a: Planet, b: Planet) = Velocity(
        x = gravity(a.position.x, b.position.x),
        y = gravity(a.position.y, b.position.y),
        z = gravity(a.position.z, b.position.z)
)

private fun positionsFromInput(): List<Position> {
    val number = """-?\d+"""
    val positionPattern = """<x=($number), y=($number), z=($number)>""".toRegex()

    return File("input/day12.txt").readLines().map { line ->
        val (x, y, z) = positionPattern.matchEntire(line)!!.destructured.toList().map(String::toInt)
        Position(x, y, z)
    }
}

private fun planetsFromInput() = positionsFromInput().map { position -> Planet(position, Velocity(0, 0, 0)) }

private fun findRepetition(initialState: List<SingleDimensionMovement>): Long {
    var state = initialState
    var n = 0L

    do {
        state = state.nextState()
        n++
    } while (state differentFrom initialState)

    return n
}

private fun lowestCommonMultiple(a: Long, b: Long): Long {
    if (a == 0L || b == 0L) return 0L

    val (smallerNumber, biggerNumber) = listOf(abs(a), abs(b)).sorted()

    var lowestCommonMultiple = biggerNumber
    while (lowestCommonMultiple % smallerNumber != 0L) {
        lowestCommonMultiple += biggerNumber
    }

    return lowestCommonMultiple
}

private fun List<Long>.lowestCommonMultiple(): Long = reduce { acc, value -> lowestCommonMultiple(value, acc) }

private fun List<SingleDimensionMovement>.nextState(): List<SingleDimensionMovement> = map { planet ->
    val otherPlanets = filterNot { it === planet }

    val newVelocity = otherPlanets.fold(planet.velocity) { velocity, otherPlanet ->
        velocity + gravity(planet.position, otherPlanet.position)
    }

    val newPosition = planet.position + newVelocity

    SingleDimensionMovement(newPosition, newVelocity)
}

private infix fun List<SingleDimensionMovement>.differentFrom(other: List<SingleDimensionMovement>): Boolean {
    if (size != other.size) error("Cannot compare to lists of different sizes.")

    for (i in indices) {
        val thisItem = this[i]
        val otherItem = other[i]
        if (thisItem != otherItem) return true
    }

    return false
}