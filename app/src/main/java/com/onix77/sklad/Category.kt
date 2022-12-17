package com.onix77.sklad

import java.io.Serializable

class Category(val name: String) : Serializable {

    val listEl: MutableList<Element> = mutableListOf()

    fun addElement (el: Element) = listEl.add(el)

}