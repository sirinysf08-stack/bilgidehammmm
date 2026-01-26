package com.example.bilgideham

/**
 * AR Laboratuvarı - 3D Model Verileri
 * 20 hazır model: Fen (12), Matematik (4), Sosyal (4)
 */

enum class ARModelCategory(val displayName: String, val icon: String) {
    FEN("Fen Bilimleri", "🔬"),
    MATEMATIK("Matematik", "📐"),
    SOSYAL("Sosyal Bilgiler", "🏛️")
}

data class ARModel(
    val id: String,
    val name: String,
    val emoji: String,
    val category: ARModelCategory,
    val description: String,
    val color: Long // ARGB format
)

object ARModels {
    val allModels = listOf(
        // ========== FEN BİLİMLERİ (12 Model) ==========
        ARModel("volcano", "Yanardağ", "🌋", ARModelCategory.FEN, 
            "Patlayan volkan, lav akışı ve duman animasyonu", 0xFFE64A19),
        ARModel("heart", "İnsan Kalbi", "❤️", ARModelCategory.FEN, 
            "Atan, yarı şeffaf kalp modeli", 0xFFE91E63),
        ARModel("solar_system", "Güneş Sistemi", "🪐", ARModelCategory.FEN, 
            "Güneş ve dönen 8 gezegen", 0xFF3F51B5),
        ARModel("dna", "DNA Molekülü", "🧬", ARModelCategory.FEN, 
            "Çift sarmal DNA yapısı", 0xFF9C27B0),
        ARModel("animal_cell", "Hayvan Hücresi", "🦠", ARModelCategory.FEN, 
            "Çekirdek, mitokondri ve organeller", 0xFF4CAF50),
        ARModel("plant_cell", "Bitki Hücresi", "🌱", ARModelCategory.FEN, 
            "Hücre duvarı, kloroplast ile", 0xFF8BC34A),
        ARModel("water_cycle", "Su Döngüsü", "💧", ARModelCategory.FEN, 
            "Buharlaşma, yoğuşma, yağış", 0xFF03A9F4),
        ARModel("earth_layers", "Dünya Katmanları", "🌍", ARModelCategory.FEN, 
            "Kabuk, manto, dış ve iç çekirdek", 0xFF795548),
        ARModel("atom", "Atom Modeli", "⚡", ARModelCategory.FEN, 
            "Proton, nötron ve elektron yörüngeleri", 0xFFFF9800),
        ARModel("lungs", "Akciğerler", "🫁", ARModelCategory.FEN, 
            "Solunum sistemi, bronşlar", 0xFFFF5722),
        ARModel("brain", "İnsan Beyni", "🧠", ARModelCategory.FEN, 
            "Beyin lobları ve fonksiyonları", 0xFFEC407A),
        ARModel("eye", "Göz Anatomisi", "👁️", ARModelCategory.FEN, 
            "Kornea, lens, retina yapısı", 0xFF00BCD4),
        
        // ========== MATEMATİK (4 Model) ==========
        ARModel("geometric_shapes", "Geometrik Cisimler", "🔺", ARModelCategory.MATEMATIK, 
            "Küp, silindir, koni, küre", 0xFF2196F3),
        ARModel("pyramid", "Piramit", "📐", ARModelCategory.MATEMATIK, 
            "Kare tabanlı piramit", 0xFFFFC107),
        ARModel("prisms", "Prizmalar", "🎲", ARModelCategory.MATEMATIK, 
            "Üçgen ve dikdörtgen prizma", 0xFF009688),
        ARModel("cross_sections", "Kesitler", "🔵", ARModelCategory.MATEMATIK, 
            "Koni ve silindir kesitleri", 0xFF673AB7),
        
        // ========== SOSYAL BİLGİLER (4 Model) ==========
        ARModel("ancient_temple", "Antik Tapınak", "🏛️", ARModelCategory.SOSYAL, 
            "Yunan/Roma mimarisi", 0xFF607D8B),
        ARModel("gobeklitepe", "Göbeklitepe", "🗿", ARModelCategory.SOSYAL, 
            "12.000 yıllık T dikilitaşları", 0xFF8D6E63),
        ARModel("selimiye", "Selimiye Camii", "🕌", ARModelCategory.SOSYAL, 
            "Mimar Sinan'ın şaheseri", 0xFF00897B),
        ARModel("castle", "Ortaçağ Kalesi", "🏰", ARModelCategory.SOSYAL, 
            "Surlar, kuleler ve hendek", 0xFF546E7A)
    )
    
    fun getByCategory(category: ARModelCategory): List<ARModel> = 
        allModels.filter { it.category == category }
    
    fun getById(id: String): ARModel? = 
        allModels.find { it.id == id }
}
