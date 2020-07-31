package ml.arseniy899.bankaccounttest.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ml.arseniy899.bankaccounttest.data.DataCard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CardRepositoryImpl(context: Context) : CardRepository
{
	
	private val mApiService: UserAccountService
	
	companion object
	{
		val BASE_URL = "https://hr.peterpartner.net/test/android/v1/"
		var cardsLiveData : MutableLiveData<CardApiResponse>? = null
		
	}
	var memoryWork : MemoryWork = MemoryWork(context)
	init
	{
		val retrofit =
			Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
				.build()
		mApiService = retrofit.create<UserAccountService>(UserAccountService::class.java)
		
	}
	
	override fun getCards(): LiveData<CardApiResponse>
	{
		val liveData = MutableLiveData<CardApiResponse>()
		if(cardsLiveData?.value != null)
			liveData.value = cardsLiveData?.value
		else
		{
			val liveDataMem = memoryWork.loadObj(CardApiResponse::class.java,"cache-cards")
			if(liveDataMem != null)
				liveData.value = liveDataMem
		}
		val call = mApiService.getCards()
		
		call.enqueue(object : Callback<DataCard>
		{
			override fun onResponse(call: Call<DataCard>, response: Response<DataCard>)
			{
				cardsLiveData = liveData
				liveData.value = response.body()?.let { CardApiResponse(it) }
				if(liveData.value != null)
					memoryWork.saveObj("cache-cards", liveData.value!!)
			}
			
			override fun onFailure(call: Call<DataCard>, t: Throwable)
			{
				liveData.value = CardApiResponse(t)
			}
		})
		return liveData
	}
	
	
}