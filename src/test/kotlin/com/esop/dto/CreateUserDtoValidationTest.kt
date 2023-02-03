package com.esop.dto

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.validation.Validator

@MicronautTest
class CreateUserDtoValidationTest {

    @Inject
    lateinit var validator: Validator

    @Test
    fun `it should generate error if first name is null`() {
        val userDetails = UserCreationDTO()

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "First Name is required" } != null)
    }


    @Test
    fun `it should generate error if last name is null`() {
        val userDetails = UserCreationDTO()

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "Last Name is required" } != null)
    }


    @Test
    fun `it should generate error if first name contains two consecutive whitespaces`() {
        val userDetails = UserCreationDTO(firstName = "a  n")

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "First Name should not contain consecutive whitespaces" } != null)
    }


    @Test
    fun `it should generate error if last name contains two consecutive whitespaces`() {
        val userDetails = UserCreationDTO(lastName = "a  n")

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "Last Name should not contain consecutive whitespaces" } != null)
    }

    @Test
    fun `it should generate error if first name contains digits`() {
        val userDetails = UserCreationDTO(firstName = "a 1")

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "First Name should not contain number digits" } != null)
    }

    @Test
    fun `it should generate error if last name contains digits`() {
        val userDetails = UserCreationDTO(lastName = "a 1")

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "Last Name should not contain number digits" } != null)
    }


    @Test
    fun `it should generate error if first name contains special characters`() {
        val userDetails = UserCreationDTO(firstName = "arun%")

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "First Name should not contain special characters" } != null)
    }

    @Test
    fun `it should generate error if last name contains special characters`() {
        val userDetails = UserCreationDTO(lastName = "arun@")

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "Last Name should not contain special characters" } != null)
    }



    @Test
    fun `it should generate error if length after combining first name and last name is greater than 64 characters`() {
        val userDetails = UserCreationDTO(firstName = "abcdefghij".repeat(10), lastName = "abcde")

        val errors = validator.validate(userDetails)

        Assertions.assertTrue(errors.find { it.message == "combination of First Name and Last Name should not exceed 64 characters" } != null)
    }

}