package com.esop.dto.validation


const val SPECIAL_CHARS_REGEX = "[^<>{}\\\"/|;:.,~!?@#\$%^=&*\\\\]\\\\\\\\()\\\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©0-9_+]]"


class Utils {
    companion object {
        fun containsDigit(value: String) = value.contains("\\d".toRegex())

        fun containsSpecialCharacters(value: String) = value.contains(SPECIAL_CHARS_REGEX.toRegex())
    }
}