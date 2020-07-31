package ml.arseniy899.bankaccounttest.model

import com.google.gson.Gson
import android.R.attr.data
import com.google.gson.JsonObject
import ml.arseniy899.bankaccounttest.data.Valute
import ml.arseniy899.bankaccounttest.data.Valutes


class CurrencyApiResponse
{
	public var valutes : HashMap<String, Valute>? = null
	var error: Throwable? = null
	
	constructor(valutesRaw: Valutes)
	{
		this.valutes = valutesRaw.valutes// Gson().fromJson(valutesRaw, Valutes::class.java)
		this.error = null
	}
	
	constructor(error: Throwable)
	{
		this.error = error
		this.valutes = null
	}    // Getters...
	constructor(valutes: HashMap<String, Valute>, error: Throwable)
	{
		this.error = error
		this.valutes = valutes// Gson().fromJson(valutesRaw, Valutes::class.java)
	}    // Getters...
}