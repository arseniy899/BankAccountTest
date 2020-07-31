package ml.arseniy899.bankaccounttest.view

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_card_history.view.*
import ml.arseniy899.bankaccounttest.R
import ml.arseniy899.bankaccounttest.data.TransactionHistory
import ml.arseniy899.bankaccounttest.data.User
import ml.arseniy899.bankaccounttest.model.Status
import ml.arseniy899.bankaccounttest.viewModel.NetworkStateReceiver
import ml.arseniy899.bankaccounttest.viewModel.UserAccountViewModel
import ml.arseniy899.bankaccounttest.viewModel.UserAccountViewModelFactory


class MainActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener
{
	
	val mViewModel: UserAccountViewModel by lazy {
			ViewModelProviders.of(this, UserAccountViewModelFactory(this))
				.get(UserAccountViewModel::class.java)
	}
	var noInternetSnackbar : Snackbar? = null
	var loadingSnackbar : Snackbar? = null
	private var networkStateReceiver: NetworkStateReceiver? = null
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setupView()
		historyLay.visibility = View.GONE
		currencyChangeLay.visibility = View.GONE
		mViewModel.cardApiResponse.observe(this, Observer { apiResponse ->
			
			if (apiResponse.error != null)
			{
				Log.e("MainAct/cardLoad", apiResponse.error?.localizedMessage)
				
				if(apiResponse.error?.localizedMessage?.contains("Unable to resolve host")
						?: false)
					noInternetSnackbar?.show()
				else
					Toast.makeText(baseContext, getString(R.string.loading_error), Toast.LENGTH_SHORT).show()
			}
			else
				noInternetSnackbar?.dismiss()
		})
		mViewModel.card.observe(this, Observer { card ->
			invalidate(card)
		})
		
		mViewModel.refreshStatus.observe(this, Observer {
			when(it.status)
			{
				Status.LOADING -> {
					startAnimations()
					noInternetSnackbar?.dismiss()
					loadingSnackbar?.show()
				}
				Status.SUCCESS -> {
					loadingSnackbar?.dismiss()
					shimmer.cancel()
				}
				Status.ERROR -> {
					loadingSnackbar?.dismiss()
				}
			}
		})
		mViewModel.loadCurrentCard()
		
		
		
