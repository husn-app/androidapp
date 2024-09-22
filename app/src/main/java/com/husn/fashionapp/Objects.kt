package com.husn.fashionapp

import org.json.JSONObject

data class Product(
    val additionalInfo: String? = "",
    val articleType: String? = "",
    val brand: String? = "",
    val category: String? = "",
    val gender: String? = "",
    val index: Int? = 0,
    val landingPageUrl: String? = "",
    val masterCategory: String? = "",
    val price: Int? = 0,
    val primaryColour: String? = "",
    val product: String? = "",
    val productId: Int? = 0,
    val productName: String? = "",
    val rating: Double = 0.0,
    val ratingCount: Int? = 0,
    val searchImage: String? = "",
    val sizes: String? = "",
    val subCategory: String? = ""
) {
    constructor(json: JSONObject) : this(
        additionalInfo = json.optString("additionalInfo", ""),
        articleType = json.optString("articleType", ""),
        brand = json.optString("brand", ""),
        category = json.optString("category", ""),
        gender = json.optString("gender", ""),
        index = json.optInt("index", 0),
        landingPageUrl = json.optString("landingPageUrl", ""),
        masterCategory = json.optString("masterCategory", ""),
        price = json.optInt("price", 0),
        primaryColour = json.optString("primaryColour", ""),
        product = json.optString("product", ""),
        productId = json.optInt("productId", 0),
        productName = json.optString("productName", ""),
        rating = json.optDouble("rating", 0.0),
        ratingCount = json.optInt("ratingCount", 0),
        searchImage = json.optString("searchImage", ""),
        sizes = json.optString("sizes", ""),
        subCategory = json.optString("subCategory", "")
    )
}
