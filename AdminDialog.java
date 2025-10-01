// 기능 : 관리자 패널 다이얼로그 구현

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class AdminDialog extends JDialog {
    public AdminDialog(MainAppFrame parent, List<User> users, List<Item> items) {
        super(parent, "관리자 패널", true);
        setSize(500, 400);
        setLayout(new GridLayout(2, 1, 10, 10));

        // 사용자 추가 패널
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(new TitledBorder("신규 사용자 추가"));
        GridBagConstraints gbcUser = new GridBagConstraints();
        gbcUser.insets = new Insets(5, 5, 5, 5);
        JTextField userIdField = new JTextField();
        JTextField userNameField = new JTextField();
        JTextField userPassField = new JTextField();
        JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"Student", "Staff"});
        JButton addUserButton = new JButton("사용자 추가");

        gbcUser.gridx = 0; gbcUser.gridy = 0; gbcUser.weightx = 0.0; gbcUser.fill = GridBagConstraints.NONE; userPanel.add(new JLabel("ID:"), gbcUser);
        gbcUser.gridy = 1; userPanel.add(new JLabel("이름:"), gbcUser);
        gbcUser.gridy = 2; userPanel.add(new JLabel("비밀번호:"), gbcUser);
        gbcUser.gridy = 3; userPanel.add(new JLabel("타입:"), gbcUser);
        gbcUser.gridx = 1; gbcUser.gridy = 0; gbcUser.weightx = 1.0; gbcUser.fill = GridBagConstraints.HORIZONTAL; userPanel.add(userIdField, gbcUser);
        gbcUser.gridy = 1; userPanel.add(userNameField, gbcUser);
        gbcUser.gridy = 2; userPanel.add(userPassField, gbcUser);
        gbcUser.gridy = 3; userPanel.add(userTypeCombo, gbcUser);
        gbcUser.gridy = 4; gbcUser.fill = GridBagConstraints.NONE; gbcUser.anchor = GridBagConstraints.EAST; userPanel.add(addUserButton, gbcUser);

        // 물품 추가 패널
        JPanel itemPanel = new JPanel(new GridBagLayout());
        itemPanel.setBorder(new TitledBorder("신규 물품 추가"));
        GridBagConstraints gbcItem = new GridBagConstraints();
        gbcItem.insets = new Insets(5, 5, 5, 5);
        JTextField itemNameField = new JTextField();
        JTextField itemStockField = new JTextField();
        JTextField itemFeeField = new JTextField();
        JButton addItemButton = new JButton("물품 추가");

        gbcItem.gridx = 0; gbcItem.gridy = 0; gbcItem.weightx = 0.0; gbcItem.fill = GridBagConstraints.NONE; itemPanel.add(new JLabel("물품명:"), gbcItem);
        gbcItem.gridy = 1; itemPanel.add(new JLabel("최대재고:"), gbcItem);
        gbcItem.gridy = 2; itemPanel.add(new JLabel("기본요금:"), gbcItem);
        gbcItem.gridx = 1; gbcItem.gridy = 0; gbcItem.weightx = 1.0; gbcItem.fill = GridBagConstraints.HORIZONTAL; itemPanel.add(itemNameField, gbcItem);
        gbcItem.gridy = 1; itemPanel.add(itemStockField, gbcItem);
        gbcItem.gridy = 2; itemPanel.add(itemFeeField, gbcItem);
        gbcItem.gridy = 3; gbcItem.fill = GridBagConstraints.NONE; gbcItem.anchor = GridBagConstraints.EAST; itemPanel.add(addItemButton, gbcItem);

        addUserButton.addActionListener(e -> {
            users.add(new User(userIdField.getText(), userNameField.getText(), (String) userTypeCombo.getSelectedItem(), userPassField.getText()));
            JOptionPane.showMessageDialog(this, "사용자가 추가되었습니다.");
            userIdField.setText("");
            userNameField.setText("");
            userPassField.setText("");
        });

        addItemButton.addActionListener(e -> {
            try {
                Item newItem = new Item(itemNameField.getText(), Integer.parseInt(itemStockField.getText()), Double.parseDouble(itemFeeField.getText()));
                newItem.addObserver(parent);
                items.add(newItem);
                parent.refreshItemComboBox();
                JOptionPane.showMessageDialog(this, "물품이 추가되었습니다.");
                itemNameField.setText("");
                itemStockField.setText("");
                itemFeeField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "재고와 요금은 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(userPanel);
        add(itemPanel);
        setLocationRelativeTo(parent);
    }
}