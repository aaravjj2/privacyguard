package com.privacyguard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.privacyguard.ui.theme.*

// ---------------------------------------------------------------------------
// 1. SettingsSection
// ---------------------------------------------------------------------------

/**
 * A collapsible section header for grouping related settings.
 * Supports animated expand/collapse with a chevron icon.
 *
 * @param title Section title text.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon for the section.
 * @param isExpanded Whether the section body is visible.
 * @param onToggle Callback when the section header is tapped.
 * @param badge Optional trailing badge text (e.g., count).
 * @param contentDescription Accessibility label.
 * @param content The section body content displayed when expanded.
 */
@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isExpanded: Boolean = true,
    onToggle: () -> Unit = {},
    badge: String = "",
    contentDescription: String = title,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "section_chevron"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(horizontal = 4.dp, vertical = 12.dp)
                .semantics {
                    this.contentDescription = contentDescription
                    this.role = Role.Button
                    this.stateDescription = if (isExpanded) "Expanded" else "Collapsed"
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            if (badge.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse section" else "Expand section",
                modifier = Modifier
                    .size(20.dp)
                    .rotate(chevronRotation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Expandable content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(tween(300)),
            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(tween(200))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }
    }
}

@Preview(showBackground = true, name = "SettingsSection - Expanded")
@Composable
private fun SettingsSectionExpandedPreview() {
    MaterialTheme {
        SettingsSection(
            title = "Privacy Protection",
            icon = Icons.Default.Shield,
            isExpanded = true,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Setting 1", modifier = Modifier.padding(start = 28.dp))
            Text("Setting 2", modifier = Modifier.padding(start = 28.dp))
        }
    }
}

