/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package test_4_f;

import java.time.LocalDate;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/*
✅ Main Models
| Model                | Use         |
| -------------------- | ----------- |
| `SpinnerNumberModel` | numbers     |
| `SpinnerDateModel`   | date/time   |
| `SpinnerListModel`   | list values |

✅ Real-world examples
| System    | JSpinner Use     |
| --------- | ---------------- |
| POS       | quantity         |
| Hospital  | appointment date |
| Parking   | entry/exit time  |
| School    | attendance date  |
| Hotel     | booking date     |
| Inventory | stock count      |

⭐ Most important methods
| Method        | Purpose               |
| ------------- | --------------------- |
| `getValue()`  | get value             |
| `setValue()`  | set value             |
| `setModel()`  | change model          |
| `setEditor()` | change display format 

⭐ In short
JSpinner is a smart input component that can:
handle numbers
handle dates/times
handle lists
increase/decrease automatically
validate ranges
format display
work with databases and forms easily
*/
import javax.swing.JSpinner;

/*
This model is used with JSpinner to handle:
📅 datces
⏰ time
calendar values

✅ What SpinnerDateModel can do
It controls:
current date/time value
minimum date
maximum date
how the spinner increases/decreases

1. Store date/time value 📅

2. Increase/Decrease dates 🔼🔽
Using arrows:
next day
previous month
next year
next hour
depending on cursor position.

3. Set minimum date ⛔
Prevent dates before today.

4. Set maximum date ⛔
Prevent dates after limit.

5. Control step field ⭐
Very important.
You can control what changes:
YEAR
MONTH
DAY
HOUR
MINUTE
SECOND
using Calendar.

🧠 Meaning
| Part                    | Meaning            |
| ----------------------- | ------------------ |
| `new Date()`            | current value      |
| `null`                  | no minimum         |
| `null`                  | no maximum         |
| `Calendar.DAY_OF_MONTH` | arrows change days |

✅ Step types
| Calendar Constant       | What changes |
| ----------------------- | ------------ |
| `Calendar.YEAR`         | year         |
| `Calendar.MONTH`        | month        |
| `Calendar.DAY_OF_MONTH` | day          |
| `Calendar.HOUR`         | hour         |
| `Calendar.MINUTE`       | minute       |
| `Calendar.SECOND`       | second       |

⭐ Real-world uses
| System     | Use              |
| ---------- | ---------------- |
| Hospital   | appointment time |
| Parking    | entry/exit       |
| Hotel      | booking date     |
| POS        | invoice date     |
| Attendance | attendance date  |

✅ Important methods
| Method               | Purpose          |
| -------------------- | ---------------- |
| `getDate()`          | get current date |
| `setStart()`         | minimum date     |
| `setEnd()`           | maximum date     |
| `setCalendarField()` | change step type |
*/
import javax.swing.SpinnerDateModel;


// import javax.swing.JFrame;

// import java.awt.FlowLayout;

// store width and height together.
import java.awt.Dimension;

// FlowLayout is a layout manager in Java Swing that arranges components like words in a sentence.
import java.awt.FlowLayout;

import javax.swing.JLabel;

import java.awt.Color;

// Mouse
import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;

import java.util.Date;

// show Dialog
import javax.swing.JOptionPane;

import java.text.SimpleDateFormat;

/**
 *
 * @author Admin
 */



public class test_4_F_jf_Car_Parking extends javax.swing.JFrame {

    // LocalDate currentDate = LocalDate.now();
    // LocalTime currentTime = LocalTime.now();
    
    Color myGreen = new Color(136, 255, 136);
    
    void refreshSpinnerBooking (){
        // SpinnerDateModel() = 5/16/26, 10:06 PM
        jsDateAndTimeBook.setModel(new SpinnerDateModel());
        
        // ( JSpinner ) A Swing input component with a textbox and 🔼🔽 buttons used to increase/decrease 
        //   values like numbers, dates, times, or lists.
        // ( JSpinner.DateEditor ) A formatter/editor for JSpinner that controls how date/time 
        //   is displayed and edited inside the spinner.
        JSpinner.DateEditor editor = new JSpinner.DateEditor(jsDateAndTimeBook, "dd-MM-yyyy   hh:mm:ss a");
        
        jsDateAndTimeBook.setEditor(editor);
        
    }
    
