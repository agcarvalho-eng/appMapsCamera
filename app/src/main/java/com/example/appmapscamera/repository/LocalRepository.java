package com.example.appmapscamera.repository;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.example.appmapscamera.model.Local;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar a persistência de dados relacionada aos objetos {@link Local}.
 * Armazena e recupera arquivos no armazenamento externo público (em formato JSON).
 */
public class LocalRepository {

    private static final String TAG = "LocalRepository";

    /** Caminho base onde os dados dos locais serão armazenados no diretório de documentos públicos. */
    private static final String BASE_DIR = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Locais/";

    /**
     * Retorna o diretório base para armazenamento dos locais.
     * Se o diretório não existir, ele será criado.
     * @return Objeto {@link File} apontando para o diretório base.
     */
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

    /**
     * Salva as informações de um {@link Local} em um arquivo JSON dentro de um subdiretório específico.
     * O nome do diretório é baseado no nome do local.
     * @param context Contexto da aplicação (não usado diretamente, mas mantido para futuras extensões).
     * @param local   Objeto {@link Local} a ser salvo.
     */
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

    /**
     * Recupera todos os locais salvos no diretório base.
     * Os dados são lidos de arquivos "metadata.json" contidos nos subdiretórios.
     * @return Lista de objetos {@link Local} recuperados dos arquivos.
     */
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
}
