package cl.duoc.pataverde.controller;

import cl.duoc.pataverde.dao.MascotaDAO;
import cl.duoc.pataverde.dao.MascotaJson;
import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Mascota;
import cl.duoc.pataverde.model.Perro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    // Contenedor
    @FXML private AnchorPane panelBase;
    // Declarar e instanciar la persistencia
    private MascotaDAO mascotaDAO = new MascotaJson();
    // Boton Cerrar
    @FXML private void cerrarVentana() {
        Platform.exit();
        System.exit(0);
    }
    // Entrada datos Mascota
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEdad;
    @FXML private TextField txtPeso;
    // Marco datos perro
    @FXML private TitledPane datosPerro;
    @FXML private TextField txtRaza;
    @FXML private CheckBox chkVacunaPerro;
    @FXML private CheckBox chkAdopcion;
    // Marco datos gato
    @FXML private TitledPane datosGato;
    @FXML private CheckBox chkVacunaGato;
    // Tabla de datos Mascotas
    @FXML private TableView<Mascota> tblMascotas;
    @FXML private TableColumn<Mascota, String> colTipo;
    @FXML private TableColumn<Mascota, String> colNombre;
    @FXML private TableColumn<Mascota, Integer> colEdad;
    @FXML private TableColumn<Mascota, Double> colPeso;
    @FXML private TableColumn<Mascota, String> colCostoCon;
    // Notificacion estado de registro
    @FXML private Label lblEstadoRegistro;
    // Lista automatica para la tabla
    private ObservableList<Mascota> listaMascotas = FXCollections.observableArrayList();
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private void initialize() {
        panelBase.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        panelBase.setOnMouseDragged(event -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) panelBase.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colCostoCon.setCellValueFactory(cellData -> {
            double costo = cellData.getValue().calcularCostoConsulta();
            java.text.DecimalFormat formato = new java.text.DecimalFormat("$ #,##0");
            return new javafx.beans.property.SimpleStringProperty(formato.format(costo));
        });
        // cargar datos previos y agregarlos a la tabla
        try {
            List<Perro> perrosGuardados = mascotaDAO.cargarPerros();
            List<Gato> gatosGuardados = mascotaDAO.cargarGatos();
            listaMascotas.addAll(perrosGuardados);
            listaMascotas.addAll(gatosGuardados);
            tblMascotas.setItems(listaMascotas);
        } catch (Exception e) {
            lblEstadoRegistro.setStyle("-fx-text-fill: red;");
            lblEstadoRegistro.setText("Error al cargar los datos desde el disco.");
        }
    }
    // Boton Registro
    @FXML
    private void registrarMascota() {
        String tipo = cmbTipo.getValue();
        if (tipo == null || tipo.isEmpty()) {
            lblEstadoRegistro.setStyle("-fx-text-fill: red;");
            lblEstadoRegistro.setText("Error: Debe seleccionar un tipo de mascota.");
            return;
        }
        String nombre = txtNombre.getText();
        if (nombre == null || nombre.isEmpty()) {
            lblEstadoRegistro.setStyle("-fx-text-fill: red;");
            lblEstadoRegistro.setText("Error: Debe ingresar un nombre.");
            return;
        }
        try {
            int edad;
            try {
                edad = Integer.parseInt(txtEdad.getText());
            } catch (NumberFormatException e) {
                lblEstadoRegistro.setStyle("-fx-text-fill: red;");
                lblEstadoRegistro.setText("Edad: debe ser un número entero entre 0 y 30.");
                return;
            }
            double peso;
            try {
                peso = Double.parseDouble(txtPeso.getText());
            } catch (NumberFormatException e) {
                lblEstadoRegistro.setStyle("-fx-text-fill: red;");
                lblEstadoRegistro.setText("Peso: debe ser un número mayor que cero.");
                return;
            }
            Mascota nuevaMascota = null;
            if (tipo.equals("Perro")) {
                String raza = txtRaza.getText();
                if (raza == null || raza.trim().isEmpty()) {
                    lblEstadoRegistro.setStyle("-fx-text-fill: red;");
                    lblEstadoRegistro.setText("Raza: no puede estar vacía ni contener solo espacios.");
                    return;
                }
                boolean vacunado = chkVacunaPerro.isSelected();
                boolean adopcion = chkAdopcion.isSelected();

                Perro perro = new Perro(nombre, edad, peso, raza.trim(), vacunado);
                if (adopcion) {
                    perro.marcarDisponibleParaAdopcion();
                }
                nuevaMascota = perro;

            } else if (tipo.equals("Gato")) {
                boolean exterior = chkVacunaGato.isSelected(); // O el checkbox que uses para exterior
                nuevaMascota = new Gato(nombre, edad, peso, exterior);
            }
            listaMascotas.add(nuevaMascota);
            // Guardamos los perros
            if (nuevaMascota instanceof Perro) {
                // Filtramos de la tabla general solo los perros para guardarlos en perros.json
                List<Perro> listaPerros = new ArrayList<>();
                for (Mascota m : listaMascotas) {
                    if (m instanceof Perro) {
                        listaPerros.add((Perro) m);
                    }
                }
                mascotaDAO.guardarPerros(listaPerros);
            // Guardamos los gatos
            } else if (nuevaMascota instanceof Gato) {
                // Filtramos de la tabla general solo los gatos para guardarlos en gatos.json
                List<Gato> listaGatos = new ArrayList<>();
                for (Mascota m : listaMascotas) {
                    if (m instanceof Gato) {
                        listaGatos.add((Gato) m);
                    }
                }
                mascotaDAO.guardarGatos(listaGatos);
            }
            // Mensaje de éxito
            lblEstadoRegistro.setStyle("-fx-text-fill: green;");
            lblEstadoRegistro.setText(nuevaMascota.getNombre() + " (" + tipo + ") registrado correctamente.");
            // Limpiar formulario para un nuevo registro
            limpiarDatos();
        } catch (IllegalArgumentException e){
            lblEstadoRegistro.setStyle("-fx-text-fill: red;");
            lblEstadoRegistro.setText(e.getMessage());
        }
    }

    private void limpiarDatos() {
        cmbTipo.setValue(null);
        txtNombre.clear();
        txtEdad.clear();
        txtPeso.clear();
        txtRaza.clear();
        chkVacunaPerro.setSelected(false);
        chkAdopcion.setSelected(false);
        chkVacunaGato.setSelected(false);
        datosPerro.setDisable(true);
        datosGato.setDisable(true);
    }
    @FXML
    private void tipoSeleccionado() {
        String tipo = cmbTipo.getValue();
        if (tipo != null) {
            if (tipo.equals("Perro")) {
                datosPerro.setDisable(false);
                datosGato.setDisable(true);
            } else if (tipo.equals("Gato")) {
                datosGato.setDisable(false);
                datosPerro.setDisable(true);
            }
        }
    }
    @FXML
    private void eliminarMascota() {
        // Tomar la mascota seleccionada en la tabla
        Mascota mascotaSeleccionada = tblMascotas.getSelectionModel().getSelectedItem();

        // informar que no se puede eliminar sin nada seleccionado
        if (mascotaSeleccionada == null) {
            lblEstadoRegistro.setStyle("-fx-text-fill: red;");
            lblEstadoRegistro.setText("Por favor, selecciona una mascota.");
            return;
        }

        // Eliminar de la tabla visible
        listaMascotas.remove(mascotaSeleccionada);

        // Actualizar el Json
        if (mascotaSeleccionada instanceof Perro) {
            List<Perro> listaPerrosRestantes = new ArrayList<>();
            for (Mascota m : listaMascotas) {
                if (m instanceof Perro) {
                    listaPerrosRestantes.add((Perro) m);
                }
            }
            mascotaDAO.guardarPerros(listaPerrosRestantes);
            lblEstadoRegistro.setStyle("-fx-text-fill: green;");
            lblEstadoRegistro.setText("Perro eliminado con exito.");

        } else if (mascotaSeleccionada instanceof Gato) {
            List<Gato> listaGatosRestantes = new ArrayList<>();
            for (Mascota m : listaMascotas) {
                if (m instanceof Gato) {
                    listaGatosRestantes.add((Gato) m);
                }
            }
            try {
                mascotaDAO.guardarGatos(listaGatosRestantes);
                lblEstadoRegistro.setStyle("-fx-text-fill: green;");
                lblEstadoRegistro.setText("Gato eliminado exitosamente.");
            }catch (Exception e) {
                lblEstadoRegistro.setStyle("-fx-text-fill: red;");
                lblEstadoRegistro.setText("Error al registrar la eliminacion.");
            }
        }
    }
}

