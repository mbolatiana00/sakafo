package com.example.sakafo.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sakafo.data.Api.model.MenuItem
import com.example.sakafo.data.Api.model.Restaurant
import com.example.sakafo.data.Api.retrofit.RetrofitClient
import com.example.sakafo.data.repository.RestaurantRepositoryImplement
import com.example.sakafo.viewmodel.HomeUiState
import com.example.sakafo.viewmodel.HomeViewModel
import com.example.sakafo.viewmodel.HomeViewModelFactory

private val OrangePrimary = Color(0xFFFF6B35)

@Composable
fun HomeScreen(
    onDishClick:       (Int) -> Unit,
    onRestaurantClick: (Int) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel(
        factory = remember {
            HomeViewModelFactory(RestaurantRepositoryImplement(RetrofitClient.apiService))
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                "Sakafo 🍽️",
                fontSize    = 24.sp,
                fontWeight  = FontWeight.Bold,
                color       = OrangePrimary,
            )
            Text("Que voulez-vous manger ?", fontSize = 14.sp, color = Color.Gray)

            Spacer(Modifier.height(12.dp))

            // Barre de recherche
            OutlinedTextField(
                value         = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.search(it)
                },
                placeholder   = { Text("Rechercher un plat, un restaurant…") },
                leadingIcon   = { Icon(Icons.Default.Search, null, tint = OrangePrimary) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = OrangePrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                )
            )
        }

        // ── Contenu principal ─────────────────────────────────────────────────
        when (val state = uiState) {

            is HomeUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            }

            is HomeUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, color = Color.Red, fontSize = 13.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            colors  = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) { Text("Réessayer") }
                    }
                }
            }

            is HomeUiState.Success -> {
                HomeContent(
                    state              = state,
                    onCategorySelect   = { viewModel.selectCategory(it) },
                    onDishClick        = onDishClick,
                    onRestaurantClick  = onRestaurantClick,
                    onLoadMore         = { viewModel.loadMoreMenuItems() },
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state:             HomeUiState.Success,
    onCategorySelect:  (String) -> Unit,
    onDishClick:       (Int) -> Unit,
    onRestaurantClick: (Int) -> Unit,
    onLoadMore:        () -> Unit,
) {
    val listState = rememberLazyListState()

    // Détection fin de liste → charge la page suivante
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo) {
        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        val totalItems  = listState.layoutInfo.totalItemsCount
        if (lastVisible >= totalItems - 3 && state.hasMorePages && !state.isLoadingMore) {
            onLoadMore()
        }
    }

    LazyColumn(
        state           = listState,
        contentPadding  = PaddingValues(bottom = 80.dp),
    ) {
        // ── Restaurants populaires ────────────────────────────────────────────
        item {
            SectionTitle("Restaurants")
            LazyRow(
                contentPadding      = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.restaurants) { resto ->
                    RestaurantCard(resto, onClick = { onRestaurantClick(resto.id) })
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Catégories ────────────────────────────────────────────────────────
        item {
            SectionTitle("Catégories")
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.categories) { cat ->
                    CategoryChip(
                        label    = cat,
                        selected = cat == state.selectedCategory,
                        onClick  = { onCategorySelect(cat) },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Titre section plats ───────────────────────────────────────────────
        item {
            SectionTitle(
                if (state.selectedCategory == "Tout") "Tous les plats"
                else state.selectedCategory
            )
        }

        // ── Liste des plats ───────────────────────────────────────────────────
        if (state.menuItems.isEmpty()) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucun plat trouvé", color = Color.Gray)
                }
            }
        } else {
            items(state.menuItems, key = { it.id }) { dish ->
                DishCard(dish = dish, onClick = { onDishClick(dish.id) })
            }
        }

        // ── Loader pagination ─────────────────────────────────────────────────
        if (state.isLoadingMore) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrangePrimary, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

// ── Composants ────────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        fontSize   = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier   = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape     = RoundedCornerShape(20.dp),
        color     = if (selected) OrangePrimary else Color.White,
        shadowElevation = if (selected) 0.dp else 2.dp,
        modifier  = Modifier.clickable { onClick() },
    ) {
        Text(
            label,
            color      = if (selected) Color.White else Color.DarkGray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize   = 13.sp,
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        modifier  = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column {
            AsyncImage(
                model              = restaurant.imageUrl,
                contentDescription = restaurant.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
            )
            Column(Modifier.padding(8.dp)) {
                Text(
                    restaurant.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (restaurant.isOpen) Color(0xFF4CAF50) else Color.Red)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (restaurant.isOpen) "Ouvert" else "Fermé",
                        fontSize = 11.sp,
                        color    = Color.Gray,
                    )
                }
            }
        }
    }
}

@Composable
private fun DishCard(dish: MenuItem, onClick: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(Modifier.padding(12.dp)) {
            AsyncImage(
                model              = dish.imageUrl,
                contentDescription = dish.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(dish.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                dish.restaurant?.name?.let {
                    Text(it, fontSize = 12.sp, color = OrangePrimary)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    dish.description ?: "",
                    fontSize  = 12.sp,
                    color     = Color.Gray,
                    maxLines  = 2,
                    overflow  = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${dish.price.toInt()} Ar",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = OrangePrimary,
                )
            }
        }
    }
}