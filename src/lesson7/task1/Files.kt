@file:Suppress("UNUSED_PARAMETER", "ConvertCallChainIntoSequence")

package lesson7.task1

import ru.spbstu.wheels.toMutableMap
import java.io.File

// Урок 7: работа с файлами
// Урок интегральный, поэтому его задачи имеют сильно увеличенную стоимость
// Максимальное количество баллов = 55
// Рекомендуемое количество баллов = 20
// Вместе с предыдущими уроками (пять лучших, 3-7) = 55/103

/**
 * Пример
 *
 * Во входном файле с именем inputName содержится некоторый текст.
 * Вывести его в выходной файл с именем outputName, выровняв по левому краю,
 * чтобы длина каждой строки не превосходила lineLength.
 * Слова в слишком длинных строках следует переносить на следующую строку.
 * Слишком короткие строки следует дополнять словами из следующей строки.
 * Пустые строки во входном файле обозначают конец абзаца,
 * их следует сохранить и в выходном файле
 */
fun alignFile(inputName: String, lineLength: Int, outputName: String) {
    val writer = File(outputName).bufferedWriter()
    var currentLineLength = 0
    fun append(word: String) {
        if (currentLineLength > 0) {
            if (word.length + currentLineLength >= lineLength) {
                writer.newLine()
                currentLineLength = 0
            } else {
                writer.write(" ")
                currentLineLength++
            }
        }
        writer.write(word)
        currentLineLength += word.length
    }
    for (line in File(inputName).readLines()) {
        if (line.isEmpty()) {
            writer.newLine()
            if (currentLineLength > 0) {
                writer.newLine()
                currentLineLength = 0
            }
            continue
        }
        for (word in line.split(Regex("\\s+"))) {
            append(word)
        }
    }
    writer.close()
}

/**
 * Простая (8 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст.
 * Некоторые его строки помечены на удаление первым символом _ (подчёркивание).
 * Перенести в выходной файл с именем outputName все строки входного файла, убрав при этом помеченные на удаление.
 * Все остальные строки должны быть перенесены без изменений, включая пустые строки.
 * Подчёркивание в середине и/или в конце строк значения не имеет.
 */
fun deleteMarked(inputName: String, outputName: String) {
    File(outputName).bufferedWriter().use { writer ->
        File(inputName).forEachLine { line ->
            if (!line.startsWith("_")) writer.write("$line\n")
        }
    }
}

/**
 * Средняя (14 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст.
 * На вход подаётся список строк substrings.
 * Вернуть ассоциативный массив с числом вхождений каждой из строк в текст.
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 *
 */
fun countSubstrings(inputName: String, substrings: List<String>): Map<String, Int> {
    val substringsU = substrings.toSet().toList()
    val map = substringsU.map { it to 0 }.toMutableMap()
    val substringsLowercase = substringsU.map { it.lowercase() }
    File(inputName).forEachLine { line ->
        val lineLowercase = line.lowercase()
        for ((i, substring) in substringsLowercase.withIndex()) {
            map[substringsU[i]] = map[substringsU[i]]!! + substringsCount(lineLowercase, substring)
        }
    }
    return map
}

fun substringsCount(string: String, substring: String): Int {
    // С пересечениями
    var result = 0
    for (i in 0..string.length - substring.length) {
        if (string.substring(i, i + substring.length) == substring) result++
    }
    return result
}


/**
 * Средняя (12 баллов)
 *
 * В русском языке, как правило, после букв Ж, Ч, Ш, Щ пишется И, А, У, а не Ы, Я, Ю.
 * Во входном файле с именем inputName содержится некоторый текст на русском языке.
 * Проверить текст во входном файле на соблюдение данного правила и вывести в выходной
 * файл outputName текст с исправленными ошибками.
 *
 * Регистр заменённых букв следует сохранять.
 *
 * Исключения (жюри, брошюра, парашют) в рамках данного задания обрабатывать не нужно
 *
 */
