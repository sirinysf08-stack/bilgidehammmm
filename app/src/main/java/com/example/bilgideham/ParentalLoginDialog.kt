package com.example.bilgideham

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ParentalLoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    var hintAnswerInput by remember { mutableStateOf("") }
    
    val hintQuestion = remember { ParentalPrefs.getHintQuestion(context) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Tam ekran hissi verebilir ama dialog iyidir
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Rounded.Lock,
                    null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    "Veli Girişi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                if (!showHint) {
                    // --- PIN GİRİŞİ ---
                    Text("Devam etmek için 4 haneli PIN'i girin.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { 
                            if (it.length <= 4) pin = it 
                            if (it.length == 4) {
                                if (ParentalPrefs.validatePin(context, it)) {
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Hatalı PIN!", Toast.LENGTH_SHORT).show()
                                    pin = ""
                                }
                            }
                        },
                        label = { Text("PIN") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    TextButton(onClick = { 
                        if (hintQuestion != null) {
                            showHint = true 
                        } else {
                            val supportId = ParentalPrefs.getSupportId(context)
                            Toast.makeText(context, "Destek ID: $supportId panoya kopyalandı (simüle)", Toast.LENGTH_LONG).show()
                            showHint = true // Fallback to show hint UI to allow master key entry
                        }
                    }) {
                        Text("Şifremi Unuttum?")
                    }
                } else {
                    // --- İPUCU / DESTEK ---
                    val supportId = remember { ParentalPrefs.getSupportId(context) }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Destek ID: $supportId",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Şifre sıfırlama için bu ID ile\nbilgideham@gmail.com\nadresine e-posta gönderin.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    if (hintQuestion != null) {
                        Text(
                            "Veya Gizli Soru:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            hintQuestion ?: "",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    OutlinedTextField(
                        value = hintAnswerInput,
                        onValueChange = { hintAnswerInput = it },
                        label = { Text(if (hintQuestion!=null) "Cevap veya Sıfırlama Kodu" else "Sıfırlama Kodu") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Button(
                        onClick = {
                            var success = false
                            if (hintQuestion != null && ParentalPrefs.validateHintAnswer(context, hintAnswerInput)) {
                                success = true
                            } else if (ParentalPrefs.validateMasterKey(context, hintAnswerInput)) {
                                success = true
                            }

                            if (success) {
                                Toast.makeText(context, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            } else {
                                Toast.makeText(context, "Hatalı Cevap veya Kod!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Doğrula ve Aç")
                    }
                    
                    TextButton(onClick = { showHint = false }) {
                        Text("PIN ile Giriş")
                    }
                }
                
                // İptal Butonu
                TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("İptal")
                }
            }
        }
    }
}
