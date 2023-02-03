package com.esop.dto

import com.esop.dto.validation.PhoneNumber
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.*


const val USERNAME_REGEX = "^[a-zA-Z]+([a-zA-Z]|_|[0-9])*"

const val EMAIL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"

const val CHARS_EXCEPT_LANGUAGE_SCRIPT_DIGITS_AND_WHITESPACE_REGEX = "^[\\p{L}\\p{M}\\d\\s]*$"


@Introspected
class UserCreationDTO @JsonCreator constructor(
    @JsonProperty("firstName")
    @field:NotBlank(message = "First Name can not be missing or empty.")
    @field:Pattern(regexp = "^(\\S+\\s?)*\$", message = "First Name should not contain consecutive whitespaces")
    @field:Pattern(regexp = "[\\D]*", message = "First Name should not contain number digits")
    @field:Pattern(regexp = CHARS_EXCEPT_LANGUAGE_SCRIPT_DIGITS_AND_WHITESPACE_REGEX, message = "First Name should not contain special characters")
    var firstName: String? = null,

    @JsonProperty("lastName")
    @field:NotBlank(message = "First Name can not be missing or empty.")
    @field:Pattern(regexp = "^(\\S+\\s?)*\$", message = "First Name should not contain consecutive whitespaces")
    @field:Pattern(regexp = "[\\D]*", message = "First Name should not contain number digits")
    @field:Pattern(regexp = CHARS_EXCEPT_LANGUAGE_SCRIPT_DIGITS_AND_WHITESPACE_REGEX, message = "First Name should not contain special characters")
    var lastName: String? = null,

    @JsonProperty("phoneNumber")
    @field:NotBlank(message = "Phone Number can not be missing or empty.")
    @field:PhoneNumber()
    var phoneNumber: String? = null,

    @JsonProperty("email")
    @field:NotBlank(message = "Email can not be missing or empty.")
    @field:Size(max=30, message = "Email should not exceed 30 characters")
    @field:Email(regexp = "($EMAIL_REGEX| *)", message = "Invalid Email-ID.")
    var email: String? = null,

    @JsonProperty("username")
    @field:NotBlank(message = "User Name can not be missing or empty.")
    @field:Size(max=20, message = "User Name should not exceed 20 characters")
    @field:Pattern(regexp = "($USERNAME_REGEX| *)", message =
    "User Name should only consist alphabets, numbers or underscore(s) and it must start with an alphabet.")
    var username: String? = null,
) {
    @AssertTrue(message = "combination of First Name and Last Name should not exceed 64 characters")
    fun isCombinationOfFirstNameAndLastNameIsLessThanLimit(): Boolean {
        return (firstName == null || lastName == null || (firstName + lastName).length <= 64)
    }
}
