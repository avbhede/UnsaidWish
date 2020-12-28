package com.av.uwish.Model

import com.google.gson.annotations.SerializedName

class MyVideoModel(
    @SerializedName("wishname")
    private val wishname: String,
    @SerializedName("videourl")
    private val videourl: String,
    @SerializedName("id")
    private val id: String,
    @SerializedName("date")
    private val date: String,
    @SerializedName("nominee")
    private val nominee: String,
    @SerializedName("description")
    private val description: String
)
