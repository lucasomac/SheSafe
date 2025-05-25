package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.OrderHelpModel

interface OrderHelpService {
    suspend fun getOrdersHelp(): List<OrderHelpModel>
    suspend fun registerOrderHelp(
        orderHelpModel: OrderHelpModel
    ): Boolean
}