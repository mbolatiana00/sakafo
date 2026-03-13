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
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.TextButton
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sakafo.viewmodel.AuthState
import com.example.sakafo.viewmodel.AuthViewModel

// ─────────────────────────────────────────────
// Palette — identique SplashScreen & SignUpScreen
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
// LoginScreen
// ─────────────────────────────────────────────
@Composable
fun LoginScreenAuth(
    onBackClick: () -> Unit = {},
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    viewModel: AuthViewModel,
    onSuccess: () -> Unit,
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError      by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

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
    val footerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, delayMillis = 400, easing = EaseOutCubic),
        label = "footerAlpha"
    )

    // ── Succès ────────────────────────────────
    LaunchedEffect(authState) {
        if (authState is AuthState.LoginSuccess) {
            onSuccess()
            viewModel.resetState()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite)
    ) {
        // Orbes décoratifs
        LoginOrbs()

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
                        .clickable { onBackClick() },
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
                        text = "Welcome\nback 👋",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepBrown,
                        lineHeight = 42.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Sign in to your account",
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

                        // ── Champ Email ───────────────────────
                        LoginField(
                            label = "Email",
                            value = email,
                            icon = Icons.Default.Email,
                            placeholder = "you@example.com",
                            keyboardType = KeyboardType.Email
                        ) { email = it }

                        Spacer(Modifier.height(16.dp))

                        // ── Champ Password ────────────────────
                        LoginPasswordField(
                            label = "Password",
                            value = password,
                            visible = passwordVisible,
                            placeholder = "••••••••",
                            onToggle = { passwordVisible = !passwordVisible }
                        ) { password = it }

                        // ── Forgot Password ───────────────────
                        Box(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = onForgotPasswordClick,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    color = OrangePrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // ── Erreur ────────────────────────────
                        val errorMessage = when {
                            localError.isNotEmpty() -> localError
                            authState is AuthState.Error -> (authState as AuthState.Error).message
                            else -> ""
                        }
                        if (errorMessage.isNotEmpty()) {
                            LoginErrorBanner(message = errorMessage)
                            Spacer(Modifier.height(12.dp))
                        }

                        // ── Bouton Login ──────────────────────
                        LoginButton(
                            isLoading = authState is AuthState.Loading,
                            onClick = {
                                localError = when {
                                    email.isBlank()    -> "L'email est requis"
                                    password.isBlank() -> "Le mot de passe est requis"
                                    else               -> ""
                                }
                                if (localError.isEmpty()) {
                                    viewModel.login(email, password)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Footer ────────────────────────
            Row(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .graphicsLayer { alpha = footerAlpha },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = TextGray
                )
                Text(
                    text = "Sign Up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// Champ texte Login
// ─────────────────────────────────────────────
@Composable
private fun LoginField(
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
// Champ mot de passe Login
// ─────────────────────────────────────────────
@Composable
private fun LoginPasswordField(
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
// Bannière d'erreur
// ─────────────────────────────────────────────
@Composable
private fun LoginErrorBanner(message: String) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "errorScale"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFEDED))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFE53935),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            color = Color(0xFFE53935),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────
// Bouton Login animé
// ─────────────────────────────────────────────
@Composable
private fun LoginButton(
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
                text = "Sign In",
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
// Orbes animés — cohérents avec les autres screens
// ─────────────────────────────────────────────
@Composable
private fun LoginOrbs() {
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
        // Orbe haut-droite (plus grand)
        drawCircle(
            color = OrbColor.copy(alpha = 0.5f),
            radius = size.width * 0.55f,
            center = Offset(
                x = size.width * 0.9f,
                y = size.height * 0.10f + offset
            )
        )
        // Orbe bas-gauche
        drawCircle(
            color = OrbColor.copy(alpha = 0.3f),
            radius = size.width * 0.38f,
            center = Offset(
                x = size.width * 0.05f,
                y = size.height * 0.82f - offset * 0.5f
            )
        )
        // Orbe accent centre-bas
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
// Easing — identique aux autres screens
// ─────────────────────────────────────────────
private val EaseOutCubic  = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)