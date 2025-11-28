/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package srexwingsapp.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import srexwingsapp.conexion.Conexion;


/**
 *
 * @author enriquejaramillo
 */
public class frmPedidos extends javax.swing.JFrame {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    DefaultTableModel modelo;

    // info del usuario logueado
    private final String rolUsuario;
    private final String nombreUsuario;

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(frmPedidos.class.getName());

    // ========== CONSTRUCTOR QUE USA LOGIN ==========
    public frmPedidos(String rolUsuario, String nombreUsuario) {
        this.rolUsuario = rolUsuario;
        this.nombreUsuario = nombreUsuario;

        initComponents();
        configurarVentana();
        cargarTablaPedidos();
    }

    // 🔹 Constructor vacío (por si lo ejecutas directo desde NetBeans)
    public frmPedidos() {
        this.rolUsuario = "ADMIN";
        this.nombreUsuario = "Admin (prueba)";
        initComponents();
        configurarVentana();
        cargarTablaPedidos();
    }

    // ========== CONFIGURAR VENTANA Y PERMISOS ==========
    private void configurarVentana() {
        setTitle("EsRexx_Wings - Pedidos (" + rolUsuario + " - " + nombreUsuario + ")");
        setLocationRelativeTo(null);
        setResizable(false);

        // Tamaño visual de los campos
        txtFolio.setColumns(8);
        txtClave.setColumns(8);
        txtNombre.setColumns(10);
        txtCantidad.setColumns(4);

        // Permisos según rol
        configurarPermisosPorRol();
    }

    private void configurarPermisosPorRol() {
        switch (rolUsuario) {
            case "ADMIN":
                // Admin puede todo
                btnAgregar.setEnabled(true);
                btnActualizar.setEnabled(true);
                btnEliminar.setEnabled(true);
                txtFolio.setEditable(true);
                txtClave.setEditable(true);
                txtNombre.setEditable(true);
                txtCantidad.setEditable(true);
                break;

            case "MESERO":
                // Mesero solo agrega
                btnAgregar.setEnabled(true);
                btnActualizar.setEnabled(false);
                btnEliminar.setEnabled(false);
                txtFolio.setEditable(true);
                txtClave.setEditable(true);
                txtNombre.setEditable(true);
                txtCantidad.setEditable(true);
                break;

            case "COCINERO":
                // Cocinero solo ve
                btnAgregar.setEnabled(false);
                btnActualizar.setEnabled(false);
                btnEliminar.setEnabled(false);
                txtFolio.setEditable(false);
                txtClave.setEditable(false);
                txtNombre.setEditable(false);
                txtCantidad.setEditable(false);
                break;

            default:
                // Por si acaso
                btnAgregar.setEnabled(false);
                btnActualizar.setEnabled(false);
                btnEliminar.setEnabled(false);
        }
    }






