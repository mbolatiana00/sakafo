package com.example.sakafo.ui.screen.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sakafo.data.Api.retrofit.RetrofitClient
import com.example.sakafo.data.repository.PaymentRepositoryImplement
import com.example.sakafo.viewmodel.PaymentUiState
import com.example.sakafo.viewmodel.PaymentViewModel
import com.example.sakafo.viewmodel.PaymentViewModelFactory

private val OrangePrimary = Color(0xFFFF6B35)

enum class PaymentMethodOption(val label: String, val icon: ImageVector, val apiValue: String) {
    CASH("Espèces",        Icons.Default.Money,        "CASH"),
    CARD("Carte bancaire", Icons.Default.CreditCard,   "CARD"),
    MOBILE_MONEY("Mobile Money", Icons.Default.PhoneAndroid, "MOBILE_MONEY")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    orderId: Int,
    amount: Double,
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val paymentViewModel: PaymentViewModel = viewModel(
        factory = remember {
            PaymentViewModelFactory(PaymentRepositoryImplement(RetrofitClient.apiService))
        }
    )

    val uiState by paymentViewModel.uiState.collectAsState()
    var selectedMethod by remember { mutableStateOf<PaymentMethodOption?>(null) }

    // ── Succès → naviguer vers OrdersScreen ──────────────────────────────────
    LaunchedEffect(uiState) {
        if (uiState is PaymentUiState.Success) {
            onPaymentSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paiement", fontWeight = FontWeight.Bold) },
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

        // ── LOADING ───────────────────────────────────────────────────────────
        if (uiState is PaymentUiState.Loading) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = OrangePrimary)
                    Spacer(Modifier.height(16.dp))
                    Text("Traitement du paiement…", color = Color.Gray)
                }
            }
            return@Scaffold
        }

        // ── ERROR ─────────────────────────────────────────────────────────────
        if (uiState is PaymentUiState.Error) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text("❌", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        (uiState as PaymentUiState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { paymentViewModel.resetState() },
                        colors  = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) { Text("Réessayer") }
                }
            }
            return@Scaffold
        }

        // ── FORMULAIRE ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Récapitulatif montant ─────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💳", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Commande #$orderId", color = Color.Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "%.2f€".format(amount),
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = OrangePrimary
                    )
                }
            }

            // ── Choix du mode de paiement ─────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Mode de paiement", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(Modifier.height(16.dp))

                    PaymentMethodOption.entries.forEach { method ->
                        val isSelected = selectedMethod == method
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape  = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) OrangePrimary else Color(0xFFE0E0E0)
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) OrangePrimary.copy(alpha = 0.06f) else Color.White
                            ),
                            onClick = { selectedMethod = method }
                        ) {
                            Row(
                                modifier          = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    method.icon, null,
                                    tint     = if (isSelected) OrangePrimary else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(14.dp))
                                Text(
                                    method.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color      = if (isSelected) OrangePrimary else Color.Black,
                                    fontSize   = 15.sp
                                )
                                Spacer(Modifier.weight(1f))
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Bouton payer ──────────────────────────────────────────────────
            Button(
                onClick = {
                    selectedMethod?.let { method ->
                        paymentViewModel.createPayment(
                            orderId = orderId,
                            amount  = amount,
                            method  = method.apiValue  // "CASH", "CARD", "MOBILE_MONEY"
                        )
                    }
                },
                enabled  = selectedMethod != null,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = OrangePrimary,
                    disabledContainerColor = Color(0xFFFFCCB3)
                )
            ) {
                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (selectedMethod != null) "Payer via ${selectedMethod!!.label}"
                    else "Choisir un mode de paiement",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
            }

            if (selectedMethod == null) {
                Text(
                    "⚠️ Sélectionnez un mode de paiement pour continuer",
                    color     = Color.Gray,
                    fontSize  = 12.sp,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}