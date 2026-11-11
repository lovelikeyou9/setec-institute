/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package pkg3rd_year_s2_f;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.Timer;

/**
 *
 * @author Admin
 */
public class JPanel_BookingManage extends javax.swing.JPanel {

    private Timer refreshTimer;

    public JPanel_BookingManage() {
        initComponents();

        initTableModel();
        refreshSpinnerToday();
        refreshSpinnerToday2();

        loadBuildings();

        if (jcbbCustomerBuilding.getItemCount() > 0) {
            jcbbCustomerBuilding.setSelectedIndex(0);
            loadRoomsByBuilding(jcbbCustomerBuilding.getSelectedItem().toString());
        }

        loadBuildingRoomTable();
        loadBooking();

    }

    // ================= TIMER =================
    @Override
    public void removeNotify() {
        super.removeNotify();
    }

    // ================= TABLE =================
    void initTableModel() {
        jtbBookingManage.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "Guest Name", "Phone", "Room",
                    "Check In", "Check Out", "Deposit", "Status"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    // ================= SPINNER =================
    void refreshSpinnerToday() {
        jsnTodayOrCheckInOrCheckout.setModel(new SpinnerDateModel());
        JSpinner.DateEditor editor
                = new JSpinner.DateEditor(jsnTodayOrCheckInOrCheckout, "dd-MM-yyyy hh:mm:ss a");
        jsnTodayOrCheckInOrCheckout.setEditor(editor);
    }

    void refreshSpinnerToday2() {
        jsnTodayOrCheckInOrCheckout2.setModel(new SpinnerDateModel());
        JSpinner.DateEditor editor
                = new JSpinner.DateEditor(jsnTodayOrCheckInOrCheckout2, "dd-MM-yyyy hh:mm:ss a");
        jsnTodayOrCheckInOrCheckout2.setEditor(editor);
    }

    // ================= LOAD BOOKING =================
    public void loadBooking() {

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM tbl_booking"
            );

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jtbBookingManage.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("guest_name"),
                    rs.getString("phone"),
                    rs.getString("room_number"),
                    rs.getTimestamp("checkin_date"),
                    rs.getTimestamp("checkout_date"),
                    rs.getDouble("deposit"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= ROOMS =================
    void loadRoomsByBuilding(String building) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT room_number FROM tbl_room WHERE building_name=? AND status='Available'"
            );

            pst.setString(1, building);

            ResultSet rs = pst.executeQuery();

            jcbbCustomerRoom.removeAllItems();

            while (rs.next()) {
                jcbbCustomerRoom.addItem(rs.getString("room_number"));
            }

            if (jcbbCustomerRoom.getItemCount() > 0) {
                jcbbCustomerRoom.setSelectedIndex(0);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    void loadBuildings() {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT DISTINCT building_name FROM tbl_room"
            );

            ResultSet rs = pst.executeQuery();

            jcbbCustomerBuilding.removeAllItems();

            while (rs.next()) {
                jcbbCustomerBuilding.addItem(rs.getString("building_name"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= AUTO EXPIRE =================
    private void autoExpireBookings() {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "UPDATE tbl_booking SET status='Cancelled' WHERE status='Booked' AND checkout_date < NOW()"
            );

            pst.executeUpdate();

            loadBooking();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= BUILDING ROOM TABLE =================
    void loadBuildingRoomTable() {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT building_name, room_number, status, room_price FROM tbl_room"
            );

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jtbBuildingAndRoom.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("building_name"),
                    rs.getString("room_number"),
                    rs.getString("status"),
                    rs.getDouble("room_price")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    
    private void bookRoom() {
    try {
        String guestName = jtxtCustomerName.getText().trim();
        String phone = jtxtCustomerPhone.getText().trim();
        String room = jcbbCustomerRoom.getSelectedItem().toString();

        if (guestName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields");
            return;
        }

        Connection con = DBConnection.getConnection();

        // CHECK ROOM
        PreparedStatement pstRoom = con.prepareStatement(
            "SELECT room_price, status FROM tbl_room WHERE room_number=?"
        );
        pstRoom.setString(1, room);
        ResultSet rsRoom = pstRoom.executeQuery();

        if (!rsRoom.next()) {
            JOptionPane.showMessageDialog(this, "Room not found");
            return;
        }

        if (!rsRoom.getString("status").equalsIgnoreCase("Available")) {
            JOptionPane.showMessageDialog(this, "Room not available");
            return;
        }

        double roomPrice = rsRoom.getDouble("room_price");

        java.util.Date inDate = (java.util.Date) jsnTodayOrCheckInOrCheckout.getValue();
        java.util.Date outDate = (java.util.Date) jsnTodayOrCheckInOrCheckout2.getValue();

        if (!outDate.after(inDate)) {
            JOptionPane.showMessageDialog(this, "Invalid date");
            return;
        }

        Timestamp in = new Timestamp(inDate.getTime());
        Timestamp out = new Timestamp(outDate.getTime());

        // CHECK CONFLICT
        PreparedStatement check = con.prepareStatement(
            "SELECT * FROM tbl_booking WHERE room_number=? " +
            "AND status IN ('Booked','Checked-In') " +
            "AND (checkin_date < ? AND checkout_date > ?)"
        );

        check.setString(1, room);
        check.setTimestamp(2, out);
        check.setTimestamp(3, in);

        ResultSet rsCheck = check.executeQuery();

        if (rsCheck.next()) {
            JOptionPane.showMessageDialog(this, "Room already booked in this time");
            return;
        }

        double deposit = roomPrice * 0.20;

        PreparedStatement pst = con.prepareStatement(
            "INSERT INTO tbl_booking " +
            "(guest_name, phone, room_number, checkin_date, checkout_date, deposit, status, booked_by) " +
            "VALUES (?,?,?,?,?,?,?,?)"
        );

        pst.setString(1, guestName);
        pst.setString(2, phone);
        pst.setString(3, room);
        pst.setTimestamp(4, in);
        pst.setTimestamp(5, out);
        pst.setDouble(6, deposit);
        pst.setString(7, "Booked");
        pst.setString(8, "Admin");

        pst.executeUpdate();

        // UPDATE ROOM
        PreparedStatement upd = con.prepareStatement(
            "UPDATE tbl_room SET status='Booked' WHERE room_number=?"
        );
        upd.setString(1, room);
        upd.executeUpdate();

        JOptionPane.showMessageDialog(this, "Booking success");

        loadBooking();
        loadBuildingRoomTable();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    
    
    private void cancelBooking() {
    try {
        int row = jtbBookingManage.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(jtbBookingManage.getValueAt(row, 0).toString());
        String room = jtbBookingManage.getValueAt(row, 3).toString();

        Connection con = DBConnection.getConnection();

        PreparedStatement pst = con.prepareStatement(
            "SELECT checkin_date, deposit FROM tbl_booking WHERE booking_id=?"
        );
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (!rs.next()) return;

        Timestamp checkIn = rs.getTimestamp("checkin_date");
        double deposit = rs.getDouble("deposit");

        double refund;

        if (new java.util.Date().before(checkIn)) {
            refund = deposit * 0.80; // 20% penalty
        } else {
            refund = 0; // no refund after check-in
        }

        PreparedStatement update = con.prepareStatement(
            "UPDATE tbl_booking SET status='Cancelled' WHERE booking_id=?"
        );
        update.setInt(1, id);
        update.executeUpdate();

        PreparedStatement roomUpd = con.prepareStatement(
            "UPDATE tbl_room SET status='Available' WHERE room_number=?"
        );
        roomUpd.setString(1, room);
        roomUpd.executeUpdate();

        JOptionPane.showMessageDialog(this,
            "Cancelled. Refund: " + refund);

        loadBooking();
        loadBuildingRoomTable();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    
    
    
    private void updateBooking() {
    try {
        int id = Integer.parseInt(jtxtBookingId.getText().trim());
        String newRoom = jcbbCustomerRoom.getSelectedItem().toString();

        Connection con = DBConnection.getConnection();

        PreparedStatement old = con.prepareStatement(
            "SELECT checkin_date, room_number FROM tbl_booking WHERE booking_id=?"
        );
        old.setInt(1, id);
        ResultSet rs = old.executeQuery();

        if (!rs.next()) return;

        Timestamp checkIn = rs.getTimestamp("checkin_date");
        String oldRoom = rs.getString("room_number");

        if (new java.util.Date().after(checkIn)) {
            JOptionPane.showMessageDialog(this, "Cannot update after check-in");
            return;
        }

        if (!oldRoom.equals(newRoom)) {

            PreparedStatement r1 = con.prepareStatement(
                "UPDATE tbl_room SET status='Available' WHERE room_number=?"
            );
            r1.setString(1, oldRoom);
            r1.executeUpdate();

            PreparedStatement r2 = con.prepareStatement(
                "UPDATE tbl_room SET status='Booked' WHERE room_number=?"
            );
            r2.setString(1, newRoom);
            r2.executeUpdate();
        }

        PreparedStatement up = con.prepareStatement(
            "UPDATE tbl_booking SET guest_name=?, phone=?, room_number=? WHERE booking_id=?"
        );

        up.setString(1, jtxtCustomerName.getText());
        up.setString(2, jtxtCustomerPhone.getText());
        up.setString(3, newRoom);
        up.setInt(4, id);

        up.executeUpdate();

        JOptionPane.showMessageDialog(this, "Updated");

        loadBooking();
        loadBuildingRoomTable();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    
    
    
    private void autoNoShow() {
    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement pst = con.prepareStatement(
            "UPDATE tbl_booking SET status='No-Show' " +
            "WHERE status='Booked' AND checkin_date < NOW()"
        );

        pst.executeUpdate();

        loadBooking();

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
        jtbBookingManage = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jtxtCustomerPhone = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtxtCustomerDeposit = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jcbbCustomerRoom = new javax.swing.JComboBox<>();
        jbtBook = new javax.swing.JButton();
        jbtUpdate = new javax.swing.JButton();
        jbtCancel = new javax.swing.JButton();
        jbtClear = new javax.swing.JButton();
        jtxtSearchBooking = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jbtSearchBooking = new javax.swing.JButton();
        jtxtCustomerName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jcbbCustomerBuilding = new javax.swing.JComboBox<>();
        jsnTodayOrCheckInOrCheckout = new javax.swing.JSpinner();
        jsnTodayOrCheckInOrCheckout2 = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtbBuildingAndRoom = new javax.swing.JTable();
        jbtRefresh = new javax.swing.JButton();
        jtxtBookingId = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        jtbBookingManage.setModel(new javax.swing.table.DefaultTableModel(
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
        jtbBookingManage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbBookingManageMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtbBookingManage);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Booking Manage");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Guest Name :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Phone           :");

        jtxtCustomerPhone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCustomerPhoneActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Room            :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Check-in       :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Check-out    :");

        jtxtCustomerDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCustomerDepositActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Deposit        :");

        jcbbCustomerRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jcbbCustomerRoom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbbCustomerRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbbCustomerRoomActionPerformed(evt);
            }
        });

        jbtBook.setText("Book");
        jbtBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBookActionPerformed(evt);
            }
        });

        jbtUpdate.setText("Update");
        jbtUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtUpdateActionPerformed(evt);
            }
        });

        jbtCancel.setText("Cancel");
        jbtCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtCancelActionPerformed(evt);
            }
        });

