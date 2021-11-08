@file:Suppress("UNUSED_PARAMETER")

package lesson9.task2

import lesson9.task1.Cell
import lesson9.task1.Matrix
import lesson9.task1.MatrixImpl
import lesson9.task1.createMatrix
import kotlin.IllegalArgumentException
import kotlin.math.ceil
import kotlin.math.min

// Все задачи в этом файле требуют наличия реализации интерфейса "Матрица" в Matrix.kt

/**
 * Пример
 *
 * Транспонировать заданную матрицу matrix.
 * При транспонировании строки матрицы становятся столбцами и наоборот:
 *
 * 1 2 3      1 4 6 3
 * 4 5 6  ==> 2 5 5 2
 * 6 5 4      3 6 4 1
 * 3 2 1
 */
fun <E> transpose(matrix: Matrix<E>): Matrix<E> {
    if (matrix.width < 1 || matrix.height < 1) return matrix
    val result = createMatrix(height = matrix.width, width = matrix.height, e = matrix[0, 0])
    for (i in 0 until matrix.width) {
        for (j in 0 until matrix.height) {
            result[i, j] = matrix[j, i]
        }
    }
    return result
}

/**
 * Пример
 *
 * Сложить две заданные матрицы друг с другом.
 * Складывать можно только матрицы совпадающего размера -- в противном случае бросить IllegalArgumentException.
 * При сложении попарно складываются соответствующие элементы матриц
 */
operator fun Matrix<Int>.plus(other: Matrix<Int>): Matrix<Int> {
    require(!(width != other.width || height != other.height))
    if (width < 1 || height < 1) return this
    val result = createMatrix(height, width, this[0, 0])
    for (i in 0 until height) {
        for (j in 0 until width) {
            result[i, j] = this[i, j] + other[i, j]
        }
    }
    return result
}

/**
 * Сложная (5 баллов)
 *
 * Заполнить матрицу заданной высоты height и ширины width
 * натуральными числами от 1 до m*n по спирали,
 * начинающейся в левом верхнем углу и закрученной по часовой стрелке.
 *
 * Пример для height = 3, width = 4:
 *  1  2  3  4
 * 10 11 12  5
 *  9  8  7  6
 */
fun generateSpiral(height: Int, width: Int): Matrix<Int> {
    val matrix = createMatrix(height, width, 0)
    var step = 0
    val value = ceil(min(height, width).toDouble() / 2).toInt() - 1
    val size = height * width

    for (i in 0 until value) {
        for (col in i until width - i) matrix[i, col] = ++step
        for (row in i + 1 until height - i) matrix[row, width - i - 1] = ++step
        for (col in width - i - 2 downTo i) matrix[height - i - 1, col] = ++step
        for (row in height - i - 2 downTo i + 1) matrix[row, i] = ++step
    }

    if (step == size) return matrix
    for (col in value until width - value) matrix[value, col] = ++step
    if (step == size) return matrix
    for (row in value + 1 until height - value) matrix[row, width - value - 1] = ++step
    if (step == size) return matrix
    for (col in width - value - 2 downTo value) matrix[height - value - 1, col] = ++step
    if (step == size) return matrix
    for (row in height - value - 2 downTo value + 1) matrix[row, value] = ++step

    return matrix
}

/**
 * Сложная (5 баллов)
 *
 * Заполнить матрицу заданной высоты height и ширины width следующим образом.
 * Элементам, находящимся на периферии (по периметру матрицы), присвоить значение 1;
 * периметру оставшейся подматрицы – значение 2 и так далее до заполнения всей матрицы.
 *
 * Пример для height = 5, width = 6:
 *  1  1  1  1  1  1
 *  1  2  2  2  2  1
 *  1  2  3  3  2  1
 *  1  2  2  2  2  1
 *  1  1  1  1  1  1
 */
fun generateRectangles(height: Int, width: Int): Matrix<Int> {
    val matrix = createMatrix(height, width, 0)
    for (row in 0 until height) {
        for (col in 0 until width) {
            matrix[row, col] = min(min(row, height - row - 1), min(col, width - col - 1)) + 1
        }
    }
    return matrix
}

