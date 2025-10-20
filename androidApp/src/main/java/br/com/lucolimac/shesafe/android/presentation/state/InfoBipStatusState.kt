package br.com.lucolimac.shesafe.android.presentation.state

sealed class InfoBipStatusState(){
    object Idle : InfoBipStatusState()
    data class Result(val sent: Boolean) : InfoBipStatusState()

}
