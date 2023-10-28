package com.example.storyapp

import com.example.storyapp.data.model.ListStoryItem

object DataDummy {
    fun generateDummyStoryEntity() : List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..15) {
            val story = ListStoryItem(
                "photo + $i",
                "created + $i",
                "name + $i",
                "desc + $i",
                i.toDouble(),
                i.toString(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}