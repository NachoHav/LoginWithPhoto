package com.example.tp3_lab3;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tp3_lab3.models.Usuario;
import com.example.tp3_lab3.request.ApiClient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegistroActivityViewModel extends AndroidViewModel {
    private Context context;
    private final MutableLiveData<Usuario> mUsuario = new MutableLiveData<>(new Usuario("","","","","",""));
    private MutableLiveData<String> mTitulo;
    private MutableLiveData<String> mBtnTitulo;
    private MutableLiveData<Boolean> mEnable;
    private MutableLiveData<Bitmap> mFoto;

    private final MutableLiveData<Void> iCaptura;
    private String nombreArchivo;

    public LiveData<Void> getIniciarCaptura() {
        return iCaptura;
    }

    public void solicitarCapturaFoto() {
        iCaptura.setValue(null);
    }

    public RegistroActivityViewModel(@NonNull Application application) {
        super(application);
        mTitulo = new MutableLiveData<>();
        mBtnTitulo = new MutableLiveData<>();
        mEnable = new MutableLiveData<>(false);
        mFoto = new MutableLiveData<>();
        iCaptura = new MutableLiveData<>();
        context = getApplication();
    }

    public LiveData<Bitmap> getFoto() {
        return mFoto;
    }

    public LiveData<String> getmTitulo() {
        return mTitulo;
    }

    public LiveData<String> getmBtnTitulo() {
        return mBtnTitulo;
    }

    public LiveData<Boolean> getmEnable() {
        return mEnable;
    }

    public LiveData<Usuario> getmUsuario() {
        return mUsuario;
    }

    public void registrarse(Usuario u) {
        Usuario usuario = u;
        Log.d("salida",usuario.toString());
        if (usuario.getAvatar() == "") {
            Bitmap bitmap = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.ffa09aec412db3f54deadf1b3781de2a);
            File archivo = new File(getApplication().getApplicationContext().getFilesDir(), nombreArchivo);
            Log.d("salida", "RegisteredAvatar: " + archivo.getAbsolutePath());
            if (archivo.exists()) {
                archivo.delete();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            byte[] b = baos.toByteArray();
            usuario.setAvatar(archivo.getAbsolutePath());
            try {
                FileOutputStream fo = new FileOutputStream(archivo);
                BufferedOutputStream bo = new BufferedOutputStream(fo);
                bo.write(b);
                bo.flush();
                bo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ApiClient.guardar(getApplication(), usuario);
        Intent intent = new Intent(getApplication(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }

    public void existente(Bundle bundle) {
        Usuario usuario = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            usuario = bundle.getSerializable("usuario", Usuario.class);
        }

        if (usuario == null) {
            mTitulo.setValue("Registrar");
            mBtnTitulo.setValue("Crear");
            mUsuario.getValue().setNombre("");
            mUsuario.getValue().setDni("");
            mUsuario.getValue().setApellido("");
            mUsuario.getValue().setEmail("");
            mUsuario.getValue().setPassword("");
            mFoto.setValue(BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.ffa09aec412db3f54deadf1b3781de2a));
        } else {
            mTitulo.setValue("Perfil");
            mBtnTitulo.setValue("Editar");
            mUsuario.getValue().setNombre(usuario.getNombre());
            mUsuario.getValue().setDni(usuario.getDni());
            mUsuario.getValue().setApellido(usuario.getApellido());
            mUsuario.getValue().setEmail(usuario.getEmail());
            mUsuario.getValue().setPassword(usuario.getPassword());
            mUsuario.getValue().setAvatar(usuario.getAvatar());
            Glide.with(getApplication())
                    .asBitmap()
                    .load(usuario.getAvatar())
                    .apply(new RequestOptions()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mFoto.setValue(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }

    public void cargarDni(String s) {
        nombreArchivo = "avatar_" + s;
        boolean isDniNotEmpty = s != null && !s.isEmpty();
        mEnable.setValue(isDniNotEmpty);
    }

    public void tomarFoto(View v) {
        solicitarCapturaFoto();
    }

    public void respuetaDeCamara(int requestCode, int resultCode, @Nullable Intent data, int REQUEST_IMAGE_CAPTURE) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            mFoto.setValue(imageBitmap);

            byte[] b = baos.toByteArray();

            mUsuario.getValue().setAvatar(getApplication().getApplicationContext().getFilesDir().getPath() + "/" + nombreArchivo);
            Log.d("salida", mUsuario.getValue().getAvatar());

            File archivo = new File(getApplication().getApplicationContext().getFilesDir(), nombreArchivo);
            if (archivo.exists()) {
                archivo.delete();
            }

            try {
                FileOutputStream fo = new FileOutputStream(archivo);
                BufferedOutputStream bo = new BufferedOutputStream(fo);
                bo.write(b);
                bo.flush();
                bo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}