package com.example.application.data

import com.example.application.R
import com.example.application.model.Affirmation

class Datasource {

    fun loadAffirmations(): List<Affirmation> {
        return listOf<Affirmation>(
            Affirmation("cenas"),
            Affirmation("batata"),
            Affirmation("lock"),
            Affirmation("outro"),
            Affirmation("sopa"),
            Affirmation("Conflito"),
            Affirmation("Miha"),
            Affirmation("Contexto")
        )
    }
}