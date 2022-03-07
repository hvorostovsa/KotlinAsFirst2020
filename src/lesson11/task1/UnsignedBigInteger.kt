@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

/**
 * Класс "беззнаковое большое целое число".
 *
 * Общая сложность задания -- очень сложная, общая ценность в баллах -- 32.
 * Объект класса содержит целое число без знака произвольного размера
 * и поддерживает основные операции над такими числами, а именно:
 * сложение, вычитание (при вычитании большего числа из меньшего бросается исключение),
 * умножение, деление, остаток от деления,
 * преобразование в строку/из строки, преобразование в целое/из целого,
 * сравнение на равенство и неравенство
 */
class UnsignedBigInteger : Comparable<UnsignedBigInteger> {
    private val incorrectNumberException = IllegalArgumentException("Incorrect number")
    private val blockSize = 9
    private val blockDelimiter = 10.0.pow(blockSize).toInt()

    private val list: MutableList<Int>

    // 1122334455667788 => [455667788, 1122334]

    /**
     * Конструктор из строки
     */
    constructor(s: String) {
        val newS = s.trimStart { it == '0' }
        list = mutableListOf()
        if (newS.isEmpty()) {
            list.add(0)
        } else {
            val listSize = newS.length / blockSize + if (newS.length % blockSize > 0) 1 else 0
            for (i in 0 until listSize) {
                val end = newS.length - i * blockSize // not inclusive
                val start = max(0, end - blockSize)
                list.add(newS.substring(start, end).toInt())
            }
        }
    }

    /**
     * Конструктор из целого
     */
    constructor(i: Int) {
        if (i < 0) throw incorrectNumberException
        if (i == 0) {
            list = mutableListOf(0)
        } else {
            list = mutableListOf()
            var number = i
            while (number > 0) {
                list.add(number % blockDelimiter)
                number /= blockDelimiter
            }
        }
    }

    // Пользовательский конструктор
    private constructor(list: List<Int>) {
        this.list = list.toMutableList()
    }

    val length get() = getDigitLength(list)

    private fun getDigitLength(list: MutableList<Int>): Int {
        // Get number of significant digits
        var result = 0
        for (i in 0 until list.size - 1) {
            result += blockSize
        }
        result += getNumberLength(list[list.size - 1])
        return result
    }

    private fun getDigit(list: MutableList<Int>, index: Int): Int {
        // get digit from list by its index
        val i = index / blockSize // number index
        val j = index % blockSize // digit index
        if ((i >= list.size) ||
            (i == list.size - 1 && j >= getNumberLength(list[i]))
        ) throw IndexOutOfBoundsException()
        val number = list[i]
        return number / 10.0.pow(j).toInt() % 10
    }

    private fun getDigit(index: Int): Int = getDigit(list, index)

    /**
     * Сложение
     */

    private fun plusIndex(
        otherBigInteger: UnsignedBigInteger,
        list: MutableList<Int>,
        index: Int
    ) {
        val digit = list[index] +  // Переполнения быть не может. (999 999 999 + 999 999 999 + 1) < Integer.MAX_VALUE
                this.list.getOrElse(index) { 0 } +
                otherBigInteger.list.getOrElse(index) { 0 }
        list[index] = digit % blockDelimiter
        val higherDigit = digit / blockDelimiter // Разряд выше
        if (index < list.size - 1) list[index + 1] = higherDigit
        else if (higherDigit > 0) list.add(higherDigit)
    }

    operator fun plus(other: UnsignedBigInteger): UnsignedBigInteger {
        val length = max(this.list.size, other.list.size)
        val newList = MutableList(length) { 0 }
        for (i in 0 until length) {
            plusIndex(other, newList, i)
        }
        return UnsignedBigInteger(newList)
    }

    /**
     * Вычитание (бросить ArithmeticException, если this < other)
     */

    private fun minusIndex(
        otherBigInteger: UnsignedBigInteger,
        list: MutableList<Int>,
        index: Int
    ) {
        list[index] -= otherBigInteger.list[index]
        if (list[index] < 0) {
            if (index == list.size - 1) throw ArithmeticException()
            list[index] += blockDelimiter
            list[index + 1] -= 1
        }
    }

    operator fun minus(other: UnsignedBigInteger): UnsignedBigInteger {
        val arithmeticException = ArithmeticException()
        if (other.list.size > this.list.size) throw arithmeticException
        val newList = this.list.toMutableList()

        for (i in 0 until other.list.size) {
            minusIndex(other, newList, i)
        }
        val resultList = newList.subList(0, newList.indexOfLast { it != 0 } + 1)
        return UnsignedBigInteger(if (resultList.isEmpty()) mutableListOf(0) else resultList)
    }

    /**
     * Умножение
     */
    operator fun times(other: UnsignedBigInteger): UnsignedBigInteger {
        val first: MutableList<Int> // biggest by List.size
        val second: MutableList<Int>  // lowest by List.size
        if (this > other) {
            first = this.list
            second = other.list
        } else {
            first = other.list
            second = this.list
        }
        if (second.isEmpty()) return UnsignedBigInteger(0)

        var result = UnsignedBigInteger(0)
        for (i in 0 until getDigitLength(second)) {
            val mulList = timesList(first, getDigit(second, i))
            if (getDigit(second, i) != 0) result += UnsignedBigInteger(shiftList(mulList, i))
        }
        return result
    }

