package br.com.lucolimac.shesafe.feature.home.di

import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin dependency injection module for the home and emergency SOS dispatch feature.
 *
 * Registers [HomeViewModel] for coordinating map location updates, contact verification,
 * confirmation dialogs, and emergency alert dispatch.
 */
val homeModule = module {
    viewModelOf(::HomeViewModel)
}
