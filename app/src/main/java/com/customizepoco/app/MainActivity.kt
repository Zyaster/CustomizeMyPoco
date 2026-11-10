package com.customizepoco.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.customizepoco.app.provider.ConfigProvider
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF64B5F6),
                    secondary = Color(0xFF81C784),
                    background = Color(0xFF0F141C),
                    surface = Color(0xFF1B222E),
                    surfaceVariant = Color(0xFF263244)
                )
            ) {
                CustomizeMyPocoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeMyPocoScreen() {
    val context = LocalContext.current
    val prefs = remember { ConfigProvider.getPrefs(context) }

    var iconScale by remember { mutableFloatStateOf(prefs.getFloat(ConfigProvider.KEY_ICON_SCALE, 1.0f)) }
    var hideLabels by remember { mutableStateOf(prefs.getBoolean(ConfigProvider.KEY_HIDE_LABELS, false)) }
    var superIcons by remember { mutableStateOf(prefs.getBoolean(ConfigProvider.KEY_SUPER_ICONS, true)) }
    var liquidGlass by remember { mutableStateOf(prefs.getBoolean(ConfigProvider.KEY_LIQUID_GLASS, true)) }
    var deviceLevel by remember { mutableStateOf(prefs.getBoolean(ConfigProvider.KEY_DEVICE_LEVEL, true)) }
    var unlock120Hz by remember { mutableStateOf(prefs.getBoolean(ConfigProvider.KEY_UNLOCK_120HZ, true)) }
    var blurRadius by remember { mutableFloatStateOf(prefs.getInt(ConfigProvider.KEY_BLUR_RADIUS, 45).toFloat()) }

    fun saveConfig() {
        prefs.edit().apply {
            putFloat(ConfigProvider.KEY_ICON_SCALE, iconScale)
            putBoolean(ConfigProvider.KEY_HIDE_LABELS, hideLabels)
            putBoolean(ConfigProvider.KEY_SUPER_ICONS, superIcons)
            putBoolean(ConfigProvider.KEY_LIQUID_GLASS, liquidGlass)
            putBoolean(ConfigProvider.KEY_DEVICE_LEVEL, deviceLevel)
            putBoolean(ConfigProvider.KEY_UNLOCK_120HZ, unlock120Hz)
            putInt(ConfigProvider.KEY_BLUR_RADIUS, blurRadius.toInt())
            apply()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CustomizeMyPoco",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F141C)
                )
            )
        },
        containerColor = Color(0xFF0F141C)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF1E3A8A), Color(0xFF0D9488))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "POCO & HyperOS Suite",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Liquid Glass blur, XXL icons up to 200%, 120Hz lock release & Flagship profile unlock.",
                        fontSize = 13.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            // Section 1: Icons & Launcher
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B222E)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle(
                        icon = Icons.Default.Apps,
                        title = "POCO Launcher & Icons"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Icon Size: ${(iconScale * 100).roundToInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = iconScale,
                        onValueChange = {
                            iconScale = it
                            saveConfig()
                        },
                        valueRange = 0.5f..2.0f,
                        steps = 29, // 5% increments
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF64B5F6),
                            activeTrackColor = Color(0xFF64B5F6)
                        )
                    )

                    HorizontalDivider(color = Color(0xFF263244), modifier = Modifier.padding(vertical = 8.dp))

                    ToggleItem(
                        title = "Hide Icon Labels",
                        subtitle = "Clean minimalist home screen with icons only",
                        checked = hideLabels,
                        onCheckedChange = {
                            hideLabels = it
                            saveConfig()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF263244), modifier = Modifier.padding(vertical = 8.dp))

                    ToggleItem(
                        title = "Unlock XXL 3×3 Folders & Super Icons",
                        subtitle = "Enable big folders and flagship widgets on POCO",
                        checked = superIcons,
                        onCheckedChange = {
                            superIcons = it
                            saveConfig()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            saveConfig()
                            try {
                                Runtime.getRuntime().exec(arrayOf("su", "-c", "pkill -f com.mi.android.globallauncher"))
                                Toast.makeText(context, "POCO Launcher restarted!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Saved! Restart launcher to apply.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apply & Restart Launcher")
                    }
                }
            }

            // Section 2: Visuals & Liquid Glass
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B222E)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle(
                        icon = Icons.Default.BlurOn,
                        title = "SystemUI & Liquid Glass"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ToggleItem(
                        title = "Force Liquid Glass & Gaussian Blur",
                        subtitle = "Replace flat gray scrim in Control Center with real-time blur",
                        checked = liquidGlass,
                        onCheckedChange = {
                            liquidGlass = it
                            saveConfig()
                        }
                    )

                    if (liquidGlass) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Blur Radius: ${blurRadius.roundToInt()} px",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = blurRadius,
                            onValueChange = {
                                blurRadius = it
                                saveConfig()
                            },
                            valueRange = 10f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF81C784),
                                activeTrackColor = Color(0xFF81C784)
                            )
                        )
                    }

                    HorizontalDivider(color = Color(0xFF263244), modifier = Modifier.padding(vertical = 8.dp))

                    ToggleItem(
                        title = "Unlock 120 FPS Everywhere",
                        subtitle = "Disable 60 FPS panel lock when pulling down notifications",
                        checked = unlock120Hz,
                        onCheckedChange = {
                            unlock120Hz = it
                            saveConfig()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF263244), modifier = Modifier.padding(vertical = 8.dp))

                    ToggleItem(
                        title = "Flagship Profile (deviceLevel = 3)",
                        subtitle = "Spoof device capabilities in miui.os.Build",
                        checked = deviceLevel,
                        onCheckedChange = {
                            deviceLevel = it
                            saveConfig()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            saveConfig()
                            try {
                                Runtime.getRuntime().exec(arrayOf("su", "-c", "killall -9 com.android.systemui"))
                                Toast.makeText(context, "SystemUI restarting...", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Saved! Reboot or restart SystemUI to apply.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apply & Restart SystemUI")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF64B5F6), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun ToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2563EB)
            )
        )
    }
}
