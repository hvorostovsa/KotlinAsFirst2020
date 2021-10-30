@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import kotlin.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.max

/**
 * Точка (гекс) на шестиугольной сетке.
 * Координаты заданы как в примере (первая цифра - y, вторая цифра - x)
 *
 *       60  61  62  63  64  65
 *     50  51  52  53  54  55  56
 *   40  41  42  43  44  45  46  47
 * 30  31  32  33  34  35  36  37  38
 *   21  22  23  24  25  26  27  28
 *     12  13  14  15  16  17  18
 *       03  04  05  06  07  08
 *
 * В примерах к задачам используются те же обозначения точек,
 * к примеру, 16 соответствует HexPoint(x = 6, y = 1), а 41 -- HexPoint(x = 1, y = 4).
 *
 * В задачах, работающих с шестиугольниками на сетке, считать, что они имеют
 * _плоскую_ ориентацию:
 *  __
 * /  \
 * \__/
 *
 * со сторонами, параллельными координатным осям сетки.
 *
 * Более подробно про шестиугольные системы координат можно почитать по следующей ссылке:
 *   https://www.redblobgames.com/grids/hexagons/
 */
data class HexPoint(val x: Int, val y: Int) {
    /**
     * Средняя (3 балла)
     *
     * Найти целочисленное расстояние между двумя гексами сетки.
     * Расстояние вычисляется как число единичных отрезков в пути между двумя гексами.
     * Например, путь межу гексами 16 и 41 (см. выше) может проходить через 25, 34, 43 и 42 и имеет длину 5.
     */
    fun distance(other: HexPoint): Int = when {
        (x >= other.x && y >= other.y) || (x <= other.x && y <= other.y) -> abs(x - other.x) + abs(y - other.y)
        else -> max(abs(x - other.x), abs(y - other.y))
    }

    override fun toString(): String = "$y.$x"
}

/**
 * Правильный шестиугольник на гексагональной сетке.
 * Как окружность на плоскости, задаётся центральным гексом и радиусом.
 * Например, шестиугольник с центром в 33 и радиусом 1 состоит из гексов 42, 43, 34, 24, 23, 32.
 */
data class Hexagon(val center: HexPoint, val radius: Int) {

    /**
     * Средняя (3 балла)
     *
     * Рассчитать расстояние между двумя шестиугольниками.
     * Оно равно расстоянию между ближайшими точками этих шестиугольников,
     * или 0, если шестиугольники имеют общую точку.
     *
     * Например, расстояние между шестиугольником A с центром в 31 и радиусом 1
     * и другим шестиугольником B с центром в 26 и радиуоом 2 равно 2
     * (расстояние между точками 32 и 24)
     */
    fun distance(other: Hexagon): Int =
        max(center.distance(other.center) - radius - other.radius, 0)

    /**
     * Тривиальная (1 балл)
     *
     * Вернуть true, если заданная точка находится внутри или на границе шестиугольника
     */
    fun contains(point: HexPoint): Boolean = center.distance(point) <= radius
}

/**
 * Прямолинейный отрезок между двумя гексами
 */
class HexSegment(val begin: HexPoint, val end: HexPoint) {
    /**
     * Простая (2 балла)
     *
     * Определить "правильность" отрезка.
     * "Правильным" считается только отрезок, проходящий параллельно одной из трёх осей шестиугольника.
     * Такими являются, например, отрезок 30-34 (горизонталь), 13-63 (прямая диагональ) или 51-24 (косая диагональ).
     * А, например, 13-26 не является "правильным" отрезком.
     */
    fun isValid(): Boolean = (begin != end) &&
            (begin.x == end.x || begin.y == end.y || begin.x + begin.y == end.x + end.y)

    /**
     * Средняя (3 балла)
     *
     * Вернуть направление отрезка (см. описание класса Direction ниже).
     * Для "правильного" отрезка выбирается одно из первых шести направлений,
     * для "неправильного" -- INCORRECT.
     */
    fun direction(): Direction = when {
        this.begin == this.end -> Direction.INCORRECT

        this.begin.y == this.end.y ->
            if (this.begin.x <= this.end.x) Direction.RIGHT else Direction.LEFT

        this.begin.x == this.end.x ->
            if (this.begin.y <= this.end.y) Direction.UP_RIGHT else Direction.DOWN_LEFT

        this.begin.x + this.begin.y == this.end.x + this.end.y ->
            if (this.begin.y <= this.end.y) Direction.UP_LEFT else Direction.DOWN_RIGHT

        else -> Direction.INCORRECT
    }

