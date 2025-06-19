package com.example.appmapscamera.repository;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.appmapscamera.model.Local;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LocalRepository {

    private static final String TAG = "LocalRepository";
    private static final String BASE_DIR = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Locais/";

    public static File getBaseDir() {
        File dir = new File(BASE_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            Log.d(TAG, "Diretório base criado: " + created + " em " + dir.getAbsolutePath());
        } else {
            Log.d(TAG, "Diretório base já existe: " + dir.getAbsolutePath());
        }
        return dir;
    }

    public static void salvarLocal(Context context, Local local) {
        File dir = new File(BASE_DIR + local.getNome());
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            Log.d(TAG, "Diretório para local '" + local.getNome() + "' criado: " + created + " em " + dir.getAbsolutePath());
        } else {
            Log.d(TAG, "Diretório para local '" + local.getNome() + "' já existe: " + dir.getAbsolutePath());
        }

        File metadata = new File(dir, "metadata.json");
        try (FileWriter writer = new FileWriter(metadata)) {
            Gson gson = new Gson();
            writer.write(gson.toJson(local));
            Log.d(TAG, "Local salvo em: " + metadata.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Erro ao salvar local: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Local> getTodosLocais() {
        List<Local> locals = new ArrayList<>();
        File baseDir = getBaseDir();

        Log.d(TAG, "Buscando locais em: " + baseDir.getAbsolutePath());

        File[] directories = baseDir.listFiles(File::isDirectory);
        if (directories != null) {
            Gson gson = new Gson();
            for (File dir : directories) {
                File metadata = new File(dir, "metadata.json");
                if (metadata.exists()) {
                    try (FileReader reader = new FileReader(metadata)) {
                        Local local = gson.fromJson(reader, Local.class);
                        locals.add(local);
                        Log.d(TAG, "Local carregado: " + local.getNome() + " de " + metadata.getAbsolutePath());
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler metadata: " + metadata.getAbsolutePath(), e);
                    }
                }
            }
        }

        return locals;
    }

    public static List<String> getFotosPorLocal(String localName) {
        File dir = new File(BASE_DIR + localName + "/picture");
        List<String> fotos = new ArrayList<>();

        Log.d(TAG, "Buscando fotos no diretório: " + dir.getAbsolutePath());

        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".jpg")) {
                        fotos.add(f.getAbsolutePath());
                        Log.d(TAG, "Foto encontrada: " + f.getAbsolutePath());
                    }
                }
            }
        }

        return fotos;
    }

    public static Uri salvarFoto(Context context, String localName, File photoFile) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Documents/Locais/" + localName + "/picture");

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                Files.copy(photoFile.toPath(), outputStream);
                Log.d(TAG, "Foto salva em: " + uri.toString());
            } catch (IOException e) {
                Log.e(TAG, "Erro ao salvar foto: " + e.getMessage());
            }
        }
        return uri;
    }
}