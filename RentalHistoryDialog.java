// 파일 이름: RentalHistoryDialog.java

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 시스템의 전체 대여 기록을 표(Table) 형태로 보여주는 다이얼로그(Dialog) 클래스입니다.
 * 이 창은 관리자 메뉴를 통해서만 접근할 수 있습니다.
 */
public class RentalHistoryDialog extends JDialog {

    /**
     * RentalHistoryDialog 생성자
     * @param parent 부모 프레임 (MainAppFrame)
     * @param history 표시할 전체 대여 기록 데이터 리스트
     */
    public RentalHistoryDialog(Frame parent, List<RentalRecord> history) {
        // JDialog 생성자를 호출하여 부모, 타이틀, 모달(Modal) 여부 설정
        super(parent, "전체 대여 기록", true);
        setSize(700, 500);

        // --- 1. 테이블 모델 및 테이블 생성 ---
        String[] columnNames = {"사용자", "물품명", "대여시간", "반납시간"};
        
        // 테이블의 셀을 직접 편집할 수 없도록 isCellEditable 메소드를 오버라이드
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);

        // --- 2. 테이블 UI 스타일 설정 ---
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setRowHeight(24);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        
        // 테이블 헤더(컬럼명) 스타일 설정
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 44, 122)); // 진한 파란색 배경
        header.setForeground(Color.BLACK);
        header.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        // --- 3. 테이블 데이터 채우기 ---
        // 날짜/시간을 "yyyy-MM-dd HH:mm:ss" 형식의 문자열로 변환하기 위한 포매터
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // history 리스트의 모든 기록을 순회하며 테이블 행으로 추가
        for (RentalRecord record : history) {
            String userName = record.getUser().getName();
            String itemName = record.getItemName();
            String rentalTime = record.getRentalTime().format(formatter);
            
            // 반납 시간이 null이 아니면 포맷에 맞게 변환하고, null이면 "(대여 중)"으로 표시
            String returnTime = (record.getReturnTime() != null) 
                                ? record.getReturnTime().format(formatter) 
                                : " (대여 중)";
            
            // 모델에 한 줄(row)의 데이터 추가
            tableModel.addRow(new Object[]{userName, itemName, rentalTime, returnTime});
        }
        
        // --- 4. 다이얼로그에 컴포넌트 추가 및 표시 ---
        // JScrollPane으로 테이블을 감싸서 내용이 많을 경우 스크롤이 가능하도록 함
        add(new JScrollPane(table));
        
        // 다이얼로그를 부모 창의 중앙에 위치시킴
        setLocationRelativeTo(parent);
    }
}