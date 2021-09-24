@file:Suppress("UNUSED_PARAMETER", "ConvertCallChainIntoSequence")

package lesson6.task1

import lesson2.task2.daysInMonth
import lesson4.task1.roman
import ru.spbstu.wheels.tryEx
import java.lang.IllegalArgumentException
import java.time.Year
import kotlin.math.max

// Урок 6: разбор строк, исключения
// Максимальное количество баллов = 13
// Рекомендуемое количество баллов = 11
// Вместе с предыдущими уроками (пять лучших, 2-6) = 40/54

/**
 * Пример
 *
 * Время представлено строкой вида "11:34:45", содержащей часы, минуты и секунды, разделённые двоеточием.
 * Разобрать эту строку и рассчитать количество секунд, прошедшее с начала дня.
 */
fun timeStrToSeconds(str: String): Int {
    val parts = str.split(":")
    var result = 0
    for (part in parts) {
        val number = part.toInt()
        result = result * 60 + number
    }
    return result
}

/**
 * Пример
 *
 * Дано число n от 0 до 99.
 * Вернуть его же в виде двухсимвольной строки, от "00" до "99"
 */
fun twoDigitStr(n: Int) = if (n in 0..9) "0$n" else "$n"

/**
 * Пример
 *
 * Дано seconds -- время в секундах, прошедшее с начала дня.
 * Вернуть текущее время в виде строки в формате "ЧЧ:ММ:СС".
 */
fun timeSecondsToStr(seconds: Int): String {
    val hour = seconds / 3600
    val minute = (seconds % 3600) / 60
    val second = seconds % 60
    return String.format("%02d:%02d:%02d", hour, minute, second)
}

/**
 * Пример: консольный ввод
 */
fun main() {
    println("Введите время в формате ЧЧ:ММ:СС")
    val line = readLine()
    if (line != null) {
        val seconds = timeStrToSeconds(line)
        if (seconds == -1) {
            println("Введённая строка $line не соответствует формату ЧЧ:ММ:СС")
        } else {
            println("Прошло секунд с начала суток: $seconds")
        }
    } else {
        println("Достигнут <конец файла> в процессе чтения строки. Программа прервана")
    }
}


/**
 * Средняя (4 балла)
 *
 * Дата представлена строкой вида "15 июля 2016".
 * Перевести её в цифровой формат "15.07.2016".
 * День и месяц всегда представлять двумя цифрами, например: 03.04.2011.
 * При неверном формате входной строки вернуть пустую строку.
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30.02.2009) считается неверными
 * входными данными.
 */
fun dateStrToDigit(str: String): String {
    val days: Int
    val months: Int
    val years: Int

    val data = str.trim().split(Regex("""\s+"""))
    if (data.size != 3) return ""

    try {
        days = data[0].toInt()
        months = when (data[1].lowercase()) {
            "января" -> 1
            "февраля" -> 2
            "марта" -> 3
            "апреля" -> 4
            "мая" -> 5
            "июня" -> 6
            "июля" -> 7
            "августа" -> 8
            "сентября" -> 9
            "октября" -> 10
            "ноября" -> 11
            "декабря" -> 12
            else -> throw NumberFormatException("Incorrect month")
        }
        years = data[2].toInt()
    } catch (e: NumberFormatException) {
        return ""
    }

    if (days < 1 || days > daysInMonth(months, years)) return ""
    return "%02d.%02d.%d".format(days, months, years)

}

/**
 * Средняя (4 балла)
 *
 * Дата представлена строкой вида "15.07.2016".
 * Перевести её в строковый формат вида "15 июля 2016".
 * При неверном формате входной строки вернуть пустую строку
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30 февраля 2009) считается неверными
 * входными данными.
 */
fun dateDigitToStr(digital: String): String {
    val days: Int
    val months: Int
    val years: Int

    val data = digital.trim().split(".")
    if (data.size != 3) return ""

    try {
        days = data[0].toInt()
        months = data[1].toInt()
        years = data[2].toInt()
    } catch (e: NumberFormatException) {
        return ""
    }

    if (years < 0) return ""
    if (months < 1 || months > 12) return ""
    if (days < 1 || days > daysInMonth(months, years)) return ""

    val monthsName = listOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря",
    )[months - 1]

    return "%d %s %d".format(days, monthsName, years)
}

