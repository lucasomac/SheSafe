package br.com.lucolimac.shesafe.feature.home.di

import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
}
