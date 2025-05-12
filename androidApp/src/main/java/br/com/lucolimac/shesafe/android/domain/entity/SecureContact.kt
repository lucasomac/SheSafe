package br.com.lucolimac.shesafe.android.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SecureContact(val name: String, val phoneNumber: String) : Parcelable {
    //Create a default constructor with empty strings
    constructor() : this("", "")
}
