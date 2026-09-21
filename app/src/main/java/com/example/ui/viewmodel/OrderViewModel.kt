package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ServiceOrder
import com.example.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardStats(
    val totalOrders: Int = 0,
    val pendingCount: Int = 0,
    val processingCount: Int = 0,
    val completedCount: Int = 0,
    val totalRevenue: Double = 0.0
)

data class OrderFormState(
    val id: Long = 0,
    val customerName: String = "",
    val phone: String = "",
    val passportNo: String = "",
    val serviceType: String = ServiceOrder.CATEGORIES.first().bengali,
    val amount: String = "",
    val status: String = ServiceOrder.STATUS_PENDING,
    val notes: String = "",
    val customerNameError: String? = null,
    val phoneError: String? = null,
    val isEditing: Boolean = false
)

sealed class UiMessage {
    data class Success(val message: String) : UiMessage()
    data class Error(val message: String) : UiMessage()
}

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrderRepository
    init {
        val db = AppDatabase.getDatabase(application)
        repository = OrderRepository(db.orderDao())
        seedSampleOrdersIfEmpty()
    }

    private fun seedSampleOrdersIfEmpty() {
        viewModelScope.launch {
            repository.allOrders.firstOrNull()?.let { existing ->
                if (existing.isEmpty()) {
                    val sampleOrders = listOf(
                        ServiceOrder(
                            customerName = "আব্দুল্লাহ আল মামুন",
                            phone = "01712345678",
                            passportNo = "A04829104",
                            serviceType = "যাওযায়াত",
                            status = ServiceOrder.STATUS_COMPLETED,
                            amount = 1200.0,
                            createdAt = System.currentTimeMillis() - 7200000,
                            notes = "ইকামা নবায়ন সফল"
                        ),
                        ServiceOrder(
                            customerName = "মোহাম্মদ হাসান",
                            phone = "01898765432",
                            passportNo = "B09238411",
                            serviceType = "টিকেট বুকিং",
                            status = ServiceOrder.STATUS_PROCESSING,
                            amount = 35000.0,
                            createdAt = System.currentTimeMillis() - 14400000,
                            notes = "ঢাকা টু রিয়াদ ফ্লাইট বুকিং"
                        ),
                        ServiceOrder(
                            customerName = "কামাল হোসেন",
                            phone = "01955667788",
                            passportNo = "C01928374",
                            serviceType = "অনলাইনের কাজ",
                            status = ServiceOrder.STATUS_PENDING,
                            amount = 350.0,
                            createdAt = System.currentTimeMillis() - 28800000,
                            notes = "অনলাইন সার্টিফিকেট আবেদন"
                        ),
                        ServiceOrder(
                            customerName = "রফিকুল ইসলাম",
                            phone = "01611223344",
                            passportNo = "D08291029",
                            serviceType = "টি ইউ ভি কার্ড",
                            status = ServiceOrder.STATUS_PENDING,
                            amount = 850.0,
                            createdAt = System.currentTimeMillis() - 43200000,
                            notes = "ড্রাইভিং ও টেকনিক্যাল কার্ড"
                        ),
                        ServiceOrder(
                            customerName = "মো: জসিম উদ্দিন",
                            phone = "01544332211",
                            passportNo = "E03910283",
                            serviceType = "ইন্স্যুরেন্স",
                            status = ServiceOrder.STATUS_PROCESSING,
                            amount = 2400.0,
                            createdAt = System.currentTimeMillis() - 86400000,
                            notes = "১ বছরের হেলথ ইন্স্যুরেন্স"
                        ),
                        ServiceOrder(
                            customerName = "শফিকুর রহমান",
                            phone = "01322334455",
                            passportNo = "",
                            serviceType = "ফটোকপি",
                            status = ServiceOrder.STATUS_COMPLETED,
                            amount = 50.0,
                            createdAt = System.currentTimeMillis() - 100000000,
                            notes = "১০ পাতা কালার ফটোকপি"
                        )
                    )
                    sampleOrders.forEach { repository.saveOrder(it) }
                }
            }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<String?>(null)
    val selectedStatusFilter: StateFlow<String?> = _selectedStatusFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _backendUrl = MutableStateFlow("http://10.0.2.2:3000")
    val backendUrl: StateFlow<String> = _backendUrl.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _formState = MutableStateFlow(OrderFormState())
    val formState: StateFlow<OrderFormState> = _formState.asStateFlow()

    private val _isFormVisible = MutableStateFlow(false)
    val isFormVisible: StateFlow<Boolean> = _isFormVisible.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiMessage>()
    val uiEvents: SharedFlow<UiMessage> = _uiEvents.asSharedFlow()

    val allOrders: StateFlow<List<ServiceOrder>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredOrders: StateFlow<List<ServiceOrder>> = combine(
        allOrders,
        _searchQuery,
        _selectedStatusFilter,
        _selectedCategoryFilter
    ) { orders, query, statusFilter, categoryFilter ->
        orders.filter { order ->
            val matchesQuery = query.isBlank() ||
                order.customerName.contains(query, ignoreCase = true) ||
                order.phone.contains(query, ignoreCase = true) ||
                order.passportNo.contains(query, ignoreCase = true) ||
                order.serviceType.contains(query, ignoreCase = true)

            val matchesStatus = statusFilter == null || order.status.equals(statusFilter, ignoreCase = true)

            val matchesCategory = categoryFilter == null ||
                order.serviceType.equals(categoryFilter, ignoreCase = true) ||
                ServiceOrder.getCategoryDisplayName(order.serviceType).equals(categoryFilter, ignoreCase = true)

            matchesQuery && matchesStatus && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stats: StateFlow<DashboardStats> = allOrders.combine(MutableStateFlow(Unit)) { orders, _ ->
        val total = orders.size
        val pending = orders.count { it.status.equals(ServiceOrder.STATUS_PENDING, ignoreCase = true) }
        val processing = orders.count { it.status.equals(ServiceOrder.STATUS_PROCESSING, ignoreCase = true) }
        val completed = orders.count { it.status.equals(ServiceOrder.STATUS_COMPLETED, ignoreCase = true) }
        val revenue = orders.sumOf { it.amount }

        DashboardStats(
            totalOrders = total,
            pendingCount = pending,
            processingCount = processing,
            completedCount = completed,
            totalRevenue = revenue
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardStats()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterSelected(status: String?) {
        _selectedStatusFilter.value = if (_selectedStatusFilter.value == status) null else status
    }

    fun onCategoryFilterSelected(category: String?) {
        _selectedCategoryFilter.value = if (_selectedCategoryFilter.value == category) null else category
    }

    fun setBackendUrl(url: String) {
        _backendUrl.value = url.trim()
    }

    fun openCreateDialog() {
        _formState.value = OrderFormState()
        _isFormVisible.value = true
    }

    fun openEditDialog(order: ServiceOrder) {
        _formState.value = OrderFormState(
            id = order.id,
            customerName = order.customerName,
            phone = order.phone,
            passportNo = order.passportNo,
            serviceType = ServiceOrder.getCategoryDisplayName(order.serviceType),
            amount = if (order.amount > 0) order.amount.toString() else "",
            status = order.status,
            notes = order.notes,
            isEditing = true
        )
        _isFormVisible.value = true
    }

    fun closeFormDialog() {
        _isFormVisible.value = false
        _formState.value = OrderFormState()
    }

    fun onCustomerNameChanged(value: String) {
        _formState.value = _formState.value.copy(
            customerName = value,
            customerNameError = if (value.isBlank()) "কাস্টমারের নাম প্রদান করুন" else null
        )
    }

    fun onPhoneChanged(value: String) {
        _formState.value = _formState.value.copy(
            phone = value,
            phoneError = if (value.isBlank()) "মোবাইল নম্বর প্রদান করুন" else null
        )
    }

    fun onPassportChanged(value: String) {
        _formState.value = _formState.value.copy(passportNo = value)
    }

    fun onServiceTypeChanged(value: String) {
        _formState.value = _formState.value.copy(serviceType = value)
    }

    fun onAmountChanged(value: String) {
        _formState.value = _formState.value.copy(amount = value)
    }

    fun onStatusChanged(value: String) {
        _formState.value = _formState.value.copy(status = value)
    }

    fun onNotesChanged(value: String) {
        _formState.value = _formState.value.copy(notes = value)
    }

    fun saveOrder() {
        val form = _formState.value
        var hasError = false

        if (form.customerName.isBlank()) {
            _formState.value = _formState.value.copy(customerNameError = "কাস্টমারের নাম প্রদান করুন")
            hasError = true
        }
        if (form.phone.isBlank()) {
            _formState.value = _formState.value.copy(phoneError = "মোবাইল নম্বর প্রদান করুন")
            hasError = true
        }

        if (hasError) return

        val parsedAmount = form.amount.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            try {
                if (form.isEditing) {
                    val order = ServiceOrder(
                        id = form.id,
                        customerName = form.customerName.trim(),
                        phone = form.phone.trim(),
                        passportNo = form.passportNo.trim(),
                        serviceType = form.serviceType,
                        status = form.status,
                        amount = parsedAmount,
                        notes = form.notes.trim()
                    )
                    repository.updateOrder(order)
                    _uiEvents.emit(UiMessage.Success("অর্ডার সফলভাবে আপডেট হয়েছে"))
                } else {
                    val order = ServiceOrder(
                        customerName = form.customerName.trim(),
                        phone = form.phone.trim(),
                        passportNo = form.passportNo.trim(),
                        serviceType = form.serviceType,
                        status = form.status,
                        amount = parsedAmount,
                        notes = form.notes.trim(),
                        createdAt = System.currentTimeMillis()
                    )
                    repository.saveOrder(order, _backendUrl.value.ifBlank { null })
                    _uiEvents.emit(UiMessage.Success("নতুন সার্ভিস অর্ডার সফলভাবে সেভ হয়েছে"))
                }
                closeFormDialog()
            } catch (e: Exception) {
                _uiEvents.emit(UiMessage.Error("সংরক্ষণে সমস্যা হয়েছে: ${e.message}"))
            }
        }
    }

    fun updateStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, newStatus)
                _uiEvents.emit(UiMessage.Success("স্ট্যাটাস পরিবর্তন করা হয়েছে: $newStatus"))
            } catch (e: Exception) {
                _uiEvents.emit(UiMessage.Error("ত্রুটি: ${e.message}"))
            }
        }
    }

    fun deleteOrder(order: ServiceOrder) {
        viewModelScope.launch {
            try {
                repository.deleteOrder(order)
                _uiEvents.emit(UiMessage.Success("অর্ডারটি সফলভাবে মুছে ফেলা হয়েছে"))
            } catch (e: Exception) {
                _uiEvents.emit(UiMessage.Error("মুছতে ব্যর্থ হয়েছে: ${e.message}"))
            }
        }
    }

    fun syncWithBackend() {
        val url = _backendUrl.value.trim()
        if (url.isBlank()) {
            viewModelScope.launch {
                _uiEvents.emit(UiMessage.Error("অনুগ্রহ করে সঠিক সার্ভার URL লিখুন"))
            }
            return
        }

        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncWithBackend(url)
            _isSyncing.value = false
            result.onSuccess { count ->
                _uiEvents.emit(UiMessage.Success("সার্ভার থেকে $count টি অর্ডার সিঙ্ক হয়েছে"))
            }.onFailure { err ->
                _uiEvents.emit(UiMessage.Error("সিঙ্ক ব্যর্থ: ${err.message ?: "কানেকশন এরর"}"))
            }
        }
    }
}
