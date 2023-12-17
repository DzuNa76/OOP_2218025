/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Sewa_Villa;

/**
 *
 * @author DzulF
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

public class GUI_Pembayaran extends javax.swing.JFrame {

    /**
     * Creates new form GUI_Pembayaran
     */
    
    String Nama1, Alamat1, kode1, Lama1, Total1, Bayar1, Kembalian1, Durasi1, Status1;
    public GUI_Pembayaran() {
        initComponents();       
        DefaultTableModel dataModel = (DefaultTableModel) table_data_pembayaran.getModel();
        int rowCount = dataModel.getRowCount();
        while (rowCount > 0){
            dataModel.removeRow(rowCount - 1);// menghapus baris terakhir
            rowCount = dataModel.getRowCount();//memperbarui nilai setelah dihapus
        }
        tampil();
        tampil_nama();
    }
    
    public Connection conn;

    public void koneksi() throws SQLException {
        try {
            conn = null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/oop_persewaan_villa?user=root&password=");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_Pembayaran.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            Logger.getLogger(GUI_Pembayaran.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception es) {
            Logger.getLogger(GUI_Pembayaran.class.getName()).log(Level.SEVERE, null, es);
        }
    }
    
    public void tampil() {
        DefaultTableModel tabelhead = new DefaultTableModel();
        tabelhead.addColumn("Nama");
        tabelhead.addColumn("Alamat");
        tabelhead.addColumn("Kode");
        tabelhead.addColumn("Lama");
        tabelhead.addColumn("Total");
        tabelhead.addColumn("Pembayaran");
        tabelhead.addColumn("Kembalian");
        tabelhead.addColumn("Status");
        try {
            koneksi();
            String sql = "SELECT * FROM tb_pembayaran";
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                tabelhead.addRow(new Object[]{res.getString(2), res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7), res.getString(8),res.getString(9),});
            }
            table_data_pembayaran.setModel(tabelhead);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "BELUM TERKONEKSI");
        }
    }
    
    public void tampil_nama() {
        try {
            koneksi();
            String sql = "SELECT nama FROM tb_pemesanan order by nama asc";
            Statement stt = conn.createStatement();
            ResultSet res = stt.executeQuery(sql);
            while (res.next()) {
                Object[] ob = new Object[3];
                ob[0] = res.getString(1);
                cmbNama.addItem((String) ob[0]);
            }
            cmbNama.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    JComboBox comboBox = (JComboBox) event.getSource();
                    Object selected = comboBox.getSelectedItem();
                    try {
                        koneksi();
                        String sql = "SELECT * FROM tb_pemesanan WHERE nama = ?";
                        PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setString(1, selected.toString());
                        ResultSet rs = pst.executeQuery();
                        if (rs.next()) {
                            txtAlamat.setText(rs.getString("alamat"));
                            String kode = rs.getString("kode_villa");
                            if (kode.equalsIgnoreCase(Villa.villaKecil.getkodeVilla())) {
                                radiobtnKecil.setSelected(true);
                            } else if (kode.equalsIgnoreCase(Villa.villaSedang.getkodeVilla())){
                                radiobtnSedang.setSelected(true);
                            } else {
                                radiobtnBesar.setSelected(true);
                            }
                            txtDurasi.setText(rs.getString("lama_sewa"));
                        }
                        rs.close();
                        pst.close();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
            res.close();
            stt.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void refresh() {
        new GUI_Pembayaran().setVisible(true);
        this.setVisible(false);
    }

public void insert() {
    try {
        koneksi();
        Statement statement = conn.createStatement();

        // Ambil nilai dari inputan
        String nama = (String) cmbNama.getSelectedItem();
        String alamat = txtAlamat.getText();
        int durasi = Integer.parseInt(txtDurasi.getText());
        double bayar = Double.parseDouble(txtBayar.getText());

        // Inisialisasi objek Villa dan harga
        Villa villa = null;
        double harga = 0;

        // Tentukan tipe villa berdasarkan seleksi radio button
        if (radiobtnKecil.isSelected()) {
            villa = Villa.villaKecil;
        } else if (radiobtnSedang.isSelected()) {
            villa = Villa.villaSedang;
        } else if (radiobtnBesar.isSelected()) {
            villa = Villa.villaBesar;
        } else {
            JOptionPane.showMessageDialog(null, "Pilih Villa Terlebih Dahulu");
            return;
        }

        // Dapatkan harga dari villa yang dipilih
        harga = villa.hargaSewaPerMalam;

        // Hitung total biaya dengan benar
        double totalBiaya = durasi * harga;
        double kembalian = bayar - totalBiaya;

        // Tentukan status pembayaran
        String statusPembayaran = (kembalian >= 0) ? "Lunas" : "Belum Lunas";

        // Menyisipkan data ke dalam tabel pembayaran
        statement.executeUpdate("INSERT INTO tb_pembayaran(nama, alamat, kode_villa, lama_sewa, total, pembayaran, kembalian, status)"
                + " VALUES('" + nama + "','" + alamat + "','" + villa.getkodeVilla() + "','" + durasi + "','"
                + totalBiaya + "','" + bayar + "','" +kembalian + "','" + statusPembayaran + "')");

        // Menutup statement
        statement.close();

        // Menampilkan pesan berhasil
        JOptionPane.showMessageDialog(null, "Data Telah Ditambahkan");
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Masukkan angka yang valid untuk durasi dan pembayaran");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Terjadi Kesalahan Input: " + e.getMessage());
    }

    // Refresh tabel
    refresh();
}



    public void update() {
    String Nama = (String) cmbNama.getSelectedItem();
    String Alamat = txtAlamat.getText();
    String Pembayaran = txtBayar.getText();
    Villa villa = null;
    String kode = null;
    double harga = 0;
    
    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost/oop_persewaan_villa?user=root&password=");
        
        if (radiobtnKecil.isSelected()) {
            villa = Villa.villaKecil;
            harga = villa.hargaSewaPerMalam;
        } else if (radiobtnSedang.isSelected()) {
            villa = Villa.villaSedang;
            harga = villa.hargaSewaPerMalam;
        } else if (radiobtnBesar.isSelected()) {
            villa = Villa.villaBesar;
            harga = villa.hargaSewaPerMalam;
        }

        String Lama = txtDurasi.getText();
        //objek pembayaran
        Pembayaran pbyr = new Pembayaran();
        double totalBiaya, bayar, kembalian;
        pbyr.durasi = Double.parseDouble(txtDurasi.getText());
        pbyr.harga = harga;
        bayar = Double.parseDouble(txtBayar.getText());
        totalBiaya = pbyr.hargaSewa();
        kembalian = bayar - totalBiaya;
        //status pembayaran 
        String statusPembayaran; 
        if (kembalian >= 0) {
            statusPembayaran = "Lunas";
        } else {
            statusPembayaran = "Belum Lunas";
        }

        String nama_lama = Nama1;
        String kode_lama = kode1;

        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate("UPDATE tb_pembayaran SET nama='" + Nama + "', kode_villa='" + villa.getkodeVilla() + "'"
                    + ",lama_sewa='" + Lama + "',total='" + totalBiaya + "',kembalian='" + kembalian + "',status='" + statusPembayaran + "' WHERE nama ='" + nama_lama + "' AND kode_villa='" + kode_lama + "'");
            statement.close();
            JOptionPane.showMessageDialog(null, "Update Data Nilai!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan dalam eksekusi pernyataan SQL: " + e.getMessage());
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Koneksi ke database gagal: " + e.getMessage());
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Kesalahan penutupan koneksi: " + e.getMessage());
            }
        }
    }
    refresh();
}

    public void delete() {
        int ok = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin akan menghapus data ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == 0) {
            try {
                String sql = "DELETE FROM tb_pembayaran WHERE Nama=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, cmbNama.getSelectedItem().toString());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data Berhasil dihapus");
                clear();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Data gagal dihapus: " + e.getMessage());
            }
        }
        refresh();
    }

    public void cari() {
        try {
            try ( Statement statement = conn.createStatement()) {
                String sql = "SELECT * FROM tb_pembayaran WHERE `Nama` LIKE '%" + txtCari.getText() + "%'";
                ResultSet rs = statement.executeQuery(sql);
                //menampilkan data dari sql query
                if (rs.next()) // .next() = scanner method
                {
                    cmbNama.setSelectedItem(rs.getString(2));
                    txtAlamat.setText(rs.getString(3));
                    String kode = rs.getString(4);
                    if (kode.equalsIgnoreCase(Villa.villaKecil.getkodeVilla())) {
                        radiobtnKecil.setSelected(true);
                    } else if (kode.equalsIgnoreCase(Villa.villaSedang.getkodeVilla())){
                        radiobtnSedang.setSelected(true);
                    } else {
                        radiobtnBesar.setSelected(true);
                    }
                    txtDurasi.setText(rs.getString(5));
                    txtBayar.setText(rs.getString(7));
                } else {
                    JOptionPane.showMessageDialog(null, "Data yang Anda cari tidak ada");
                }
            }
        } catch (Exception ex) {
            System.out.println("Error." + ex);
        }
    }

    void itempilih() {
        cmbNama.setSelectedItem(Nama1);
        txtAlamat.setText(Alamat1);
        txtBayar.setText(Bayar1);
        txtDurasi.setText(Durasi1);
        if (kode1.equalsIgnoreCase(Villa.villaKecil.getkodeVilla())) {
            radiobtnKecil.setSelected(true);
        } else if (kode1.equalsIgnoreCase(Villa.villaSedang.getkodeVilla())){
            radiobtnSedang.setSelected(true);
        } else {
            radiobtnBesar.setSelected(true);
        }
    }

    
    public void clear(){
        txtCari.setText("");
        txtAlamat.setText("");
        txtBayar.setText("");
        txtDurasi.setText("");
        cmbNama.setSelectedIndex(0);
        memoBayar.setText("");
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
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        radiobtnKecil = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        radiobtnSedang = new javax.swing.JRadioButton();
        radiobtnBesar = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        txtDurasi = new javax.swing.JTextField();
        txtBayar = new javax.swing.JTextField();
        btnBayar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        memoBayar = new javax.swing.JTextArea();
        btnSewa = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_pembayaran = new javax.swing.JTable();
        btnSimpan = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        cmbNama = new javax.swing.JComboBox<>();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        btnNama = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel3.setText("Alamat");

        jLabel1.setText("Tipe Villa");

        buttonGroup1.add(radiobtnKecil);
        radiobtnKecil.setText("Villa Kecil");
        radiobtnKecil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobtnKecilActionPerformed(evt);
            }
        });

        jLabel4.setText("Bayar");

        buttonGroup1.add(radiobtnSedang);
        radiobtnSedang.setText("Villa Sedang");
        radiobtnSedang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobtnSedangActionPerformed(evt);
            }
        });

        buttonGroup1.add(radiobtnBesar);
        radiobtnBesar.setText("Villa Besar");

        jLabel5.setText("Lama Sewa");

        txtBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBayarActionPerformed(evt);
            }
        });

        btnBayar.setText("Bayar");
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBayarActionPerformed(evt);
            }
        });

        memoBayar.setColumns(20);
        memoBayar.setRows(5);
        jScrollPane1.setViewportView(memoBayar);

        btnSewa.setText("Sewa");
        btnSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSewaActionPerformed(evt);
            }
        });

        jLabel6.setText("Pembayaran");

        table_data_pembayaran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Nama", "Alamat", "Kode Villa", "Lama Sewa", "Total", "Pembayaran", "Kembalian", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(table_data_pembayaran);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

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

        cmbNama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama" }));
        cmbNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbNamaActionPerformed(evt);
            }
        });

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnNama.setText("Nama");
        btnNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNamaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(373, 373, 373)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCari))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addComponent(btnSewa, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDurasi)
                                    .addComponent(txtBayar)
                                    .addComponent(btnBayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel3))
                                        .addGap(37, 37, 37))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(btnNama)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(radiobtnSedang, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                                    .addComponent(radiobtnKecil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(radiobtnBesar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtAlamat)
                                    .addComponent(cmbNama, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSimpan)
                                .addGap(18, 18, 18)
                                .addComponent(btnBatal)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus)
                                .addGap(18, 18, 18)
                                .addComponent(btnClose)
                                .addGap(18, 18, 18)
                                .addComponent(btnUpdate)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCari)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNama))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(radiobtnKecil))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radiobtnSedang)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radiobtnBesar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtDurasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSewa))
                    .addComponent(jScrollPane1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnBayar)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnBatal)
                    .addComponent(btnHapus)
                    .addComponent(btnClose)
                    .addComponent(btnUpdate))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void radiobtnSedangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobtnSedangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radiobtnSedangActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        Villa villa = null;
        double harga = 0;
        String tipeVilla = "";
        String nama = (String) cmbNama.getSelectedItem();
        String alamat = txtAlamat.getText();
        
        //objek pembayaran
        Pembayaran pbyr = new Pembayaran();
        
        if (radiobtnKecil.isSelected()) {
            tipeVilla = "VK";
            villa = Villa.villaKecil;
            harga = villa.hargaSewaPerMalam;
        } else if (radiobtnSedang.isSelected()) {
            tipeVilla = "VS";
            villa = Villa.villaSedang;
            harga = villa.hargaSewaPerMalam;
        } else if (radiobtnBesar.isSelected()) {
            tipeVilla = "VB";
            villa = Villa.villaBesar;
            harga = villa.hargaSewaPerMalam;
        }

        // Hitung total biaya dengan benar
        double totalBiaya, bayar, kembalian;
        
        try { // Blok try-catch untuk menangani eksepsi
            pbyr.durasi = Double.parseDouble(txtDurasi.getText()); // Mengambil teks dari txtDurasi dan mengubahnya menjadi double
            pbyr.harga = harga; // Menetapkan nilai harga ke variabel harga di objek pbyr
            bayar = Double.parseDouble(txtBayar.getText()); // Mengambil teks dari txtBayar dan mengubahnya menjadi double
            totalBiaya = pbyr.hargaSewa(); // Menghitung total biaya sewa dengan memanggil metode hargaSewa() pada objek pbyr
            kembalian = bayar - totalBiaya; // Menghitung total kembalian dengan mengurangi totalBiaya dari bayar
        } catch (NumberFormatException e) { // Jika terjadi NumberFormatException (misalnya, jika teks dari txtDurasi atau txtBayar tidak dapat diubah menjadi double)
            memoBayar.setText("Masukkan durasi sewa dan jumlah pembayaran yang valid."); // Menampilkan pesan kesalahan ke pengguna
            throw e; // Melempar eksepsi lebih lanjut
        }
        
        String statusPembayaran;//status pembayaran  
        if (kembalian >= 0) {
            statusPembayaran = "Lunas";
        } else {
            statusPembayaran = "Belum Lunas";
        }

        // Menampilkan hasil pembayaran
        memoBayar.setText("===== Detail Pemesanan =====\n");
        memoBayar.append("Nama: " + nama + "\n");
        memoBayar.append("Alamat: " + alamat + "\n");
        memoBayar.append("Tipe Villa: " + tipeVilla + "\n");
        memoBayar.append("Lama Sewa: " + pbyr.durasi + " malam\n");
        memoBayar.append("Total Biaya: Rp " + pbyr.formatBiaya(totalBiaya) + "\n");// implement interface 
        memoBayar.append("Dibayar: Rp " + pbyr.formatBiaya(bayar) + "\n");// implement interface 
        memoBayar.append("Kembalian: Rp " + pbyr.formatBiaya(kembalian) + "\n");// implement interface 
        memoBayar.append("==============================\n");
    }//GEN-LAST:event_btnBayarActionPerformed
 
    private void btnSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSewaActionPerformed
       
        // Dapatkan nilai dari inputan
        String nama = (String) cmbNama.getSelectedItem();
        String alamat = txtAlamat.getText();
        Pembayaran pbyr = new Pembayaran();
        
        // Dapatkan tipe villa yang dipilih berdasarkan tombol radio
        String tipevila = "";
        Villa villa = null;
        double harga;
        
        if (radiobtnKecil.isSelected()) {
            tipevila = "VK";
            villa = Villa.villaKecil;
            harga = villa.hargaSewaPerMalam;
        } else if (radiobtnSedang.isSelected()) {
            tipevila = "VS";
            villa = Villa.villaSedang;
            harga = villa.hargaSewaPerMalam;
        } else if (radiobtnBesar.isSelected()) {
            tipevila = "VB";
            villa = Villa.villaBesar;
            harga = villa.hargaSewaPerMalam;
        } else {
                memoBayar.setText("Pilih tipe villa");
                return;
        }    
        
        double totalBiaya; // Deklarasi variabel totalBiaya dengan tipe data double

        try { // Blok try-catch untuk menangani eksepsi
            pbyr.durasi = Double.parseDouble(txtDurasi.getText()); // Mengambil teks dari txtDurasi dan mengubahnya menjadi double
            pbyr.harga = harga; // Menetapkan nilai harga ke variabel harga di objek pbyr
            totalBiaya = pbyr.hargaSewa(); // Menghitung total biaya sewa dengan memanggil metode hargaSewa() pada objek pbyr
        } catch (NumberFormatException e) { // Jika terjadi NumberFormatException (misalnya, jika teks dari txtDurasi tidak dapat diubah menjadi double)
            memoBayar.setText("Masukkan durasi sewa dan jumlah pembayaran yang valid."); // Menampilkan pesan kesalahan ke pengguna
            throw e; // Melempar eksepsi lebih lanjut
        }
        
        if (villa == null) {
            memoBayar.setText("Pilih tipe villa terlebih dahulu.");
        } else {
            
            // Tampilkan detailnya di memoBayar
            memoBayar.setText("===== Detail Pemesanan =====\n");
            memoBayar.append("Nama: " + nama + "\n");
            memoBayar.append("Alamat: " + alamat + "\n");
            memoBayar.append("Tipe Villa: " + tipevila + "\n");
            memoBayar.append("Lama Sewa: " + pbyr.durasi + " malam\n");
            memoBayar.append("Total Biaya: Rp " + pbyr.formatBiaya(totalBiaya) + "\n");// implementasi interfacce
            memoBayar.append("Pemesanan berhasil.\n");
            memoBayar.append("==============================\n");           
        }
    }//GEN-LAST:event_btnSewaActionPerformed

    private void txtBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBayarActionPerformed

    private void radiobtnKecilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobtnKecilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radiobtnKecilActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        Villa villa = null;
        double harga = 0;
        table_data_pembayaran.setAutoCreateColumnsFromModel(true);

        // mengambil model data dari tabel
        DefaultTableModel dataModel = (DefaultTableModel) table_data_pembayaran.getModel();
        //inisialisasi array
        List list = new ArrayList<>();
        //membuat objek pembayaran

        Pembayaran pbyr = new Pembayaran();
        pbyr.setNama((String) cmbNama.getSelectedItem());
        pbyr.setAlamat(txtAlamat.getText());

        try {
            int durasi = Integer.parseInt(txtDurasi.getText());
            double bayar = Double.parseDouble(txtBayar.getText());

            if (radiobtnKecil.isSelected()) {
                villa = Villa.villaKecil;
            } else if (radiobtnSedang.isSelected()) {
                villa = Villa.villaSedang;
            } else if (radiobtnBesar.isSelected()) {
                villa = Villa.villaBesar;
            } else {
                JOptionPane.showMessageDialog(null, "Pilih Villa Terlebih Dahulu");
                return;
            }
            harga = villa.hargaSewaPerMalam;
            // Hitung total biaya dengan benar
            double totalBiaya;
            pbyr.durasi = durasi;
            pbyr.harga = harga;            
            totalBiaya = pbyr.hargaSewa(); //menghitung total biaya sewa
            double kembalian = bayar - totalBiaya; //menghitung total kembalian jika ada

            pbyr.formatBiaya(totalBiaya);// implement interface 
            pbyr.formatBiaya(kembalian);// implement interface 

            String statusPembayaran;//status pembayaran  
            if (kembalian >= 0) {
                statusPembayaran = "Lunas";
            } else {
                statusPembayaran = "Belum Lunas";
            }
            //menambahkan data
            list.add(pbyr.getNama());
            list.add(pbyr.getAlamat());
            list.add(villa.getkodeVilla());
            list.add(durasi);
            list.add(totalBiaya);
            list.add(bayar);
            list.add(kembalian);
            list.add(statusPembayaran);

            //menambah baris baru
            dataModel.addRow(list.toArray());
            insert();
            //memanggil fungsi clear
            clear();

            // pesan data ditambahkan
            JOptionPane.showMessageDialog(null, "Data Telah Ditambahkan");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Masukkan angka yang valid untuk durasi dan pembayaran");
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        DefaultTableModel dataModel = (DefaultTableModel) table_data_pembayaran.getModel();
        int rowCount = dataModel.getRowCount();
        while (rowCount > 0){
            dataModel.removeRow(rowCount - 1);
            rowCount = dataModel.getRowCount();}
        delete();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        cari();
    }//GEN-LAST:event_btnCariActionPerformed

    private void btnNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNamaActionPerformed
        new GUI_Pemesanan().setVisible(true);
    }//GEN-LAST:event_btnNamaActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        int tabel = table_data_pembayaran.getSelectedRow(); //0
        Nama1 = table_data_pembayaran.getValueAt(tabel, 0).toString();
        Alamat1 = table_data_pembayaran.getValueAt(tabel, 1).toString();
        kode1 = table_data_pembayaran.getValueAt(tabel, 2).toString();
        Lama1 = table_data_pembayaran.getValueAt(tabel, 3).toString();
        Total1 = table_data_pembayaran.getValueAt(tabel, 4).toString();
        Bayar1 = table_data_pembayaran.getValueAt(tabel, 5).toString();
        Kembalian1 = table_data_pembayaran.getValueAt(tabel, 6).toString();
        Status1 = table_data_pembayaran.getValueAt(tabel, 7).toString();
        itempilih();

    }//GEN-LAST:event_formMouseClicked

    private void cmbNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbNamaActionPerformed

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
            java.util.logging.Logger.getLogger(GUI_Pembayaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI_Pembayaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI_Pembayaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI_Pembayaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI_Pembayaran().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnNama;
    private javax.swing.JButton btnSewa;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cmbNama;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea memoBayar;
    private javax.swing.JRadioButton radiobtnBesar;
    private javax.swing.JRadioButton radiobtnKecil;
    private javax.swing.JRadioButton radiobtnSedang;
    private javax.swing.JTable table_data_pembayaran;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtDurasi;
    // End of variables declaration//GEN-END:variables
}
