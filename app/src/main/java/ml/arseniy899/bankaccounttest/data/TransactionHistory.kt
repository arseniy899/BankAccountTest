package ml.arseniy899.bankaccounttest.data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TransactionHistory : Serializable
{
	
	@SerializedName("title")
	@Expose
	var title: String? = null
	@SerializedName("icon_url")
	@Expose
	var iconUrl: String? = null
	@SerializedName("date")
	@Expose
	var date: String? = null
	@SerializedName("amount")
	@Expose
	var amount: Double? = null
	
	var amountCURUser: String? = null
	var amountUSDUser: String? = null
	
}