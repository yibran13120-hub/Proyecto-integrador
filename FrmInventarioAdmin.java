package srexwingsapp.interfaces;

import srexwingsapp.conexion.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FrmInventarioAdmin extends JFrame {

    private String usuarioLogueado;
    private JTable tablaProductos;
    private DefaultTableModel modelo;
    private JButton btnGuardarCambios, btnRefrescar, btnCerrar, btnAgregar, btnEliminar;

    private JComboBox<String> cmbNombre;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrecio, txtCaducidad, txtInventario;

    private final Color COLOR_BG_MAIN    = new Color(4, 46, 90);     // azul
    private final Color COLOR_HEADER     = new Color(220, 53, 69);   // rojo
    private final Color COLOR_PANEL      = new Color(255, 239, 204); // crema
    private final Color COLOR_PRIMARY    = new Color(247, 201, 71);  // amarillo
    private final Color COLOR_SECONDARY  = new Color(0, 82, 156);    // azul botón

    private final String[] CATEGORIAS = {"Comida", "Postre", "Bebida"};
    private final String[] PRODUCTOS  = {"Boneless", "Alitas", "Wings Pop", "Wings Pop Extrem", "Agua", "Refresco", "Café"};

    public FrmInventarioAdmin(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        inicializarVentana();
        inicializarComponentes();
        inicializarEventos();
        cargarProductos();
    }

    private void inicializarVentana() {
        setTitle("EssRexx_Wings - Inventario (ADMIN)");
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(COLOR_BG_MAIN);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {

        JPanel contenedor = new JPanel(null);
        contenedor.setPreferredSize(new Dimension(1100, 620));
        contenedor.setBackground(COLOR_BG_MAIN);

        JPanel header = new JPanel(null);
        header.setBackground(COLOR_HEADER);
        header.setBounds(0, 0, 1100, 60);
        contenedor.add(header);

        JLabel lblTitulo = new JLabel("Gestión de Inventario (ADMIN)", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setBounds(300, 10, 500, 40);
        header.add(lblTitulo);

        // Tabla Productos
        JPanel panelTabla = new JPanel(null);
        panelTabla.setBackground(COLOR_PANEL);
        panelTabla.setBounds(40, 80, 1020, 300);
        contenedor.add(panelTabla);

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Categoría", "Precio", "Caducidad", "Inventario"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        tablaProductos = new JTable(modelo);
        tablaProductos.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.setBounds(20, 20, 980, 260);
        panelTabla.add(scroll);

        // Panel agregar producto
        JPanel panelAgregar = new JPanel(null);
        panelAgregar.setBackground(COLOR_PANEL);
        panelAgregar.setBounds(40, 390, 1020, 100);
        panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar nuevo producto"));
        contenedor.add(panelAgregar);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 30, 90, 20);
        panelAgregar.add(lblNombre);

        cmbNombre = new JComboBox<>(PRODUCTOS);
        cmbNombre.setBounds(80, 30, 140, 24);
        panelAgregar.add(cmbNombre);

        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setBounds(240, 30, 80, 20);
        panelAgregar.add(lblCategoria);

        cmbCategoria = new JComboBox<>(CATEGORIAS);
        cmbCategoria.setBounds(310, 30, 110, 24);
        panelAgregar.add(cmbCategoria);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(430, 30, 60, 20);
        panelAgregar.add(lblPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(480, 30, 80, 24);
        panelAgregar.add(txtPrecio);

        JLabel lblCad = new JLabel("Caducidad (yyyy-MM-dd):");
        lblCad.setBounds(570, 15, 180, 20);
        panelAgregar.add(lblCad);

        txtCaducidad = new JTextField();
        txtCaducidad.setBounds(570, 35, 130, 24);
        panelAgregar.add(txtCaducidad);

        JLabel lblInv = new JLabel("Inv=");
        lblInv.setBounds(720, 30, 40, 20);
        panelAgregar.add(lblInv);

        txtInventario = new JTextField();
        txtInventario.setBounds(760, 30, 80, 24);
        panelAgregar.add(txtInventario);

        // Botones
        btnAgregar = crearBoton("Agregar producto", COLOR_HEADER);
        btnAgregar.setBounds(40, 510, 180, 35);

        btnEliminar = crearBoton("Eliminar seleccionado", new Color(200, 0, 0));  // NUEVO
        btnEliminar.setBounds(230, 510, 180, 35);

        btnGuardarCambios = crearBoton("Guardar cambios (fila)", COLOR_SECONDARY);
        btnGuardarCambios.setBounds(420, 510, 200, 35);

        btnRefrescar = crearBoton("Refrescar", COLOR_PRIMARY);
        btnRefrescar.setBounds(630, 510, 160, 35);

        btnCerrar = crearBoton("Cerrar", Color.GRAY);
        btnCerrar.setBounds(800, 510, 160, 35);

        contenedor.add(btnAgregar);
        contenedor.add(btnEliminar);
        contenedor.add(btnGuardarCambios);
        contenedor.add(btnRefrescar);
        contenedor.add(btnCerrar);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        getContentPane().add(contenedor, gbc);
    }

    private JButton crearBoton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(bg.darker(), 2, true));
        btn.setBackground(bg);
        return btn;
    }

    private void inicializarEventos() {
        btnRefrescar.addActionListener(e -> cargarProductos());
        btnAgregar.addActionListener(e -> agregarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());  
        btnGuardarCambios.addActionListener(e -> guardarCambios());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void cargarProductos() {
        modelo.setRowCount(0);
        String sql = "SELECT * FROM productos";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getDate("caducidad"),
                        rs.getInt("inventario")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage());
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.");
            return;
        }

        int id = Integer.parseInt(tablaProductos.getValueAt(fila, 0).toString());

        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar definitivamente este producto?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (r == JOptionPane.YES_OPTION) {
            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM productos WHERE id_producto=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                modelo.removeRow(fila);
                JOptionPane.showMessageDialog(this, "Producto eliminado.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void agregarProducto() {
        try {
            String nombre = cmbNombre.getSelectedItem().toString();
            String categoria = cmbCategoria.getSelectedItem().toString();
            double precio = Double.parseDouble(txtPrecio.getText());
            String cad = txtCaducidad.getText();
            int inv = Integer.parseInt(txtInventario.getText());

            String sql = "INSERT INTO productos (nombre, categoria, precio, caducidad, inventario) VALUES (?,?,?,?,?)";
            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, nombre);
                ps.setString(2, categoria);
                ps.setDouble(3, precio);
                ps.setDate(4, java.sql.Date.valueOf(cad));
                ps.setInt(5, inv);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Producto agregado.");
                cargarProductos();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar: " + ex.getMessage());
        }
    }

    private void guardarCambios() {
        cargarProductos();
        JOptionPane.showMessageDialog(this, "Actualización aplicada.");
    }
}
