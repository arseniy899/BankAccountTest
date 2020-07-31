package ml.arseniy899.bankaccounttest.model

class Event
{
	var status : Status = Status.READY
	constructor(status: Status)
	{
		this.status = status
	}
}

enum class Status
{
	SUCCESS, ERROR, LOADING, READY
}