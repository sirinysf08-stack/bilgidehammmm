package com.example.bilgideham

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentalControlScreen(navController: NavController) {
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme
    
    var pin by remember { mutableStateOf("") }
    var hintQuestion by remember { mutableStateOf(ParentalPrefs.getHintQuestion(context) ?: "") }
    var hintAnswer by remember { mutableStateOf("") }
    var recoveryEmail by remember { mutableStateOf(ParentalPrefs.getRecoveryEmail(context) ?: "") }
    
    // UI States
    val hasPin = remember { ParentalPrefs.hasPin(context) }
    var showPinDialog by remember { mutableStateOf(!hasPin) } // Şifre yoksa hemen oluştur
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ebeveyn Kontrolü", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HESAP KURULUMU VE GÜVENLİK ---
            item {
                if (!hasPin) {
                    // --- İLK KURULUM SİHİRBAZI ---
                    SecuritySetupCard(
                        context = context,
                        cs = cs,
                        onSetupComplete = {
                            // Refresh state to show full settings
                            navController.popBackStack()
                            navController.navigate("parental_control")
                        }
                    )
                } else {
                    // --- NORMAL AYARLAR (Kurulum yapılmış) ---
                    
                    // Veli Raporu Butonu
                    ParentReportCard(navController)
                    
                    Spacer(Modifier.height(8.dp))
                    
                    // 2. PIN Değiştir
                    ChangePinCard(context, cs)
                    
                    // 3. İpucu Ayarları
                    SecretHintCard(context, cs, hintQuestion, hintAnswer) { q, a ->
                        hintQuestion = q
                        hintAnswer = a
                    }
                    
                    // 4. E-posta Güncelle
                    RecoveryEmailCard(context, cs, recoveryEmail) { e ->
                        recoveryEmail = e
                    }
                }
            }
        }
    }
}

// --- ALT BİLEŞENLER ---

@Composable
fun ParentReportCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5E35B1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("parent_report") }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Assessment,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Veli Raporu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    "Öğrenci performans analizi ve öneriler",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun SecuritySetupCard(context: android.content.Context, cs: ColorScheme, onSetupComplete: () -> Unit) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), // Turuncu tonu (Uyarı/Kurulum)
        border = BorderStroke(1.dp, Color(0xFFFFB74D))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Security, null, tint = Color(0xFFF57C00), modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Güvenlik Kurulumu", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFE65100))
                    Text("Devam etmek için bir şifre belirleyin.", fontSize = 12.sp, color = Color(0xFFEF6C00))
                }
            }
            
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFFFB74D).copy(alpha = 0.5f))
            
            // 1. PIN
            OutlinedTextField(
                value = newPin,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPin = it },
                label = { Text("PIN Belirle (4 Rakam)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFFE65100)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF57C00),
                    focusedLabelColor = Color(0xFFF57C00),
                    focusedTextColor = Color(0xFFE65100),
                    unfocusedTextColor = Color(0xFFE65100),
                    cursorColor = Color(0xFFE65100)
                )
            )
            
            // 2. PIN Confirm
            OutlinedTextField(
                value = confirmPin,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirmPin = it },
                label = { Text("PIN Doğrula") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmPin.isNotEmpty() && confirmPin != newPin,
                textStyle = LocalTextStyle.current.copy(color = Color(0xFFE65100)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF57C00),
                    focusedLabelColor = Color(0xFFF57C00),
                    focusedTextColor = Color(0xFFE65100),
                    unfocusedTextColor = Color(0xFFE65100),
                    cursorColor = Color(0xFFE65100)
                )
            )
            if (confirmPin.isNotEmpty() && confirmPin != newPin) {
                Text("PIN'ler eşleşmiyor!", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
            
            // 3. Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Kurtarma E-postası (Zorunlu)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = LocalTextStyle.current.copy(color = Color(0xFFE65100)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF57C00),
                    focusedLabelColor = Color(0xFFF57C00),
                    focusedTextColor = Color(0xFFE65100),
                    unfocusedTextColor = Color(0xFFE65100),
                    cursorColor = Color(0xFFE65100)
                )
            )
            
            Button(
                onClick = {
                    if (newPin.length == 4 && newPin == confirmPin && email.isNotEmpty() && email.contains("@")) {
                        ParentalPrefs.setPin(context, newPin)
                        ParentalPrefs.setRecoveryEmail(context, email)
                        Toast.makeText(context, "Kurulum Tamamlandı!", Toast.LENGTH_SHORT).show()
                        onSetupComplete()
                    } else {
                        Toast.makeText(context, "Lütfen tüm alanları doğru doldurun.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                enabled = newPin.length == 4 && newPin == confirmPin && email.isNotEmpty()
            ) {
                Text("Kaydet ve Devam Et", color = Color.White)
            }
        }
    }
}



@Composable
fun ChangePinCard(context: android.content.Context, cs: ColorScheme) {
    var newPin by remember { mutableStateOf("") }
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cs.surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("PIN Değiştir", fontWeight = FontWeight.Bold, color = cs.onSurface)
            OutlinedTextField(
                value = newPin, onValueChange = { if (it.length <= 4) newPin = it },
                label = { Text("Yeni PIN", color = cs.onSurface.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (newPin.length == 4) IconButton(onClick = { ParentalPrefs.setPin(context, newPin); newPin = ""; Toast.makeText(context, "PIN Güncellendi", Toast.LENGTH_SHORT).show() }) { Icon(Icons.Rounded.Save, null, tint = cs.primary) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(color = cs.onSurface),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = cs.onSurface,
                    unfocusedTextColor = cs.onSurface,
                    cursorColor = cs.primary,
                    focusedBorderColor = cs.primary,
                    unfocusedBorderColor = cs.onSurface.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun SecretHintCard(context: android.content.Context, cs: ColorScheme, q: String, a: String, onChange: (String, String) -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cs.surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Gizli İpucu", fontWeight = FontWeight.Bold, color = cs.onSurface)
            OutlinedTextField(
                value = q, onValueChange = { onChange(it, a) }, 
                label = { Text("Soru", color = cs.onSurface.copy(alpha = 0.7f)) }, 
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = cs.onSurface),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = cs.onSurface,
                    unfocusedTextColor = cs.onSurface
                )
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = a, onValueChange = { onChange(q, it) }, 
                label = { Text("Cevap", color = cs.onSurface.copy(alpha = 0.7f)) }, 
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = cs.onSurface),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = cs.onSurface,
                    unfocusedTextColor = cs.onSurface
                )
            )
            Button(onClick = { ParentalPrefs.setSecretHint(context, q, a); Toast.makeText(context, "Kaydedildi", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth().padding(top=8.dp)) { Text("Güncelle") }
        }
    }
}

@Composable
fun RecoveryEmailCard(context: android.content.Context, cs: ColorScheme, email: String, onChange: (String) -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cs.surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Kurtarma E-postası", fontWeight = FontWeight.Bold, color = cs.onSurface)
            OutlinedTextField(
                value = email, onValueChange = { onChange(it) }, 
                label = { Text("E-posta", color = cs.onSurface.copy(alpha = 0.7f)) }, 
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = cs.onSurface),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = cs.onSurface,
                    unfocusedTextColor = cs.onSurface
                ),
                trailingIcon = { IconButton(onClick = { ParentalPrefs.setRecoveryEmail(context, email); Toast.makeText(context, "Kaydedildi", Toast.LENGTH_SHORT).show() }) { Icon(Icons.Rounded.Save, null, tint = cs.primary) } }
            )
        }
    }
}
