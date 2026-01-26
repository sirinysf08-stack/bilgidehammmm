package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDeleteScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isAuthenticated by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteMessage by remember { mutableStateOf("") }
    var deleteMessageType by remember { mutableStateOf(MessageType.INFO) }

    val CORRECT_PASSWORD = "787878"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "🔒 Gizli Silme Paneli",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB71C1C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (!isAuthenticated) {
                // ŞİFRE GİRİŞ EKRANI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFFB71C1C)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        "Bu alan şifre korumalıdır",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    
                    Text(
                        "Devam etmek için şifreyi girin",
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { 
                            passwordInput = it
                            passwordError = false
                        },
                        label = { Text("Şifre") },
                        placeholder = { Text("6 haneli şifre") },
                        visualTransformation = if (passwordVisible) 
                            VisualTransformation.None 
                        else 
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) 
                                        Icons.Default.Visibility 
                                    else 
                                        Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Gizle" else "Göster"
                                )
                            }
                        },
                        isError = passwordError,
                        supportingText = if (passwordError) {
                            { Text("Yanlış şifre!", color = Color.Red) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            if (passwordInput == CORRECT_PASSWORD) {
                                isAuthenticated = true
                                passwordError = false
                            } else {
                                passwordError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB71C1C)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kilidi Aç", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // SİLME İŞLEMLERİ EKRANI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // UYARI MESAJI
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF6F00),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "⚠️ Dikkat!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFFF6F00)
                                )
                                Text(
                                    "Silme işlemleri geri alınamaz. Dikkatli olun!",
                                    fontSize = 14.sp,
                                    color = Color(0xFF424242)
                                )
                            }
                        }
                    }

                    // MESAJ GÖSTERME
                    if (deleteMessage.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (deleteMessageType) {
                                    MessageType.SUCCESS -> Color(0xFFE8F5E9)
                                    MessageType.ERROR -> Color(0xFFFFEBEE)
                                    MessageType.WARNING -> Color(0xFFFFF3E0)
                                    MessageType.INFO -> Color(0xFFE3F2FD)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    when (deleteMessageType) {
                                        MessageType.SUCCESS -> Icons.Default.CheckCircle
                                        MessageType.ERROR -> Icons.Default.Error
                                        MessageType.WARNING -> Icons.Default.Warning
                                        MessageType.INFO -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = when (deleteMessageType) {
                                        MessageType.SUCCESS -> Color(0xFF2E7D32)
                                        MessageType.ERROR -> Color(0xFFC62828)
                                        MessageType.WARNING -> Color(0xFFFF6F00)
                                        MessageType.INFO -> Color(0xFF1976D2)
                                    }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    deleteMessage,
                                    fontSize = 14.sp,
                                    color = Color(0xFF212121)
                                )
                            }
                        }
                    }

                    // 1. TÜM SORULARI SİL
                    DeleteCard(
                        title = "Tüm Soruları Sil",
                        description = "Dikkat: Geri alınamaz!",
                        icon = Icons.Default.Delete,
                        backgroundColor = Color(0xFFFFEBEE),
                        iconColor = Color(0xFFB71C1C),
                        isDeleting = isDeleting,
                        onDelete = {
                            scope.launch {
                                isDeleting = true
                                deleteMessage = "🗑️ Tüm sorular siliniyor..."
                                deleteMessageType = MessageType.WARNING
                                
                                try {
                                    withContext(Dispatchers.IO) {
                                        QuestionRepository.deleteAllQuestionsFromFirestore()
                                    }
                                    deleteMessage = "✅ Tüm sorular başarıyla silindi!"
                                    deleteMessageType = MessageType.SUCCESS
                                } catch (e: Exception) {
                                    deleteMessage = "❌ Hata: ${e.message}"
                                    deleteMessageType = MessageType.ERROR
                                }
                                
                                isDeleting = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. SEVİYE BAZLI SİLME
                    Text(
                        "Seviye Bazlı Silme",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Text(
                        "Sadece seçilen seviyenin soruları silinir",
                        fontSize = 13.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    EducationLevel.entries.forEach { level ->
                        DeleteLevelCard(
                            level = level,
                            isDeleting = isDeleting,
                            onDelete = {
                                scope.launch {
                                    isDeleting = true
                                    deleteMessage = "🗑️ ${level.displayName} soruları siliniyor..."
                                    deleteMessageType = MessageType.WARNING
                                    
                                    try {
                                        val deleted = withContext(Dispatchers.IO) {
                                            QuestionRepository.deleteQuestionsByLevel(level)
                                        }
                                        deleteMessage = "✅ ${level.displayName}: $deleted soru silindi"
                                        deleteMessageType = MessageType.SUCCESS
                                    } catch (e: Exception) {
                                        deleteMessage = "❌ Hata: ${e.message}"
                                        deleteMessageType = MessageType.ERROR
                                    }
                                    
                                    isDeleting = false
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. PARAGRAF SORULARINI SİL
                    DeleteCard(
                        title = "Tüm Paragraf Sorularını Sil",
                        description = "Ortaokul, Lise, KPSS ve AGS paragraf soruları",
                        icon = Icons.Default.Description,
                        backgroundColor = Color(0xFFF3E5F5),
                        iconColor = Color(0xFF9C27B0),
                        isDeleting = isDeleting,
                        onDelete = {
                            scope.launch {
                                isDeleting = true
                                deleteMessage = "🗑️ Tüm paragraf soruları siliniyor..."
                                deleteMessageType = MessageType.WARNING
                                
                                try {
                                    val deleted = withContext(Dispatchers.IO) {
                                        QuestionRepository.deleteAllParagrafQuestions()
                                    }
                                    deleteMessage = "✅ $deleted paragraf sorusu silindi!"
                                    deleteMessageType = MessageType.SUCCESS
                                } catch (e: Exception) {
                                    deleteMessage = "❌ Hata: ${e.message}"
                                    deleteMessageType = MessageType.ERROR
                                }
                                
                                isDeleting = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. KPSS DENEME PAKETLERİNİ SİL
                    DeleteCard(
                        title = "KPSS Deneme Paketlerini Sil",
                        description = "Tüm deneme sınavı paketlerini ve sorularını siler",
                        icon = Icons.Default.Description,
                        backgroundColor = Color(0xFFE0F2F1),
                        iconColor = Color(0xFF00695C),
                        isDeleting = isDeleting,
                        onDelete = {
                            scope.launch {
                                isDeleting = true
                                deleteMessage = "🗑️ KPSS Deneme paketleri siliniyor..."
                                deleteMessageType = MessageType.WARNING
                                
                                try {
                                    val deleted = withContext(Dispatchers.IO) {
                                        QuestionRepository.deleteAllKpssDenemePackages()
                                    }
                                    deleteMessage = "✅ $deleted KPSS Deneme paketi silindi"
                                    deleteMessageType = MessageType.SUCCESS
                                } catch (e: Exception) {
                                    deleteMessage = "❌ Hata: ${e.message}"
                                    deleteMessageType = MessageType.ERROR
                                }
                                
                                isDeleting = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DeleteCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    isDeleting: Boolean,
    onDelete: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    
    val CORRECT_PASSWORD = "636363"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    description,
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
            }
            
            Button(
                onClick = { 
                    showPasswordDialog = true
                    password = ""
                    passwordError = false
                },
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = iconColor,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sil", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    
    // Şifre giriş dialogu
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false 
                password = ""
                passwordError = false
            },
            title = { Text("🔐 Şifre Gerekli", fontWeight = FontWeight.Bold) },
            text = { 
                Column {
                    Text("Bu işlemi onaylamak için yetkilendirme şifresini girin:")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = false
                        },
                        label = { Text("Şifre") },
                        placeholder = { Text("6 haneli şifre") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = passwordError,
                        supportingText = if (passwordError) {
                            { Text("Yanlış şifre!", color = Color.Red) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        if (password == CORRECT_PASSWORD) {
                            showPasswordDialog = false
                            showConfirmDialog = true
                            password = ""
                        } else {
                            passwordError = true
                        }
                    }
                ) {
                    Text("Doğrula", color = iconColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showPasswordDialog = false 
                    password = ""
                    passwordError = false
                }) { 
                    Text("İptal") 
                }
            }
        )
    }
    
    // Son onay dialogu
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("⚠️ Son Onay", fontWeight = FontWeight.Bold, color = iconColor) },
            text = { Text("$title işlemi geri alınamaz. Emin misiniz?") },
            confirmButton = {
                TextButton(onClick = { 
                    showConfirmDialog = false
                    onDelete() 
                }) {
                    Text("Evet, Sil", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("İptal") }
            }
        )
    }
}

@Composable
private fun DeleteLevelCard(
    level: EducationLevel,
    isDeleting: Boolean,
    onDelete: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    
    val CORRECT_PASSWORD = "636363"
    
    val (emoji, color) = when (level) {
        EducationLevel.ILKOKUL -> "📚" to Color(0xFF4CAF50)
        EducationLevel.ORTAOKUL -> "📘" to Color(0xFF2196F3)
        EducationLevel.LISE -> "📕" to Color(0xFFE91E63)
        EducationLevel.KPSS -> "🎓" to Color(0xFF9C27B0)
        EducationLevel.AGS -> "🏛️" to Color(0xFFFF9800)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Text(
                level.displayName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = { 
                    showPasswordDialog = true
                    password = ""
                    passwordError = false
                },
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = color,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sil", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    
    // Şifre giriş dialogu
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false 
                password = ""
                passwordError = false
            },
            title = { Text("🔐 Şifre Gerekli", fontWeight = FontWeight.Bold) },
            text = { 
                Column {
                    Text("${level.displayName} sorularını silmek için yetkilendirme şifresini girin:")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = false
                        },
                        label = { Text("Şifre") },
                        placeholder = { Text("6 haneli şifre") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = passwordError,
                        supportingText = if (passwordError) {
                            { Text("Yanlış şifre!", color = Color.Red) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        if (password == CORRECT_PASSWORD) {
                            showPasswordDialog = false
                            showConfirmDialog = true
                            password = ""
                        } else {
                            passwordError = true
                        }
                    }
                ) {
                    Text("Doğrula", color = color)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showPasswordDialog = false 
                    password = ""
                    passwordError = false
                }) { 
                    Text("İptal") 
                }
            }
        )
    }
    
    // Son onay dialogu
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("⚠️ Son Onay", fontWeight = FontWeight.Bold, color = color) },
            text = { Text("${level.displayName} soruları kalıcı olarak silinecek. Bu işlem geri alınamaz!") },
            confirmButton = {
                TextButton(onClick = { 
                    showConfirmDialog = false
                    onDelete() 
                }) {
                    Text("Evet, Sil", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("İptal") }
            }
        )
    }
}

private enum class MessageType {
    SUCCESS, ERROR, WARNING, INFO
}
