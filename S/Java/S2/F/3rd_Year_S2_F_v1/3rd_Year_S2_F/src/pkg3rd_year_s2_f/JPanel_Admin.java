/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package pkg3rd_year_s2_f;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.Timer;
import java.awt.CardLayout;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class JPanel_Admin extends javax.swing.JPanel {

    /**
     * Creates new form JPanel_Admin
     */
    public JPanel_Admin() {
        initComponents();

//        loadRooms();
        refreshSpinnerToday();
        loadRooms();
        loadDashboard();
    autoUpdateIncome();
//        refreshSpinnerBooking();
//        refreshSpinnerToday();
//        jpnScrollPane.setLayout(new BorderLayout());
//        jpnScrollPane.add(scrollPane2, BorderLayout.CENTER);
        LocalDate currentDate = LocalDate.now();
        // EEE = Mon/Tue/Wed/...
        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("EEE, dd-MMM-yyyy");
        jlbDate.setText("Date : " + currentDate.format(dataFormatter));

        ActionListener actionListenerTime = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalTime currentTime = LocalTime.now();
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                jlbTime.setText("Time : " + currentTime.format(timeFormatter));

                // auto refresh parking
                // refreshAll(); // refreshParkingState();
            }
        };

        Timer timer = new Timer(1000, actionListenerTime);

        timer.start();

        // TIME (LIVE UPDATE)
