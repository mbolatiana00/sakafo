package com.example.sakafo.ui.screen.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Palette ───────────────────────────────────────────────────────────────────
private val OrangePrimary  = Color(0xFFFF6B35)
private val OrangeLight    = Color(0xFFFF9A5C)
private val OrangeDark     = Color(0xFFE84E0F)
private val DeepBrown      = Color(0xFF1A0A00)
private val WarmWhite      = Color(0xFFFFFBF7)
private val TextGray       = Color(0xFF9A8880)
private val UnreadBg       = Color(0xFFFFF3EE)
private val SuccessGreen   = Color(0xFF2ECC71)
private val InfoBlue       = Color(0xFF3498DB)
private val WarningYellow  = Color(0xFFF39C12)

// ── Types de notification ─────────────────────────────────────────────────────
enum class NotifType { ORDER, PROMO, DELIVERY, SYSTEM }

data class NotificationItem(
    val id: Int,
    val type: NotifType,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)

// ── Données fictives ──────────────────────────────────────────────────────────
private val fakeNotifications = listOf(
    NotificationItem(
        id = 1, type = NotifType.ORDER,
        title = "Commande confirmée ✅",
        message = "Votre commande #1042 a été confirmée et est en cours de préparation.",
        time = "Il y a 2 min", isRead = false
    ),
    NotificationItem(
        id = 2, type = NotifType.DELIVERY,
        title = "Livreur en route 🛵",
        message = "Jean-Pierre est en route vers votre adresse. Temps estimé : 15 min.",
        time = "Il y a 10 min", isRead = false
    ),
    NotificationItem(
        id = 3, type = NotifType.PROMO,
        title = "Offre spéciale 🎉",
        message = "20% de réduction sur toutes les commandes ce soir ! Code : SAKAFO20",
        time = "Il y a 1h", isRead = false
    ),
    NotificationItem(
        id = 4, type = NotifType.ORDER,
        title = "Commande livrée 🎊",
        message = "Votre commande #1038 a été livrée. Bon appétit DurLP@sswor1d!",
        time = "Il y a 3h", isRead = true
    ),
    NotificationItem(
        id = 5, type = NotifType.SYSTEM,
        title = "Bienvenue sur Sakafo !",
        message = "Découvrez les meilleurs restaurants près de chez vous. Commandez maintenant !",
        time = "Hier", isRead = true
    ),
    NotificationItem(
        id = 6, type = NotifType.PROMO,
        title = "Nouveau restaurant 🍜",
        message = "Le restaurant Chez Mama vient de rejoindre Sakafo. Essayez leurs spécialités !",
        time = "Hier", isRead = true
    ),
    NotificationItem(
        id = 7, type = NotifType.ORDER,
        title = "Commande annulée ❌",
        message = "Votre commande #1035 a été annulée. Le remboursement sera effectué sous 48h.",
        time = "Il y a 2 jours", isRead = true
    ),
    NotificationItem(
        id = 8, type = NotifType.DELIVERY,
        title = "Note votre livreur ⭐",
        message = "Comment s'est passée votre livraison avec Marc ? Donnez votre avis !",
        time = "Il y a 3 jours", isRead = true
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// NotificationScreen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit = {}
) {
    var notifications by remember { mutableStateOf(fakeNotifications) }
    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text       = "Notifications",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = DeepBrown
                        )
                        if (unreadCount > 0) {
                            Spacer(Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(OrangePrimary)
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = unreadCount.toString(),
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color.White
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.1f))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint     = OrangePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                notifications = notifications.map { it.copy(isRead = true) }
                            }
                        ) {
                            Text(
                                text       = "Tout lire",
                                color      = OrangePrimary,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmWhite)
            )
        },
        containerColor = WarmWhite
    ) { padding ->

        if (notifications.isEmpty()) {
            // ── État vide ──────────────────────────────────────────────────
            EmptyNotifications(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier       = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── Section Non lues ───────────────────────────────────────
                val unread = notifications.filter { !it.isRead }
                val read   = notifications.filter { it.isRead }

                if (unread.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Nouvelles", count = unread.size)
                    }
                    itemsIndexed(unread) { index, notif ->
                        AnimatedNotifCard(
                            notif   = notif,
                            index   = index,
                            onRead  = {
                                notifications = notifications.map {
                                    if (it.id == notif.id) it.copy(isRead = true) else it
                                }
                            },
                            onDelete = {
                                notifications = notifications.filter { it.id != notif.id }
                            }
                        )
                    }
                }

                // ── Section Lues ───────────────────────────────────────────
                if (read.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        SectionHeader(title = "Précédentes", count = read.size)
                    }
                    itemsIndexed(read) { index, notif ->
                        AnimatedNotifCard(
                            notif    = notif,
                            index    = index + unread.size,
                            onRead   = {},
                            onDelete = {
                                notifications = notifications.filter { it.id != notif.id }
                            }
                        )
                    }
                }

                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SectionHeader
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = title,
            fontSize   = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = TextGray,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text       = "($count)",
            fontSize   = 12.sp,
            color      = TextGray.copy(alpha = 0.6f)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AnimatedNotifCard
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AnimatedNotifCard(
    notif: NotificationItem,
    index: Int,
    onRead: () -> Unit,
    onDelete: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 60L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + slideInVertically(
            tween(300),
            initialOffsetY = { it / 3 }
        )
    ) {
        NotifCard(notif = notif, onRead = onRead, onDelete = onDelete)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NotifCard
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NotifCard(
    notif: NotificationItem,
    onRead: () -> Unit,
    onDelete: () -> Unit
) {
    val bgColor = if (!notif.isRead) UnreadBg else Color.White
    val (icon, iconBg) = notifIconAndColor(notif.type)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (!notif.isRead) onRead() },
            shape     = RoundedCornerShape(18.dp),
            colors    = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (!notif.isRead) 4.dp else 1.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // ── Icône type ─────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBg.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint     = iconBg,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                // ── Contenu ────────────────────────────────────────────────
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = notif.title,
                            fontSize   = 14.sp,
                            fontWeight = if (!notif.isRead) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color      = DeepBrown,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        // Dot non lu
                        if (!notif.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(OrangeLight, OrangeDark)
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text     = notif.message,
                        fontSize = 13.sp,
                        color    = if (!notif.isRead) DeepBrown.copy(alpha = 0.75f) else TextGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = notif.time,
                            fontSize   = 11.sp,
                            color      = TextGray,
                            fontWeight = FontWeight.Medium
                        )
                        // Bouton supprimer
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEDED))
                                .clickable { onDelete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Supprimer",
                                tint     = Color(0xFFE53935),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Barre orange gauche pour non lues
        if (!notif.isRead) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(
                        Brush.verticalGradient(colors = listOf(OrangeLight, OrangeDark))
                    )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Icône et couleur selon le type
// ─────────────────────────────────────────────────────────────────────────────
private fun notifIconAndColor(type: NotifType): Pair<ImageVector, Color> = when (type) {
    NotifType.ORDER    -> Icons.Default.ShoppingBag   to Color(0xFFFF6B35)
    NotifType.DELIVERY -> Icons.Default.DeliveryDining to Color(0xFF2ECC71)
    NotifType.PROMO    -> Icons.Default.LocalOffer    to Color(0xFF3498DB)
    NotifType.SYSTEM   -> Icons.Default.Info          to Color(0xFF9B59B6)
}

// ─────────────────────────────────────────────────────────────────────────────
// État vide
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyNotifications(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "bell")
    val bellScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "bellScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(OrangePrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint     = OrangePrimary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text       = "Aucune notification",
            fontSize   = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = DeepBrown
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text      = "Vous êtes à jour ! Revenez plus tard\npour voir vos nouvelles notifications.",
            fontSize  = 14.sp,
            color     = TextGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}