package ml.arseniy899.bankaccounttest.model
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.gson.JsonSyntaxException
import com.google.gson.JsonElement
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import android.icu.lang.UCharacter.GraphemeClusterBreak.T








class MemoryWork(context: Context)
{
	internal var context: Context
	internal var sharedPref: SharedPreferences
	
	val dumpOfEntries: String
		get()
		{
			val keys = this.sharedPref.all
			var ret = ""
			for ((key, value) in keys) ret += "$key=$value\n"
			return ret
		}
	
	init
	{
		this.context = context.getApplicationContext()
		this.sharedPref = context.getSharedPreferences("main", MODE_PRIVATE)
		
	}
	
	fun writeInt(key: String, value: Int)
	{
		val editor = this.sharedPref.edit()
		editor.putInt(key, value)
		editor.commit()
		Log.d("MemoryWorkerSave", "$key=$value")
	}
	
	fun writeStr(key: String, value: String)
	{
		val editor = this.sharedPref.edit()
		editor.putString(key, value)
		editor.commit()
		Log.d("MemoryWorkerSave", "$key=$value")
	}
	
	fun writeB(key: String, value: Boolean)
	{
		val editor = this.sharedPref.edit()
		editor.putBoolean(key, value)
		editor.commit()
		Log.d("MemoryWorkerSave", "$key=$value")
	}
	
	fun loadInt(key: String): Int
	{
		Log.d("MemoryWorkerLoad", key + "=" + this.sharedPref.getInt(key, 0))
		return this.sharedPref.getInt(key, 0)
	}
	
	fun loadString(key: String): String
	{
		Log.d("MemoryWorkerLoad", key + "=" + this.sharedPref.getString(key, ""))
		return this.sharedPref.getString(key, "").toString()
	}
	
	fun loadB(key: String): Boolean
	{
		if (key != "debug-on") Log.d("MemoryWorkerLoad",
			key + "=" + this.sharedPref.getBoolean(key, false))
		return this.sharedPref.getBoolean(key, false)
	}
	
	fun loadBool(key: String): Boolean
	{
		return this.loadB(key)
	}
	
	fun <T> loadObj(TClass: Class<T>, key: String): T?
	{
		val gson = Gson()
		val text = loadString(key)
		val ret = gson.fromJson(text, TClass)
		return ret as T
	}
	
	fun <T> loadObject(TClass: Class<T>, key: String): T?
	{
		return loadObj(TClass, key)
	}
	
	fun <T> loadObject(context: Context, TClass: Class<T>, key: String): T?
	{
		val gson = Gson()
		val text = loadString(context, key)
		val ret = gson.fromJson(text, TClass)
		return ret as T
	}
	
	fun saveObj(key: String, `object`: Any)
	{
		val gson = Gson()
		val text = gson.toJson(`object`)
		writeStr(key, text)
	}
	
	fun saveObject(key: String, `object`: Any)
	{
		saveObj(key, `object`)
	}
	
	fun saveObject(context: Context, key: String, `object`: Any)
	{
		val gson = Gson()
		val text = gson.toJson(`object`)
		writeStr(context, key, text)
	}
	
	fun clearAll()
	{
		var editor = this.sharedPref.edit()
		editor = editor.clear()
		editor.apply()
	}
	fun <T> saveObjects(name: String,objects: ArrayList<T>): ArrayList<T>
	{
		val gson = Gson()
		val objectsN = ArrayList<T>()
		objectsN.addAll(objects)
		writeStr(name, gson.toJson(objectsN))
		
		return objectsN
	}
	fun <T> loadObjects(clazz: Class<T>, key: String): ArrayList<T>
	{
		Log.i("loadObjects", "STARTED")
		val gson = Gson()
		// Consuming remote method
		val lst = ArrayList<T>()
		val parser = JsonParser()
		var strJson = loadString(key)
		if (parser.parse(strJson) == null || !parser.parse(strJson).isJsonArray)
		{
			return lst
		}
		val array = parser.parse(strJson).asJsonArray
		
		
		for (json in array)
		{
			try
			{
				val entity = gson.fromJson(json, clazz)
				lst.add(entity)
			} catch (e: JsonSyntaxException)
			{
				Log.e("MemoryWorke/loadObjects", "ParseError: class=$clazz;value=$json")
			}
			
		}
		
		
		return lst
	}
	companion object
	{
		
		internal fun writeInt(context: Context, key: String, value: Int)
		{
			val sharedPref = context.getSharedPreferences("main", MODE_PRIVATE)
			val editor = sharedPref.edit()
			editor.putInt(key, value)
			editor.commit()
			if (key != "debug-on") Log.d("MemoryWorkerSave", "$key=$value")
		}
		
		internal fun writeStr(context: Context, key: String, value: String)
		{
			val sharedPref = context.getSharedPreferences("main", MODE_PRIVATE)
			val editor = sharedPref.edit()
			editor.putString(key, value)
			editor.commit()
			if (key != "debug-on") Log.d("MemoryWorkerSave", "$key=$value")
		}
		
		internal fun writeB(context: Context, key: String, value: Boolean)
		{
			val sharedPref = context.getSharedPreferences("main", MODE_PRIVATE)
			val editor = sharedPref.edit()
			editor.putBoolean(key, value)
			editor.commit()
			if (key != "debug-on") Log.d("MemoryWorkerSave", "$key=$value")
		}
		
		internal fun loadInt(context: Context, key: String): Int
		{
			val sharedPref = context.getSharedPreferences("main", MODE_PRIVATE)
			if (key != "debug-on") Log.d("MemoryWorkerLoad",
				key + "=" + sharedPref.getInt(key, 0))
			return sharedPref.getInt(key, 0)
		}
		
		internal fun loadString(context: Context, key: String): String
		{
			val sharedPref = context.getSharedPreferences("main", MODE_PRIVATE)
			if (key != "debug-on") Log.d("MemoryWorkerLoad",
				key + "=" + sharedPref.getString(key, ""))
			return sharedPref.getString(key, "").toString()
		}
		
		internal fun loadB(context: Context, key: String): Boolean
		{
			val sharedPref = context.getSharedPreferences("main", MODE_PRIVATE)
			if (key != "debug-on") Log.d("MemoryWorkerLoad",
				key + "=" + sharedPref.getBoolean(key, false))
			return sharedPref.getBoolean(key, false)
		}
		
		fun clearCache(context: Context)
		{
			val sharedPref = context.getSharedPreferences("main", MODE_PRIVATE)
			val keys = sharedPref.getAll()
			for (entry in keys.entries)
			{
				if (entry.value.toString().length > 100) Log.d("ClearCache",
					"lentg=" + entry.key)
				if (entry.key.startsWith("/rs/") || entry.value.toString().length > 100) sharedPref.edit().remove(
					entry.key).apply()
				
			}
			sharedPref.edit().apply()
		}
	}
	
	
}