package com.example.forecasticaapp.favourite.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.forecasticaapp.R
import com.example.forecasticaapp.database.RoomFavPojo
import com.example.forecasticaapp.databinding.RowFavBinding
import com.example.forecasticaapp.utils.isConnected
import com.google.android.material.snackbar.Snackbar

class FavouriteAdapter(
    private var favList: MutableList<RoomFavPojo>,
    var context: Context,
    var myListener: OnFavoriteListener
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {
    private lateinit var binding: RowFavBinding


    fun setList(list: List<RoomFavPojo>) {
        favList = list as MutableList<RoomFavPojo>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = RowFavBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = favList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = favList[position]
        holder.binding.txtFavCountryName.text = current.address
        holder.binding.favCardView.setOnClickListener {
            if (isConnected(context)) {
                myListener.cardClick(current)
            } else {
                Snackbar.make(
                    binding.root,
                    "You're offline, Check Internet Connection",
                    Snackbar.ANIMATION_MODE_FADE
                ).show()
            }
        }
        holder.binding.imageFavDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)

            alertDialog.apply {
                setIcon(R.drawable.baseline_delete_24)
                setTitle("Delete")
                setMessage("Are you sure you want to delete ${current.address} from favorite?")
                setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                    myListener.deleteClick(current)
                    Snackbar.make(binding.root,"${current.address} deleted successfully",Snackbar.LENGTH_LONG).show()
                }
                setNegativeButton("Cancel") { _, _ ->

                }


            }.create().show()
           }

    }

    inner class ViewHolder(var binding: RowFavBinding) : RecyclerView.ViewHolder(binding.root)

}

interface OnFavoriteListener {
    fun cardClick(favObject: RoomFavPojo)
    fun deleteClick(favObject: RoomFavPojo)
}