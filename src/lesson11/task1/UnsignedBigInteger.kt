@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

import kotlin.math.max

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

    private val list: MutableList<Int>

    /**
     * Конструктор из строки
     */
    constructor(s: String) {
        list = MutableList(s.length) {
            val digit = s[s.length - it - 1]
            if (digit.isDigit()) digit.digitToInt()
            else throw incorrectNumberException
        }
    }

    /**
     * Конструктор из целого
     */
    constructor(i: Int) {
        if (i < 0) throw incorrectNumberException
        list = mutableListOf()
        var numberNow = i
        while (numberNow > 0) {
            list.add(numberNow % 10)
            numberNow /= 10
        }
    }

    // Пользовательский конструктор
    private constructor(list: List<Int>) {
        this.list = list.toMutableList()
    }

    /**
     * Сложение
     */
    operator fun plus(other: UnsignedBigInteger): UnsignedBigInteger {
        val length = max(this.list.size, other.list.size)
        val newList = MutableList(length) { 0 }
        for (i in 0 until length - 1) {
            val digit = newList[i] +
                    this.list.getOrElse(i) { 0 } +
                    other.list.getOrElse(i) { 0 }
            newList[i] = digit % 10
            newList[i + 1] = digit / 10
        }
        val digit = newList[length - 1] +
                this.list.getOrElse(length - 1) { 0 } +
                other.list.getOrElse(length - 1) { 0 }
        newList[length - 1] = digit % 10
        if (digit / 10 > 0) newList.add(digit / 10)
        return UnsignedBigInteger(newList)
    }

    /**
     * Вычитание (бросить ArithmeticException, если this < other)
     */
    operator fun minus(other: UnsignedBigInteger): UnsignedBigInteger {
        val arithmeticException = ArithmeticException()
        if (other.list.size > this.list.size) throw arithmeticException
        val newList = this.list.toMutableList()

        if (newList.size > other.list.size) {
            for (i in 0 until other.list.size) {
                newList[i] -= other.list[i]
                if (newList[i] < 0) {
                    newList[i] += 10
                    newList[i + 1] -= 1
                }
            }
        } else {
            for (i in 0 until other.list.size - 1) {
                newList[i] -= other.list[i]
                if (newList[i] < 0) {
                    newList[i] += 10
                    newList[i + 1] -= 1
                }
            }
            val size = newList.size
            newList[size - 1] -= other.list[size - 1]
            if (newList[size - 1] < 0) throw arithmeticException
        }
        return UnsignedBigInteger(newList.subList(0, newList.indexOfLast { it != 0 } + 1))
    }

    /**
     * Умножение
     */
    operator fun times(other: UnsignedBigInteger): UnsignedBigInteger {
        val first: MutableList<Int>
        val second: MutableList<Int>
        if (this > other) {
            first = this.list
            second = other.list
        } else {
            first = other.list
            second = this.list
        }
        if (second.isEmpty()) return UnsignedBigInteger(0)

        var result = UnsignedBigInteger(0)
        for (i in 0 until second.size) {
            if (second[i] != 0) result += UnsignedBigInteger(shiftList(timesList(first, second[i]), i))
        }
        return result
    }

    private fun timesList(list: List<Int>, digit: Int): MutableList<Int> {
        if (digit == 0) return mutableListOf()
        val newList = list.toMutableList()
        if (digit == 1) return newList
        for (i in 0 until newList.size) newList[i] *= digit
        for (i in 0 until newList.size - 1) {
            newList[i + 1] += newList[i] / 10
            newList[i] %= 10
        }
        val size = newList.size
        var number = newList[size - 1] / 10
        newList[size - 1] %= 10
        while (number > 0) {
            newList.add(number % 10)
            number /= 10
        }
        return newList
    }

    // change existing list
    private fun shiftList(list: MutableList<Int>, shiftNumber: Int): MutableList<Int> {
        list.addAll(0, List(shiftNumber) { 0 })
        return list
    }

    fun divmod(other: UnsignedBigInteger): Pair<UnsignedBigInteger, UnsignedBigInteger> {
        if (this < other) return Pair(UnsignedBigInteger(0), UnsignedBigInteger(this.list))
        var first = UnsignedBigInteger(this.list)
        val second = other.list
        val size = second.size
        var result = mutableListOf<Int>()

        while (first >= other) {
            var k = first.list.size - size
            var n = UnsignedBigInteger(shiftList(second.toMutableList(), k))
            while (n > first && k > 0) {
                k -= 1
                n = UnsignedBigInteger(shiftList(second.toMutableList(), k))
            }
            var digit = 0
            while (first >= n) {
                digit++
                first -= n
            }
            if (result.size == 0) result = MutableList(k + 1) { 0 } // will be worked only during first iteration
            result[k] = digit
        }

        return Pair(UnsignedBigInteger(result), first)
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
    override fun toString(): String = list.reversed().joinToString("")

    /**
     * Преобразование в целое
     * Если число не влезает в диапазон Int, бросить ArithmeticException
     */
    fun toInt(): Int {
        if (this <= UnsignedBigInteger(Int.MAX_VALUE)) return this.toString().toInt()
        throw ArithmeticException("Number is too large for Int data type")
    }

}