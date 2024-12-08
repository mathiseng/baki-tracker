package com.example.baki_tracker.repository

import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.User
import me.tatarka.inject.annotations.Inject


//not fully implemented because we dont know what we want to do with the user
@Inject
@Singleton
class UserProfileRepository() : IUserProfileRepository {
    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(user: User) {
        TODO("Not yet implemented")
    }


}

interface IUserProfileRepository {

    suspend fun updateUser(user: User)

    suspend fun createUser(user: User)
}