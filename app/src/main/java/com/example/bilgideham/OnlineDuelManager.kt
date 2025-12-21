package com.example.bilgideham

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class OnlineDuelManager {

    companion object {
        private const val COLLECTION = "online_duels"

        // Status
        const val STATUS_WAITING = "WAITING"
        const val STATUS_STARTED = "STARTED"
        const val STATUS_FINISHED = "FINISHED"

        // Game Types
        const val GAME_QUIZ = "QUIZ"
        const val GAME_STORY = "STORY"

        // Roles
        const val ROLE_HOST = "HOST"
        const val ROLE_JOIN = "JOIN"
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    fun stop() {
        listener?.remove()
        listener = null
    }

    fun generateRoomCode(): String {
        // Confusing karakterleri azalt (O/0, I/1)
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return buildString {
            repeat(6) { append(alphabet[Random.nextInt(alphabet.length)]) }
        }
    }

    /**
     * Oda oluşturma: QUIZ alanları korunur.
     * STORY modu için gereken alanlar da default olarak eklenir (geriye uyumlu).
     */
    suspend fun createRoom(roomCode: String, hostName: String) {
        val doc = db.collection(COLLECTION).document(roomCode)
        val data = hashMapOf(
            "roomCode" to roomCode,
            "status" to STATUS_WAITING, // WAITING, STARTED, FINISHED
            "gameType" to GAME_QUIZ,    // QUIZ / STORY (default QUIZ)
            "hostName" to hostName.trim(),
            "joinName" to "",
            "createdAt" to FieldValue.serverTimestamp(),

            // -------------------------
            // QUIZ (mevcut alanlar)
            // -------------------------
            "questionsJson" to "",
            "currentIndex" to 0,
            "aHost" to "",
            "aJoin" to "",
            "aQ" to -1,
            "roundEnded" to false,
            "gameOver" to false,

            // -------------------------
            // STORY (yeni alanlar)
            // -------------------------
            "storyLevel" to 1,                 // 1..100
            "storySeed" to 0,                  // aynı level için deterministik varyasyon (opsiyonel)
            "storyFinishCounter" to 0L,        // transaction ile artar
            "storyFinishOrderHost" to 0L,      // 1 = ilk bitiren
            "storyFinishOrderJoin" to 0L,
            "storyElapsedMsHost" to -1L,       // client elapsed (informational)
            "storyElapsedMsJoin" to -1L,
            "storyRoundEnded" to false,        // STORY round bitiş flag
            "storyWinner" to ""                // HOST / JOIN
        )
        doc.set(data, SetOptions.merge()).await()
    }

    suspend fun joinRoom(roomCode: String, joinName: String): Boolean {
        val docRef = db.collection(COLLECTION).document(roomCode)
        return db.runTransaction { tx ->
            val snap = tx.get(docRef)
            if (!snap.exists()) return@runTransaction false
            val status = snap.getString("status") ?: STATUS_WAITING
            val existingJoin = snap.getString("joinName") ?: ""
            if (status != STATUS_WAITING) return@runTransaction false
            if (existingJoin.isNotBlank()) return@runTransaction false

            tx.update(docRef, mapOf("joinName" to joinName.trim()))
            true
        }.await()
    }

    fun listenRoom(
        roomCode: String,
        onUpdate: (DocumentSnapshot) -> Unit,
        onError: (String) -> Unit
    ) {
        stop()
        listener = db.collection(COLLECTION).document(roomCode)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    onError(e.message ?: "Firestore listen error")
                    return@addSnapshotListener
                }
                if (snap != null && snap.exists()) onUpdate(snap)
            }
    }

    // -------------------------
    // QUIZ FLOW (mevcut)
    // -------------------------

    suspend fun setQuestionsAndStart(roomCode: String, questionsJson: String) {
        val docRef = db.collection(COLLECTION).document(roomCode)
        val payload = mapOf(
            "status" to STATUS_STARTED,
            "gameType" to GAME_QUIZ,

            "questionsJson" to questionsJson,
            "currentIndex" to 0,
            "aHost" to "",
            "aJoin" to "",
            "aQ" to 0,
            "roundEnded" to false,
            "gameOver" to false
        )
        docRef.set(payload, SetOptions.merge()).await()
    }

    suspend fun sendAnswer(roomCode: String, role: String, qIndex: Int, answer: String) {
        val docRef = db.collection(COLLECTION).document(roomCode)
        val key = if (role == ROLE_HOST) "aHost" else "aJoin"
        val payload = mapOf(
            key to answer,
            "aQ" to qIndex
        )
        docRef.set(payload, SetOptions.merge()).await()
    }

    suspend fun hostEndRound(roomCode: String, qIndex: Int) {
        val docRef = db.collection(COLLECTION).document(roomCode)
        val payload = mapOf(
            "roundEnded" to true,
            "aQ" to qIndex
        )
        docRef.set(payload, SetOptions.merge()).await()
    }

    suspend fun hostNext(roomCode: String, nextIndex: Int, gameOver: Boolean) {
        val docRef = db.collection(COLLECTION).document(roomCode)
        val payload = mapOf(
            "currentIndex" to nextIndex,
            "aHost" to "",
            "aJoin" to "",
            "aQ" to nextIndex,
            "roundEnded" to false,
            "gameOver" to gameOver,
            "status" to if (gameOver) STATUS_FINISHED else STATUS_STARTED
        )
        docRef.set(payload, SetOptions.merge()).await()
    }

    // -------------------------
    // STORY FLOW (yeni)
    // -------------------------

    /**
     * Host STORY oyununu başlatır (level + seed).
     * - finish counter/order resetlenir
     * - winner/roundEnded temizlenir
     */
    suspend fun setStoryAndStart(roomCode: String, levelNo: Int, seed: Int = 0) {
        val docRef = db.collection(COLLECTION).document(roomCode)
        val payload = mapOf(
            "status" to STATUS_STARTED,
            "gameType" to GAME_STORY,

            "storyLevel" to levelNo.coerceIn(1, 100),
            "storySeed" to seed,

            "storyFinishCounter" to 0L,
            "storyFinishOrderHost" to 0L,
            "storyFinishOrderJoin" to 0L,
            "storyElapsedMsHost" to -1L,
            "storyElapsedMsJoin" to -1L,
            "storyRoundEnded" to false,
            "storyWinner" to ""
        )
        docRef.set(payload, SetOptions.merge()).await()
    }

    /**
     * Oyuncu STORY level'ını bitirdiğini bildirir.
     * “İlk bitiren kazanır” mantığı:
     * - Transaction içinde counter++ yapılır ve role’e order atanır.
     * - Karşı taraf da bitirdiyse (iki order da >0), winner belirlenir ve round kapatılır.
     *
     * Not: elapsedMs bilgilendirme amaçlıdır; “kazanan” order ile belirlenir (ilk finish = order 1).
     */
    suspend fun sendStoryFinish(roomCode: String, role: String, levelNo: Int, elapsedMs: Long) {
        val docRef = db.collection(COLLECTION).document(roomCode)

        db.runTransaction { tx ->
            val snap = tx.get(docRef)
            if (!snap.exists()) return@runTransaction

            val gameType = snap.getString("gameType") ?: GAME_QUIZ
            if (gameType != GAME_STORY) return@runTransaction

            val currentLevel = (snap.getLong("storyLevel") ?: 1L).toInt()
            if (currentLevel != levelNo) return@runTransaction

            val counter = snap.getLong("storyFinishCounter") ?: 0L

            val hostOrder = snap.getLong("storyFinishOrderHost") ?: 0L
            val joinOrder = snap.getLong("storyFinishOrderJoin") ?: 0L

            val isHost = role == ROLE_HOST
            val myOrder = if (isHost) hostOrder else joinOrder
            if (myOrder > 0L) {
                // Daha önce finish atmış; tekrar yazma
                return@runTransaction
            }

            val newCounter = counter + 1L
            val updates = hashMapOf<String, Any>(
                "storyFinishCounter" to newCounter
            )

            if (isHost) {
                updates["storyFinishOrderHost"] = newCounter
                updates["storyElapsedMsHost"] = elapsedMs
            } else {
                updates["storyFinishOrderJoin"] = newCounter
                updates["storyElapsedMsJoin"] = elapsedMs
            }

            // Finish sonrası iki taraf da bitirdiyse winner'ı belirle ve round'u kapat
            val newHostOrder = if (isHost) newCounter else hostOrder
            val newJoinOrder = if (!isHost) newCounter else joinOrder

            if (newHostOrder > 0L && newJoinOrder > 0L) {
                val winner = if (newHostOrder < newJoinOrder) ROLE_HOST else ROLE_JOIN
                updates["storyWinner"] = winner
                updates["storyRoundEnded"] = true
                // status'ı FINISHED yapmıyoruz; host bir sonraki level'a geçirebilir.
            }

            tx.set(docRef, updates, SetOptions.merge())
        }.await()
    }

    /**
     * Host bir sonraki STORY level'a geçişi yapar.
     * - round reset
     * - level increment (veya dışarıdan set)
     */
    suspend fun hostStoryNext(roomCode: String, nextLevelNo: Int, seed: Int = 0, gameOver: Boolean) {
        val docRef = db.collection(COLLECTION).document(roomCode)
        val payload = mapOf(
            "status" to if (gameOver) STATUS_FINISHED else STATUS_STARTED,
            "gameType" to GAME_STORY,

            "storyLevel" to nextLevelNo.coerceIn(1, 100),
            "storySeed" to seed,

            "storyFinishCounter" to 0L,
            "storyFinishOrderHost" to 0L,
            "storyFinishOrderJoin" to 0L,
            "storyElapsedMsHost" to -1L,
            "storyElapsedMsJoin" to -1L,
            "storyRoundEnded" to false,
            "storyWinner" to "",

            // QUIZ alanlarına dokunmuyoruz (kompatibilite)
        )
        docRef.set(payload, SetOptions.merge()).await()
    }
}