        jbtClear.setText("Clear");
        jbtClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtClearActionPerformed(evt);
            }
        });

        jtxtSearchBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSearchBookingActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Search         :");

        jbtSearchBooking.setText("Search");
        jbtSearchBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSearchBookingActionPerformed(evt);
            }
        });

        jtxtCustomerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCustomerNameActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Building        :");

        jcbbCustomerBuilding.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jcbbCustomerBuilding.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbbCustomerBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbbCustomerBuildingActionPerformed(evt);
            }
        });

        jtbBuildingAndRoom.setModel(new javax.swing.table.DefaultTableModel(
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
        jtbBuildingAndRoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbBuildingAndRoomMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtbBuildingAndRoom);

        jbtRefresh.setText("Refresh");
        jbtRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRefreshActionPerformed(evt);
            }
        });

        jtxtBookingId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtBookingIdActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("ID Booking   :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtSearchBooking)
                                    .addComponent(jtxtSearchBooking)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jsnTodayOrCheckInOrCheckout2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jsnTodayOrCheckInOrCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jbtClear)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jbtBook)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtUpdate)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtCancel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbbCustomerBuilding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbbCustomerRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtCustomerDeposit))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtCustomerPhone))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtCustomerName))
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jbtRefresh))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtBookingId)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jtxtBookingId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jtxtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jtxtCustomerPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jcbbCustomerBuilding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jcbbCustomerRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jsnTodayOrCheckInOrCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jsnTodayOrCheckInOrCheckout2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jtxtCustomerDeposit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtBook)
                            .addComponent(jbtUpdate)
                            .addComponent(jbtCancel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtClear)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jtxtSearchBooking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtSearchBooking)
                        .addGap(32, 32, 32)
                        .addComponent(jbtRefresh)
                        .addGap(0, 120, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtbBookingManageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbBookingManageMouseClicked
        // TODO add your handling code here:

        try {

        String guestName = jtxtCustomerName.getText().trim();
        String phone = jtxtCustomerPhone.getText().trim();
        String room = jcbbCustomerRoom.getSelectedItem().toString();

        if (guestName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields");
            return;
        }

        Connection con = DBConnection.getConnection();

        // =========================
        // CHECK ROOM INFO
        // =========================
        PreparedStatement pstRoom = con.prepareStatement(
                "SELECT room_price, status FROM tbl_room WHERE room_number=?"
        );

        pstRoom.setString(1, room);
        ResultSet rsRoom = pstRoom.executeQuery();

        if (!rsRoom.next()) {
            JOptionPane.showMessageDialog(this, "Room not found");
            return;
        }

        String roomStatus = rsRoom.getString("status");
        double roomPrice = rsRoom.getDouble("room_price");

        if (!"Available".equalsIgnoreCase(roomStatus)) {
            JOptionPane.showMessageDialog(this, "Room not available");
            return;
        }

        java.util.Date checkIn = (java.util.Date) jsnTodayOrCheckInOrCheckout.getValue();
        java.util.Date checkOut = (java.util.Date) jsnTodayOrCheckInOrCheckout2.getValue();

        if (!checkOut.after(checkIn)) {
            JOptionPane.showMessageDialog(this, "Invalid dates");
            return;
        }

        Timestamp in = new Timestamp(checkIn.getTime());
        Timestamp out = new Timestamp(checkOut.getTime());

        // =========================
        // 🔥 CHECK DATE CONFLICT (IMPORTANT FIX)
        // =========================
        PreparedStatement pstCheck = con.prepareStatement(
            "SELECT * FROM tbl_booking " +
            "WHERE room_number=? AND status='Booked' " +
            "AND (? < checkout_date AND ? > checkin_date)"
        );

        pstCheck.setString(1, room);
        pstCheck.setTimestamp(2, in);
        pstCheck.setTimestamp(3, out);

        ResultSet rsCheck = pstCheck.executeQuery();

        if (rsCheck.next()) {
            JOptionPane.showMessageDialog(this,
                "❌ Room already booked in this date range!");
            return;
        }

        // =========================
        // DEPOSIT (20%)
        // =========================
        double deposit = roomPrice * 0.20;

        // =========================
        // INSERT BOOKING
        // =========================
        PreparedStatement pst = con.prepareStatement(
                "INSERT INTO tbl_booking " +
                "(guest_name, phone, room_number, checkin_date, checkout_date, deposit, customer_type, status, booked_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        pst.setString(1, guestName);
        pst.setString(2, phone);
        pst.setString(3, room);
        pst.setTimestamp(4, in);
        pst.setTimestamp(5, out);
        pst.setDouble(6, deposit);
        pst.setString(7, "BOOKED");
        pst.setString(8, "Booked");
        pst.setString(9, "Admin");

        pst.executeUpdate();

        // =========================
        // UPDATE ROOM STATUS
        // =========================
        PreparedStatement pstRoomUpdate = con.prepareStatement(
                "UPDATE tbl_room SET status='Booked' WHERE room_number=?"
        );

        pstRoomUpdate.setString(1, room);
        pstRoomUpdate.executeUpdate();

        JOptionPane.showMessageDialog(this, "Booking success");

        loadBooking();
        loadBuildingRoomTable();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }

    }//GEN-LAST:event_jtbBookingManageMouseClicked

    private void jtxtCustomerPhoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCustomerPhoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCustomerPhoneActionPerformed

    private void jcbbCustomerRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbbCustomerRoomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbbCustomerRoomActionPerformed

    private void jbtBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBookActionPerformed
        // TODO add your handling code here:
   
        bookRoom();
        

    }//GEN-LAST:event_jbtBookActionPerformed

    private void jbtUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtUpdateActionPerformed
        // TODO add your handling code here:

        updateBooking();


    }//GEN-LAST:event_jbtUpdateActionPerformed

    private void jbtCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtCancelActionPerformed
        // TODO add your handling code here:

         cancelBooking();

    }//GEN-LAST:event_jbtCancelActionPerformed

    private void jbtClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtClearActionPerformed
        // TODO add your handling code here:

        jtxtCustomerName.setText("");
        jtxtCustomerPhone.setText("");
        refreshSpinnerToday();
        refreshSpinnerToday2();
        jtxtCustomerDeposit.setText("");

    }//GEN-LAST:event_jbtClearActionPerformed

    private void jtxtSearchBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtSearchBookingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtSearchBookingActionPerformed

    private void jbtSearchBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSearchBookingActionPerformed
        // TODO add your handling code here:
        try {

            String keyword = jtxtSearchBooking.getText().trim();

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM tbl_booking WHERE guest_name LIKE ?"
            );

            pst.setString(1, "%" + keyword + "%");

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jtbBookingManage.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("guest_name"),
                    rs.getString("phone"),
                    rs.getString("room_number"),
                    rs.getTimestamp("checkin_date"),
                    rs.getTimestamp("checkout_date"),
                    rs.getDouble("deposit"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_jbtSearchBookingActionPerformed

    private void jtxtCustomerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCustomerNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCustomerNameActionPerformed

    private void jcbbCustomerBuildingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbbCustomerBuildingActionPerformed
        // TODO add your handling code here:
        if (jcbbCustomerBuilding.getSelectedItem() != null) {
            String building = jcbbCustomerBuilding.getSelectedItem().toString();
            loadRoomsByBuilding(building);
        }
    }//GEN-LAST:event_jcbbCustomerBuildingActionPerformed

    private void jtbBuildingAndRoomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbBuildingAndRoomMouseClicked
        // TODO add your handling code here:

        int row = jtbBuildingAndRoom.getSelectedRow();

        if (row == -1) {
            return;
        }

        try {
            String building = jtbBuildingAndRoom.getValueAt(row, 0).toString();
            String room = jtbBuildingAndRoom.getValueAt(row, 1).toString();
            double roomPrice = Double.parseDouble(
                    jtbBuildingAndRoom.getValueAt(row, 3).toString()
            );

            // 1. SET BUILDING COMBOBOX
            jcbbCustomerBuilding.setSelectedItem(building);

            // 2. LOAD ROOMS AGAIN FOR THAT BUILDING
            loadRoomsByBuilding(building);

            // 3. SET ROOM (safe delay)
            javax.swing.SwingUtilities.invokeLater(() -> {
                jcbbCustomerRoom.setSelectedItem(room);
            });

            // 4. SET DEPOSIT PRICE (20% example OR full price)
            double deposit = roomPrice * 0.20;

            jtxtCustomerDeposit.setText(String.valueOf(deposit));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error selecting room: " + e.getMessage());
        }


    }//GEN-LAST:event_jtbBuildingAndRoomMouseClicked

    private void jtxtCustomerDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCustomerDepositActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCustomerDepositActionPerformed

    private void jbtRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRefreshActionPerformed
        // TODO add your handling code here:
        try {

            // =========================
            // 1. Reload booking table
            // =========================
            loadBooking();

            // =========================
            // 2. Reload building-room table
            // =========================
            loadBuildingRoomTable();

            // =========================
            // 3. Reload buildings combobox
            // =========================
            loadBuildings();

            // =========================
            // 4. Refresh room list (safe check)
            // =========================
            if (jcbbCustomerBuilding.getSelectedItem() != null) {
                String building = jcbbCustomerBuilding.getSelectedItem().toString();
                loadRoomsByBuilding(building);
            }

            // =========================
            // 5. Clear selection (avoid confusion)
            // =========================
            jtbBookingManage.clearSelection();
            jtbBuildingAndRoom.clearSelection();

            // =========================
            // 6. Optional clear input fields
            // =========================
            jtxtCustomerName.setText("");
            jtxtCustomerPhone.setText("");
            jtxtCustomerDeposit.setText("");

            JOptionPane.showMessageDialog(this, "Refreshed Successfully");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Refresh Error: " + e.getMessage());
        }
    }//GEN-LAST:event_jbtRefreshActionPerformed

    private void jtxtBookingIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtBookingIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtBookingIdActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtBook;
    private javax.swing.JButton jbtCancel;
    private javax.swing.JButton jbtClear;
    private javax.swing.JButton jbtRefresh;
    private javax.swing.JButton jbtSearchBooking;
    private javax.swing.JButton jbtUpdate;
    private javax.swing.JComboBox<String> jcbbCustomerBuilding;
    private javax.swing.JComboBox<String> jcbbCustomerRoom;
    private javax.swing.JSpinner jsnTodayOrCheckInOrCheckout;
    private javax.swing.JSpinner jsnTodayOrCheckInOrCheckout2;
    private javax.swing.JTable jtbBookingManage;
    private javax.swing.JTable jtbBuildingAndRoom;
    private javax.swing.JTextField jtxtBookingId;
    private javax.swing.JTextField jtxtCustomerDeposit;
    private javax.swing.JTextField jtxtCustomerName;
    private javax.swing.JTextField jtxtCustomerPhone;
    private javax.swing.JTextField jtxtSearchBooking;
    // End of variables declaration//GEN-END:variables
}