    // ================= CONEXIÓN ====================
    private Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = Conexion.getConexion();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener conexión: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return con;
    }

    // ================= TABLA =======================
    private void cargarTablaPedidos() {
        String[] titulos = {"ID", "Folio", "Clave", "Nombre", "Cantidad"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);

        String sql = "SELECT id_pedido, folio, clave, nombre, cantidad FROM pedidos";

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id_pedido"),
                        rs.getString("folio"),
                        rs.getString("clave"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad")
                };
                modelo.addRow(fila);
            }

            tablaPedidos.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar pedidos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtFolio.setText("");
        txtClave.setText("");
        txtNombre.setText("");
        txtCantidad.setText("");
        tablaPedidos.clearSelection();
    }

    // ================= AGREGAR ======================
    private void agregarPedido() {
    if (!"ADMIN".equalsIgnoreCase(this.rolUsuario)
            && !"MESERO".equalsIgnoreCase(this.rolUsuario)) {
        JOptionPane.showMessageDialog(this,
                "No tienes permiso para agregar pedidos.");
        return;
    }

    String folio = txtFolio.getText().trim();
    String clave = txtClave.getText().trim();
    String nombre = txtNombre.getText().trim();
    String cantidadStr = txtCantidad.getText().trim();

    if (folio.isEmpty() || clave.isEmpty() || nombre.isEmpty() || cantidadStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Completa todos los campos.");
        return;
    }

    int cantidad;
    try {
        cantidad = Integer.parseInt(cantidadStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Cantidad debe ser un número entero.");
        return;
    }

    String sql = "INSERT INTO pedidos (folio, clave, nombre, cantidad) VALUES (?, ?, ?, ?)";
    try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setString(1, folio);
        ps.setString(2, clave);
        ps.setString(3, nombre);
        ps.setInt(4, cantidad);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Pedido agregado correctamente.");
        cargarTablaPedidos();
        limpiarCampos();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al agregar pedido: " + e.getMessage());
    }
}


    // ================= ACTUALIZAR ===================
    private void actualizarPedido() {
    if (!"ADMIN".equalsIgnoreCase(this.rolUsuario)) {
        JOptionPane.showMessageDialog(this,
                "Solo el administrador puede actualizar pedidos.");
        return;
    }

    int fila = tablaPedidos.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un pedido de la tabla.");
        return;
    }

    int id = Integer.parseInt(tablaPedidos.getValueAt(fila, 0).toString());

    String folio = txtFolio.getText().trim();
    String clave = txtClave.getText().trim();
    String nombre = txtNombre.getText().trim();
    String cantidadStr = txtCantidad.getText().trim();

    if (folio.isEmpty() || clave.isEmpty() || nombre.isEmpty() || cantidadStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Completa todos los campos.");
        return;
    }

    int cantidad;
    try {
        cantidad = Integer.parseInt(cantidadStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Cantidad debe ser un número entero.");
        return;
    }

    String sql = "UPDATE pedidos SET folio=?, clave=?, nombre=?, cantidad=? WHERE id_pedido=?";
    try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setString(1, folio);
        ps.setString(2, clave);
        ps.setString(3, nombre);
        ps.setInt(4, cantidad);
        ps.setInt(5, id);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Pedido actualizado.");
        cargarTablaPedidos();
        limpiarCampos();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al actualizar pedido: " + e.getMessage());
    }
}



    // ================= ELIMINAR =====================
    private void eliminarPedido() {
    if (!"ADMIN".equalsIgnoreCase(this.rolUsuario)
            && !"MESERO".equalsIgnoreCase(this.rolUsuario)) {
        JOptionPane.showMessageDialog(this,
                "No tienes permiso para eliminar pedidos.");
        return;
    }

    int fila = tablaPedidos.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un pedido.");
        return;
    }

    int id = Integer.parseInt(tablaPedidos.getValueAt(fila, 0).toString());

    int resp = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el pedido con ID " + id + "?",
            "Confirmación",
            JOptionPane.YES_NO_OPTION);

    if (resp != JOptionPane.YES_OPTION) return;

    String sql = "DELETE FROM pedidos WHERE id_pedido=?";
    try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, id);
        ps.executeUpdate();
        JOptionPane.showMessageDialog(this, "Pedido eliminado.");
        cargarTablaPedidos();
        limpiarCampos();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al eliminar pedido: " + e.getMessage());
    }
}
    



        






    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panelDatos = new javax.swing.JPanel();
        jLabelFolio = new javax.swing.JLabel();
        txtFolio = new javax.swing.JTextField();
        jLabelClave = new javax.swing.JLabel();
        txtClave = new javax.swing.JTextField();
        jLabelNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabelCantidad = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaPedidos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(900, 600));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelHeader.setBackground(new java.awt.Color(255, 140, 0));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel1.setText("EsRexx_Wings");

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(245, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(240, 240, 240))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        getContentPane().add(panelHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, -1));

        panelDatos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos del Pedido", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        panelDatos.setPreferredSize(new java.awt.Dimension(1000, 650));
        panelDatos.setRequestFocusEnabled(false);
        panelDatos.setLayout(null);

        jLabelFolio.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabelFolio.setText("Folio");
        panelDatos.add(jLabelFolio);
        jLabelFolio.setBounds(28, 25, 36, 17);
        panelDatos.add(txtFolio);
        txtFolio.setBounds(5, 48, 80, 20);

        jLabelClave.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabelClave.setText("Clave");
        panelDatos.add(jLabelClave);
        jLabelClave.setBounds(137, 25, 39, 17);

        txtClave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClaveActionPerformed(evt);
            }
        });
        panelDatos.add(txtClave);
        txtClave.setBounds(119, 48, 80, 23);

        jLabelNombre.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabelNombre.setText("Nombre");
        panelDatos.add(jLabelNombre);
        jLabelNombre.setBounds(410, 30, 60, 20);
        panelDatos.add(txtNombre);
        txtNombre.setBounds(400, 50, 80, 30);

        jLabelCantidad.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabelCantidad.setText("Cantidad");
        panelDatos.add(jLabelCantidad);
        jLabelCantidad.setBounds(520, 30, 64, 17);
        panelDatos.add(txtCantidad);
        txtCantidad.setBounds(510, 50, 90, 30);

        btnAgregar.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        panelDatos.add(btnAgregar);
        btnAgregar.setBounds(86, 137, 84, 24);

        btnActualizar.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        panelDatos.add(btnActualizar);
        btnActualizar.setBounds(188, 137, 97, 24);

        btnEliminar.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        panelDatos.add(btnEliminar);
        btnEliminar.setBounds(415, 137, 85, 24);

        btnLimpiar.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        panelDatos.add(btnLimpiar);
        btnLimpiar.setBounds(518, 137, 81, 24);

        getContentPane().add(panelDatos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 660, 180));

        panelTabla.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lista de Pedidos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        tablaPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaPedidos);

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 31, Short.MAX_VALUE))
        );

        getContentPane().add(panelTabla, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 660, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        agregarPedido();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarPedido();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarPedido();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
                                
    int fila = tablaPedidos.getSelectedRow();
    if (fila >= 0) {
        txtFolio.setText(tablaPedidos.getValueAt(fila, 1).toString());
        txtClave.setText(tablaPedidos.getValueAt(fila, 2).toString());
        txtNombre.setText(tablaPedidos.getValueAt(fila, 3).toString());
        txtCantidad.setText(tablaPedidos.getValueAt(fila, 4).toString());
    }






    }//GEN-LAST:event_formMouseClicked

    private void txtClaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClaveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new frmPedidos().setVisible(true));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelCantidad;
    private javax.swing.JLabel jLabelClave;
    private javax.swing.JLabel jLabelFolio;
    private javax.swing.JLabel jLabelNombre;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JTable tablaPedidos;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtClave;
    private javax.swing.JTextField txtFolio;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables

}
    
