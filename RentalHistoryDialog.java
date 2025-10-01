import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * [새로운 UI 클래스] 관리자용 대여 기록 뷰어
 */
class RentalHistoryDialog extends JDialog {
    public RentalHistoryDialog(Frame parent, List<RentalRecord> history) {
        super(parent, "전체 대여 기록", true);
        setSize(700, 500);

        String[] columnNames = {"사용자", "물품명", "대여시간", "반납시간"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 테이블 수정 불가
            }
        };

        JTable table = new JTable(tableModel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (RentalRecord record : history) {
            String userName = record.getUser().getName();
            String itemName = record.getItemName();
            String rentalTime = record.getRentalTime().format(formatter);
            String returnTime = (record.getReturnTime() != null) ? record.getReturnTime().format(formatter) : " (대여 중)";

            tableModel.addRow(new Object[]{userName, itemName, rentalTime, returnTime});
        }

        add(new JScrollPane(table));
        setLocationRelativeTo(parent);
    }
}