//        Timer timer = new Timer(1000, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                LocalTime time = LocalTime.now();
//                DateTimeFormatter tf = DateTimeFormatter.ofPattern("hh:mm:ss a");
//
//                jlbTime.setText("Time: " + time.format(tf));
//            }
//        });
    }

    public void loadRooms() {

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM tbl_room"
            );

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model
                    = (DefaultTableModel) jtbBuildAndRoom.getModel();

            model.setRowCount(0);

            while (rs.next()) {

                model.addRow(new Object[]{
                    rs.getInt("room_id"),
                    rs.getString("building_name"),
                    rs.getString("room_number"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void clearFields() {
        jtxtCustomerBuilding.setText("");
        jtxtCustomerRoom.setText("");
        jtxtCustomerName.setText("");
        jtxtCustomerPhone.setText("");
        jtxtCustomerIdentityCard.setText("");
    }

//    void refreshSpinnerToday(){
//        jsnTodayOrCheckInOrCheckout.setModel(new SpinnerDateModel());
//        JSpinner.DateEditor editor = new JSpinner.DateEditor(jsnTodayOrCheckInOrCheckout, "dd-MM-yyyy   hh:mm:ss a");
//        
//        jsnTodayOrCheckInOrCheckout.setEditor(editor);
//    }
//    void refreshSpinnerBooking (){
//        // SpinnerDateModel() = 5/16/26, 10:06 PM
//        jsDateAndTimeBook.setModel(new SpinnerDateModel());
//        
//        // ( JSpinner ) A Swing input component with a textbox and 🔼🔽 buttons used to increase/decrease 
//        //   values like numbers, dates, times, or lists.
//        // ( JSpinner.DateEditor ) A formatter/editor for JSpinner that controls how date/time 
//        //   is displayed and edited inside the spinner.
//        JSpinner.DateEditor editor = new JSpinner.DateEditor(jsDateAndTimeBook, "dd-MM-yyyy   hh:mm:ss a");
//        
//        jsDateAndTimeBook.setEditor(editor);
//        
//    }
    void refreshSpinnerToday() {
        jsnTodayOrCheckInOrCheckout.setModel(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(jsnTodayOrCheckInOrCheckout, "dd-MM-yyyy   hh:mm:ss a");

        jsnTodayOrCheckInOrCheckout.setEditor(editor);

    }

private void autoUpdateIncome() {

    try {

        Connection con = DBConnection.getConnection();

        // 1. Deposit income
        PreparedStatement pst1 = con.prepareStatement(
            "SELECT SUM(deposit) FROM tbl_booking"
        );
        ResultSet rs1 = pst1.executeQuery();
        double depositIncome = rs1.next() ? rs1.getDouble(1) : 0;

        // 2. Check-in income (IF you store it, otherwise skip)
        PreparedStatement pst2 = con.prepareStatement(
            "SELECT SUM(checkin_payment) FROM tbl_booking"
        );
        ResultSet rs2 = pst2.executeQuery();
        double checkinIncome = rs2.next() ? rs2.getDouble(1) : 0;

        // 3. Checkout income (remaining payment)
        PreparedStatement pst3 = con.prepareStatement(
            "SELECT SUM(checkout_payment) FROM tbl_booking"
        );
        ResultSet rs3 = pst3.executeQuery();
        double checkoutIncome = rs3.next() ? rs3.getDouble(1) : 0;

        // 4. Penalty income (late checkout)
        PreparedStatement pst4 = con.prepareStatement(
            "SELECT SUM(penalty) FROM tbl_booking"
        );
        ResultSet rs4 = pst4.executeQuery();
        double penaltyIncome = rs4.next() ? rs4.getDouble(1) : 0;

        // TOTAL
        double totalIncome = depositIncome + checkinIncome + checkoutIncome + penaltyIncome;

        // SHOW IN LABEL
        jlbTotalIncome.setText(String.format("$ %.2f", totalIncome));

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}



    private void loadDashboard() {

        try {

            Connection con = DBConnection.getConnection();

            // 1. Total rooms
            PreparedStatement pst1 = con.prepareStatement(
                    "SELECT COUNT(*) FROM tbl_room"
            );
            ResultSet rs1 = pst1.executeQuery();
            int totalRooms = rs1.next() ? rs1.getInt(1) : 0;

            // 2. Available rooms
            PreparedStatement pst2 = con.prepareStatement(
                    "SELECT COUNT(*) FROM tbl_room WHERE status='Available'"
            );
            ResultSet rs2 = pst2.executeQuery();
            int availableRooms = rs2.next() ? rs2.getInt(1) : 0;

            // 3. Occupied rooms
            PreparedStatement pst3 = con.prepareStatement(
                    "SELECT COUNT(*) FROM tbl_room WHERE status='Occupied'"
            );
            ResultSet rs3 = pst3.executeQuery();
            int occupiedRooms = rs3.next() ? rs3.getInt(1) : 0;

            // 4. Booked rooms
            PreparedStatement pst4 = con.prepareStatement(
                    "SELECT COUNT(*) FROM tbl_room WHERE status='Booked'"
            );
            ResultSet rs4 = pst4.executeQuery();
            int bookedRooms = rs4.next() ? rs4.getInt(1) : 0;

            // 5. Total bookings
            PreparedStatement pst5 = con.prepareStatement(
                    "SELECT COUNT(*) FROM tbl_booking"
            );
            ResultSet rs5 = pst5.executeQuery();
            int totalBookings = rs5.next() ? rs5.getInt(1) : 0;

            // 6. Total income
            PreparedStatement pst6 = con.prepareStatement(
                    "SELECT SUM(total_income) FROM tbl_income"
            );
            ResultSet rs6 = pst6.executeQuery();
            double totalIncome = rs6.next() ? rs6.getDouble(1) : 0;

            // SHOW IN LABELS (YOU MUST CREATE THESE LABELS)
            jlbTotalRooms.setText(String.valueOf(totalRooms));
            jlbAvailableRooms.setText(String.valueOf(availableRooms));
jlbOccupiedRooms.setText(String.valueOf(occupiedRooms));
jlbBookedRooms.setText(String.valueOf(bookedRooms));
            jlbTotalBookings.setText(String.valueOf(totalBookings));
            jlbTotalIncome.setText(String.valueOf(totalIncome));

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
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jlbTime = new javax.swing.JLabel();
        jlbDate = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jbtBookingManage = new javax.swing.JButton();
        jbtCheckout = new javax.swing.JButton();
        jtxtCustomerName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtCustomerPhone = new javax.swing.JTextField();
        jbtCheckIn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtCustomerBuilding = new javax.swing.JTextField();
        jtxtCustomerRoom = new javax.swing.JTextField();
        jsnTodayOrCheckInOrCheckout = new javax.swing.JSpinner();
        jbtRefreshTodayOrCheckInOrCheckout = new javax.swing.JButton();
        jbtViewOrManage = new javax.swing.JButton();
        jtxtCustomerIdentityCard = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jbtAdminLogout = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbBuildAndRoom = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jlbTotalRooms = new javax.swing.JLabel();
        jlbAvailableRooms = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jlbOccupiedRooms = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jlbBookedRooms = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlbTotalBookings = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlbTotalIncome = new javax.swing.JLabel();
        jbtSearch = new javax.swing.JButton();
        jtxtSearch = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 51, 153));
        setPreferredSize(new java.awt.Dimension(801, 500));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jlbTime.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jlbTime.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        jlbDate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jlbDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Name  :");

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jbtBookingManage.setBackground(new java.awt.Color(0, 102, 102));
        jbtBookingManage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtBookingManage.setForeground(new java.awt.Color(255, 255, 255));
        jbtBookingManage.setText("Booking Manage");
        jbtBookingManage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBookingManageActionPerformed(evt);
            }
        });
        jPanel3.add(jbtBookingManage, new java.awt.GridBagConstraints());

        jbtCheckout.setBackground(new java.awt.Color(255, 51, 51));
        jbtCheckout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtCheckout.setForeground(new java.awt.Color(255, 255, 255));
        jbtCheckout.setText("Checkout");
        jbtCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtCheckoutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 26, 0, 0);
        jPanel3.add(jbtCheckout, gridBagConstraints);

        jtxtCustomerName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtCustomerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCustomerNameActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Phone :");

        jtxtCustomerPhone.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtCustomerPhone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCustomerPhoneActionPerformed(evt);
            }
        });

        jbtCheckIn.setBackground(new java.awt.Color(255, 153, 0));
        jbtCheckIn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtCheckIn.setForeground(new java.awt.Color(255, 255, 255));
        jbtCheckIn.setText("Check In");
        jbtCheckIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtCheckInActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Building :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Room     :");

        jtxtCustomerBuilding.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtCustomerBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCustomerBuildingActionPerformed(evt);
            }
        });

        jtxtCustomerRoom.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtCustomerRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCustomerRoomActionPerformed(evt);
            }
        });

        jsnTodayOrCheckInOrCheckout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jbtRefreshTodayOrCheckInOrCheckout.setText("Refresh");
        jbtRefreshTodayOrCheckInOrCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRefreshTodayOrCheckInOrCheckoutActionPerformed(evt);
            }
        });

        jbtViewOrManage.setBackground(new java.awt.Color(0, 0, 0));
        jbtViewOrManage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtViewOrManage.setForeground(new java.awt.Color(255, 255, 255));
        jbtViewOrManage.setText("View/Manage");
        jbtViewOrManage.setPreferredSize(new java.awt.Dimension(80, 28));
        jbtViewOrManage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtViewOrManageActionPerformed(evt);
            }
        });

        jtxtCustomerIdentityCard.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtCustomerIdentityCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCustomerIdentityCardActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("ID Card :");

        jbtAdminLogout.setBackground(new java.awt.Color(204, 0, 204));
        jbtAdminLogout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtAdminLogout.setForeground(new java.awt.Color(255, 255, 255));
        jbtAdminLogout.setText("Logout");
        jbtAdminLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtAdminLogoutActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Admin");

        jtbBuildAndRoom.setModel(new javax.swing.table.DefaultTableModel(
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
        jtbBuildAndRoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbBuildAndRoomMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtbBuildAndRoom);

        jLabel7.setText("Total Rooms :");

        jlbTotalRooms.setOpaque(true);

        jlbAvailableRooms.setOpaque(true);

        jLabel8.setText("Available Rooms :");

        jLabel9.setText("Occupied Rooms");

        jlbOccupiedRooms.setOpaque(true);

        jLabel10.setText("Booked Rooms :");

        jlbBookedRooms.setOpaque(true);

        jLabel11.setText("Total Bookings :");

        jlbTotalBookings.setOpaque(true);

        jLabel12.setText("Total Income :");

        jlbTotalIncome.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlbTotalIncome.setOpaque(true);

        jbtSearch.setText("Search");
        jbtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSearchActionPerformed(evt);
            }
        });

        jtxtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlbTime, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbDate, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtCustomerIdentityCard))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtCustomerPhone))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxtCustomerBuilding, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                                    .addComponent(jtxtCustomerRoom))))
                        .addGap(4, 4, 4)
                        .addComponent(jbtCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jtxtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtSearch))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jbtRefreshTodayOrCheckInOrCheckout)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtViewOrManage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jsnTodayOrCheckInOrCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jbtAdminLogout))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbTotalRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbAvailableRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbOccupiedRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbBookedRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbTotalBookings, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 187, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbTotalIncome, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jlbDate, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                        .addComponent(jlbTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jtxtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)
                                .addComponent(jtxtCustomerBuilding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jtxtCustomerPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)
                                .addComponent(jtxtCustomerRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jbtRefreshTodayOrCheckInOrCheckout)
                                        .addComponent(jbtViewOrManage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jsnTodayOrCheckInOrCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jbtCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(5, 5, 5)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jtxtCustomerIdentityCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbtAdminLogout))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtSearch)
                        .addComponent(jbtSearch)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbTotalRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jlbAvailableRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jlbOccupiedRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jlbBookedRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jlbTotalBookings, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbTotalIncome, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtRefreshTodayOrCheckInOrCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRefreshTodayOrCheckInOrCheckoutActionPerformed
        // TODO add your handling code here:
        refreshSpinnerToday();
    }//GEN-LAST:event_jbtRefreshTodayOrCheckInOrCheckoutActionPerformed

    private void jbtCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtCheckoutActionPerformed
        // TODO add your handling code here:

        try {

        String room = jtxtCustomerRoom.getText().trim();
        String staff = "Admin";

        Connection con = DBConnection.getConnection();

        // STEP 1: get active booking
        PreparedStatement pst = con.prepareStatement(
            "SELECT * FROM tbl_booking WHERE room_number=? AND status='Checked-In' ORDER BY booking_id DESC LIMIT 1"
        );
        pst.setString(1, room);
        ResultSet rs = pst.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "No active check-in");
            return;
        }

        int bookingId = rs.getInt("booking_id");
        String customerType = rs.getString("customer_type");

        // STEP 2: get room price
        PreparedStatement pstRoom = con.prepareStatement(
            "SELECT room_price FROM tbl_room WHERE room_number=?"
        );
        pstRoom.setString(1, room);
        ResultSet rsRoom = pstRoom.executeQuery();

        if (!rsRoom.next()) {
            JOptionPane.showMessageDialog(this, "Room not found");
            return;
        }

        double price = rsRoom.getDouble("room_price");

        // STEP 3: payment rule
        double total;

        if (customerType.equalsIgnoreCase("Walk-in")) {
            total = price; // 100%
        } else {
            total = price * 0.80; // booked 80%
        }

        // STEP 4: late penalty
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp checkoutDate = rs.getTimestamp("checkout_date");

        double penalty = 0;

        if (checkoutDate != null && now.after(checkoutDate)) {
            long hours = (now.getTime() - checkoutDate.getTime()) / (1000 * 60 * 60);
            if (hours < 1) hours = 1;

            double hourly = price / 24;
            penalty = hours * hourly * 1.5;
        }

        total += penalty;

        // STEP 5: update booking
        PreparedStatement update = con.prepareStatement(
            "UPDATE tbl_booking SET status='Checked-Out', checked_out_by=? WHERE booking_id=?"
        );
        update.setString(1, staff);
        update.setInt(2, bookingId);
        update.executeUpdate();

        // STEP 6: update room
        PreparedStatement roomUpdate = con.prepareStatement(
            "UPDATE tbl_room SET status='Available' WHERE room_number=?"
        );
        roomUpdate.setString(1, room);
        roomUpdate.executeUpdate();

        JOptionPane.showMessageDialog(this,
            "CHECK-OUT SUCCESS\nTotal: " + total + "\nPenalty: " + penalty
        );

        loadRooms();
        loadDashboard();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }


    }//GEN-LAST:event_jbtCheckoutActionPerformed

    private void jbtViewOrManageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtViewOrManageActionPerformed
        // TODO add your handling code here:

        JFrame frame = new JFrame("View & Manage");

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new JPanel_ViewOrManage());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // JFrame_Main mainFrame = (JFrame_Main) SwingUtilities.getWindowAncestor(this);
        // mainFrame.switchPanel(new JPanel_ViewOrManage());
