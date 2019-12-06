package aoc2019.day04

import java.io.File

private fun String.isSorted() = this == toCharArray().sorted().joinToString("")
private fun String.hasDuplicate() = length != toSet().size
private fun String.hasDouble() = groupingBy { it }.eachCount().values.contains(2)

private fun isValidPassword1(password: String) = password.isSorted() && password.hasDuplicate()
private fun isValidPassword2(password: String) = password.isSorted() && password.hasDouble()

fun main() {
    val (start, end) = File("input/day04.txt").readText().split("-").map { it.toInt() }

    val validPasswordCount1 = (start..end).count { isValidPassword1(it.toString()) }
    val validPasswordCount2 = (start..end).count { isValidPassword2(it.toString()) }

    println("Part 1: $validPasswordCount1")
    println("Part 2: $validPasswordCount2")
}