    void refreshSpinnerToday(){
        jsToday.setModel(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(jsToday, "dd-MM-yyyy   hh:mm:ss a");
        
        jsToday.setEditor(editor);
    }
    
    
    class Car {
        String plate;
//        int floor;
//        int slot;
        boolean booked;         // booking or normal
        Date timeIn;
        // Date bookingDate;
        
        Date bookingStartDate;  // when reservation becomes active
        // boolean active;         // becomes true only on booking day
        
        
        public Car(String c_plate, boolean c_booked, Date c_timeIn, Date c_bookingStartDate){
            this.plate = c_plate;
//            this.floor = c_floor;
//            this.slot = c_slot;
            this.booked = c_booked;
            this.timeIn = c_timeIn;
            this.bookingStartDate = c_bookingStartDate;
            // this.active = false;
        }
    }
    
//    // 1D
//    Car[] parking = new Car[15];    // Data
//    JLabel[] slots = new JLabel[15];    // UI
    
    
    
    // 2D
    // DATA
    Car[][] parking = new Car[3][5]; // actual parking cars
    Car[][] booking = new Car[3][5]; // reservation system
    
    // UI
    JLabel[][] slots = new JLabel[3][5]; // UI Labels

    
    void searchCar(){
        String plate = jtxtPlateNumber.getText();
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++){
                
                if (parking[i][j] != null && parking[i][j].plate.equals(plate)){
                    slots[i][j].setBackground(Color.ORANGE);
                    
                    JOptionPane.showMessageDialog(null, "Found at Floor " + (i + 1) + " Slot " + (j + 1));
                    return;
                }
                
            }
        }
        JOptionPane.showMessageDialog(null,"Car not found!");
    }
    
    
    boolean plateExist(){
        String plate = jtxtPlateNumber.getText();
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++){
                
                if (parking[i][j] != null && parking[i][j].plate.equals(plate)){
                    // slots[i][j].setBackground(Color.ORANGE);
                    return true;
                }                
            }
        }
        return false;
    }
    
    
    boolean canPark(int floor, int slot, Date today){
        Car car = parking[floor][slot];
        
        if(car == null) return true;
        
        // if booking exists but not active yet -> allow normal parking
        if(car.booked && car.bookingStartDate.after(today)){
            return true;
        }
        return false;
    }
    
    void checkIn(){
                
        Date time = (Date) jsToday.getValue();
        
        int floor = Integer.parseInt(jtxtFloor.getText()) - 1;
        int slot = Integer.parseInt(jtxtPark.getText()) - 1;
        
        String plate = jtxtPlateNumber.getText();
        
        
        if (plateExist()) {
            JOptionPane.showMessageDialog(null, "This plate is already parked today.");
            return;
        }
        
        // booking check-in
        // if(booking[floor][slot] != null){
        if(booking[floor][slot] != null && isSameDay(booking[floor][slot].bookingStartDate, time)){
            Car b = booking[floor][slot];
            
            if(!b.plate.equals(plate)){
                JOptionPane.showMessageDialog(null, "Wrong and it is booking plate");
                return;
            }
            
            parking[floor][slot] = new Car(plate, true, time, b.bookingStartDate);
            
            booking[floor][slot] = null;
            
            updateSlot(floor, slot);
            
            JOptionPane.showMessageDialog(null, "Booking car checked in!");
            return;
        }
        
        // normal check-in
        if(parking[floor][slot] == null){
            parking[floor][slot] = new Car(plate, false, time, null);
            
            updateSlot(floor, slot);
            
            JOptionPane.showMessageDialog(null, "Check-In recorded!");
        } else {
            JOptionPane.showMessageDialog(null, "Slot already occupied!");
        }
        
        
//        if(!canPark(floor, slot, time)){
//            JOptionPane.showMessageDialog(null, "This slot is reserved for future booking!");
//            return;
//        }
//
//        if(parking[floor][slot] == null){
//            
//            String plate = jtxtPlateNumber.getText();
//            
//            parking[floor][slot] = new Car(plate, false, time, time);
//            
//            // slots[floor][slot].setText(plate);
//            slots[floor][slot].setBackground(Color.RED);
//            
//            JOptionPane.showMessageDialog(null, "Check-in recorded!");
//        } else {
//            JOptionPane.showMessageDialog(null, "Slot already occupied!");
//        }
        
        // jlblTime.setText(time.toString());
    }
    
    void checkOut(){
        
        String plate = jtxtCheckOutPlateNumber.getText();
        // Date timeOut = (Date) jsDateAndTime.getValue();
        Date timeOut = (Date) jsToday.getValue();
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++){
                
                if(parking[i][j] != null && parking[i][j].plate.equals(plate)){
                    
                    Car car = parking[i][j];
                    
                    // time difference
                    long diffMillis = timeOut.getTime() - car.timeIn.getTime();
                    
                    long hours = diffMillis / (1000 * 60 * 60);
                    
                    // long days = diffMillis / (1000 * 60 * 60 * 24);
                    
                    // if less than 1 hour -> still 1 hour charge
                    if(hours < 1){
                        hours = 0;
                        // hours = 1;
                    }
                    
                    // total Days
                    int totalDays = (int)Math.ceil(diffMillis / (1000.0 * 60 * 60 * 24));
                    
                    if(totalDays < 1){
                        totalDays = 1;
                    }
                    
                    // payment
                    int price = 0;
                    
                    // (int) is called a type cast (or type casting operator)
                    // (int) = convert a value into an integer type
                    // booking car
                    if(car.booked){
                        // booked already paid 20000r
                        if(totalDays == 1){
                            price = 0;
                        } else {
                            price = (totalDays - 1) * 20000;
                        }
                    } else {  // normal car
                        if(hours == 0){
                            price = 0;
                        } else {
                            price = (int) Math.min(hours * 1000, 20000);
                        }
                        // price = (hours == 0) ? 0 : (int)Math.min(hours * 1000, 20000);
                        // price = (int)Math.min(hours * 1000, 20000);
                    }
//                    // MAX CAP = 20000r
//                    if(price > 20000){
//                        price = 20000;
//                    }
                    
                    // Free SLOT
                    parking[i][j] = null;
                    
                    // slots[i][j].setText();
                    updateSlot(i, j);
                            
                    // slots[i][j].setBackground(myGreen);
                    
                    // jlblDate.setText(timeOut.toString());
                    
                    JOptionPane.showMessageDialog(null, "Plate: " + plate + "\nHours: " + hours + "\nTotal Payment: " + price + "៛");
                    
                    return;
                }
                
            }
        }
        JOptionPane.showMessageDialog(null, "Car not found!");
    }
    
    void booking() {
        String plate = jtxtPlateNumber.getText();
        
        int floor = Integer.parseInt(jtxtFloor.getText()) - 1;
        int slot = Integer.parseInt(jtxtPark.getText()) - 1;
        
        Date bookingDate = (Date) jsDateAndTimeBook.getValue();
        // Date bookingDate = (Date) jsToday.getValue();
        
        if(jtxtPlateNumber.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please input plate Number!");
            return;
        }
        
        // if(parking[floor][slot] != null){
        if(parking[floor][slot] != null && booking[floor][slot] != null){
            JOptionPane.showMessageDialog(null, "slot already occupied!");
            return;
        }
        
        if(booking[floor][slot] != null){
            JOptionPane.showMessageDialog(null, "Slot already booked!");
            return;
        }
        
        booking[floor][slot] = new Car(plate, true, bookingDate, bookingDate);
        
        JOptionPane.showMessageDialog(null, "Booking Successful! \n Fee: 20000៛");
        
        updateSlot(floor, slot);
            
            
//        if(parking[floor][slot] == null){
//            
//            parking[floor][slot] = new Car(plate, true, bookingDate, bookingDate);
//            
//            // slots[floor][slot].setText(plate);
//            slots[floor][slot].setBackground(Color.RED);
//            
//            // jlblTime.setText(timeIn.toString());
//            
//            JOptionPane.showMessageDialog(null, "Booking Successful!\nFee: 20000៛");
//        } else {
//            JOptionPane.showMessageDialog(null, "Slot already occupied!");
//        }
        
    }
    
    void cancelBooking() {
        
        String plate = jtxtCheckOutPlateNumber.getText();
        Date now = new Date();
        
        for(int i=0; i < 3; i++){
            for(int j=0; j < 5; j++){
            
                if(booking[i][j] != null && booking[i][j].plate.equals(plate)){
                    
                    Car car = booking[i][j];
                    
                    // only booking can cancel
                    if(!car.booked){
                        JOptionPane.showMessageDialog(null, "Normal parking cannot cancel!");
                        return;
                    }
                    
                    // today check if booking is today
                    if(isSameDay(car.bookingStartDate, now)){
                        JOptionPane.showMessageDialog(null,
                                "Cannot cancel booking for today!");
                        return;
                    }
//                    if( car.bookingStartDate.getDate() == now.getDate() &&
//                        car.bookingStartDate.getMonth() == now.getMonth() &&
//                        car.bookingStartDate.getYear() == now.getYear()){
//                        
//                        JOptionPane.showMessageDialog(null, "Cannot cancel booking for today!");
//                        return;
//                    }
                    
                    // parking[i][j] = null;
                    booking[i][j] = null;
                    
                    updateSlot(i, j);
                    
                    // slots[i][j].setText("");
                    // slots[i][j].setBackground(myGreen);
                    
                    JOptionPane.showMessageDialog(null, "Booking Cancelled!");
                    
                    return;
                }                
            }
        }
        JOptionPane.showMessageDialog(null, "Booking not found!");
    }
    
