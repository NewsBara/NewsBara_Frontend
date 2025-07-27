package com.example.newsbara.domain.repository

import com.example.newsbara.BuildConfig
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.youtube.VideoSection
import com.example.newsbara.data.service.YouTubeApiService
import com.example.newsbara.di.YouTubeUtil
import javax.inject.Inject

class YouTubeRepository @Inject constructor(
    private val api: YouTubeApiService
) {

    suspend fun fetchVideoSections(channels: Map<String, String>): List<VideoSection> {
        val videoCategoryMap = fetchVideoCategoryMap()

        return channels.map { (channelName, channelId) ->
            val searchResponse = api.searchVideosByChannel(
                part = "snippet",
                channelId = channelId,
                query = "",
                type = "video",
                maxResults = 10,
                apiKey = getApiKey()
            )

            val videoIds = searchResponse.items.mapNotNull { it.id.videoId }

            val detailsResponse = api.getVideoDetails(
                part = "contentDetails,snippet",
                videoIds = videoIds.joinToString(","),
                apiKey = getApiKey()
            )

            val durationMap = detailsResponse.items.associate {
                it.id to YouTubeUtil.parseYouTubeDuration(it.contentDetails.duration)
            }

            val categoryIdMap = detailsResponse.items.associate {
                it.id to it.snippet.categoryId
            }

            val videos = searchResponse.items.mapNotNull {
                val videoId = it.id.videoId ?: return@mapNotNull null
                val length = durationMap[videoId] ?: "00:00"
                val categoryId = categoryIdMap[videoId] ?: ""
                val categoryName = videoCategoryMap[categoryId] ?: "Unknown"

                HistoryItem(
                    id = 0,
                    videoId = videoId,
                    title = it.snippet.title,
                    thumbnail = it.snippet.thumbnails.medium.url,
                    channel = channelName,
                    length = length,
                    category = categoryName,
                    status = "WATCHED",
                    createdAt = ""
                )
            }

            VideoSection(
                categoryTitle = channelName,
                videos = videos
            )
        }
    }

    private suspend fun fetchVideoCategoryMap(): Map<String, String> {
        val response = api.getVideoCategories("snippet", "US", getApiKey())
        return response.items.associate { it.id to it.snippet.title }
    }

    private fun getApiKey(): String = BuildConfig.YOUTUBE_API_KEY
}