/**
 * Средняя (4 балла)
 *
 * Номер телефона задан строкой вида "+7 (921) 123-45-67".
 * Префикс (+7) может отсутствовать, код города (в скобках) также может отсутствовать.
 * Может присутствовать неограниченное количество пробелов и чёрточек,
 * например, номер 12 --  34- 5 -- 67 -89 тоже следует считать легальным.
 * Перевести номер в формат без скобок, пробелов и чёрточек (но с +), например,
 * "+79211234567" или "123456789" для приведённых примеров.
 * Все символы в номере, кроме цифр, пробелов и +-(), считать недопустимыми.
 * При неверном формате вернуть пустую строку.
 *
 * PS: Дополнительные примеры работы функции можно посмотреть в соответствующих тестах.
 */
fun flattenPhoneNumber(phone: String): String {
    var flattenPhone = phone.replace(" ", "")

    // if incorrect prefix
    if (flattenPhone.startsWith("+") && !flattenPhone[1].isDigit()) return ""

    val openBracket = flattenPhone.indexOf("(")
    val closeBracket = flattenPhone.indexOf(")")

    // if more than 1 open or more than 1 close brackets in phone
    if (openBracket != flattenPhone.lastIndexOf("(") || closeBracket != flattenPhone.lastIndexOf(")"))
        return ""

    // if the pair of brackets is missing, so only '(' or ')' in phone
    if ((openBracket == -1 && closeBracket != -1) || (openBracket != -1 && closeBracket == -1)) return ""

    // if brackets exist but there aren't any digits between them. Or if it's an incorrect brackets order - ")("
    if (openBracket != -1 && closeBracket - openBracket < 2) return ""

    var result = ""
    if (flattenPhone.startsWith("+")) {
        result = "+"
        flattenPhone = flattenPhone.substring(1)
    }
    val goodSet = setOf('-', '(', ')')
    for (char in flattenPhone) {
        if (char in goodSet) continue
        if (!char.isDigit()) return "" // not allowed char, so phone is incorrect
        result += char
    }

    return result
}

/**
 * Средняя (5 баллов)
 *
 * Результаты спортсмена на соревнованиях в прыжках в длину представлены строкой вида
 * "706 - % 717 % 703".
 * В строке могут присутствовать числа, черточки - и знаки процента %, разделённые пробелами;
 * число соответствует удачному прыжку, - пропущенной попытке, % заступу.
 * Прочитать строку и вернуть максимальное присутствующее в ней число (717 в примере).
 * При нарушении формата входной строки или при отсутствии в ней чисел, вернуть -1.
 */
fun bestLongJump(jumps: String): Int {
    var maxResult = -1
    for (elem in jumps.split(Regex("""\s+"""))) {
        if (elem in setOf("-", "%")) continue
        try {
            maxResult = max(maxResult, elem.toInt())
        } catch (e: NumberFormatException) {
            return -1
        }
    }
    return maxResult
}

/**
 * Сложная (6 баллов)
 *
 * Результаты спортсмена на соревнованиях в прыжках в высоту представлены строкой вида
 * "220 + 224 %+ 228 %- 230 + 232 %%- 234 %".
 * Здесь + соответствует удачной попытке, % неудачной, - пропущенной.
 * Высота и соответствующие ей попытки разделяются пробелом.
 * Прочитать строку и вернуть максимальную взятую высоту (230 в примере).
 * При нарушении формата входной строки, а также в случае отсутствия удачных попыток,
 * вернуть -1.
 */
fun bestHighJump(jumps: String): Int {
    val good = setOf('%', '+', '-')
    val jumpResults = jumps.split(Regex("""\s+"""))
    var maxResult = -1

    if (jumpResults.size % 2 != 0) return -1

    for (i in jumpResults.indices step 2) {
        val resultNow: Int
        try {
            resultNow = jumpResults[i].toInt()
        } catch (e: NumberFormatException) {
            return -1
        }
        if (resultNow <= maxResult) continue // skip not the best result
        val attempts = jumpResults[i + 1].toSet()
        if (attempts.any { it !in good }) return -1 // stop if founded an incorrect char
        if ('+' in attempts) maxResult = max(resultNow, maxResult)
    }

    return maxResult
}

