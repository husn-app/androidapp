package com.husn.fashionapp

import org.json.JSONObject
import java.util.Dictionary

data class Product(
    val originalWebsite: String = "",
    val productUrl: String = "",
    val productId: String = "",
    val productName: String = "",
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val brand: String = "",
    val primaryImage: String = "",
    val sizes: String = "",
    val gender: String = "",
    val price: Int = 0,
    val index: Int = 0,
) {
    constructor(json: JSONObject) : this(
        originalWebsite = json.optString("original_website", ""),
        productUrl = json.optString("product_url", ""),
        productId = json.optString("product_id", ""),
        productName = json.optString("product_name", ""),
        rating = json.optDouble("rating", 0.0),
        ratingCount = json.optInt("rating_count", 0),
        brand = json.optString("brand", ""),
        primaryImage = json.optString("primary_image", ""),
        sizes = json.optString("sizes", ""),
        gender = json.optString("gender", ""),
        price = json.optInt("price", 0),
        index = json.optInt("index", 0),
    )
}

data class InspirationProduct(
    val primary_image: String = "",
    val inspiration_subcategory_name: String = "",
    val inspiration_subcategory_query: String = "",
) {
    constructor(json: JSONObject) : this(
        primary_image = json.optString("primary_image", ""),
        inspiration_subcategory_name = json.optJSONObject("inspiration_subcategory")?.optString("name", "") ?: "",
        inspiration_subcategory_query = json.optJSONObject("inspiration_subcategory")?.optString("query", "") ?: ""
    )
}