//        JFrame_Main.cardLayout.show(JFrame_Main.mainPanel, "JPANEL_VIEWORMANAGE");
//        CardLayout c1 = (CardLayout) JPanel_ViewOrManage.getLayout();        
//        JPanel_ViewOrManage formTest2Calculate = new JPanel_ViewOrManage();
//        formTest2Calculate.setVisible(true);
    }//GEN-LAST:event_jbtViewOrManageActionPerformed

    private void jbtCheckInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtCheckInActionPerformed
        // TODO add your handling code here:

    try {

        String room = jtxtCustomerRoom.getText().trim();
        String staff = "Admin"; // or login user

        Connection con = DBConnection.getConnection();

        // STEP 1: get room status + price
        PreparedStatement pst = con.prepareStatement(
            "SELECT status FROM tbl_room WHERE room_number=?"
        );
        pst.setString(1, room);
        ResultSet rs = pst.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Room not found");
            return;
        }

        String status = rs.getString("status");

        if (status.equalsIgnoreCase("Occupied")) {
            JOptionPane.showMessageDialog(this, "Room already OCCUPIED");
            return;
        }

        // STEP 2: BOOKED CHECK-IN
        if (status.equalsIgnoreCase("Booked")) {

            PreparedStatement pst2 = con.prepareStatement(
                "SELECT booking_id FROM tbl_booking WHERE room_number=? AND status='Booked' ORDER BY booking_id DESC LIMIT 1"
            );
            pst2.setString(1, room);
            ResultSet rs2 = pst2.executeQuery();

            if (!rs2.next()) {
                JOptionPane.showMessageDialog(this, "No booking found");
                return;
            }

            int bookingId = rs2.getInt("booking_id");

            PreparedStatement update = con.prepareStatement(
                "UPDATE tbl_booking SET status='Checked-In', checked_in_by=?, checkin_date=NOW() WHERE booking_id=?"
            );
            update.setString(1, staff);
            update.setInt(2, bookingId);
            update.executeUpdate();
        }

        // STEP 3: WALK-IN CHECK-IN (Available room)
        else if (status.equalsIgnoreCase("Available")) {

            PreparedStatement insert = con.prepareStatement(
                "INSERT INTO tbl_booking(room_number, guest_name, phone, identity_card, customer_type, status, checkin_date, booked_by, checked_in_by, deposit) " +
                "VALUES (?, 'WALK-IN', '', '', 'Walk-in', 'Checked-In', NOW(), ?, ?, 0)"
            );

            insert.setString(1, room);
            insert.setString(2, staff);
            insert.setString(3, staff);
            insert.executeUpdate();
        }

        // STEP 4: UPDATE ROOM STATUS
        PreparedStatement pstRoom = con.prepareStatement(
            "UPDATE tbl_room SET status='Occupied' WHERE room_number=?"
        );
        pstRoom.setString(1, room);
        pstRoom.executeUpdate();

        JOptionPane.showMessageDialog(this, "CHECK-IN SUCCESS by " + staff);

        loadRooms();
        loadDashboard();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }

    }//GEN-LAST:event_jbtCheckInActionPerformed

    private void jtxtCustomerPhoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCustomerPhoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCustomerPhoneActionPerformed

    private void jtxtCustomerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCustomerNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCustomerNameActionPerformed

    private void jtxtCustomerIdentityCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCustomerIdentityCardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCustomerIdentityCardActionPerformed

    private void jbtAdminLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtAdminLogoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbtAdminLogoutActionPerformed

    private void jtxtCustomerRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCustomerRoomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCustomerRoomActionPerformed

    private void jtxtCustomerBuildingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCustomerBuildingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCustomerBuildingActionPerformed

    private void jbtBookingManageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBookingManageActionPerformed
        // TODO add your handling code here:

        JFrame frame = new JFrame("Booking Manage");

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new JPanel_BookingManage());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }//GEN-LAST:event_jbtBookingManageActionPerformed

    private void jtbBuildAndRoomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbBuildAndRoomMouseClicked
        // TODO add your handling code here:

        int row = jtbBuildAndRoom.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room first");
            return;
        }

        try {

            String roomId = String.valueOf(jtbBuildAndRoom.getValueAt(row, 0));
            String building = String.valueOf(jtbBuildAndRoom.getValueAt(row, 1));
            String roomNumber = String.valueOf(jtbBuildAndRoom.getValueAt(row, 2));
            String status = String.valueOf(jtbBuildAndRoom.getValueAt(row, 3));

            // fill UI
            jtxtCustomerBuilding.setText(building);
            jtxtCustomerRoom.setText(roomNumber);

            // optional: show info
            JOptionPane.showMessageDialog(this,
                    "Room ID: " + roomId
                    + "\nBuilding: " + building
                    + "\nRoom: " + roomNumber
                    + "\nStatus: " + status
            );

            // warning
            if ("Occupied".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this,
                        "⚠ This room is already OCCUPIED");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading room data: " + e.getMessage());
        }

    }//GEN-LAST:event_jtbBuildAndRoomMouseClicked

    private void jtxtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtSearchActionPerformed

    private void jbtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSearchActionPerformed
        // TODO add your handling code here:

        try {

            String keyword = jtxtSearch.getText().trim();

            // ✅ IF EMPTY → SHOW ALL ROOMS
            if (keyword.isEmpty()) {
                loadRooms();
                return;
            }

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM tbl_room WHERE room_number LIKE ? OR building_name LIKE ? OR status LIKE ?"
            );

            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            pst.setString(3, "%" + keyword + "%");

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "Room ID",
                        "Building",
                        "Room Number",
                        "Status"
                    }
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("room_id"),
                    rs.getString("building_name"),
                    rs.getString("room_number"),
                    rs.getString("status")
                });
            }

            jtbBuildAndRoom.setModel(model);
            jtbBuildAndRoom.setAutoCreateRowSorter(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }


    }//GEN-LAST:event_jbtSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtAdminLogout;
    private javax.swing.JButton jbtBookingManage;
    private javax.swing.JButton jbtCheckIn;
    private javax.swing.JButton jbtCheckout;
    private javax.swing.JButton jbtRefreshTodayOrCheckInOrCheckout;
    private javax.swing.JButton jbtSearch;
    private javax.swing.JButton jbtViewOrManage;
    private javax.swing.JLabel jlbAvailableRooms;
    private javax.swing.JLabel jlbBookedRooms;
    private javax.swing.JLabel jlbDate;
    private javax.swing.JLabel jlbOccupiedRooms;
    private javax.swing.JLabel jlbTime;
    private javax.swing.JLabel jlbTotalBookings;
    private javax.swing.JLabel jlbTotalIncome;
    private javax.swing.JLabel jlbTotalRooms;
    private javax.swing.JSpinner jsnTodayOrCheckInOrCheckout;
    private javax.swing.JTable jtbBuildAndRoom;
    private javax.swing.JTextField jtxtCustomerBuilding;
    private javax.swing.JTextField jtxtCustomerIdentityCard;
    private javax.swing.JTextField jtxtCustomerName;
    private javax.swing.JTextField jtxtCustomerPhone;
    private javax.swing.JTextField jtxtCustomerRoom;
    private javax.swing.JTextField jtxtSearch;
    // End of variables declaration//GEN-END:variables
}
