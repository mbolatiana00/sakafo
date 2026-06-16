package com.example.sakafo.ui.screen.auth

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakafo.viewmodel.ForgotPasswordViewModel

// ─────────────────────────────────────────────
// Palette — identique LoginScreen
// ─────────────────────────────────────────────
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight   = Color(0xFFFF9A5C)
private val OrangeDark    = Color(0xFFE84E0F)
private val DeepBrown     = Color(0xFF1A0A00)
private val WarmWhite     = Color(0xFFFFFBF7)
private val Cream         = Color(0xFFFFF8F3)
private val TextGray      = Color(0xFF7A7A7A)
private val OrbColor      = Color(0xFFFFE0CC)

// ─────────────────────────────────────────────
// ForgotPasswordScreen
// ─────────────────────────────────────────────
@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onNavigateBack: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // ── Animations d'entrée ───────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val headerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "headerAlpha"
    )
    val headerOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else -60f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "headerOffset"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, delayMillis = 200, easing = EaseOutCubic),
        label = "cardAlpha"
    )
    val cardOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 80f,
        animationSpec = tween(700, delayMillis = 200, easing = EaseOutCubic),
        label = "cardOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite)
    ) {
        // Orbes décoratifs
        ForgotPasswordOrbs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // ── Bouton retour ─────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = headerAlpha
                        translationY = headerOffsetY
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary.copy(alpha = 0.12f))
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OrangePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Logo + titre ──────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = headerAlpha
                        translationY = headerOffsetY
                    }
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(OrangeLight, OrangeDark),
                                        start = Offset(0f, 0f),
                                        end = Offset(80f, 80f)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalDining,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "sakafo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = DeepBrown,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = ".",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangePrimary
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = if (viewModel.step == 1) "Forgot\npassword? 🔒" else "Verify &\nreset 🔑",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepBrown,
                        lineHeight = 42.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (viewModel.step == 1)
                            "Enter your email to receive a reset code"
                        else
                            "Enter the code sent to ${viewModel.email}",
                        fontSize = 15.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Carte formulaire ──────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = cardAlpha
                        translationY = cardOffsetY
                    }
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        if (viewModel.step == 1) {
                            // ── Étape 1 : Email ───────────────
                            ForgotField(
                                label = "Email",
                                value = viewModel.email,
                                icon = Icons.Default.Email,
                                placeholder = "you@example.com",
                                keyboardType = KeyboardType.Email
                            ) { viewModel.email = it }

                            Spacer(Modifier.height(8.dp))

                            // ── Message ────────────────────────
                            viewModel.message?.let {
                                Spacer(Modifier.height(8.dp))
                                ForgotInfoBanner(message = it)
                                Spacer(Modifier.height(12.dp))
                            }

                            Spacer(Modifier.height(8.dp))

                            // ── Bouton ──────────────────────────
                            ForgotButton(
                                text = "Send Code",
                                isLoading = viewModel.isLoading,
                                onClick = { viewModel.sendOtp() }
                            )
                        } else {
                            // ── Étape 2 : Code + nouveau mdp ──
                            ForgotField(
                                label = "OTP Code",
                                value = viewModel.code,
                                icon = Icons.Default.Pin,
                                placeholder = "000000",
                                keyboardType = KeyboardType.Number
                            ) { viewModel.code = it }

                            Spacer(Modifier.height(16.dp))

                            ForgotPasswordField(
                                label = "New Password",
                                value = viewModel.newPassword,
                                visible = passwordVisible,
                                placeholder = "••••••••",
                                onToggle = { passwordVisible = !passwordVisible }
                            ) { viewModel.newPassword = it }

                            // ── Message ────────────────────────
                            viewModel.message?.let {
                                Spacer(Modifier.height(12.dp))
                                ForgotInfoBanner(message = it)
                            }

                            Spacer(Modifier.height(16.dp))

                            // ── Bouton ──────────────────────────
                            ForgotButton(
                                text = "Reset Password",
                                isLoading = viewModel.isLoading,
                                onClick = { viewModel.resetPassword(onNavigateBack) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Footer ────────────────────────
            Row(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .graphicsLayer { alpha = cardAlpha },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remembered your password? ",
                    fontSize = 14.sp,
                    color = TextGray
                )
                Text(
                    text = "Sign In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// Champ texte
// ─────────────────────────────────────────────
@Composable
private fun ForgotField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    val isFocused = value.isNotEmpty()
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) OrangePrimary else Color(0xFFE8E0DA),
        animationSpec = tween(300),
        label = "border"
    )

    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isFocused) OrangePrimary else TextGray,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = Color(0xFFCCC5BE), fontSize = 14.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFocused) OrangePrimary else Color(0xFFCCC5BE),
                    modifier = Modifier.size(20.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = Color(0xFFE8E0DA),
                focusedContainerColor = OrangePrimary.copy(alpha = 0.04f),
                unfocusedContainerColor = Cream,
                cursorColor = OrangePrimary
            )
        )
    }
}

// ─────────────────────────────────────────────
// Champ mot de passe
// ─────────────────────────────────────────────
@Composable
private fun ForgotPasswordField(
    label: String,
    value: String,
    visible: Boolean,
    placeholder: String,
    onToggle: () -> Unit,
    onChange: (String) -> Unit
) {
    val isFocused = value.isNotEmpty()

    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isFocused) OrangePrimary else TextGray,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = Color(0xFFCCC5BE), fontSize = 14.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isFocused) OrangePrimary else Color(0xFFCCC5BE),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (visible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle",
                        tint = if (isFocused) OrangePrimary else Color(0xFFCCC5BE),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = Color(0xFFE8E0DA),
                focusedContainerColor = OrangePrimary.copy(alpha = 0.04f),
                unfocusedContainerColor = Cream,
                cursorColor = OrangePrimary
            )
        )
    }
}

// ─────────────────────────────────────────────
// Bannière d'info / message
// ─────────────────────────────────────────────
@Composable
private fun ForgotInfoBanner(message: String) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "infoScale"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(OrangePrimary.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MarkEmailRead,
            contentDescription = null,
            tint = OrangePrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            color = OrangeDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────
// Bouton animé
// ─────────────────────────────────────────────
@Composable
private fun ForgotButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isLoading) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btnScale"
    )

    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OrangePrimary,
            contentColor = Color.White,
            disabledContainerColor = OrangePrimary.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 3.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────
// Orbes animés — cohérents avec LoginScreen
// ─────────────────────────────────────────────
@Composable
private fun ForgotPasswordOrbs() {
    val transition = rememberInfiniteTransition(label = "orb")
    val offset by transition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbOffset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = OrbColor.copy(alpha = 0.5f),
            radius = size.width * 0.55f,
            center = Offset(
                x = size.width * 0.9f,
                y = size.height * 0.10f + offset
            )
        )
        drawCircle(
            color = OrbColor.copy(alpha = 0.3f),
            radius = size.width * 0.38f,
            center = Offset(
                x = size.width * 0.05f,
                y = size.height * 0.82f - offset * 0.5f
            )
        )
        drawCircle(
            color = OrangePrimary.copy(alpha = 0.07f),
            radius = size.width * 0.25f,
            center = Offset(
                x = size.width * 0.5f,
                y = size.height * 0.95f + offset * 0.3f
            )
        )
    }
}

// ─────────────────────────────────────────────
// Easing — identique LoginScreen
// ─────────────────────────────────────────────
private val EaseOutCubic  = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)