fun sibilants(inputName: String, outputName: String) {
    val goodLetters = setOf('ж', 'ч', 'ш', 'щ')
    val toFix = mapOf('ы' to 'и', 'я' to 'а', 'ю' to 'у',)

    File(outputName).bufferedWriter().use { writer ->
        File(inputName).forEachLine { line ->
            val resultLine = StringBuilder(line[0].toString())
            for (i in 1 until line.length) {
                val c = line[i]
                val cLower = c.lowercaseChar()
                if (line[i - 1].lowercaseChar() in goodLetters && cLower in toFix) {
                    resultLine.append(if (c.isLowerCase()) toFix[cLower]!! else toFix[cLower]!!.uppercaseChar())
                } else {
                    resultLine.append(c)
                }
            }
            writer.write("$resultLine\n")
        }
    }
}

/**
 * Средняя (15 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 * Вывести его в выходной файл с именем outputName, выровняв по центру
 * относительно самой длинной строки.
 *
 * Выравнивание следует производить путём добавления пробелов в начало строки.
 *
 *
 * Следующие правила должны быть выполнены:
 * 1) Пробелы в начале и в конце всех строк не следует сохранять.
 * 2) В случае невозможности выравнивания строго по центру, строка должна быть сдвинута в ЛЕВУЮ сторону
 * 3) Пустые строки не являются особым случаем, их тоже следует выравнивать
 * 4) Число строк в выходном файле должно быть равно числу строк во входном (в т. ч. пустых)
 *
 */
fun centerFile(inputName: String, outputName: String) {
    val lines = File(inputName).readLines().map { it.trim() }
    val length = if (lines.isNotEmpty()) lines.maxOf { it.length } else 0
    File(outputName).bufferedWriter().use { writer ->
        lines.forEach { line ->
            val leftIndent = (length - line.length) / 2
            writer.write(" ".repeat(leftIndent) + line + "\n")
        }
    }
}

/**
 * Сложная (20 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 * Вывести его в выходной файл с именем outputName, выровняв по левому и правому краю относительно
 * самой длинной строки.
 * Выравнивание производить, вставляя дополнительные пробелы между словами: равномерно по всей строке
 *
 * Слова внутри строки отделяются друг от друга одним или более пробелом.
 *
 * Следующие правила должны быть выполнены:
 * 1) Каждая строка входного и выходного файла не должна начинаться или заканчиваться пробелом.
 * 2) Пустые строки или строки из пробелов трансформируются в пустые строки без пробелов.
 * 3) Строки из одного слова выводятся без пробелов.
 * 4) Число строк в выходном файле должно быть равно числу строк во входном (в т. ч. пустых).
 *
 * Равномерность определяется следующими формальными правилами:
 * 5) Число пробелов между каждыми двумя парами соседних слов не должно отличаться более, чем на 1.
 * 6) Число пробелов между более левой парой соседних слов должно быть больше или равно числу пробелов
 *    между более правой парой соседних слов.
 *
 * Следует учесть, что входной файл может содержать последовательности из нескольких пробелов  между слвоами. Такие
 * последовательности следует учитывать при выравнивании и при необходимости избавляться от лишних пробелов.
 * Из этого следуют следующие правила:
 * 7) В самой длинной строке каждая пара соседних слов должна быть отделена В ТОЧНОСТИ одним пробелом
 * 8) Если входной файл удовлетворяет требованиям 1-7, то он должен быть в точности идентичен выходному файлу
 */
fun alignFileByWidth(inputName: String, outputName: String) {
    val lines = File(inputName).readLines().map { it.trim().split(Regex("\\s+")) }
    val length = if (lines.isNotEmpty()) lines.maxOf { it.joinToString(" ").length } else 0
    File(outputName).bufferedWriter().use { writer ->
        lines.forEach { line ->
            if (line.isEmpty()) writer.newLine()
            else if (line.size == 1) writer.write("${line[0]}\n")
            else {
                val lengthNow = line.sumOf { it.length }
                val spaceNumber = (length - lengthNow) / (line.size - 1)
                val remainder = (length - lengthNow) % (line.size - 1)
                val resultLine = StringBuilder()
                for (i in 0 until remainder) {
                    resultLine.append(line[i], " ".repeat(spaceNumber + 1))
                }
                for (i in remainder until line.size - 1) {
                    resultLine.append(line[i], " ".repeat(spaceNumber))
                }
                resultLine.append(line[line.size - 1])
                writer.write("$resultLine\n")
            }
        }
    }
}

