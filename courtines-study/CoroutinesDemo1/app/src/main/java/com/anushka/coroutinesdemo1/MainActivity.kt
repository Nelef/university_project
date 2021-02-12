package com.anushka.coroutinesdemo1

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Main).launch {
            Log.i("MyTag", "CoroutineScope Start")
            val stock1 = async(IO) { getStock1() }
            val stock2 = async(IO) { getStock2() }
            val total = stock1.await() + stock2.await()
            Log.i("MyTag", "TOTAL -> $total")
            Toast.makeText(this@MainActivity, "Totail is $total", Toast.LENGTH_SHORT).show()
        }

        btnCount.setOnClickListener {
            tvCount.text = count++.toString()
        }
        btnDownloadUserData.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                downloadUserData()
            }
        }
    }

    private suspend fun downloadUserData() {
        withContext(Dispatchers.Main) {
            for (i in 1..200000) {
                tvUserMessage.text = i.toString() + " in " + Thread.currentThread().name
                delay(1000)
            }
        }
    }
}

private suspend fun getStock1(): Int {
    delay(10000)
    Log.i("MyTag", "sotck 1 returned")
    return 55000
}

private suspend fun getStock2(): Int {
    delay(8000)
    Log.i("MyTag", "sotck 2 returned")
    return 35000
}