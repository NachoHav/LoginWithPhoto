package com.example.tp3_lab3.request;

import android.content.Context;
import android.widget.Toast;

import com.example.tp3_lab3.models.Usuario;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ApiClient {

    private static File carpeta;
    private static File archivo;

    private static File conectar(Context context) {
        carpeta = context.getFilesDir();
        archivo = new File(carpeta, "usuarios.dat");

        return archivo;
    }

    public static void guardar(Context context, Usuario u) {
        archivo = conectar(context);
        Usuario usuario = u;
        try {
            FileOutputStream fos = new FileOutputStream(archivo);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(usuario);
            oos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Error al guardar", Toast.LENGTH_LONG).show();
        } catch (IOException io) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
        }

    }

    public static Usuario leer(Context context) {
        archivo = conectar(context);

        try {
            FileInputStream fis = new FileInputStream(archivo);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Usuario usuario;
            usuario = (Usuario) ois.readObject();

            fis.close();
            return usuario;

        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException io) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }


    }

    public static Usuario login(Context context, String correo, String clave) {
        archivo = conectar(context);

        Usuario usuario = leer(context);
        if (usuario != null) {
            if (correo.equals(usuario.getEmail()) && clave.equals(usuario.getPassword())) {
                return usuario;
            }
        }
        return null;
    }

}