    private fun timesList(list: List<Int>, digit: Int): MutableList<Int> {
        if (digit == 0) return mutableListOf()
        val newList = list.toMutableList()
        if (digit == 1) return newList

        val lowerBlockSize = blockSize - 1 // 8
        val lowerBlockDelimiter = 10.0.pow(lowerBlockSize).toInt() // 100 000 000

        var currentIndexResult: Int
        var nextIndexResult = 0

        for (i in 0 until newList.size) {
            if (getNumberLength(newList[i]) < blockSize) { // adaptation
                val result = newList[i] * digit + nextIndexResult
                currentIndexResult = result % blockDelimiter
                nextIndexResult = result / blockDelimiter
            } else {
                val firstDigit = newList[i] / lowerBlockDelimiter // 123 456 789 => 1
                val otherDigits = newList[i] % lowerBlockDelimiter // 123 456 789 => 23 456 789

                val otherDigitsMul = otherDigits * digit
                val firstDigitMul = firstDigit * digit + nextIndexResult

                currentIndexResult =
                    (firstDigitMul % 10 + otherDigitsMul / lowerBlockDelimiter) % 10 * lowerBlockDelimiter +
                            otherDigitsMul % lowerBlockDelimiter
                nextIndexResult = (firstDigitMul + otherDigitsMul / lowerBlockDelimiter) / 10
            }
            newList[i] = currentIndexResult
        }

        if (nextIndexResult > 0) newList.add(nextIndexResult)
        return newList
    }

    private fun getNumberLength(number: Int): Int {
        if (number == 0) return 0
        var n = abs(number)
        var length = 0
        while (n > 0) {
            length++
            n /= 10
        }
        return length
    }

    private fun shiftList(list: MutableList<Int>, shiftNumber: Int): MutableList<Int> {
        // change existing list, multiplies list by 10^shiftNumber
        val blocksToAdd = shiftNumber / blockSize
        val zerosToAdd = shiftNumber % blockSize
        val delimiter = 10.0.pow(blockSize - zerosToAdd).toInt()
        val multiplier = 10.0.pow(zerosToAdd).toInt()
        if (zerosToAdd > 0) {
            val size = list.size // remember current size
            if (getNumberLength(list[size - 1]) + zerosToAdd > blockSize) list.add(0)

            var toCurrentIndex: Int
            var toNextIndex: Int
            for (i in size - 1 downTo 0) {
                if (list[i] == 0) continue
                toCurrentIndex = list[i] % delimiter * multiplier
                toNextIndex = list[i] / delimiter
                list[i] = toCurrentIndex
                if (toNextIndex > 0) list[i + 1] += toNextIndex
            }
        }
        list.addAll(0, List(blocksToAdd) { 0 })
        return list
    }

    fun divmod(other: UnsignedBigInteger): Pair<UnsignedBigInteger, UnsignedBigInteger> {
        if (other.list.isEmpty() || other.list == mutableListOf(0)) throw ArithmeticException("Zero division error")
        if (this < other) return Pair(UnsignedBigInteger(0), UnsignedBigInteger(this.list))
        var first = UnsignedBigInteger(this.list)
        val second = other.list
        val result = StringBuilder()

        while (first >= other) {
            var shiftSize = first.length - getDigitLength(second)
            var n = UnsignedBigInteger(shiftList(second.toMutableList(), shiftSize))

            // здесь мы обрабатываем случай 12345 / 99000
            // 12345 / 99000 => 12345 / 9900
            while (n > first && shiftSize > 0) {
                shiftSize -= 1
                n = UnsignedBigInteger(shiftList(second.toMutableList(), shiftSize))
            }

            var digit = '0'
            while (first >= n) {
                digit++
                first -= n
            }
            result.append(digit)
        }

        return Pair(UnsignedBigInteger(result.toString()), first)
    }

    /**
     * Деление
     */
    operator fun div(other: UnsignedBigInteger): UnsignedBigInteger = divmod(other).first

    /**
     * Взятие остатка
     */
    operator fun rem(other: UnsignedBigInteger): UnsignedBigInteger = divmod(other).second

    /**
     * Сравнение на равенство (по контракту Any.equals)
     */
    override fun equals(other: Any?): Boolean = other is UnsignedBigInteger &&
            (list == other.list)

    /**
     * Сравнение на больше/меньше (по контракту Comparable.compareTo)
     */
    override fun compareTo(other: UnsignedBigInteger): Int {
        if (this.list.size > other.list.size) return 1
        if (this.list.size < other.list.size) return -1
        for (i in this.list.size - 1 downTo 0) {
            if (this.list[i] > other.list[i]) return 1
            if (this.list[i] < other.list[i]) return -1
        }
        return 0
    }

    /**
     * Преобразование в строку
     */
    override fun toString(): String =
        if (list.isNotEmpty()) list.reversed().joinToString("") else "0"

    /**
     * Преобразование в целое
     * Если число не влезает в диапазон Int, бросить ArithmeticException
     */
    fun toInt(): Int {
        if (this <= UnsignedBigInteger(Int.MAX_VALUE)) return this.toString().toInt()
        throw ArithmeticException("Number is too large for Int data type")
    }

    override fun hashCode(): Int = list.hashCode()

}