/**
 * Средняя (14 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 *
 * Вернуть ассоциативный массив, содержащий 20 наиболее часто встречающихся слов с их количеством.
 * Если в тексте менее 20 различных слов, вернуть все слова.
 * Вернуть ассоциативный массив с числом слов больше 20, если 20-е, 21-е, ..., последнее слова
 * имеют одинаковое количество вхождений (см. также тест файла input/onegin.txt).
 *
 * Словом считается непрерывная последовательность из букв (кириллических,
 * либо латинских, без знаков препинания и цифр).
 * Цифры, пробелы, знаки препинания считаются разделителями слов:
 * Привет, привет42, привет!!! -привет?!
 * ^ В этой строчке слово привет встречается 4 раза.
 *
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 * Ключи в ассоциативном массиве должны быть в нижнем регистре.
 *
 */
fun top20Words(inputName: String): Map<String, Int> {
    val map = mutableMapOf<String, Int>()

    File(inputName).forEachLine { line ->
        val newLine = line.trim().lowercase().replace(Regex("""[^\sa-zа-яё]"""), " ")
        for (word in newLine.split(Regex("\\s+"))) {
            if (word.isNotBlank()) map[word] = map.getOrDefault(word, 0) + 1
        }
    }

    if (map.size <= 20) return map
    val lowestTop20 = map.values.toList().sortedByDescending { it }[19]
    return map.filter { it.value >= lowestTop20 }
}

/**
 * Средняя (14 баллов)
 *
 * Реализовать транслитерацию текста из входного файла в выходной файл посредством динамически задаваемых правил.

 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 *
 * В ассоциативном массиве dictionary содержится словарь, в котором некоторым символам
 * ставится в соответствие строчка из символов, например
 * mapOf('з' to "zz", 'р' to "r", 'д' to "d", 'й' to "y", 'М' to "m", 'и' to "yy", '!' to "!!!")
 *
 * Необходимо вывести в итоговый файл с именем outputName
 * содержимое текста с заменой всех символов из словаря на соответствующие им строки.
 *
 * При этом регистр символов в словаре должен игнорироваться,
 * но при выводе символ в верхнем регистре отображается в строку, начинающуюся с символа в верхнем регистре.
 *
 * Пример.
 * Входной текст: Здравствуй, мир!
 *
 * заменяется на
 *
 * Выходной текст: Zzdrавствуy, mир!!!
 *
 * Пример 2.
 *
 * Входной текст: Здравствуй, мир!
 * Словарь: mapOf('з' to "zZ", 'р' to "r", 'д' to "d", 'й' to "y", 'М' to "m", 'и' to "YY", '!' to "!!!")
 *
 * заменяется на
 *
 * Выходной текст: Zzdrавствуy, mир!!!
 *
 * Обратите внимание: данная функция не имеет возвращаемого значения
 */
fun transliterate(inputName: String, dictionary: Map<Char, String>, outputName: String) {
    val newDictionary = dictionary.map { it.key.lowercaseChar() to it.value.lowercase() }.toMap()
    File(outputName).bufferedWriter().use { writer ->
        File(inputName).forEachLine { line ->
            val resultLine = StringBuilder()
            line.forEach { char ->
                val lowerChar = char.lowercaseChar()
                if (lowerChar in newDictionary) {
                    if (char.isUpperCase()) {
                        resultLine.append(newDictionary[lowerChar]!!.replaceFirstChar { it.uppercaseChar() })
                    } else resultLine.append(newDictionary[lowerChar])
                } else resultLine.append(char)
            }
            writer.write("$resultLine\n")
        }
    }
}

