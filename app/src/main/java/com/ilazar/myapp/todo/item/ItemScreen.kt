package com.ilazar.myapp.todo

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ilazar.myapp.R
import com.ilazar.myapp.todo.item.ItemViewModel
import java.util.*

val TAG = "ItemScreen"

@Composable
fun ItemScreen(navController: NavController, itemId: String? = null) {
    val itemViewModel = viewModel<ItemViewModel>()
    val item = itemViewModel.item.observeAsState().value

    if(itemId != null && item?._id == "") {
        Log.d(TAG, "the item id is $itemId")
        itemViewModel.loadItem(itemId = itemId)
    }

    val fetching = itemViewModel.fetching.observeAsState().value
    val fetchingError = itemViewModel.fetchingError.observeAsState().value
    val completed = itemViewModel.completed.observeAsState().value

    var submitStarted by remember { mutableStateOf(false) }
//    Log.d(TAG, "recompose $completed $submitStarted");
    if (completed != null) {
        if (submitStarted && completed) {
            Log.d(TAG, "navigate");
            submitStarted = false;
            navController.navigate("items")
        }
    }

//    text

    var itemText by remember { mutableStateOf("") }

    if (itemText == "" && item != null && item.text != ""){
        itemText = item.text
    }

//    /text

//    date
    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    var itemDate by remember { mutableStateOf("") }
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            itemDate = "$dayOfMonth/$month/$year"
        }, year, month, day
    )

    if(itemDate == "" && item != null && item.date != "" ) {
        itemDate = item.date
    }

//    /date

//    length

    var itemLength by remember { mutableStateOf("0") }

    if (itemLength == "0" && item != null && item.length != 0) {
        itemLength = item.length.toString()
    }

//    /length

//    liked

    var itemLiked by remember { mutableStateOf(false) }

    var initializedLiked = false

    if (item != null && !initializedLiked) {
        itemLiked = item.liked
        initializedLiked = true
    }

//    /liked



    Log.d(TAG, "the itemLiked is $itemLiked")
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.item)) })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d(TAG, "save text: $itemText, date: $itemDate");
                    submitStarted = true
                    itemViewModel.saveOrUpdateItem(itemText, itemDate, itemLength.toInt(), itemLiked)
                },
            ) { Icon(imageVector = Icons.Rounded.Save, contentDescription = "Save") }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
//            text edit
            TextField(
                label = { Text(text = "Text") },
                value =  itemText,
                onValueChange = { itemText = it },
                modifier = Modifier.fillMaxWidth()
            )
//            /text edit

            Spacer(modifier = Modifier.height(16.dp))

//            length edit
            TextField(
                label = { Text(text = "Length") },
                value = itemLength.toString(),
                onValueChange = { value ->
                    itemLength = value.filter { it.isDigit() }
                }
            )
//            /length edit

            Spacer(modifier = Modifier.height(16.dp))

//            liked edit

            Row (
                modifier = Modifier.padding(8.dp)
            ){
                Checkbox(
                    checked = itemLiked,
                    onCheckedChange = {
                        itemLiked = it
                        item?.liked = it
                    } )
                Text(text = ("Liked"))
            }
            

//            /liked edit

//            date picker
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = "Selected Date: ${itemDate}")
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    datePickerDialog.show()
                }) {
                    Text(text = "Open Date Picker")
                }
            }
//            date picker

            if (fetching != null && fetching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp)
                );
            }
            if (fetchingError != null) {
                Text(text = "Fetching failed")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemScreenPreview() {
    ItemScreen(rememberNavController())
}