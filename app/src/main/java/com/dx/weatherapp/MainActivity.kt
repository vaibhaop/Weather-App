package com.dx.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.dx.weatherapp.util.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var Temp: TextView
    private lateinit var MaxTemp: TextView
    private lateinit var MinTemp: TextView
    private lateinit var Condition: TextView
    private lateinit var Condition2: TextView
    private lateinit var Humidity: TextView
    private lateinit var WindSpeed: TextView
    private lateinit var Sunset: TextView
    private lateinit var Sunrise: TextView
    private lateinit var SeaLevel: TextView
    private lateinit var Day: TextView
    private lateinit var Date: TextView
    private lateinit var CityName: TextView
    private lateinit var SearchCity: SearchView
    private lateinit var Root: ConstraintLayout
    private lateinit var Animation: LottieAnimationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Temp = findViewById(R.id.temp)
        MaxTemp = findViewById(R.id.maxtemp)
        MinTemp = findViewById(R.id.mintemp)
        Condition = findViewById(R.id.weather)
        Condition2 = findViewById(R.id.condition)
        Humidity = findViewById(R.id.humidity)
        WindSpeed = findViewById(R.id.windspeed)
        Sunset = findViewById(R.id.sunset)
        Sunrise = findViewById(R.id.sunrise)
        SeaLevel = findViewById(R.id.sea)
        Date = findViewById(R.id.date)
        Day = findViewById(R.id.day)
        CityName = findViewById(R.id.cityname)
        SearchCity = findViewById(R.id.searchView)
        Root = findViewById(R.id.root)
        Animation = findViewById(R.id.lottieAnimationView)


        searchCity()
//        fetchWeatherData("Nagpur")

        Day.text = dayName()
        Date.text = currentDate()


    }

    private fun searchCity() {
        SearchCity.requestFocus()
        SearchCity.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getWeatherData(
                    cityName,
                    "285654e97a66bcfa11e4c232441a9264",
                    "metric"
                )
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "App error $e", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(this@MainActivity, "Http error $e", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {

                    val tempreture = response.body()!!.main.temp.toString()
                    val mintempreture = response.body()!!.main.temp_min
                    val maxtempreture = response.body()!!.main.temp_max
                    val humidity = response.body()!!.main.humidity
                    val wind = response.body()!!.wind.speed
                    val condition = response.body()!!.weather.firstOrNull()?.main ?: "unknown"
                    val sunrise = response.body()!!.sys.sunrise.toLong()
                    val sunset = response.body()!!.sys.sunset.toLong()
                    val sea = response.body()!!.main.pressure




                    Temp.text = "${tempreture} °C"
                    MinTemp.text = "Min Temp:${mintempreture}°C"
                    MaxTemp.text = "Max Temp:${maxtempreture}°C"
                    Condition.text = condition
                    Condition2.text = condition
                    Humidity.text = "$humidity %"
                    WindSpeed.text = "$wind m/s"
                    Sunrise.text = "${time(sunrise)}"
                    Sunset.text = "${time(sunset)}"
                    SeaLevel.text = "$sea hPa"
                    CityName.text = "$cityName"


                    changeImageAccordingToCondition(condition)


                }
            }
        }
    }

    private fun changeImageAccordingToCondition(condition: String) {

        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                Root.setBackgroundResource(R.drawable.sunny_background)
                Animation.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Haze", "Foggy" -> {
                Root.setBackgroundResource(R.drawable.colud_background)
                Animation.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                Root.setBackgroundResource(R.drawable.rain_background)
                Animation.setAnimation(R.raw.rain)
            }
            "Light Snow", "Blizzard", "Moderate Snow", "Heavy Snow" -> {
                Root.setBackgroundResource(R.drawable.snow_background)
                Animation.setAnimation(R.raw.snow)
            }
            else ->{
                Root.setBackgroundResource(R.drawable.sunny_background)
                Animation.setAnimation(R.raw.sun)
            }
        }
        Animation.playAnimation()

    }

    private fun currentDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun dayName(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

}