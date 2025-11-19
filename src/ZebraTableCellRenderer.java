// 파일 이름: ZebraTableCellRenderer.java

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * JTable의 행(row) 배경색을 번갈아 칠해주는 커스텀 셀 렌더러(Cell Renderer)입니다.
 * 이른바 '얼룩말 무늬(Zebra Striping)' 효과를 주어 테이블의 가독성을 높이는 역할을 합니다.
 * DefaultTableCellRenderer를 상속받아 기존의 기능은 유지하면서 배경색만 변경합니다.
 */
public class ZebraTableCellRenderer extends DefaultTableCellRenderer {

    // 짝수 행에 적용할 배경색 (흰색)
    private static final Color ROW_EVEN_COLOR = Color.WHITE;
    // 홀수 행에 적용할 배경색 (밝은 회색)
    private static final Color ROW_ODD_COLOR = new Color(245, 245, 245);

    /**
     * 테이블의 각 셀을 어떻게 그릴지 정의하는 메소드입니다.
     * @param table         해당 렌더러가 사용되는 JTable
     * @param value         셀에 표시될 값
     * @param isSelected    셀이 현재 선택되었는지 여부
     * @param hasFocus      셀이 포커스를 가지고 있는지 여부
     * @param row           셀의 행(row) 인덱스
     * @param column        셀의 열(column) 인덱스
     * @return              화면에 그려질 컴포넌트 (일반적으로 JLabel)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        // 1. 부모 클래스의 메소드를 호출하여 기본적인 셀 컴포넌트를 가져옵니다.
        //    (선택 색상, 포커스 테두리 등 기본 처리가 여기서 이루어짐)
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // 2. 셀이 '선택되지 않은' 상태일 때만 배경색을 변경합니다.
        //    (선택된 상태일 경우에는 기본 선택 색상이 유지되어야 함)
        if (!isSelected) {
            // 3. 행(row) 인덱스가 짝수인지 홀수인지에 따라 다른 배경색을 설정합니다.
            c.setBackground(row % 2 == 0 ? ROW_EVEN_COLOR : ROW_ODD_COLOR);
        }

        // 4. 스타일이 적용된 컴포넌트를 반환합니다.
        return c;
    }
}