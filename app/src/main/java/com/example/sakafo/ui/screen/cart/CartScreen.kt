package com.example.sakafo.ui.screen.cart

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OrangePrimary = Color(0xFFFF6B35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckout: () -> Unit          // ✅ navigue vers CheckoutScreen
) {
    // ✅ observe le state Compose du CartManager directement
    val cartItems = CartManager.cartItems
    val subtotal  by remember { derivedStateOf { CartManager.getTotalPrice() } }
    val itemCount by remember { derivedStateOf { CartManager.getItemCount() } }

    val deliveryFee = 2.99
    val total = subtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Mon Panier  ($itemCount)",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { CartManager.clearCart() }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Vider le panier",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        // ── Bottom bar avec résumé + bouton commander ──────────────────────
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier       = Modifier.fillMaxWidth(),
                    color          = Color.White,
                    shadowElevation = 12.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        // Sous-total
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sous-total", color = Color.Gray, fontSize = 14.sp)
                            Text("%.2f€".format(subtotal), fontSize = 14.sp)
                        }

                        Spacer(Modifier.height(6.dp))

                        // Frais de livraison
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Frais de livraison", color = Color.Gray, fontSize = 14.sp)
                            Text("%.2f€".format(deliveryFee), fontSize = 14.sp)
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Total
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("Total", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                            Text(
                                "%.2f€".format(total),
                                fontSize   = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = OrangePrimary
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // ✅ Bouton Commander → CheckoutScreen (ne vide PAS le panier ici)
                        Button(
                            onClick  = onCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape  = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Commander  •  %.2f€".format(total),
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->

        if (cartItems.isEmpty()) {
            // ── Panier vide ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint     = Color(0xFFE0E0E0)
                )
                Spacer(Modifier.height(20.dp))
                Text("Votre panier est vide", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text(
                    "Ajoutez des plats délicieux pour commencer",
                    fontSize  = 14.sp,
                    color     = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))
                Button(
                    onClick = onBackClick,
                    colors  = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape   = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Restaurant, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Parcourir les plats")
                }
            }

        } else {
            // ── Liste des articles ─────────────────────────────────────────
            LazyColumn(
                modifier       = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F8F8))
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems, key = { it.dish.id }) { cartItem ->
                    CartItemCard(
                        cartItem        = cartItem,
                        onIncrease      = { CartManager.addItem(cartItem.dish) },
                        onDecrease      = { CartManager.decreaseQuantity(cartItem.dish.id) },
                        onRemove        = { CartManager.removeItem(cartItem.dish.id) }
                    )
                }
                // espace pour le bottom bar
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// ── CartItemCard ──────────────────────────────────────────────────────────────
@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Image placeholder
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍽️", fontSize = 30.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        cartItem.dish.name,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        cartItem.dish.restaurant,
                        fontSize = 12.sp,
                        color    = Color.Gray
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "%.2f€".format(cartItem.dish.price),
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = OrangePrimary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Bouton supprimer
                TextButton(
                    onClick = onRemove,
                    colors  = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Supprimer", fontSize = 13.sp)
                }

                // Contrôles quantité
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick  = onDecrease,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                    ) {
                        Icon(Icons.Default.Remove, null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        cartItem.quantity.toString(),
                        fontSize   = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.widthIn(min = 28.dp),
                        textAlign  = TextAlign.Center
                    )

                    IconButton(
                        onClick  = onIncrease,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Total ligne si quantité > 1
            if (cartItem.quantity > 1) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Total : %.2f€".format(cartItem.dish.price * cartItem.quantity),
                    fontSize = 13.sp,
                    color    = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}