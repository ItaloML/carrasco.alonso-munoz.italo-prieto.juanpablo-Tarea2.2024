package com.example.tarea2etapafinal.controlador;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import com.example.tarea2etapafinal.modelo.*;
import com.example.tarea2etapafinal.vista.MenuGC;
import com.example.tarea2etapafinal.vista.View;

import java.util.ArrayList;
import java.util.Scanner;


public class Controlador {
    Scanner in;
    ArrayList<Item >inventarioOriginal = new ArrayList<>();
    private Mascota mascota;
    private final View view;
    private final Timeline time;


    public Controlador(Scanner in) {
        this.in = in;
        readConfiguration(in);
        view = new View(mascota.getName());
        double INTERVAL = 0.5;
        time = new Timeline(new KeyFrame(Duration.seconds(INTERVAL), _-> {
            if (mascota.getState()!=Estado.Muerto) {
                mascota.GetOld();
                view.getPanelDeEstado().getEstado().setText(mascota.determineState().toString());
                view.getPanelDeEstado().getNameText().setText("Nombre:\t\t" + mascota.getName() + "\n\nEdad:\t\t"+mascota.getAge().get());
            }
            else {
                mascota.morir();
                view.getInventario().disableButtons();
            }
        }));

        time.setCycleCount(Timeline.INDEFINITE);
        time.play();
        time.pause();
        view.getInventario().intializeInventarioGC(inventarioOriginal);
        attachItemsEventHandlers();
        view.getInventario().disableButtons();
        bindPetProperties();
        attachMenuEventHandlers();
    }


    private void readConfiguration(Scanner in) {
        // Creación de mascota
        String nombre_mascota = in.nextLine();
        this.mascota = new Mascota(nombre_mascota);

        // Llenando del inventario de la mascota
        while (in.hasNextLine()) {
            String linea = in.nextLine();
            String[] item_csv = linea.split(";");
            int id = Integer.parseInt(item_csv[0]);
            String tipoItem = item_csv[1];
            String nombreItem = item_csv[2];
            if (tipoItem.equals("Juguete")) {
                String rutaImagen = "/" + item_csv[3];
                inventarioOriginal.add(new Juguete(nombreItem, id, rutaImagen));
            } else if (tipoItem.equals("Alimento")) {
                int cantidad = Integer.parseInt(item_csv[3]);
                inventarioOriginal.add(new Comida(nombreItem, id, cantidad));
            } else {
                int cantidad = Integer.parseInt(item_csv[3]);
                inventarioOriginal.add(new Medicina(nombreItem, id, cantidad));
            }
        }
    }


    public View getView() {
        return view;
    }

    private void restart() {
        time.pause();
        mascota.reset();
        view.getPanelDeEstado().getEstado().setText(mascota.determineState().toString());
        view.getPanelDeEstado().getNameText().setText("Nombre:\t\t" + mascota.getName() + "\n\nEdad:\t\t"+mascota.getAge().get());
        view.getInventario().deleteAndDettachEventHandlers();
        view.getInventario().intializeInventarioGC(inventarioOriginal);
        view.getInventario().attachEventHandlers(mascota);
        view.getEscenaMascota().encenderLuz();
        view.getInventario().disableButtons();
    }



    private void attachItemsEventHandlers() {
        view.getInventario().attachEventHandlers(mascota);
    }

    private void bindPetProperties() {
        view.getPanelDeEstado().getHappinessBar().progressProperty().bind(mascota.getHappiness().divide(100.0).asObject());
        view.getPanelDeEstado().getEnergyBar().progressProperty().bind(mascota.getEnergy().divide(100.0).asObject());
        view.getPanelDeEstado().getHealthBar().progressProperty().bind(mascota.getHealth().divide(100.0).asObject());
    }



    private void attachMenuEventHandlers() {
        MenuGC menu = view.getMenu();

        menu.getIniciar().setOnAction(_->{
            if (time.getStatus() == Animation.Status.PAUSED) time.play();
            view.getInventario().enableButtons();
        });

        menu.getSalir().setOnAction(_->{
            System.exit(0);
        });

        menu.getReiniciar().setOnAction(_->restart());

        menu.getApagarLuz().setOnAction(_->{
            view.getEscenaMascota().apagarLuz();
            mascota.setSleeping(true);
            view.getInventario().disableButtons();
        });

        menu.getEncenderLuz().setOnAction(_->{
            view.getEscenaMascota().encenderLuz();
            mascota.setSleeping(false);
            view.getInventario().enableButtons();
        });
    }
}
