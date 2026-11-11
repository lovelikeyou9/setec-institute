/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package test_4_f;

// =========================================

/*
working with time only
| Use                | Example              |
| ------------------ | -------------------- |
| Show current time  | LocalTime.now()      |
| Store time         | LocalTime time = ... |
| Add time           | plusHours(2)         |
| Subtract time      | minusMinutes(30)     |
| Compare time       | time1.isAfter(time2) |
| Parse text to time | LocalTime.parse(...) |
| Format time        | format(...)          |

- Core methods
now()
of()
plusHours()
plusMinutes()
minusMinutes()
minusHours()

- Compare
isBefore()
isAfter()
equals()

- Convert
parse()
format()

- Get values
getHour()
getMinute()
getSecond()

- Constants
MIN
MAX
MIDNIGHT
NOON
*/
import java.time.LocalTime;

/*
working with date only.
| Use                  | Example           |
| -------------------- | ----------------- |
| Get current date     | LocalDate.now()   |
| Store a date         | LocalDate.of(...) |
| Add days             | plusDays(1)       |
| Subtract months      | minusMonths(1)    |
| Compare dates        | isAfter()         |
| Parse String to date | LocalDate.parse() |
- now()
- of()
- plusHours()
- plusMinutes()
- minusMinutes()
- minusHours()
- isBefore()
- isAfter()
- equals()
- parse()
- format()
- getHour()
- getMinute()
- getSecond()
- MIN / MAX
- MIDNIGHT / NOON
*/
import java.time.LocalDate;

/*
working with both date + time together
| Class         | Meaning     |
| ------------- | ----------- |
| LocalDate     | Date only   |
| LocalTime     | Time only   |
| LocalDateTime | Date + Time |

1. Create specific date + time
LocalDateTime.of(2026, 5, 15, 10, 30);

2. Get parts (very important)
dt.getYear();
dt.getMonth();
dt.getDayOfMonth();
dt.getHour();
dt.getMinute();
dt.getSecond();

3. Add / subtract time
dt.plusHours(2);
dt.plusDays(1);

dt.minusMinutes(30);
dt.minusMonths(1);

4. Compare date-time
dt1.isAfter(dt2);
dt1.isBefore(dt2);

5. Equality check
dt1.equals(dt2);

6. Format DateTime → String
dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

7. Parse String → DateTime
LocalDateTime.parse("2026-05-15 10:30", formatter);
*/
import java.time.LocalDateTime;

/*
Duration = measure time difference (between two time points)

1. Create Duration
Duration d1 = Duration.ofHours(2);
Duration d2 = Duration.ofMinutes(30);
Duration d3 = Duration.ofSeconds(10);

2. Calculate difference
Duration d = Duration.between(time1, time2);

used for:
LocalTime
LocalDateTime

3. Convert Duration
d.toHours();
d.toMinutes();
d.toSeconds();

4. Add time to Duration
d.plusHours(1);
d.plusMinutes(15);
d.plusSeconds(10);

5. Subtract time
d.minusHours(1);
d.minusMinutes(10);

6. Get exact value
d.toMillis();   // milliseconds
d.toNanos();    // nanoseconds

7. Compare Duration
d1.equals(d2);
d1.compareTo(d2);

8. Check if negative
d.isNegative();
d.isZero();
*/
import java.time.Duration;

/*
It is used to convert date/time ↔ text (String)
1. Format (Date → String)

2. Parse (String → Date/Time)

3. Define patterns (FORMAT STYLE)
Common patterns:
yyyy = year
MM   = month
dd   = day
HH   = 24-hour
hh   = 12-hour
mm   = minutes
ss   = seconds
a    = AM/PM

4. Used with different classes
✔ LocalDate
✔ LocalTime
✔ LocalDateTime

6. Real use in apps
✔ Show time in JLabel
✔ Read input from JTextField
✔ Save formatted date in database
✔ Receipt / invoice printing
✔ Logs (login time, exit time)
*/
import java.time.format.DateTimeFormatter;

/*
It is used to show popup dialog boxes in Java Swing.
1. Show message (information / alert)
✔ Used for:
success message
error message
info popup

2. show Message with type (error / warning / info)
Types:
INFORMATION_MESSAGE
WARNING_MESSAGE
ERROR_MESSAGE
QUESTION_MESSAGE

3. Input dialog (get user input)
✔ Used for:
login
simple forms
quick input

4. Confirm dialog (YES / NO / CANCEL)
✔ Returns:
YES = 0
NO = 1
CANCEL = 2

5. Custom buttons (Option dialog)
✔ Used for:
custom UI buttons
advanced dialogs

6. Set parent window (important)
✔ Instead of null, you can use:
rootPane
this
👉 Keeps dialog centered on your form

7. Show icon types
JOptionPane.ERROR_MESSAGE
JOptionPane.INFORMATION_MESSAGE
JOptionPane.WARNING_MESSAGE
JOptionPane.QUESTION_MESSAGE

🔥 REAL USE IN PROJECTS

✔ Login system
✔ Save confirmation
✔ Error handling
✔ Delete confirmation
✔ Input forms
✔ Alerts in apps

⭐ FINAL MEMORY
JOptionPane = quick popup UI tool
It can:
- show message
- get input
- confirm yes/no
- create custom buttons
- show errors/warnings/info
*/
import javax.swing.JOptionPane;

