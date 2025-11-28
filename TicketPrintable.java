/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package srexwingsapp.reportes;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketPrintable implements Printable {

    private final String nombreNegocio;
    private final String direccion;
    private final String telefono;

    private final String mesa;
    private final String mesero;
    private final String cliente;

    private final DefaultTableModel modeloTabla;
    private final String subtotal;
    private final String iva;
    private final String total;

    public TicketPrintable(String nombreNegocio,
                           String direccion,
                           String telefono,
                           String mesa,
                           String mesero,
                           String cliente,
                           DefaultTableModel modeloTabla,
                           String subtotal,
                           String iva,
                           String total) {

        this.nombreNegocio = nombreNegocio;
        this.direccion = direccion;
        this.telefono = telefono;

        this.mesa = mesa;
        this.mesero = mesero;
        this.cliente = cliente;

        this.modeloTabla = modeloTabla;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        Font fuenteTitulo = new Font("Monospaced", Font.BOLD, 10);
        Font fuenteNormal = new Font("Monospaced", Font.PLAIN, 9);

        int y = 10;
        int salto = 12;

        g2d.setFont(fuenteTitulo);
        g2d.drawString(nombreNegocio, 0, y); y += salto;

        g2d.setFont(fuenteNormal);
        g2d.drawString(direccion, 0, y); y += salto;

        if (telefono != null && !telefono.isEmpty()) {
            g2d.drawString("Tel: " + telefono, 0, y); y += salto;
        }

        y += 4;
        g2d.drawString("--------------------------------", 0, y); y += salto;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fecha = LocalDateTime.now().format(formatter);

        g2d.drawString("Fecha: " + fecha, 0, y); y += salto;
        g2d.drawString("Mesa: " + mesa, 0, y); y += salto;
        g2d.drawString("Mesero: " + mesero, 0, y); y += salto;
        if (cliente != null && !cliente.isEmpty()) {
            g2d.drawString("Cliente: " + cliente, 0, y); y += salto;
        }

        y += 4;
        g2d.drawString("--------------------------------", 0, y); y += salto;

        g2d.drawString("Cant  Producto       Importe", 0, y); y += salto;
        g2d.drawString("--------------------------------", 0, y); y += salto;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String producto = String.valueOf(modeloTabla.getValueAt(i, 0));
            String cant     = String.valueOf(modeloTabla.getValueAt(i, 1));
            String importe  = String.valueOf(modeloTabla.getValueAt(i, 2));

            if (producto.length() > 12) {
                producto = producto.substring(0, 12);
            }

            String linea = String.format("%-4s %-12s %7s", cant, producto, importe);
            g2d.drawString(linea, 0, y);
            y += salto;
        }

        y += 4;
        g2d.drawString("--------------------------------", 0, y); y += salto;

        if (subtotal != null && !subtotal.isEmpty()) {
            g2d.drawString("SUBTOTAL: " + subtotal, 0, y); y += salto;
        }
        if (iva != null && !iva.isEmpty()) {
            g2d.drawString("IVA:      " + iva, 0, y); y += salto;
        }

        g2d.setFont(fuenteTitulo);
        g2d.drawString("TOTAL:    " + total, 0, y); y += salto;

        y += salto;
        g2d.setFont(fuenteNormal);
        g2d.drawString("Gracias por su preferencia", 0, y); y += salto;
        g2d.drawString("¡Vuelva pronto!", 0, y);

        return PAGE_EXISTS;
    }
}
