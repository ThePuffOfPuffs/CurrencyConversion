package com.example.currencyconversion

import com.google.gson.annotations.SerializedName

class ApiResponse {
    @SerializedName("rates") var rates: Map<String, CurrencyData>? = null
}