package com.husn.fashionapp

import androidx.compose.runtime.Composable

@Composable
fun getDummyProductsList(): List<Product> {
    val dummyProducts = listOf(
        Product(
            additionalInfo = "Comfortable and stylish",
            articleType = "Jeans",
            brand = "Brand A",
            category = "Bottomwear",
            gender = "Men",
            index = 1,
            landingPageUrl = "dresses/tokyo+talkies/tokyo-talkies-abstract-printed-sheath-mini-dress/21627304/buy",
            masterCategory = "Apparel",
            price = 1999,
            primaryColour = "Blue",
            product = "Denim Jeans",
            productId = 101,
            productName = "Blue Denim Jeans",
            rating = 4.5,
            ratingCount = 150,
            searchImage = "https://example.com/image1.jpg",
            sizes = "M,L,XL",
            subCategory = "Casual Wear"
        ),
        Product(
            additionalInfo = "Elegant and comfortable",
            articleType = "Shirt",
            brand = "Brand B",
            category = "Topwear",
            gender = "Women",
            index = 2,
            landingPageUrl = "dresses/sera/sera-black-bodycon-mini-dress/16404534/buy",
            masterCategory = "Apparel",
            price = 1299,
            primaryColour = "Red",
            product = "Cotton Shirt",
            productId = 102,
            productName = "Red Cotton Shirt",
            rating = 4.0,
            ratingCount = 200,
            searchImage = "https://example.com/image2.jpg",
            sizes = "S,M,L",
            subCategory = "Formal Wear"
        ),
        Product(
            additionalInfo = "Elegant and comfortable",
            articleType = "Shirt",
            brand = "Brand B",
            category = "Topwear",
            gender = "Women",
            index = 2,
            landingPageUrl = "dresses/sera/sera-black-bodycon-mini-dress/16404534/buy",
            masterCategory = "Apparel",
            price = 1299,
            primaryColour = "Red",
            product = "Cotton Shirt",
            productId = 102,
            productName = "Red Cotton Shirt",
            rating = 4.0,
            ratingCount = 200,
            searchImage = "https://example.com/image2.jpg",
            sizes = "S,M,L",
            subCategory = "Formal Wear"
        )
    )
    return dummyProducts
}

@Composable
fun getDummyProduct(): Product{
    return Product(
        additionalInfo = "Comfortable and stylish and a veyr tremendous title",
        articleType = "Jeans",
        brand = "Brand super duper big brand master blaster A",
        category = "Bottomwear",
        gender = "Men",
        index = 1,
        landingPageUrl = "dresses/tokyo+talkies/tokyo-talkies-abstract-printed-sheath-mini-dress/21627304/buy",
        masterCategory = "Apparel",
        price = 1999,
        primaryColour = "Blue",
        product = "Denim Jeans",
        productId = 101,
        productName = "Blue Denim Jeans",
        rating = 4.5,
        ratingCount = 150,
        searchImage = "https://example.com/image1.jpg",
        sizes = "M,L,XL",
        subCategory = "Casual Wear"
    )
}