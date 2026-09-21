package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.viewmodel.DashboardStats
import java.util.Locale

@Composable
fun DashboardStatsView(
    stats: DashboardStats,
    selectedStatusFilter: String?,
    onStatusFilterClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_stats_row"),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Total Orders Card
        item {
            StatCard(
                title = "মোট অর্ডার",
                value = stats.totalOrders.toString(),
                subtitle = "সকল এন্ট্রি",
                icon = Icons.Default.Inventory2,
                color = MaterialTheme.colorScheme.primary,
                bgColor = MaterialTheme.colorScheme.primaryContainer,
                isSelected = selectedStatusFilter == null,
                onClick = { onStatusFilterClick(null) },
                testTag = "stat_card_total"
            )
        }

        // Pending Card
        item {
            StatCard(
                title = "পেন্ডিং",
                value = stats.pendingCount.toString(),
                subtitle = "অপেক্ষমাণ",
                icon = Icons.Default.HourglassTop,
                color = StatusPendingColor,
                bgColor = StatusPendingBg,
                isSelected = selectedStatusFilter == ServiceOrder.STATUS_PENDING,
                onClick = { onStatusFilterClick(ServiceOrder.STATUS_PENDING) },
                testTag = "stat_card_pending"
            )
        }

        // Processing Card
        item {
            StatCard(
                title = "প্রসেসিং",
                value = stats.processingCount.toString(),
                subtitle = "চলমান কাজ",
                icon = Icons.Default.Sync,
                color = StatusProcessingColor,
                bgColor = StatusProcessingBg,
                isSelected = selectedStatusFilter == ServiceOrder.STATUS_PROCESSING,
                onClick = { onStatusFilterClick(ServiceOrder.STATUS_PROCESSING) },
                testTag = "stat_card_processing"
            )
        }

        // Completed Card
        item {
            StatCard(
                title = "সম্পন্ন",
                value = stats.completedCount.toString(),
                subtitle = "ডেলিভারি সম্পন্ন",
                icon = Icons.Default.CheckCircle,
                color = StatusCompletedColor,
                bgColor = StatusCompletedBg,
                isSelected = selectedStatusFilter == ServiceOrder.STATUS_COMPLETED,
                onClick = { onStatusFilterClick(ServiceOrder.STATUS_COMPLETED) },
                testTag = "stat_card_completed"
            )
        }

        // Total Amount Card
        item {
            StatCard(
                title = "মোট বিল",
                value = String.format(Locale.getDefault(), "৳ %.0f", stats.totalRevenue),
                subtitle = "মোট আদায়",
                icon = Icons.Default.Payments,
                color = Color(0xFF0F766E),
                bgColor = Color(0xFFCCFBF1),
                isSelected = false,
                onClick = {},
                testTag = "stat_card_revenue"
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .width(135.dp)
            .height(115.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = color
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
