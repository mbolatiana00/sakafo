package com.example.sakafo.ui.screen.splashscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// Palette de couleurs
// ─────────────────────────────────────────────
private val OrangePrimary   = Color(0xFFFF6B35)
private val OrangeLight     = Color(0xFFFF9A5C)
private val OrangeDark      = Color(0xFFE84E0F)
private val Cream           = Color(0xFFFFF8F3)
private val WarmWhite       = Color(0xFFFFFBF7)
private val DeepBrown       = Color(0xFF1A0A00)
private val TextGray        = Color(0xFF7A7A7A)
private val DotInactive     = Color(0xFFE0D5CC)

// ─────────────────────────────────────────────
// Modèle de données
// ─────────────────────────────────────────────
data class OnboardingPage(
    val tag: String,
    val title: String,
    val highlight: String,   // mot mis en couleur dans le titre
    val description: String,
    val icon: ImageVector,
    val bgShape: Color,      // couleur de l'orbe décoratif
)

val onboardingPages = listOf(
    OnboardingPage(
        tag = "DISCOVER",
        title = "All your\nfavorites",
        highlight = "favorites",
        description = "Order from the best local restaurants with easy, on-demand delivery.",
        icon = Icons.Default.Favorite,
        bgShape = Color(0xFFFFE0CC),
    ),
    OnboardingPage(
        tag = "CHEF'S TABLE",
        title = "Expert\nchefs nearby",
        highlight = "chefs",
        description = "Choose your personal chef and enjoy restaurant-quality meals at home.",
        icon = Icons.Default.Restaurant,
        bgShape = Color(0xFFFFD6B8),
    ),
    OnboardingPage(
        tag = "FAST & FREE",
        title = "Free delivery,\nalways",
        highlight = "Free",
        description = "Enjoy zero delivery fees on every order, every day, no minimum spend.",
        icon = Icons.Default.DeliveryDining,
        bgShape = Color(0xFFFFCBA4),
    ),
)

// ─────────────────────────────────────────────
// Composable principal
// ─────────────────────────────────────────────
@Composable
fun SplashScreen(onGetStartedClick: () -> Unit) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    // Animation du bouton GET STARTED
    val btnScale by animateFloatAsState(
        targetValue = if (currentPage == onboardingPages.lastIndex) 1f else 0.96f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btnScale"
    )

    // Auto-scroll
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000L)
            val next = (pagerState.currentPage + 1) % onboardingPages.size
            pagerState.animateScrollToPage(next)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite)
    ) {
        // Orbe décoratif en arrière-plan
        AnimatedBackgroundOrb(
            color = onboardingPages[currentPage].bgShape,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo ────────────────────────────────
            Spacer(Modifier.height(56.dp))
            SakafoLogo()
            Spacer(Modifier.height(36.dp))

            // ── Pager ────────────────────────────────
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                userScrollEnabled = true
            ) { page ->
                OnboardingPageContent(page = onboardingPages[page])
            }

            // ── Navigation (skip | dots | next) ─────
            Spacer(Modifier.height(24.dp))
            NavigationRow(
                currentPage = currentPage,
                total = onboardingPages.size,
                onSkip = {
                    scope.launch {
                        pagerState.animateScrollToPage(onboardingPages.lastIndex)
                    }
                },
                onNext = {
                    scope.launch {
                        if (currentPage < onboardingPages.lastIndex)
                            pagerState.animateScrollToPage(currentPage + 1)
                    }
                }
            )

            Spacer(Modifier.weight(1f))

            // ── Bouton CTA ───────────────────────────
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .scale(btnScale),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Lien connexion ───────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 13.sp,
                    color = TextGray
                )
                Text(
                    text = "Sign in",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    modifier = Modifier.clickable { /* navigation */ }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Logo
// ─────────────────────────────────────────────
@Composable
private fun SakafoLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Icône ronde
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(OrangeLight, OrangeDark),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalDining,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = "sakafo",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = DeepBrown,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = ".",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = OrangePrimary
        )
    }
}

// ─────────────────────────────────────────────
// Contenu d'une page d'onboarding
// ─────────────────────────────────────────────
@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    // Animation d'entrée
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "iconScale"
    )

    // Pulse de l'icône
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ── Illustration ──────────────────────────
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            // Cercle extérieur dégradé
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                page.bgShape,
                                page.bgShape.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
            // Cercle intérieur
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }
            // Badge tag
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = (-8).dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(OrangePrimary)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = page.tag,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(Modifier.height(44.dp))

        // ── Titre ─────────────────────────────────
        // On met en orange le mot "highlight"
        val parts = page.title.split(page.highlight)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Approche simple : titre complet en noir, highlight en orange
            // Utilisation d'un AnnotatedString inline
           buildAnnotatedString {
                page.title.split("\n").forEachIndexed { lineIdx, line ->
                    if (lineIdx > 0) append("\n")
                    val words = line.split(" ")
                    words.forEachIndexed { wi, word ->
                        if (wi > 0) append(" ")
                        val isHighlight = word.contains(page.highlight, ignoreCase = true)
                        if (isHighlight) {
                         withStyle(
                                SpanStyle(
                                    color = OrangePrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            ) { append(word) }
                        } else {
                           withStyle(
                               SpanStyle(
                                    color = DeepBrown,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            ) { append(word) }
                        }
                    }
                }
            }.let { annotated ->
                Text(
                    text = annotated,
                    fontSize = 34.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 42.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Description ───────────────────────────
        Text(
            text = page.description,
            fontSize = 15.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            lineHeight = 23.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

// ─────────────────────────────────────────────
// Navigation row
// ─────────────────────────────────────────────
@Composable
private fun NavigationRow(
    currentPage: Int,
    total: Int,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Skip
        Text(
            text = if (currentPage < total - 1) "Skip" else "",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextGray,
            modifier = Modifier
                .clickable(enabled = currentPage < total - 1) { onSkip() }
                .padding(8.dp)
                .width(52.dp)
        )

        // Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(total) { index ->
                val isActive = index == currentPage
                val width by animateDpAsState(
                    targetValue = if (isActive) 28.dp else 8.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "dotWidth"
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (isActive) OrangePrimary else DotInactive
                        )
                )
            }
        }

        // Next
        if (currentPage < total - 1) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary)
                    .clickable { onNext() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Spacer(Modifier.width(44.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Orbe animé en arrière-plan
// ─────────────────────────────────────────────
@Composable
private fun AnimatedBackgroundOrb(color: Color, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "orb")
    val offset by transition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbOffset"
    )
    val animColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(600),
        label = "orbColor"
    )

    Canvas(modifier = modifier) {
        drawCircle(
            color = animColor.copy(alpha = 0.45f),
            radius = size.width * 0.65f,
            center = Offset(
                x = size.width * 0.85f,
                y = size.height * 0.18f + offset
            )
        )
        drawCircle(
            color = animColor.copy(alpha = 0.25f),
            radius = size.width * 0.45f,
            center = Offset(
                x = size.width * 0.1f,
                y = size.height * 0.75f - offset * 0.5f
            )
        )
    }
}

// ─────────────────────────────────────────────
// Easing personnalisé
// ─────────────────────────────────────────────
private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)