/**
 * Сложная (6 баллов)
 *
 * В строке представлено выражение вида "2 + 31 - 40 + 13",
 * использующее целые положительные числа, плюсы и минусы, разделённые пробелами.
 * Наличие двух знаков подряд "13 + + 10" или двух чисел подряд "1 2" не допускается.
 * Вернуть значение выражения (6 для примера).
 * Про нарушении формата входной строки бросить исключение IllegalArgumentException
 */
fun plusMinus(expression: String): Int {
    val invalidExpressionException = IllegalArgumentException("Invalid expression")

    val data = expression.split(Regex("""\s+"""))
    if (data.size % 2 != 1) throw invalidExpressionException

    if (!Regex("""\d+""").matches(data[0])) throw invalidExpressionException
    var result = data[0].toInt()

    for (i in 1 until data.size step 2) {
        val operation = data[i]
        if (!Regex("""\d+""").matches(data[i + 1])) throw invalidExpressionException
        val number = data[i + 1].toInt()
        when (operation) {
            "+" -> result += number
            "-" -> result -= number
            else -> throw invalidExpressionException
        }
    }
    return result
}

/**
 * Сложная (6 баллов)
 *
 * Строка состоит из набора слов, отделённых друг от друга одним пробелом.
 * Определить, имеются ли в строке повторяющиеся слова, идущие друг за другом.
 * Слова, отличающиеся только регистром, считать совпадающими.
 * Вернуть индекс начала первого повторяющегося слова, или -1, если повторов нет.
 * Пример: "Он пошёл в в школу" => результат 9 (индекс первого 'в')
 */
fun firstDuplicateIndex(str: String): Int {
    val data = str.split(" ")
    if (data.size < 2) return -1

    var lastWord = data[0].lowercase()
    var lastIndex = 0
    for (word in data.subList(1, data.size)) {
        val wordLowercase = word.lowercase()
        if (wordLowercase == lastWord) return lastIndex
        lastIndex += lastWord.length + 1
        lastWord = wordLowercase
    }
    return -1
}

/**
 * Сложная (6 баллов)
 *
 * Строка содержит названия товаров и цены на них в формате вида
 * "Хлеб 39.9; Молоко 62; Курица 184.0; Конфеты 89.9".
 * То есть, название товара отделено от цены пробелом,
 * а цена отделена от названия следующего товара точкой с запятой и пробелом.
 * Вернуть название самого дорогого товара в списке (в примере это Курица),
 * или пустую строку при нарушении формата строки.
 * Все цены должны быть больше нуля либо равны нулю.
 */
fun mostExpensive(description: String): String {
    val data = description.split(Regex(""";\s+"""))
    var highestGood = Pair("", -1.0)
    for (goodStr in data) {
        val dataRow = goodStr.split(Regex("""\s+"""))
        if (dataRow.size != 2) return ""
        val name = dataRow[0]
        val price: Double
        try {
            price = dataRow[1].toDouble()
        } catch (e: NumberFormatException) {
            return ""
        }
        if (price < 0) return ""
        if (price > highestGood.second) highestGood = name to price
    }
    return highestGood.first
}

/**
 * Сложная (6 баллов)
 *
 * Перевести число roman, заданное в римской системе счисления,
 * в десятичную систему и вернуть как результат.
 * Римские цифры: 1 = I, 4 = IV, 5 = V, 9 = IX, 10 = X, 40 = XL, 50 = L,
 * 90 = XC, 100 = C, 400 = CD, 500 = D, 900 = CM, 1000 = M.
 * Например: XXIII = 23, XLIV = 44, C = 100
 *
 * Вернуть -1, если roman не является корректным римским числом
 */
fun fromRoman(roman: String): Int {
    val digits = mapOf(
        'I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000,
    )
    if (roman.any { it !in digits.keys }) return -1
    val list = roman.map { digits[it]!! }
    if (list.isEmpty()) return -1
    var result = list[list.size - 1]
    for (i in list.size - 2 downTo 0) {
        if (list[i + 1] > list[i]) result -= list[i]
        else result += list[i]
    }
    if (roman(result) == roman) return result
    return -1
}