/*
It is used to run code repeatedly after a time delay
1. Run code every X milliseconds
✔ 1000 ms = 1 second

2. Update real-time clock (VERY COMMON)
✔ Used in:
digital clock
GUI time display

3. Auto refresh UI
✔ Used for:
refresh table
update database view
live data

4. Stop timer
✔ Used when:
closing form
stopping updates

5. Restart timer
timer.restart();

6. Change delay (speed)
timer.setDelay(500);
✔ 500 ms = faster updates

7. Basic structure
new Timer(delay, ActionListener)
delay = milliseconds
ActionListener = what runs every time

Timer = runs code automatically every X time

🔥 REAL USES
✔ Digital clock
✔ Auto refresh table
✔ Game animation
✔ Live updates
✔ Countdown timer

⭐ FINAL SUMMARY
Timer can:
- run code repeatedly
- update UI automatically
- refresh data
- act like a clock
- start / stop / restart
*/
import javax.swing.Timer;

/*
ActionEvent = object that represents an action happened in GUI
1. Detect button click
✔ ActionEvent carries the event info

2. Get event source (who triggered it)
✔ tells you:
button clicked
menu clicked
text field action

3. Get command name
✔ returns button text or command name

4. Get event time
✔ time when event happened

5. Get modifiers (keys pressed)
✔ checks:
Ctrl
Shift
Alt

6. Event ID (type of action)
int id = e.getID();

7. Used with ActionListener
This is where ActionEvent is used most

🔥 REAL USES
✔ Button click
✔ Menu click
✔ Enter key pressed
✔ Form submit
✔ Timer events (Swing Timer)

⭐ FINAL SUMMARY
ActionEvent can:
- tell what was clicked (getSource)
- get button name (getActionCommand)
- get time of event (getWhen)
- detect keyboard modifiers
- identify event type (getID)
*/
import java.awt.event.ActionEvent;

/*
ActionListener = an interface that listens for actions (events) in GUI

1. Detect button clicks
✔ This is the most common use

2. Required method
When you use ActionListener, you MUST implement:
👉 This method runs when action happens

3. Works with many components
✔ JButton
✔ JMenuItem
✔ Timer
✔ JTextField (Enter key)

4. Used with Timer

5. Can detect different actions

6. Lambda version (short way)

🔥 REAL USES
✔ Button click events
✔ Submit forms
✔ Menu actions
✔ Timer updates
✔ Input handling

⭐ FINAL SUMMARY
ActionListener can:
- listen to button clicks
- run code when action happens
- work with Timer and GUI components
- use actionPerformed() method
*/
import java.awt.event.ActionListener;

// =========================================

/**
 *
 * @author Admin
 */
public class test_4_F_jf_testing extends javax.swing.JFrame {

    /**
     * Creates new form test_4_F_jf_testing
     */
    