//    void refreshSpinner() {
//        java.util.Date now = new java.util.Date();
//        jSpinnerDateAndTime.setValue(now);
//    }
    
//    void parkCar(String plate, int floor, int slot) {
//        if(parking[floor][slot] == null){
//            parking[floor][slot] = new Car(plate, true);
//            slots[floor][slot].setText(plate);
//            slots[floor][slot].setBackground(Color.RED);
//        } else {
//            System.out.print("Already Occupied!");
//        }
//    }
    
//    void parkCarFromForm(){
//        String plate = 
//    }
//
//    void removeCar(int floor, int slot){
//        parking[floor][slot] = null;
//        
//        // slots[floor][slot].setText("");
//        slots[floor][slot].setBackground(myGreen);
//    }
    
    void updateSlot(int i, int j){
        
        // Date today = (Date) jsDateAndTime.getValue();
        Date today = (Date) jsToday.getValue();
        
        Car b = booking[i][j];
        Car p = parking[i][j];
        
        // parking car
        if(p != null){
            slots[i][j].setBackground(Color.RED);
            return;
        }
        
        // booking logic
        if(b != null){
            
            // booking is Today / Same Day
            if(isSameDay(b.bookingStartDate, today)){
                slots[i][j].setBackground(Color.YELLOW);
                return;
            }
            
            // booking Future
            if(b.bookingStartDate.after(today)){
                slots[i][j].setBackground(myGreen);
                return;
            }
            
            // Expired booking
            if(b.bookingStartDate.before(today) && b.bookingStartDate.before(today)){
                booking[i][j] = null;
                
                slots[i][j].setBackground(myGreen);
                return;
            }
            
//            if(isSameDay(b.bookingStartDate, today)){
//                slots[i][j].setBackground(Color.YELLOW);
//            } else if(b.bookingStartDate.after(today)){
//                slots[i][j].setBackground(myGreen);
//            }
//            return;
        }
        
        slots[i][j].setBackground(myGreen);
//        if(parking[i][j] != null){
//            slots[i][j].setBackground(Color.RED);
//            // slots[i][j].setText(parking[i][j].plate);
//        } else if(booking[i][j] != null){
//            slots[i][j].setBackground(Color.YELLOW);
//            // slots[i][j].setText("BOOKED");
//        } else {
//            slots[i][j].setBackground(myGreen);
//            // slots[i][j].setText("Empty");
//        }
    }
    
    void refreshAll(){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++){
                updateSlot(i,j);
            }
        }
    }
    
    boolean isSameDay(Date d1, Date d2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d1).equals(sdf.format(d2));
    }
    
    boolean findFreeSlot(){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++){
            
                if(parking[i][j] == null && booking[i][j] == null){
                    jtxtFloor.setText(String.valueOf(i + 1));
                    jtxtPark.setText(String.valueOf(j + 1));
                    
                    return true;
                }                
            }
        }
        JOptionPane.showMessageDialog(null, "No Free Slot!");        
        return false;
    }
    
    
    /**
     * Creates new form test_4_F_jf_Car_Parking
     */
    public test_4_F_jf_Car_Parking() {
        initComponents();
        refreshSpinnerToday();
        refreshSpinnerBooking();
        //jSpinner1.setVisible(false);
//        JFrame frame = new JFrame();
//        frame.setSize(714,436);
//        frame.setMinimumSize(new Dimension(714, 436));
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);

