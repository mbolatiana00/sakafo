package com.example.sakafo.ui.screen.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    userId: Int,
    onOrderClick: (Int) -> Unit
) {
    val orderViewModel: OrderViewModel = viewModel(
        factory = remember {
            OrderViewModelFactory(OrderRepositoryImplement(RetrofitClient.apiService))
        }
    )

    val uiState by orderViewModel.uiState.collectAsState()

    // ── Charge les commandes au démarrage ─────────────────────────────────────
    LaunchedEffect(Unit) {
        if (userId > 0) orderViewModel.getUserOrders(userId)
    }

    // ── Recharge après confirmation ───────────────────────────────────────────
    LaunchedEffect(uiState) {
        if (uiState is OrderUiState.SingleSuccess) {
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            if (userId <= 0) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠️", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Utilisateur non connecté", color = Color.Gray)
                }
                return@Box
            }

            when (val state = uiState) {

                is OrderUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = OrangePrimary)
                }

                is OrderUiState.Success -> {
                    if (state.orders.isEmpty()) {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛍️", fontSize = 64.sp)
                            Spacer(Modifier.height(16.dp))
                            Text("Aucune commande", fontSize = 16.sp, color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = state.orders, key = { it.id }) { order ->
                                OrderCard(
                                    order          = order,
                                    onDetailClick  = { onOrderClick(order.id) },
                                    onConfirmClick = {
                                        orderViewModel.updateOrderStatus(order.id, OrderStatus.CONFIRMED)
                                    }
                                )
                            }
                        }
                    }
                }

                is OrderUiState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, color = Color.Red, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { orderViewModel.getUserOrders(userId) },
                            colors  = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) { Text("Réessayer") }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onDetailClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val safeStatus = order.status ?: OrderStatus.PENDING
    val canConfirm = safeStatus == OrderStatus.PENDING

    val statusLabel = when (safeStatus) {
        OrderStatus.PENDING    -> "En attente"
        OrderStatus.CONFIRMED  -> "Confirmée"
        OrderStatus.PICKED_UP  -> "Récupérée"
        OrderStatus.IN_TRANSIT -> "En livraison"
        OrderStatus.DELIVERED  -> "Livrée"
        OrderStatus.CANCELED   -> "Annulée"
    }

    val statusColor = when (safeStatus) {
        OrderStatus.PENDING    -> Color(0xFFFFA726)
        OrderStatus.CONFIRMED  -> Color(0xFF42A5F5)
        OrderStatus.PICKED_UP  -> Color(0xFF7E57C2)
        OrderStatus.IN_TRANSIT -> OrangePrimary
        OrderStatus.DELIVERED  -> Color(0xFF66BB6A)
        OrderStatus.CANCELED   -> Color(0xFFEF5350)
    }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onDetailClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── En-tête ───────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Commande #${order.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.12f)) {
                    Text(
                        statusLabel,
                        color      = statusColor,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Adresses ──────────────────────────────────────────────────────
            Text("📍 ${order.pickupAddress ?: "Adresse inconnue"}", fontSize = 13.sp, color = Color.Gray)
            Text("🏁 ${order.deliveryAddress ?: "Adresse inconnue"}", fontSize = 13.sp, color = Color.Gray)

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            // ── Date + prix ───────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.createdAt?.take(10) ?: "-", fontSize = 12.sp, color = Color.Gray)
                Text("%.2f€".format(order.totalPrice), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OrangePrimary)
            }

            // ── Bouton confirmer (PENDING seulement) ──────────────────────────
            if (canConfirm) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick  = onConfirmClick,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmer la commande", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}