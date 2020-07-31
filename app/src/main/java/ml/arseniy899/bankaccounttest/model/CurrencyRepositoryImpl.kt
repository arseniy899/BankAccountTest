package ml.arseniy899.bankaccounttest.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson

import com.google.gson.JsonObject
import com.google.gson.JsonArray
import ml.arseniy899.bankaccounttest.data.Valutes


class CurrencyRepositoryImpl(context: Context) : CurrencyRepository
{
	private val mApiService: CurrencyAccountService
	
	companion object
	{
		val BASE_URL = "https://www.cbr-xml-daily.ru/"
		val currLiveData : MutableLiveData<CurrencyApiResponse>? = null
	}
	
	init
	{
		val retrofit =
			Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
				.build()
		mApiService = retrofit.create<CurrencyAccountService>(CurrencyAccountService::class.java)
	}
	
	var memoryWork : MemoryWork = MemoryWork(context)
	override fun getCurency(): LiveData<CurrencyApiResponse>
	{
		val liveData = MutableLiveData<CurrencyApiResponse>()
		if(currLiveData?.value != null)
			liveData.value = currLiveData.value
		else
		{
			val liveDataMem = memoryWork.loadObj(CurrencyApiResponse::class.java,"cache-currencies")
			if(liveDataMem != null)
				liveData.value = liveDataMem
		}
		val call = mApiService.getCurrency()
		call.enqueue(object : Callback<Valutes>
		{
			override fun onResponse(call: Call<Valutes>, response: Response<Valutes>)
			{
				
				liveData.value = response.body()?.let { CurrencyApiResponse(it) }
//				liveData.value = response.body()?.get("Valute")?.asJsonObject?.let { CurrencyApiResponse(it) }
				if(liveData.value != null)
					memoryWork.saveObj("cache-currencies", liveData.value!!)
			}
			
			override fun onFailure(call: Call<Valutes>, t: Throwable)
			{
				val liveDataMem = memoryWork.loadObj(CurrencyApiResponse::class.java,"cache-currencies")
				if(liveDataMem?.valutes == null)
					liveData.value = CurrencyApiResponse(t)
				else
					liveData.value = CurrencyApiResponse(liveDataMem.valutes!!, t)
				liveData.value?.valutes = liveDataMem?.valutes
				if(liveData.value != null)
					memoryWork.saveObj("cache-currencies", liveData.value!!)
			}
		})
		return liveData
	}
	
	
}