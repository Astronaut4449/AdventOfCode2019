package aoc2019.day14

import java.io.File
import kotlin.math.ceil

fun main() {
    run {
        val orePerFuel = oreRequiredFor(fuelAmount = 1L)
        println("Part 1: $orePerFuel")
    }

    run {
        val availableOre = 1_000_000_000_000L

        var low = 0L
        var high = availableOre

        while (low < high) {
            val guessMiddle = (low + high + 1) / 2

            if (oreRequiredFor(guessMiddle) > availableOre) {
                high = guessMiddle - 1
            } else {
                low = guessMiddle
            }
        }

        println("Part 2: $low")
    }
}

private class Amount(val resource: String, val amount: Long)

private class Recipe(val output: Amount, val inputs: List<Amount>)

private val recipes: Map<String, Recipe> = File("input/day14.txt").readLines().map { line ->
    val (ingredients, output) = line.split(" => ")

    val (outputAmount, outputResource) = output.split(" ")

    val ingredientList = ingredients.split(", ").map {
        val (amount, input) = it.split(" ")
        Amount(input, amount.toLong())
    }

    Pair(outputResource, Recipe(
            Amount(outputResource, outputAmount.toLong()),
            ingredientList
    ))
}.toMap()

private fun oreRequiredFor(fuelAmount: Long): Long {
    val resourceDemands: MutableMap<String, Long> = mutableMapOf("FUEL" to fuelAmount).withDefault { 0 }

    fun isIntermediateProductsRequired(): Boolean = resourceDemands.entries.asSequence()
            .filterNot { it.key == "ORE" }
            .any { (_, demand) -> demand > 0 }

    while (isIntermediateProductsRequired()) {
        val (requiredProduct, requiredAmount) = resourceDemands.entries.asSequence()
                .filterNot { it.key == "ORE" }
                .first { (_, demand) -> demand > 0 }

        val recipe = recipes[requiredProduct]!!
        val repeat = ceil(requiredAmount.toFloat() / recipe.output.amount).toInt()

        resourceDemands[requiredProduct] = resourceDemands[requiredProduct]!! - (repeat * recipe.output.amount)

        for (input in recipe.inputs) {
            resourceDemands[input.resource] = resourceDemands.getValue(input.resource) + (repeat * input.amount)
        }
    }

    return resourceDemands["ORE"]!!
}
