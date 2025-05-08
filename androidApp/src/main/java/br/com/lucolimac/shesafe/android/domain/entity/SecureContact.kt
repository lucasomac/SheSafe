package br.com.lucolimac.shesafe.android.domain.entity

data class SecureContact(
    val name: String, val lastName: String = "", val phoneNumber: String, val email: String = ""
) {
    //Create a default constructor with empty strings
    constructor() : this("", "", "", "")
}


