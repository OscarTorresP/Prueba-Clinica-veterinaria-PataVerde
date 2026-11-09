package cl.duoc.pataverde.dao;

import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Perro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MascotaJson implements MascotaDAO {
    // definicion de ruta para guardar los archivos Json
    private static final String RUTA_DIRECTORIO = "Mascotas";
    private static final File DIRECTORIO = new File(RUTA_DIRECTORIO);
    private static final File ARCHIVO_PERROS = new File(DIRECTORIO, "perros.json");
    private static final File ARCHIVO_GATOS = new File(DIRECTORIO, "gatos.json");

    //Instancia para escribir el Json
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // si la carpeta "Mascotas" no existe la crea
    private void asegurarDirectorio() {
        if (!DIRECTORIO.exists()) {
            DIRECTORIO.mkdirs(); // Crea la carpeta automáticamente si no existe
        }
    }

    @Override
    public void guardarPerros(List<Perro> perros) {
        asegurarDirectorio();
        try (FileWriter writer = new FileWriter(ARCHIVO_PERROS)) {
            gson.toJson(perros, writer);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo.", e);
        }
    }

    @Override
    public void guardarGatos(List<Gato> gatos) {
        asegurarDirectorio();
        try (FileWriter writer = new FileWriter(ARCHIVO_GATOS)) {
            gson.toJson(gatos, writer);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo.", e);
        }
    }

    @Override
    public List<Perro> cargarPerros() {
        asegurarDirectorio();
        List<Perro> perros = new ArrayList<>();
        // Si la carpeta o el archivo no existen, retorna lista vacía
        if (!ARCHIVO_PERROS.exists()) {
            return perros;
        }
        try (FileReader reader = new FileReader(ARCHIVO_PERROS)) {
            Type tipoLista = new TypeToken<List<Perro>>() {}.getType();
            List<Perro> listaCargada = gson.fromJson(reader, tipoLista);
            if (listaCargada != null) {
                perros = listaCargada;
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron leer los perros.", e);
        }
        return perros;
    }

    @Override
    public List<Gato> cargarGatos() {
        asegurarDirectorio();
        List<Gato> gatos = new ArrayList<>();
        if (!ARCHIVO_GATOS.exists()) {
            return gatos;
        }
        try (FileReader reader = new FileReader(ARCHIVO_GATOS)) {
            Type tipoLista = new TypeToken<List<Gato>>() {}.getType();
            List<Gato> listaCargada = gson.fromJson(reader, tipoLista);
            if (listaCargada != null) {
                gatos = listaCargada;
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron leer los gatos.", e);
        }
        return gatos;
    }
}