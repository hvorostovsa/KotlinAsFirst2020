@file:Suppress("UNUSED_PARAMETER", "ConvertCallChainIntoSequence")

package lesson7.task1

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
    val result = File(outputName).bufferedWriter()
    for (line in File(inputName).readLines()) {
        if (line.isEmpty()) result.newLine()
        else if (line.first() != '_') {
            result.write(line)
            result.newLine()
        }
    }
    result.close()
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
fun countInLine(line: String, sub: String, k: Int): Int {
    return if (!line.contains(sub)) k
    else {
        val new = k + 1
        countInLine(line.substring(line.indexOf(sub) + 1), sub, new)
    }
}

fun countSubstrings(inputName: String, substrings: List<String>): Map<String, Int> {
    val map = mutableMapOf<String, Int>()
    var count = 0
    for (elem in substrings) {
        for (line in File(inputName).readLines()) {
            val lowerLine = line.toLowerCase()
            val lowerElem = elem.toLowerCase()
            count += countInLine(lowerLine, lowerElem, 0)
        }
        map[elem] = count
        count = 0
    }
    return map
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
    val list = listOf('ж', 'Ж', 'ш', 'Ш', 'ч', 'Ч', 'щ', 'Щ')
    val map = mapOf('ы' to 'и', 'Ы' to 'И', 'я' to 'а', 'Я' to 'А', 'ю' to 'у', 'Ю' to 'У')
    val result = File(outputName).bufferedWriter()
    for (line in File(inputName).readLines()) {
        result.write(line[0].toString())
        for (i in 1 until line.length) {
            if (line[i - 1] in list && line[i] in map.keys)
                result.write(map[line[i]].toString())
            else result.write(line[i].toString())
        }
        result.newLine()
    }
    result.close()
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
    val newFile = File(outputName).bufferedWriter()
    val lengths = mutableListOf<Int>()
    for (line in File(inputName).readLines())
        lengths += line.trim().length
    val max = lengths.maxOrNull() ?: return newFile.close()
    for ((i, line) in File(inputName).readLines().withIndex()) {
        newFile.write(" ".repeat((max - lengths[i]) / 2))
        newFile.write(line.trim())
        newFile.newLine()
    }
    newFile.close()
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
    val newFile = File(outputName).bufferedWriter()
    val lengths = mutableListOf<Int>()
    val wordsCount = mutableListOf<Int>()
    for ((i, line) in File(inputName).readLines().withIndex()) {
        wordsCount += 0
        lengths += 0
        if (!line.matches(Regex("""\s*"""))) {
            lengths[i] = 0
            for (word in line.split(Regex(" +"))) {
                if (word.isNotEmpty()) {
                    lengths[i] += word.length + 1
                    wordsCount[i]++
                }
            }
            lengths[i]--
        }
    }

    val max = lengths.maxOrNull() ?: return newFile.close()
    for ((i, line) in File(inputName).readLines().withIndex()) {
        if (!line.matches(Regex("""\s*"""))) {
            var modCount = 0
            var spaceCount = 0
            if (wordsCount[i] != 1) {
                modCount = (max - lengths[i]) % (wordsCount[i] - 1)
                spaceCount = (max - lengths[i]) / (wordsCount[i] - 1)
            }
            val newString = StringBuilder()
            for (word in line.split(Regex(" +"))) {
                if (word.isNotEmpty()) {
                    newString.append(word + " ".repeat(if (modCount > 0) spaceCount + 2 else spaceCount + 1))
                    modCount--
                }
            }
            newFile.write(newString.trim().toString())
            newFile.newLine()
        } else newFile.newLine()
    }
    newFile.close()
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
fun top20Words(inputName: String): Map<String, Int> = TODO()

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
    TODO()
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
    TODO()
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
<body>+-
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
    val htmlFile = File(outputName).bufferedWriter()
    var stars = 0
    var dashes = 0
    var paragraphs = 1
    var prevLine = ""
    htmlFile.write("<html>\n<body>\n<p>\n")
    for (line in File(inputName).readLines()) {
        if (!line.matches(Regex("\\s*"))) {
            if (paragraphs == 0) {
                htmlFile.write("<p>\n")
                paragraphs++
            }
            val list = line.split("") + ""
            for (i in 1 until list.size - 1) {
                val symbol = list[i]
                val last = list[i - 1]
                val next = list[i + 1]
                if (symbol != "*" && symbol != "~") htmlFile.write(symbol)
                else {
                    when {
                        // для зачеркнутого
                        symbol == "~" && next == "~" && dashes == 0
                        -> {
                            htmlFile.write("<s>")
                            dashes++
                        }
                        symbol == "~" && next == "~" && dashes == 1
                        -> {
                            htmlFile.write("</s>")
                            dashes--
                        }

                        // для курсива
                        symbol == "*" && next != "*" && last != "*" && (stars == 0 || stars == 2)
                        -> {
                            htmlFile.write("<i>")
                            stars++
                        }
                        symbol == "*" && next != "*" && last != "*" && (stars == 1 || stars == 3)
                        -> {
                            htmlFile.write("</i>")
                            stars--
                        }

                        // для полужирного
                        symbol == "*" && next == "*" && last != "*" && list[i + 2] != "*" && (stars == 0 || stars == 1)
                        -> {
                            htmlFile.write("<b>")
                            stars += 2
                        }
                        symbol == "*" && next == "*" && last != "*" && list[i + 2] != "*" && (stars == 2 || stars == 3)
                        -> {
                            htmlFile.write("</b>")
                            stars -= 2
                        }

                        //для полужирного и курсива
                        symbol == "*" && next == "*" && last == "*" && stars == 0
                        -> {
                            htmlFile.write("<b><i>")
                            stars += 3
                        }
                        symbol == "*" && next == "*" && last == "*" && stars == 3
                        -> {
                            htmlFile.write("</b></i>")
                            stars -= 3
                        }
                    }
                }
            }
        } else if (line.matches(Regex("\\s*")) && !prevLine.matches(Regex("\\s*"))) {
            htmlFile.write("</p>\n")
            paragraphs--
        }
        prevLine = line
    }
    if (paragraphs == 1) htmlFile.write("</p>")
    htmlFile.write("</body>\n</html>")
    htmlFile.close()
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
    TODO()
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
    TODO()
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
// оно не работает, можно не проверять, пытаюсь что-то придумать
fun printDivisionProcess(lhv: Int, rhv: Int, outputName: String) {
    val division = File(outputName).bufferedWriter()
    val divResult = (lhv / rhv).toString()
    var a = lhv
    var b = 0 // цифра, которую сносят для следующего вычитания
    var subtrahend = rhv * (divResult[0].code - 48) // число, которое вычитают
    val length = subtrahend.toString().length
    while (a - subtrahend > rhv) {
        b = a % 10
        a /= 10
    }
    var remainder = a - subtrahend // остаток от вычитания
    var newNumber = if (divResult.length == 1) "$remainder"
    else "$remainder$b"
    var n = (length + 1) - a.toString().length // разница между длинами вычитаемого числа и числа из которого вычитают
    division.write(" ".repeat(n) + "$lhv | $rhv\n")
    var spaceCount = length
    division.write(
        "-$subtrahend" + " ".repeat((lhv.toString().length + n) - (length + 1) + 3) +
                divResult + "\n" + "-".repeat(length + 1) + "\n" + " ".repeat(spaceCount - 1 + n) +
                newNumber + "\n"
    )


    for (i in 1 until divResult.length) {
        a = lhv
        b = a % 10
        subtrahend = rhv * (divResult[i].code - 48)
        val lengthInCycle = subtrahend.toString().length
        while (newNumber.toInt() - subtrahend >= rhv) {
            b = a % 10
            a /= 10
        }

        n = (lengthInCycle + 1) - newNumber.length

        remainder = newNumber.toInt() - subtrahend

        val m =
            newNumber.length - remainder.toString().length // разница между длинами числа из которого вычитают и остатка от вычитания

        newNumber = if (i + 1 == divResult.length) "$remainder"
        else "$remainder$b"
        division.write(
            " ".repeat(spaceCount - n) + "-$subtrahend" + "\n" +
                    " ".repeat(spaceCount - n) + "-".repeat(lengthInCycle + 1) + "\n" +
                    " ".repeat(spaceCount + m) + newNumber + "\n"
        )
        spaceCount += m
    }
    division.close()
}

