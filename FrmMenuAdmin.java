/*
 * Panel de administración – EssRexx_Wings
 */
package srexwingsapp.interfaces;

import srexwingsapp.reportes.ReporteVentasPDF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FrmMenuAdmin extends JFrame {

    private String usuarioLogueado;
    private JLabel lblUsuario;
    private JButton btnPedidos;
    private JButton btnInventario;
    private JButton btnReporteVentas;
    private JButton btnCerrarSesion;

    // Paleta EssRexx_Wings 
    private final Color COLOR_BG_MAIN    = new Color(4, 46, 90);
    private final Color COLOR_HEADER    = new Color(220, 53, 69);
    private final Color COLOR_PANEL     = new Color(255, 239, 204);
    private final Color COLOR_PRIMARY   = new Color(247, 201, 71);
    private final Color COLOR_SECONDARY = new Color(0, 82, 156);
    private final Color COLOR_TEXT_DARK = new Color(40, 40, 40);

    public FrmMenuAdmin(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        inicializarVentana();
        inicializarComponentes();
        inicializarEventos();
    }

    private void inicializarVentana() {
        setTitle("EssRexx_Wings – Panel Administrador");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(COLOR_BG_MAIN);
        getContentPane().setLayout(new GridBagLayout()); 
    }

    private void inicializarComponentes() {

        // Contenedor principal
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setPreferredSize(new Dimension(520, 340));

        // ====== Header rojo ======
        JPanel header = new JPanel(null);
        header.setBackground(COLOR_HEADER);
        header.setPreferredSize(new Dimension(520, 60));

        JLabel lblTitulo = new JLabel("Panel de Administración – EssRexx_Wings");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(40, 15, 440, 30);
        header.add(lblTitulo);

        contenedor.add(header, BorderLayout.NORTH);

        // ====== Panel central crema ======
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 210, 170)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 210, 170)),
                BorderFactory.createEmptyBorder(15, 40, 15, 40)
        ));

        lblUsuario = new JLabel("Admin: " + usuarioLogueado);
        lblUsuario.setForeground(COLOR_TEXT_DARK);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 13));

        btnPedidos = new JButton("Pedidos");
        btnInventario = new JButton("Inventario");
        btnReporteVentas = new JButton("Reporte ventas del día (PDF)");

        Dimension tamañoBoton = new Dimension(260, 40);

        estilizarBoton(btnPedidos, COLOR_PRIMARY, COLOR_TEXT_DARK, tamañoBoton);
        estilizarBoton(btnInventario, COLOR_PRIMARY, COLOR_TEXT_DARK, tamañoBoton);
        estilizarBoton(btnReporteVentas, COLOR_SECONDARY, Color.WHITE, tamañoBoton);

        btnPedidos.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnInventario.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReporteVentas.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblUsuario);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnPedidos);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnInventario);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnReporteVentas);

        contenedor.add(panel, BorderLayout.CENTER);

        // ====== Botón Cerrar sesión centrado abajo ======
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(COLOR_BG_MAIN);
        btnCerrarSesion = new JButton("Cerrar sesión");
        estilizarBoton(btnCerrarSesion, COLOR_HEADER, Color.WHITE, new Dimension(150, 35));
        panelInferior.add(btnCerrarSesion);

        contenedor.add(panelInferior, BorderLayout.SOUTH);

        // Agregar contenedor centrado al frame
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        getContentPane().add(contenedor, gbc);
    }

    private void inicializarEventos() {

        btnPedidos.addActionListener((ActionEvent e) -> {
            FrmPedidosNuevo frm = new FrmPedidosNuevo("ADMIN", usuarioLogueado);
            frm.setVisible(true);
        });

        btnInventario.addActionListener((ActionEvent e) -> {
            FrmInventarioAdmin frm = new FrmInventarioAdmin(usuarioLogueado);
            frm.setVisible(true);
        });

        btnReporteVentas.addActionListener((ActionEvent e) -> {
            ReporteVentasPDF.generarReporteVentasDia(this);
        });

        btnCerrarSesion.addActionListener((ActionEvent e) -> {
            dispose();
            new FrmLoginNuevo().setVisible(true);
        });
    }

    // ==== ESTILO BOTONES CENTRADOS ====
    private void estilizarBoton(JButton btn, Color base, Color text, Dimension size) {
        btn.setBackground(base);
        btn.setForeground(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(size);
        btn.setMaximumSize(size);

        btn.setBorder(new javax.swing.border.LineBorder(base.darker(), 2, true));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(base.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(base);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new FrmMenuAdmin("Administrador del sistema").setVisible(true));
    }
}
