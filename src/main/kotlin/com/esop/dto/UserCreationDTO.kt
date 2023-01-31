package com.esop.dto

import com.esop.PhoneNumber
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


const val PHONE_NUMBER_REGEX = "^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}\$"

const val USERNAME_REGEX = "^[a-zA-Z]+([a-zA-Z]|_|[0-9])*"

const val EMAIL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"

const val ALPHABET_SEQUENCE_REGEX = "^\\s*[a-zA-Z]+[a-zA-Z\\s]*"

@Introspected
class UserCreationDTO @JsonCreator constructor(
    @JsonProperty("firstName")
    @field:NotBlank(message = "The firstName field cannot be blank or missing.")
    @field:Size(max=64, message = "firstName should not exceed 64 characters.")
    @field:Pattern(regexp = "($ALPHABET_SEQUENCE_REGEX| *)", message = "firstName can only contain english alphabets.")
    var firstName: String? = null,

    @JsonProperty("lastName")
    @field:NotBlank(message = "The lastName field cannot be blank or missing.")
    @field:Size(max=64, message = "lastName should not exceed 64 characters.")
    @field:Pattern(regexp = "($ALPHABET_SEQUENCE_REGEX| *)", message = "lastName can only contain english alphabets")
    var lastName: String? = null,

    @JsonProperty("phoneNumber")
    @field:NotBlank(message = "The phoneNumber field cannot be blank or missing.")
    @field:PhoneNumber()
    var phoneNumber: String? = null,

    @JsonProperty("email")
    @field:NotBlank(message = "The email field cannot be blank or missing.")
    @field:Size(max=30, message = "Email should not exceed 30 characters")
    @field:Email(regexp = "($EMAIL_REGEX| *)", message = "Invalid Email-ID.")
    var email: String? = null,

    @JsonProperty("username")
    @field:NotBlank(message = "The username field cannot be blank or missing.")
    @field:Size(max=25, message = "Username should not exceed 25 characters")
    @field:Pattern(regexp = "($USERNAME_REGEX| *)", message =
    "User Name should only consist english alphabets, numbers or underscore(s) and it must start with an english alphabet.")
    var username: String? = null,
)
