package ml.arseniy899.bankaccounttest.viewModel

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

open class ViewInterface : AppCompatActivity()
{
	
	open fun onInvalidate()
	{
	}
	
	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?)
	{
		super.onCreate(savedInstanceState, persistentState)
		onInvalidate()
	}
	
}