    override fun equals(other: Any?) =
        other is HexSegment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Направление отрезка на гексагональной сетке.
 * Если отрезок "правильный", то он проходит вдоль одной из трёх осей шестугольника.
 * Если нет, его направление считается INCORRECT
 */
enum class Direction {
    RIGHT,      // слева направо, например 30 -> 34
    UP_RIGHT,   // вверх-вправо, например 32 -> 62
    UP_LEFT,    // вверх-влево, например 25 -> 61
    LEFT,       // справа налево, например 34 -> 30
    DOWN_LEFT,  // вниз-влево, например 62 -> 32
    DOWN_RIGHT, // вниз-вправо, например 61 -> 25
    INCORRECT;  // отрезок имеет изгиб, например 30 -> 55 (изгиб в точке 35)

    /**
     * Простая (2 балла)
     *
     * Вернуть направление, противоположное данному.
     * Для INCORRECT вернуть INCORRECT
     */
    fun opposite(): Direction = when (this) {
        RIGHT -> LEFT
        UP_RIGHT -> DOWN_LEFT
        UP_LEFT -> DOWN_RIGHT
        LEFT -> RIGHT
        DOWN_LEFT -> UP_RIGHT
        DOWN_RIGHT -> UP_LEFT
        else -> INCORRECT
    }

    /**
     * Средняя (3 балла)
     *
     * Вернуть направление, повёрнутое относительно
     * заданного на 60 градусов против часовой стрелки.
     *
     * Например, для RIGHT это UP_RIGHT, для UP_LEFT это LEFT, для LEFT это DOWN_LEFT.
     * Для направления INCORRECT бросить исключение IllegalArgumentException.
     * При решении этой задачи попробуйте обойтись без перечисления всех семи вариантов.
     */
    fun next(): Direction {
        if (this == INCORRECT) throw IllegalArgumentException("Incorrect direction")
        val data = listOf(UP_LEFT, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN_LEFT, LEFT)
        return data[(data.indexOf(this) + data.size - 1) % data.size]
    }

