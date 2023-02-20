package com.onix77.sklad




class Repository(private val dao: Dao) {

    fun getEl(cat: String) = dao.getEl(cat)

    suspend fun updateEl(el: ElementDB) {
        dao.updateEl(el)
    }

    suspend fun delete(elementDB: ElementDB) {
        dao.delete(elementDB)
    }

    suspend fun insertEl(item: ElementDB) {
        dao.insertEl(item)
    }

    suspend fun insertInHistory(item: EntryHistory) {
        dao.insertInHistory(item)
    }

    fun getCat() = dao.getCat()

    fun getNameEl(cat: String) = dao.getNameEl(cat)

    fun getAllDateHis(fromDate: String, toDate: String) = dao.getAllDateHis(fromDate, toDate)

    fun getCatDateHis(fromDate: String, toDate: String, cat: String) = dao.getCatDateHis(fromDate, toDate, cat)

    fun getElDateHis(fromDate: String, toDate: String, cat: String, el: String) = dao.getElDateHis(fromDate, toDate, cat, el)

}