package com.e.aplikasikeamanan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView textViewId;
    TextView textViewName;
    TextView textViewKeluar;
    TextView textViewMasuk;
    TextView textViewAlasan;
    Button btnScan;
    Button btnIzin;
    Spinner spinner;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static final String ID = "id_student";

    String id,nama,masuk,keluar,izin,alasan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!textViewId.equals(null)) {
            id = getIntent().getStringExtra(ID);
            textViewId.setText(id);
        }


        textViewId = findViewById(R.id.txt_id);
        textViewName = findViewById(R.id.student_name);
        textViewKeluar = findViewById(R.id.tanggal_keluar);
        textViewMasuk = findViewById(R.id.tanggal_masuk);
        textViewAlasan = findViewById(R.id.alasan);
        btnIzin = findViewById(R.id.btn_izin_scan);
        spinner = findViewById(R.id.string_izin);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter
                .createFromResource(this, R.array.izin_array, R.layout.activity_main);

        arrayAdapter.setDropDownViewResource(R.layout.activity_main);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                izin = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(MainActivity.this, "harap isi keterangan izin", Toast.LENGTH_SHORT).show();
            }
        });

        setUp();

        btnIzin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id.trim().isEmpty() || nama.trim().isEmpty() || keluar.isEmpty() || masuk.isEmpty() || alasan.isEmpty() || izin.isEmpty()){
                    Toast.makeText(MainActivity.this, "harap lengkapi data yang masih kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String,Object> map = new HashMap<>();
                map.put("izin",izin);


                db.collection("Student").document(id)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "berhasil", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    public void setUp(){
        btnScan = findViewById(R.id.btn_serial_number);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ScanActivity.class);
                startActivity(intent);
                finish();
            }
        });
        db.collection("Perizinan").whereEqualTo("id", id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot snapshot : task.getResult()){
                                textViewName.setText(snapshot.get("nama").toString());
                                textViewKeluar.setText(snapshot.get("WaktuKeluar").toString());
                                textViewMasuk.setText(snapshot.get("WaktuMasuk").toString());
                                textViewAlasan.setText(snapshot.get("Alasan").toString());
                            }
                            nama = textViewName.toString();
                            masuk = textViewMasuk.toString();
                            keluar = textViewKeluar.toString();
                            alasan = textViewAlasan.toString();
                        }
                    }
                });

    }

}