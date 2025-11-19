// 파일 이름: RentalHistoryDialog.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RentalHistoryDialog extends JDialog {
    public RentalHistoryDialog(Frame parent, List<RentalRecord> history) {
        super(parent, "전체 대여 기록", true); setSize(900, 500); // 가로 크기 확장

        // --- 컬럼명 수정 ---
        String[] columnNames = {"사용자", "물품명", "대여시간", "반납기한", "반납시간", "상태", "환불액"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        
        JTable table = new JTable(tableModel);
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 44, 122));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        table.setRowHeight(24);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // --- 데이터 표시 수정 ---
        for (RentalRecord record : history) {
            String userName = record.getUser().getName();
            String itemName = record.getItemName();
            String rentalTime = record.getRentalTime().format(formatter);
            String dueDate = record.getDueDate().format(formatter); // 반납기한
            String returnTime = (record.getReturnTime() != null) ? record.getReturnTime().format(formatter) : " - "; // 반납시간
            String status = record.getStatus(); // 상태
            String refund = (record.getReturnTime() != null) ? String.format("%.0f원", record.getRefundAmount()) : " - "; // 환불액

            tableModel.addRow(new Object[]{userName, itemName, rentalTime, dueDate, returnTime, status, refund});
        }
        add(new JScrollPane(table));
        setLocationRelativeTo(parent);
    }
}