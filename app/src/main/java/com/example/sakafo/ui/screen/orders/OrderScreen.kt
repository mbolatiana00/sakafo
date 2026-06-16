package com.example.sakafo.ui.screen.orders

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun OrderDetailScreen(
    orderId: Int,
    onBackClick: () -> Unit
) {
    val orderViewModel: OrderViewModel = viewModel(
        factory = remember {
            OrderViewModelFactory(OrderRepositoryImplement(RetrofitClient.apiService))
        }
    )

    val uiState by orderViewModel.uiState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        orderViewModel.getOrderById(orderId)
    }

    // ── Dialog confirmation annulation ────────────────────────────────────────
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Annuler la commande", fontWeight = FontWeight.Bold) },
            text  = { Text("Voulez-vous vraiment annuler cette commande ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        orderViewModel.cancelOrder(orderId)
                        showCancelDialog = false
                    }
                ) {
                    Text("Confirmer", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Retour")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail commande", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->

        when (val state = uiState) {

            is OrderUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            }

            is OrderUiState.SingleSuccess -> {
                OrderDetailContent(
                    order           = state.order,
                    padding         = padding,
                    onCancelClick   = { showCancelDialog = true }
                )
            }

            is OrderUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, color = Color.Red, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { orderViewModel.getOrderById(orderId) },
                            colors  = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) { Text("Réessayer") }
                    }
                }
            }

            else -> {}
        }
    }
}

// ── Contenu principal ─────────────────────────────────────────────────────────
@Composable
private fun OrderDetailContent(
    order: Order,
    padding: PaddingValues,
    onCancelClick: () -> Unit
) {
    val safeStatus = order.status ?: OrderStatus.PENDING
    val canCancel  = safeStatus == OrderStatus.PENDING || safeStatus == OrderStatus.CONFIRMED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── En-tête statut ────────────────────────────────────────────────────
        StatusHeaderCard(order = order, status = safeStatus)

        // ── Timeline ──────────────────────────────────────────────────────────
        TimelineCard(currentStatus = safeStatus)

        // ── Adresses ─────────────────────────────────────────────────────────
        DetailSection(title = "📍 Adresses") {
            AddressRow(icon = Icons.Default.Store, label = "Récupération", address = order.pickupAddress ?: "—")
            Spacer(Modifier.height(10.dp))
            AddressRow(icon = Icons.Default.Home, label = "Livraison", address = order.deliveryAddress ?: "—")
        }

        // ── Résumé prix ───────────────────────────────────────────────────────
        DetailSection(title = "💰 Résumé") {
            PriceRow("Date",        order.createdAt?.take(10) ?: "—")
            Spacer(Modifier.height(6.dp))
            Divider()
            Spacer(Modifier.height(6.dp))
            PriceRow("Total", "%.2f€".format(order.totalPrice), bold = true, color = OrangePrimary)
        }

        // ── Bouton annuler ────────────────────────────────────────────────────
        if (canCancel) {
            Button(
                onClick  = onCancelClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
            ) {
                Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Annuler la commande", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ── Carte statut en-tête ──────────────────────────────────────────────────────
@Composable
private fun StatusHeaderCard(order: Order, status: OrderStatus) {
    val statusColor = statusColor(status)
    val statusLabel = statusLabel(status)
    val statusEmoji = statusEmoji(status)

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(statusEmoji, fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Commande #${order.id}",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text       = statusLabel,
                    color      = statusColor,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// ── Timeline ──────────────────────────────────────────────────────────────────
@Composable
private fun TimelineCard(currentStatus: OrderStatus) {
    val steps = listOf(
        OrderStatus.PENDING    to "En attente",
        OrderStatus.CONFIRMED  to "Confirmée",
        OrderStatus.PICKED_UP  to "Récupérée",
        OrderStatus.IN_TRANSIT to "En livraison",
        OrderStatus.DELIVERED  to "Livrée"
    )

    val currentIndex = steps.indexOfFirst { it.first == currentStatus }
    val isCanceled   = currentStatus == OrderStatus.CANCELED

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Suivi", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(16.dp))

            if (isCanceled) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFEF5350)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Commande annulée", color = Color(0xFFEF5350), fontWeight = FontWeight.SemiBold)
                }
                return@Column
            }

            steps.forEachIndexed { index, (_, label) ->
                val isDone    = index <= currentIndex
                val isCurrent = index == currentIndex
                val dotColor  = if (isDone) OrangePrimary else Color(0xFFE0E0E0)

                Row(verticalAlignment = Alignment.Top) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier         = Modifier.size(28.dp).clip(CircleShape).background(dotColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone && !isCurrent) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            } else if (isCurrent) {
                                Box(Modifier.size(10.dp).clip(CircleShape).background(Color.White))
                            }
                        }
                        if (index < steps.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(28.dp)
                                    .background(if (index < currentIndex) OrangePrimary else Color(0xFFE0E0E0))
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text       = label,
                        fontSize   = 14.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isDone) Color.Black else Color.Gray,
                        modifier   = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

// ── Section générique ─────────────────────────────────────────────────────────
@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

// ── Ligne adresse ─────────────────────────────────────────────────────────────
@Composable
private fun AddressRow(icon: ImageVector, label: String, address: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(address, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Ligne prix ────────────────────────────────────────────────────────────────
@Composable
private fun PriceRow(
    label: String,
    value: String,
    bold: Boolean = false,
    color: Color = Color.Black
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = if (bold) Color.Black else Color.Gray)
        Text(
            value,
            fontSize   = if (bold) 16.sp else 14.sp,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Normal,
            color      = color
        )
    }
}

// ── Helpers statut ────────────────────────────────────────────────────────────
private fun statusColor(status: OrderStatus) = when (status) {
    OrderStatus.PENDING    -> Color(0xFFFFA726)
    OrderStatus.CONFIRMED  -> Color(0xFF42A5F5)
    OrderStatus.PICKED_UP  -> Color(0xFF7E57C2)
    OrderStatus.IN_TRANSIT -> Color(0xFFFF6B35)
    OrderStatus.DELIVERED  -> Color(0xFF66BB6A)
    OrderStatus.CANCELED   -> Color(0xFFEF5350)
}

private fun statusLabel(status: OrderStatus) = when (status) {
    OrderStatus.PENDING    -> "En attente"
    OrderStatus.CONFIRMED  -> "Confirmée"
    OrderStatus.PICKED_UP  -> "Récupérée"
    OrderStatus.IN_TRANSIT -> "En livraison"
    OrderStatus.DELIVERED  -> "Livrée"
    OrderStatus.CANCELED   -> "Annulée"
}

private fun statusEmoji(status: OrderStatus) = when (status) {
    OrderStatus.PENDING    -> "⏳"
    OrderStatus.CONFIRMED  -> "✅"
    OrderStatus.PICKED_UP  -> "🛵"
    OrderStatus.IN_TRANSIT -> "🚀"
    OrderStatus.DELIVERED  -> "🎉"
    OrderStatus.CANCELED   -> "❌"
}