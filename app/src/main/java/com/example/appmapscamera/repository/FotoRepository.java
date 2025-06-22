package com.example.appmapscamera.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;

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

    /**
     * Realiza a captura e salvamento da foto no diretório apropriado.
     * @param context Contexto da aplicação
     * @param localName Nome do local (diretório)
     * @param imageCapture Instância do ImageCapture
     * @param executor Executor para a thread principal
     * @param callback Callback chamado após salvamento ou erro
     */
    public static void salvarFoto(Context context, String localName,
                                  ImageCapture imageCapture,
                                  Executor executor,
                                  ImageCapture.OnImageSavedCallback callback) {

        File dir = getBaseDir(context, localName);
        String nomeArquivo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".jpg";
        File fotoArquivo = new File(dir, nomeArquivo);

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(fotoArquivo).build();

        imageCapture.takePicture(outputOptions, executor, callback);
    }
}


