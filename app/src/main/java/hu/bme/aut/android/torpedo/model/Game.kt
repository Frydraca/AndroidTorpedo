package hu.bme.aut.android.torpedo.model

class Game (
    var gameID: String? = null,
    var firstPlayerTurn: Boolean = false,
    var squares: String? = null,
    var squares2: String? = null,
    var squaresSeen1: String? = null,
    var squaresSeen2: String? = null
){}