package ml.arseniy899.bankaccounttest.model

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.LiveData


interface CardRepository
{
	fun getCards(): LiveData<CardApiResponse>
	
}