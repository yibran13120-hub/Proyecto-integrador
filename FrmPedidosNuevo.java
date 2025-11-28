/*
 * FrmPedidosNuevo.java
 */
package srexwingsapp.interfaces;

import srexwingsapp.conexion.Conexion;
import srexwingsapp.reportes.TicketPrintable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashSet;

public class FrmPedidosNuevo extends JFrame {

    // ==== Datos de sesión ====
    private String rolUsuario;
    private String nombreUsuario;

    // ==== Componentes ====
    private JTable tablaPedido;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEliminar, btnGuardar, btnCancelar, btnLimpiar, btnImprimir, btnCerrarSesion;
    private JTextField txtCliente, txtMesero, txtMesa, txtSubtotal, txtImpuestos, txtTotal;

    // ==== Catálogo de productos (del menú) ====
    private final Map<String, Double> MENU_PRECIOS = new LinkedHashMap<>();

    // ==== Grupos de productos para inventario y sabores ====
    private final Set<String> PRODUCTOS_ALITAS = new HashSet<>(Arrays.asList(
            "Boneless", "Boneless c/papas",
            "Alitas sencillas", "Alitas sencillas c/papas",
            "Wings Pop", "Wings Pop Extrem"
    ));

    private final Set<String> PRODUCTOS_BEBIDAS = new HashSet<>(Arrays.asList(
            "Agua de sabor", "Refresco", "Café"
    ));

    private final Set<String> PRODUCTOS_POSTRES = new HashSet<>(Arrays.asList(
            "Gelatina con crema", "Duraznos con crema", "Uvas con crema",
            "Frutas mixtas con crema", "Pay de limón",
            "Flan napolitano", "Arroz con leche"
    ));

    // ==== Paleta EssRexx_Wings ====
    private final Color COLOR_BG_MAIN    = new Color(4, 46, 90);     // azul fondo
    private final Color COLOR_HEADER    = new Color(220, 53, 69);    // rojo barra
    private final Color COLOR_PANEL     = new Color(255, 239, 204);  // crema
    private final Color COLOR_PANEL_ALT = new Color(243, 225, 188);  // crema más oscuro
    private final Color COLOR_PRIMARY   = new Color(247, 201, 71);   // amarillo
    private final Color COLOR_SECONDARY = new Color(0, 82, 156);     // azul botón
    private final Color COLOR_TEXT_DARK = new Color(40, 40, 40);
    private final Color COLOR_TEXT_MUT  = new Color(90, 90, 90);

    // ==== Constructor principal ====
    public FrmPedidosNuevo(String rolUsuario, String nombreUsuario) {
        this.rolUsuario = rolUsuario;
        this.nombreUsuario = nombreUsuario;

        inicializarMenuPrecios();
        inicializarVentana();
        inicializarComponentes();
        aplicarPermisos();
    }

    // Constructor de prueba
    public FrmPedidosNuevo() {
        this("ADMIN", "Demo");
    }

    // ================= CARGAR PRECIOS DEL MENÚ =================
    private void inicializarMenuPrecios() {
        // Platillos principales
        MENU_PRECIOS.put("Boneless", 80.0);
        MENU_PRECIOS.put("Boneless c/papas", 90.0);
        MENU_PRECIOS.put("Alitas sencillas", 75.0);
        MENU_PRECIOS.put("Alitas sencillas c/papas", 85.0);
        MENU_PRECIOS.put("Wings Pop", 110.0);
        MENU_PRECIOS.put("Wings Pop Extrem", 150.0);

        // Postres
        MENU_PRECIOS.put("Gelatina con crema", 35.0);
        MENU_PRECIOS.put("Duraznos con crema", 40.0);
        MENU_PRECIOS.put("Uvas con crema", 40.0);
        MENU_PRECIOS.put("Frutas mixtas con crema", 40.0);
        MENU_PRECIOS.put("Pay de limón", 40.0);
        MENU_PRECIOS.put("Flan napolitano", 40.0);
        MENU_PRECIOS.put("Arroz con leche", 40.0);

        // Bebidas
        MENU_PRECIOS.put("Agua de sabor", 30.0);
        MENU_PRECIOS.put("Refresco", 25.0);
        MENU_PRECIOS.put("Café", 20.0);
    }

