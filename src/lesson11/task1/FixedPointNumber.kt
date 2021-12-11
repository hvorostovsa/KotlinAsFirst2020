@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

import lesson3.task1.digitNumber
import ru.spbstu.wheels.defaultCompareTo
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Класс "вещественное число с фиксированной точкой"
 *
 * Общая сложность задания - сложная, общая ценность в баллах -- 20.
 * Объект класса - вещественное число с заданным числом десятичных цифр после запятой (precision, точность).
 * Например, для ограничения в три знака это может быть число 1.234 или -987654.321.
 * Числа можно складывать, вычитать, умножать, делить
 * (при этом точность результата выбирается как наибольшая точность аргументов),
 * а также сравнить на равенство и больше/меньше, преобразовывать в строку и тип Double.
 *
 * Вы можете сами выбрать, как хранить число в памяти
 * (в виде строки, целого числа, двух целых чисел и т.д.).
 * Представление числа должно позволять хранить числа с общим числом десятичных цифр не менее 9.
 */
class FixedPointNumber : Comparable<FixedPointNumber> {
    /**
     * Точность - число десятичных цифр после запятой.
     */
    val precision: Int
    private val first: Int  // Before dot
    private val second: Int // After dot
    private val isPositive: Boolean // if number >= 0
    private val maxPrecision = 9 // Ограничение на максимальную точность

    /**
     * Конструктор из строки, точность выбирается в соответствии
     * с числом цифр после десятичной точки.
     * Если строка некорректна или цифр слишком много,
     * бросить NumberFormatException.
     *
     * Внимание: этот или другой конструктор можно сделать основным
     */
    constructor(s: String) {
        val incorrectNumberException = NumberFormatException("Incorrect number")
        val dotIndex = s.indexOfFirst { it == '.' }

        if (dotIndex == -1) { // if no dot
            if (s.getOrNull(0) in setOf('-', '+')) {
                if (s.length > maxPrecision + 1) throw incorrectNumberException
            } else if (s.length > maxPrecision) throw incorrectNumberException
            try {
                first = abs(s.toInt())
                second = 0
                precision = 0
            } catch (e: Exception) {
                throw incorrectNumberException
            }
        } else { // if dot
            if (s.getOrNull(0) in setOf('-', '+')) {
                if (dotIndex > maxPrecision + 1) throw incorrectNumberException
            } else if (dotIndex > maxPrecision) throw incorrectNumberException
            if (s.length - dotIndex - 1 > maxPrecision) throw incorrectNumberException
            try {
                first = abs(s.substring(0, dotIndex).toInt())
                second =
                    if (s.length - dotIndex - 1 == 0) 0
                    else s.substring(dotIndex + 1, s.length).toInt()
                if (second < 0) throw incorrectNumberException
                precision = s.length - dotIndex - 1
            } catch (e: Exception) {
                throw incorrectNumberException
            }
        }
        isPositive = s.getOrNull(0) != '-'
    }

    /**
     * Конструктор из вещественного числа с заданной точностью
     */
    constructor(d: Double, p: Int) {
        val incorrectNumberException = NumberFormatException("Incorrect number")
        isPositive = d >= 0
        if (p > maxPrecision || p < 0) throw incorrectNumberException
        precision = p
        first = abs(d.toInt())
        if (digitNumber(first) > maxPrecision) throw incorrectNumberException
        val newSecondDouble = ((d - first) * 10.0.pow(precision))
        second = if ((newSecondDouble - newSecondDouble.toInt()) * 10 >= 5) {
            newSecondDouble.toInt() + 1
        } else {
            newSecondDouble.toInt()
        }

    }

    /**
     * Конструктор из целого числа (предполагает нулевую точность)
     */
    constructor(i: Int) {
        val incorrectNumberException = NumberFormatException("Incorrect number")
        isPositive = i >= 0
        precision = 0
        first = abs(i)
        if (digitNumber(first) > maxPrecision) throw incorrectNumberException
        second = 0
    }

