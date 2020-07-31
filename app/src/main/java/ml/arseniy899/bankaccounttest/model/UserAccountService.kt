package ml.arseniy899.bankaccounttest.model

import ml.arseniy899.bankaccounttest.data.DataCard
import retrofit2.Call
import retrofit2.http.GET


interface UserAccountService
{
	@GET("users.json")
	fun getCards(): Call<DataCard>
}