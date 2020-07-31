package ml.arseniy899.bankaccounttest.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_currency_selector.view.*
import ml.arseniy899.bankaccounttest.R
import ml.arseniy899.bankaccounttest.viewModel.UserAccountViewModel

class CurrencyChangeViewHolder(var viewModel : UserAccountViewModel, var inflater: LayoutInflater, var parent: ViewGroup) :
	RecyclerView.ViewHolder(inflater.inflate(R.layout.item_currency_selector, parent, false)) {
	private var currencySymb: TextView? = null
	private var currencyName: TextView? = null
	private var backgroundV: View? = null
	
	
	init {
		currencySymb = itemView.currencySymbTV
		currencyName = itemView.currencyNameTV
		backgroundV = itemView.backgroundV
		
	}
	
	fun bind(name : String) {
		currencySymb?.text = viewModel.getCurrencySymbol(name)
		currencyName?.text = name
		if(name == viewModel.currentCurrency)
		{
			currencySymb?.setTextColor(ContextCompat.getColor(inflater.context,
				R.color.colorForeground))
			currencyName?.setTextColor(ContextCompat.getColor(inflater.context,
				R.color.colorForeground))
			backgroundV?.setBackgroundResource(R.drawable.background_roundcorner_selected);
		}
		else
		{
			currencyName?.setTextColor(+R.color.colorPrimaryDark)
			currencySymb?.setTextColor(+R.color.colorPrimaryDark)
			backgroundV?.setBackgroundResource(R.drawable.background_roundcorner);
		}
		itemView.setOnClickListener { view ->
			viewModel.changeCurrentCurrency(name)
		}
	}
	
}
class CurrencyChangeAdapter(var viewModel : UserAccountViewModel) :
	RecyclerView.Adapter<CurrencyChangeViewHolder>()
{
	var isWidthCalculated = false
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyChangeViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		if (parent.width == 0)
		{
			parent.viewTreeObserver.addOnGlobalLayoutListener {
				if (!isWidthCalculated)
				{
					notifyDataSetChanged()
					isWidthCalculated = true
				}
			}
		}
		return CurrencyChangeViewHolder(viewModel, inflater, parent)
	}
	var standartWidth = 0
	override fun onBindViewHolder(holder: CurrencyChangeViewHolder, position: Int)
	{
		
		val params = holder.itemView.layoutParams
		params.width = holder.parent.width /3
		/*if (itemCount < 4)
		{
			if(standartWidth == 0)
				standartWidth = params.width
			params.width = holder.parent.width /itemCount
//			params.width = width /itemCount
//			holder.itemView.layoutParams = params
		}
		else
		{
			params.width = (holder.itemView.resources.displayMetrics.density *114).toInt()
		}*/
		holder.bind(viewModel.currencyListSelectable[position])
	}
	
	override fun getItemCount(): Int{
		return viewModel.currencyListSelectable.size
	}
	
}