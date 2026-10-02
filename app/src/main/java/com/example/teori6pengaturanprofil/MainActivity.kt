package com.example.teori6pengaturanprofil

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doOnTextChanged

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NAMA_PREFS = "profil_prefs"

        private const val KEY_NAMA         = "nama"
        private const val KEY_EMAIL        = "email"
        private const val KEY_BIO          = "bio"
        private const val KEY_AVATAR       = "avatar"
        private const val KEY_GELAP        = "mode_gelap"
        private const val KEY_KOTA         = "kota"
        private const val KEY_PANGGILAN    = "panggilan"
        private const val KEY_JUMLAH_BUKA  = "jumlah_buka"

        private val DAFTAR_AVATAR = listOf(
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
        )
    }

    private val prefs by lazy { getSharedPreferences(NAMA_PREFS, MODE_PRIVATE) }
    private var indeksAvatar = 0

    private lateinit var tvAppBar: TextView
    private lateinit var tvJumlahBuka: TextView
    private lateinit var layoutUtama: ScrollView
    private lateinit var ivAvatar: ImageView
    private lateinit var tvNamaKartu: TextView
    private lateinit var tvEmailKartu: TextView
    private lateinit var tvLabelAvatar: TextView
    private lateinit var tvLabelGelap: TextView
    private lateinit var ivAvatarOpsi1: ImageView
    private lateinit var ivAvatarOpsi2: ImageView
    private lateinit var ivAvatarOpsi3: ImageView
    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etBio: EditText
    private lateinit var etKota: EditText
    private lateinit var etPanggilan: EditText
    private lateinit var tvPanggilanHasil: TextView
    private lateinit var swGelap: SwitchCompat
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvAppBar         = findViewById(R.id.tvAppBar)
        tvJumlahBuka     = findViewById(R.id.tvJumlahBuka)
        layoutUtama      = findViewById(R.id.layoutUtama)
        ivAvatar         = findViewById(R.id.ivAvatar)
        tvNamaKartu      = findViewById(R.id.tvNamaKartu)
        tvEmailKartu     = findViewById(R.id.tvEmailKartu)
        tvLabelAvatar    = findViewById(R.id.tvLabelAvatar)
        tvLabelGelap     = findViewById(R.id.tvLabelGelap)
        ivAvatarOpsi1    = findViewById(R.id.ivAvatarOpsi1)
        ivAvatarOpsi2    = findViewById(R.id.ivAvatarOpsi2)
        ivAvatarOpsi3    = findViewById(R.id.ivAvatarOpsi3)
        etNama           = findViewById(R.id.etNama)
        etEmail          = findViewById(R.id.etEmail)
        etBio            = findViewById(R.id.etBio)
        etKota           = findViewById(R.id.etKota)
        etPanggilan      = findViewById(R.id.etPanggilan)
        tvPanggilanHasil = findViewById(R.id.tvPanggilanHasil)
        swGelap          = findViewById(R.id.swGelap)
        btnReset         = findViewById(R.id.btnReset)

        swGelap.setOnCheckedChangeListener { _, isChecked ->
            terapkanModeGelap(isChecked)
        }

        ivAvatarOpsi1.setOnClickListener { pilihAvatar(0) }
        ivAvatarOpsi2.setOnClickListener { pilihAvatar(1) }
        ivAvatarOpsi3.setOnClickListener { pilihAvatar(2) }

        findViewById<Button>(R.id.btnSimpan).setOnClickListener { simpanProfil() }
        btnReset.setOnClickListener { hapusSemuaData() }
        findViewById<Button>(R.id.btnSimpanPanggilan).setOnClickListener { simpanPanggilan() }

        etNama.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etEmail.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }

        muatProfil()
        muatPanggilan()
        hitungPembukaanAplikasi()
    }

    //  menyimpan
    private fun simpanProfil() {
        prefs.edit()
            .putString(KEY_NAMA,   etNama.text.toString().trim())
            .putString(KEY_EMAIL,  etEmail.text.toString().trim())
            .putString(KEY_BIO,    etBio.text.toString().trim())
            .putString(KEY_KOTA,   etKota.text.toString().trim())
            .putInt(KEY_AVATAR,    indeksAvatar)
            .putBoolean(KEY_GELAP, swGelap.isChecked)
            .apply()

        Toast.makeText(this, getString(R.string.pesan_tersimpan), Toast.LENGTH_SHORT).show()
    }

    //  membaca
    private fun muatProfil() {
        val nama  = prefs.getString(KEY_NAMA, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val bio   = prefs.getString(KEY_BIO, "") ?: ""
        val kota  = prefs.getString(KEY_KOTA, "") ?: ""
        indeksAvatar = prefs.getInt(KEY_AVATAR, 0)
        val gelap = prefs.getBoolean(KEY_GELAP, false)

        etNama.setText(nama)
        etEmail.setText(email)
        etBio.setText(bio)
        etKota.setText(kota)
        ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
        perbaruiRingAvatar()
        swGelap.isChecked = gelap

        terapkanModeGelap(gelap)
        perbaruiKartu()
    }

    //  menghapus
    private fun hapusSemuaData() {
        prefs.edit().clear().apply()
        Toast.makeText(this, getString(R.string.pesan_reset), Toast.LENGTH_SHORT).show()
        muatProfil()
        muatPanggilan()
    }

    //  avatar: ketuk langsung
    private fun pilihAvatar(index: Int) {
        indeksAvatar = index
        ivAvatar.setImageResource(DAFTAR_AVATAR[index])
        perbaruiRingAvatar()
    }

    private fun perbaruiRingAvatar() {
        val opsi = listOf(ivAvatarOpsi1, ivAvatarOpsi2, ivAvatarOpsi3)
        opsi.forEachIndexed { i, tampilan ->
            tampilan.setBackgroundResource(
                if (i == indeksAvatar) R.drawable.bg_avatar_ring_on else R.drawable.bg_avatar_ring_off
            )
        }
    }

    private fun perbaruiKartu() {
        val nama  = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()

        tvNamaKartu.text  = nama.ifEmpty { getString(R.string.nama_default) }
        tvEmailKartu.text = email.ifEmpty { getString(R.string.email_default) }
    }

    private fun terapkanModeGelap(aktif: Boolean) {
        val warnaAppBar     = if (aktif) "#14532d" else "#2db86f"
        val warnaTeksAppBar = if (aktif) "#d1fae5" else "#08301c"
        val warnaBody       = if (aktif) "#121212" else "#f8fafc"
        val warnaNama       = if (aktif) "#F5F5F5" else "#1B1B1B"
        val warnaEmail      = if (aktif) "#B0B0B0" else "#6B6B6B"
        val warnaLabel      = if (aktif) "#9ca3af" else "#64748b"

        tvAppBar.setBackgroundColor(Color.parseColor(warnaAppBar))
        tvAppBar.setTextColor(Color.parseColor(warnaTeksAppBar))

        layoutUtama.setBackgroundColor(Color.parseColor(warnaBody))

        tvNamaKartu.setTextColor(Color.parseColor(warnaNama))
        tvEmailKartu.setTextColor(Color.parseColor(warnaEmail))
        tvLabelAvatar.setTextColor(Color.parseColor(warnaLabel))
        tvLabelGelap.setTextColor(Color.parseColor(warnaNama))

        val bgInput = if (aktif) R.drawable.bg_input_gelap else R.drawable.bg_input_terang
        val warnaTeksInput = if (aktif) "#E5E5E5" else "#334155"
        listOf(etNama, etEmail, etBio, etKota, etPanggilan).forEach {
            it.setBackgroundResource(bgInput)
            it.setTextColor(Color.parseColor(warnaTeksInput))
        }

        val bgGhost = if (aktif) R.drawable.bg_tombol_ghost_gelap else R.drawable.bg_tombol_ghost
        btnReset.setBackgroundResource(bgGhost)
        btnReset.setTextColor(Color.parseColor(warnaLabel))

        tvPanggilanHasil.setTextColor(Color.parseColor(warnaEmail))
    }

    //  Latihan 1: nama panggilan
    private fun simpanPanggilan() {
        prefs.edit()
            .putString(KEY_PANGGILAN, etPanggilan.text.toString().trim())
            .apply()
        muatPanggilan()
        Toast.makeText(this, "Panggilan tersimpan", Toast.LENGTH_SHORT).show()
    }

    private fun muatPanggilan() {
        val panggilan = prefs.getString(KEY_PANGGILAN, "") ?: ""
        tvPanggilanHasil.text = panggilan.ifEmpty { getString(R.string.teks_panggilan_kosong) }
    }

    //  Latihan 3: penghitung pembukaan aplikasi
    private fun hitungPembukaanAplikasi() {
        val jumlah = prefs.getInt(KEY_JUMLAH_BUKA, 0) + 1
        prefs.edit().putInt(KEY_JUMLAH_BUKA, jumlah).apply()
        tvJumlahBuka.text = getString(R.string.jumlah_buka_format, jumlah)
    }
}