package com.example.sakafo.ui.screen.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakafo.data.Dish
import com.example.sakafo.data.FoodData
import com.example.sakafo.ui.screen.cart.CartManager

// ── Palette ───────────────────────────────────────────────────────────────────
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight   = Color(0xFFFF9A5C)
private val OrangeDark    = Color(0xFFE84E0F)
private val DeepBrown     = Color(0xFF1A0A00)
private val TextGray      = Color(0xFF9A8880)

// ─────────────────────────────────────────────────────────────────────────────
// UserHeaderCard
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun UserHeaderCard(
    userName: String,
    userPhone: String,
    onNotificationClick: () -> Unit = {}, // ✅ reçu depuis FoodDeliveryScreen
    onDropdownClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue   = if (expanded) 180f else 0f,
        animationSpec = tween(300),
        label         = "arrow"
    )

    val initials = userName
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }

    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Bonjour"
        hour < 18 -> "Bon après-midi"
        else      -> "Bonsoir"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 12.dp,
                shape        = RoundedCornerShape(24.dp),
                ambientColor = OrangePrimary.copy(alpha = 0.12f),
                spotColor    = OrangePrimary.copy(alpha = 0.18f)
            ),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(OrangePrimary.copy(alpha = 0.07f), Color.Transparent),
                        start  = Offset(0f, 0f),
                        end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.weight(1f)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(OrangeLight, OrangeDark),
                                    start  = Offset(0f, 0f),
                                    end    = Offset(80f, 80f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { expanded = !expanded; onDropdownClick() }
                    ) {
                        Text(
                            text          = "$greeting 👋",
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.Medium,
                            color         = TextGray,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text       = userName.ifEmpty { "Bon Appétit" },
                                fontSize   = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = DeepBrown,
                                maxLines   = 1,
                                overflow   = TextOverflow.Ellipsis,
                                modifier   = Modifier.weight(1f, fill = false)
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint     = OrangePrimary,
                                modifier = Modifier.size(18.dp).rotate(arrowRotation)
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        if (userPhone.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(OrangePrimary)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text       = userPhone,
                                    fontSize   = 12.sp,
                                    color      = TextGray,
                                    fontWeight = FontWeight.Medium,
                                    maxLines   = 1,
                                    overflow   = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                // ✅ Cloche — appelle onNotificationClick directement
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(OrangePrimary.copy(alpha = 0.08f))
                        .clickable { onNotificationClick() },   // ✅ correct
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint     = OrangePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FoodDeliveryScreen — ✅ onNotificationClick ajouté comme paramètre
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDeliveryScreen(
    onLogoutClick: () -> Unit,
    onCartClick: () -> Unit,
    onDishClick: (String) -> Unit,
    onNotificationClick: () -> Unit = {},   // ✅ nouveau paramètre
    onOrdersClick: () -> Unit = {},        // ✅ nouveau
    userPhone: String = "",
    userName: String = ""
) {
    var selectedCategory by remember { mutableStateOf("Tout") }
    var searchQuery by remember { mutableStateOf("") }

    val cartSize by remember {
        derivedStateOf { CartManager.cartItems.sumOf { it.quantity } }
    }

    val filteredDishes = remember(selectedCategory, searchQuery) {
        if (searchQuery.isNotEmpty()) FoodData.searchDishes(searchQuery)
        else FoodData.getDishesByCategory(selectedCategory)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {

                        // ✅ Bouton Mes commandes
                        IconButton(onClick = onOrdersClick) {
                            Icon(
                                Icons.Default.Receipt,         // ou ListAlt
                                contentDescription = "Mes commandes",
                                tint = Color.Gray
                            )
                        }
                        IconButton(onClick = onLogoutClick) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, "Déconnexion", tint = Color.Gray)
                        }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Déconnexion", tint = Color.Gray)
                    }
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        IconButton(onClick = onCartClick) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ShoppingCart, "Panier", tint = Color.White)
                            }
                        }
                        if (cartSize > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = if (cartSize > 9) "9+" else cartSize.toString(),
                                    color      = Color.White,
                                    fontSize   = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text       = "LIVRER À",
                        fontSize   = 12.sp,
                        color      = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(10.dp))

                    // ✅ onNotificationClick transmis depuis NavGraph
                    UserHeaderCard(
                        userName            = userName,
                        userPhone           = userPhone,
                        onNotificationClick = onNotificationClick,
                        onDropdownClick     = {}
                    )

                    Spacer(Modifier.height(20.dp))
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier      = Modifier.fillMaxWidth().height(56.dp),
                        placeholder   = { Text("Rechercher des plats, restaurants", color = Color.Gray) },
                        leadingIcon   = { Icon(Icons.Default.Search, "Rechercher", tint = Color.Gray) },
                        trailingIcon  = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, "Effacer", tint = Color.Gray)
                                }
                            }
                        },
                        shape  = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor   = OrangePrimary
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("Toutes les Catégories", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        TextButton(onClick = {}) {
                            Text("Voir Tout", color = OrangePrimary)
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint     = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding        = PaddingValues(horizontal = 20.dp)
                ) {
                    items(FoodData.categories) { category ->
                        CategoryChip(
                            text       = category.name,
                            emoji      = category.emoji,
                            isSelected = selectedCategory == category.name,
                            onClick    = { selectedCategory = category.name; searchQuery = "" }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = if (searchQuery.isNotEmpty())
                            "Résultats de recherche (${filteredDishes.size})"
                        else "Plats disponibles",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            if (filteredDishes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("Aucun plat trouvé", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            } else {
                items(filteredDishes) { dish ->
                    DishCard(
                        dish        = dish,
                        onAddToCart = { CartManager.addItem(dish) },
                        onDishClick = { onDishClick(dish.id) },
                        modifier    = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CategoryChip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CategoryChip(text: String, emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick  = onClick,
        shape    = RoundedCornerShape(24.dp),
        color    = if (isSelected) Color(0xFFFFF3E0) else Color(0xFFF5F5F5),
        modifier = Modifier.height(48.dp)
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text(text = text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DishCard
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun DishCard(dish: Dish, onAddToCart: () -> Unit, onDishClick: () -> Unit, modifier: Modifier = Modifier) {
    var showSnackbar by remember { mutableStateOf(false) }

    Card(
        onClick   = onDishClick,
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Box(
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍽️", fontSize = 40.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(dish.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(dish.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Store, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(dish.restaurant, fontSize = 11.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(dish.preparationTime, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("${dish.price}€", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(dish.rating.toString(), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Button(
                    onClick = { onAddToCart(); showSnackbar = true },
                    colors  = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape   = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Ajouter")
                }
            }
        }
    }

    if (showSnackbar) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1500)
            showSnackbar = false
        }
    }
}