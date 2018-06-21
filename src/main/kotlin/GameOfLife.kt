package com.rafasoares.kotlin

import processing.core.PApplet

inline fun <T> traverse(grid: List<List<T>>, body: (Int, Int, T) -> Unit) =
        grid.forEachIndexed { x, row -> row.forEachIndexed { y, cell -> body(x, y, cell) } }

inline fun <T> update(grid: List<List<T>>, body: (Int, Int, T) -> T): List<List<T>> =
        grid.mapIndexed { x, row -> row.mapIndexed { y, cell -> body(x, y, cell) } }

val Boolean.int
    get() = if (this) 1 else 0

val Boolean.color
    get() = if (this) 0 else 255

class GameOfLife : PApplet() {
    private val res = 10f
    private var grid = listOf<List<Boolean>>()

    override fun settings() {
        size(600, 600)
    }

    override fun setup() {
        frameRate(30f)
        background(255)
        grid = (0 until floor(width / res)).map {
            (0 until floor(height / res)).map {
                random(2f) > 1f
            }
        }
    }

    override fun draw() {
        color(50)

        traverse(grid) { x, y, value ->
            fill(value.color)
            rect(x * res, y * res, res, res)
        }

        updateGrid()
    }

    private fun updateGrid() {
        grid = update(grid) { x, y, value ->
            val neighbors = calculateNeighbors(x, y)
            when {
                value && neighbors < 2 -> false
                value && neighbors > 3 -> false
                !value && neighbors == 3 -> true
                else -> value
            }
        }
    }

    private fun calculateNeighbors(x: Int, y: Int): Int =
            (-1..1).flatMap { i ->
                (-1..1).map { j ->
                    if (i == 0 && j == 0) return@map 0
                    val xOff = wrapValue(x + i, grid)
                    val yOff = wrapValue(y + j, grid[xOff])
                    grid[xOff][yOff].int
                }
            }.sum()

    private fun wrapValue(value: Int, list: Collection<Any>) = when {
        value < 0 -> list.size - 1
        value >= list.size -> 0
        else -> value
    }
}

fun main(args: Array<String>) {
    PApplet.main("com.rafasoares.kotlin.GameOfLife")
}