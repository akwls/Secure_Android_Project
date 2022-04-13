package com.example.secureproject.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encrypt")
class Encrypt(
    var plainText: String?, var encryptText: String?,
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(plainText)
        parcel.writeString(encryptText)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Encrypt> {
        override fun createFromParcel(parcel: Parcel): Encrypt {
            return Encrypt(parcel)
        }

        override fun newArray(size: Int): Array<Encrypt?> {
            return arrayOfNulls(size)
        }
    }

}