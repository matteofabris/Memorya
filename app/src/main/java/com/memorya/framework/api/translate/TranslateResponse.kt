package com.memorya.framework.api.translate

import com.google.gson.annotations.SerializedName

class TranslateResponse {
    @SerializedName("data")
    var data: TranslateTextResponseList? = null
}

class TranslateTextResponseList {
    @SerializedName("translations")
    var translations: ArrayList<TranslateTextResponseTranslation>? = null
}

class TranslateTextResponseTranslation {
    @SerializedName("detectedSourceLanguage")
    var detectedSourceLanguage: String = ""
    @SerializedName("model")
    var model: String = ""
    @SerializedName("translatedText")
    var translatedText: String = ""
}