    public test_4_F_jf_testing() {
        initComponents();
        
//        LocalDate currentDate = LocalDate.now();
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
//        jlblDate.setText("Date: " + currentDate.format(dateFormatter));
//
//        // Timer to update time every second
//        Timer timer = new Timer(1000, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                LocalTime currentTime = LocalTime.now();
//                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//                jlblTime.setText("Time: " + currentTime.format(timeFormatter));
//            }
//        });
//        
//        timer.start();

        // LocalDate.now() takes time from your COMPUTER (system clock), NOT internet
        LocalDate currentDate = LocalDate.now();
        
        // DateTimeFormatter.ofPattern() is for formatter date to text
        // but it does not have data of date yet
        // d   = 1 digit day (5)
        // dd  = 2 digit day (05)
        // M    = 1–12
        // MM   = 01–12
        // MMM  = Jan, Feb, May
        // MMMM = January, February
        // yy   = 26
        // yyyy = 2026
        // H  = 0–23
        // HH = 00–23
        // h  = 1–12
        // hh = 01–12
        // mm = minutes
        // ss = seconds
        // a  = AM/PM
        // "dd/MM/yyyy" | "yyyy-MM-dd" |"dd MMM yyyy" | "dd MMMM yyyy" | "HH:mm:ss" "hh:mm a"
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        // currentDate.format(dateFormatter) it is real format date
        // it is used to convert time/clock into formatted text.
        jlblDate.setText("Date: " + currentDate.format(dateFormatter));

        // Timer to update time every second
        // It is used to run code repeatedly after a time delay
        // Timer is used to run code every 1 second (1000 milliseconds). 
        // ActionListener() is used to create a listener that reacts when the Timer fires (every 1 second)
        // and it used to help to create the code that will run every time the Timer 
        // fires(every 1 second of 1000ms)
        Timer timer = new Timer(1000, new ActionListener() { // It will execute the code inside it every 1 second (1000 ms).
            @Override
            // ActionEvent is used to carry information about the action that happened
            // ActionEvent helps to receive input (event information
            // ActionEvent is used to receive information about the (action event)/(event information)
            // Button click, Timer tick, Menu click, JTextField (press Enter),
            // JComboBox (drop-down select), JCheckBox (sometimes when toggled),
            // JRadioButton, JMenu (sometimes with sub menu actions)
            // ❌ JMenu itself does NOT fire ActionEvent
            // ✔ Only JMenuItem fires ActionEvent
            // ❌ Not all GUI components use ActionEvent (some use ItemEvent, MouseEvent, KeyEvent)
            public void actionPerformed(ActionEvent e) {
                LocalTime currentTime = LocalTime.now();
                // DateTimeFormatter.ofPattern() is used to define a format for converting time/clock into text.
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                // currentTime.format(timeFormatter) is used to convert time/clock into formatted text.
                jlblTime.setText("Time: " + currentTime.format(timeFormatter));
            }
        });
        
        // it is call to right process
        // or it is used to start the Timer process
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

        jtxtIn = new javax.swing.JTextField();
        jlblDate = new javax.swing.JLabel();
        jlblTime = new javax.swing.JLabel();
        jtxtOut = new javax.swing.JTextField();
        jbtOut = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jtxtIn.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jtxtIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtInActionPerformed(evt);
            }
        });

        jlblDate.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        jlblTime.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        jtxtOut.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        jbtOut.setText("Out");
        jbtOut.setPreferredSize(new java.awt.Dimension(72, 72));
        jbtOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtOutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtIn, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtxtOut, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(jbtOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 42, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jtxtIn, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtOut, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                    .addComponent(jtxtOut, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtInActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtInActionPerformed

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private void jbtOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtOutActionPerformed
        // TODO add your handling code here:
//        String entryInput = jtxtIn.getText();
//        LocalDateTime entryTime = LocalDateTime.parse(entryInput, formatter);
//        String exitInput = jtxtOut.getText();
//        LocalDateTime exitTime = LocalDateTime.parse(exitInput, formatter);
//        Duration duration = Duration.between(entryTime, exitTime);
//        long minutes = duration.toMinutes();
//        double hours = Math.ceil(minutes / 60.0);
//        JOptionPane.showMessageDialog(rootPane, hours + "");


        String entryInput = jtxtIn.getText();
        // LocalDateTime is used to handle/store date + time together at the same time
        // .parse() is from LocalDateTime
        // .parse() is used to convert formatted text into a date-time object.
        LocalDateTime entryTime = LocalDateTime.parse(entryInput, formatter);
        
        String exitInput = jtxtOut.getText();
        LocalDateTime exitTime = LocalDateTime.parse(exitInput, formatter);
        
        // Duration is used to measure/calculate the amount of time between two times or date-times
        // between() is used to calculate the difference between two times/date-times
        // LocalTime start = LocalTime.of(8, 0); // 08:00
        // LocalTime end = LocalTime.of(10, 30); // 10:30
        // Duration d = Duration.between(start, end); 10:30 - 08:00 = = 2 hours 30 minutes
        // d.toHours();   // 2
        // d.toMinutes(); // 150    2 hours = 120 minutes, 30 minutes = 30 minutes, 120 + 30 = 150
        Duration duration = Duration.between(entryTime, exitTime);
        
        // long is whole numbers only, long like int but bigger
        // double is decimal numbers
        // toMinutes() convert time to minute
        long minutes = duration.toMinutes();
        // Math.ceil() rounds UP to the next whole number.
        // Math.ceil(2.1) -> 3.0
        // Math.ceil(5.0) -> 5.0
        double hours = Math.ceil(minutes / 60.0);
        
        // JOptionPane for show pop out
        // showMessageDialog is used to show text in a dialog box by using JOptionPan
        // or showMessageDialog() is used to display a message in a popup dialog box using JOptionPane.
        // rootPane is the main container inside a JFrame
        // rootPane is used as the parent component
        // rootPane → centers the popup on your window
        // NO rootPane: JOptionPane.showMessageDialog(null, "Hello World");
        // A popup appears, but:
        // may not be centered on your window
        // can appear in random screen position
        JOptionPane.showMessageDialog(rootPane, hours + "");
        
    }//GEN-LAST:event_jbtOutActionPerformed

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
            java.util.logging.Logger.getLogger(test_4_F_jf_testing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(test_4_F_jf_testing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(test_4_F_jf_testing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(test_4_F_jf_testing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new test_4_F_jf_testing().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbtOut;
    private javax.swing.JLabel jlblDate;
    private javax.swing.JLabel jlblTime;
    private javax.swing.JTextField jtxtIn;
    private javax.swing.JTextField jtxtOut;
    // End of variables declaration//GEN-END:variables
}
