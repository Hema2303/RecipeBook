package com.example.myrecipebook.data

class Recipe() {
    var title: String? = null
    var description: String? = null
    var ingredients: String? = null
    var thumbnail: String? = null
    var key: String? = null

    constructor(title: String, link: String,
                ingredients: String, thumbnail: String,key :String): this() {
        this.title = title
        this.description = link
        this.ingredients = ingredients
        this.thumbnail = thumbnail
        this.key = key
    }
}