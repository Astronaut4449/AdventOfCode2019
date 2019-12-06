package aoc2019.day06

import java.io.File

private class Node(val children: MutableSet<Node> = mutableSetOf())

private fun Node.indirectConnectionCount(level: Int = 1): Int =
        children.count() * level + children.sumBy { it.indirectConnectionCount(level + 1) }

private infix fun Node.pathTo(target: Node): List<Node> =
        if (this == target) listOf(this)
        else children
                .map { nextNode -> nextNode pathTo target }
                .flatMap { path -> if (path.isEmpty()) emptyList() else listOf(this) + path }

fun main() {
    val nodeByName = mutableMapOf<String, Node>().apply {
        for (line in File("input/day06.txt").readLines()) {
            val (parent, child) = line.split(')')
            getOrPut(parent, { Node() }).children += getOrPut(child, { Node() })
        }
    }

    val centerOfMass = nodeByName["COM"]!!

    val orbitCount = centerOfMass.indirectConnectionCount()

    println("Part 1: $orbitCount")

    val santa = nodeByName["SAN"]!!
    val you = nodeByName["YOU"]!!

    val pathYou = centerOfMass pathTo you
    val pathSanta = centerOfMass pathTo santa

    val transferCount = ((pathYou union pathSanta) - (pathSanta intersect pathYou)).size - 2

    println("Part 2: $transferCount")
}
