package com.example.appmapscamera.repository;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FotoRepository {

    private static final String TAG = "FotoRepository";

    /**
     * Retorna o diretório base privado de fotos para o local informado.
     */
    public static File getBaseDir(Context context, String localName) {
        File baseDir = new File(context.getExternalFilesDir("pictures"), localName);
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            Log.d(TAG, "Diretório de fotos criado: " + created + " em " + baseDir.getAbsolutePath());
        }
        return baseDir;
    }

    /**
     * Retorna a lista de caminhos de fotos salvas para o local informado.
     */
    public static List<String> getTodasFotos(Context context, String localName) {
        File dir = getBaseDir(context, localName);
        List<String> fotos = new ArrayList<>();

        File[] arquivos = dir.listFiles();
        if (arquivos != null) {
            for (File f : arquivos) {
                if (f.getName().endsWith(".jpg")) {
                    fotos.add(f.getAbsolutePath());
                    Log.d(TAG, "Foto carregada: " + f.getAbsolutePath());
                }
            }
        }

        return fotos;
    }
}
