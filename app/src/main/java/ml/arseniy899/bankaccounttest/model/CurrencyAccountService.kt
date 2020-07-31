package ml.arseniy899.bankaccounttest.model

import com.google.gson.JsonObject
import ml.arseniy899.bankaccounttest.data.DataCard
import ml.arseniy899.bankaccounttest.data.Valute
import ml.arseniy899.bankaccounttest.data.Valutes
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET


interface CurrencyAccountService
{
	@GET("daily_json.js")
	fun getCurrency(): Call<Valutes>
}