/**
 * Сложная (5 баллов)
 *
 * Заполнить матрицу заданной высоты height и ширины width диагональной змейкой:
 * в левый верхний угол 1, во вторую от угла диагональ 2 и 3 сверху вниз, в третью 4-6 сверху вниз и так далее.
 *
 * Пример для height = 5, width = 4:
 *  1  2  4  7
 *  3  5  8 11
 *  6  9 12 15
 * 10 13 16 18
 * 14 17 19 20
 */
fun generateSnake(height: Int, width: Int): Matrix<Int> = TODO()

/**
 * Средняя (3 балла)
 *
 * Содержимое квадратной матрицы matrix (с произвольным содержимым) повернуть на 90 градусов по часовой стрелке.
 * Если height != width, бросить IllegalArgumentException.
 *
 * Пример:    Станет:
 * 1 2 3      7 4 1
 * 4 5 6      8 5 2
 * 7 8 9      9 6 3
 */
fun <E> rotate(matrix: Matrix<E>): Matrix<E> {
    if (matrix.width != matrix.height) throw IllegalArgumentException("Height must be equal to width")
    if (matrix.width < 1 || matrix.height < 1) return matrix
    val result = createMatrix(height = matrix.width, width = matrix.height, e = matrix[0, 0])
    for (i in 0 until matrix.width) {
        for (j in 0 until matrix.height) {
            result[i, j] = matrix[matrix.width - j - 1, i]
        }
    }
    return result
}

fun <E> Matrix<E>.isValidIndex(row: Int, column: Int): Boolean =
    (0 <= row) && (row < this.height) && (0 <= column) && (column < this.width)

fun <E> Matrix<E>.isValidIndex(cell: Cell) = this.isValidIndex(cell.row, cell.column)

/**
 * Сложная (5 баллов)
 *
 * Проверить, является ли квадратная целочисленная матрица matrix латинским квадратом.
 * Латинским квадратом называется матрица размером n x n,
 * каждая строка и каждый столбец которой содержат все числа от 1 до n.
 * Если height != width, вернуть false.
 *
 * Пример латинского квадрата 3х3:
 * 2 3 1
 * 1 2 3
 * 3 1 2
 */
fun isLatinSquare(matrix: Matrix<Int>): Boolean {
    if (matrix.height != matrix.width) return false
    val n = matrix.height

    fun validCell(number: Int): Boolean = (1 <= number) && (number <= n)

    var sum = 0
    for (col in 0 until matrix.width) {
        if (!validCell(matrix[0, col])) return false
        sum += matrix[0, col]
    }
    val controlSum = sum

    // Check all the rows
    for (row in 1 until matrix.height) {
        sum = 0
        for (col in 0 until matrix.width) {
            if (!validCell(matrix[row, col])) return false // In the same time checking that all the cells is valid
            sum += matrix[row, col]
        }
        if (sum != controlSum) return false
    }

    // Check all the columns
    for (col in 0 until matrix.width) {
        sum = 0
        for (row in 0 until matrix.height) sum += matrix[row, col]
        if (sum != controlSum) return false
    }

//    // Check main diagonal
//    sum = 0
//    for (k in 0 until n) sum += matrix[k, k]
//    if (sum != controlSum) return false
//
//    // Check secondary diagonal
//    sum = 0
//    for (k in 0 until n) sum += matrix[k, n - k - 1]
//    if (sum != controlSum) return false

    return true
}

/**
 * Средняя (3 балла)
 *
 * В матрице matrix каждый элемент заменить суммой непосредственно примыкающих к нему
 * элементов по вертикали, горизонтали и диагоналям.
 *
 * Пример для матрицы 4 x 3: (11=2+4+5, 19=1+3+4+5+6, ...)
 * 1 2 3       11 19 13
 * 4 5 6  ===> 19 31 19
 * 6 5 4       19 31 19
 * 3 2 1       13 19 11
 *
 * Поскольку в матрице 1 х 1 примыкающие элементы отсутствуют,
 * для неё следует вернуть как результат нулевую матрицу:
 *
 * 42 ===> 0
 */
