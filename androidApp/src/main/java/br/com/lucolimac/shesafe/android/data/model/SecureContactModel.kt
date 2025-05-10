package br.com.lucolimac.shesafe.android.data.model

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import com.google.gson.annotations.SerializedName

data class SecureContactModel(
    @SerializedName("name") val name: String, @SerializedName("phone") val phone: String
) {
    constructor() : this(name = "", phone = "")

    fun toEntity(): SecureContact {
        return SecureContact(name, phone)
    }

    companion object {
        fun fromEntity(secureContact: SecureContact): SecureContactModel {
            return SecureContactModel(
                name = secureContact.name, phone = secureContact.phoneNumber
            )
        }
    }
}