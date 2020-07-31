package ml.arseniy899.bankaccounttest.view

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.R.attr.maxHeight
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.AttributeSet
import android.view.View


class MaxHeightRecyclerView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0
									  ) : RecyclerView(context, attrs, defStyle)
{
	var maxHeight = (context.resources.displayMetrics.density * 180).toInt();
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
	{
		var heightMeasureSpec = heightMeasureSpec
		if (maxHeight > 0)
		{
			val hSize = MeasureSpec.getSize(heightMeasureSpec)
			val hMode = MeasureSpec.getMode(heightMeasureSpec)
			
			when (hMode)
			{
				MeasureSpec.AT_MOST -> heightMeasureSpec = MeasureSpec.makeMeasureSpec(
					Math.min(hSize, maxHeight),
					MeasureSpec.AT_MOST)
				MeasureSpec.UNSPECIFIED -> heightMeasureSpec =
					MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
				MeasureSpec.EXACTLY -> heightMeasureSpec = MeasureSpec.makeMeasureSpec(
					Math.min(hSize, maxHeight),
					MeasureSpec.EXACTLY)
			}
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
	}
}