package models

data class LocalSpot(
    var name: String = "",
    var type: String = "",   // Waterfall, Viewpoint, Temple, Farm, Market
    var distanceKm: Double = 0.0,
    var id: String = "",
    var description: String = "",
    var bestTime: String = "",
    var imageUrl: String = "",
    var mapsLink: String = ""
)