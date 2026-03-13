package com.example.sakafo.ui.screen.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sakafo.data.Api.model.CreateOrderRequest
import com.example.sakafo.data.Api.retrofit.RetrofitClient
import com.example.sakafo.data.repository.OrderRepositoryImplement
import com.example.sakafo.viewmodel.OrderUiState
import com.example.sakafo.viewmodel.OrderViewModel
import com.example.sakafo.viewmodel.OrderViewModelFactory

private val OrangePrimary = Color(0xFFFF6B35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    userId: Int,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit      // ✅ navigue vers OrdersScreen après succès
) {
    // ── ViewModel branché sur l'API ───────────────────────────────────────────
    val orderViewModel: OrderViewModel = viewModel(
        factory = remember {
            OrderViewModelFactory(
                OrderRepositoryImplement(RetrofitClient.apiService)
            )
        }
    )

    val uiState by orderViewModel.uiState.collectAsState()

    // ── Champs adresses ───────────────────────────────────────────────────────
    var pickupAddress   by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }

    // ── Calcul des prix ───────────────────────────────────────────────────────
    val subtotal    = CartManager.getTotalPrice()
    val deliveryFee = 2.99
    val total       = subtotal + deliveryFee

    // ── Succès API → vider le panier + naviguer ───────────────────────────────
    LaunchedEffect(uiState) {
        if (uiState is OrderUiState.SingleSuccess) {
            CartManager.clearCart()
            onOrderSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmer la commande", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->

        // ════════════════════════════════════════════════════════════════════
        // État LOADING
        // ════════════════════════════════════════════════════════════════════
        if (uiState is OrderUiState.Loading) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = OrangePrimary)
                    Spacer(Modifier.height(16.dp))
                    Text("Envoi de votre commande…", color = Color.Gray)
                }
            }
            return@Scaffold
        }

        // ════════════════════════════════════════════════════════════════════
        // État ERROR
        // ════════════════════════════════════════════════════════════════════
        if (uiState is OrderUiState.Error) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text("❌", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        (uiState as OrderUiState.Error).message,
                        color     = Color.Red,
                        fontSize  = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { orderViewModel.resetState() },
                        colors  = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) { Text("Réessayer") }
                }
            }
            return@Scaffold
        }

        // ════════════════════════════════════════════════════════════════════
        // Formulaire principal
        // ════════════════════════════════════════════════════════════════════
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))

            // ── Section 1 : Récapitulatif des articles ────────────────────
            CheckoutSection(title = "🛒  Récapitulatif") {
                if (CartManager.cartItems.isEmpty()) {
                    Text("Panier vide", color = Color.Gray, fontSize = 14.sp)
                } else {
                    CartManager.cartItems.forEach { item ->
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🍽️", fontSize = 18.sp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        item.dish.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = 14.sp,
                                        maxLines   = 1,
                                        overflow   = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "x${item.quantity}  •  ${item.dish.restaurant}",
                                        fontSize = 12.sp,
                                        color    = Color.Gray
                                    )
                                }
                            }
                            Text(
                                "%.2f€".format(item.dish.price * item.quantity),
                                fontWeight = FontWeight.Bold,
                                color      = OrangePrimary,
                                fontSize   = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Section 2 : Adresses ──────────────────────────────────────
            CheckoutSection(title = "📍  Adresses") {

                // Adresse de récupération (pickup)
                OutlinedTextField(
                    value         = pickupAddress,
                    onValueChange = { pickupAddress = it },
                    label         = { Text("Adresse de récupération") },
                    placeholder   = { Text("Ex : 12 rue du Marché") },
                    leadingIcon   = {
                        Icon(Icons.Default.Store, null, tint = OrangePrimary)
                    },
                    trailingIcon  = {
                        if (pickupAddress.isNotEmpty()) {
                            IconButton(onClick = { pickupAddress = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier   = Modifier.fillMaxWidth(),
                    shape      = RoundedCornerShape(12.dp),
                    colors     = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = OrangePrimary,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                // Adresse de livraison (delivery)
                OutlinedTextField(
                    value         = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label         = { Text("Adresse de livraison") },
                    placeholder   = { Text("Ex : 5 avenue de la Paix") },
                    leadingIcon   = {
                        Icon(Icons.Default.Home, null, tint = OrangePrimary)
                    },
                    trailingIcon  = {
                        if (deliveryAddress.isNotEmpty()) {
                            IconButton(onClick = { deliveryAddress = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier   = Modifier.fillMaxWidth(),
                    shape      = RoundedCornerShape(12.dp),
                    colors     = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = OrangePrimary,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Section 3 : Résumé des prix ───────────────────────────────
            CheckoutSection(title = "💰  Total") {
                PriceLine("Sous-total",         "%.2f€".format(subtotal))
                Spacer(Modifier.height(6.dp))
                PriceLine("Frais de livraison", "%.2f€".format(deliveryFee))
                Divider(modifier = Modifier.padding(vertical = 10.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Total", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "%.2f€".format(total),
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = OrangePrimary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Bouton Confirmer ──────────────────────────────────────────
            val canOrder = pickupAddress.isNotBlank() && deliveryAddress.isNotBlank()

            Button(
                onClick = {
                    orderViewModel.createOrder(
                        CreateOrderRequest(
                            userId          = userId,
                            pickupAddress   = pickupAddress.trim(),
                            deliveryAddress = deliveryAddress.trim(),
                            price           = total
                        )
                    )
                },
                enabled  = canOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = OrangePrimary,
                    disabledContainerColor = Color(0xFFFFCCB3)
                )
            ) {
                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Confirmer la commande  •  %.2f€".format(total),
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Message d'aide si bouton désactivé
            if (!canOrder) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "⚠️ Remplissez les deux adresses pour continuer",
                    color     = Color.Gray,
                    fontSize  = 12.sp,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(36.dp))
        }
    }
}

// ── Composants utilitaires ────────────────────────────────────────────────────

@Composable
private fun CheckoutSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun PriceLine(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value,               fontSize = 14.sp)
    }
}