package com.example.dicodingapi

import com.example.dicodingapi.database.Stories

object DataDummy {
    fun generateDummyStoryResponse(): List<Stories>{
        val items:MutableList<Stories> = arrayListOf()
        for (i in 0 .. 100){
            val story = Stories(
                id = "12932984dskksd",
                name = "Supri",
                photoUrl = "",
                createdAt = "2023-04-27T05:19.504Z",
                description = "sdfserdsd",
                lon = -7.288,
                lat = 110.01
            )
            items.add(story)
        }
        return items
    }
}