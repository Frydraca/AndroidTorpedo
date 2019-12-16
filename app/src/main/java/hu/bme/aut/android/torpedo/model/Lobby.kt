package hu.bme.aut.android.torpedo.model

class Lobby (
    var lobbyID: String? = null,
    var lobbyName: String? = null,
    var firstPlayerName: String? = null,
    var secondPlayerName: String? = null,
    var firstPlayerReady: Boolean = false,
    var secondPlayerReady: Boolean = false,
    var firstPlayerTurn: Boolean = false,
    var squares: String? = null,
    var squares2: String? = null,
    var squaresSeen1: String? = null,
    var squaresSeen2: String? = null
){}

