package com.ilazar.myapp.todo.items

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ilazar.myapp.R
import com.ilazar.myapp.todo.data.Item

val TAG = "ItemsScreen"

@Composable
fun ItemsScreen(navController: NavController, itemsViewModel: ItemsViewModel) {

    val connectivityManager
            = LocalContext.current.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetworkInfo = connectivityManager.getActiveNetworkInfo()
    var networkState by remember { mutableStateOf(activeNetworkInfo?.isConnected) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network : Network) {
                Log.e(TAG, "The default network is now: " + network)
                networkState = true

            }

            override fun onLost(network : Network) {
                Log.e(TAG, "The application no longer has a default network. The last default network was " + network)
                networkState = false
            }

            override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
//                Log.e(TAG, "The default network changed capabilities: " + networkCapabilities)
            }

            override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {
//                Log.e(TAG, "The default network changed link properties: " + linkProperties)
            }
        })
    }

    Log.d("main app", "!!!!!!!!!!!!!!!!!!! items view model: ${itemsViewModel.items.value}");


    Scaffold(
        topBar = { TopAppBar(title = {
            Text(text = stringResource(id = R.string.items))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "   " + if(networkState == true) "connected" else "disconnected"
            )
        }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("item") },
            ) { Icon(imageVector = Icons.Rounded.Add, contentDescription = "Save") }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            val loading = itemsViewModel.loading.observeAsState().value
            val itemList = itemsViewModel.items.observeAsState().value
            val loadingError = itemsViewModel.loadingError.observeAsState().value

            if(itemList == null) {
                itemsViewModel.refresh()

                Log.d("main app", "!!!!!!!!!!!!!!!!!!! items view model: ${itemsViewModel.items.value}");

            }

            if (loading != null && loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp)
                );
            } else if (loadingError != null) {
                Text(text = "Loading failed")
            } else {
                if(itemList == null) {
                    Log.d("main app", "!!!!!!!!!!!!!!!!!!! items view model is still: ${itemsViewModel.items.value}")
                } else
                    ItemList(itemList!!, navController)
            }
        }
    }
}

@Composable
fun ItemList(itemList: List<Item>, navController: NavController) {
    LazyColumn {
        items(itemList) { item ->
            ItemDetail(item, navController)
        }
    }
}

@Composable
fun ItemDetail(item: Item, navController: NavController) {
    Row(
        modifier = Modifier.padding(all = 8.dp)
            .clickable {
//                Log.d(TAG, "the item clicked: ${item._id}, ${item.text}")
                navController.navigate("item?itemId=${item._id}")
            }
    ) {
        Column {
            Text(
                text = item.text,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenPreview() {
//    ItemsScreen(rememberNavController())
}