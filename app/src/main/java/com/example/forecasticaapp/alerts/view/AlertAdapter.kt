package com.example.forecasticaapp.alerts.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.forecasticaapp.R
import com.example.forecasticaapp.database.RoomAlertPojo
import com.example.forecasticaapp.databinding.RowAlertBinding
import com.example.forecasticaapp.utils.Constants
import com.example.forecasticaapp.utils.getDateToAlert
import com.example.forecasticaapp.utils.getDayFormat
import com.example.forecasticaapp.utils.getTimeToAlert
import com.google.android.material.snackbar.Snackbar


class AlertAdapter(
    private var alertList: MutableList<RoomAlertPojo>,
    var context: Context,
    var myListener: OnAlertListener
) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private lateinit var binding: RowAlertBinding


    fun setList(list: List<RoomAlertPojo>) {
        alertList = list as MutableList<RoomAlertPojo>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = RowAlertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = alertList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val current = alertList[position]

        holder.binding.txtFromDate.text = getDateToAlert(current.dateFrom,"en")
        holder.binding.txtToDate.text = getDateToAlert(current.dateTo,"en")
        holder.binding.txtFromTime.text = getTimeToAlert(current.time,"en")
        holder.binding.txtToTime.text = getTimeToAlert(current.time,"en")
        holder.binding.imageAlertDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)

            alertDialog.apply {
                setIcon(R.drawable.baseline_delete_24)
                setTitle("Delete")
                setMessage("Are you sure you want to delete this alert ?")
                setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                    myListener.alertDeleteClick(current)
                    Snackbar.make(
                        binding.root,
                        "The alert deleted successfully",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                setNegativeButton("Cancel") { _, _ ->
                }
            }.create().show()
        }
    }

    inner class ViewHolder(var binding: RowAlertBinding) : RecyclerView.ViewHolder(binding.root)

}

interface OnAlertListener {
    fun alertCardClick(alertObject: RoomAlertPojo)
    fun alertDeleteClick(alertObject: RoomAlertPojo)
}