fun sumNeighbours(matrix: Matrix<Int>): Matrix<Int> {
    val result = createMatrix(matrix.height, matrix.width, 0)

    val combinations = mutableSetOf<Pair<Int, Int>>()
    for (i in -1..1) {
        for (j in -1..1) if (i != 0 || j != 0) combinations.add(i to j)
    }

    for (row in 0 until matrix.height) {
        for (col in 0 until matrix.width) {
            for ((rowModifier, colModifier) in combinations) {
                val newRow = row + rowModifier
                val newCol = col + colModifier
                if (matrix.isValidIndex(newRow, newCol)) result[row, col] += matrix[newRow, newCol]
            }
        }
    }

    return result
}


/**
 * Средняя (4 балла)
 *
 * Целочисленная матрица matrix состоит из "дырок" (на их месте стоит 0) и "кирпичей" (на их месте стоит 1).
 * Найти в этой матрице все ряды и колонки, целиком состоящие из "дырок".
 * Результат вернуть в виде Holes(rows = список дырчатых рядов, columns = список дырчатых колонок).
 * Ряды и колонки нумеруются с нуля. Любой из спискоов rows / columns может оказаться пустым.
 *
 * Пример для матрицы 5 х 4:
 * 1 0 1 0
 * 0 0 1 0
 * 1 0 0 0 ==> результат: Holes(rows = listOf(4), columns = listOf(1, 3)): 4-й ряд, 1-я и 3-я колонки
 * 0 0 1 0
 * 0 0 0 0
 */
fun findHoles(matrix: Matrix<Int>): Holes {
    val badRows = mutableSetOf<Int>()
    val badCols = mutableSetOf<Int>()

    for (row in 0 until matrix.height) {
        for (col in 0 until matrix.width) {
            if (matrix[row, col] == 1) {
                badRows.add(row)
                badCols.add(col)
            }
        }
    }

    return Holes(
        rows = List(matrix.height) { it }.filter { it !in badRows },
        columns = List(matrix.width) { it }.filter { it !in badCols },
    )
}

/**
 * Класс для описания местонахождения "дырок" в матрице
 */
data class Holes(val rows: List<Int>, val columns: List<Int>)

/**
 * Средняя (3 балла)
 *
 * В целочисленной матрице matrix каждый элемент заменить суммой элементов подматрицы,
 * расположенной в левом верхнем углу матрицы matrix и ограниченной справа-снизу данным элементом.
 *
 * Пример для матрицы 3 х 3:
 *
 * 1  2  3      1  3  6
 * 4  5  6  =>  5 12 21
 * 7  8  9     12 27 45
 *
 * К примеру, центральный элемент 12 = 1 + 2 + 4 + 5, элемент в левом нижнем углу 12 = 1 + 4 + 7 и так далее.
 */
fun sumSubMatrix(matrix: Matrix<Int>): Matrix<Int> {
    fun calculateSubMatrixSum(toRow: Int, toColumn: Int): Int {
        // toRow, toColumn включительно
        var sum = 0
        for (row in 0..toRow) {
            for (col in 0..toColumn) sum += matrix[row, col]
        }
        return sum
    }

    val result = createMatrix(matrix.height, matrix.width, 0)

    for (row in 0 until matrix.height) {
        for (col in 0 until matrix.width) {
            result[row, col] = calculateSubMatrixSum(row, col)
        }
    }

    return result

}

/**
 * Простая (2 балла)
 *
 * Инвертировать заданную матрицу.
 * При инвертировании знак каждого элемента матрицы следует заменить на обратный
 */
operator fun Matrix<Int>.unaryMinus(): Matrix<Int> {
    for (row in 0 until this.height) {
        for (col in 0 until this.width) {
            this[row, col] *= -1
        }
    }
    return this
}

