package com.example.sakafo.ui.screen.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sakafo.data.Api.model.Order
import com.example.sakafo.data.Api.model.OrderStatus
import com.example.sakafo.data.Api.retrofit.RetrofitClient
import com.example.sakafo.data.repository.OrderRepositoryImplement
import com.example.sakafo.viewmodel.OrderUiState
import com.example.sakafo.viewmodel.OrderViewModel
import com.example.sakafo.viewmodel.OrderViewModelFactory

private val OrangePrimary = Color(0xFFFF6B35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBackClick: () -> Unit,
    userId: Int
) {
    val orderViewModel: OrderViewModel = viewModel(
        factory = remember {
            OrderViewModelFactory(
                OrderRepositoryImplement(RetrofitClient.apiService)
            )
        }
    )

    val uiState by orderViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // ✅ FIX : on ne fait l'appel que si userId est valide
        if (userId > 0) {
            orderViewModel.getUserOrders(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes commandes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ✅ FIX : userId invalide → message clair sans appel réseau
            if (userId <= 0) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠️", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Utilisateur non connecté",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                return@Box
            }

            when (val state = uiState) {
                is OrderUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = OrangePrimary
                    )
                }

                is OrderUiState.Success -> {
                    if (state.orders.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🛍️", fontSize = 64.sp)
                            Spacer(Modifier.height(16.dp))
                            Text("Aucune commande", fontSize = 16.sp, color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.orders,
                                key = { it.id }
                            ) { order ->
                                OrderCard(order = order)
                            }
                        }
                    }
                }

                is OrderUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("❌", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, color = Color.Red, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { orderViewModel.getUserOrders(userId) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Réessayer")
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    // ✅ FIX : status nullable → fallback sur PENDING si null
    val safeStatus = order.status ?: OrderStatus.PENDING

    val statusColor = when (safeStatus) {
        OrderStatus.PENDING     -> Color(0xFFFFA726)
        OrderStatus.CONFIRMED   -> Color(0xFF42A5F5)
        OrderStatus.IN_PROGRESS -> OrangePrimary
        OrderStatus.DELIVERED   -> Color(0xFF66BB6A)
        OrderStatus.CANCELED    -> Color(0xFFEF5350)
    }

    val statusLabel = when (safeStatus) {
        OrderStatus.PENDING     -> "En attente"
        OrderStatus.CONFIRMED   -> "Confirmée"
        OrderStatus.IN_PROGRESS -> "En cours"
        OrderStatus.DELIVERED   -> "Livrée"
        OrderStatus.CANCELED    -> "Annulée"
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Commande #${order.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text       = statusLabel,
                        color      = statusColor,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ✅ FIX : pickupAddress et deliveryAddress sont nullable
            Text(
                "📍 ${order.pickupAddress ?: "Adresse inconnue"}",
                fontSize = 13.sp,
                color    = Color.Gray
            )
            Text(
                "🏁 ${order.deliveryAddress ?: "Adresse inconnue"}",
                fontSize = 13.sp,
                color    = Color.Gray
            )

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ✅ FIX : createdAt nullable → fallback "-"
                Text(
                    order.createdAt?.take(10) ?: "-",
                    fontSize = 12.sp,
                    color    = Color.Gray
                )
                Text(
                    "${order.price}€",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = OrangePrimary
                )
            }
        }
    }
}