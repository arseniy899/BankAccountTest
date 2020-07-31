package ml.arseniy899.bankaccounttest.viewModel

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import com.bumptech.glide.Glide
import ml.arseniy899.bankaccounttest.view.CardSelectActivity
import ml.arseniy899.bankaccounttest.data.TransactionHistory
import ml.arseniy899.bankaccounttest.data.User
import ml.arseniy899.bankaccounttest.model.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.*
import ml.arseniy899.bankaccounttest.R

class  UserAccountViewModelFactory(var activity : Activity) :
	ViewModelProvider.Factory
{
	
	
	override fun <T : ViewModel> create(modelClass: Class<T>): T
	{
		return UserAccountViewModel(activity) as T
	}
}
class UserAccountViewModel(var activity : Activity) : ViewModel(), Serializable
{
	private val mCardApiResponse: MediatorLiveData<CardApiResponse>
	private val mCurrencyApiResponse : MediatorLiveData<CurrencyApiResponse>
	private val mCard: MediatorLiveData<User>
	private val mCardRepository: CardRepository    // No argument constructor
	private val mCurrencyRepository: CurrencyRepository    // No argument constructor
	
	val refreshStatus : MutableLiveData<Event>
	
	val cardApiResponse: LiveData<CardApiResponse>
		get() = mCardApiResponse
	val card : LiveData<User>
		get() = mCard
	val currencyApiResponse : LiveData<CurrencyApiResponse>
		get() = mCurrencyApiResponse
	var currentDataCardUser : User? = null;
	var availableCards : ArrayList<User>? = null;
	
	var currentCurrency = "GBP"
	var currencyListSelectable : ArrayList<String> = ArrayList(listOf("GBP", "RUB", "EUR"))
		set(value)
		{
			field = value
			memoryWork.saveObjects("selected-cur-set", value.toList() as ArrayList<Any>)
		}
//	val currencyListSelectable = listOf("GBP");
	var memoryWork : MemoryWork = MemoryWork(activity)
	init
	{
		mCardApiResponse = MediatorLiveData()
		mCard = MediatorLiveData()
		mCurrencyApiResponse = MediatorLiveData()
		mCardRepository = CardRepositoryImpl(activity.applicationContext)
		mCurrencyRepository = CurrencyRepositoryImpl(activity.applicationContext)
		val newCurrency = memoryWork.loadString("def-currency") ?: ""
		if(newCurrency.isNotEmpty())
			currentCurrency = newCurrency
		var memCurrencyListSelectable = memoryWork.loadObjects(String::class.java,"selected-cur-set")
		
		if (memCurrencyListSelectable.size > 0)
		{
			currencyListSelectable = memCurrencyListSelectable
		}
		
		refreshStatus = MutableLiveData<Event>()
	}
	