/**
 * Средняя (4 балла)
 *
 * Перемножить две заданные матрицы друг с другом.
 * Матрицы можно умножать, только если ширина первой матрицы совпадает с высотой второй матрицы.
 * В противном случае бросить IllegalArgumentException.
 * Подробно про порядок умножения см. статью Википедии "Умножение матриц".
 */
operator fun Matrix<Int>.times(other: Matrix<Int>): Matrix<Int> {
    if (this.width != other.height)
        throw IllegalArgumentException("Width of the first matrix must be equal to height of the second matrix")

    val result = createMatrix(this.height, other.width, 0)

    for (row in 0 until result.height) {
        for (col in 0 until result.width) {
            var sum = 0
            for (k in 0 until this.width) {
                sum += this[row, k] * other[k, col]
            }
            result[row, col] = sum
        }
    }

    return result
}

/**
 * Сложная (7 баллов)
 *
 * Даны мозаичные изображения замочной скважины и ключа. Пройдет ли ключ в скважину?
 * То есть даны две матрицы key и lock, key.height <= lock.height, key.width <= lock.width, состоящие из нулей и единиц.
 *
 * Проверить, можно ли наложить матрицу key на матрицу lock (без поворота, разрешается только сдвиг) так,
 * чтобы каждой единице в матрице lock (штырь) соответствовал ноль в матрице key (прорезь),
 * а каждому нулю в матрице lock (дырка) соответствовала, наоборот, единица в матрице key (штырь).
 * Ключ при сдвиге не может выходить за пределы замка.
 *
 * Пример: ключ подойдёт, если его сдвинуть на 1 по ширине
 * lock    key
 * 1 0 1   1 0
 * 0 1 0   0 1
 * 1 1 1
 *
 * Вернуть тройку (Triple) -- (да/нет, требуемый сдвиг по высоте, требуемый сдвиг по ширине).
 * Если наложение невозможно, то первый элемент тройки "нет" и сдвиги могут быть любыми.
 */
fun canOpenLock(key: Matrix<Int>, lock: Matrix<Int>): Triple<Boolean, Int, Int> {
    if (key.width > lock.width || key.height > lock.height) return Triple(false, -1, -1)

    fun isKeyFits(offsetRow: Int, offsetCol: Int): Boolean {
        for (row in 0 until key.height) {
            for (col in 0 until key.width)
                if (key[row, col] == lock[row + offsetRow, col + offsetCol]) return false
        }
        return true
    }

    for (offsetRow in 0..lock.height - key.height) {
        for (offsetCol in 0..lock.width - key.width) {
            if (isKeyFits(offsetRow, offsetCol)) return Triple(true, offsetRow, offsetCol)
        }
    }

    return Triple(false, -1, -1)
}

/**
 * Сложная (8 баллов)
 *
 * В матрице matrix размером 4х4 дана исходная позиция для игры в 15, например
 *  5  7  9  1
 *  2 12 14 15
 *  3  4  6  8
 * 10 11 13  0
 *
 * Здесь 0 обозначает пустую клетку, а 1-15 – фишки с соответствующими номерами.
 * Напомним, что "игра в 15" имеет квадратное поле 4х4, по которому двигается 15 фишек,
 * одна клетка всегда остаётся пустой. Цель игры -- упорядочить фишки на игровом поле.
 *
 * В списке moves задана последовательность ходов, например [8, 6, 13, 11, 10, 3].
 * Ход задаётся номером фишки, которая передвигается на пустое место (то есть, меняется местами с нулём).
 * Фишка должна примыкать к пустому месту по горизонтали или вертикали, иначе ход не будет возможным.
 * Все номера должны быть в пределах от 1 до 15.
 * Определить финальную позицию после выполнения всех ходов и вернуть её.
 * Если какой-либо ход является невозможным или список содержит неверные номера,
 * бросить IllegalStateException.
 *
 * В данном случае должно получиться
 * 5  7  9  1
 * 2 12 14 15
 * 0  4 13  6
 * 3 10 11  8
 */
