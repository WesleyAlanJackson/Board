package board

import board.Direction.*
import java.lang.IllegalArgumentException
import kotlin.math.ceil

open class SquareBoardImpl(override val width: Int) : SquareBoard
{

	private var board: List<Cell> = mutableListOf()

	init
	{
		board = initBoard()
	}

	private fun initBoard(): ArrayList<Cell>
	{
		val b: ArrayList<Cell> = mutableListOf<Cell>() as ArrayList<Cell>
		(1..width).forEach { i ->
			(1..width).forEach { j ->
				b.add(Cell(i, j))
			}
		}
		return b
	}

	override fun getCellOrNull(i: Int, j: Int): Cell? = if (board.filter { it.i == i && it.j == j }.isNotEmpty())
		board.filter { it.i == i && it.j == j }[0] else null

	override fun getCell(i: Int, j: Int): Cell = if (board.filter { it.i == i && it.j == j }.isNotEmpty())
		board.filter { it.i == i && it.j == j }[0] else throw IllegalArgumentException()

	override fun getAllCells(): Collection<Cell> = board

	//	override fun getRow(i: Int, jRange: IntProgression): List<Cell> = board.filter { it.i == i }.sortedWith(compareBy { cell ->  jRange.first > cell.j})
	override fun getRow(i: Int, jRange: IntProgression): List<Cell> =
			when
			{
				jRange.last > width -> IntRange(jRange.first, width).map { j: Int -> getCell(i, j) }.toList()
				else -> jRange.map { j: Int -> getCell(i, j) }.toList()
			}

	override fun getColumn(iRange: IntProgression, j: Int): List<Cell> =
			when
			{
				iRange.last > width -> IntRange(iRange.first, width).map { i: Int -> getCell(i, j) }.toList()
				else -> iRange.map { i: Int -> getCell(i, j) }.toList()
			}

	override fun Cell.getNeighbour(direction: Direction): Cell? = when (direction)
	{
		UP -> getCellOrNull(i - 1, j)
		DOWN -> getCellOrNull(i + 1, j)
		RIGHT -> getCellOrNull(i, j + 1)
		LEFT -> getCellOrNull(i, j - 1)
	}
}

private class GameBoardImpl<T>(board: SquareBoard) :
		GameBoard<T>, SquareBoard by board
{

	private val cellValues: MutableMap<Cell, T?> = getAllCells().associateWithTo(mutableMapOf(), { null })

	override fun get(cell: Cell): T? = cellValues[cell]

	override fun set(cell: Cell, value: T?)
	{
		cellValues[cell] = value
	}

	override fun filter(predicate: (T?) -> Boolean): Collection<Cell> = cellValues.filterValues(predicate).keys

	override fun find(predicate: (T?) -> Boolean): Cell? = cellValues.filter { predicate.invoke(it.value) }.keys.first()

	override fun any(predicate: (T?) -> Boolean): Boolean = cellValues.values.any(predicate)

	override fun all(predicate: (T?) -> Boolean): Boolean = cellValues.values.all(predicate)

}

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(createSquareBoard(width))

