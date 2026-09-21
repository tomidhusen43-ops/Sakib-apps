package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ServiceOrder
import com.example.ui.viewmodel.OrderFormState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderFormDialog(
    formState: OrderFormState,
    onCustomerNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPassportChange: (String) -> Unit,
    onServiceTypeChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("order_form_surface"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with Bengali title matching user prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (formState.isEditing) "অর্ডার এডিট করুন" else "সার্ভিস ম্যানেজমেন্ট ড্যাশবোর্ড",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("form_title")
                        )
                        Text(
                            text = if (formState.isEditing) "গ্রাহকের তথ্য পরিবর্তন করুন" else "নতুন গ্রাহক ও সেবার তথ্য এন্ট্রি",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_form")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // etCustomerName
                OutlinedTextField(
                    value = formState.customerName,
                    onValueChange = onCustomerNameChange,
                    label = { Text("কাস্টমারের নাম *") },
                    placeholder = { Text("যেমন: আব্দুল্লাহ করিম") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    isError = formState.customerNameError != null,
                    supportingText = {
                        if (formState.customerNameError != null) {
                            Text(formState.customerNameError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("etCustomerName"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // etPhone
                OutlinedTextField(
                    value = formState.phone,
                    onValueChange = onPhoneChange,
                    label = { Text("মোবাইল নম্বর *") },
                    placeholder = { Text("যেমন: 0501234567 বা +8801...") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = formState.phoneError != null,
                    supportingText = {
                        if (formState.phoneError != null) {
                            Text(formState.phoneError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("etPhone"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Passport No
                OutlinedTextField(
                    value = formState.passportNo,
                    onValueChange = onPassportChange,
                    label = { Text("পাসপোর্ট নম্বর (ঐচ্ছিক)") },
                    placeholder = { Text("যেমন: A12345678") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("etPassportNo"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // spServiceType - Spinner / Dropdown & Quick Selection Chips
                Text(
                    text = "সার্ভিস টাইপ (Service Type)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = formState.serviceType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("সার্ভিস ক্যাটাগরি") },
                        leadingIcon = {
                            Icon(Icons.Default.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "ড্রপডাউন খুলুন",
                                modifier = Modifier.clickable { expandedDropdown = !expandedDropdown }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedDropdown = true }
                            .testTag("spServiceType"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        enabled = false
                    )

                    // Overlay box to catch clicks reliably
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expandedDropdown = true }
                    )

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.testTag("service_dropdown_menu")
                    ) {
                        ServiceOrder.CATEGORIES.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = category.bengali,
                                            fontWeight = if (category.bengali == formState.serviceType) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = "${category.english} - ${category.description}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    onServiceTypeChange(category.bengali)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick selector chips for the categories
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ServiceOrder.CATEGORIES.forEach { category ->
                        val isSelected = formState.serviceType == category.bengali
                        FilterChip(
                            selected = isSelected,
                            onClick = { onServiceTypeChange(category.bengali) },
                            label = { Text(category.bengali, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("chip_${category.english.replace(" ", "_")}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount
                OutlinedTextField(
                    value = formState.amount,
                    onValueChange = onAmountChange,
                    label = { Text("টাকার পরিমাণ / ফি (Amount)") },
                    placeholder = { Text("যেমন: 500 বা 50") },
                    leadingIcon = {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("etAmount"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Status selection
                Text(
                    text = "স্ট্যাটাস (Status)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statuses = listOf(
                        Triple(ServiceOrder.STATUS_PENDING, "পেন্ডিং", MaterialTheme.colorScheme.tertiary),
                        Triple(ServiceOrder.STATUS_PROCESSING, "প্রসেসিং", MaterialTheme.colorScheme.secondary),
                        Triple(ServiceOrder.STATUS_COMPLETED, "সম্পন্ন", MaterialTheme.colorScheme.primary)
                    )

                    statuses.forEach { (statusKey, label, color) ->
                        val isSelected = formState.status.equals(statusKey, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onStatusChange(statusKey) }
                                .testTag("status_select_$statusKey"),
                            color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optional notes
                OutlinedTextField(
                    value = formState.notes,
                    onValueChange = onNotesChange,
                    label = { Text("মন্তব্য / নোট (ঐচ্ছিক)") },
                    placeholder = { Text("কোনো বিশেষ নির্দেশাবলী বা ডেলিভারির তারিখ...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("etNotes"),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // btnSave - Matching user button
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btnSave"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (formState.isEditing) "আপডেট করুন" else "সেভ করুন",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
