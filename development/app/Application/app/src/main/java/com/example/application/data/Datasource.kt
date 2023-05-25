package com.example.application.data

import com.example.application.model.Userlockers

class Datasource {

    fun loadUserlockers(): List<Userlockers> {
        return listOf<Userlockers>(
            Userlockers("cenas"),
            Userlockers("batata"),
            Userlockers("lock"),
            Userlockers("outro"),
            Userlockers("sopa"),
            Userlockers("Conflito"),
            Userlockers("Miha"),
            Userlockers("Contexto")
        )
    }
}