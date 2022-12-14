package com.onix77.sklad

import java.io.Serializable

class Category(val name: String) : Serializable {

    private val list: MutableList<Element> = mutableListOf()

    fun addElement (el: Element) = list.add(el)

}