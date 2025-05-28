package br.com.lucolimac.shesafe.android.data.model

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import com.google.gson.annotations.SerializedName

data class SecureContactModel(
    @SerializedName("name") val name: String, @SerializedName("phoneNumber") val phoneNumber: String
) {
    constructor() : this(name = "", phoneNumber = "")

    fun toEntity(): SecureContact {
        return SecureContact(name, phoneNumber)
    }

    companion object {
        fun fromEntity(secureContact: SecureContact): SecureContactModel {
            return SecureContactModel(
                name = secureContact.name, phoneNumber = secureContact.phoneNumber
            )
        }
    }
}