/**
 * Средняя (12 баллов)
 *
 * Во входном файле с именем inputName имеется словарь с одним словом в каждой строчке.
 * Выбрать из данного словаря наиболее длинное слово,
 * в котором все буквы разные, например: Неряшливость, Четырёхдюймовка.
 * Вывести его в выходной файл с именем outputName.
 * Если во входном файле имеется несколько слов с одинаковой длиной, в которых все буквы разные,
 * в выходной файл следует вывести их все через запятую.
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 *
 * Пример входного файла:
 * Карминовый
 * Боязливый
 * Некрасивый
 * Остроумный
 * БелогЛазый
 * ФиолетОвый

 * Соответствующий выходной файл:
 * Карминовый, Некрасивый
 *
 * Обратите внимание: данная функция не имеет возвращаемого значения
 */
fun chooseLongestChaoticWord(inputName: String, outputName: String) {
    var length = 0
    val list = mutableListOf<String>()

    File(inputName).forEachLine { line ->
        val word = line.trim()
        if (word.length >= length && word.lowercase().toSet().size == word.length) {
            if (word.length > length) {
                length = word.length
                list.clear()
            }
            list.add(word)
        }
    }

    File(outputName).bufferedWriter().use { writer ->
        writer.write(list.joinToString(", "))
    }
}

/**
 * Сложная (22 балла)
 *
 * Реализовать транслитерацию текста в заданном формате разметки в формат разметки HTML.
 *
 * Во входном файле с именем inputName содержится текст, содержащий в себе элементы текстовой разметки следующих типов:
 * - *текст в курсивном начертании* -- курсив
 * - **текст в полужирном начертании** -- полужирный
 * - ~~зачёркнутый текст~~ -- зачёркивание
 *
 * Следует вывести в выходной файл этот же текст в формате HTML:
 * - <i>текст в курсивном начертании</i>
 * - <b>текст в полужирном начертании</b>
 * - <s>зачёркнутый текст</s>
 *
 * Кроме того, все абзацы исходного текста, отделённые друг от друга пустыми строками, следует обернуть в теги <p>...</p>,
 * а весь текст целиком в теги <html><body>...</body></html>.
 *
 * Все остальные части исходного текста должны остаться неизменными с точностью до наборов пробелов и переносов строк.
 * Отдельно следует заметить, что открывающая последовательность из трёх звёздочек (***) должна трактоваться как "<b><i>"
 * и никак иначе.
 *
 * При решении этой и двух следующих задач полезно прочитать статью Википедии "Стек".
 *
 * Пример входного файла:
Lorem ipsum *dolor sit amet*, consectetur **adipiscing** elit.
Vestibulum lobortis, ~~Est vehicula rutrum *suscipit*~~, ipsum ~~lib~~ero *placerat **tortor***,

Suspendisse ~~et elit in enim tempus iaculis~~.
 *
 * Соответствующий выходной файл:
<html>
    <body>
        <p>
            Lorem ipsum <i>dolor sit amet</i>, consectetur <b>adipiscing</b> elit.
            Vestibulum lobortis. <s>Est vehicula rutrum <i>suscipit</i></s>, ipsum <s>lib</s>ero <i>placerat <b>tortor</b></i>.
        </p>
        <p>
            Suspendisse <s>et elit in enim tempus iaculis</s>.
        </p>
    </body>
</html>
 *
 * (Отступы и переносы строк в примере добавлены для наглядности, при решении задачи их реализовывать не обязательно)
 */
fun markdownToHtmlSimple(inputName: String, outputName: String) {
    var stack = 0
    var isStrikethrough = false
    File(outputName).bufferedWriter().use { writer ->
        writer.write("<html><body>")
        File(inputName).readText().trim().split(Regex("\r?\n\r?\n")).forEach { paragraph ->
            val triple = markdownParagraph(paragraph, stack, isStrikethrough)
            stack = triple.second
            isStrikethrough = triple.third
            writer.write("<p>${triple.first}</p>")
        }
        writer.write("</body></html>")
    }
}

