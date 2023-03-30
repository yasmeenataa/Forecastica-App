package com.example.forecasticaapp.homePage.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.forecasticaapp.databinding.RowTimeBinding
import com.example.forecasticaapp.models.Hourly
import com.example.forecasticaapp.utils.*

class HoursAdapter(private var hoursList: MutableList<Hourly>, var context: Context) :
    RecyclerView.Adapter<HoursAdapter.ViewHolder>() {
    private lateinit var binding: RowTimeBinding
    private val sharedPreference: SharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val language =  sharedPreference.getString(Constants.LANGUAGE,"en") !!

    fun setList(list: List<Hourly>) {
        hoursList = list as MutableList<Hourly>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = RowTimeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = hoursList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = hoursList[position]
        holder.binding.txtTime.text= getTimeHourlyFormat(current.dt,language)
        holder.binding.iconTime.setImageResource(getImageIcon(current.weather[0].icon))
        holder.binding.txtTempTime.text="${current.temp}${getTemperatureUnit(context)}"
    }
    inner class ViewHolder(var binding: RowTimeBinding) : RecyclerView.ViewHolder(binding.root)


}