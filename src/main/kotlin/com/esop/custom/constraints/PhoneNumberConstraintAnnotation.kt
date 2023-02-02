package com.esop.custom.constraints

import javax.validation.Constraint

@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
annotation class PhoneNumber(
    val message: String = "Invalid Phone Number ({validatedValue})"
)