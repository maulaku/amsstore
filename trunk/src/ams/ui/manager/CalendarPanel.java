package ams.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ams.ui.time.Day;
import ams.ui.time.DayOfWeek;
import ams.ui.time.Month;

public class CalendarPanel extends JPanel implements ActionListener {
	
	private Day lastDisplayed;
	private Calendar calendar;
	
	private JTable table;
	private DefaultTableModel tableModel;
	private JLabel monthLabel;

	private final int CELL_HEIGHT = 17;

	public CalendarPanel()
	{
		setLayout(new BorderLayout());
		initComponents();
	}
	
	private void initComponents()
	{
		table = new JTable(new Object[0][0], DayOfWeek.SHORT_FORM) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
			@Override
			public boolean isCellSelected(int row, int column)
			{
				if (table.getValueAt(row, column) == null)
					return false;
				return super.isCellSelected(row, column);
			}
		};
		
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		tableModel = new DefaultTableModel();
		table.setModel(tableModel);
		table.setRowHeight(CELL_HEIGHT);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setFocusable(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		SelectionListener selectionListener = new SelectionListener();
		table.getSelectionModel().addListSelectionListener(selectionListener);
		table.getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
		table.setDefaultRenderer(Object.class, new DayRenderer());
		JScrollPane tablePane = new JScrollPane(table);
	
		JButton left = new JButton("<");
		left.setActionCommand("previous");
		left.addActionListener(this);
		JButton right = new JButton(">");
		right.setActionCommand("next");
		right.addActionListener(this);
		
		monthLabel = new JLabel();
		monthLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel monthNavigator = new JPanel(new BorderLayout());
		monthNavigator.setBackground(Color.WHITE);
		monthNavigator.add(left, BorderLayout.WEST);
		monthNavigator.add(monthLabel, BorderLayout.CENTER);
		monthNavigator.add(right, BorderLayout.EAST);

		
		add(tablePane, BorderLayout.CENTER);
		add(monthNavigator, BorderLayout.NORTH);
	}
	
	public void setCalendar(Calendar calendar)
	{		
		this.calendar = calendar;
	}
	
	public void updateDisplay()
	{
		Day date = new Day(calendar);
		if (lastDisplayed != null && lastDisplayed.equals(date))
				return;
		lastDisplayed = date;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int numMaxDaysInMonth = calendar.getActualMaximum(Calendar.DATE);
		DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
		DayOfWeek firstDayOfMonth = DayOfWeek.getFirstDayOfMonth(day, dayOfWeek);
		
		ArrayList<Integer> startOfWeek = new ArrayList<Integer>();
		for (int i = -firstDayOfMonth.ordinal(); i <= numMaxDaysInMonth; i+=7)
		{
			startOfWeek.add(i);
		}
		
		int selectedRow = 0, selectedColumn = 0;
		Object[][] dataArray = new Object[startOfWeek.size()][7];
		for (int i = 0; i < startOfWeek.size(); i++)
		{
			int start = startOfWeek.get(i);
			for (int j = 0; j < 7; j++)
			{
				int currentDay = start + j;
				if (currentDay <= 0 || currentDay > numMaxDaysInMonth )
					dataArray[i][j] = null;
				else 
					dataArray[i][j] = start + j;
				
				if (currentDay == day) 
				{
					selectedRow = i;
					selectedColumn = j;
				}
			}
		}

		tableModel.setDataVector(dataArray, DayOfWeek.SHORT_FORM);

		table.getColumnModel().getSelectionModel().setSelectionInterval(selectedColumn, selectedColumn);
		table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		
		Month month = Month.getMonth(calendar.get(Calendar.MONTH));
		monthLabel.setText(month.toString() + " " + calendar.get(Calendar.YEAR));
	}
	
	public Date getSelectedDate()
	{
		return new Date(calendar.getTimeInMillis());
	}
	
	
	private class SelectionListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			int column = table.getSelectedColumn();
			int row = table.getSelectedRow();
			
			if (column == -1 || row == -1)
				return;
			
			Object day = table.getValueAt(row, column);
			if (day == null)
				return;
			calendar.set(Calendar.DATE, (Integer) day);
			
			updateDisplay();
			//Controller.getInstance().updateDisplays();
		}
	}
	
	private class DayRenderer extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			return label;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ("previous".equals(e.getActionCommand()))
		{
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
			updateDisplay();
			//Controller.getInstance().updateDisplays();
		}
		else if ("next".equals(e.getActionCommand()))
		{			
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
			updateDisplay();
			//Controller.getInstance().updateDisplays();
		}
	}
	

}
