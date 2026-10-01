package br.com.lucolimac.shesafe.domain.entity

data class InfoBipFailure(
    val action: String,
    val description: String,
    val errorCode: String,
    val resources: List<Resource>,
    val violations: List<Violation>
) {
    data class Resource(
        val name: String, val url: String
    )

    data class Violation(
        val property: String, val violation: String
    )
}