fun markdownParagraph(paragraph: String, startStack: Int = 0, isStrikethrough: Boolean = false):
        Triple<String, Int, Boolean> {
    val result = StringBuilder()
    var stack = startStack
    var strikethroughOpened = isStrikethrough
    var cursiveLast = false
    var i = 0

    while (i < paragraph.length) {
        val c = paragraph[i]

        if (c == '*') { // stars checking
            if (paragraph.getOrNull(i + 1) == '*') {
                if (paragraph.getOrNull(i + 2) == '*') { // 3 stars
                    if (stack == 3) {
                        result.append(if (cursiveLast) "</i></b>" else "</b></i>")
                        stack = 0
                        cursiveLast = false
                    } else {
                        result.append("<b><i>")
                        stack = 3
                        cursiveLast = true
                    }
                    i += 2
                } else { // 2 stars
                    if (stack == 2 || stack == 3) {
                        result.append("</b>")
                        stack -= 2
                        cursiveLast = true
                    } else {
                        result.append("<b>")
                        stack += 2
                        cursiveLast = false
                    }
                    i += 1
                }
            } else { // 1 star
                if (stack == 1 || stack == 3) {
                    result.append("</i>")
                    stack -= 1
                    cursiveLast = false
                } else {
                    result.append("<i>")
                    stack += 1
                    cursiveLast = true
                }
            }
        } else if (c == '~' && paragraph.getOrNull(i + 1) == '~') { // strikethrough checking
            if (strikethroughOpened) result.append("</s>")
            else result.append("<s>")
            strikethroughOpened = !strikethroughOpened
            i += 1
        } else { // common char
            result.append(c)
        }
        i++ // next iteration index
    }
    return Triple(result.toString(), stack, strikethroughOpened)
}

/**
 * Сложная (23 балла)
 *
 * Реализовать транслитерацию текста в заданном формате разметки в формат разметки HTML.
 *
 * Во входном файле с именем inputName содержится текст, содержащий в себе набор вложенных друг в друга списков.
 * Списки бывают двух типов: нумерованные и ненумерованные.
 *
 * Каждый элемент ненумерованного списка начинается с новой строки и символа '*', каждый элемент нумерованного списка --
 * с новой строки, числа и точки. Каждый элемент вложенного списка начинается с отступа из пробелов, на 4 пробела большего,
 * чем список-родитель. Максимально глубина вложенности списков может достигать 6. "Верхние" списки файла начинются
 * прямо с начала строки.
 *
 * Следует вывести этот же текст в выходной файл в формате HTML:
 * Нумерованный список:
 * <ol>
 *     <li>Раз</li>
 *     <li>Два</li>
 *     <li>Три</li>
 * </ol>
 *
 * Ненумерованный список:
 * <ul>
 *     <li>Раз</li>
 *     <li>Два</li>
 *     <li>Три</li>
 * </ul>
 *
 * Кроме того, весь текст целиком следует обернуть в теги <html><body><p>...</p></body></html>
 *
 * Все остальные части исходного текста должны остаться неизменными с точностью до наборов пробелов и переносов строк.
 *
 * Пример входного файла:
///////////////////////////////начало файла/////////////////////////////////////////////////////////////////////////////
* Утка по-пекински
    * Утка
    * Соус
* Салат Оливье
    1. Мясо
        * Или колбаса
    2. Майонез
    3. Картофель
    4. Что-то там ещё
* Помидоры
* Фрукты
    1. Бананы
    23. Яблоки
        1. Красные
        2. Зелёные
///////////////////////////////конец файла//////////////////////////////////////////////////////////////////////////////
 *
 *
 * Соответствующий выходной файл:
///////////////////////////////начало файла/////////////////////////////////////////////////////////////////////////////
<html>
  <body>
    <p>
      <ul>
        <li>
          Утка по-пекински
          <ul>
            <li>Утка</li>
            <li>Соус</li>
          </ul>
        </li>
        <li>
          Салат Оливье
          <ol>
            <li>Мясо
              <ul>
                <li>Или колбаса</li>
              </ul>
            </li>
            <li>Майонез</li>
            <li>Картофель</li>
            <li>Что-то там ещё</li>
          </ol>
        </li>
        <li>Помидоры</li>
        <li>Фрукты
          <ol>
            <li>Бананы</li>
            <li>Яблоки
              <ol>
                <li>Красные</li>
                <li>Зелёные</li>
              </ol>
            </li>
          </ol>
        </li>
      </ul>
    </p>
  </body>
</html>
///////////////////////////////конец файла//////////////////////////////////////////////////////////////////////////////
 * (Отступы и переносы строк в примере добавлены для наглядности, при решении задачи их реализовывать не обязательно)
 */
