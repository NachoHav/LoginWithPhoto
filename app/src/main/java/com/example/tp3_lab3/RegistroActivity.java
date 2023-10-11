package com.example.tp3_lab3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.tp3_lab3.databinding.ActivityRegistroBinding;
import com.example.tp3_lab3.models.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private RegistroActivityViewModel vm;
    private ActivityRegistroBinding binding;
    private String dni = null;
    private static int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        vm = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegistroActivityViewModel.class);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("usuario");
        vm.existente(bundle);

        vm.getmTitulo().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.tvTitulo.setText(s);
            }
        });

        vm.getmBtnTitulo().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.btnRegGua.setText(s);
            }
        });
        vm.getmUsuario().observe(this,usuario -> {
            binding.etDni.setText(usuario.getDni());
            binding.etNombre.setText(usuario.getNombre());
            binding.etApellido.setText(usuario.getApellido());
            binding.etCorreo.setText(usuario.getEmail());
            binding.etClave.setText(usuario.getPassword());
        });


        binding.etDni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dni = s.toString();
                vm.cargarDni(dni);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        vm.getmEnable().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isButtonEnabled) {
                binding.btnFoto.setEnabled(isButtonEnabled);
            }
        });

        binding.btnRegGua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("salida","URL: "+vm.getmUsuario().getValue().getAvatar());
                vm.registrarse(new Usuario(binding.etDni.getText().toString(), binding.etNombre.getText().toString()
                        , binding.etApellido.getText().toString(), binding.etCorreo.getText().toString()
                        , binding.etClave.getText().toString(),vm.getmUsuario().getValue().getAvatar()));

            }
        });

        vm.getFoto().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                binding.imgAvatar.setImageBitmap(bitmap);
            }
        });
        binding.btnFoto.setOnClickListener(v -> {
            vm.tomarFoto(v);
        });

        vm.getIniciarCaptura().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        vm.respuetaDeCamara(requestCode, resultCode, data, REQUEST_IMAGE_CAPTURE);
    }


}