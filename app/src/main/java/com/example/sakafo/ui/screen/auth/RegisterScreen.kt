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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
private val Cream         = Color(0xFFFFF8F3)
private val TextGray      = Color(0xFF7A7A7A)
private val OrbColor      = Color(0xFFFFE0CC)

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit = {},
    onVerifyEmail: (email: String) -> Unit,
    onSuccess: () -> Unit = {}
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    var name            by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    // ✅ phone stocke les chiffres bruts (ex: "0341234567")
    var phone           by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError      by remember { mutableStateOf("") }
    var apiError        by remember { mutableStateOf("") }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val headerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutCubic), label = "headerAlpha"
    )
    val headerOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else -60f,
        animationSpec = tween(600, easing = EaseOutCubic), label = "headerOffset"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, delayMillis = 200, easing = EaseOutCubic), label = "cardAlpha"
    )
    val cardOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 80f,
        animationSpec = tween(700, delayMillis = 200, easing = EaseOutCubic), label = "cardOffset"
    )

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.RegisterSuccess -> {
                val state = authState as AuthState.RegisterSuccess
                viewModel.resetState()
                onVerifyEmail(state.email)
            }
            is AuthState.Error -> {
                apiError = (authState as AuthState.Error).message
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
        AnimatedOrbs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // ── Bouton retour ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = headerAlpha; translationY = headerOffsetY }
            ) {
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

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = headerAlpha; translationY = headerOffsetY }
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
                    Spacer(Modifier.height(20.dp))
                    Text("Create\naccount", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                        color = DeepBrown, lineHeight = 42.sp, letterSpacing = (-0.5).sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Sign up to start ordering 🍽️", fontSize = 15.sp, color = TextGray)
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Card formulaire ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = cardAlpha; translationY = cardOffsetY }
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        // Nom
                        SakafoField(
                            "FULL NAME", name, Icons.Default.Person, "John Doe"
                        ) { name = it }

                        Spacer(Modifier.height(16.dp))

                        // Email
                        SakafoField(
                            "EMAIL", email, Icons.Default.Email,
                            "you@example.com", KeyboardType.Email
                        ) { email = it }

                        Spacer(Modifier.height(16.dp))

                        // ✅ Téléphone Madagascar avec indicatif + drapeau
                        MadagascarPhoneField(
                            value         = phone,
                            onValueChange = { phone = it },
                            modifier      = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        // Mot de passe
                        SakafoPasswordField(
                            "PASSWORD", password, passwordVisible,
                            "Min. 6 characters",
                            onToggle = { passwordVisible = !passwordVisible }
                        ) { password = it }

                        Spacer(Modifier.height(20.dp))

                        // Erreur locale
                        if (localError.isNotEmpty()) {
                            ErrorBanner(message = localError)
                            Spacer(Modifier.height(12.dp))
                        }

                        // Erreur API
                        if (apiError.isNotEmpty()) {
                            ErrorBanner(message = apiError)
                            Spacer(Modifier.height(12.dp))
                        }

                        SignUpButton(
                            isLoading = authState is AuthState.Loading,
                            onClick = {
                                apiError = ""
                                localError = when {
                                    name.isBlank()                  -> "Le nom est requis"
                                    email.isBlank()                 -> "L'email est requis"
                                    phone.isBlank()                 -> "Le téléphone est requis"
                                    // ✅ Validation numéro malgache
                                    !isMadagascarPhoneValid(phone)  -> "Numéro invalide (ex: 034 12 345 67)"
                                    password.isBlank()              -> "Le mot de passe est requis"
                                    password.length < 6             -> "Mot de passe trop court (min. 6)"
                                    else                            -> ""
                                }
                                if (localError.isEmpty()) {
                                    // ✅ Envoie le numéro complet +261XXXXXXXXX à l'API
                                    viewModel.register(
                                        name     = name,
                                        email    = email,
                                        password = password,
                                        phone    = getFullMadagascarPhone(phone)
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.graphicsLayer { alpha = cardAlpha },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", fontSize = 14.sp, color = TextGray)
                Text("Sign in", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = OrangePrimary, modifier = Modifier.clickable { onBackClick() })
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Composants utilitaires ────────────────────────────────────────────────────

@Composable
private fun SakafoField(
    label: String, value: String, icon: ImageVector, placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text, onChange: (String) -> Unit
) {
    val isFocused = value.isNotEmpty()
    Column {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
            color = if (isFocused) OrangePrimary else TextGray, letterSpacing = 1.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color(0xFFCCC5BE), fontSize = 14.sp) },
            leadingIcon = {
                Icon(icon, null, tint = if (isFocused) OrangePrimary else Color(0xFFCCC5BE),
                    modifier = Modifier.size(20.dp))
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true, shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary, unfocusedBorderColor = Color(0xFFE8E0DA),
                focusedContainerColor = OrangePrimary.copy(alpha = 0.04f),
                unfocusedContainerColor = Cream, cursorColor = OrangePrimary
            )
        )
    }
}

@Composable
private fun SakafoPasswordField(
    label: String, value: String, visible: Boolean,
    placeholder: String, onToggle: () -> Unit, onChange: (String) -> Unit
) {
    val isFocused = value.isNotEmpty()
    Column {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
            color = if (isFocused) OrangePrimary else TextGray, letterSpacing = 1.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color(0xFFCCC5BE), fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Lock, null, tint = if (isFocused) OrangePrimary else Color(0xFFCCC5BE),
                    modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                IconButton(onClick = onToggle) {
                    Icon(if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        "Toggle", tint = if (isFocused) OrangePrimary else Color(0xFFCCC5BE),
                        modifier = Modifier.size(20.dp))
                }
            },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary, unfocusedBorderColor = Color(0xFFE8E0DA),
                focusedContainerColor = OrangePrimary.copy(alpha = 0.04f),
                unfocusedContainerColor = Cream, cursorColor = OrangePrimary
            )
        )
    }
}

@Composable
private fun ErrorBanner(message: String) {
    val scale by animateFloatAsState(1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "errorScale")
    Row(
        modifier = Modifier.fillMaxWidth().scale(scale)
            .clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFEDED))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Warning, null, tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(message, color = Color(0xFFE53935), fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SignUpButton(isLoading: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        if (isLoading) 0.97f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "btnScale"
    )
    Button(
        onClick = onClick, enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(56.dp).scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OrangePrimary, contentColor = Color.White,
            disabledContainerColor = OrangePrimary.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(8.dp, pressedElevation = 3.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp))
        } else {
            Text("Create Account", fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp, letterSpacing = 0.5.sp)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun AnimatedOrbs() {
    val transition = rememberInfiniteTransition(label = "orb")
    val offset by transition.animateFloat(
        -25f, 25f,
        infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "orbOffset"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(OrbColor.copy(alpha = 0.5f), size.width * 0.6f,
            Offset(size.width * 0.88f, size.height * 0.12f + offset))
        drawCircle(OrbColor.copy(alpha = 0.3f), size.width * 0.4f,
            Offset(size.width * 0.08f, size.height * 0.8f - offset * 0.5f))
    }
}

private val EaseOutCubic  = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)