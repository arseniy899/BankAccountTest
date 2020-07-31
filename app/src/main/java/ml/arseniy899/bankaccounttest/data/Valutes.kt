package ml.arseniy899.bankaccounttest.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Valutes
{
	
	@SerializedName("Valute")
	@Expose
	public var valutes: HashMap<String, Valute>? = null
}

