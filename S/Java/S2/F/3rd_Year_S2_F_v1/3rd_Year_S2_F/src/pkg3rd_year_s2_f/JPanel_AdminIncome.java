/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package pkg3rd_year_s2_f;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class JPanel_AdminIncome extends javax.swing.JPanel {

    /**
     * Creates new form JPanel_AdminIncome
     */
    public JPanel_AdminIncome() {
        initComponents();
        loadIncome();
        calculateIncome();

    }

    public void loadIncome() {

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM tbl_income"
            );

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model
                    = (DefaultTableModel) jtbAdminIncome.getModel();

            model.setRowCount(0);

            while (rs.next()) {

                model.addRow(new Object[]{
                    rs.getInt("income_id"),
                    rs.getString("date"),
                    rs.getDouble("booking_income"),
                    rs.getDouble("room_income"),
                    rs.getDouble("total_income")
                });

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void calculateIncome() {

        try {

            Connection con = DBConnection.getConnection();

            double bookingIncome = 0;
            double roomIncome = 0;

            PreparedStatement pst1 = con.prepareStatement(
                    "SELECT IFNULL(SUM(deposit),0) FROM tbl_booking"
            );

            ResultSet rs1 = pst1.executeQuery();
            if (rs1.next()) {
                bookingIncome = rs1.getDouble(1);
            }

            PreparedStatement pst2 = con.prepareStatement(
                    "SELECT COUNT(*) * 50 FROM tbl_room WHERE status='Occupied'"
            );

            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                roomIncome = rs2.getDouble(1);
            }

            double total = bookingIncome + roomIncome;

            jtxtBookingIncome.setText(String.valueOf(bookingIncome));
            jtxtRoomIncome.setText(String.valueOf(roomIncome));
            jtxtIncomeTotal.setText(String.valueOf(total));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void saveIncome() {

        try {

            if (jtxtFromDate.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter date");
                return;
            }

            double booking = Double.parseDouble(jtxtBookingIncome.getText());
            double room = Double.parseDouble(jtxtRoomIncome.getText());
            double total = Double.parseDouble(jtxtIncomeTotal.getText());

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO tbl_income(date,booking_income,room_income,total_income) VALUES(?,?,?,?)"
            );

            pst.setString(1, jtxtFromDate.getText());
            pst.setDouble(2, booking);
            pst.setDouble(3, room);
            pst.setDouble(4, total);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Income Saved Successfully");

            loadIncome();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void autoUpdateIncome() {

        try {

            Connection con = DBConnection.getConnection();

            // 1. Booking income (20%)
            PreparedStatement pst1 = con.prepareStatement(
                    "SELECT SUM(deposit) FROM tbl_booking"
            );

            ResultSet rs1 = pst1.executeQuery();

            double bookingIncome = 0;

            if (rs1.next()) {
                bookingIncome = rs1.getDouble(1);
            }

            // 2. Room income (80%)
            PreparedStatement pst2 = con.prepareStatement(
                    "SELECT COUNT(*) FROM tbl_booking WHERE status='Checked-Out'"
            );

            ResultSet rs2 = pst2.executeQuery();

            int totalBookings = 0;

            if (rs2.next()) {
                totalBookings = rs2.getInt(1);
            }

            // get average room price
            PreparedStatement pst3 = con.prepareStatement(
                    "SELECT AVG(room_price) FROM tbl_room"
            );

            ResultSet rs3 = pst3.executeQuery();

            double avgPrice = 0;

            if (rs3.next()) {
                avgPrice = rs3.getDouble(1);
            }

            double roomIncome = totalBookings * (avgPrice * 0.80);

            // 3. TOTAL
            double totalIncome = bookingIncome + roomIncome;

            // 4. INSERT OR UPDATE TODAY RECORD
            PreparedStatement pst4 = con.prepareStatement(
                    "INSERT INTO tbl_income(date, booking_income, room_income, total_income) "
                    + "VALUES (NOW(), ?, ?, ?)"
            );

            pst4.setDouble(1, bookingIncome);
            pst4.setDouble(2, roomIncome);
            pst4.setDouble(3, totalIncome);

            pst4.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Income Updated Successfully"
            );

            loadIncome();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jtbAdminIncome = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtxtFromDate = new javax.swing.JTextField();
        jtxtToDate = new javax.swing.JTextField();
        jtxtBookingIncome = new javax.swing.JTextField();
        jtxtRoomIncome = new javax.swing.JTextField();
        jtxtIncomeTotal = new javax.swing.JTextField();
        jbtCalculate = new javax.swing.JButton();
        jbtSave = new javax.swing.JButton();
        jbtRefresh = new javax.swing.JButton();

        jtbAdminIncome.setModel(new javax.swing.table.DefaultTableModel(
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
        jtbAdminIncome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbAdminIncomeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtbAdminIncome);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Admin Income");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("From Date :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("To Date     :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Booking Income :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Room Income     :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Total Income      :");

        jtxtFromDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtFromDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtFromDateActionPerformed(evt);
            }
        });

        jtxtToDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtToDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtToDateActionPerformed(evt);
            }
        });

        jtxtBookingIncome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtBookingIncome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtBookingIncomeActionPerformed(evt);
            }
        });

        jtxtRoomIncome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtRoomIncome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtRoomIncomeActionPerformed(evt);
            }
        });

        jtxtIncomeTotal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtIncomeTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtIncomeTotalActionPerformed(evt);
            }
        });

        jbtCalculate.setText("Calculate");
        jbtCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtCalculateActionPerformed(evt);
            }
        });

        jbtSave.setText("Save");
        jbtSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSaveActionPerformed(evt);
            }
        });

        jbtRefresh.setText("Refresh");
        jbtRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFromDate))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtToDate))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtBookingIncome))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtRoomIncome))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtIncomeTotal))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtCalculate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtSave))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtRefresh))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jtxtBookingIncome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jtxtRoomIncome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jtxtIncomeTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtCalculate)
                            .addComponent(jbtSave))
                        .addGap(54, 54, 54)
                        .addComponent(jbtRefresh)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtFromDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtFromDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtFromDateActionPerformed

    private void jtbAdminIncomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbAdminIncomeMouseClicked
        // TODO add your handling code here:

        try {

            int row = jtbAdminIncome.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a record first");
                return;
            }

            jtxtFromDate.setText(jtbAdminIncome.getValueAt(row, 1).toString());
            jtxtBookingIncome.setText(jtbAdminIncome.getValueAt(row, 2).toString());
            jtxtRoomIncome.setText(jtbAdminIncome.getValueAt(row, 3).toString());
            jtxtIncomeTotal.setText(jtbAdminIncome.getValueAt(row, 4).toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_jtbAdminIncomeMouseClicked

    private void jtxtToDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtToDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtToDateActionPerformed

    private void jtxtBookingIncomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtBookingIncomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtBookingIncomeActionPerformed

    private void jtxtRoomIncomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtRoomIncomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtRoomIncomeActionPerformed

    private void jtxtIncomeTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtIncomeTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtIncomeTotalActionPerformed

    private void jbtCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtCalculateActionPerformed
        // TODO add your handling code here:
        calculateIncome();

    }//GEN-LAST:event_jbtCalculateActionPerformed

    private void jbtSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSaveActionPerformed
        // TODO add your handling code here:
        saveIncome();

    }//GEN-LAST:event_jbtSaveActionPerformed

    private void jbtRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRefreshActionPerformed
        // TODO add your handling code here:
        loadIncome();

    }//GEN-LAST:event_jbtRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtCalculate;
    private javax.swing.JButton jbtRefresh;
    private javax.swing.JButton jbtSave;
    private javax.swing.JTable jtbAdminIncome;
    private javax.swing.JTextField jtxtBookingIncome;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtIncomeTotal;
    private javax.swing.JTextField jtxtRoomIncome;
    private javax.swing.JTextField jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
