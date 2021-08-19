package com.nussair.nagwatest.model

import com.google.gson.annotations.SerializedName

data class FileItem(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: Type,
    @SerializedName("url") val url: String
)

enum class Type {
    @SerializedName("VIDEO")
    VIDEO,
    @SerializedName("PDF")
    PDF
}