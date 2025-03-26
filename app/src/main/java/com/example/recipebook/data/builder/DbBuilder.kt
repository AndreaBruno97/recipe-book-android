package com.example.recipebook.data.builder

import com.example.recipebook.data.objects.ingredient.Ingredient
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.tag.Tag
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class DbBuilder {

    companion object{

        fun getDb(): Realm{
            val schema = setOf(
                Recipe::class,
                Ingredient::class,
                Tag::class
            )

            val realmConfiguration = RealmConfiguration.Builder(schema)
                .schemaVersion(DbMigration.CURRENT_VERSION)
                .initialData(DbInitialDataCallback())
                //.migration()
                .build()

            val realm = Realm.open(configuration = realmConfiguration)
            println("schemaVersion " + realm.schemaVersion())
            return realm
        }

    }

}