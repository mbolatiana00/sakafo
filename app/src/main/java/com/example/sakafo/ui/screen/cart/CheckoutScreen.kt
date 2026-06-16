package com.example.sakafo.ui.screen.cart

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sakafo.data.Api.model.CreateOrderRequest
import com.example.sakafo.data.Api.retrofit.RetrofitClient
import com.example.sakafo.data.repository.OrderRepositoryImplement
import com.example.sakafo.viewmodel.OrderUiState
import com.example.sakafo.viewmodel.OrderViewModel
import com.example.sakafo.viewmodel.OrderViewModelFactory
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

private val OrangePrimary = Color(0xFFFF6B35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    userId: Int,
    restaurantId: Int,
    onBackClick: () -> Unit,
    onOrderSuccess: (orderId: Int, amount: Double) -> Unit
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    val orderViewModel: OrderViewModel = viewModel(
        factory = remember {
            OrderViewModelFactory(OrderRepositoryImplement(RetrofitClient.apiService))
        }
    )

    val uiState by orderViewModel.uiState.collectAsState()

    var pickupAddress   by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var isLocating      by remember { mutableStateOf(false) }

    val subtotal    = CartManager.getTotalPrice()
    val deliveryFee = 2.99
    val total       = subtotal + deliveryFee

    // ── Succès API ────────────────────────────────────────────────────────────
    LaunchedEffect(uiState) {
        if (uiState is OrderUiState.SingleSuccess) {
            val order = (uiState as OrderUiState.SingleSuccess).order
            CartManager.clearCart()
            onOrderSuccess(order.id, order.totalPrice)
        }
    }

    // ── Permission GPS ────────────────────────────────────────────────────────
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                isLocating = true
                try {
                    // ✅ Vérification explicite de la permission (évite le warning lint
                    // et protège contre une révocation entre l'octroi et l'appel)
                    val hasFineLocation = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFineLocation) {
                        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                        val location    = fusedClient.lastLocation.await()

                        if (location != null) {
                            // ✅ Convertit coordonnées → adresse lisible
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val address   = addresses?.firstOrNull()

                            if (address != null) {
                                val readable = buildString {
                                    address.thoroughfare?.let { append(it) }
                                    address.subThoroughfare?.let { append(" $it") }
                                    address.locality?.let { append(", $it") }
                                }.ifEmpty { "${location.latitude}, ${location.longitude}" }

                                pickupAddress = readable  // ✅ remplit automatiquement
                            } else {
                                // Fallback : coordonnées brutes
                                pickupAddress = "${location.latitude}, ${location.longitude}"
                            }

                            // ✅ Envoie aussi la position vers n8n
                            sendLocationToN8n(
                                latitude   = location.latitude,
                                longitude  = location.longitude,
                                userId     = userId,
                                deliveryId = 0  // pas encore de livraison à ce stade
                            )
                        }
                    } else {
                        android.util.Log.w("CHECKOUT", "Permission localisation refusée")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("CHECKOUT", "Erreur GPS: ${e.message}")
                } finally {
                    isLocating = false
                }
            }
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

        if (uiState is OrderUiState.Loading) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = OrangePrimary)
                    Spacer(Modifier.height(16.dp))
                    Text("Envoi de votre commande…", color = Color.Gray)
                }
            }
            return@Scaffold
        }

        if (uiState is OrderUiState.Error) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text("❌", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text((uiState as OrderUiState.Error).message, color = Color.Red, fontSize = 14.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { orderViewModel.resetState() }, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                        Text("Réessayer")
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))

            // ── Récapitulatif ─────────────────────────────────────────────
            CheckoutSection(title = "🛒  Récapitulatif") {
                if (CartManager.cartItems.isEmpty()) {
                    Text("Panier vide", color = Color.Gray, fontSize = 14.sp)
                } else {
                    CartManager.cartItems.forEach { item ->
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text("🍽️", fontSize = 18.sp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(item.dish.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("x${item.quantity}  •  ${item.dish.restaurant}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Text("%.2f€".format(item.dish.price * item.quantity), fontWeight = FontWeight.Bold, color = OrangePrimary, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Adresses ──────────────────────────────────────────────────
            CheckoutSection(title = "📍  Adresses") {

                // ✅ Bouton localisation automatique
                OutlinedButton(
                    onClick  = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)
                ) {
                    if (isLocating) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = OrangePrimary, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Localisation en cours…")
                    } else {
                        Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Utiliser ma position actuelle", fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Adresse pickup
                OutlinedTextField(
                    value         = pickupAddress,
                    onValueChange = { pickupAddress = it },
                    label         = { Text("Adresse de récupération") },
                    placeholder   = { Text("Ex : 12 rue du Marché") },
                    leadingIcon   = { Icon(Icons.Default.Store, null, tint = OrangePrimary) },
                    trailingIcon  = {
                        if (pickupAddress.isNotEmpty()) {
                            IconButton(onClick = { pickupAddress = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier   = Modifier.fillMaxWidth(),
                    shape      = RoundedCornerShape(12.dp),
                    colors     = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary, unfocusedBorderColor = Color(0xFFE0E0E0)),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                // Adresse delivery
                OutlinedTextField(
                    value         = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label         = { Text("Adresse de livraison") },
                    placeholder   = { Text("Ex : 5 avenue de la Paix") },
                    leadingIcon   = { Icon(Icons.Default.Home, null, tint = OrangePrimary) },
                    trailingIcon  = {
                        if (deliveryAddress.isNotEmpty()) {
                            IconButton(onClick = { deliveryAddress = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier   = Modifier.fillMaxWidth(),
                    shape      = RoundedCornerShape(12.dp),
                    colors     = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary, unfocusedBorderColor = Color(0xFFE0E0E0)),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Total ─────────────────────────────────────────────────────
            CheckoutSection(title = "💰  Total") {
                PriceLine("Sous-total",         "%.2f€".format(subtotal))
                Spacer(Modifier.height(6.dp))
                PriceLine("Frais de livraison", "%.2f€".format(deliveryFee))
                Divider(modifier = Modifier.padding(vertical = 10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Total", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text("%.2f€".format(total), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
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
                            price           = total,
                            restaurantId    = CartManager.restaurantId
                        )
                    )
                },
                enabled  = canOrder,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(56.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = OrangePrimary, disabledContainerColor = Color(0xFFFFCCB3))
            ) {
                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Confirmer la commande  •  %.2f€".format(total), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            if (!canOrder) {
                Spacer(Modifier.height(8.dp))
                Text("⚠️ Remplissez les deux adresses pour continuer", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(36.dp))
        }
    }
}

// ── Envoie position vers n8n ──────────────────────────────────────────────────
private fun sendLocationToN8n(latitude: Double, longitude: Double, userId: Int, deliveryId: Int) {
    Thread {
        try {
            val url  = java.net.URL("http://192.168.1.217:5678/webhook/location")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val body = """{"deliveryId":$deliveryId,"latitude":$latitude,"longitude":$longitude,"userId":$userId,"role":"CLIENT"}"""
            conn.outputStream.write(body.toByteArray())
            conn.outputStream.flush()

            android.util.Log.d("N8N", "Position envoyée → ${conn.responseCode}")
            conn.disconnect()
        } catch (e: Exception) {
            android.util.Log.e("N8N", "Erreur envoi position: ${e.message}")
        }
    }.start()
}

// ── Composants utilitaires ────────────────────────────────────────────────────
@Composable
private fun CheckoutSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontSize = 14.sp)
    }
}