package com.example.forecasticaapp.homePage.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.example.forecasticaapp.R
import com.example.forecasticaapp.utils.Constants
import com.example.forecasticaapp.databinding.RowDayBinding
import com.example.forecasticaapp.models.Daily
import com.example.forecasticaapp.utils.getDayFormat
import com.example.forecasticaapp.utils.getImageIcon
import com.example.forecasticaapp.utils.getTemperatureUnit

class DaysAdapter(private var daysList: MutableList<Daily>, var context: Context) :
    RecyclerView.Adapter<DaysAdapter.ViewHolder>() {
    private lateinit var binding: RowDayBinding
    private val sharedPreference: SharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val language =  sharedPreference.getString(Constants.LANGUAGE,"en") !!

    fun setList(list: List<Daily>) {
        daysList = list as MutableList<Daily>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = RowDayBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = daysList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = daysList[position]
        if(position==0)
        {
            holder.binding.txtDayName.text= context.getString(R.string.tomorrow)
            holder.binding.dayConstraintLayout.setBackgroundColor(context.getColor(R.color.secondary_color))
        }
        else
        {
            holder.binding.txtDayName.text= getDayFormat(current.dt,language)
            holder.binding.dayConstraintLayout.setBackgroundColor(context.getColor(R.color.light_blue))
        }

        holder.binding.iconDay.setImageResource(getImageIcon(current.weather[0].icon))
        holder.binding.txtDescDay.text=current.weather[0].description
        holder.binding.txtMaxTempDay.text="${current.temp.max}${getTemperatureUnit(context)}"
        holder.binding.txtMinTempDay.text="${current.temp.min}${getTemperatureUnit(context)}"
    }
    inner class ViewHolder(var binding: RowDayBinding) : RecyclerView.ViewHolder(binding.root)


}