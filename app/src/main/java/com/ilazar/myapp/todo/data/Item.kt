package com.ilazar.myapp.todo.data

data class Item(
    val _id: String,
    var text: String,
    var date: String,
    var length: Int,
    var liked: Boolean
) {
    override fun toString(): String = text
}
