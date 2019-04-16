package net.mbonnin.androidqreader

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {

    var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = GlobalScope.launch(Dispatchers.Main) {
            val lines = mutableListOf<String>()

            val inputStream = contentResolver.openInputStream(Uri.parse("content://net.mbonnin.androidqwriter.fileprovider/log.txt"))
                .bufferedReader()
            val textView = findViewById<TextView>(R.id.textView)
            var i = 0
            while (true) {
                val l = inputStream.readLine()
                if (l != null) {
                    lines.add(l)
                }
                if (lines.size > 20) {
                    lines.removeAt(0)
                }
                textView.text = lines.joinToString("\n")

                delay(100)
                i++
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}
