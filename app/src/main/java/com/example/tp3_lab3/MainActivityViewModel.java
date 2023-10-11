package com.example.tp3_lab3;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.tp3_lab3.models.Usuario;
import com.example.tp3_lab3.request.ApiClient;

public class MainActivityViewModel extends AndroidViewModel {

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

    }


    public void loguearse(String correo, String clave) {
        if (correo != null && !correo.isEmpty() && clave != null && !clave.isEmpty()) {
            Usuario usuario = ApiClient.login(getApplication(), correo, clave);
            Bundle bundle = new Bundle();
            Intent intent = new Intent(getApplication(), RegistroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (usuario != null) {
                bundle.putSerializable("usuario", usuario);
                intent.putExtra("usuario", bundle);
                getApplication().startActivity(intent);

            } else {
                Toast.makeText(getApplication(), "User not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplication(), "Complete the fields", Toast.LENGTH_SHORT).show();
        }


    }


    public void registrarse() {
        Intent intent = new Intent(getApplication(), RegistroActivity.class);
        Bundle bundle = new Bundle();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bundle.putSerializable("usuario", null);
        intent.putExtra("usuario", bundle);
        getApplication().startActivity(intent);
    }
}