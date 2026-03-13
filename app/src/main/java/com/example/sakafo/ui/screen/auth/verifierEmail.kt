package com.example.sakafo.ui.screen.auth

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sakafo.viewmodel.AuthState
import com.example.sakafo.viewmodel.AuthViewModel

private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight   = Color(0xFFFF9A5C)
private val OrangeDark    = Color(0xFFE84E0F)
private val DeepBrown     = Color(0xFF1A0A00)
private val WarmWhite     = Color(0xFFFFFBF7)
private val TextGray      = Color(0xFF7A7A7A)
private val OrbColor      = Color(0xFFFFE0CC)

@Composable
fun VerifyEmailScreen(
    viewModel: AuthViewModel,
    email: String,                  // 👈 reçu depuis SignUpScreen via RegisterSuccess
    onSuccess: () -> Unit,          // → vers LoginScreen
    onBackClick: () -> Unit = {}
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    var code       by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf("") }

    // ── Animations ────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)),
        label = "alpha"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 60f,
        animationSpec = tween(600, easing = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)),
        label = "offsetY"
    )

    // ── Gestion états API ─────────────────────
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.VerifySuccess -> {
                viewModel.resetState()
                onSuccess() // → LoginScreen
            }
            is AuthState.Error -> {
                localError = (authState as AuthState.Error).message
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite)
    ) {
        // Orbes animés (même style que SignUpScreen)
        val transition = rememberInfiniteTransition(label = "orb")
        val orbOffset by transition.animateFloat(
            -25f, 25f,
            infiniteRepeatable(tween(4000), RepeatMode.Reverse),
            label = "orbOffset"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(OrbColor.copy(alpha = 0.5f), size.width * 0.6f,
                Offset(size.width * 0.88f, size.height * 0.12f + orbOffset))
            drawCircle(OrbColor.copy(alpha = 0.3f), size.width * 0.4f,
                Offset(size.width * 0.08f, size.height * 0.8f - orbOffset * 0.5f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .graphicsLayer { this.alpha = alpha; translationY = offsetY },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // ── Bouton retour ─────────────────
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary.copy(alpha = 0.12f))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                        tint = OrangePrimary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Logo ──────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(OrangeLight, OrangeDark),
                                start = Offset(0f, 0f), end = Offset(80f, 80f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocalDining, null,
                        tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text("sakafo", fontSize = 20.sp, fontWeight = FontWeight.Black,
                    color = DeepBrown, letterSpacing = (-0.5).sp)
                Text(".", fontSize = 20.sp, fontWeight = FontWeight.Black, color = OrangePrimary)
            }

            Spacer(Modifier.height(32.dp))

            // ── Icône email ───────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("📧", fontSize = 36.sp)
            }

            Spacer(Modifier.height(24.dp))

            Text("Vérifier votre email", fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold, color = DeepBrown,
                textAlign = TextAlign.Center)

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Un code à 6 chiffres a été envoyé à\n$email",
                fontSize = 14.sp, color = TextGray,
                textAlign = TextAlign.Center, lineHeight = 20.sp
            )

            Spacer(Modifier.height(40.dp))

            // ── Card avec input ───────────────
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    Text("CODE OTP", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (code.isNotEmpty()) OrangePrimary else TextGray,
                        letterSpacing = 1.sp)

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = code,
                        onValueChange = {
                            if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                                code = it
                                localError = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("_ _ _ _ _ _", color = Color(0xFFCCC5BE),
                                fontSize = 20.sp, letterSpacing = 8.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 8.sp,
                            textAlign = TextAlign.Center,
                            color = DeepBrown
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color(0xFFE8E0DA),
                            focusedContainerColor = OrangePrimary.copy(alpha = 0.04f),
                            unfocusedContainerColor = Color(0xFFFFF8F3),
                            cursorColor = OrangePrimary
                        )
                    )

                    // Erreur
                    if (localError.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFEDED))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null,
                                tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(localError, color = Color(0xFFE53935),
                                fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Bouton Vérifier
                    val btnScale by animateFloatAsState(
                        if (authState is AuthState.Loading) 0.97f else 1f,
                        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "btnScale"
                    )
                    Button(
                        onClick = {
                            localError = ""
                            viewModel.verifyEmail(email, code)
                        },
                        enabled = authState !is AuthState.Loading && code.length == 6,
                        modifier = Modifier.fillMaxWidth().height(56.dp).scale(btnScale),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            contentColor = Color.White,
                            disabledContainerColor = OrangePrimary.copy(alpha = 0.5f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(8.dp)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = Color.White,
                                strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                        } else {
                            Text("Vérifier", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                                modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Renvoyer le code ──────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pas reçu le code ? ", fontSize = 14.sp, color = TextGray)
                Text(
                    text = "Renvoyer",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    modifier = Modifier.clickable {
                        // TODO: appeler register à nouveau pour renvoyer l'OTP
                    }
                )
            }
        }
    }
}