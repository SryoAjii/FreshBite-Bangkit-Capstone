package com.example.freshbite.retrofit.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("confidenceScore")
	val confidenceScore: Double? = null,

	@field:SerializedName("suggestion")
	val suggestion: String? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("explanation")
	val explanation: String? = null
)
