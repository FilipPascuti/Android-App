package com.ilazar.myapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.ilazar.myapp.auth.LoginViewModel
import com.ilazar.myapp.todo.items.ItemsViewModel

class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        val itemsViewModel = ViewModelProvider(this).get(ItemsViewModel::class.java)

        Log.d("main app", "-------------------------- items view model: ${itemsViewModel.items.value}");

        super.onCreate(savedInstanceState)
        setContent {
            MyApp(itemsViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
//    MyApp()
}