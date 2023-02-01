package com.esop.service

import com.esop.dto.UserCreationDTO
import com.esop.service.UserService.Companion.emailList
import com.esop.service.UserService.Companion.phoneNumberList
import com.esop.service.UserService.Companion.userList
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UserServiceTest {

    @AfterEach
    fun `it should clear the in memory data`() {
        userList.clear()
        emailList.clear()
        phoneNumberList.clear()
    }

    @Test
    fun `it should create the user, given valid inputs`(){
        val user1 = UserCreationDTO("Kajal", "Pawar", "+919827362534", "kajal@sahaj.ai", "kajal")

        UserService().registerUser(user1)

        Assertions.assertTrue(emailList.contains("kajal@sahaj.ai"))
        Assertions.assertTrue(phoneNumberList.contains("+919827362534"))
        Assertions.assertTrue(userList.containsKey("kajal"))
    }

    @Test
    fun `it should not create the user as phone number is already exist`(){
        val user1 = UserCreationDTO("Kajal", "Pawar", "+919876568176", "kajal@sahaj.ai", "kajal")
        UserService().registerUser(user1)
        val user2 = UserCreationDTO("Sankaranarayanan", "M", "+919876568176", "sankaranarayananm@sahaj.ai", "sankar")

        UserService().registerUser(user2)

        Assertions.assertFalse(emailList.contains("sankaranarayananm@sahaj.ai"))
        Assertions.assertFalse(userList.containsKey("sankar"))
    }

    @Test
    fun `it should not create the user as email is already exist`(){
        val user1 = UserCreationDTO("Kajal", "Pawar", "+919876568176", "kajal@sahaj.ai", "kajal")
        UserService().registerUser(user1)
        val user2 = UserCreationDTO("Sankaranarayanan", "M", "+919887568176", "kajal@sahaj.ai", "sankar")

        UserService().registerUser(user2)

        Assertions.assertFalse(phoneNumberList.contains("+919887568176"))
        Assertions.assertFalse(userList.containsKey("sankar"))
    }

    @Test
    fun `it should not create the user as username is already exist`(){
        val user1 = UserCreationDTO("Kajal", "Pawar", "+919876568176", "kajal@sahaj.ai", "kajal")
        UserService().registerUser(user1)
        val user2 = UserCreationDTO("Sankaranarayanan", "M", "+919887568176", "sankar@sahaj.ai", "kajal")

        UserService().registerUser(user2)

        Assertions.assertFalse(emailList.contains("sankaranarayananm@sahaj.ai"))
        Assertions.assertFalse(phoneNumberList.contains("919887568176"))
    }


}
