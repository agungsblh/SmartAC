package com.pervasive.smartac

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        onAction()
        streamingData()
        changeImageAc(false)
    }
    private fun onAction(){
        binding.apply {
            frameSuhuMinimal.setOnClickListener {
                showDialogSuhuMin()
            }
            frameSuhuMaksimal.setOnClickListener {
                showDialogSuhuMax()
            }
        }
    }
    private fun showDialogSuhuMin() {
        val view = View.inflate(this, R.layout.dialog_ubah_batas_suhu, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        val judul = view.findViewById<TextView>(R.id.judul)
        val suhu = view.findViewById<TextView>(R.id.suhu)
        val btn_tambah = view.findViewById<ImageButton>(R.id.tambah_suhu)
        val btn_kurang = view.findViewById<ImageButton>(R.id.kurangi_suhu)
        val btn_ok = view.findViewById<Button>(R.id.btn_ok)

        var suhuInt = GlobalData.suhu_minimal
        var suhuMax = GlobalData.suhu_maksimal

        judul.text = "Batas suhu minimal"
        suhu.text =
            GlobalData.suhu_minimal.toString() + resources.getString(R.string.derajat_celcius)

        dialog.show()
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btn_tambah.setOnClickListener {
            if (suhuInt < 45) {
                if (suhuInt < suhuMax && (suhuMax - suhuInt) > 2) {
                    suhuInt += 1
                    suhu.text = suhuInt.toString() + resources.getString(R.string.derajat_celcius)
                }
            }
            btn_kurang.setOnClickListener {
                if (suhuInt > 16) {
                    suhuInt = suhuInt - 1
                    suhu.text = suhuInt.toString() + resources.getString(R.string.derajat_celcius)
                }
            }
            btn_ok.setOnClickListener {
                loading.startLoading()
                ref = FirebaseDatabase.getInstance().getReference("Data")
                val update = mapOf(
                    "suhu_minimal" to suhuInt
                )
                ref.updateChildren(update).addOnCompleteListener {
                    loading.isDismiss()
                    dialog.dismiss()
                }
            }
        }
    }
    private fun showDialogSuhuMax(){
        val view = View.inflate(this, R.layout.dialog_ubah_batas_suhu,null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        val judul = view.findViewById<TextView>(R.id.judul)
        val suhu = view.findViewById<TextView>(R.id.suhu)
        val btn_tambah = view.findViewById<ImageButton>(R.id.tambah_suhu)
        val btn_kurang = view.findViewById<ImageButton>(R.id.kurangi_suhu)
        val btn_ok = view.findViewById<Button>(R.id.btn_ok)

        var suhuInt = GlobalData.suhu_maksimal
        var suhuMin = GlobalData.suhu_minimal

        judul.text = "Batas suhu maksimal"
        suhu.text = GlobalData.suhu_maksimal.toString()+resources.getString(R.string.derajat_celcius)

        dialog.show()
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btn_tambah.setOnClickListener {
            if(suhuInt<45){
                suhuInt=suhuInt+1
                suhu.text = suhuInt.toString()+resources.getString(R.string.derajat_celcius)
            }
        }
        btn_kurang.setOnClickListener {
            if (suhuInt>16){
                if (suhuInt>suhuMin && (suhuInt-suhuMin)>2){
                    suhuInt=suhuInt-1
                    suhu.text = suhuInt.toString()+resources.getString(R.string.derajat_celcius)
                }
            }
        }
        btn_ok.setOnClickListener {
            loading.startLoading()
            ref = FirebaseDatabase.getInstance().getReference("Data")
            val update = mapOf(
                "suhu_maksimal" to suhuInt
            )
            ref.updateChildren(update).addOnCompleteListener {
                loading.isDismiss()
                dialog.dismiss()
            }
        }
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
    private fun streamingData(){
        loading.startLoading()
        ref = FirebaseDatabase.getInstance().getReference("Data")
        ref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        GlobalData.suhu_ac = snapshot.child("suhu_ac").getValue().toString().toInt()
                        binding.suhuAc.text  =GlobalData.suhu_ac.toString()+resources.getString(R.string.derajat_celcius)

                        GlobalData.suhu_minimal = snapshot.child("suhu_minimal").getValue().toString().toInt()
                        binding.suhuMinimal.text  =GlobalData.suhu_minimal.toString()+resources.getString(R.string.derajat_celcius)

                        GlobalData.suhu_maksimal = snapshot.child("suhu_maksimal").getValue().toString().toInt()
                        binding.suhuMaksimal.text  =GlobalData.suhu_maksimal.toString()+resources.getString(R.string.derajat_celcius)

                        GlobalData.suhu_ruangan = snapshot.child("suhu_ruangan").getValue().toString().toInt()
                        binding.suhuRuangan.text  =GlobalData.suhu_ruangan.toString()+resources.getString(R.string.derajat_celcius)

                        GlobalData.kelembapan_air = snapshot.child("kelembapan_air").getValue().toString().toInt()
                        binding.kelembapanUdara.text  =GlobalData.kelembapan_air.toString()+resources.getString(R.string.satuan_kelembapan)

                        if(snapshot.child("suhu_ruangan").getValue().toString().toInt()>=snapshot.child("suhu_minimal").getValue().toString().toInt()){
                            changeImageAc(true)
                            binding.suhuAc.text = "ON"
                            binding.suhuAc.setTextColor(resources.getColor(R.color.hijau))
                        }else{
                            changeImageAc(false)
                            binding.suhuAc.text = "OFF"
                            binding.suhuAc.setTextColor(resources.getColor(R.color.black))
                        }
                        loading.isDismiss()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                    Toast.makeText(this@MainActivity,error.toException().toString(),Toast.LENGTH_SHORT).show()
                }
            })
    }
}