    /**
     * Простая (2 балла)
     *
     * Вернуть true, если данное направление совпадает с other или противоположно ему.
     * INCORRECT не параллельно никакому направлению, в том числе другому INCORRECT.
     */
    fun isParallel(other: Direction): Boolean = when (this) {
        INCORRECT -> false
        else -> this == other || this == other.opposite()
    }
}

/**
 * Средняя (3 балла)
 *
 * Сдвинуть точку в направлении direction на расстояние distance.
 * Бросить IllegalArgumentException(), если задано направление INCORRECT.
 * Для расстояния 0 и направления не INCORRECT вернуть ту же точку.
 * Для отрицательного расстояния сдвинуть точку в противоположном направлении на -distance.
 *
 * Примеры:
 * 30, direction = RIGHT, distance = 3 --> 33
 * 35, direction = UP_LEFT, distance = 2 --> 53
 * 45, direction = DOWN_LEFT, distance = 4 --> 05
 */
fun HexPoint.move(direction: Direction, distance: Int): HexPoint {
    var directionClone = direction
    var distanceClone = distance
    if (directionClone == Direction.INCORRECT) throw IllegalArgumentException("Incorrect direction")
    if (directionClone in listOf(Direction.LEFT, Direction.DOWN_LEFT, Direction.DOWN_RIGHT)) {
        directionClone = directionClone.opposite()
        distanceClone *= -1
    }
    return when (directionClone) {
        Direction.RIGHT -> HexPoint(this.x + distanceClone, this.y)
        Direction.UP_RIGHT -> HexPoint(this.x, this.y + distanceClone)
        Direction.UP_LEFT -> HexPoint(this.x - distanceClone, this.y + distanceClone)
        else -> throw IllegalArgumentException("Something went wrong") // Never be reached
    }
}

/**
 * Сложная (5 баллов)
 *
 * Найти кратчайший путь между двумя заданными гексами, представленный в виде списка всех гексов,
 * которые входят в этот путь.
 * Начальный и конечный гекс также входят в данный список.
 * Если кратчайших путей существует несколько, вернуть любой из них.
 *
 * Пример (для координатной сетки из примера в начале файла):
 *   pathBetweenHexes(HexPoint(y = 2, x = 2), HexPoint(y = 5, x = 3)) ->
 *     listOf(
 *       HexPoint(y = 2, x = 2),
 *       HexPoint(y = 2, x = 3),
 *       HexPoint(y = 3, x = 3),
 *       HexPoint(y = 4, x = 3),
 *       HexPoint(y = 5, x = 3)
 *     )
 */
fun pathBetweenHexes(from: HexPoint, to: HexPoint): List<HexPoint> {
    val trajectoryX = if (from.x <= to.x) 1 else -1
    val trajectoryY = if (from.y <= to.y) 1 else -1
    val list = mutableListOf(from)
    val number = from.distance(to)
    var lastX = from.x
    var lastY = from.y
    if ((from.x >= to.x && from.y >= to.y) || (from.x <= to.x && from.y <= to.y)) {
        for (i in 0 until number - 1) {
            if (lastX != to.x) lastX += trajectoryX
            else lastY += trajectoryY
            list.add(HexPoint(lastX, lastY))
        }
    } else {
        for (i in 0 until number - 1) {
            if (lastX != to.x) {
                lastX += trajectoryX
                if (lastY != to.y) lastY += trajectoryY
            } else lastY += trajectoryY
            list.add(HexPoint(lastX, lastY))
        }
    }
    list.add(to)
    return list
}

/**
 * Очень сложная (20 баллов)
 *
 * Дано три точки (гекса). Построить правильный шестиугольник, проходящий через них
 * (все три точки должны лежать НА ГРАНИЦЕ, а не ВНУТРИ, шестиугольника).
 * Все стороны шестиугольника должны являться "правильными" отрезками.
 * Вернуть null, если такой шестиугольник построить невозможно.
 * Если шестиугольников существует более одного, выбрать имеющий минимальный радиус.
 *
 * Пример: через точки 13, 32 и 44 проходит правильный шестиугольник с центром в 24 и радиусом 2.
 * Для точек 13, 32 и 45 такого шестиугольника не существует.
 * Для точек 32, 33 и 35 следует вернуть шестиугольник радиусом 3 (с центром в 62 или 05).
 *
 * Если все три точки совпадают, вернуть шестиугольник нулевого радиуса с центром в данной точке.
 */
fun hexagonByThreePoints(a: HexPoint, b: HexPoint, c: HexPoint): Hexagon? {
    if (a == b && b == c) return Hexagon(a, 0)

    val maxRadius = max(max(a.distance(b), a.distance(c)), b.distance(c))
    val minRadius = max(1, maxRadius / 2)

    for (radius in minRadius..maxRadius) {
        val center = getCenter(a, b, c, radius)
        if (center != null) return Hexagon(center, radius)
    }

    return null
}

fun getAllPointsInDistance(center: HexPoint, distance: Int): Set<HexPoint> {
    // Иду по кругу на расстоянии distance, все пройденные клетки записываю в set
    val set = mutableSetOf<HexPoint>()
    var pointNow = HexPoint(center.x, center.y + distance)
    var direction = Direction.LEFT
    for (i in 0 until 6) {
        for (ii in 0 until distance) {
            set.add(pointNow)
            pointNow = pointNow.move(direction, 1)
        }
        direction = direction.next()
    }
    return set
}

fun getCenter(a: HexPoint, b: HexPoint, c: HexPoint, radius: Int): HexPoint? {
    val points = getAllPointsInDistance(a, radius)
    var pointNow = HexPoint(b.x, b.y + radius)
    var direction = Direction.LEFT
    for (i in 0 until 6) {
        for (ii in 0 until radius) {
            if (pointNow in points && pointNow.distance(c) == radius) {
                return pointNow
            }
            pointNow = pointNow.move(direction, 1)
        }
        direction = direction.next()
    }
    return null
}

/**
 * Очень сложная (20 баллов)
 *
 * Дано множество точек (гексов). Найти правильный шестиугольник минимального радиуса,
 * содержащий все эти точки (безразлично, внутри или на границе).
 * Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит один гекс, вернуть шестиугольник нулевого радиуса с центром в данной точке.
 *
 * Пример: 13, 32, 45, 18 -- шестиугольник радиусом 3 (с центром, например, в 15)
 */
fun minContainingHexagon(vararg points: HexPoint): Hexagon = TODO()



