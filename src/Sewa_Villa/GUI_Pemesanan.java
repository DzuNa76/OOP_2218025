/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Sewa_Villa;

/**
 *
 * @author Dzuna
 */
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

public class GUI_Pemesanan extends javax.swing.JFrame {

    /**
     * Creates new form GUI_Pemesanan
     */
    
    String Nama1, Alamat1, kode1, TS1, Ls1;
    
    public GUI_Pemesanan() {
        initComponents();
        DefaultTableModel dataModel = (DefaultTableModel) table_data_pesan.getModel();
        int rowCount = dataModel.getRowCount();
        while (rowCount > 0) {
            dataModel.removeRow(rowCount -1);
            rowCount = dataModel.getRowCount();
        }
        tampil();
    }
    
    public Connection conn;
    
    public void koneksi() throws SQLException {
        try {
            conn = null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/oop_persewaan_villa?user=root&password=");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_Pemesanan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            Logger.getLogger(GUI_Pemesanan.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception es) {
            Logger.getLogger(GUI_Pemesanan.class.getName()).log(Level.SEVERE, null, es);
        }
    }

    public void tampil() {
        DefaultTableModel tabelhead = new DefaultTableModel();
        tabelhead.addColumn("Nama");
        tabelhead.addColumn("Alamat");
        tabelhead.addColumn("Kode Villa");
        tabelhead.addColumn("Tanggal Sewa");
        tabelhead.addColumn("Lama Sewa");
        tabelhead.addColumn("Ahir Sewa");
        try {
            koneksi();
            String sql = "SELECT * FROM tb_pemesanan";
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                tabelhead.addRow(new Object[]{res.getString(2), res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7),});
            }
            table_data_pesan.setModel(tabelhead);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "BELUM TERKONEKSI");
        }
    }

    public void refresh() {
        new GUI_Pemesanan().setVisible(true);
        this.setVisible(false);
    }

    public void insert() {
    String Nama = txtNama.getText();
    String Alamat = txtAlamat.getText();
    String kode = null;
    if (radiobtnKecil.isSelected()) {
        kode = Villa.villaKecil.getkodeVilla();
    } else if (radiobtnSedang.isSelected()) {
        kode = Villa.villaSedang.getkodeVilla();
    } else if (radiobtnBesar.isSelected()) {
        kode = Villa.villaBesar.getkodeVilla();
    } 
    String TS = txtTanggal.getText();
    int Ls = Integer.parseInt(txtLamaSewa.getText());
    // Parse the tanggal input to a LocalDate object
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate tanggalSewa = LocalDate.parse( TS, formatter);

    // Add lamaSewa to tanggalSewa
    LocalDate tanggalAkhir = tanggalSewa.plusDays(Ls);
    try {
        koneksi();
        Statement statement = conn.createStatement();
        statement.executeUpdate("INSERT INTO tb_pemesanan (nama, alamat, kode_villa, tanggal_sewa, lama_sewa, akhir_sewa)"
                + "VALUES('" + Nama + "','" + Alamat + "','" + kode + "','" + tanggalSewa + "','" + Ls + "','" + tanggalAkhir + "')");
        statement.close();
        JOptionPane.showMessageDialog(null, "Berhasil Memasukan Data!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Terjadi Kesalahan Input!");
    }
    refresh();
    }


    public void update() {
    String Nama = txtNama.getText();
    String Alamat = txtAlamat.getText();
    String kode = null;
    if (radiobtnKecil.isSelected()) {
        kode = Villa.villaKecil.getkodeVilla();
    } else if (radiobtnSedang.isSelected()) {
        kode = Villa.villaSedang.getkodeVilla();
    } else if (radiobtnBesar.isSelected()) {
        kode = Villa.villaBesar.getkodeVilla();
    } 
    String TS = txtTanggal.getText();
    int Ls = Integer.parseInt(txtLamaSewa.getText());

    // Parse the tanggal input to a LocalDate object
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate tanggalSewa;
    try {
        tanggalSewa = LocalDate.parse(TS, formatter);
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(null, "Format tanggal salah. Harusnya yyyy-MM-dd");
        return;
    }

    // Add lamaSewa to tanggalSewa
    LocalDate tanggalAkhir = tanggalSewa.plusDays(Ls);
    String NamaLama = Nama1;
    PreparedStatement myStmt = null;
    try {
        String sql = "UPDATE tb_pemesanan SET nama=?, alamat=?, kode_villa=?, tanggal_sewa=?, lama_sewa=?, akhir_sewa=? WHERE nama=?";
        myStmt = conn.prepareStatement(sql);
        myStmt.setString(1, Nama);
        myStmt.setString(2, Alamat);
        myStmt.setString(3, kode);
        myStmt.setString(4, tanggalSewa.toString());
        myStmt.setInt(5, Ls);
        myStmt.setString(6, tanggalAkhir.toString());
        myStmt.setString(7, NamaLama);

        myStmt.executeUpdate();
        JOptionPane.showMessageDialog(null, "Update Data Berhasil!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error : " + e);
    } finally {
        try {
            if (myStmt != null) {
                myStmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error : " + ex);
        }
    }
    refresh();
}



    public void delete() {
        int ok = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin akan menghapus data ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == 0) {
            try {
                String sql = "DELETE FROM tb_pemesanan WHERE nama='" + txtNama.getText() + "'";
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data Berhasil di hapus");
                clear();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Data gagal di hapus");
            }
        }
        refresh();
    }

    public void cari() {
        try {
            try ( Statement statement = conn.createStatement()) {
                String sql = "SELECT * FROM tb_pemesanan WHERE `nama`  LIKE '%" + txtCari.getText() + "%'";
                ResultSet rs = statement.executeQuery(sql); //menampilkan data dari sql query
                if (rs.next()) // .next() = scanner method
                {
                    txtNama.setText(rs.getString(2));
                    txtAlamat.setText(rs.getString(3));
                    String kode = rs.getString(4);
                    if (kode.equalsIgnoreCase(Villa.villaKecil.getkodeVilla())) {
                        radiobtnKecil.setSelected(true);
                    } else if (kode.equalsIgnoreCase(Villa.villaSedang.getkodeVilla())){
                        radiobtnSedang.setSelected(true);
                    } else {
                        radiobtnBesar.setSelected(true);
                    }
                    txtTanggal.setText(rs.getString(5));
                    txtLamaSewa.setText(rs.getString(6));
                } else {
                    JOptionPane.showMessageDialog(null, "Data yang Anda cari tidak ada");
                }
            }
        } catch (Exception ex) {
            System.out.println("Error." + ex);
        }
    }

    public void itempilih() {
        txtNama.setText(Nama1);
        txtAlamat.setText(Alamat1);
        txtTanggal.setText(TS1);
        txtLamaSewa.setText(Ls1);
        if (kode1.equalsIgnoreCase(Villa.villaKecil.getkodeVilla())) {
            radiobtnKecil.setSelected(true);
        } else if (kode1.equalsIgnoreCase(Villa.villaSedang.getkodeVilla())){
            radiobtnSedang.setSelected(true);
        } else {
            radiobtnBesar.setSelected(true);
        }
    }
    
    public void clear() {
        txtCari.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtLamaSewa.setText("");
        txtTanggal.setText("");
        buttonGroup1.clearSelection();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        radiobtnKecil = new javax.swing.JRadioButton();
        radiobtnSedang = new javax.swing.JRadioButton();
        radiobtnBesar = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        btnSewa = new javax.swing.JButton();
        txtTanggal = new javax.swing.JTextField();
        txtAlamat = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtLamaSewa = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_pesan = new javax.swing.JTable();
        btnClose = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        btnUbah = new javax.swing.JButton();
        btnPembayaran = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel1.setText("Nama");

        jLabel2.setText("Alamat");

        jLabel3.setText("Tipe Villa");

        buttonGroup1.add(radiobtnKecil);
        radiobtnKecil.setText("Villa Kecil");
        radiobtnKecil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobtnKecilActionPerformed(evt);
            }
        });

        buttonGroup1.add(radiobtnSedang);
        radiobtnSedang.setText("Villa Sedang");
        radiobtnSedang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobtnSedangActionPerformed(evt);
            }
        });

        buttonGroup1.add(radiobtnBesar);
        radiobtnBesar.setText("Villa Besar");

        jLabel4.setText("Tanggal Sewa");

        btnSewa.setText("Sewa");
        btnSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSewaActionPerformed(evt);
            }
        });

        txtAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAlamatActionPerformed(evt);
            }
        });

        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });

        jLabel5.setText("Pesan Villa");

        jLabel6.setText("DD-MM-YYYY");

        jLabel7.setText("Lama Sewa");

        table_data_pesan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nama", "Alamat", "Kode Villa", "Tanggal Sewa", "Lama Sewa", "Akhir Sewa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(table_data_pesan);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        btnUbah.setText("Update");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        btnPembayaran.setText("Form Pembayaran");
        btnPembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPembayaranActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnPembayaran)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel7))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(btnSewa))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(radiobtnKecil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(radiobtnBesar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(radiobtnSedang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtNama)
                                        .addComponent(txtAlamat)
                                        .addComponent(txtTanggal)
                                        .addComponent(txtLamaSewa)))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBatal)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus)
                                .addGap(18, 18, 18)
                                .addComponent(btnClose)
                                .addGap(18, 18, 18)
                                .addComponent(btnUbah))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCari)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel5)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCari)
                            .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(radiobtnKecil))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radiobtnSedang)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radiobtnBesar)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addComponent(jLabel6))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtLamaSewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose)
                    .addComponent(btnBatal)
                    .addComponent(btnHapus)
                    .addComponent(btnUbah))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSewa)
                    .addComponent(btnPembayaran))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void txtAlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAlamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAlamatActionPerformed

    private void radiobtnSedangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobtnSedangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radiobtnSedangActionPerformed

    private void btnSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSewaActionPerformed
        JOptionPane.showMessageDialog(null, "Data Telah Ditambahkan");
        DefaultTableModel dataModel = (DefaultTableModel) table_data_pesan.getModel();
        List list = new ArrayList<>();
        table_data_pesan.setAutoCreateColumnsFromModel(true);
        
        String kode = null;
        Villa villa = null;
        
        Pemesanan pesan = new Pembayaran(); //Upcasting
        pesan.setNama(txtNama.getText());
        
        Pembayaran pesan1 = (Pembayaran) pesan; // downcasting
        pesan1.setAlamat(txtAlamat.getText());
        
        String tanggal = txtTanggal.getText();
        int lamaSewa = Integer.parseInt(txtLamaSewa.getText());
        
        // Parse the tanggal input to a LocalDate object
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate tanggalSewa = LocalDate.parse( tanggal, formatter);

        // Add lamaSewa to tanggalSewa
        LocalDate tanggalAkhir = tanggalSewa.plusDays(lamaSewa);
        
        if (radiobtnKecil.isSelected()) {
            kode = Villa.villaKecil.getkodeVilla();
        } else if (radiobtnSedang.isSelected()) {
            kode = villa.villaSedang.getkodeVilla();
        } else if (radiobtnBesar.isSelected()) {
            kode = villa.villaBesar.getkodeVilla();
        } 
        
        list.add(pesan.getNama());
        list.add(pesan.getAlamat());
        list.add(kode);
        list.add(tanggalSewa.format(formatter));
        list.add(lamaSewa);
        list.add(tanggalAkhir.format(formatter));       
        dataModel.addRow(list.toArray());
        insert();
        clear();
    }//GEN-LAST:event_btnSewaActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        clear();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        DefaultTableModel dataModel = (DefaultTableModel) table_data_pesan.getModel();
        int rowCount = dataModel.getRowCount();
        while (rowCount > 0) {
            dataModel.removeRow(rowCount -1);
            rowCount = dataModel.getRowCount();}
        delete();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void radiobtnKecilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobtnKecilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radiobtnKecilActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        update();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        cari();
    }//GEN-LAST:event_btnCariActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        int tabel = table_data_pesan.getSelectedRow();
        Nama1 = table_data_pesan.getValueAt(tabel, 0).toString();
        Alamat1 = table_data_pesan.getValueAt(tabel, 1).toString();
        kode1 = table_data_pesan.getValueAt(tabel, 2).toString();
        TS1 = table_data_pesan.getValueAt(tabel, 3).toString();
        Ls1 = table_data_pesan.getValueAt(tabel, 4).toString();
        itempilih();
    }//GEN-LAST:event_formMouseClicked

    private void btnPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPembayaranActionPerformed
        new GUI_Pembayaran().setVisible(true);
    }//GEN-LAST:event_btnPembayaranActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI_Pemesanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI_Pemesanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI_Pemesanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI_Pemesanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI_Pemesanan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnPembayaran;
    private javax.swing.JButton btnSewa;
    private javax.swing.JButton btnUbah;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton radiobtnBesar;
    private javax.swing.JRadioButton radiobtnKecil;
    private javax.swing.JRadioButton radiobtnSedang;
    private javax.swing.JTable table_data_pesan;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtLamaSewa;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables
}
