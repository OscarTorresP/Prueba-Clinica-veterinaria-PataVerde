package cl.duoc.pataverde.dao;

import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Perro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MascotaJson implements MascotaDAO {

    private static final String ARCHIVO_PERROS = "perros.json";
    private static final String ARCHIVO_GATOS = "gatos.json";

    //Instancia para escribir el Json
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void guardarPerros(List<Perro> perros) {
        try (FileWriter writer = new FileWriter(ARCHIVO_PERROS)) {
            gson.toJson(perros, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo de perros: " + e.getMessage());
        }
    }

    @Override
    public void guardarGatos(List<Gato> gatos) {
        try (FileWriter writer = new FileWriter(ARCHIVO_GATOS)) {
            gson.toJson(gatos, writer);
        } catch (IOException e) {
            // errores de guardado capturados en el MainController.
        }
    }

    @Override
    public List<Perro> cargarPerros() {
        List<Perro> perros = new ArrayList<>();
        try (FileReader reader = new FileReader(ARCHIVO_PERROS)) {
            Type tipoLista = new TypeToken<List<Perro>>() {}.getType();
            List<Perro> listaCargada = gson.fromJson(reader, tipoLista);
            if (listaCargada != null) {
                perros = listaCargada;
            }
        } catch (IOException e) {
            // Si el archivo no existe o es la 1ra ejecucion, retorna una lista vacía sin mostrar un error.
        }
        return perros;
    }

    @Override
    public List<Gato> cargarGatos() {
        List<Gato> gatos = new ArrayList<>();
        try (FileReader reader = new FileReader(ARCHIVO_GATOS)) {
            Type tipoLista = new TypeToken<List<Gato>>() {}.getType();
            List<Gato> listaCargada = gson.fromJson(reader, tipoLista);
            if (listaCargada != null) {
                gatos = listaCargada;
            }
        } catch (IOException e) {
            // Si el archivo no existe o es la 1ra ejecucion, retorna una lista vacía sin mostrar un error.
        }
        return gatos;
    }
}