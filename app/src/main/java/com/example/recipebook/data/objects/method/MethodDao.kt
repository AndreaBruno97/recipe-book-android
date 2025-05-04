package com.example.recipebook.data.objects.method

data class MethodDao(
    var value: String = "",

    // Form Validation Fields
    var validateValue: Boolean = false
) {
    //region Input Validation

    fun isValueValid(): Boolean {
        return value.isNotBlank()
    }

    fun validateInput(): Boolean {
        return isValueValid()
    }

    fun getInputValidationCopy(): MethodDao {
        return this.copy(
            validateValue = true
        )
    }

    //endregion
}