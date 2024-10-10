package com.husn.fashionapp

import androidx.compose.runtime.Composable

@Composable
fun getDummyProductsList(): List<Product> {
    val dummyProducts = listOf(
        Product(
            productId = "101",
            productName = "Blue Denim Jeans",
            brand = "Brand A",
            primaryImage = "https://example.com/image1.jpg",
            sizes = "M,L,XL",
            gender = "Men",
            price = 1999,
            rating = 4.5,
            ratingCount = 150,
            index = 1
        ),
        Product(
            productId = "102",
            productName = "Red Cotton Shirt",
            brand = "Brand B",
            primaryImage = "https://example.com/image2.jpg",
            sizes = "S,M,L",
            gender = "Women",
            price = 1299,
            rating = 4.0,
            ratingCount = 200,
            index = 2
        ),
        Product(
            productId = "102",
            productName = "Red Cotton Shirt",
            brand = "Brand B",
            primaryImage = "https://example.com/image2.jpg",
            sizes = "S,M,L",
            gender = "Women",
            price = 1299,
            rating = 4.0,
            ratingCount = 200,
            index = 2
        )
    )
    return dummyProducts
}

@Composable
fun getDummyProduct(): Product{
    return Product(
        productId = "101",
        productName = "Blue Denim Jeans",
        brand = "Brand super duper big brand master blaster A",
        primaryImage = "https://example.com/image1.jpg",
        sizes = "M,L,XL",
        gender = "Men",
        price = 1999,
        rating = 4.5,
        ratingCount = 150,
        index = 1
    )
}