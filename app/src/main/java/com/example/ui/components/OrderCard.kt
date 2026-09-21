package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceOrder
import com.example.ui.theme.StatusCompletedBg
import com.example.ui.theme.StatusCompletedColor
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusPendingColor
import com.example.ui.theme.StatusProcessingBg
import com.example.ui.theme.StatusProcessingColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderCard(
    order: ServiceOrder,
    onStatusChange: (Long, String) -> Unit,
    onEdit: (ServiceOrder) -> Unit,
    onDelete: (ServiceOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val statusColor = when (order.status.lowercase(Locale.ROOT)) {
        "completed", "সম্পন্ন" -> StatusCompletedColor
        "processing", "প্রসেসিং" -> StatusProcessingColor
        else -> StatusPendingColor
    }

    val statusBg = when (order.status.lowercase(Locale.ROOT)) {
        "completed", "সম্পন্ন" -> StatusCompletedBg
        "processing", "প্রসেসিং" -> StatusProcessingBg
        else -> StatusPendingBg
    }

    val statusBengaliLabel = when (order.status.lowercase(Locale.ROOT)) {
        "completed", "সম্পন্ন" -> "সম্পন্ন"
        "processing", "প্রসেসিং" -> "প্রসেসিং"
        else -> "পেন্ডিং"
    }

    val serviceCategory = order.serviceType
    val categoryIcon = getCategoryIcon(serviceCategory)

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(order.createdAt) { dateFormat.format(Date(order.createdAt)) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Customer Name and Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = order.customerName.take(1).uppercase(Locale.getDefault()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = order.customerName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status chip with quick changer
                Box {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = statusBg,
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showStatusDropdown = true }
                            .testTag("status_chip_${order.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = statusColor,
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = statusBengaliLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "স্ট্যাটাস পরিবর্তন",
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("পেন্ডিং (Pending)", color = StatusPendingColor, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onStatusChange(order.id, ServiceOrder.STATUS_PENDING)
                                showStatusDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("প্রসেসিং (Processing)", color = StatusProcessingColor, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onStatusChange(order.id, ServiceOrder.STATUS_PROCESSING)
                                showStatusDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("সম্পন্ন (Completed)", color = StatusCompletedColor, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onStatusChange(order.id, ServiceOrder.STATUS_COMPLETED)
                                showStatusDropdown = false
                            }
                        )
                    }
                }

                // Overflow action menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_menu_${order.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "অপশন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            text = { Text("এডিট করুন") },
                            onClick = {
                                showMenu = false
                                onEdit(order)
                            }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            text = { Text("শেয়ার করুন") },
                            onClick = {
                                showMenu = false
                                val shareText = "সার্ভিস স্লিপ:\nকাস্টমার: ${order.customerName}\nমোবাইল: ${order.phone}\nসার্ভিস: ${order.serviceType}\nপাসপোর্ট: ${order.passportNo.ifBlank { "নেই" }}\nবিল: ৳ ${order.amount}\nস্ট্যাটাস: $statusBengaliLabel"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "সার্ভিস তথ্য পাঠান"))
                            }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            text = { Text("মুছে ফেলুন", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                showDeleteConfirmation = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chips Row: Service Type, Passport, Amount
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Service type chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = ServiceOrder.getCategoryDisplayName(order.serviceType),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Passport chip (if provided)
                if (order.passportNo.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = order.passportNo,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Amount chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0F2FE)
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "বিল: ৳ %.0f", order.amount),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Notes if available
            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "নোট: ${order.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer action: Phone call & quick contact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${order.phone}")
                            }
                            context.startActivity(intent)
                        }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "ফোন কল",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.phone,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Direct call button
                FilledTonalIconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${order.phone}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_call_${order.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "কল করুন",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("অর্ডার মুছে ফেলবেন?") },
            text = { Text("${order.customerName} এর ${order.serviceType} অর্ডারটি স্থায়ীভাবে মুছে যাবে।") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete(order)
                    }
                ) {
                    Text("মুছে ফেলুন", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

fun getCategoryIcon(category: String): ImageVector {
    val lower = category.lowercase(Locale.ROOT)
    return when {
        lower.contains("ফটোকপি") || lower.contains("photocopy") -> Icons.Default.Print
        lower.contains("অনলাইন") || lower.contains("online") -> Icons.Default.Web
        lower.contains("যাওযায়াত") || lower.contains("jawazat") || lower.contains("ইকামা") -> Icons.Default.PermIdentity
        lower.contains("টিকেট") || lower.contains("ticket") || lower.contains("flight") -> Icons.Default.Flight
        lower.contains("ইন্স্যুরেন্স") || lower.contains("insurance") -> Icons.Default.MedicalServices
        lower.contains("টি ইউ ভি") || lower.contains("tuv") -> Icons.Default.WorkspacePremium
        else -> Icons.Default.Badge
    }
}
