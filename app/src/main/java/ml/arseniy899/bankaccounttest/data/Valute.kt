package ml.arseniy899.bankaccounttest.data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Valute
{
	
	@SerializedName("ID")
	@Expose
	var id: String? = null
	@SerializedName("NumCode")
	@Expose
	var numCode: String? = null
	@SerializedName("CharCode")
	@Expose
	var charCode: String? = null
	@SerializedName("Nominal")
	@Expose
	var nominal: Int = 0
	@SerializedName("Name")
	@Expose
	var name: String? = null
	@SerializedName("Value")
	@Expose
	var value: Double = 0.toDouble()
	@SerializedName("Previous")
	@Expose
	var previous: Double = 0.toDouble()
	
}