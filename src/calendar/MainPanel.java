
//-*- mode:java; encoding:utf-8 -*-
//vim:set fileencoding=utf-8:
//@homepage@

package calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public final class MainPanel extends JPanel {
public final LocalDate realLocalDate = LocalDate.now(ZoneId.systemDefault());
private final JLabel dateLabel = new JLabel("あ", SwingConstants.CENTER);	//ここでリンク張れる　ラベルを変える
private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
private final JTable monthTable = new JTable();
private LocalDate currentLocalDate;

public LocalDate getCurrentLocalDate() {
 return currentLocalDate;
}

private MainPanel() {
 super(new BorderLayout());

 monthTable.setDefaultRenderer(LocalDate.class, new CalendarTableRenderer());
 monthTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 monthTable.setCellSelectionEnabled(true);
 monthTable.setRowHeight(20);
 monthTable.setFillsViewportHeight(true);

 JTableHeader header = monthTable.getTableHeader();
 header.setResizingAllowed(false);
 header.setReorderingAllowed(false);
 ((JLabel) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

 ListSelectionListener selectionListener = e -> {
   if (!e.getValueIsAdjusting()) {
     LocalDate ld = (LocalDate) monthTable.getValueAt(monthTable.getSelectedRow(), monthTable.getSelectedColumn());
     dateLabel.setText(ld.toString());
   }
 };
 monthTable.getSelectionModel().addListSelectionListener(selectionListener);
 monthTable.getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);

 updateMonthView(realLocalDate);

 JButton prev = new JButton("<");
 prev.addActionListener(e -> updateMonthView(getCurrentLocalDate().minusMonths(1)));

 JButton next = new JButton(">");
 next.addActionListener(e -> updateMonthView(getCurrentLocalDate().plusMonths(1)));

 JPanel p = new JPanel(new BorderLayout());
 p.add(monthLabel);
 p.add(prev, BorderLayout.WEST);
 p.add(next, BorderLayout.EAST);

 add(p, BorderLayout.NORTH);
 add(new JScrollPane(monthTable));
 add(dateLabel, BorderLayout.SOUTH);
 setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
 setPreferredSize(new Dimension(320, 240));
}

public void updateMonthView(LocalDate localDate) {
 currentLocalDate = localDate;
 monthLabel.setText(localDate.format(DateTimeFormatter.ofPattern("yyyy / MM").withLocale(Locale.getDefault())));
 monthTable.setModel(new CalendarViewTableModel(localDate));
}

private class CalendarTableRenderer extends DefaultTableCellRenderer {
 @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
   super.getTableCellRendererComponent(table, value, selected, focused, row, column);
   setHorizontalAlignment(SwingConstants.CENTER);
   if (value instanceof LocalDate) {
     LocalDate d = (LocalDate) value;
     setText(Objects.toString(d.getDayOfMonth()));
     if (YearMonth.from(d).equals(YearMonth.from(getCurrentLocalDate()))) {
       setForeground(Color.BLACK);
     } else {
       setForeground(Color.GRAY);
     }
     if (d.isEqual(realLocalDate)) {
       setBackground(new Color(0xDC_FF_DC));
     } else {
       setBackground(getDayOfWeekColor(d.getDayOfWeek()));
     }
   }
   return this;
 }

 private Color getDayOfWeekColor(DayOfWeek dow) {
   switch (dow) {
     case SUNDAY: return new Color(0xFF_DC_DC);
     case SATURDAY: return new Color(0xDC_DC_FF);
     default: return Color.WHITE;
   }
 }
}

public static void main(String[] args) {
 EventQueue.invokeLater(MainPanel::createAndShowGui);
}

private static void createAndShowGui() {
 try {
   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
 } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
   ex.printStackTrace();
   Toolkit.getDefaultToolkit().beep();
 }
 JFrame frame = new JFrame("@title@");
 frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
 frame.getContentPane().add(new MainPanel());
 frame.pack();
 frame.setLocationRelativeTo(null);
 frame.setVisible(true);
}
}

class CalendarViewTableModel extends DefaultTableModel {
private final LocalDate startDate;
private final WeekFields weekFields = WeekFields.of(Locale.getDefault());

protected CalendarViewTableModel(LocalDate date) {
 super();
 LocalDate firstDayOfMonth = YearMonth.from(date).atDay(1); // date.with(TemporalAdjusters.firstDayOfMonth());
 // int v = firstDayOfMonth.get(WeekFields.SUNDAY_START.dayOfWeek()) - 1;
 int v = firstDayOfMonth.get(weekFields.dayOfWeek()) - 1;
 startDate = firstDayOfMonth.minusDays(v);
}

@Override public Class<?> getColumnClass(int column) {
 return LocalDate.class;
}

@Override public String getColumnName(int column) {
 return weekFields.getFirstDayOfWeek().plus(column)
   .getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
}

@Override public int getRowCount() {
 return 6;
}

@Override public int getColumnCount() {
 return 7;
}

@Override public Object getValueAt(int row, int column) {
 return startDate.plusDays((long) row * getColumnCount() + column);
}

@Override public boolean isCellEditable(int row, int column) {
 return false;
}
}
