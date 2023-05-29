package com.example.application.data

import com.example.application.model.AccountInfo
import com.example.application.model.Userlockers

class DataInfo {
    fun loadUserInfo(): List<AccountInfo> {
        return listOf<AccountInfo>(
            AccountInfo("david", "rainho.david@gmail.com"),
            AccountInfo("exemplo1_nome", "exemplo1_email"),
            AccountInfo("exemplo2_nome", "exemplo2_email"),
            AccountInfo("exemplo3_nome", "exemplo3_email"),
            AccountInfo("exemplo4_nome", "exemplo4_email"),
            AccountInfo("exemplo5_nome", "exemplo5_email"),
            AccountInfo("exemplo2_nome", "exemplo2_email"),
            AccountInfo("exemplo3_nome", "exemplo3_email"),
            AccountInfo("exemplo4_nome", "exemplo4_email"),
            AccountInfo("exemplo5_nome", "exemplo5_email")

        )
    }
}