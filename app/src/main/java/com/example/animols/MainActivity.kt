package com.example.animols

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.animols.ui.theme.AnimolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimolsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // ✅ Only call composables inside setContent {}
                    AnimalSearchScreen()
                }
            }
        }
    }
}
