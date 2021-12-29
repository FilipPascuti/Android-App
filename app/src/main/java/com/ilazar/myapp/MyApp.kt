package com.ilazar.myapp

import android.content.Context
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ilazar.myapp.theme.MyAppTheme
import com.ilazar.myapp.todo.items.ItemsViewModel

@Composable
fun MyApp(itemsViewModel: ItemsViewModel) {
    MyAppTheme {
        Surface(color = MaterialTheme.colors.background) {
            MyAppNavGraph(itemsViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
//    MyApp()
}