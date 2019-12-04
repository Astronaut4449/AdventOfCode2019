package aoc2019.day04

import java.io.File

private fun String.sorted() = toCharArray().sorted().joinToString("")

private fun isValidPassword1(password: String): Boolean =
        (password == password.sorted()) && (password.length != password.toSet().size)

private fun isValidPassword2(password: String): Boolean =
        isValidPassword1(password) && password.groupingBy { it }.eachCount().values.contains(2)


fun main() {
    val (start, end) = File("input/day04.txt").readText().split("-").map { it.toInt() }

    val validPasswordCount1 = (start..end).count { isValidPassword1(it.toString()) }
    val validPasswordCount2 = (start..end).count { isValidPassword2(it.toString()) }

    println("Part 1: $validPasswordCount1")
    println("Part 2: $validPasswordCount2")
}