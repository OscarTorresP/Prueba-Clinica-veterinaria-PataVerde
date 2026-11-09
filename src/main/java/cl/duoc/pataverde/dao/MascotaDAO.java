package cl.duoc.pataverde.dao;

import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Perro;
import java.util.List;

public interface MascotaDAO {

    // Métodos para guardar las mascotas en archivos Json separados
    void guardarPerros(List<Perro> perros);
    void guardarGatos(List<Gato> gatos);

    // Métodos para cargar los datos desde los archivos Json al iniciar la app
    List<Perro> cargarPerros();
    List<Gato> cargarGatos();
}