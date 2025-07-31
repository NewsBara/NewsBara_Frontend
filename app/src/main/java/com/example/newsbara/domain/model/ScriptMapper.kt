package com.example.newsbara.domain.model

import com.example.newsbara.data.model.script.KeywordDto
import com.example.newsbara.data.model.script.ScriptResponseDto

fun ScriptResponseDto.toScriptLine(): ScriptLine {
    return ScriptLine(
        sentence = sentence,
        sentenceKo = sentenceKo,
        timestamp = timestamp,
        keywords = keywords.map {
            it.toKeywordInfo()
        }
    )
}

fun KeywordDto.toKeywordInfo(): KeywordInfo {
    return KeywordInfo(
        word = word,
        gptDefinition = gptDefinition,
        gptDefinitionKo = gptDefinitionKo,
        bertDefinition = bertDefinition,
        bertDefinitionKo = bertDefinitionKo,
        bertSource = bertSource,
        bertConfidence = bertConfidence
    )
}

fun KeywordDto.toDictionaryEntry(): DictionaryEntry {
    return DictionaryEntry(
        word = word,
        wordnetSenses = wordnetSenses,
        wordnetSensesKo = wordnetSensesKo
    )
}