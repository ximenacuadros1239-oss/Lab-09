package com.example.lab09p

data class ProductsResponse(val products: List<ProductModel>)

data class ReviewModel(
    val rating: Int,
    val comment: String,
    val reviewerName: String
)

data class ProductModel(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val thumbnail: String,
    val images: List<String>,
    val reviews: List<ReviewModel>?
)