package aoc2019.day08

import java.io.File

private const val WIDTH = 25
private const val HEIGHT = 6
private const val TRANSPARENT = 2
private const val BLACK = 0

val layers: List<List<List<Int>>> = File("input/day08.txt").readText()
        .chunked(1).map(String::toInt).chunked(WIDTH).chunked(HEIGHT)

fun main() {
    val layerFewestZeros = layers
            .map { layer -> layer.flatten() }
            .minBy { pixel -> pixel.count { it == 0 } }!!

    val charCount = layerFewestZeros.groupingBy { it }.eachCount()

    println("Part 1: ${(charCount[1] ?: 0) * (charCount[2] ?: 0)}")

    val image = Array(HEIGHT) { IntArray(WIDTH) }
    for (row in 0 until HEIGHT) {
        for (col in 0 until WIDTH) {
            layers@ for (layer in layers) {
                val pixel = layer[row][col]
                if (pixel != TRANSPARENT) {
                    image[row][col] = pixel
                    break@layers
                }
            }
        }
    }

    val imageBeautified = image.joinToString("\n") { row ->
        row.joinToString(" ") { pixel ->
            when (pixel) {
                BLACK -> "."
                else -> "#"
            }
        }
    }

    println("Part 2:\n$imageBeautified")
}