    // ================= VENTANA =================
    private void inicializarVentana() {
        setTitle("EssRexx_Wings - Captura de Pedido");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setBackground(COLOR_BG_MAIN);
        getContentPane().setLayout(new GridBagLayout());
    }

    // ================= COMPONENTES =================
    private void inicializarComponentes() {

        // ========= CONTENEDOR PRINCIPAL CENTRADO =========
        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setOpaque(false);
        contenedorPrincipal.setPreferredSize(new Dimension(1000, 600));

        // ===== Barra superior =====
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(COLOR_HEADER);
        barraSuperior.setPreferredSize(new Dimension(0, 55));

        JLabel lblTitulo = new JLabel("Captura de Pedido - EssRexx_Wings", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        barraSuperior.add(lblTitulo, BorderLayout.CENTER);

        contenedorPrincipal.add(barraSuperior, BorderLayout.NORTH);

        // ===== Contenedor central =====
        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setOpaque(false);
        contenedorCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenedorPrincipal.add(contenedorCentral, BorderLayout.CENTER);

        // ===== Panel central tabla =====
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(COLOR_PANEL_ALT);
        panelCentro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 210, 170)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JPanel panelTablaContenedor = new JPanel(new BorderLayout());
        panelTablaContenedor.setBackground(COLOR_PANEL);
        panelTablaContenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Modelo de tabla: mesa, cliente, mesero + detalle
        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "Mesa", "Cliente", "Mesero",
                        "Cant.", "Producto", "Tamaño/Pzas",
                        "Sabor", "Nivel", "Precio", "Importe"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // columnas 0..7 editables, precio e importe no
                return column < 8;
            }
        };

        tablaPedido = new JTable(modeloTabla);
        tablaPedido.setRowHeight(24);
        tablaPedido.setGridColor(new Color(230, 210, 180));
        tablaPedido.setSelectionBackground(new Color(204, 229, 255));
        tablaPedido.setSelectionForeground(COLOR_TEXT_DARK);
        tablaPedido.setFillsViewportHeight(true);

        JTableHeader headerTabla = tablaPedido.getTableHeader();
        headerTabla.setBackground(COLOR_SECONDARY);
        headerTabla.setForeground(Color.WHITE);
        headerTabla.setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollTabla = new JScrollPane(tablaPedido);
        panelTablaContenedor.add(scrollTabla, BorderLayout.CENTER);
        panelCentro.add(panelTablaContenedor, BorderLayout.CENTER);

        contenedorCentral.add(panelCentro, BorderLayout.CENTER);

        // ===== Panel inferior (datos + totales) =====
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(COLOR_PANEL_ALT);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Datos cliente/mesero ---
        JPanel panelDatos = new JPanel(null);
        panelDatos.setPreferredSize(new Dimension(0, 90));
        panelDatos.setBackground(COLOR_PANEL);
        panelDatos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblMesa = new JLabel("Mesa/Cliente:");
        lblMesa.setForeground(COLOR_TEXT_DARK);
        lblMesa.setBounds(20, 10, 100, 20);
        panelDatos.add(lblMesa);

        txtMesa = new JTextField();
        txtMesa.setBounds(120, 10, 140, 24);
        panelDatos.add(txtMesa);

        JLabel lblMesero = new JLabel("Mesero/Cajero:");
        lblMesero.setForeground(COLOR_TEXT_DARK);
        lblMesero.setBounds(20, 45, 100, 20);
        panelDatos.add(lblMesero);

        txtMesero = new JTextField();
        txtMesero.setBounds(120, 45, 140, 24);
        panelDatos.add(txtMesero);

        JLabel lblCliente = new JLabel("Nombre cliente:");
        lblCliente.setForeground(COLOR_TEXT_DARK);
        lblCliente.setBounds(290, 10, 110, 20);
        panelDatos.add(lblCliente);

        txtCliente = new JTextField();
        txtCliente.setBounds(400, 10, 190, 24);
        panelDatos.add(txtCliente);

        JLabel lblInfoSesion = new JLabel("Sesión: " + nombreUsuario + "  |  Rol: " + rolUsuario);
        lblInfoSesion.setForeground(COLOR_TEXT_MUT);
        lblInfoSesion.setBounds(290, 45, 280, 20);
        panelDatos.add(lblInfoSesion);

        panelInferior.add(panelDatos, BorderLayout.CENTER);

        // --- Totales + botones ---
        JPanel panelTotales = new JPanel(null);
        panelTotales.setPreferredSize(new Dimension(0, 140)); // un poquito más alto
        panelTotales.setBackground(COLOR_PANEL);
        panelTotales.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblSubtotal = new JLabel("SUBTOTAL:");
        lblSubtotal.setForeground(COLOR_TEXT_DARK);
        lblSubtotal.setBounds(20, 10, 80, 20);
        panelTotales.add(lblSubtotal);

        txtSubtotal = new JTextField("0.00");
        txtSubtotal.setHorizontalAlignment(JTextField.RIGHT);
        txtSubtotal.setEditable(false);
        txtSubtotal.setBounds(100, 10, 100, 24);
        panelTotales.add(txtSubtotal);

        JLabel lblImpuestos = new JLabel("IVA 16%:");
        lblImpuestos.setForeground(COLOR_TEXT_DARK);
        lblImpuestos.setBounds(230, 10, 70, 20);
        panelTotales.add(lblImpuestos);

        txtImpuestos = new JTextField("0.00");
        txtImpuestos.setHorizontalAlignment(JTextField.RIGHT);
        txtImpuestos.setEditable(false);
        txtImpuestos.setBounds(300, 10, 100, 24);
        panelTotales.add(txtImpuestos);

        JLabel lblTotal = new JLabel("TOTAL A PAGAR:");
        lblTotal.setForeground(COLOR_TEXT_DARK);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotal.setBounds(20, 45, 120, 20);
        panelTotales.add(lblTotal);

        txtTotal = new JTextField("0.00");
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setFont(new Font("SansSerif", Font.BOLD, 13));
        txtTotal.setEditable(false);
        txtTotal.setBounds(140, 45, 120, 26);
        panelTotales.add(txtTotal);

        // Botones
        btnAgregar = new JButton("Agregar línea");
        estiloBotonAccion(btnAgregar, COLOR_PRIMARY, COLOR_TEXT_DARK);
        btnAgregar.setBounds(460, 10, 140, 30);
        panelTotales.add(btnAgregar);

        btnEliminar = new JButton("Eliminar línea");
        estiloBotonAccion(btnEliminar, COLOR_SECONDARY, Color.WHITE);
        btnEliminar.setBounds(460, 45, 140, 30);
        panelTotales.add(btnEliminar);

        btnGuardar = new JButton("Registrar pedido");
        estiloBotonAccion(btnGuardar, COLOR_HEADER, Color.WHITE);
        btnGuardar.setBounds(620, 10, 160, 30);
        panelTotales.add(btnGuardar);

        btnLimpiar = new JButton("Limpiar pedido");
        estiloBotonAccion(btnLimpiar, new Color(100, 100, 100), Color.WHITE);
        btnLimpiar.setBounds(620, 45, 160, 30);
        panelTotales.add(btnLimpiar);

        btnImprimir = new JButton("Imprimir ticket");
        estiloBotonAccion(btnImprimir, new Color(0, 140, 72), Color.WHITE);
        btnImprimir.setBounds(800, 10, 160, 30);
        panelTotales.add(btnImprimir);

        btnCancelar = new JButton("Cancelar pedido");
        estiloBotonAccion(btnCancelar, new Color(150, 0, 0), Color.WHITE);
        btnCancelar.setBounds(800, 45, 160, 30);
        panelTotales.add(btnCancelar);

        // === NUEVO: botón para volver al login / cerrar sesión ===
        btnCerrarSesion = new JButton("Cerrar sesión");
        estiloBotonAccion(btnCerrarSesion, new Color(70, 70, 70), Color.WHITE);
        btnCerrarSesion.setBounds(800, 80, 160, 30);
        panelTotales.add(btnCerrarSesion);

        panelInferior.add(panelTotales, BorderLayout.SOUTH);
        contenedorCentral.add(panelInferior, BorderLayout.SOUTH);

        // Meter el contenedor principal en el frame centrado
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        getContentPane().add(contenedorPrincipal, gbc);

        configurarEventosBotones();
    }

    private void estiloBotonAccion(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createLineBorder(bg.darker(), 2, true));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
    }

    // ================= EVENTOS BOTONES =================
    private void configurarEventosBotones() {

        // AGREGAR LÍNEA
        btnAgregar.addActionListener(e -> {

            if (txtMesa.getText().trim().isEmpty() || txtCliente.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Captura primero Mesa/Cliente y Nombre del cliente.",
                        "Datos faltantes",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<String> cmbProducto = new JComboBox<>();
            for (String nombre : MENU_PRECIOS.keySet()) {
                cmbProducto.addItem(nombre);
            }

            JSpinner spCant = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

            JComboBox<String> cmbTamano = new JComboBox<>();
            actualizarOpcionesTamano(cmbTamano, (String) cmbProducto.getSelectedItem());

            JComboBox<String> cmbSabor = new JComboBox<>();
            actualizarOpcionesSabor(cmbSabor, (String) cmbProducto.getSelectedItem());

            // Cuando cambie el producto, actualizamos tamaño y sabor
            cmbProducto.addItemListener(ev -> {
                if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                    String prodSel = (String) ev.getItem();
                    actualizarOpcionesTamano(cmbTamano, prodSel);
                    actualizarOpcionesSabor(cmbSabor, prodSel);
                }
            });

            JTextField txtNivel = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
            panel.add(new JLabel("Producto:"));
            panel.add(cmbProducto);
            panel.add(new JLabel("Cantidad:"));
            panel.add(spCant);
            panel.add(new JLabel("Tamaño/Pzas:"));
            panel.add(cmbTamano);
            panel.add(new JLabel("Sabor:"));
            panel.add(cmbSabor);
            panel.add(new JLabel("Nivel:"));
            panel.add(txtNivel);

            int res = JOptionPane.showConfirmDialog(
                    this, panel, "Agregar línea al pedido",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (res == JOptionPane.OK_OPTION) {
                String mesa    = txtMesa.getText().trim();
                String cliente = txtCliente.getText().trim();
                String mesero  = txtMesero.getText().trim();

                String producto = (String) cmbProducto.getSelectedItem();
                String tamano   = (String) cmbTamano.getSelectedItem();
                int cantidad    = (int) spCant.getValue();
                double precioUnit = MENU_PRECIOS.getOrDefault(producto, 0.0);
                double importe = cantidad * precioUnit;
                String sabor = (String) cmbSabor.getSelectedItem();

                modeloTabla.addRow(new Object[]{
                        mesa,
                        cliente,
                        mesero,
                        cantidad,
                        producto,
                        tamano,
                        sabor,
                        txtNivel.getText().trim(),
                        String.format("%.2f", precioUnit),
                        String.format("%.2f", importe)
                });

                recalcularTotales();
            }
        });

        // ELIMINAR LÍNEA
        btnEliminar.addActionListener(e -> {
            int fila = tablaPedido.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una línea para eliminar");
                return;
            }
            modeloTabla.removeRow(fila);
            recalcularTotales();
        });

        // GUARDAR / REGISTRAR PEDIDO (no limpia la tabla)
        btnGuardar.addActionListener(e -> {
            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay productos en el pedido");
                return;
            }

            if (txtMesa.getText().trim().isEmpty() || txtCliente.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Captura al menos la mesa y el nombre del cliente.",
                        "Datos faltantes",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String folio = "PED-" + System.currentTimeMillis();

            String sql = "INSERT INTO pedidos " +
                    "(folio, mesa, clave, cliente, nombre_producto, tamano, sabor, cantidad, id_empleado, total, forma_pago, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql);
                 PreparedStatement psInv = con.prepareStatement(
                         "UPDATE productos SET inventario = GREATEST(inventario - ?, 0) WHERE nombre = ?"
                 )) {

                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    String mesa    = modeloTabla.getValueAt(i, 0).toString();
                    String cliente = modeloTabla.getValueAt(i, 1).toString();
                    String mesero  = modeloTabla.getValueAt(i, 2).toString();
                    int cantidad   = Integer.parseInt(modeloTabla.getValueAt(i, 3).toString());
                    String producto = modeloTabla.getValueAt(i, 4).toString();
                    String tamano   = modeloTabla.getValueAt(i, 5).toString();
                    String sabor    = modeloTabla.getValueAt(i, 6).toString();
                    double importe  = Double.parseDouble(modeloTabla.getValueAt(i, 9).toString());

                    // === INSERT PEDIDO ===
                    ps.setString(1, folio);
                    ps.setString(2, mesa);
                    ps.setString(3, ""); // clave interna (no usada aún)
                    ps.setString(4, cliente);
                    ps.setString(5, producto);
                    ps.setString(6, tamano);
                    ps.setString(7, sabor);
                    ps.setInt(8, cantidad);

                    try {
                        int idEmpleado = Integer.parseInt(mesero);
                        ps.setInt(9, idEmpleado);
                    } catch (NumberFormatException exNum) {
                        ps.setNull(9, java.sql.Types.INTEGER);
                    }

                    ps.setDouble(10, importe);
                    ps.setString(11, "EFECTIVO");
                    ps.setString(12, "PENDIENTE");
                    ps.addBatch();

                    // === DESCONTAR INVENTARIO ===
                    int consumoUnitario = obtenerConsumoUnitario(producto, tamano);
                    int consumoTotal = cantidad * consumoUnitario;
                    String nombreInventario = obtenerNombreInventario(producto);

                    psInv.setInt(1, consumoTotal);
                    psInv.setString(2, nombreInventario);
                    psInv.addBatch();
                }

                ps.executeBatch();
                psInv.executeBatch();

                JOptionPane.showMessageDialog(this,
                        "Pedido registrado en la base de datos.\nFolio: " + folio +
                                "\n\nPuedes imprimir el ticket ahora y usar 'Limpiar pedido' para empezar otro.",
                        "Pedido guardado",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar pedido: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // LIMPIAR PEDIDO (solo UI, NO BD)
        btnLimpiar.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "¿Deseas limpiar toda la captura en pantalla?",
                    "Limpiar pedido",
                    JOptionPane.YES_NO_OPTION);

            if (r == JOptionPane.YES_OPTION) {
                limpiarFormulario();
            }
        });

        // CANCELAR PEDIDO (borra de la BD por mesa)
        btnCancelar.addActionListener(e -> {
            String mesa = txtMesa.getText().trim();

            if (mesa.isEmpty()) {
                int r = JOptionPane.showConfirmDialog(this,
                        "No hay mesa capturada. ¿Solo quieres limpiar la pantalla?",
                        "Cancelar pedido",
                        JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    limpiarFormulario();
                }
                return;
            }

            int r = JOptionPane.showConfirmDialog(this,
                    "Se cancelarán los pedidos PENDIENTES/EN PREPARACION de la mesa " + mesa +
                            " en la base de datos.\n\n¿Continuar?",
                    "Cancelar pedido",
                    JOptionPane.YES_NO_OPTION);

            if (r != JOptionPane.YES_OPTION) {
                return;
            }

            String sql = "DELETE FROM pedidos " +
                    "WHERE mesa = ? AND estado IN ('PENDIENTE','EN PREPARACION')";

            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, mesa);
                int eliminados = ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Pedidos cancelados para la mesa " + mesa +
                                ". Registros afectados: " + eliminados,
                        "Pedido cancelado",
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al cancelar pedido: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // IMPRIMIR TICKET POR MESA (selector de mesa)
        btnImprimir.addActionListener(e -> {
            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay productos capturados en el pedido.",
                        "Sin datos",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Set<String> mesas = new LinkedHashSet<>();

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                Object valMesa = modeloTabla.getValueAt(i, 0);
                if (valMesa != null) {
                    String mesa = valMesa.toString().trim();
                    if (!mesa.isEmpty()) {
                        mesas.add(mesa);
                    }
                }
            }

            if (mesas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró ninguna mesa en la tabla.",
                        "Sin mesas",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] opcionesMesas = mesas.toArray(new String[0]);

            String mesaSeleccionada = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecciona la mesa para imprimir el ticket:",
                    "Imprimir ticket de mesa",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcionesMesas,
                    opcionesMesas[0]
            );

            if (mesaSeleccionada != null) {
                imprimirTicketPorMesa(mesaSeleccionada);
            }
        });

        // CERRAR SESIÓN / VOLVER AL LOGIN
        btnCerrarSesion.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(
                    this,
                    "¿Cerrar sesión y volver al login?\n\nEl pedido en pantalla no se cancelará automáticamente.",
                    "Cerrar sesión",
                    JOptionPane.YES_NO_OPTION
            );

            if (r == JOptionPane.YES_OPTION) {
                dispose();
                new FrmLoginNuevo().setVisible(true);
            }
        });
    }

    private void limpiarFormulario() {
        modeloTabla.setRowCount(0);
        txtMesa.setText("");
        txtMesero.setText("");
        txtCliente.setText("");
        txtSubtotal.setText("0.00");
        txtImpuestos.setText("0.00");
        txtTotal.setText("0.00");
    }

    // ======= Tamaños según producto =======
    private void actualizarOpcionesTamano(JComboBox<String> cmbTamano, String producto) {
        cmbTamano.removeAllItems();

        if (PRODUCTOS_ALITAS.contains(producto)) {
            cmbTamano.addItem("Chico (13 pzs)");
            cmbTamano.addItem("Grande (18 pzs)");
        } else if (PRODUCTOS_BEBIDAS.contains(producto)) {
            cmbTamano.addItem("Chico (600 ml)");
            cmbTamano.addItem("Grande (1000 ml)");
        } else if (PRODUCTOS_POSTRES.contains(producto)) {
            cmbTamano.addItem("Único (1 porción)");
        } else {
            cmbTamano.addItem("Único");
        }
    }

    // ======= Sabores según producto =======
    private void actualizarOpcionesSabor(JComboBox<String> cmbSabor, String producto) {
        cmbSabor.removeAllItems();

        if (PRODUCTOS_ALITAS.contains(producto)) {
            cmbSabor.addItem("BBQ clásica");
            cmbSabor.addItem("Wings hunts");
            cmbSabor.addItem("Mango habanero");
        } else {
            cmbSabor.addItem("Sin sabor");
        }
    }

    // ======= Consumo unitario para inventario =======
    private int obtenerConsumoUnitario(String producto, String tamano) {
        if (PRODUCTOS_ALITAS.contains(producto)) {
            if (tamano != null && tamano.startsWith("Grande")) {
                return 18;
            } else {
                return 13;
            }
        }

        if (PRODUCTOS_BEBIDAS.contains(producto)) {
            if (tamano != null && tamano.startsWith("Grande")) {
                return 1000; // ml
            } else {
                return 600; // ml
            }
        }

        if (PRODUCTOS_POSTRES.contains(producto)) {
            return 1; // porción
        }

        return 1;
    }

    // ======= Mapeo nombre de menú -> nombre inventario =======
    private String obtenerNombreInventario(String productoMenu) {
        String p = productoMenu.toLowerCase();

        if (p.contains("boneless")) {
            return "Boneless";
        }
        if (p.contains("alitas")) {
            return "Alitas";
        }
        if (p.contains("wings pop extrem")) {
            return "Wings Pop Extrem";
        }
        if (p.contains("wings pop")) {
            return "Wings Pop";
        }
        if (p.contains("agua")) {
            return "Agua";
        }
        if (p.contains("refresco")) {
            return "Refresco";
        }
        if (p.contains("café") || p.contains("cafe")) {
            return "Café";
        }

        return productoMenu;
    }

    // ================= CÁLCULO AUTOMÁTICO =================
    private void recalcularTotales() {
        double subtotal = 0.0;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object impObj = modeloTabla.getValueAt(i, 9); // columna Importe
            if (impObj != null) {
                try {
                    double imp = Double.parseDouble(impObj.toString());
                    subtotal += imp;
                } catch (NumberFormatException ignored) {}
            }
        }

        double iva = subtotal * 0.16; // 16% IVA
        double total = subtotal + iva;

        txtSubtotal.setText(String.format("%.2f", subtotal));
        txtImpuestos.setText(String.format("%.2f", iva));
        txtTotal.setText(String.format("%.2f", total));
    }

    // ======== IMPRIMIR TICKET POR MESA =========
    private void imprimirTicketPorMesa(String mesaSeleccionada) {
        try {
            DefaultTableModel modeloTicket = new DefaultTableModel(
                    new String[]{"Producto", "Cantidad", "Importe"}, 0
            );

            int colMesa     = 0;
            int colCliente  = 1;
            int colMesero   = 2;
            int colCantidad = 3;
            int colProducto = 4;
            int colImporte  = 9;

            double subtotal = 0.0;
            String clienteMesa = "";
            String meseroMesa  = txtMesero.getText().trim();

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String mesaTabla = String.valueOf(modeloTabla.getValueAt(i, colMesa));

                if (mesaSeleccionada.equals(mesaTabla)) {
                    String producto = String.valueOf(modeloTabla.getValueAt(i, colProducto));
                    String cantStr  = String.valueOf(modeloTabla.getValueAt(i, colCantidad));
                    String impStr   = String.valueOf(modeloTabla.getValueAt(i, colImporte));
                    String clienteFila = String.valueOf(modeloTabla.getValueAt(i, colCliente));
                    String meseroFila  = String.valueOf(modeloTabla.getValueAt(i, colMesero));

                    clienteMesa = clienteFila;
                    meseroMesa  = meseroFila;

                    int cantidad = Integer.parseInt(cantStr);
                    double importe = Double.parseDouble(impStr);

                    subtotal += importe;

                    modeloTicket.addRow(new Object[]{
                            producto,
                            cantidad,
                            String.format("%.2f", importe)
                    });
                }
            }

            if (modeloTicket.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay consumo registrado para la mesa " + mesaSeleccionada,
                        "Sin datos",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            double iva = subtotal * 0.16;
            double total = subtotal + iva;

            String strSubtotal = String.format("$%.2f", subtotal);
            String strIva      = String.format("$%.2f", iva);
            String strTotal    = String.format("$%.2f", total);

            TicketPrintable ticket = new TicketPrintable(
                    "EssRexx_Wings",
                    "Av. Sor Juana Inés de la Cruz, Nezahualcóyotl",
                    "55-0000-0000",
                    mesaSeleccionada,
                    meseroMesa,
                    clienteMesa,
                    modeloTicket,
                    strSubtotal,
                    strIva,
                    strTotal
            );

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(ticket);

            if (job.printDialog()) {
                job.print();
            }

        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al imprimir el ticket:\n" + ex.getMessage(),
                    "Error de impresión",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al generar el ticket:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= PERMISOS POR ROL =================
    private void aplicarPermisos() {

        if ("MESERO".equalsIgnoreCase(rolUsuario)) {
            txtMesero.setText(nombreUsuario);
        }

        if ("COCINERO".equalsIgnoreCase(rolUsuario)) {
            btnAgregar.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnGuardar.setEnabled(false);
            btnLimpiar.setEnabled(false);
            btnImprimir.setEnabled(false);
            btnCancelar.setEnabled(false);
            btnCerrarSesion.setEnabled(false); // cocinero no debería entrar aquí, pero por si acaso
        }
    }

    // MAIN de prueba
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmPedidosNuevo().setVisible(true));
    }
}
