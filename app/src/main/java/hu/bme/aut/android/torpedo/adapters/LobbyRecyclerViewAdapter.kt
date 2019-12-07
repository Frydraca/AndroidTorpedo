package hu.bme.aut.android.torpedo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.torpedo.BrowserActivity
import hu.bme.aut.android.torpedo.R
import hu.bme.aut.android.torpedo.model.Lobby
import kotlinx.android.synthetic.main.lobby_card.view.*

class LobbyRecyclerViewAdapter() : RecyclerView.Adapter<LobbyRecyclerViewAdapter.ViewHolder>() {

    private val lobbyList = mutableListOf<Lobby>()
    var itemClickListener: LobbyItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.lobbyName
        val tvBody: TextView = itemView.hostName
        val tvButton: Button = itemView.joinButton

        var lobby: Lobby? = null
        init {
            itemView.setOnClickListener {
                lobby?.let { lobby -> itemClickListener?.onItemClick(lobby) }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.lobby_card, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val tmpLobby = lobbyList[position]
        viewHolder.lobby = tmpLobby

        viewHolder.tvTitle.text = tmpLobby.lobbyName
        viewHolder.tvBody.text = tmpLobby.firstPlayerName

    }

    override fun getItemCount() = lobbyList.size

    interface LobbyItemClickListener {
        fun onItemClick(lobby: Lobby)
    }

    fun add(newLobby: Lobby) {
        Log.w("LOBBY", "Add")
        lobbyList.add(newLobby)
        notifyDataSetChanged()
    }

    fun update(modifiedLobby: Lobby) {
        val index = lobbyList.indexOfFirst { oldLobby -> oldLobby.lobbyID == modifiedLobby.lobbyID }
        lobbyList[index] = modifiedLobby
        notifyItemChanged(index)
    }

    fun delete(deletedLobby: Lobby) {
        val index = lobbyList.indexOfFirst { oldLobby -> oldLobby.lobbyID == deletedLobby.lobbyID }
        lobbyList.remove(deletedLobby)
        notifyItemRemoved(index)
    }
}