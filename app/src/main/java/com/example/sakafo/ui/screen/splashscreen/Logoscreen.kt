package com.example.sakafo.ui.screen.splashscreen


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight   = Color(0xFFFF9A5C)
private val OrangeDark    = Color(0xFFE84E0F)
private val WarmWhite     = Color(0xFFFFFBF7)
private val DeepBrown     = Color(0xFF1A0A00)

@Composable
fun LogoScreen(onFinished: () -> Unit) {

    var started by remember { mutableStateOf(false) }

    // ── Cercle : pop avec bounce ───────────────────────────────────────────────
    val circleScale by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "circleScale"
    )

    // ── Texte : glisse depuis droite + fade ───────────────────────────────────
    val textOffsetX by animateFloatAsState(
        targetValue   = if (started) 0f else 60f,
        animationSpec = tween(550, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "textOffsetX"
    )

    val textAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(500, delayMillis = 200),
        label = "textAlpha"
    )

    // ── Pulse infini sur le cercle ────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.07f,
        animationSpec = infiniteRepeatable(
            animation  = tween(950, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // ── Déclenchement + navigation ────────────────────────────────────────────
    LaunchedEffect(Unit) {
        delay(80L)
        started = true
        delay(2400L)
        onFinished()
    }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(WarmWhite),
        contentAlignment = Alignment.Center
    ) {

        // Orbe décoratif fond
        Box(
            modifier = Modifier
                .size(360.dp)
                .scale(circleScale)
                .clip(CircleShape)
                .background(OrangePrimary.copy(alpha = 0.07f))
        )
        Box(
            modifier = Modifier
                .size(230.dp)
                .scale(circleScale)
                .clip(CircleShape)
                .background(OrangePrimary.copy(alpha = 0.05f))
        )

        // ── Logo ──────────────────────────────────────────────────────────────
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            // Cercle icône avec pulse
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .scale(circleScale * if (started) pulse else 1f)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(OrangeLight, OrangeDark),
                            start  = Offset(0f, 0f),
                            end    = Offset(100f, 100f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.LocalDining,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Texte animé
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.offset(x = textOffsetX.dp)
            ) {
                Text(
                    text          = "sakafo",
                    fontSize      = 40.sp,
                    fontWeight    = FontWeight.Black,
                    color         = DeepBrown.copy(alpha = textAlpha),
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text       = ".",
                    fontSize   = 40.sp,
                    fontWeight = FontWeight.Black,
                    color      = OrangePrimary.copy(alpha = textAlpha)
                )
            }
        }

        // ── Tagline bas de page ───────────────────────────────────────────────
        Text(
            text     = "Livraison rapide & savoureuse",
            fontSize = 13.sp,
            color    = Color(0xFF9A8880).copy(alpha = textAlpha),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )
    }
}