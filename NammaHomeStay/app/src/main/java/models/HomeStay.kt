package models

data class HomeStay(
    var id: String = "",
    var hostName: String = "",
    var location: String = "",
    var description: String = "",
    var phone: String = "",
    var dailyRate: Double = 0.0,
    var isAvailable: Boolean = true,
    var photoUrls: List<String> = emptyList(),

    // Verification Checklist
    var cleanRoom: Boolean = false,
    var cleanToilet: Boolean = false,
    var mosquitoNet: Boolean = false,
    var hotWater: Boolean = false,
    var wifiAvailable: Boolean = false,
    var parkingAvailable: Boolean = false
)