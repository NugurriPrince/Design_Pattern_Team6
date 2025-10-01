// 기능 : 로그인 다이얼로그 구현

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class LoginDialog extends JDialog {
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton, cancelButton;
    private User loggedInUser = null;

    public LoginDialog(Frame parent, List<User> users) {
        super(parent, "로그인", true);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("사용자 ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; idField = new JTextField(15); panel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("비밀번호:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; passwordField = new JPasswordField(15); panel.add(passwordField, gbc);

        loginButton = new JButton("로그인");
        cancelButton = new JButton("취소");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        loginButton.addActionListener(e -> {
            Optional<User> user = users.stream()
                    .filter(u -> u.getId().equals(idField.getText()) && u.getPassword().equals(new String(passwordField.getPassword())))
                    .findFirst();
            if (user.isPresent()) {
                loggedInUser = user.get();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "ID 또는 비밀번호가 잘못되었습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}