package com.pervasive.smartac

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pervasive.smartac.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var ref : DatabaseReference
    val loading = LoadingDialog(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSuhuDanKelembapanRuangan()

        changeImageAc(false)

    }

    private fun changeImageAc(on:Boolean){
        binding.apply {
            if (on==true){
                Picasso.get().load(R.drawable.ac_on).into(imgAc)
            }else{
                Picasso.get().load(R.drawable.ac_off).into(imgAc)
            }
        }
    }
    private fun getSuhuDanKelembapanRuangan(){
        loading.startLoading()
        ref = FirebaseDatabase.getInstance().getReference("Data")
        ref.child("suhu_ruangan").get().addOnSuccessListener {
            GlobalData.suhu_ruangan = it.value.toString().toInt()
            binding.suhuRuangan.text  = it.value.toString()+resources.getString(R.string.derajat_celcius)
            ref.child("kelembapan_air").get().addOnSuccessListener {
                GlobalData.kelembapan_air = it.value.toString().toInt()
                binding.kelembapanUdara.text  = it.value.toString()+resources.getString(R.string.satuan_kelembapan)
                loading.isDismiss()
            }.addOnFailureListener {
                loading.isDismiss()
            }
        }.addOnFailureListener {
            loading.isDismiss()
        }
    }
}