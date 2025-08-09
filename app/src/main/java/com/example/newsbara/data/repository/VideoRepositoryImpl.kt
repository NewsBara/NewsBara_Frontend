package com.example.newsbara.data.repository

import com.example.newsbara.data.model.script.ScriptResponseDto
import com.example.newsbara.data.service.VideoService
import com.example.newsbara.domain.model.DictionaryEntry
import com.example.newsbara.domain.model.ScriptLine
import com.example.newsbara.domain.model.toDictionaryEntries
import com.example.newsbara.domain.model.toScriptLine
import com.example.newsbara.domain.repository.VideoRepository
import com.example.newsbara.presentation.common.ResultState
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoService: VideoService
) : VideoRepository {

    override suspend fun fetchScript(videoId: String): ResultState<List<ScriptLine>> {
        return try {
            val response = videoService.getScriptByVideoId(videoId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    val domainResult = body.result.map { it.toScriptLine() }
                    ResultState.Success(domainResult)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun fetchDictionaryEntries(videoId: String): ResultState<List<DictionaryEntry>> {
        return try {
            val res = videoService.getScriptByVideoId(videoId)
            if (res.isSuccessful) {
                val body = res.body()
                if (body?.isSuccess == true && body.result != null) {
                    // 1) 각 ScriptResponseDto에서 keywords → DictionaryEntry 리스트로 변환
                    val raw = body.result.flatMap { it.toDictionaryEntries() }

                    // 2) 의미가 아예 없는 항목 제거(빈 리스트 필터)
                    val nonEmpty = raw.filter { it.wordnetSenses.isNotEmpty() || it.wordnetSensesKo.isNotEmpty() }

                    // 3) 같은 단어가 여러 문장에 등장했을 수 있으니 word 기준으로 머지
                    val merged = nonEmpty
                        .groupBy { it.word.lowercase() }
                        .map { (_, list) ->
                            val first = list.first()
                            DictionaryEntry(
                                word = first.word,
                                wordnetSenses = list.flatMap { it.wordnetSenses }.distinct(),
                                wordnetSensesKo = list.flatMap { it.wordnetSensesKo }.distinct()
                            )
                        }
                    // (선택) 다의어만 보고 싶다면 뜻이 2개 이상인 것만 남기기
                    // .filter { it.wordnetSenses.size > 1 || it.wordnetSensesKo.size > 1 }

                    ResultState.Success(merged)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${res.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }
}
