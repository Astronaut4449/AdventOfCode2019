package aoc2019.day01

import java.io.File

private fun fuelConsumption(mass: Int) = (mass / 3 - 2).coerceAtLeast(0)

fun main() {
    val moduleMasses = File("input/day01.txt").readLines().map { it.toInt() }

    val moduleFuelConsumptions = moduleMasses.map { mass -> fuelConsumption(mass) }
    val moduleFuelConsumptionTotal = moduleFuelConsumptions.sum()

    println("Part 1: $moduleFuelConsumptionTotal")

    var fuelConsumptions = moduleFuelConsumptions
    var fuelTotal = moduleFuelConsumptionTotal
    do {
        fuelConsumptions = fuelConsumptions.map { mass -> fuelConsumption(mass) }
        val additionalFuel = fuelConsumptions.sum()
        fuelTotal += additionalFuel
    } while (additionalFuel > 0)

    println("Part 2: $fuelTotal")
}