//        slots[0] = jlbParking1;
//        slots[1] = jlbParking2;
//        slots[2] = jlbParking3;        
//        slots[3] = jlbParking4;
//        slots[4] = jlbParking5;
//        slots[5] = jlbParking6;
//        slots[6] = jlbParking7;
//        slots[7] = jlbParking8;
//        slots[8] = jlbParking9;
//        slots[9] = jlbParking10;
//        slots[10] = jlbParking11;
//        slots[11] = jlbParking12;
//        slots[12] = jlbParking13;
//        slots[13] = jlbParking14;
//        slots[14] = jlbParking15;
//        
//        for (int i = 0; i < 15; i++) {
//            slots[i].setText("Empty");
//            slots[i].setBackground(myGreen);            
//        }

//        slots[0][0] = jlbParking1;
//        slots[0][1] = jlbParking2;
//        slots[0][2] = jlbParking3;
//        slots[0][3] = jlbParking4;
//        slots[0][4] = jlbParking5;
//
//        slots[1][0] = jlbParking6;
//        slots[1][1] = jlbParking7;
//        slots[1][2] = jlbParking8;
//        slots[1][3] = jlbParking9;
//        slots[1][4] = jlbParking10;
//
//        slots[2][0] = jlbParking11;
//        slots[2][1] = jlbParking12;
//        slots[2][2] = jlbParking13;
//        slots[2][3] = jlbParking14;
//        slots[2][4] = jlbParking15;

        JLabel[] floor1 = {jlbParking1, jlbParking2, jlbParking3, jlbParking4, jlbParking5};
        JLabel[] floor2 = {jlbParking6, jlbParking7, jlbParking8, jlbParking9, jlbParking10};
        JLabel[] floor3 = {jlbParking11, jlbParking12, jlbParking13, jlbParking14, jlbParking15};

        for (int i = 0; i < 5; i++) {
            slots[0][i] = floor1[i];
            slots[1][i] = floor2[i];
            slots[2][i] = floor3[i];
        }
        
        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 5; j++){
                // slots[i][j].setText();
                slots[i][j].setBackground(myGreen);
            }
        }
        
        // mouse listener
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++){
                
                final int floor = i;
                final int slot = j;
                
                slots[i][j].addMouseListener( new MouseAdapter(){
                        
                    @Override
                    public void mouseClicked(MouseEvent e){
                        // if(parking[floor][slot] != null){
                        if(parking[floor][slot] != null || booking[floor][slot] != null){
                            JOptionPane.showMessageDialog(null, "This slot is occupied!");
                            return;
                        }
                        
                        jtxtFloor.setText(String.valueOf(floor + 1));
                        jtxtPark.setText(String.valueOf(slot + 1));
                    }                        
                });                
            }
        }
        
        jbtBooking.addActionListener(e -> booking());
        jbtCheckIn.addActionListener(e -> checkIn());
        jbtCheckOut.addActionListener(e -> checkOut());
        jbtCancelBooking.addActionListener(e -> cancelBooking());
        // jbtRefreshSpinner.addActionListener(e -> refreshSpinner());
        
        refreshAll();
        