fun markdownToHtmlLists(inputName: String, outputName: String) {
    File(outputName).bufferedWriter().use { writer ->
        writer.write("<html><body>")
        File(inputName).readText().trim().split(Regex("\r?\n\r?\n")).forEach { paragraph ->
            writer.write("<p>${listParagraph(paragraph)}</p>")
        }
        writer.write("</body></html>")
    }
}

fun listParagraph(paragraph: String): String {
    val data = paragraph.trimIndent()
    val isNumbered = data[0] != '*'

    val listTag = if (isNumbered) "ol" else "ul"
    val listCheckRegex = if (isNumbered) Regex("^\\d+\\. ") else Regex("^\\* ")

    val result = StringBuilder()
    result.append("<$listTag>")

    val rows = data.split(Regex("\r?\n"))
    var i = 0
    var needCloseTag = false
    while (i < rows.size) {
        val row = rows[i]
        if (row.startsWith(" ".repeat(4))) {
            val indents = row.indexOfFirst { it != ' ' }
            val newParagraphList = mutableListOf(row)
            var j = i + 1
            while (j < rows.size) {
                if (rows[j].indexOfFirst { it != ' ' } >= indents) {
                    newParagraphList.add(rows[j])
                } else break
                j++
            }
            i = j - 1
            result.append(listParagraph(newParagraphList.joinToString("\n")))
        } else {
            val match = listCheckRegex.find(row)
            if (match != null) {
                if (needCloseTag) result.append("</li>")
                result.append("<li>${row.substring(match.range.last + 1)}")
                needCloseTag = true
            } else {
                result.append(row)
            }
        }
        i++
    }
    if (needCloseTag) result.append("</li>")
    result.append("</$listTag>")

    return result.toString()
}

/**
 * Очень сложная (30 баллов)
 *
 * Реализовать преобразования из двух предыдущих задач одновременно над одним и тем же файлом.
 * Следует помнить, что:
 * - Списки, отделённые друг от друга пустой строкой, являются разными и должны оказаться в разных параграфах выходного файла.
 *
 */
fun markdownToHtml(inputName: String, outputName: String) {
    var stack = 0
    var isStrikethrough = false
    File(outputName).bufferedWriter().use { writer ->
        writer.write("<html><body>")
        File(inputName).readText().trim().split(Regex("\r?\n\r?\n")).forEach { paragraph ->
            val triple = if (paragraph.contains(Regex("^ *\\* |^ *\\d+. "))) {
                markdownParagraph(listParagraph(paragraph), stack, isStrikethrough)
            } else {
                markdownParagraph(paragraph, stack, isStrikethrough)
            }
            stack = triple.second
            isStrikethrough = triple.third
            writer.write("<p>${triple.first}</p>")
        }
        writer.write("</body></html>")
    }
}

/**
 * Средняя (12 баллов)
 *
 * Вывести в выходной файл процесс умножения столбиком числа lhv (> 0) на число rhv (> 0).
 *
 * Пример (для lhv == 19935, rhv == 111):
   19935
*    111
--------
   19935
+ 19935
+19935
--------
 2212785
 * Используемые пробелы, отступы и дефисы должны в точности соответствовать примеру.
 * Нули в множителе обрабатывать так же, как и остальные цифры:
  235
*  10
-----
    0
+235
-----
 2350
 *
 */
fun printMultiplicationProcess(lhv: Int, rhv: Int, outputName: String) {
    TODO()
}


/**
 * Сложная (25 баллов)
 *
 * Вывести в выходной файл процесс деления столбиком числа lhv (> 0) на число rhv (> 0).
 *
 * Пример (для lhv == 19935, rhv == 22):
  19935 | 22
 -198     906
 ----
    13
    -0
    --
    135
   -132
   ----
      3

 * Используемые пробелы, отступы и дефисы должны в точности соответствовать примеру.
 *
 */
fun printDivisionProcess(lhv: Int, rhv: Int, outputName: String) {
    TODO()
}

