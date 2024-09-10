package com.example.currencyconversion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.currencyconversion.ui.theme.CurrencyConversionTheme
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

const val APIKEY = "d557386d0fff55c64cad46a8d8aba087f5265358"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyConversionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CurrencyConverter(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyConverter(modifier: Modifier = Modifier) {
    var fromCurrency by remember { mutableStateOf("EUR") }
    var toCurrency by remember { mutableStateOf("GBP") }
    var amount by remember { mutableStateOf("10") }
    var convertedAmount by remember { mutableStateOf("") }

    val client = OkHttpClient()

    fun performConversion() {
        val format = "json"
        val url =
            "https://api.getgeoapi.com/v2/currency/convert?api_key=$APIKEY&from=$fromCurrency&to=$toCurrency&amount=$amount&format=$format"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val apiResponse = response.body?.string()
                    val gson = Gson()
                    val gsonResponse = gson.fromJson(apiResponse, ApiResponse::class.java)
                    val gsonData = gsonResponse.rates?.get(toCurrency)

                    if (gsonData != null) {
                        convertedAmount = gsonData.rateForAmount ?: "Conversion failed"
                    }
                }
            }
        })
    }

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = fromCurrency,
            onValueChange = { fromCurrency = it },
            label = { Text("From Currency (e.g., EUR)") }
        )

        OutlinedTextField(
            value = toCurrency,
            onValueChange = { toCurrency = it },
            label = { Text("To Currency (e.g., GBP)") }
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") }
        )

        Button(onClick = { performConversion() }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Convert")
        }

        Text(
            text = "Converted Amount: $convertedAmount",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyConverterPreview() {
    CurrencyConversionTheme {
        CurrencyConverter()
    }
}
