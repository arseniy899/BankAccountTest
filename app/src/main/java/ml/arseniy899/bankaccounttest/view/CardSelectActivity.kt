package ml.arseniy899.bankaccounttest.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_card_select.*
import kotlinx.android.synthetic.main.item_card_select.view.*
import ml.arseniy899.bankaccounttest.R
import ml.arseniy899.bankaccounttest.data.User

class CardSelectActivity : AppCompatActivity()
{
	var curentCard : User? = null
	var curentCardList : ArrayList<User>? = null
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_card_select)
		curentCard = intent.extras?.get("current-card") as User
		curentCardList = intent.extras?.get("list-card") as ArrayList<User>?
		
		
		setupView()
	}
	fun setupView()
	{
		
		val activity = this;
		finish.setOnClickListener { finish()}
		cardSelectList.apply {
			layoutManager = LinearLayoutManager(activity)
			if(curentCard != null && curentCardList != null)
				adapter =
					SelectCardListAdapter(
						curentCard!!,
						curentCardList!!,
						activity)
		}
		
	}
	class SelectCardViewHolder(var curentCard : User, inflater: LayoutInflater, parent: ViewGroup,
							   var activity: Activity) :
		RecyclerView.ViewHolder(inflater.inflate(R.layout.item_card_select, parent, false))
	{
		private var cardSelectIV: ImageView? = null
		private var cardSelectTV: TextView? = null
		private var cardSelectIsSelIV: View? = null
		
		
		init {
			cardSelectIV = itemView.cardSelectIV
			cardSelectTV = itemView.cardSelectTV
			cardSelectIsSelIV = itemView.cardSelectIsSelIV
		}
		
		fun bind(card : User) {
			cardSelectTV?.text = card.cardNumber
			cardSelectIV?.setImageResource(card.icon)
			if(curentCard.cardNumber == card.cardNumber)
				cardSelectIsSelIV?.visibility = View.VISIBLE
			else
				cardSelectIsSelIV?.visibility = View.GONE
			itemView.setOnClickListener {
				var resIntent = Intent();
				resIntent.putExtra("card", card)
				activity.setResult(Activity.RESULT_OK,resIntent)
				activity.finish()
			}
		}
		
	}
	class SelectCardListAdapter(var curentCard : User, var list : ArrayList<User>,var activity: Activity)
		: RecyclerView.Adapter<SelectCardViewHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCardViewHolder
		{
			val inflater = LayoutInflater.from(parent.context)
			return SelectCardViewHolder(
				curentCard,
				inflater,
				parent,
				activity)
		}
		
		override fun onBindViewHolder(holder: SelectCardViewHolder, position: Int) {
			val card = list.get(position)
			holder.bind(card)
		}
		
		override fun getItemCount(): Int{
			return list.size
		}
		
	}
}