		networkStateReceiver = NetworkStateReceiver()
		networkStateReceiver?.addListener(this)
		this.registerReceiver(networkStateReceiver,
			IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION))
		
	}
	var shimmer = Shimmer()
	private fun invalidate(card: User)
	{
		historyLay.visibility = View.VISIBLE
		currencyChangeLay.visibility = View.VISIBLE
		cardNumberTV.text = card.cardNumber
		cardHolderNameTV.text = card.cardholderName
		cardBalanceUSDTV.text = card.balanceUSD
		cardBalanceCurTV.text = card.balance2
		cardValidTV.text = card.valid
		cardIconIV?.setImageResource(card.icon)
		historyList.adapter?.notifyDataSetChanged()
		currencyList.adapter?.notifyDataSetChanged()
		historyList.smoothScrollToPosition(0)
		currencyList.smoothScrollToPosition(0)
	}
	
	fun setupView()
	{
		val activity = this;
		
		historyList.apply {
			layoutManager = LinearLayoutManager(activity)
			adapter = HistoryListAdapter(mViewModel)
		}
		currencyList.apply {
			layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
			adapter = CurrencyChangeAdapter( mViewModel)
			
		}
		cardView.setOnClickListener { view ->
			mViewModel.cardViewClick()
		}
		noInternetSnackbar = Snackbar.make(currencyList,
			getString(R.string.loading_no_internet), Snackbar.LENGTH_INDEFINITE)
		loadingSnackbar = Snackbar.make(historyList,
			getString(R.string.loading), Snackbar.LENGTH_INDEFINITE)
		
		currencySetChange.setOnClickListener{
			var keys = mViewModel.currencyApiResponse.value?.valutes?.keys
			if (keys != null)
			{
				val multiItems = ArrayList<String>(keys)
				val multiItemsShow = ArrayList<String>()
				multiItems.add("RUB")
				val checkedItems = BooleanArray(multiItems.size)
				multiItems.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it }))
				var newSelectedItems = ArrayList<String>()
				for(i in 0 until multiItems.size)
				{
					var item = multiItems[i]
					if(mViewModel.currencyListSelectable.contains(item))
					{
						checkedItems[i] = true
						newSelectedItems.add(item)
					}
					multiItemsShow.add(mViewModel.getCurrencySymbol(item)+
							" ($item)")
				}
				MaterialAlertDialogBuilder(activity)
					.setTitle(getString(R.string.setup_fav_cur))
					.setMultiChoiceItems(multiItemsShow.toTypedArray(), checkedItems)
					{
						dialog, which, checked ->
						Log.d("MainAct/CurSetCh", "which: $which; checked: $checked")
						if(checked)
							newSelectedItems.add(multiItems[which])
						else
							newSelectedItems.remove(multiItems[which])
					}
					.setPositiveButton(getString(R.string.save))
					{
						dialogInterface: DialogInterface, i: Int ->
						if(newSelectedItems.size > 0)
							mViewModel.currencyListSelectable = newSelectedItems
						else
							mViewModel.currencyListSelectable = ArrayList(listOf("GBP", "RUB", "EUR"))
						if(!mViewModel.currencyListSelectable.contains(mViewModel.currentCurrency))
							mViewModel.changeCurrentCurrency(mViewModel.currencyListSelectable[0])
						currencyList.invalidate()
						currencyList.adapter?.notifyDataSetChanged()
					}
					.setNegativeButton(getString(R.string.cancel))
						{ dialogInterface: DialogInterface, i: Int -> }.show()
			}
		}
	}
	fun startAnimations()
	{
		shimmer.start(cardNumberTV)
		shimmer.start(cardHolderNameTV)
		shimmer.start(cardBalanceUSDTV)
		shimmer.start(cardValidTV)
		shimmer.start(cardBalanceCurTV)
		
	}
	
	override fun onDestroy()
	{
		super.onDestroy()
		networkStateReceiver?.removeListener(this)
		this.unregisterReceiver(networkStateReceiver)
	}
	var wasNetworkLost = false
	override fun networkAvailable()
	{
		if(wasNetworkLost)
		{
			mViewModel.loadCurrentCard()
			wasNetworkLost = false
		}
	}
	
	override fun networkUnavailable()
	{
		wasNetworkLost = true
		noInternetSnackbar?.show()
	}
	
	// History recyclerView
	
	class HistoryViewHolder(var viewModel : UserAccountViewModel, inflater: LayoutInflater, var parent: ViewGroup) :
		RecyclerView.ViewHolder(inflater.inflate(R.layout.item_card_history, parent, false)) {
		private var historyTitleTV: TextView? = null
		private var historyDateTV: TextView? = null
		private var historyPriceCurTV: TextView? = null
		private var historyPriceUSDTV: TextView? = null
		private var historyIV: ImageView? = null
		
		
		init {
			historyTitleTV = itemView.historyTitleTV
			historyDateTV = itemView.historyDateTV
			historyPriceCurTV = itemView.historyPriceCurTV
			historyPriceUSDTV = itemView.historyPriceUSDTV
			historyIV = itemView.historyIV
			
		}
		
		fun bind(transaction: TransactionHistory) {
			historyTitleTV?.text = transaction.title
			historyDateTV?.text = transaction.date
			historyPriceUSDTV?.text = viewModel.formatMoneyWithCur("USD",
				transaction.amount ?: 0.0)
			historyPriceCurTV?.text = viewModel.formatMoneyWithCur(viewModel.currentCurrency,
				viewModel.convertCurrency("USD",viewModel.currentCurrency, transaction.amount ?: 0.0))
			historyIV?.let { viewModel.getIconForTransaction(transaction, it) }
		}
		
	}
	
	class HistoryListAdapter(var viewModel : UserAccountViewModel) : RecyclerView.Adapter<HistoryViewHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
			val inflater = LayoutInflater.from(parent.context)
			return HistoryViewHolder(viewModel,inflater, parent)
		}
		
		override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
			val transaction = viewModel.getTransactionFromHistory()?.get(position)
			
			if (transaction != null)
			{
				holder.bind(transaction)
			}
		}
		
		override fun getItemCount(): Int{
			return viewModel.getTransactionFromHistory()?.size ?: 0
		}
		
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		super.onActivityResult(requestCode, resultCode, data)
		if(requestCode == 150 && resultCode == Activity.RESULT_OK)
		{
			val card = data?.extras?.get("card") as User?
			if (card != null)
			{
				mViewModel.setNewDefaultCard(card)
			}
		}
	}
}