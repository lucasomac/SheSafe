package br.com.lucolimac.shesafe.android.data.model

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import com.google.gson.annotations.SerializedName

data class SecureContactModel(
    @SerializedName("email") val email: String,
    @SerializedName("lastname") val lastname: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String
) {
    constructor() : this(
        email = "",
        lastname = "",
        name = "",
        phone = ""
    )
    fun toEntity(): SecureContact {
        return SecureContact(name, lastname, phone, email)
    }
    companion object {
        fun fromEntity(secureContact: SecureContact): SecureContactModel {
            return SecureContactModel(
                email = secureContact.email,
                lastname = secureContact.lastName,
                name = secureContact.name,
                phone = secureContact.phoneNumber
            )
        }
    }
}