@Preview(showBackground = true, name = "SettingsSection - Collapsed")
@Composable
private fun SettingsSectionCollapsedPreview() {
    MaterialTheme {
        SettingsSection(
            title = "Notifications",
            icon = Icons.Default.Notifications,
            isExpanded = false,
            badge = "3",
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 2. SettingsSwitch
// ---------------------------------------------------------------------------

/**
 * A settings row with a label, description, icon, and a toggle switch.
 *
 * @param title The setting name.
 * @param description Explanatory text below the title.
 * @param checked Current toggle state.
 * @param onCheckedChange Callback when the toggle state changes.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param enabled Whether the switch is interactive.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    contentDescription: String = "$title toggle"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { onCheckedChange(!checked) }
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                    this.role = Role.Switch
                    this.stateDescription = if (checked) "On" else "Off"
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled
            )
        }
    }
}

@Preview(showBackground = true, name = "SettingsSwitch - On")
@Composable
private fun SettingsSwitchOnPreview() {
    MaterialTheme {
        SettingsSwitch(
            title = "Clipboard Monitoring",
            description = "Monitor clipboard for sensitive data",
            checked = true,
            onCheckedChange = {},
            icon = Icons.Default.ContentPaste,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsSwitch - Off")
@Composable
private fun SettingsSwitchOffPreview() {
    MaterialTheme {
        SettingsSwitch(
            title = "Text Field Monitoring",
            description = "Monitor text fields via Accessibility Service",
            checked = false,
            onCheckedChange = {},
            icon = Icons.Default.TextFields,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsSwitch - Disabled")
@Composable
private fun SettingsSwitchDisabledPreview() {
    MaterialTheme {
        SettingsSwitch(
            title = "Cloud Sync",
            description = "Not available - all data stays on device",
            checked = false,
            onCheckedChange = {},
            icon = Icons.Default.CloudOff,
            enabled = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 3. SettingsSlider
// ---------------------------------------------------------------------------

/**
 * A settings row with a labeled slider control, showing the current value.
 *
 * @param title The setting name.
 * @param description Explanatory text.
 * @param value Current slider value.
 * @param onValueChange Callback for value updates.
 * @param onValueChangeFinished Callback when the user finishes dragging.
 * @param valueRange The range of the slider.
 * @param steps Number of discrete steps (0 for continuous).
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param valueLabel Formatted string for the current value display.
 * @param enabled Whether the slider is interactive.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsSlider(
    title: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueLabel: String = "",
    enabled: Boolean = true,
    contentDescription: String = "$title slider"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (valueLabel.isNotEmpty()) {
                    Text(
                        text = valueLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = value,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )

            // Min/Max labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${valueRange.start.let { if (it == it.toLong().toFloat()) it.toLong().toString() else "%.1f".format(it) }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${valueRange.endInclusive.let { if (it == it.toLong().toFloat()) it.toLong().toString() else "%.1f".format(it) }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsSlider - Debounce")
@Composable
private fun SettingsSliderDebouncePreview() {
    MaterialTheme {
        var value by remember { mutableFloatStateOf(800f) }
        SettingsSlider(
            title = "Debounce Delay",
            description = "Wait time before analyzing text",
            value = value,
            onValueChange = { value = it },
            valueRange = 200f..2000f,
            steps = 8,
            icon = Icons.Default.Timer,
            valueLabel = "${value.toLong()}ms",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsSlider - Confidence")
@Composable
private fun SettingsSliderConfidencePreview() {
    MaterialTheme {
        var value by remember { mutableFloatStateOf(0.75f) }
        SettingsSlider(
            title = "Detection Confidence",
            description = "Minimum confidence threshold for alerts",
            value = value,
            onValueChange = { value = it },
            valueRange = 0.5f..1.0f,
            icon = Icons.Default.TrendingUp,
            valueLabel = "${(value * 100).toInt()}%",
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 4. SettingsDropdown
// ---------------------------------------------------------------------------

/**
 * A settings row with a dropdown selector for choosing among options.
 *
 * @param title The setting name.
 * @param description Explanatory text.
 * @param options List of available options.
 * @param selectedOption Currently selected option.
 * @param onOptionSelected Callback when an option is chosen.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param enabled Whether the dropdown is interactive.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsDropdown(
    title: String,
    description: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    contentDescription: String = "$title dropdown"
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { expanded = true }
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                    this.role = Role.DropdownList
                    this.stateDescription = "Selected: $selectedOption"
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedOption,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = option,
                                    fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
                                )
                                if (option == selectedOption) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsDropdown - Theme")
@Composable
private fun SettingsDropdownThemePreview() {
    MaterialTheme {
        SettingsDropdown(
            title = "App Theme",
            description = "Choose the visual appearance",
            options = listOf("System Default", "Light", "Dark"),
            selectedOption = "System Default",
            onOptionSelected = {},
            icon = Icons.Default.Palette,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsDropdown - Language")
@Composable
private fun SettingsDropdownLanguagePreview() {
    MaterialTheme {
        SettingsDropdown(
            title = "Language",
            description = "App display language",
            options = listOf("English", "Spanish", "French", "German", "Japanese"),
            selectedOption = "English",
            onOptionSelected = {},
            icon = Icons.Default.Language,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 5. SettingsRadioGroup
// ---------------------------------------------------------------------------

/**
 * A group of radio button options inside a settings card.
 *
 * @param title The setting name.
 * @param description Explanatory text.
 * @param options List of option labels.
 * @param selectedOption The currently selected label.
 * @param onOptionSelected Callback when selection changes.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param optionDescriptions Optional descriptions for each option.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsRadioGroup(
    title: String,
    description: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    optionDescriptions: List<String> = emptyList(),
    contentDescription: String = "$title radio group"
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = false) {
                    this.contentDescription = contentDescription
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .selectable(
                                selected = (option == selectedOption),
                                onClick = { onOptionSelected(option) },
                                role = Role.RadioButton
                            )
                            .then(
                                if (option == selectedOption)
                                    Modifier.background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    )
                                else Modifier
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
                            )
                            if (optionDescriptions.size > index && optionDescriptions[index].isNotEmpty()) {
                                Text(
                                    text = optionDescriptions[index],
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsRadioGroup - Alert Style")
@Composable
private fun SettingsRadioGroupPreview() {
    MaterialTheme {
        SettingsRadioGroup(
            title = "Alert Style",
            description = "How to display privacy alerts",
            options = listOf("Overlay Banner", "Notification Only", "Silent Log"),
            optionDescriptions = listOf(
                "Show a floating banner over other apps",
                "Send a system notification",
                "Record in history without alerting"
            ),
            selectedOption = "Overlay Banner",
            onOptionSelected = {},
            icon = Icons.Default.NotificationsActive,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 6. SettingsTextField
// ---------------------------------------------------------------------------

/**
 * A settings row with a text input field for entering custom values.
 *
 * @param title The setting name.
 * @param description Explanatory text.
 * @param value Current text value.
 * @param onValueChange Callback for text changes.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param placeholder Placeholder text when empty.
 * @param keyboardType Type of soft keyboard to display.
 * @param isPassword Whether to mask the input.
 * @param maxLength Maximum character length (0 for unlimited).
 * @param errorMessage Optional error text to display.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsTextField(
    title: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    maxLength: Int = 0,
    errorMessage: String = "",
    contentDescription: String = "$title text field"
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                    if (errorMessage.isNotEmpty()) {
                        this.error(errorMessage)
                    }
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (errorMessage.isEmpty()) MaterialTheme.colorScheme.primary
                        else AlertRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    if (maxLength <= 0 || newValue.length <= maxLength) {
                        onValueChange(newValue)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    if (placeholder.isNotEmpty()) Text(placeholder)
                },
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation()
                else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                isError = errorMessage.isNotEmpty(),
                trailingIcon = {
                    if (isPassword) {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    } else if (value.isNotEmpty()) {
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear text")
                        }
                    }
                },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = AlertRed,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            Spacer(modifier = Modifier)
                        }
                        if (maxLength > 0) {
                            Text(
                                text = "${value.length}/$maxLength",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "SettingsTextField - Basic")
@Composable
private fun SettingsTextFieldBasicPreview() {
    MaterialTheme {
        var text by remember { mutableStateOf("") }
        SettingsTextField(
            title = "Whitelist Pattern",
            description = "Enter a regex pattern to whitelist",
            value = text,
            onValueChange = { text = it },
            icon = Icons.Default.FilterList,
            placeholder = "e.g., \\d{4}-\\d{4}",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsTextField - Error")
@Composable
private fun SettingsTextFieldErrorPreview() {
    MaterialTheme {
        SettingsTextField(
            title = "Export Path",
            description = "Directory for exported data",
            value = "/invalid/path",
            onValueChange = {},
            icon = Icons.Default.FolderOpen,
            errorMessage = "Directory does not exist",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsTextField - Password")
@Composable
private fun SettingsTextFieldPasswordPreview() {
    MaterialTheme {
        SettingsTextField(
            title = "Encryption Key",
            description = "Key for local database encryption",
            value = "mysecretkey123",
            onValueChange = {},
            icon = Icons.Default.Key,
            isPassword = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 7. SettingsInfoRow
// ---------------------------------------------------------------------------

/**
 * A read-only informational row for displaying static settings values.
 *
 * @param title The label.
 * @param value The displayed value.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param valueColor Color of the value text.
 * @param onClick Optional click handler (e.g., to copy the value).
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsInfoRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    contentDescription: String = "$title: $value"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor,
                fontWeight = FontWeight.SemiBold
            )

            if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsInfoRow")
@Composable
private fun SettingsInfoRowPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsInfoRow(
                title = "Version",
                value = "1.0.0",
                icon = Icons.Default.Info
            )
            SettingsInfoRow(
                title = "Model Status",
                value = "Loaded",
                icon = Icons.Default.Memory,
                valueColor = SuccessGreen
            )
            SettingsInfoRow(
                title = "Database Size",
                value = "2.4 MB",
                icon = Icons.Default.Storage,
                onClick = {}
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 8. SettingsNavigationRow
// ---------------------------------------------------------------------------

/**
 * A navigation row that opens a sub-screen or dialog.
 *
 * @param title The label.
 * @param subtitle Optional subtitle.
 * @param icon Optional leading icon.
 * @param onClick Callback when tapped.
 * @param modifier Modifier for layout.
 * @param trailingContent Optional trailing composable (defaults to chevron).
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsNavigationRow(
    title: String,
    subtitle: String = "",
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    contentDescription: String = title
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                    this.role = Role.Button
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsNavigationRow")
@Composable
private fun SettingsNavigationRowPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsNavigationRow(
                title = "Manage Whitelist",
                subtitle = "12 patterns configured",
                icon = Icons.Default.FilterList
            )
            SettingsNavigationRow(
                title = "Export Data",
                subtitle = "Export detection history",
                icon = Icons.Default.Upload
            )
            SettingsNavigationRow(
                title = "Licenses",
                icon = Icons.Default.Description
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 9. SettingsMultiSelect
// ---------------------------------------------------------------------------

/**
 * A settings card with multiple checkboxes for selecting several items.
 *
 * @param title The setting name.
 * @param description Explanatory text.
 * @param options List of option labels.
 * @param selectedOptions Currently selected option labels.
 * @param onSelectionChanged Callback with updated set of selected labels.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsMultiSelect(
    title: String,
    description: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentDescription: String = "$title multi-select"
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = false) {
                    this.contentDescription = contentDescription
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${selectedOptions.size}/${options.size}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            options.forEach { option ->
                val isSelected = option in selectedOptions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val newSet = selectedOptions.toMutableSet()
                            if (isSelected) newSet.remove(option) else newSet.add(option)
                            onSelectionChanged(newSet)
                        }
                        .then(
                            if (isSelected)
                                Modifier.background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                )
                            else Modifier
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsMultiSelect")
@Composable
private fun SettingsMultiSelectPreview() {
    MaterialTheme {
        var selected by remember {
            mutableStateOf(setOf("Credit Card", "SSN", "Password"))
        }
        SettingsMultiSelect(
            title = "Monitored Entity Types",
            description = "Select which data types to detect",
            options = listOf("Credit Card", "SSN", "Password", "API Key", "Email", "Phone"),
            selectedOptions = selected,
            onSelectionChanged = { selected = it },
            icon = Icons.Default.Checklist,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 10. SettingsColorPicker
// ---------------------------------------------------------------------------

/**
 * A compact color picker setting with predefined color swatches.
 *
 * @param title The setting name.
 * @param description Explanatory text.
 * @param colors Available color choices.
 * @param selectedColor Currently selected color.
 * @param onColorSelected Callback when a color is chosen.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsColorPicker(
    title: String,
    description: String,
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentDescription: String = "$title color picker"
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colors.forEach { color ->
                    val isSelected = color == selectedColor

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelected) Modifier.border(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                )
                                else Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                            )
                            .clickable { onColorSelected(color) }
                            .semantics {
                                this.role = Role.RadioButton
                                this.selected = isSelected
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsColorPicker")
@Composable
private fun SettingsColorPickerPreview() {
    MaterialTheme {
        SettingsColorPicker(
            title = "Accent Color",
            description = "Choose the app accent color",
            colors = listOf(TrustBlue, ProtectionActive, AlertOrange, AlertRed, SeverityMedium),
            selectedColor = TrustBlue,
            onColorSelected = {},
            icon = Icons.Default.ColorLens,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 11. SettingsDangerButton
// ---------------------------------------------------------------------------

/**
 * A prominent danger/destructive action button for settings like
 * "Reset All" or "Delete Data".
 *
 * @param title Button text.
 * @param description Explanatory text.
 * @param onClick Callback when clicked (after confirmation if enabled).
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param requireConfirmation Whether to show a confirmation dialog.
 * @param confirmationTitle Dialog title text.
 * @param confirmationMessage Dialog body text.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsDangerButton(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Warning,
    requireConfirmation: Boolean = true,
    confirmationTitle: String = "Confirm Action",
    confirmationMessage: String = "This action cannot be undone. Are you sure?",
    contentDescription: String = title
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog && requireConfirmation) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(confirmationTitle) },
            text = { Text(confirmationMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClick()
                        showDialog = false
                    }
                ) {
                    Text("Confirm", color = AlertRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AlertRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = AlertRed
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    if (requireConfirmation) showDialog = true
                    else onClick()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(AlertRed.copy(alpha = 0.5f))
                )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title)
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsDangerButton - Reset")
@Composable
private fun SettingsDangerButtonResetPreview() {
    MaterialTheme {
        SettingsDangerButton(
            title = "Reset All Settings",
            description = "Restore all settings to their default values",
            onClick = {},
            icon = Icons.Default.RestartAlt,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsDangerButton - Delete")
@Composable
private fun SettingsDangerButtonDeletePreview() {
    MaterialTheme {
        SettingsDangerButton(
            title = "Clear Detection History",
            description = "Permanently delete all detection records",
            onClick = {},
            icon = Icons.Default.DeleteForever,
            confirmationTitle = "Delete All History?",
            confirmationMessage = "All detection records will be permanently deleted. This cannot be undone.",
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 12. SettingsSearchBar
// ---------------------------------------------------------------------------

/**
 * A search bar for filtering settings items.
 *
 * @param query Current search query.
 * @param onQueryChange Callback for query changes.
 * @param modifier Modifier for layout.
 * @param placeholder Placeholder text.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search settings...",
    contentDescription: String = "Search settings"
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = contentDescription
            },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { focusManager.clearFocus() }
        )
    )
}

@Preview(showBackground = true, name = "SettingsSearchBar - Empty")
@Composable
private fun SettingsSearchBarEmptyPreview() {
    MaterialTheme {
        SettingsSearchBar(
            query = "",
            onQueryChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsSearchBar - With Query")
@Composable
private fun SettingsSearchBarWithQueryPreview() {
    MaterialTheme {
        SettingsSearchBar(
            query = "clipboard",
            onQueryChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 13. SettingsStepper
// ---------------------------------------------------------------------------

/**
 * A numeric stepper control for incrementing/decrementing integer values.
 *
 * @param title The setting name.
 * @param description Explanatory text.
 * @param value Current integer value.
 * @param onValueChange Callback for value changes.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param minValue Minimum allowed value.
 * @param maxValue Maximum allowed value.
 * @param stepSize Increment/decrement step size.
 * @param valueLabel Formatter for the displayed value.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SettingsStepper(
    title: String,
    description: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    minValue: Int = 0,
    maxValue: Int = 100,
    stepSize: Int = 1,
    valueLabel: String = "$value",
    contentDescription: String = "$title stepper"
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledIconButton(
                    onClick = { onValueChange((value - stepSize).coerceAtLeast(minValue)) },
                    enabled = value > minValue,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }

                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.defaultMinSize(minWidth = 40.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                FilledIconButton(
                    onClick = { onValueChange((value + stepSize).coerceAtMost(maxValue)) },
                    enabled = value < maxValue,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsStepper")
@Composable
private fun SettingsStepperPreview() {
    MaterialTheme {
        var value by remember { mutableIntStateOf(5) }
        SettingsStepper(
            title = "Max Alerts Per Hour",
            description = "Limit how many alerts can fire per hour",
            value = value,
            onValueChange = { value = it },
            icon = Icons.Default.NotificationsActive,
            minValue = 1,
            maxValue = 50,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 14. SettingsToggleChips
// ---------------------------------------------------------------------------

/**
 * A row of filter/toggle chips for quick binary settings.
 *
 * @param title The group label.
 * @param chips Map of chip label to enabled state.
 * @param onChipToggled Callback with label and new state.
 * @param modifier Modifier for layout.
 * @param icon Optional leading icon.
 * @param contentDescription Accessibility label.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsToggleChips(
    title: String,
    chips: Map<String, Boolean>,
    onChipToggled: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentDescription: String = "$title chip group"
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    this.contentDescription = contentDescription
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chips.forEach { (label, enabled) ->
                    FilterChip(
                        selected = enabled,
                        onClick = { onChipToggled(label, !enabled) },
                        label = { Text(label) },
                        leadingIcon = if (enabled) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "SettingsToggleChips")
@Composable
private fun SettingsToggleChipsPreview() {
    MaterialTheme {
        SettingsToggleChips(
            title = "Quick Filters",
            chips = mapOf(
                "Credit Cards" to true,
                "SSN" to true,
                "Passwords" to false,
                "Emails" to true,
                "Phone" to false,
                "API Keys" to true
            ),
            onChipToggled = { _, _ -> },
            icon = Icons.Default.FilterAlt,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// All Form Components Gallery Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Form Components Gallery")
@Composable
private fun FormComponentsGalleryPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Form Components Gallery",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            SettingsSwitch(
                title = "Example Toggle",
                description = "An example toggle switch",
                checked = true,
                onCheckedChange = {},
                icon = Icons.Default.ToggleOn
            )

            SettingsInfoRow(
                title = "Version",
                value = "1.0.0",
                icon = Icons.Default.Info
            )

            SettingsNavigationRow(
                title = "Sub-Settings",
                subtitle = "Tap to navigate",
                icon = Icons.Default.Settings
            )
        }
    }
}
