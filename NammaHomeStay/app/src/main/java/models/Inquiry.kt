package models

data class Inquiry(
    var id: String = "",
    var travelerName: String = "",
    var travelerPhone: String = "",
    var message: String = "",
    var checkIn: String = "",
    var checkOut: String = "",
    var guests: Int = 0,
    var isRead: Boolean = false
)