    // Пользовательский конструктор
    private constructor(first: Int, second: Int? = null, precision: Int? = null, isPositive: Boolean? = null) {
        val incorrectNumberException = NumberFormatException("Incorrect number")
        if (digitNumber(first) > maxPrecision) throw incorrectNumberException
        if (second != null && (second < 0 ||
                    (if (second == 0) 0 else digitNumber(second)) > maxPrecision)
        )
            throw incorrectNumberException
        if (precision != null && (precision > maxPrecision || precision < 0)) throw incorrectNumberException
        this.first = abs(first)
        this.isPositive = isPositive ?: (first >= 0)
        if (precision != null) {
            this.precision = precision
            if (precision == 0 || second == null) this.second = 0
            else {
                val secondLength = if (second == 0) 0 else digitNumber(second)
                var secondNow = second
                // if secondLength > precision
                if (secondLength > precision) {
                    for (i in precision until secondLength - 1) secondNow /= 10
                    if (secondNow % 10 >= 5) secondNow++
                }

//                // if precision > secondLength
//                for (i in secondLength until precision) secondNow *= 10
                this.second = secondNow
            }
        } else {
            this.second = second ?: 0
            this.precision = if (this.second == 0) 0 else digitNumber(this.second)
        }
    }

    /**
     * Сложение.
     *
     * Здесь и в других бинарных операциях
     * точность результата выбирается как наибольшая точность аргументов.
     * Лишние знаки отрбрасываются, число округляется по правилам арифметики.
     */
    operator fun plus(other: FixedPointNumber): FixedPointNumber {
        val precision: Int
        val thisSecond: Int
        val otherSecond: Int
        val thisModifier = if (this.isPositive) 1 else -1
        val otherModifier = if (other.isPositive) 1 else -1
        if (this.precision >= other.precision) {
            precision = this.precision
            thisSecond = this.second
            otherSecond = other.second * 10.0.pow(this.precision - other.precision).toInt()
        } else {
            precision = other.precision
            thisSecond = this.second * 10.0.pow(other.precision - this.precision).toInt()
            otherSecond = other.second
        }
        var firstResult = thisModifier * this.first + otherModifier * other.first
        var secondResult = thisModifier * thisSecond + otherModifier * otherSecond
        firstResult += secondResult / 10.0.pow(precision).toInt()
        secondResult = abs(secondResult) % 10.0.pow(precision).toInt()

        return FixedPointNumber(
            abs(firstResult),
            secondResult,
            precision,
            firstResult >= 0,
        )
    }

    /**
     * Смена знака
     */
    operator fun unaryMinus(): FixedPointNumber = FixedPointNumber(first, second, precision, !isPositive)

    /**
     * Вычитание
     */
    operator fun minus(other: FixedPointNumber): FixedPointNumber = this + -other

    /**
     * Умножение
     */
    operator fun times(other: FixedPointNumber): FixedPointNumber {
        val precision = max(this.precision, other.precision)
        return FixedPointNumber(this.toDouble() * other.toDouble(), precision)
    }

    /**
     * Деление
     */
    operator fun div(other: FixedPointNumber): FixedPointNumber {
        val precision = min(this.precision, other.precision)
        return FixedPointNumber(this.toDouble() / other.toDouble(), precision)
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean =
        other is FixedPointNumber &&
                (first == other.first && second == other.second &&
                        precision == other.precision && isPositive == other.isPositive)

    /**
     * Сравнение на больше/меньше
     */
    override fun compareTo(other: FixedPointNumber): Int =
        ((this.toDouble() - other.toDouble()) * 10.0.pow(max(this.precision, other.precision))).toInt()

    /**
     * Преобразование в строку
     */
    override fun toString(): String {
        val result = StringBuilder()
        if (!isPositive) result.append("-")
        result.append(first)
        result.append(".")
        if (second == 0) result.append("0")
        else {
            result.append("0".repeat(precision - digitNumber(second)))
            result.append(second)
        }
        return result.toString()
    }

    /**
     * Преобразование к вещественному числу
     */
    fun toDouble(): Double = this.toString().toDouble()

    override fun hashCode(): Int {
        var result = precision
        result = 31 * result + first
        result = 31 * result + second
        result = 31 * result + isPositive.hashCode()
        return result
    }
}