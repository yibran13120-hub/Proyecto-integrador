/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package srexwingsapp;

import srexwingsapp.interfaces.FrmLoginNuevo;

import javax.swing.*;
import java.awt.*;

public class SrexWingsApp {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // === Cargar logo y escalarlo para los diálogos ===
            ImageIcon iconoOriginal = new ImageIcon(
                    SrexWingsApp.class.getResource("/assets/logo.png")
            );
            Image img = iconoOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            ImageIcon appIcon = new ImageIcon(img);

            // === Este logo se usará en TODOS los JOptionPane ===
            UIManager.put("OptionPane.informationIcon", appIcon);
            UIManager.put("OptionPane.errorIcon", appIcon);
            UIManager.put("OptionPane.warningIcon", appIcon);
            UIManager.put("OptionPane.questionIcon", appIcon);

            // Lanzar login
            FrmLoginNuevo frm = new FrmLoginNuevo();
            frm.setVisible(true);
        });
    }
}
