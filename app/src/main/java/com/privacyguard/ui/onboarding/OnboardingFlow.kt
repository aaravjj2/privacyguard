package com.privacyguard.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privacyguard.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

// ---------------------------------------------------------------------------
// Data model for onboarding pages
// ---------------------------------------------------------------------------

/**
 * Represents a single page in the onboarding flow.
 *
 * @param id Unique page identifier.
 * @param title Headline text.
 * @param description Body text with explanatory content.
 * @param icon Material icon for fallback display.
 * @param iconTint Tint color for the icon.
 * @param illustrationType The type of canvas-drawn illustration.
 * @param primaryColor Primary color for the illustration.
 * @param secondaryColor Secondary color for the illustration.
 * @param features Optional list of feature bullet points.
 * @param actionLabel Optional label for a page-specific action button.
 */
data class OnboardingPage(
    val id: OnboardingPageId,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconTint: Color,
    val illustrationType: IllustrationType = IllustrationType.SHIELD,
    val primaryColor: Color = TrustBlue,
    val secondaryColor: Color = TrustBlueLight,
    val features: List<OnboardingFeature> = emptyList(),
    val actionLabel: String = ""
)

/**
 * Page identifiers for the onboarding flow.
 */
enum class OnboardingPageId {
    WELCOME,
    HOW_IT_WORKS,
    PRIVACY_PROMISE,
    PERMISSIONS,
    CONFIGURATION,
    GETTING_STARTED
}

/**
 * Types of canvas-drawn illustrations for each page.
 */
enum class IllustrationType {
    SHIELD,
    SCANNING,
    LOCK,
    PERMISSIONS,
    GEAR,
    ROCKET
}

/**
 * A feature bullet point with icon and text.
 */
data class OnboardingFeature(
    val icon: ImageVector,
    val text: String,
    val color: Color = TrustBlue
)

// ---------------------------------------------------------------------------
// All onboarding pages definition
// ---------------------------------------------------------------------------

private val onboardingPages = listOf(
    // Page 1: Welcome
    OnboardingPage(
        id = OnboardingPageId.WELCOME,
        title = "Welcome to PrivacyGuard",
        description = "Your personal AI-powered privacy shield. PrivacyGuard detects " +
                "sensitive data before it leaks -- credit cards, passwords, SSNs, and more.\n\n" +
                "Everything runs on your device. Nothing ever leaves your phone.",
        icon = Icons.Default.Shield,
        iconTint = TrustBlue,
        illustrationType = IllustrationType.SHIELD,
        primaryColor = TrustBlue,
        secondaryColor = TrustBlueLight,
        features = listOf(
            OnboardingFeature(Icons.Default.Security, "Real-time PII detection", TrustBlue),
            OnboardingFeature(Icons.Default.PhoneAndroid, "100% on-device processing", ProtectionActive),
            OnboardingFeature(Icons.Default.Speed, "Sub-second analysis", AlertOrange)
        )
    ),

    // Page 2: How It Works
    OnboardingPage(
        id = OnboardingPageId.HOW_IT_WORKS,
        title = "How It Works",
        description = "PrivacyGuard uses a lightweight on-device AI model to analyze text " +
                "in real-time. When you copy something to your clipboard or type in a text field, " +
                "the model scans for sensitive patterns.\n\n" +
                "If personal data is detected, you get an instant alert with recommended actions.",
        icon = Icons.Default.Psychology,
        iconTint = TrustBlueLight,
        illustrationType = IllustrationType.SCANNING,
        primaryColor = TrustBlue,
        secondaryColor = ProtectionActive,
        features = listOf(
            OnboardingFeature(Icons.Default.ContentPaste, "Clipboard monitoring", TrustBlue),
            OnboardingFeature(Icons.Default.TextFields, "Text field scanning", TrustBlueLight),
            OnboardingFeature(Icons.Default.FilterAlt, "Regex pre-screening", AlertOrange),
            OnboardingFeature(Icons.Default.Memory, "On-device ML inference", ProtectionActive)
        )
    ),

    // Page 3: Privacy Promise
    OnboardingPage(
        id = OnboardingPageId.PRIVACY_PROMISE,
        title = "Our Privacy Promise",
        description = "We built PrivacyGuard to protect your privacy, not compromise it.\n\n" +
                "Your data never leaves your device. There are no servers, no cloud uploads, " +
                "no analytics, and no tracking. The AI model runs entirely on your phone's processor.\n\n" +
                "We cannot see your data. Period.",
        icon = Icons.Default.Lock,
        iconTint = ProtectionActive,
        illustrationType = IllustrationType.LOCK,
        primaryColor = ProtectionActive,
        secondaryColor = SuccessGreen,
        features = listOf(
            OnboardingFeature(Icons.Default.CloudOff, "No cloud connectivity", ProtectionActive),
            OnboardingFeature(Icons.Default.AnalyticsOutlined, "Zero analytics or tracking", SuccessGreen),
            OnboardingFeature(Icons.Default.StorageOutlined, "Local-only storage", TrustBlue),
            OnboardingFeature(Icons.Default.Code, "Open source model", TrustBlueLight)
        )
    ),

    // Page 4: Permissions
    OnboardingPage(
        id = OnboardingPageId.PERMISSIONS,
        title = "Permissions Needed",
        description = "PrivacyGuard needs a few permissions to protect you effectively. " +
                "Each permission serves a specific purpose and can be revoked at any time.",
        icon = Icons.Default.Security,
        iconTint = AlertOrange,
        illustrationType = IllustrationType.PERMISSIONS,
        primaryColor = AlertOrange,
        secondaryColor = SeverityMedium,
        features = listOf(
            OnboardingFeature(
                Icons.Default.Accessibility,
                "Accessibility Service -- monitor text fields across apps",
                TrustBlue
            ),
            OnboardingFeature(
                Icons.Default.Layers,
                "Overlay Permission -- show real-time alert banners",
                AlertOrange
            ),
            OnboardingFeature(
                Icons.Default.Notifications,
                "Notifications -- send detection alerts",
                ProtectionActive
            )
        ),
        actionLabel = "Grant Permissions"
    ),

    // Page 5: Configuration
    OnboardingPage(
        id = OnboardingPageId.CONFIGURATION,
        title = "Quick Configuration",
        description = "Customize PrivacyGuard to fit your needs. You can always change these " +
                "settings later from the Settings screen.\n\n" +
                "Choose what to monitor and how to be notified.",
        icon = Icons.Default.Tune,
        iconTint = TrustBlueLight,
        illustrationType = IllustrationType.GEAR,
        primaryColor = TrustBlueLight,
        secondaryColor = TrustBlue,
        features = listOf(
            OnboardingFeature(Icons.Default.ContentPaste, "Clipboard monitoring", TrustBlue),
            OnboardingFeature(Icons.Default.TextFields, "Text field monitoring", TrustBlueLight),
            OnboardingFeature(Icons.Default.NotificationsActive, "Alert preferences", AlertOrange),
            OnboardingFeature(Icons.Default.Palette, "Appearance settings", ProtectionActive)
        )
    ),

    // Page 6: Getting Started
    OnboardingPage(
        id = OnboardingPageId.GETTING_STARTED,
        title = "You're All Set!",
        description = "PrivacyGuard is ready to protect you. The AI model is loaded and " +
                "monitoring is active.\n\n" +
                "Try copying a credit card number to see it in action. PrivacyGuard will " +
                "detect it instantly and show you an alert.",
        icon = Icons.Default.RocketLaunch,
        iconTint = ProtectionActive,
        illustrationType = IllustrationType.ROCKET,
        primaryColor = ProtectionActive,
        secondaryColor = SuccessGreen,
        features = listOf(
            OnboardingFeature(Icons.Default.CheckCircle, "AI model loaded", ProtectionActive),
            OnboardingFeature(Icons.Default.CheckCircle, "Clipboard monitoring active", ProtectionActive),
            OnboardingFeature(Icons.Default.CheckCircle, "Alerts configured", ProtectionActive),
            OnboardingFeature(Icons.Default.CheckCircle, "Ready to protect", ProtectionActive)
        ),
        actionLabel = "Start Using PrivacyGuard"
    )
)