//        for(int i = 0; i < 3; i++){
//            for(int j = 0; i < 5; j++){
//                
//                final int floor = i;
//                final int slot = j;
//                
//                slots[i][j].addMouseListener(new MouseAdapter(){
//                    public void mouseClicked(MouseEvent e){
//                        if(parking[floor][slot] == null){
//                            String plate = 
//                        }
//                    }
//                });
//                
//            }
//        }
        
        // setSize(906, 436);
        // Dimension is used to store width and height together.
        setMinimumSize(new Dimension(906, 450));
        
        // used to control where a window (JFrame, JDialog) appears on the screen.
        setLocationRelativeTo(null); // set GUI to the middle of screen
        
        // FlowLayout is a layout manager in Java Swing that arranges components like words in a sentence.
        jpnCenterOfDateAndTime.setLayout(new FlowLayout(FlowLayout.CENTER));
        jpnCenterOfDateAndTime.add(jbtRefreshSpinnerBooking);
        jpnCenterOfDateAndTime.add(jsDateAndTimeBook);
        jpnCenterOfDateAndTime.add(jbtBooking);
        
        
        
//        jpnFloor1.setLayout(new FlowLayout(FlowLayout.CENTER));
//        
//        jlbParking1.setPreferredSize(new Dimension(100, 100));
//        jlbParking2.setPreferredSize(new Dimension(100, 100));
//        jlbParking3.setPreferredSize(new Dimension(100, 100));
//        jlbParking4.setPreferredSize(new Dimension(100, 100));
//        jlbParking5.setPreferredSize(new Dimension(100, 100));
//        
//        jpnFloor1.add(jlbParking1);
//        jpnFloor1.add(jlbParking2);
//        jpnFloor1.add(jlbParking3);
//        jpnFloor1.add(jlbParking4);
//        jpnFloor1.add(jlbParking5);
        
        
        
//        jpnCenterDuration.setLayout(new FlowLayout(FlowLayout.CENTER));
//        jpnCenterDuration.add(jblDuration);
        
