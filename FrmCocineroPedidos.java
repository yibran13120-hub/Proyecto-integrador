package srexwingsapp.interfaces;

import srexwingsapp.conexion.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FrmCocineroPedidos extends JFrame {

    private String usuarioLogueado;
    private JTable tablaPedidos;
    private DefaultTableModel modelo;
    private JButton btnRefrescar, btnMarcarListo, btnCerrar;

    // PALETA EssRexx_Wings (igual que FrmPedidosNuevo y login)
    private final Color COLOR_BG_MAIN    = new Color(4, 46, 90);
    private final Color COLOR_HEADER    = new Color(220, 53, 69);
    private final Color COLOR_PANEL     = new Color(255, 239, 204);
    private final Color COLOR_SECONDARY = new Color(0, 82, 156);
    private final Color COLOR_PRIMARY   = new Color(247, 201, 71);

    public FrmCocineroPedidos(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        inicializarVentana();
        inicializarComponentes();
        inicializarEventos();
        cargarPedidos();
    }

    private void inicializarVentana() {
        setTitle("EssRexx_Wings - Vista Cocinero");
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Pantalla completa
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG_MAIN);
    }

    private void inicializarComponentes() {

        // BARRA SUPERIOR
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel lblTitulo = new JLabel("Pedidos en Cocina - EssRexx_Wings", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel("Cocinero: " + usuarioLogueado);
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUsuario.setForeground(Color.WHITE);

        header.add(lblUsuario, BorderLayout.WEST);
        header.add(lblTitulo, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // PANEL TABLA CENTRADO
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(COLOR_PANEL);
        panelTabla.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        // INCLUIMOS TAMANO Y SABOR PARA QUE EL CHEF SEPA QUÉ HACER
        modelo = new DefaultTableModel(
                new Object[]{
                        "ID Pedido",
                        "Mesa",
                        "Cliente",
                        "Producto",
                        "Tamaño/Pzas",
                        "Sabor",
                        "Cantidad",
                        "Total",
                        "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPedidos = new JTable(modelo);
        tablaPedidos.setRowHeight(28);
        tablaPedidos.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTableHeader th = tablaPedidos.getTableHeader();
        th.setBackground(COLOR_SECONDARY);
        th.setForeground(Color.WHITE);
        th.setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tablaPedidos);
        panelTabla.add(scroll, BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);

        // BOTONES INFERIORES
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(COLOR_PANEL);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        btnRefrescar   = crearBoton("Refrescar", COLOR_PRIMARY, Color.BLACK);
        btnMarcarListo = crearBoton("Marcar como listo", COLOR_SECONDARY, Color.WHITE);
        btnCerrar      = crearBoton("Regresar al login", new Color(100, 100, 100), Color.WHITE);

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnMarcarListo);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(170, 40));
        btn.setBorder(BorderFactory.createLineBorder(bg.darker(), 2, true));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });

        return btn;
    }

    private void inicializarEventos() {
        btnRefrescar.addActionListener((ActionEvent e) -> cargarPedidos());

        btnMarcarListo.addActionListener((ActionEvent e) -> marcarPedidoComoListo());

        // REGRESAR AL LOGIN
        btnCerrar.addActionListener((ActionEvent e) -> {
            dispose();
            new FrmLoginNuevo().setVisible(true);
        });
    }

    private void cargarPedidos() {
        modelo.setRowCount(0);

        // IMPORTANTE: AQUÍ YA PEDIMOS TAMANO Y SABOR A LA BD
        String sql =
                "SELECT id_pedido, mesa, cliente, nombre_producto, tamano, sabor, cantidad, total, estado " +
                "FROM pedidos " +
                "WHERE estado IN ('PENDIENTE','EN PREPARACION') " +
                "ORDER BY fecha ASC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_pedido"),
                        rs.getString("mesa"),
                        rs.getString("cliente"),
                        rs.getString("nombre_producto"),
                        rs.getString("tamano"),
                        rs.getString("sabor"),
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("total"),
                        rs.getString("estado")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos: " + ex.getMessage());
        }
    }

    private void marcarPedidoComoListo() {
        int fila = tablaPedidos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido");
            return;
        }

        int idPedido = (int) tablaPedidos.getValueAt(fila, 0);

        String sql = "UPDATE pedidos SET estado = 'LISTO' WHERE id_pedido = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPedido);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pedido listo para entregar");
            cargarPedidos();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        }
    }
}
