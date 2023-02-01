package com.esop.customConstraints

import javax.validation.Constraint
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
annotation class PhoneNumber(
    val message: String = "Invalid Phone Number ({validatedValue})"
)