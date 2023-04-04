package com.onix77.sklad

import java.io.File

class FileShare <T> (val list: List<T>, private val dirApp: File) {

    fun createFileRest(): File {
        list as List<ElementDB>
        val dir = File(dirApp, "SHARE_FILE")
        if (!dir.exists()) dir.mkdir()
        val date = MyDate().getDateTimeForName()
        val file = File(dir, "rest$date.txt")
        file.writeText("\t**************Остатки на $date**************\n")
        file.appendText("""
            ----------------------------------------------------------------
            | Название                                     |    Остатки    |
            ================================================================
        """.trimIndent())
        file.appendText("\n")
        val groupMap = list.groupBy { it.nameCat}
        for ((k, v) in groupMap) {
            var str = "|     $k"
            str += " ".repeat(57 - k.length)
            str += "|\n"
            file.appendText(str)
            file.appendText("----------------------------------------------------------------\n")
            for (el in v) {
                str = "| ${el.nameEl}"
                str += " ".repeat(45 - el.nameEl.length)
                str += "|"
                str += " ".repeat(14 - el.number.toString().length)
                str += "${el.number}"
                str += " |\n"
                file.appendText(str)
                file.appendText("----------------------------------------------------------------\n")
            }
        }
        return file
    }

    fun createFileStat(dateFrom: String, dateTo: String): File {
        val dir = File(dirApp, "SHARE_FILE")
        if (!dir.exists()) dir.mkdir()
        val date = MyDate().getDateTimeForName()
        val file = File(dir, "statistic$date.txt")
        list as List<ItemStatistic>
        file.writeText("\t**************Статистика с $dateFrom по $dateTo**************\n")
        file.appendText("""
            --------------------------------------------------------------------------------
            | Название                                     | Приход (+)    | Расход (-)    |
            ================================================================================
        """.trimIndent())
        file.appendText("\n")
        val groupMap = list.groupBy { it.cat }
        for ((k, v) in groupMap) {
            var str = "|     $k"
            str += " ".repeat(73 - k.length)
            str += "|\n"
            file.appendText(str)
            file.appendText("--------------------------------------------------------------------------------\n")
            for (el in v) {
                str = "| ${el.el}"
                str += " ".repeat(45 - el.el.length)
                str += "|"
                str += " ".repeat(13 - el.sumPlus.toString().length)
                str += "+${el.sumPlus}"
                str += " |"
                str += " ".repeat(14 - el.sumMinus.toString().length)
                str += "${el.sumMinus}"
                str += " |\n"
                file.appendText(str)
                file.appendText("--------------------------------------------------------------------------------\n")
            }
        }
        return file
    }

}
