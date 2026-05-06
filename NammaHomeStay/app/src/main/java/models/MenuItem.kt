package models

data class MenuItem(
    var dishName: String = "",
    var category: String = "",  // Breakfast, Lunch, Dinner, Snacks, Special
    var price: Double = 0.0,
    var isVeg: Boolean = true,
    var id: String = "",
    var description: String = "",
    var imageUrl: String = ""
)