// ---------------------------------------------------------------------------
// Main OnboardingFlow composable
// ---------------------------------------------------------------------------

/**
 * Main onboarding flow with 6-page HorizontalPager, animated illustrations,
 * page indicators, and gesture support.
 *
 * @param onComplete Callback when onboarding is finished.
 * @param onRequestAccessibility Callback to request accessibility service.
 * @param onRequestOverlayPermission Callback to request overlay permission.
 * @param onRequestNotificationPermission Callback to request notification permission.
 * @param onSkip Callback when the user skips onboarding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingFlow(
    onComplete: () -> Unit = {},
    onRequestAccessibility: () -> Unit = {},
    onRequestOverlayPermission: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val pages = onboardingPages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    // Track permission grants
    var accessibilityGranted by remember { mutableStateOf(false) }
    var overlayGranted by remember { mutableStateOf(false) }
    var notificationGranted by remember { mutableStateOf(false) }

    // Configuration state
    var clipboardMonitoring by remember { mutableStateOf(true) }
    var textFieldMonitoring by remember { mutableStateOf(true) }
    var alertStyle by remember { mutableStateOf("Overlay Banner") }
    var selectedSensitivity by remember { mutableStateOf("Balanced") }

    // Animation state
    var isPageTransitioning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .semantics {
                contentDescription = "Onboarding flow, page ${pagerState.currentPage + 1} of ${pages.size}"
            }
    ) {
        // Top bar with skip button and progress
        OnboardingTopBar(
            currentPage = pagerState.currentPage,
            totalPages = pages.size,
            onSkip = onSkip
        )

        // Main pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            beyondViewportPageCount = 1
        ) { pageIndex ->
            val page = pages[pageIndex]

            when (page.id) {
                OnboardingPageId.WELCOME -> WelcomePage(page = page)
                OnboardingPageId.HOW_IT_WORKS -> HowItWorksPage(page = page)
                OnboardingPageId.PRIVACY_PROMISE -> PrivacyPromisePage(page = page)
                OnboardingPageId.PERMISSIONS -> PermissionsPage(
                    page = page,
                    accessibilityGranted = accessibilityGranted,
                    overlayGranted = overlayGranted,
                    notificationGranted = notificationGranted,
                    onRequestAccessibility = {
                        onRequestAccessibility()
                        accessibilityGranted = true
                    },
                    onRequestOverlay = {
                        onRequestOverlayPermission()
                        overlayGranted = true
                    },
                    onRequestNotification = {
                        onRequestNotificationPermission()
                        notificationGranted = true
                    }
                )
                OnboardingPageId.CONFIGURATION -> ConfigurationPage(
                    page = page,
                    clipboardMonitoring = clipboardMonitoring,
                    onClipboardMonitoringChanged = { clipboardMonitoring = it },
                    textFieldMonitoring = textFieldMonitoring,
                    onTextFieldMonitoringChanged = { textFieldMonitoring = it },
                    alertStyle = alertStyle,
                    onAlertStyleChanged = { alertStyle = it },
                    selectedSensitivity = selectedSensitivity,
                    onSensitivityChanged = { selectedSensitivity = it }
                )
                OnboardingPageId.GETTING_STARTED -> GettingStartedPage(page = page)
            }
        }

        // Page indicators
        OnboardingPageIndicators(
            currentPage = pagerState.currentPage,
            totalPages = pages.size
        )

        // Bottom navigation buttons
        OnboardingBottomBar(
            currentPage = pagerState.currentPage,
            totalPages = pages.size,
            currentPageData = pages[pagerState.currentPage],
            onBack = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNext = {
                coroutineScope.launch {
                    if (pagerState.currentPage < pages.size - 1) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        onComplete()
                    }
                }
            },
            onComplete = onComplete
        )
    }
}

// ---------------------------------------------------------------------------
// Top Bar
// ---------------------------------------------------------------------------

/**
 * Top bar with skip button and linear progress indicator.
 */
