package br.com.lucolimac.shesafe.domain.entity

/**
 * Domain entity representing a structured error returned by the Infobip SMS gateway.
 *
 * @property action Suggested remediation steps returned by the Infobip API.
 * @property description Human-readable description of why the request failed.
 * @property errorCode Machine-readable Infobip error code identifier.
 * @property resources Related developer documentation [Resource] links.
 * @property violations Specific field-level [Violation] entries when request validation fails.
 */
data class InfoBipFailure(
    val action: String,
    val description: String,
    val errorCode: String,
    val resources: List<Resource>,
    val violations: List<Violation>
) {
    /**
     * Domain representation of a documentation reference link returned with an Infobip API failure.
     *
     * @property name Title of the documentation resource.
     * @property url External URL pointing to the documentation page.
     */
    data class Resource(
        val name: String, val url: String
    )

    /**
     * Domain representation of a request property validation violation reported by Infobip.
     *
     * @property property Name or path of the invalid request field.
     * @property violation Reason why [property] failed validation.
     */
    data class Violation(
        val property: String, val violation: String
    )
}