/**
 * Очень сложная (7 баллов)
 *
 * Имеется специальное устройство, представляющее собой
 * конвейер из cells ячеек (нумеруются от 0 до cells - 1 слева направо) и датчик, двигающийся над этим конвейером.
 * Строка commands содержит последовательность команд, выполняемых данным устройством, например +>+>+>+>+
 * Каждая команда кодируется одним специальным символом:
 *	> - сдвиг датчика вправо на 1 ячейку;
 *  < - сдвиг датчика влево на 1 ячейку;
 *	+ - увеличение значения в ячейке под датчиком на 1 ед.;
 *	- - уменьшение значения в ячейке под датчиком на 1 ед.;
 *	[ - если значение под датчиком равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей следующей командой ']' (с учётом вложенности);
 *	] - если значение под датчиком не равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей предыдущей командой '[' (с учётом вложенности);
 *      (комбинация [] имитирует цикл)
 *  пробел - пустая команда
 *
 * Изначально все ячейки заполнены значением 0 и датчик стоит на ячейке с номером N/2 (округлять вниз)
 *
 * После выполнения limit команд или всех команд из commands следует прекратить выполнение последовательности команд.
 * Учитываются все команды, в том числе несостоявшиеся переходы ("[" при значении под датчиком не равном 0 и "]" при
 * значении под датчиком равном 0) и пробелы.
 *
 * Вернуть список размера cells, содержащий элементы ячеек устройства после завершения выполнения последовательности.
 * Например, для 10 ячеек и командной строки +>+>+>+>+ результат должен быть 0,0,0,0,0,1,1,1,1,1
 *
 * Все прочие символы следует считать ошибочными и формировать исключение IllegalArgumentException.
 * То же исключение формируется, если у символов [ ] не оказывается пары.
 * Выход за границу конвейера также следует считать ошибкой и формировать исключение IllegalStateException.
 * Считать, что ошибочные символы и непарные скобки являются более приоритетной ошибкой чем выход за границу ленты,
 * то есть если в программе присутствует некорректный символ или непарная скобка, то должно быть выброшено
 * IllegalArgumentException.
 * IllegalArgumentException должен бросаться даже если ошибочная команда не была достигнута в ходе выполнения.
 *
 */
// Я знаю, что не работает. Я не могу найти ошибку
fun computeDeviceCells(cells: Int, commands: String, limit: Int): List<Int> {
    val goodChars = "><+-[] ".toSet()

    val incorrectCharsException = IllegalArgumentException("Incorrect chars")
    val incorrectBracketsException = IllegalArgumentException("Incorrect brackets sequence")
    val passEdgeException = IllegalStateException("Edge passing denied")

    // Check that all chars is correct
    if (commands.any { it !in goodChars }) throw incorrectCharsException

    // Check that brackets sequence is correct
    var openedBracketCounter = 0
    for (c in commands) {
        if (c in "[]") {
            if (c == '[') openedBracketCounter++
            else if (--openedBracketCounter < 0) throw incorrectBracketsException
        }
    }
    if (openedBracketCounter != 0) throw incorrectBracketsException

    val cellsList = MutableList(cells) { 0 }
    var index = cells / 2
    var limitLast = limit

    fun findNextClosingBracketIndex(startIndex: Int): Int {
        // "[[][]]" -> 5
        var counter = 0
        for (i in startIndex until commands.length) {
            if (commands[i] in "[]") {
                if (commands[i] == '[') counter++
                else if (--counter == 0) return i
            }
        }
        return -1 // never reached for correct brackets sequence
    }

    fun executeCommandLine(commandsLine: String) {
        var commandIndex = 0
        while (commandIndex < commandsLine.length) {
            val c = commandsLine[commandIndex]
            if (--limitLast < 0) break
            when (c) {
                ' ' -> Unit // do nothing
                '>' -> if (++index >= cells) throw passEdgeException
                '<' -> if (--index < 0) throw passEdgeException
                '+' -> cellsList[index]++
                '-' -> cellsList[index]--
                '[' -> {
                    if (cellsList[index] == 0) commandIndex = findNextClosingBracketIndex(commandIndex)
                    else {
                        val start = commandIndex + 1
                        val end = findNextClosingBracketIndex(commandIndex)
                        do {
                            executeCommandLine(
                                commandsLine.substring(start, end)
                            )
                        } while (cellsList[index] != 0)
                        commandIndex--
                    }
                }
            }
            commandIndex++
        }
    }

    executeCommandLine(commands)
    return cellsList
}