@Composable
fun OnboardingTopBar(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (currentPage + 1).toFloat() / totalPages.toFloat(),
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "progress_bar"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentPage + 1} / $totalPages",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (currentPage < totalPages - 1) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.semantics {
                        contentDescription = "Skip onboarding"
                    }
                ) {
                    Text("Skip")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

// ---------------------------------------------------------------------------
// Page Indicators
// ---------------------------------------------------------------------------

/**
 * Animated dot-style page indicators with size and color transitions.
 */
@Composable
fun OnboardingPageIndicators(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isSelected = currentPage == index

            val dotSize by animateDpAsState(
                targetValue = if (isSelected) 12.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "dot_size_$index"
            )

            val dotWidth by animateDpAsState(
                targetValue = if (isSelected) 28.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "dot_width_$index"
            )

            val dotColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                animationSpec = tween(300),
                label = "dot_color_$index"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(dotWidth)
                    .height(dotSize)
                    .clip(CircleShape)
                    .background(dotColor)
                    .semantics {
                        contentDescription = if (isSelected) "Page ${index + 1}, current"
                        else "Page ${index + 1}"
                    }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Bottom Bar
// ---------------------------------------------------------------------------

/**
 * Bottom navigation with back/next/finish buttons.
 */
@Composable
fun OnboardingBottomBar(
    currentPage: Int,
    totalPages: Int,
    currentPageData: OnboardingPage,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLastPage = currentPage == totalPages - 1
    val isFirstPage = currentPage == 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        AnimatedVisibility(
            visible = !isFirstPage,
            enter = fadeIn(tween(200)) + expandHorizontally(tween(300)),
            exit = fadeOut(tween(200)) + shrinkHorizontally(tween(300))
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back")
            }
        }

        // Next / Finish button
        Button(
            onClick = if (isLastPage) onComplete else onNext,
            modifier = Modifier.weight(if (isFirstPage) 2f else 1f),
            colors = if (isLastPage) ButtonDefaults.buttonColors(
                containerColor = ProtectionActive
            ) else ButtonDefaults.buttonColors()
        ) {
            Text(
                text = when {
                    isLastPage -> currentPageData.actionLabel.ifEmpty { "Get Started" }
                    currentPageData.actionLabel.isNotEmpty() && currentPage == 3 -> currentPageData.actionLabel
                    else -> "Next"
                },
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (isLastPage) Icons.Default.CheckCircle else Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Page 1: Welcome
// ---------------------------------------------------------------------------

@Composable
fun WelcomePage(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated shield illustration
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                ShieldIllustration(
                    primaryColor = page.primaryColor,
                    secondaryColor = page.secondaryColor,
                    isAnimated = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title with fade-in
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 200)
            )
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 400)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 400)
            )
        ) {
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feature bullets
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 600)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 600)
            )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                page.features.forEach { feature ->
                    FeatureBullet(feature = feature)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Page 2: How It Works
// ---------------------------------------------------------------------------

@Composable
fun HowItWorksPage(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Scanning illustration
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                ScanningIllustration(
                    primaryColor = page.primaryColor,
                    secondaryColor = page.secondaryColor,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 200)
            )
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 400)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 400)
            )
        ) {
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Step-by-step flow diagram
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 600))
        ) {
            ProcessFlowDiagram(
                steps = listOf(
                    ProcessStep("Copy Text", Icons.Default.ContentPaste, TrustBlue),
                    ProcessStep("AI Scans", Icons.Default.Memory, TrustBlueLight),
                    ProcessStep("Detect PII", Icons.Default.FindInPage, AlertOrange),
                    ProcessStep("Alert You", Icons.Default.NotificationsActive, ProtectionActive)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 800))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                page.features.forEach { feature ->
                    FeatureBullet(feature = feature)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Page 3: Privacy Promise
// ---------------------------------------------------------------------------

@Composable
fun PrivacyPromisePage(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lock illustration
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                LockIllustration(
                    primaryColor = page.primaryColor,
                    secondaryColor = page.secondaryColor,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 200)
            )
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 400)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 400)
            )
        ) {
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy guarantees
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 600))
        ) {
            PrivacyGuaranteeCard()
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 800))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                page.features.forEach { feature ->
                    FeatureBullet(feature = feature)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Page 4: Permissions
// ---------------------------------------------------------------------------

@Composable
fun PermissionsPage(
    page: OnboardingPage,
    accessibilityGranted: Boolean,
    overlayGranted: Boolean,
    notificationGranted: Boolean,
    onRequestAccessibility: () -> Unit,
    onRequestOverlay: () -> Unit,
    onRequestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Permissions illustration
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        ) {
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                PermissionsIllustration(
                    primaryColor = page.primaryColor,
                    secondaryColor = page.secondaryColor,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 200)
            )
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 300)
            )
        ) {
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Permission cards
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 400))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PermissionCard(
                    title = "Accessibility Service",
                    description = "Monitor text fields across apps for sensitive data",
                    icon = Icons.Default.Accessibility,
                    isGranted = accessibilityGranted,
                    onGrant = onRequestAccessibility,
                    color = TrustBlue
                )

                PermissionCard(
                    title = "Overlay Permission",
                    description = "Show real-time alert banners over other apps",
                    icon = Icons.Default.Layers,
                    isGranted = overlayGranted,
                    onGrant = onRequestOverlay,
                    color = AlertOrange
                )

                PermissionCard(
                    title = "Notifications",
                    description = "Send detection alerts and status updates",
                    icon = Icons.Default.Notifications,
                    isGranted = notificationGranted,
                    onGrant = onRequestNotification,
                    color = ProtectionActive
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status summary
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 600))
        ) {
            val grantedCount = listOf(accessibilityGranted, overlayGranted, notificationGranted).count { it }
            Surface(
                color = if (grantedCount == 3) ProtectionActive.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "$grantedCount of 3 permissions granted",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (grantedCount == 3) ProtectionActive
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Page 5: Configuration
// ---------------------------------------------------------------------------

@Composable
fun ConfigurationPage(
    page: OnboardingPage,
    clipboardMonitoring: Boolean,
    onClipboardMonitoringChanged: (Boolean) -> Unit,
    textFieldMonitoring: Boolean,
    onTextFieldMonitoringChanged: (Boolean) -> Unit,
    alertStyle: String,
    onAlertStyleChanged: (String) -> Unit,
    selectedSensitivity: String,
    onSensitivityChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Gear illustration
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        ) {
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                GearIllustration(
                    primaryColor = page.primaryColor,
                    secondaryColor = page.secondaryColor,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 200)
            )
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 300))
        ) {
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Configuration options
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 400))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Monitoring toggles
                ConfigToggleRow(
                    title = "Clipboard Monitoring",
                    description = "Scan clipboard for sensitive data",
                    icon = Icons.Default.ContentPaste,
                    checked = clipboardMonitoring,
                    onCheckedChange = onClipboardMonitoringChanged
                )

                ConfigToggleRow(
                    title = "Text Field Monitoring",
                    description = "Scan text fields across apps",
                    icon = Icons.Default.TextFields,
                    checked = textFieldMonitoring,
                    onCheckedChange = onTextFieldMonitoringChanged
                )

                // Sensitivity selector
                ConfigSensitivitySelector(
                    selectedSensitivity = selectedSensitivity,
                    onSensitivityChanged = onSensitivityChanged
                )

                // Alert style selector
                ConfigAlertStyleSelector(
                    selectedStyle = alertStyle,
                    onStyleChanged = onAlertStyleChanged
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Page 6: Getting Started
// ---------------------------------------------------------------------------

@Composable
fun GettingStartedPage(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }
    var checklistAnimated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
        delay(600)
        checklistAnimated = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Rocket illustration
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600)) + scaleIn(
                initialScale = 0.3f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                RocketIllustration(
                    primaryColor = page.primaryColor,
                    secondaryColor = page.secondaryColor,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 200)
            )
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = ProtectionActive
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(600, delayMillis = 400))
        ) {
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Animated checklist
        AnimatedVisibility(
            visible = checklistAnimated,
            enter = fadeIn(tween(400))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                page.features.forEachIndexed { index, feature ->
                    AnimatedChecklistItem(
                        feature = feature,
                        delayMs = index * 300
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tip card
        AnimatedVisibility(
            visible = checklistAnimated,
            enter = fadeIn(tween(600, delayMillis = 1200)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600, delayMillis = 1200)
            )
        ) {
            Surface(
                color = TrustBlue.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = TrustBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tip: Try copying \"4532-1234-5678-9012\" to see PrivacyGuard detect a credit card number.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Canvas-drawn Illustrations
// ---------------------------------------------------------------------------

/**
 * Shield illustration drawn on Canvas with animated glow and pulse.
 */
@Composable
fun ShieldIllustration(
    primaryColor: Color,
    secondaryColor: Color,
    isAnimated: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shield_illustration")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shield_orbit"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_glow"
    )

    Canvas(modifier = modifier.semantics { contentDescription = "Shield icon" }) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val shieldWidth = size.width * 0.45f
        val shieldHeight = size.height * 0.55f

        // Outer glow
        if (isAnimated) {
            drawCircle(
                color = primaryColor.copy(alpha = glowAlpha * 0.3f),
                radius = cx * pulseScale,
                center = Offset(cx, cy)
            )
        }

        // Orbiting dots
        if (isAnimated) {
            for (i in 0 until 8) {
                val angle = rotationAngle + (i * 45f)
                val rad = Math.toRadians(angle.toDouble())
                val orbitRadius = cx * 0.8f
                val dotX = cx + orbitRadius * cos(rad).toFloat()
                val dotY = cy + orbitRadius * sin(rad).toFloat()
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.5f),
                    radius = 2.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
        }

        // Shield body
        val shieldPath = Path().apply {
            moveTo(cx, cy - shieldHeight / 2f)
            cubicTo(
                cx + shieldWidth / 2f, cy - shieldHeight / 2f,
                cx + shieldWidth / 2f, cy + shieldHeight * 0.1f,
                cx, cy + shieldHeight / 2f
            )
            cubicTo(
                cx - shieldWidth / 2f, cy + shieldHeight * 0.1f,
                cx - shieldWidth / 2f, cy - shieldHeight / 2f,
                cx, cy - shieldHeight / 2f
            )
            close()
        }

        // Shield fill
        drawPath(
            path = shieldPath,
            color = primaryColor.copy(alpha = 0.12f)
        )

        // Shield stroke
        drawPath(
            path = shieldPath,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Checkmark
        val checkPath = Path().apply {
            moveTo(cx - shieldWidth * 0.15f, cy + shieldHeight * 0.02f)
            lineTo(cx - shieldWidth * 0.01f, cy + shieldHeight * 0.13f)
            lineTo(cx + shieldWidth * 0.18f, cy - shieldHeight * 0.1f)
        }
        drawPath(
            path = checkPath,
            color = primaryColor,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Scanning illustration with animated scan line and data bars.
 */
@Composable
fun ScanningIllustration(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")

    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_y"
    )

    val barAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar_alpha"
    )

    Canvas(modifier = modifier.semantics { contentDescription = "Scanning illustration" }) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val docWidth = size.width * 0.55f
        val docHeight = size.height * 0.65f

        // Document background
        drawRoundRect(
            color = primaryColor.copy(alpha = 0.08f),
            topLeft = Offset(cx - docWidth / 2f, cy - docHeight / 2f),
            size = Size(docWidth, docHeight),
            cornerRadius = CornerRadius(8.dp.toPx())
        )

        // Document border
        drawRoundRect(
            color = primaryColor.copy(alpha = 0.3f),
            topLeft = Offset(cx - docWidth / 2f, cy - docHeight / 2f),
            size = Size(docWidth, docHeight),
            cornerRadius = CornerRadius(8.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )

        // Text lines
        val lineStartX = cx - docWidth / 2f + 16.dp.toPx()
        val lineWidths = listOf(0.8f, 0.6f, 0.9f, 0.5f, 0.7f, 0.85f)
        val lineY0 = cy - docHeight / 2f + 24.dp.toPx()
        val lineSpacing = 14.dp.toPx()

        lineWidths.forEachIndexed { index, widthFraction ->
            val lineY = lineY0 + index * lineSpacing
            val lineW = (docWidth - 32.dp.toPx()) * widthFraction

            val isHighlighted = index == 2 || index == 4

            drawRoundRect(
                color = if (isHighlighted) AlertOrange.copy(alpha = barAlpha)
                else primaryColor.copy(alpha = 0.2f),
                topLeft = Offset(lineStartX, lineY),
                size = Size(lineW, 6.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
        }

        // Scan line
        val scanLineY = (cy - docHeight / 2f) + scanY * docHeight
        drawLine(
            color = secondaryColor,
            start = Offset(cx - docWidth / 2f, scanLineY),
            end = Offset(cx + docWidth / 2f, scanLineY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Scan glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    secondaryColor.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                startY = scanLineY - 15.dp.toPx(),
                endY = scanLineY + 15.dp.toPx()
            ),
            topLeft = Offset(cx - docWidth / 2f, scanLineY - 15.dp.toPx()),
            size = Size(docWidth, 30.dp.toPx())
        )
    }
}

/**
 * Lock illustration with animated rings.
 */
@Composable
fun LockIllustration(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lock")

    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_scale"
    )

    val lockRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lock_rotation"
    )

    Canvas(
        modifier = modifier
            .rotate(lockRotation)
            .semantics { contentDescription = "Lock illustration" }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Concentric protection rings
        for (i in 3 downTo 1) {
            val ringRadius = cx * (0.5f + i * 0.12f) * ringScale
            drawCircle(
                color = primaryColor.copy(alpha = 0.05f * i),
                radius = ringRadius,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.1f),
                radius = ringRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Lock body
        val bodyWidth = size.width * 0.35f
        val bodyHeight = size.height * 0.25f
        val bodyTop = cy

        drawRoundRect(
            color = primaryColor.copy(alpha = 0.15f),
            topLeft = Offset(cx - bodyWidth / 2f, bodyTop),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(6.dp.toPx())
        )
        drawRoundRect(
            color = primaryColor,
            topLeft = Offset(cx - bodyWidth / 2f, bodyTop),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )

        // Lock shackle
        val shackleWidth = bodyWidth * 0.6f
        val shackleHeight = bodyHeight * 0.8f
        drawArc(
            color = primaryColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - shackleWidth / 2f, bodyTop - shackleHeight),
            size = Size(shackleWidth, shackleHeight * 2f),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Keyhole
        drawCircle(
            color = primaryColor,
            radius = 4.dp.toPx(),
            center = Offset(cx, bodyTop + bodyHeight * 0.4f)
        )
        drawLine(
            color = primaryColor,
            start = Offset(cx, bodyTop + bodyHeight * 0.4f),
            end = Offset(cx, bodyTop + bodyHeight * 0.7f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * Permissions illustration with three overlapping circles.
 */
@Composable
fun PermissionsIllustration(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "permissions")

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "perm_bounce"
    )

    Canvas(modifier = modifier.semantics { contentDescription = "Permissions illustration" }) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val circleRadius = size.width * 0.2f

        val colors = listOf(TrustBlue, AlertOrange, ProtectionActive)
        val offsets = listOf(
            Offset(cx, cy - circleRadius * 0.6f - bounce),
            Offset(cx - circleRadius * 0.5f, cy + circleRadius * 0.3f + bounce * 0.5f),
            Offset(cx + circleRadius * 0.5f, cy + circleRadius * 0.3f + bounce * 0.5f)
        )

        // Draw overlapping translucent circles
        colors.forEachIndexed { index, color ->
            drawCircle(
                color = color.copy(alpha = 0.15f),
                radius = circleRadius,
                center = offsets[index]
            )
            drawCircle(
                color = color,
                radius = circleRadius,
                center = offsets[index],
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Center checkmark
        val checkSize = circleRadius * 0.3f
        val checkPath = Path().apply {
            moveTo(cx - checkSize * 0.5f, cy)
            lineTo(cx - checkSize * 0.1f, cy + checkSize * 0.35f)
            lineTo(cx + checkSize * 0.5f, cy - checkSize * 0.25f)
        }
        drawPath(
            path = checkPath,
            color = ProtectionActive,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Gear/settings illustration with animated rotation.
 */
@Composable
fun GearIllustration(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gear")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gear_rotation"
    )

    Canvas(
        modifier = modifier.semantics { contentDescription = "Configuration gear" }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerRadius = minOf(cx, cy) * 0.7f
        val innerRadius = outerRadius * 0.65f
        val teethCount = 8
        val toothDepth = outerRadius * 0.2f

        // Draw gear with rotation
        rotate(rotation, Offset(cx, cy)) {
            // Gear teeth
            val gearPath = Path().apply {
                for (i in 0 until teethCount) {
                    val angleStep = 360f / teethCount
                    val startAngle = i * angleStep
                    val toothHalfAngle = angleStep * 0.25f

                    val innerAngle1 = Math.toRadians((startAngle - toothHalfAngle).toDouble())
                    val outerAngle1 = Math.toRadians((startAngle - toothHalfAngle * 0.6f).toDouble())
                    val outerAngle2 = Math.toRadians((startAngle + toothHalfAngle * 0.6f).toDouble())
                    val innerAngle2 = Math.toRadians((startAngle + toothHalfAngle).toDouble())

                    val ix1 = cx + innerRadius * cos(innerAngle1).toFloat()
                    val iy1 = cy + innerRadius * sin(innerAngle1).toFloat()
                    val ox1 = cx + (innerRadius + toothDepth) * cos(outerAngle1).toFloat()
                    val oy1 = cy + (innerRadius + toothDepth) * sin(outerAngle1).toFloat()
                    val ox2 = cx + (innerRadius + toothDepth) * cos(outerAngle2).toFloat()
                    val oy2 = cy + (innerRadius + toothDepth) * sin(outerAngle2).toFloat()
                    val ix2 = cx + innerRadius * cos(innerAngle2).toFloat()
                    val iy2 = cy + innerRadius * sin(innerAngle2).toFloat()

                    if (i == 0) moveTo(ix1, iy1)
                    else lineTo(ix1, iy1)
                    lineTo(ox1, oy1)
                    lineTo(ox2, oy2)
                    lineTo(ix2, iy2)

                    // Arc to next tooth
                    val nextAngle = Math.toRadians(((i + 1) * angleStep - toothHalfAngle).toDouble())
                    val nx = cx + innerRadius * cos(nextAngle).toFloat()
                    val ny = cy + innerRadius * sin(nextAngle).toFloat()
                    lineTo(nx, ny)
                }
                close()
            }

            drawPath(
                path = gearPath,
                color = primaryColor.copy(alpha = 0.12f)
            )
            drawPath(
                path = gearPath,
                color = primaryColor,
                style = Stroke(width = 2.dp.toPx())
            )

            // Center circle
            drawCircle(
                color = secondaryColor.copy(alpha = 0.2f),
                radius = innerRadius * 0.4f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = primaryColor,
                radius = innerRadius * 0.4f,
                center = Offset(cx, cy),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

/**
 * Rocket illustration for the getting started page.
 */
@Composable
fun RocketIllustration(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rocket")

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rocket_float"
    )

    val flameAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_alpha"
    )

    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_alpha"
    )

    Canvas(modifier = modifier.semantics { contentDescription = "Rocket illustration" }) {
        val cx = size.width / 2f
        val cy = size.height / 2f - floatOffset

        // Stars
        val starPositions = listOf(
            Offset(cx * 0.3f, cy * 0.4f),
            Offset(cx * 1.7f, cy * 0.3f),
            Offset(cx * 0.5f, cy * 1.5f),
            Offset(cx * 1.6f, cy * 1.4f),
            Offset(cx * 0.2f, cy * 1.0f),
            Offset(cx * 1.8f, cy * 0.8f)
        )

        starPositions.forEach { pos ->
            drawCircle(
                color = secondaryColor.copy(alpha = starAlpha),
                radius = 2.dp.toPx(),
                center = pos
            )
        }

        // Rocket body
        val bodyWidth = size.width * 0.15f
        val bodyHeight = size.height * 0.35f

        val rocketPath = Path().apply {
            // Nose cone
            moveTo(cx, cy - bodyHeight / 2f - bodyWidth * 0.5f)
            quadraticTo(
                cx + bodyWidth / 2f, cy - bodyHeight / 2f + bodyWidth * 0.2f,
                cx + bodyWidth / 2f, cy - bodyHeight / 4f
            )
            // Right side
            lineTo(cx + bodyWidth / 2f, cy + bodyHeight / 4f)
            // Right fin
            lineTo(cx + bodyWidth, cy + bodyHeight / 2f)
            lineTo(cx + bodyWidth / 2f, cy + bodyHeight / 3f)
            // Bottom
            lineTo(cx + bodyWidth / 4f, cy + bodyHeight / 2.5f)
            lineTo(cx - bodyWidth / 4f, cy + bodyHeight / 2.5f)
            // Left fin
            lineTo(cx - bodyWidth / 2f, cy + bodyHeight / 3f)
            lineTo(cx - bodyWidth, cy + bodyHeight / 2f)
            lineTo(cx - bodyWidth / 2f, cy + bodyHeight / 4f)
            // Left side
            lineTo(cx - bodyWidth / 2f, cy - bodyHeight / 4f)
            quadraticTo(
                cx - bodyWidth / 2f, cy - bodyHeight / 2f + bodyWidth * 0.2f,
                cx, cy - bodyHeight / 2f - bodyWidth * 0.5f
            )
            close()
        }

        drawPath(
            path = rocketPath,
            color = primaryColor.copy(alpha = 0.15f)
        )
        drawPath(
            path = rocketPath,
            color = primaryColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Window
        drawCircle(
            color = secondaryColor.copy(alpha = 0.3f),
            radius = bodyWidth * 0.2f,
            center = Offset(cx, cy - bodyHeight * 0.1f)
        )
        drawCircle(
            color = primaryColor,
            radius = bodyWidth * 0.2f,
            center = Offset(cx, cy - bodyHeight * 0.1f),
            style = Stroke(width = 2.dp.toPx())
        )

        // Flame
        val flameHeight = bodyHeight * 0.25f
        val flamePath = Path().apply {
            moveTo(cx - bodyWidth * 0.2f, cy + bodyHeight / 2.5f)
            quadraticTo(
                cx, cy + bodyHeight / 2.5f + flameHeight * flameAlpha,
                cx + bodyWidth * 0.2f, cy + bodyHeight / 2.5f
            )
        }
        drawPath(
            path = flamePath,
            color = AlertOrange.copy(alpha = flameAlpha),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = flamePath,
            color = AlertOrange.copy(alpha = flameAlpha * 0.3f)
        )

        // Inner flame
        val innerFlamePath = Path().apply {
            moveTo(cx - bodyWidth * 0.1f, cy + bodyHeight / 2.5f)
            quadraticTo(
                cx, cy + bodyHeight / 2.5f + flameHeight * 0.6f * flameAlpha,
                cx + bodyWidth * 0.1f, cy + bodyHeight / 2.5f
            )
        }
        drawPath(
            path = innerFlamePath,
            color = AlertRed.copy(alpha = flameAlpha * 0.7f)
        )
    }
}

// ---------------------------------------------------------------------------
// Supporting Composables
// ---------------------------------------------------------------------------

/**
 * A feature bullet point with icon and text.
 */
@Composable
fun FeatureBullet(
    feature: OnboardingFeature,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = feature.text
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(feature.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = feature.color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = feature.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Data class for process flow steps.
 */
data class ProcessStep(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

/**
 * A horizontal process flow diagram with arrows between steps.
 */
@Composable
fun ProcessFlowDiagram(
    steps: List<ProcessStep>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(step.color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = step.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.label,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            if (index < steps.size - 1) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * A card showing privacy guarantees on the privacy promise page.
 */
@Composable
fun PrivacyGuaranteeCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ProtectionActive.copy(alpha = 0.06f),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(ProtectionActive.copy(alpha = 0.2f))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = ProtectionActive,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Privacy Guarantee",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ProtectionActive
                )
            }
            Text(
                "We certify that PrivacyGuard performs all processing on-device. " +
                        "No network permissions are requested or used. Your data stays exclusively on your phone.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * Permission card with grant button.
 */
@Composable
fun PermissionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    onGrant: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isGranted) ProtectionActive.copy(alpha = 0.08f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "perm_bg"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else icon,
                    contentDescription = null,
                    tint = if (isGranted) ProtectionActive else color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isGranted) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ProtectionActive.copy(alpha = 0.15f)
                ) {
                    Text(
                        "Granted",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProtectionActive,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            } else {
                FilledTonalButton(
                    onClick = onGrant,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Grant", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

/**
 * Configuration toggle row for the configuration page.
 */
@Composable
fun ConfigToggleRow(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

/**
 * Sensitivity selector for the configuration page.
 */
@Composable
fun ConfigSensitivitySelector(
    selectedSensitivity: String,
    onSensitivityChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Aggressive", "Balanced", "Conservative")
    val descriptions = listOf(
        "Detect everything, more false positives",
        "Best balance of accuracy and coverage",
        "Only high-confidence detections"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Detection Sensitivity",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSensitivityChanged(option) }
                        .then(
                            if (option == selectedSensitivity)
                                Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                            else Modifier
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = option == selectedSensitivity,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            option,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (option == selectedSensitivity) FontWeight.SemiBold else FontWeight.Normal
                        )
                        Text(
                            descriptions[index],
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Alert style selector for the configuration page.
 */
@Composable
fun ConfigAlertStyleSelector(
    selectedStyle: String,
    onStyleChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Overlay Banner", "Notification Only", "Silent Log")
    val icons = listOf(Icons.Default.Layers, Icons.Default.Notifications, Icons.Default.Description)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Alert Style",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEachIndexed { index, option ->
                    val isSelected = option == selectedStyle
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onStyleChanged(option) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent,
                        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.split(" ").first(),
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Animated checklist item that checks itself after a delay.
 */
@Composable
fun AnimatedChecklistItem(
    feature: OnboardingFeature,
    delayMs: Int = 0,
    modifier: Modifier = Modifier
) {
    var isChecked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        isChecked = true
    }

    val checkScale by animateFloatAsState(
        targetValue = if (isChecked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "check_scale"
    )

    val bgAlpha by animateFloatAsState(
        targetValue = if (isChecked) 1f else 0f,
        animationSpec = tween(300),
        label = "check_bg"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = feature.color.copy(alpha = 0.06f * bgAlpha)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .scale(checkScale)
                    .clip(CircleShape)
                    .background(feature.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = feature.color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = feature.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(bgAlpha)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Fallback page content (legacy support)
// ---------------------------------------------------------------------------

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            page.icon,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = page.iconTint
        )
        Spacer(Modifier.height(32.dp))
        Text(
            page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Preview Composables (6+)
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "OnboardingFlow - Full")
@Composable
private fun OnboardingFlowFullPreview() {
    MaterialTheme {
        OnboardingFlow()
    }
}

@Preview(showBackground = true, name = "WelcomePage")
@Composable
private fun WelcomePagePreview() {
    MaterialTheme {
        WelcomePage(page = onboardingPages[0])
    }
}

@Preview(showBackground = true, name = "HowItWorksPage")
@Composable
private fun HowItWorksPagePreview() {
    MaterialTheme {
        HowItWorksPage(page = onboardingPages[1])
    }
}

@Preview(showBackground = true, name = "PrivacyPromisePage")
@Composable
private fun PrivacyPromisePagePreview() {
    MaterialTheme {
        PrivacyPromisePage(page = onboardingPages[2])
    }
}

@Preview(showBackground = true, name = "PermissionsPage")
@Composable
private fun PermissionsPagePreview() {
    MaterialTheme {
        PermissionsPage(
            page = onboardingPages[3],
            accessibilityGranted = true,
            overlayGranted = false,
            notificationGranted = false,
            onRequestAccessibility = {},
            onRequestOverlay = {},
            onRequestNotification = {}
        )
    }
}

@Preview(showBackground = true, name = "ConfigurationPage")
@Composable
private fun ConfigurationPagePreview() {
    MaterialTheme {
        ConfigurationPage(
            page = onboardingPages[4],
            clipboardMonitoring = true,
            onClipboardMonitoringChanged = {},
            textFieldMonitoring = true,
            onTextFieldMonitoringChanged = {},
            alertStyle = "Overlay Banner",
            onAlertStyleChanged = {},
            selectedSensitivity = "Balanced",
            onSensitivityChanged = {}
        )
    }
}

@Preview(showBackground = true, name = "GettingStartedPage")
@Composable
private fun GettingStartedPagePreview() {
    MaterialTheme {
        GettingStartedPage(page = onboardingPages[5])
    }
}

@Preview(showBackground = true, name = "PageIndicators")
@Composable
private fun PageIndicatorsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OnboardingPageIndicators(currentPage = 0, totalPages = 6)
            OnboardingPageIndicators(currentPage = 2, totalPages = 6)
            OnboardingPageIndicators(currentPage = 5, totalPages = 6)
        }
    }
}

@Preview(showBackground = true, name = "PermissionCard")
@Composable
private fun PermissionCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PermissionCard(
                title = "Accessibility Service",
                description = "Monitor text fields",
                icon = Icons.Default.Accessibility,
                isGranted = true,
                onGrant = {},
                color = TrustBlue
            )
            PermissionCard(
                title = "Overlay Permission",
                description = "Show alert banners",
                icon = Icons.Default.Layers,
                isGranted = false,
                onGrant = {},
                color = AlertOrange
            )
        }
    }
}

@Preview(showBackground = true, name = "Canvas Illustrations")
@Composable
private fun CanvasIllustrationsPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShieldIllustration(
                primaryColor = TrustBlue,
                secondaryColor = TrustBlueLight,
                modifier = Modifier.size(80.dp)
            )
            ScanningIllustration(
                primaryColor = TrustBlue,
                secondaryColor = ProtectionActive,
                modifier = Modifier.size(80.dp)
            )
            LockIllustration(
                primaryColor = ProtectionActive,
                secondaryColor = SuccessGreen,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}
