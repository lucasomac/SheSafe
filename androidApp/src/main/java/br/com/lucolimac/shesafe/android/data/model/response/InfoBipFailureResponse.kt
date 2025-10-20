package br.com.lucolimac.shesafe.android.data.model.response

import br.com.lucolimac.shesafe.android.domain.entity.InfoBipFailure
import com.google.gson.annotations.SerializedName

data class InfoBipFailureResponse(
    @SerializedName("action") val action: String,
    @SerializedName("description") val description: String,
    @SerializedName("errorCode") val errorCode: String,
    @SerializedName("resources") val resources: List<Resource>,
    @SerializedName("violations") val violations: List<Violation>
) {
    fun toEntity(): InfoBipFailure {
        return InfoBipFailure(
            this.action,
            this.description,
            this.errorCode,
            this.resources.map { it.toEntity() },
            this.violations.map { it.toEntity() })
    }

    data class Resource(
        @SerializedName("name") val name: String, @SerializedName("url") val url: String
    ) {
        fun toEntity(): InfoBipFailure.Resource {
            return InfoBipFailure.Resource(this.name, this.url)
        }
    }

    data class Violation(
        @SerializedName("property") val `property`: String,
        @SerializedName("violation") val violation: String
    ) {
        fun toEntity(): InfoBipFailure.Violation {
            return InfoBipFailure.Violation(this.`property`, this.violation)
        }
    }
}


