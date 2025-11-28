/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package srexwingsapp.reportes;

import srexwingsapp.conexion.Conexion;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ReporteVentasUtil {

    public static void generarReporteVentasDia(JFrame parent) {

        LocalDate hoy = LocalDate.now();

        String sql = "SELECT id_pedido, folio, mesa, cliente, nombre_producto, cantidad, total, fecha " +
                     "FROM pedidos WHERE DATE(fecha) = CURDATE() ORDER BY fecha ASC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(parent,
                        "No hay ventas registradas hoy: " + hoy,
                        "Reporte de ventas",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String rutaEscritorio = System.getProperty("user.home") + "/Desktop/";
            File archivo = new File(rutaEscritorio + "reporte_ventas_" + hoy + ".pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(archivo));
            document.open();

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("EssRexx_Wings\nReporte de ventas del día: " + hoy + "\n\n", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            PdfPTable tabla = new PdfPTable(7);
            tabla.setWidthPercentage(100);
            tabla.addCell("ID");
            tabla.addCell("Folio");
            tabla.addCell("Mesa");
            tabla.addCell("Cliente");
            tabla.addCell("Producto");
            tabla.addCell("Cant.");
            tabla.addCell("Total");

            double totalDia = 0;

            while (rs.next()) {
                tabla.addCell(String.valueOf(rs.getInt("id_pedido")));
                tabla.addCell(rs.getString("folio"));
                tabla.addCell(rs.getString("mesa"));
                tabla.addCell(rs.getString("cliente"));
                tabla.addCell(rs.getString("nombre_producto"));
                tabla.addCell(String.valueOf(rs.getInt("cantidad")));
                double total = rs.getDouble("total");
                totalDia += total;
                tabla.addCell("$" + String.format("%.2f", total));
            }

            document.add(tabla);
            document.add(new Paragraph("\nTotal de ventas del día: $" + String.format("%.2f", totalDia)));
            document.close();

            JOptionPane.showMessageDialog(parent,
                    "Reporte generado correctamente en:\n" + archivo.getAbsolutePath(),
                    "Reporte ventas PDF",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException | DocumentException | IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Error al generar el reporte:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
