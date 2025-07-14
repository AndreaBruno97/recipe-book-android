package com.example.recipebook.data.objects.ingredient

import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup

sealed interface IngredientItemDao {
    fun validateInput(): Boolean
    fun isEmpty(): Boolean
    fun enableInputValidation()
}

//region IngredientGroupTitleDao

data class IngredientGroupTitleDao(
    var title: String? = null,

    // Form Validation Fields
    var validateTitle: Boolean = false
) : IngredientItemDao {

    fun toIngredientGroup(): IngredientGroup = IngredientGroup(
        title = title
    )

    //region Input Validation

    fun isTitleValid(): Boolean {
        return true
    }

    override fun validateInput(): Boolean {
        return isTitleValid()
    }

    override fun isEmpty(): Boolean {
        return title.isNullOrEmpty()
    }

    override fun enableInputValidation() {
        validateTitle = true
    }

    //endregion
}

fun IngredientGroup.toIngredientGroupTitleDao() = IngredientGroupTitleDao(
    title = title
)

//endregion

//region IngredientDao

data class IngredientDao(
    var name: String = "",
    var quantity: String = "",
    var value: String = "",

    // Form Validation Fields
    var validateName: Boolean = false,
    var validateQuantity: Boolean = false,
    var validateValue: Boolean = false
) : IngredientItemDao {
    fun toIngredient(): Ingredient = Ingredient(
        name = name,
        quantity = quantity.toFloatOrNull(),
        value = value
    )

    //region Input Validation

    fun isNameValid(): Boolean {
        return name.isNotBlank()
    }

    fun isQuantityValid(): Boolean {
        return quantity.isBlank() || quantity.toFloatOrNull() != null
    }

    fun isValueValid(): Boolean {
        return true
    }

    override fun validateInput(): Boolean {
        return isNameValid() &&
                isQuantityValid() &&
                isValueValid()
    }

    override fun isEmpty(): Boolean {
        return name.isEmpty() &&
                quantity.isEmpty() &&
                value.isEmpty()
    }

    override fun enableInputValidation() {
        validateName = true
        validateQuantity = true
        validateValue = true
    }

    //endregion
}

fun Ingredient.toIngredientDao(): IngredientDao {
    var formattedQuantity: String = ""

    if (quantity != null) {
        formattedQuantity = if (quantity?.rem(1F) == 0F) {
            quantity?.toInt().toString()
        } else {
            quantity.toString()
        }
    }

    return IngredientDao(
        name = name,
        quantity = formattedQuantity,
        value = value
    )
}

//endregion