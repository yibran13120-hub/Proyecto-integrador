package srexwingsapp.reportes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import srexwingsapp.conexion.Conexion;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReporteVentasPDF {

    public static void generarReporteVentasDia(JFrame parent) {
        try {
            // =================== SELECTOR DE CARPETA ===================
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Seleccionar carpeta para guardar reporte");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int seleccion = chooser.showSaveDialog(parent);
            if (seleccion != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(parent,
                        "Operación cancelada. No se generó el reporte.");
                return;
            }

            File carpeta = chooser.getSelectedFile();
            String fechaActual = java.time.LocalDate.now().toString();
            String ruta = carpeta.getAbsolutePath() + "/Reporte_Ventas_" + fechaActual + ".pdf";

            // =================== CONSULTA BD ===================
            Connection con = Conexion.getConexion();
            String sql = "SELECT folio, cliente, nombre_producto, cantidad, total, fecha " +
                         "FROM pedidos WHERE DATE(fecha) = CURDATE()";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // ================= CREACIÓN DEL PDF =================
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            Paragraph encabezado = new Paragraph("Reporte de Ventas del Día\nEssRexx_Wings\n\n", titulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);

            PdfPTable tabla = new PdfPTable(6);
            tabla.addCell("Folio");
            tabla.addCell("Cliente");
            tabla.addCell("Producto");
            tabla.addCell("Cant.");
            tabla.addCell("Total");
            tabla.addCell("Fecha");

            double totalGeneral = 0.0;

            while (rs.next()) {
                tabla.addCell(rs.getString("folio"));
                tabla.addCell(rs.getString("cliente"));
                tabla.addCell(rs.getString("nombre_producto"));
                tabla.addCell(rs.getString("cantidad"));
                tabla.addCell("$" + rs.getString("total"));
                tabla.addCell(rs.getString("fecha"));

                totalGeneral += rs.getDouble("total");
            }

            documento.add(tabla);

            Paragraph totalFinal = new Paragraph("\nTotal del día: $" + totalGeneral,
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
            documento.add(totalFinal);

            documento.close();

            JOptionPane.showMessageDialog(parent,
                    "Reporte generado exitosamente:\n" + ruta,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // automáticamente abre el PDF
            java.awt.Desktop.getDesktop().open(new File(ruta));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                    "Error al generar reporte: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
