package ml.arseniy899.bankaccounttest.model

import androidx.lifecycle.LiveData


interface CurrencyRepository
{
	fun getCurency(): LiveData<CurrencyApiResponse>
}