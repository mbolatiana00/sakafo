package com.example.sakafo.ui.screen.auth


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Palette ───────────────────────────────────────────────────────────────────
private val OrangePrimary = Color(0xFFFF6B35)
private val Cream         = Color(0xFFFFF8F3)
private val TextGray      = Color(0xFF7A7A7A)
private val GreenOk       = Color(0xFF4CAF50)
private val RedError      = Color(0xFFE53935)

// ── Validation numéro malgache ────────────────────────────────────────────────
// Formats valides Madagascar :
//   032 XX XXX XX  →  Airtel
//   033 XX XXX XX  →  Orange
//   034 XX XXX XX  →  Orange
//   038 XX XXX XX  →  Telma
//   020 XX XXX XX  →  fixe Antananarivo
// On accepte le numéro sans le +261 (l'indicatif est affiché séparément)
// Le numéro local commence toujours par 0 + 2 chiffres (3 chiffres au total)
// puis 7 chiffres → 10 chiffres au total

fun isMadagascarPhoneValid(phone: String): Boolean {
    val cleaned = phone.replace(" ", "").replace("-", "")
    // Accepte : 10 chiffres commençant par 03X ou 02X
    return cleaned.length == 10 &&
            cleaned.all { it.isDigit() } &&
            (cleaned.startsWith("03") || cleaned.startsWith("02"))
}

fun formatMadagascarPhone(input: String): String {
    // Formatte au fur et à mesure : 034 12 345 67
    val digits = input.filter { it.isDigit() }.take(10)
    return buildString {
        digits.forEachIndexed { i, c ->
            if (i == 3 || i == 5 || i == 8) append(' ')
            append(c)
        }
    }
}

// ── Composant principal ───────────────────────────────────────────────────────
@Composable
fun MadagascarPhoneField(
    value: String,
    onValueChange: (String) -> Unit,    // retourne le numéro brut (sans espaces)
    modifier: Modifier = Modifier,
    showValidation: Boolean = true
) {
    val isFocused   = value.isNotEmpty()
    val isValid     = isMadagascarPhoneValid(value)
    val isError     = isFocused && value.length >= 10 && !isValid

    val borderColor by animateColorAsState(
        targetValue = when {
            isError       -> RedError
            isValid       -> GreenOk
            isFocused     -> OrangePrimary
            else          -> Color(0xFFE8E0DA)
        },
        animationSpec = tween(300),
        label = "phoneBorder"
    )

    Column(modifier = modifier) {

        // ── Label ─────────────────────────────────────────────────────────────
        Text(
            text          = "TÉLÉPHONE",
            fontSize      = 11.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = when {
                isError   -> RedError
                isValid   -> GreenOk
                isFocused -> OrangePrimary
                else      -> TextGray
            },
            letterSpacing = 1.sp
        )

        Spacer(Modifier.height(6.dp))

        // ── Champ avec préfixe +261 ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                .background(if (isFocused) OrangePrimary.copy(alpha = 0.04f) else Cream),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── Badge drapeau + indicatif ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Drapeau Madagascar (bandes blanc/rouge/vert) en SVG Canvas
                MadagascarFlag(modifier = Modifier.size(width = 24.dp, height = 16.dp))

                Spacer(Modifier.width(6.dp))

                Text(
                    text       = "+261",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF1A0A00)
                )
            }

            // Séparateur vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(Color(0xFFE0E0E0))
            )

            // ── Champ texte ───────────────────────────────────────────────────
            BasicPhoneInput(
                value         = formatMadagascarPhone(value),
                onValueChange = { raw ->
                    // Ne garde que les chiffres, max 10
                    val digits = raw.filter { it.isDigit() }.take(10)
                    onValueChange(digits)
                },
                modifier = Modifier.weight(1f)
            )

            // ── Icône validation ──────────────────────────────────────────────
            if (showValidation && isFocused) {
                Icon(
                    imageVector        = if (isValid) Icons.Default.CheckCircle
                    else Icons.Default.Warning,
                    contentDescription = null,
                    tint               = if (isValid) GreenOk else if (isError) RedError
                    else Color(0xFFCCC5BE),
                    modifier           = Modifier
                        .padding(end = 12.dp)
                        .size(20.dp)
                )
            }
        }

        // ── Message d'aide / erreur ───────────────────────────────────────────
        Spacer(Modifier.height(4.dp))

        when {
            isError -> Text(
                text      = "Format invalide — ex: 034 12 345 67",
                fontSize  = 11.sp,
                color     = RedError,
                fontWeight = FontWeight.Medium
            )
            isValid -> Text(
                text     = "✓ Numéro valide (Madagascar)",
                fontSize = 11.sp,
                color    = GreenOk
            )
            isFocused -> Text(
                text     = "Commencez par 032, 033, 034, 038 ou 020",
                fontSize = 11.sp,
                color    = TextGray
            )
        }
    }
}

// ── Input basique sans décoration OutlinedTextField ───────────────────────────
@Composable
private fun BasicPhoneInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value         = value,
        onValueChange = onValueChange,
        modifier      = modifier,
        placeholder   = {
            Text(
                "034 XX XXX XX",
                color    = Color(0xFFCCC5BE),
                fontSize = 14.sp
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine      = true,
        colors          = TextFieldDefaults.colors(
            focusedContainerColor   = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor   = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor  = Color.Transparent,
            cursorColor             = OrangePrimary
        )
    )
}

// ── Drapeau Madagascar dessiné en Compose ─────────────────────────────────────
// Drapeau : bande verticale blanche à gauche, puis rouge en haut, vert en bas
@Composable
fun MadagascarFlag(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Bande blanche gauche (1/3 largeur)
        drawRect(
            color  = Color.White,
            size   = androidx.compose.ui.geometry.Size(w / 3f, h)
        )

        // Bande rouge (haut-droite, 2/3 largeur, moitié hauteur)
        drawRect(
            color  = Color(0xFFFC3D32),
            topLeft = androidx.compose.ui.geometry.Offset(w / 3f, 0f),
            size   = androidx.compose.ui.geometry.Size(w * 2f / 3f, h / 2f)
        )

        // Bande verte (bas-droite, 2/3 largeur, moitié hauteur)
        drawRect(
            color  = Color(0xFF007E3A),
            topLeft = androidx.compose.ui.geometry.Offset(w / 3f, h / 2f),
            size   = androidx.compose.ui.geometry.Size(w * 2f / 3f, h / 2f)
        )

        // Bordure fine autour du drapeau
        drawRect(
            color       = Color(0xFFE0E0E0),
            style       = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
        )
    }
}

// ── Fonction utilitaire pour récupérer le numéro complet avec indicatif ───────
fun getFullMadagascarPhone(localNumber: String): String {
    val cleaned = localNumber.replace(" ", "").replace("-", "")
    // Remplace le 0 initial par +261
    return if (cleaned.startsWith("0")) "+261${cleaned.drop(1)}"
    else "+261$cleaned"
}