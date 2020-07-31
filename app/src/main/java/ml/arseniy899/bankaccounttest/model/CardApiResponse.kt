package ml.arseniy899.bankaccounttest.model

import ml.arseniy899.bankaccounttest.data.DataCard

class CardApiResponse
{
	var card: DataCard? = null
	var error: Throwable? = null
	
	constructor(card: DataCard)
	{
		this.card = card
		this.error = null
	}
	
	constructor(error: Throwable)
	{
		this.error = error
		this.card = null
	}
}