	fun loadCards(): LiveData<CardApiResponse>
	{
		refreshStatus.value = Event(Status.LOADING)
		mCardApiResponse.addSource(mCardRepository.getCards()) { apiResponse ->
			mCardApiResponse.setValue(apiResponse)
			if(apiResponse.error != null)
				refreshStatus.value = Event(Status.ERROR)
			else
				refreshStatus.value = Event(Status.SUCCESS)
		}
		return mCardApiResponse
	}
	fun convertCurrency(from : String, to : String, money: Double) : Double
	{
		if(mCurrencyApiResponse.value == null)
			return 0.0
		val valutes  = (mCurrencyApiResponse.value as CurrencyApiResponse).valutes
		val rubs = (valutes?.get(from)?.value ?: 1.0) * money
		if(to != "RUB")
		{
			val newCur = rubs / (valutes?.get(to)?.value ?: 1.0)
			return newCur
		}
		else
			return rubs
	}
	fun changeCurrentCurrency(currency : String)
	{
		val newCurIndex = currencyListSelectable.indexOf(currency)
		if(newCurIndex >= 3)
		{
			currencyListSelectable.removeAt(newCurIndex)
			currencyListSelectable.add(0,currency)
		}
		currencyListSelectable = currencyListSelectable
		currentCurrency = currency
		processDataCardsForUI()
		memoryWork.writeStr("def-currency", currency)
		mCard.value = currentDataCardUser
	}
	fun getCurrencySymbol(currency: String) : String
	{
		var res = ""
		when(currency)
		{
			"RUB" -> res = "\u20BD"
			"GPB" -> res = "£"
			else -> res = Currency.getInstance(currency).symbol
			
		}
		return res
	}
	fun formatMoneyWithCur(currency: String, money : Double) : String
	{
		val formatter = NumberFormat.getCurrencyInstance(Locale("ru")) as DecimalFormat
		val symbols = formatter.getDecimalFormatSymbols()
		symbols.setCurrencySymbol("") // Don't use null.
		formatter.setDecimalFormatSymbols(symbols)
		return getCurrencySymbol(currency)+" " + formatter.format(money)
	}
	fun getIconForTransaction(transaction: TransactionHistory, imageView: ImageView)
	{
		Glide.with(imageView).load(transaction.iconUrl).into(imageView);
	}
	fun processDataCardsForUI()
	{
		
		if (availableCards != null)
		{
			for (card in availableCards!!)
			{
				
				card.balanceUSD = formatMoneyWithCur("USD", card.balance)
				card.balance2 = formatMoneyWithCur(currentCurrency,
					convertCurrency("USD", currentCurrency, card.balance))
				
				
				when (card.type)
				{
					"mastercard" -> card.icon = R.drawable.bb_card_mastercard
					"visa" -> card.icon = R.drawable.bb_card_visa
					"unionpay" -> card.icon = R.drawable.bb_card_unionpay
				}
				if(card.cardNumber == currentDataCardUser?.cardNumber)
					currentDataCardUser = card
			}
			
		}
		
	}
	fun getTransactionFromHistory() : List<TransactionHistory>?
	{
		return currentDataCardUser?.transactionHistory
	}
	fun getDefaultCard() : User?
	{
		if(availableCards == null)
			return null
		val defCardNumb = memoryWork.loadString("def-card-numb")
		
		if (defCardNumb.isNotEmpty())
		{
			for (card in availableCards!!)
			{
				if(defCardNumb == card.cardNumber)
					return card
			}
		}
		else
		{
			return availableCards!![0]
		}
		return null
	}
	
	fun setNewDefaultCard(card : User)
	{
		currentDataCardUser = card
		loadCurrentCard()
		memoryWork.writeStr("def-card-numb", card.cardNumber)
		mCard.value = currentDataCardUser
	}
	fun loadCurrentCard(): LiveData<CardApiResponse>
	{
		refreshStatus.value = Event(Status.LOADING)
		mCardApiResponse.addSource(mCurrencyRepository.getCurency()) { apiCurrResponse ->
			mCurrencyApiResponse.value = apiCurrResponse
			processDataCardsForUI()
		}
		mCardApiResponse.addSource(mCardRepository.getCards()) { apiResponse ->
			mCardApiResponse.value = apiResponse
			if (apiResponse.error == null)
			{
				refreshStatus.value = Event(Status.SUCCESS)
				apiResponse.card?.let {
					availableCards = it.users as ArrayList<User>?
					if (currentDataCardUser == null)
					{
						getDefaultCard().let { it1 ->
							currentDataCardUser = it1;
						}
					}
					processDataCardsForUI()
					mCard.value = currentDataCardUser
				}
			}
			else
				refreshStatus.value = Event(Status.ERROR)
		}
		return mCardApiResponse
	}
	fun cardViewClick()
	{
		val intent = Intent(activity, CardSelectActivity::class.java)
		intent.putExtra("current-card",currentDataCardUser)
		intent.putExtra("list-card",availableCards)
		activity.startActivityForResult(intent, 150)
	}
	
	
}