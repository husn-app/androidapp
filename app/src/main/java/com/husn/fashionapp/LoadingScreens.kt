package com.husn.fashionapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WishlistLoadingScreen(showSearchBar: Boolean = false) {
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomBar(selectedItem = 3) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                TopNavBar()
                if(showSearchBar){
                    SearchBar()
                }
            }
            items(5) { // Show 5 rows of placeholders
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // First product in row
                    StaticProductItem(
                        modifier = Modifier.weight(1f)
                    )

                    // Second product in row
                    StaticProductItem(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun StaticProductItem(
    modifier: Modifier = Modifier
) {
    val placeholderColor = Color.LightGray.copy(alpha = 0.3f)

    Column(modifier = modifier) {
        // Product image placeholder with same aspect ratio as ImageFromUrl
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(16.dp))
                .background(placeholderColor)
        )

        // Brand name placeholder
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(15.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(placeholderColor)
        )

        // Product name placeholder
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(15.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(placeholderColor)
        )
    }
}

@Composable
fun ProductLoadingScreen() {
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomBar(selectedItem = 3) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                TopNavBar()
                SearchBar()
                StaticProductItem(modifier = Modifier.padding(8.dp))
            }

            items(5) { // Show 5 rows of placeholders
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // First product in row
                    StaticProductItem(
                        modifier = Modifier.weight(1f)
                    )

                    // Second product in row
                    StaticProductItem(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun FeedLoadingScreen(){
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomBar(selectedItem = 1) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                TopNavBar()
                SearchBar()
                StaticProductItem(modifier = Modifier.padding(8.dp))
                StaticProductItem(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun InspirationLoadingScreen() {
    val placeholderColor = Color.LightGray.copy(alpha = 0.3f)
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomBar(selectedItem = 2) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar + Search
            item {
                TopNavBar()
                SearchBar()
            }

            // Loading sections
            items(2) { sectionIndex ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Category title placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(15.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(placeholderColor)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Products row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(3) {
                            Column(
                                modifier = Modifier.width(250.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Image placeholder
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.75f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(placeholderColor)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(15.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(placeholderColor)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
                Divider()
            }
        }
    }
}