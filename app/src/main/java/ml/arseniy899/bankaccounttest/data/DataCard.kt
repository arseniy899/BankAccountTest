package ml.arseniy899.bankaccounttest.data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataCard
{
	
	@SerializedName("users")
	@Expose
	var users: List<User>? = null
	
}