fun fifteenGameMoves(matrix: Matrix<Int>, moves: List<Int>): Matrix<Int> {
    val size = 4
    val gameName = 15

//    // check if matrix is correct
//    if (matrix.width != size || matrix.height != size) // if incorrect size
//        throw IllegalStateException("Incorrect size of the matrix")
//    val numbersSet = mutableSetOf<Int>()
//    for (row in 0 until matrix.height) {
//        for (col in 0 until matrix.width) {
//            if (matrix[row, col] < 0 || matrix[row, col] > gameName) // if incorrect number in matrix
//                throw IllegalStateException("Incorrect numbers")
//            if (matrix[row, col] in numbersSet) // if some numbers were duplicated
//                throw IllegalStateException("Several identical numbers")
//        }
//    }

    // check if moves is correct
    for (move in moves) if (move !in 1..15) throw IllegalStateException("Incorrect moves")

    // find zero position
    var zeroPos = Pair(-1, -1)
    for (row in 0 until matrix.height) {
        for (col in 0 until matrix.width) {
            if (matrix[row, col] == 0) {
                zeroPos = Pair(row, col)
                break
            }
        }
        if (zeroPos.first != -1) break
    }

    // make moves
    val offsets = setOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1))
    var moveMade: Boolean
    for (move in moves) {
        moveMade = false
        for (offset in offsets) {
            val newRow = zeroPos.first + offset.first
            val newCol = zeroPos.second + offset.second
            if (!matrix.isValidIndex(newRow, newCol)) continue
            if (matrix[newRow, newCol] != move) continue
            matrix[zeroPos.first, zeroPos.second] = move
            matrix[newRow, newCol] = 0
            zeroPos = Pair(newRow, newCol)
            moveMade = true
            break
        }
        if (!moveMade) throw IllegalStateException("Unreachable moves")
    }

    return matrix
}

/**
 * Очень сложная (32 балла)
 *
 * В матрице matrix размером 4х4 дана исходная позиция для игры в 15, например
 *  5  7  9  2
 *  1 12 14 15
 *  3  4  6  8
 * 10 11 13  0
 *
 * Здесь 0 обозначает пустую клетку, а 1-15 – фишки с соответствующими номерами.
 * Напомним, что "игра в 15" имеет квадратное поле 4х4, по которому двигается 15 фишек,
 * одна клетка всегда остаётся пустой.
 *
 * Цель игры -- упорядочить фишки на игровом поле, приведя позицию к одному из следующих двух состояний:
 *
 *  1  2  3  4          1  2  3  4
 *  5  6  7  8   ИЛИ    5  6  7  8
 *  9 10 11 12          9 10 11 12
 * 13 14 15  0         13 15 14  0
 *
 * Можно математически доказать, что РОВНО ОДНО из этих двух состояний достижимо из любой исходной позиции.
 *
 * Вернуть решение -- список ходов, приводящих исходную позицию к одной из двух упорядоченных.
 * Каждый ход -- это перемена мест фишки с заданным номером с пустой клеткой (0),
 * при этом заданная фишка должна по горизонтали или по вертикали примыкать к пустой клетке (но НЕ по диагонали).
 * К примеру, ход 13 в исходной позиции меняет местами 13 и 0, а ход 11 в той же позиции невозможен.
 *
 * Одно из решений исходной позиции:
 *
 * [8, 6, 14, 12, 4, 11, 13, 14, 12, 4,
 * 7, 5, 1, 3, 11, 7, 3, 11, 7, 12, 6,
 * 15, 4, 9, 2, 4, 9, 3, 5, 2, 3, 9,
 * 15, 8, 14, 13, 12, 7, 11, 5, 7, 6,
 * 9, 15, 8, 14, 13, 9, 15, 7, 6, 12,
 * 9, 13, 14, 15, 12, 11, 10, 9, 13, 14,
 * 15, 12, 11, 10, 9, 13, 14, 15]
 *
 * Перед решением этой задачи НЕОБХОДИМО решить предыдущую
 */
fun fifteenGameSolution(matrix: Matrix<Int>): List<Int> = TODO()
