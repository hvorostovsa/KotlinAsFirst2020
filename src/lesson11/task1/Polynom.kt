@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

import kotlin.math.max

/**
 * Класс "полином с вещественными коэффициентами".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса -- полином от одной переменной (x) вида 7x^4+3x^3-6x^2+x-8.
 * Количество слагаемых неограничено.
 *
 * Полиномы можно складывать -- (x^2+3x+2) + (x^3-2x^2-x+4) = x^3-x^2+2x+6,
 * вычитать -- (x^3-2x^2-x+4) - (x^2+3x+2) = x^3-3x^2-4x+2,
 * умножать -- (x^2+3x+2) * (x^3-2x^2-x+4) = x^5+x^4-5x^3-3x^2+10x+8,
 * делить с остатком -- (x^3-2x^2-x+4) / (x^2+3x+2) = x-5, остаток 12x+16
 * вычислять значение при заданном x: при x=5 (x^2+3x+2) = 42.
 *
 * В конструктор полинома передаются его коэффициенты, начиная со старшего.
 * Нули в середине и в конце пропускаться не должны, например: x^3+2x+1 --> Polynom(1.0, 2.0, 0.0, 1.0)
 * Старшие коэффициенты, равные нулю, игнорировать, например Polynom(0.0, 0.0, 5.0, 3.0) соответствует 5x+3
 */
class Polynom(vararg coeffs: Double) {
    private val coeffs: MutableList<Double>

    init {
        val startIndex = coeffs.indexOfFirst { it != 0.0 }
        if (startIndex == -1) this.coeffs = mutableListOf()
        else {
            this.coeffs = MutableList(coeffs.size - startIndex) { 0.0 }
            for (i in 0 until this.coeffs.size) {
                this.coeffs[i] = coeffs[coeffs.size - 1 - i]
            }
        }
    }

    /**
     * Геттер: вернуть значение коэффициента при x^i
     */
    fun coeff(i: Int): Double = coeffs.getOrNull(i) ?: throw IndexOutOfBoundsException()

    /**
     * Расчёт значения при заданном x
     */
    fun getValue(x: Double): Double {
        var elemNow = 1.0
        var result = 0.0
        for (coeff in coeffs) {
            result += coeff * elemNow
            elemNow *= x
        }
        return result
    }

    /**
     * Степень (максимальная степень x при ненулевом слагаемом, например 2 для x^2+x+1).
     *
     * Степень полинома с нулевыми коэффициентами считать равной 0.
     * Слагаемые с нулевыми коэффициентами игнорировать, т.е.
     * степень 0x^2+0x+2 также равна 0.
     */
    fun degree(): Int = max(0, coeffs.size - 1)

    /**
     * Сложение
     */
    operator fun plus(other: Polynom): Polynom {
        val newCoeffs = MutableList(max(coeffs.size, other.coeffs.size)) { 0.0 }
        for (i in 0 until coeffs.size) {
            newCoeffs[i] += coeffs[i]
        }
        for (i in 0 until other.coeffs.size) {
            newCoeffs[i] += other.coeffs[i]
        }
        return Polynom(*newCoeffs.toDoubleArray().reversedArray())
    }

    /**
     * Смена знака (при всех слагаемых)
     */
    operator fun unaryMinus(): Polynom =
        Polynom(*coeffs.map { -it }.toDoubleArray().reversedArray())

    /**
     * Вычитание
     */
    operator fun minus(other: Polynom): Polynom = this + -other

    /**
     * Умножение
     */
    operator fun times(other: Polynom): Polynom {
        val newCoeffs = MutableList(coeffs.size + other.coeffs.size - 1) { 0.0 }
        for (i in 0 until coeffs.size) {
            for (j in 0 until other.coeffs.size) {
                newCoeffs[i + j] += coeffs[i] * other.coeffs[j]
            }
        }
        return Polynom(*newCoeffs.toDoubleArray().reversedArray())
    }

    /**
     * Деление
     *
     * Про операции деления и взятия остатка см. статью Википедии
     * "Деление многочленов столбиком". Основные свойства:
     *
     * Если A / B = C и A % B = D, то A = B * C + D и степень D меньше степени B
     */

    fun copy(): Polynom = Polynom(*coeffs.toDoubleArray().reversedArray())

    fun divmod(other: Polynom): Pair<Polynom, Polynom> {
        if (other.coeffs.size == 0) throw IllegalArgumentException("Can't divide by zero-Polynom")
        if (other.degree() > this.degree()) return Pair(Polynom(), this.copy())
        val coeffsNow = coeffs.map { it }.toMutableList() // copy
        val result = mutableListOf<Double>()
        while (true) {
            val startIndex = coeffsNow.size - other.coeffs.size
            if (startIndex < 0) break
            val n = coeffsNow[coeffsNow.size - 1] / other.coeffs[other.coeffs.size - 1]
            coeffsNow.removeAt(coeffsNow.size - 1)
            result.add(n)
            for (i in 0 until coeffsNow.size - startIndex) {
                coeffsNow[startIndex + i] -= n * other.coeffs[i]
            }
            while (coeffsNow.size > 1 && coeffsNow[coeffsNow.size - 1] == 0.0) { // remove top zero coefficients
                coeffsNow.removeAt(coeffsNow.size - 1)
            }
        }
        return Pair(
            Polynom(*result.toDoubleArray()),
            Polynom(*coeffsNow.toDoubleArray().reversedArray())
        )
    }

    operator fun div(other: Polynom): Polynom = this.divmod(other).first

    /**
     * Взятие остатка
     */
    operator fun rem(other: Polynom): Polynom = this.divmod(other).second

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean = other is Polynom && coeffs == other.coeffs

    /**
     * Получение хеш-кода
     */
    override fun hashCode(): Int = coeffs.hashCode()
}
