package ml.arseniy899.bankaccounttest.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ml.arseniy899.bankaccounttest.R
import java.io.Serializable

class User : Serializable
{
	
	@SerializedName("card_number")
	@Expose
	var cardNumber: String = ""
	@SerializedName("type")
	@Expose
	var type: String = ""
	@SerializedName("cardholder_name")
	@Expose
	var cardholderName: String = ""
	@SerializedName("valid")
	@Expose
	var valid: String = ""
	@SerializedName("balance")
	@Expose
	var balance: Double = 0.toDouble()
	@SerializedName("transaction_history")
	@Expose
	var transactionHistory: List<TransactionHistory>? = null
	
	// UI-formatted fields
	
	var icon		: Int = R.drawable.bb_card_mastercard
	var balanceUSD	: String = "000.00"
	var balance2	: String = "000.00"
}