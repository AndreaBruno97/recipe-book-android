package com.example.recipebook.data

import com.example.recipebook.data.ingredient.Ingredient
import com.example.recipebook.data.recipe.Recipe
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class DbBuilder {

    companion object{

        fun getDb(): Realm{
            val schema = setOf(
                Recipe::class,
                Ingredient::class
            )

            val realmConfiguration = RealmConfiguration.Builder(schema)
                .schemaVersion(DbMigration.currentVersion)
                //.migration()
                .build()

            val realm = Realm.open(configuration = realmConfiguration)
            println("schemaVersion " + realm.schemaVersion())
            return realm
        }

    }

}