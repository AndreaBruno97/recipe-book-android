package com.example.recipebook.data

import com.example.recipebook.data.recipe.Recipe
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class DbBuilder {

    companion object{

        fun getDb(): Realm{
            val realm = Realm.open(
                configuration = RealmConfiguration.create(
                    schema = setOf(
                        Recipe::class
                    )
                )
            )

            return realm
        }

    }

}