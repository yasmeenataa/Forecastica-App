package com.example.forecasticaapp.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteWeather")
data class RoomFavPojo(
    @PrimaryKey(autoGenerate = true)
    var favID:Int=0,
    val lat: Double,
    val lon: Double,
    val address: String?
    ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(favID)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomFavPojo> {
        override fun createFromParcel(parcel: Parcel): RoomFavPojo {
            return RoomFavPojo(parcel)
        }

        override fun newArray(size: Int): Array<RoomFavPojo?> {
            return arrayOfNulls(size)
        }
    }
}