//        jpnCenterAll.setLayout(new BorderLayout());
//        jpnCenterAll.add(jpnCenterOfDateAndTime, BorderLayout.CENTER);
//        jpnCenterAll.add(jpnCenterDuration, BorderLayout.SOUTH);
        
        
        

        LocalDate currentDate = LocalDate.now();
        // EEE = Mon/Tue/Wed/...
        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("EEE, dd-MMM-yyyy");
        jlblDate.setText("Date : " + currentDate.format(dataFormatter));
        
        ActionListener actionListenerTime= new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                LocalTime currentTime = LocalTime.now();
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                jlblTime.setText("Time : " + currentTime.format(timeFormatter));
                
                // auto refresh parking
                refreshAll(); // refreshParkingState();
                
            }
        };
        
        // new usually use clss and constructor
        Timer timer = new Timer( 1000, actionListenerTime);
        
        timer.start();

        
        
        
        
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlblTime = new javax.swing.JLabel();
        jlblDate = new javax.swing.JLabel();
        jpnCenterOfDateAndTime = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jsDateAndTimeBook = new javax.swing.JSpinner();
        jbtBooking = new javax.swing.JButton();
        jbtRefreshSpinnerBooking = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtxtPlateNumber = new javax.swing.JTextField();
        jtxtFloor = new javax.swing.JTextField();
        jtxtPark = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jbtCheckIn = new javax.swing.JButton();
        jbtCheckOut = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jtxtCheckOutPlateNumber = new javax.swing.JTextField();
        jpnFloor1 = new javax.swing.JPanel();
        jlbParking1 = new javax.swing.JLabel();
        jlbParking2 = new javax.swing.JLabel();
        jlbParking3 = new javax.swing.JLabel();
        jlbParking5 = new javax.swing.JLabel();
        jlbParking4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jpnFloor3 = new javax.swing.JPanel();
        jlbParking6 = new javax.swing.JLabel();
        jlbParking7 = new javax.swing.JLabel();
        jlbParking8 = new javax.swing.JLabel();
        jlbParking10 = new javax.swing.JLabel();
        jlbParking9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpnFloor4 = new javax.swing.JPanel();
        jlbParking11 = new javax.swing.JLabel();
        jlbParking12 = new javax.swing.JLabel();
        jlbParking13 = new javax.swing.JLabel();
        jlbParking15 = new javax.swing.JLabel();
        jlbParking14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jbtCancelBooking = new javax.swing.JButton();
        jsToday = new javax.swing.JSpinner();
        jbtRefreshToday = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jlblTime.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jlblDate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jlblDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jsDateAndTimeBook.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jbtBooking.setText("Booking");
        jbtBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBookingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCenterLayout = new javax.swing.GroupLayout(jPanelCenter);
        jPanelCenter.setLayout(jPanelCenterLayout);
        jPanelCenterLayout.setHorizontalGroup(
            jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCenterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jsDateAndTimeBook, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelCenterLayout.setVerticalGroup(
            jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCenterLayout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addGroup(jPanelCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jsDateAndTimeBook, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtBooking, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)))
        );

        jbtRefreshSpinnerBooking.setText("Refresh");
        jbtRefreshSpinnerBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRefreshSpinnerBookingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpnCenterOfDateAndTimeLayout = new javax.swing.GroupLayout(jpnCenterOfDateAndTime);
        jpnCenterOfDateAndTime.setLayout(jpnCenterOfDateAndTimeLayout);
        jpnCenterOfDateAndTimeLayout.setHorizontalGroup(
            jpnCenterOfDateAndTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnCenterOfDateAndTimeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtRefreshSpinnerBooking)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelCenter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(26, 26, 26))
        );
        jpnCenterOfDateAndTimeLayout.setVerticalGroup(
            jpnCenterOfDateAndTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelCenter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnCenterOfDateAndTimeLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jbtRefreshSpinnerBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Plate Number : ");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Floor : ");

        jtxtPlateNumber.setColumns(15);
        jtxtPlateNumber.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jtxtPlateNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtPlateNumberActionPerformed(evt);
            }
        });

        jtxtFloor.setColumns(5);
        jtxtFloor.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jtxtFloor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtFloorActionPerformed(evt);
            }
        });

        jtxtPark.setColumns(5);
        jtxtPark.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jtxtPark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtParkActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Park : ");

        jbtCheckIn.setBackground(new java.awt.Color(255, 153, 51));
        jbtCheckIn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtCheckIn.setForeground(new java.awt.Color(255, 255, 255));
        jbtCheckIn.setText("Check In");
        jbtCheckIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtCheckInActionPerformed(evt);
            }
        });

        jbtCheckOut.setBackground(new java.awt.Color(0, 204, 0));
        jbtCheckOut.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtCheckOut.setForeground(new java.awt.Color(255, 255, 255));
        jbtCheckOut.setText("Check Out");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Check Out Plate Number/ID : ");

        jtxtCheckOutPlateNumber.setColumns(15);
        jtxtCheckOutPlateNumber.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jtxtCheckOutPlateNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCheckOutPlateNumberActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtCheckOutPlateNumber))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCheckOutPlateNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jpnFloor1.setBackground(new java.awt.Color(183, 189, 195));

        jlbParking1.setBackground(new java.awt.Color(136, 255, 136));
        jlbParking1.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking1.setText("P1");
        jlbParking1.setOpaque(true);
        jlbParking1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking1MouseClicked(evt);
            }
        });

        jlbParking2.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking2.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking2.setText("P2");
        jlbParking2.setOpaque(true);
        jlbParking2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking2MouseClicked(evt);
            }
        });

        jlbParking3.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking3.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking3.setText("P3");
        jlbParking3.setOpaque(true);
        jlbParking3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking2MouseClicked(evt);
            }
        });

        jlbParking5.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking5.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking5.setText("P5");
        jlbParking5.setOpaque(true);
        jlbParking5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking5MouseClicked(evt);
            }
        });

        jlbParking4.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking4.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking4.setText("P4");
        jlbParking4.setOpaque(true);
        jlbParking4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking2MouseClicked(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(114, 145, 218));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Floor 1");
        jLabel5.setOpaque(true);

        javax.swing.GroupLayout jpnFloor1Layout = new javax.swing.GroupLayout(jpnFloor1);
        jpnFloor1.setLayout(jpnFloor1Layout);
        jpnFloor1Layout.setHorizontalGroup(
            jpnFloor1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFloor1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(jlbParking1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jlbParking2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(jlbParking3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jlbParking4, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jlbParking5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnFloor1Layout.setVerticalGroup(
            jpnFloor1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFloor1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnFloor1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnFloor1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlbParking1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpnFloor3.setBackground(new java.awt.Color(183, 189, 195));

        jlbParking6.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking6.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking6.setText("P1");
        jlbParking6.setOpaque(true);
        jlbParking6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking6MouseClicked(evt);
            }
        });

        jlbParking7.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking7.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking7.setText("P2");
        jlbParking7.setOpaque(true);
        jlbParking7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking6MouseClicked(evt);
            }
        });

        jlbParking8.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking8.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking8.setText("P3");
        jlbParking8.setOpaque(true);
        jlbParking8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking6MouseClicked(evt);
            }
        });

        jlbParking10.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking10.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking10.setText("P5");
        jlbParking10.setOpaque(true);
        jlbParking10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking6MouseClicked(evt);
            }
        });

        jlbParking9.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking9.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking9.setText("P4");
        jlbParking9.setOpaque(true);
        jlbParking9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking6MouseClicked(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(114, 145, 218));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Floor 2");
        jLabel7.setOpaque(true);

        javax.swing.GroupLayout jpnFloor3Layout = new javax.swing.GroupLayout(jpnFloor3);
        jpnFloor3.setLayout(jpnFloor3Layout);
        jpnFloor3Layout.setHorizontalGroup(
            jpnFloor3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFloor3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(jlbParking6, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jlbParking7, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(jlbParking8, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jlbParking9, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jlbParking10, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnFloor3Layout.setVerticalGroup(
            jpnFloor3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFloor3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnFloor3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnFloor3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlbParking6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking8, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking9, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking10, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpnFloor4.setBackground(new java.awt.Color(183, 189, 195));

        jlbParking11.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking11.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking11.setText("P1");
        jlbParking11.setOpaque(true);
        jlbParking11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking11MouseClicked(evt);
            }
        });

        jlbParking12.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking12.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking12.setText("P2");
        jlbParking12.setOpaque(true);
        jlbParking12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking11MouseClicked(evt);
            }
        });

        jlbParking13.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking13.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking13.setText("P3");
        jlbParking13.setOpaque(true);
        jlbParking13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking11MouseClicked(evt);
            }
        });

        jlbParking15.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking15.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking15.setText("P5");
        jlbParking15.setOpaque(true);
        jlbParking15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking11MouseClicked(evt);
            }
        });

        jlbParking14.setBackground(new java.awt.Color(0, 255, 0));
        jlbParking14.setFont(new java.awt.Font("Segoe UI", 1, 56)); // NOI18N
        jlbParking14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbParking14.setText("P4");
        jlbParking14.setOpaque(true);
        jlbParking14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbParking11MouseClicked(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(114, 145, 218));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Floor 3");
        jLabel8.setOpaque(true);

        javax.swing.GroupLayout jpnFloor4Layout = new javax.swing.GroupLayout(jpnFloor4);
        jpnFloor4.setLayout(jpnFloor4Layout);
        jpnFloor4Layout.setHorizontalGroup(
            jpnFloor4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFloor4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(jlbParking11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jlbParking12, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(jlbParking13, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jlbParking14, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jlbParking15, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnFloor4Layout.setVerticalGroup(
            jpnFloor4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnFloor4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnFloor4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnFloor4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlbParking11, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking12, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking13, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking14, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlbParking15, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jbtCancelBooking.setText("Cancel Booking");
        jbtCancelBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtCancelBookingActionPerformed(evt);
            }
        });

        jbtRefreshToday.setText("Refresh");
        jbtRefreshToday.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRefreshTodayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jpnCenterOfDateAndTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(7, 7, 7))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jtxtPlateNumber)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jtxtFloor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jtxtPark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtCheckIn)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jsToday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtRefreshToday)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbtCancelBooking)
                                .addGap(18, 18, 18)
                                .addComponent(jbtCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(20, 20, 20))
            .addComponent(jpnFloor1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpnFloor3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpnFloor4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpnCenterOfDateAndTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jtxtPlateNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jtxtFloor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jtxtPark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jbtCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtCancelBooking)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jsToday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtRefreshToday))
                                .addGap(6, 6, 6))))
                    .addComponent(jbtCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpnFloor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpnFloor3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpnFloor4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtRefreshSpinnerBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRefreshSpinnerBookingActionPerformed
        // TODO add your handling code here:
        refreshSpinnerBooking();
    }//GEN-LAST:event_jbtRefreshSpinnerBookingActionPerformed

    private void jbtBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBookingActionPerformed
        // TODO add your handling code here:        
        
    }//GEN-LAST:event_jbtBookingActionPerformed

    private void jtxtPlateNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtPlateNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtPlateNumberActionPerformed

    private void jtxtFloorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtFloorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtFloorActionPerformed

    private void jtxtParkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtParkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtParkActionPerformed

    private void jtxtCheckOutPlateNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCheckOutPlateNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtCheckOutPlateNumberActionPerformed

    private void jbtCheckInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtCheckInActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbtCheckInActionPerformed

    private void jlbParking1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlbParking1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jlbParking1MouseClicked

    private void jlbParking2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlbParking2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jlbParking2MouseClicked

    private void jlbParking5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlbParking5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jlbParking5MouseClicked

    private void jlbParking6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlbParking6MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jlbParking6MouseClicked

    private void jlbParking11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlbParking11MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jlbParking11MouseClicked

    private void jbtCancelBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtCancelBookingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbtCancelBookingActionPerformed

    private void jbtRefreshTodayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRefreshTodayActionPerformed
        // TODO add your handling code here:
        refreshSpinnerToday();
    }//GEN-LAST:event_jbtRefreshTodayActionPerformed

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
            java.util.logging.Logger.getLogger(test_4_F_jf_Car_Parking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(test_4_F_jf_Car_Parking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(test_4_F_jf_Car_Parking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(test_4_F_jf_Car_Parking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>        
        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new test_4_F_jf_Car_Parking().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JButton jbtBooking;
    private javax.swing.JButton jbtCancelBooking;
    private javax.swing.JButton jbtCheckIn;
    private javax.swing.JButton jbtCheckOut;
    private javax.swing.JButton jbtRefreshSpinnerBooking;
    private javax.swing.JButton jbtRefreshToday;
    private javax.swing.JLabel jlbParking1;
    private javax.swing.JLabel jlbParking10;
    private javax.swing.JLabel jlbParking11;
    private javax.swing.JLabel jlbParking12;
    private javax.swing.JLabel jlbParking13;
    private javax.swing.JLabel jlbParking14;
    private javax.swing.JLabel jlbParking15;
    private javax.swing.JLabel jlbParking2;
    private javax.swing.JLabel jlbParking3;
    private javax.swing.JLabel jlbParking4;
    private javax.swing.JLabel jlbParking5;
    private javax.swing.JLabel jlbParking6;
    private javax.swing.JLabel jlbParking7;
    private javax.swing.JLabel jlbParking8;
    private javax.swing.JLabel jlbParking9;
    private javax.swing.JLabel jlblDate;
    private javax.swing.JLabel jlblTime;
    private javax.swing.JPanel jpnCenterOfDateAndTime;
    private javax.swing.JPanel jpnFloor1;
    private javax.swing.JPanel jpnFloor3;
    private javax.swing.JPanel jpnFloor4;
    private javax.swing.JSpinner jsDateAndTimeBook;
    private javax.swing.JSpinner jsToday;
    private javax.swing.JTextField jtxtCheckOutPlateNumber;
    private javax.swing.JTextField jtxtFloor;
    private javax.swing.JTextField jtxtPark;
    private javax.swing.JTextField jtxtPlateNumber;
    // End of variables declaration//GEN-END:variables
}
