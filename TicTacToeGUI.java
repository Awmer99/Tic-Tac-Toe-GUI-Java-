import javax.swing.*;
import javax.swing.JColorChooser; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class TicTacToeGUI {
    private JFrame frame;
    private JButton[][] buttons = new JButton[3][3];
    private JLabel scoreLabel;
    private JButton replayButton;
    private JButton leaderboardButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton customizeButton;
    private JComboBox<String> themeSelector;

    private String playerXName;
    private String playerOName;

    private char currentPlayer = 'X';
    private int scoreX = 0;
    private int scoreO = 0;
    private boolean singlePlayer = false;

    private final String LEADERBOARD_FILE = "leaderboard.txt";
    private final String GAME_STATE_FILE = "gamestate.txt";
    private final String THEME_FILE = "theme.txt";

    private Color backgroundColor = Color.LIGHT_GRAY;
    private Color buttonColor = Color.WHITE;
    private Font buttonFont = new Font("Arial", Font.BOLD, 60);

    public TicTacToeGUI() {
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        loadThemePreferences();
        initializePlayerNames();
        initializeModeSelection();
        initializeBoard();
        initializeControlPanel();

        frame.setSize(500, 600);
        frame.setVisible(true);
    }

    private void initializePlayerNames() {
        playerXName = JOptionPane.showInputDialog(frame, "Enter Player X name:", "Player X");
        if (playerXName == null || playerXName.trim().isEmpty()) {
            playerXName = "Player X";
        }

        playerOName = JOptionPane.showInputDialog(frame, "Enter Player O name:", "Player O");
        if (playerOName == null || playerOName.trim().isEmpty()) {
            playerOName = "Player O";
        }
    }

    private void initializeModeSelection() {
        String[] options = {"Single Player", "Two Players"};
        int choice = JOptionPane.showOptionDialog(frame, "Choose game mode:", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        singlePlayer = (choice == 0);
    }

    private void initializeBoard() {
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setBackground(backgroundColor);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("-");
                buttons[i][j].setFont(buttonFont);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(buttonColor);

                int row = i;
                int col = j;

                buttons[i][j].addActionListener(e -> buttonClicked(row, col));

                boardPanel.add(buttons[i][j]);
            }
        }

        frame.add(boardPanel, BorderLayout.CENTER);
    }

    private void initializeControlPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(7, 1));

        scoreLabel = new JLabel(playerXName + ": 0 | " + playerOName + ": 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));

        replayButton = new JButton("Replay");
        replayButton.setFont(new Font("Arial", Font.BOLD, 18));
        replayButton.setBackground(new Color(70, 130, 180));
        replayButton.setForeground(Color.WHITE);
        replayButton.addActionListener(e -> resetBoard());

        leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 18));
        leaderboardButton.setBackground(new Color(34, 139, 34));
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.addActionListener(e -> showLeaderboard());

        saveButton = new JButton("Save Game");
        saveButton.setFont(new Font("Arial", Font.BOLD, 18));
        saveButton.setBackground(new Color(255, 165, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveGameState());

        loadButton = new JButton("Load Game");
        loadButton.setFont(new Font("Arial", Font.BOLD, 18));
        loadButton.setBackground(new Color(138, 43, 226));
        loadButton.setForeground(Color.WHITE);
        loadButton.addActionListener(e -> loadGameState());

        customizeButton = new JButton("Customize Theme");
        customizeButton.setFont(new Font("Arial", Font.BOLD, 18));
        customizeButton.setBackground(new Color(0, 191, 255));
        customizeButton.setForeground(Color.WHITE);
        customizeButton.addActionListener(e -> openThemeCustomization());

        String[] themes = {"Classic Mode", "Dark Mode", "Light Mode"};
        themeSelector = new JComboBox<>(themes);
        themeSelector.setFont(new Font("Arial", Font.BOLD, 16));
        themeSelector.addActionListener(e -> applyTheme((String) themeSelector.getSelectedItem()));

        controlPanel.add(scoreLabel);
        controlPanel.add(replayButton);
        controlPanel.add(leaderboardButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        controlPanel.add(customizeButton);
        controlPanel.add(themeSelector);

        frame.add(controlPanel, BorderLayout.SOUTH);
    }

    private void applyTheme(String theme) {
        switch (theme) {
            case "Classic Mode":
                backgroundColor = Color.LIGHT_GRAY;
                buttonColor = Color.WHITE;
                break;

            case "Dark Mode":
                backgroundColor = Color.DARK_GRAY;
                buttonColor = Color.BLACK;
                break;

            case "Light Mode":
                backgroundColor = Color.WHITE;
                buttonColor = new Color(173, 216, 230);
                break;
        }
        updateTheme();
    }

    private void updateTheme() {
        frame.getContentPane().setBackground(backgroundColor);

        for (JButton[] row : buttons) {
            for (JButton button : row) {
                button.setBackground(buttonColor);
                button.setFont(buttonFont);
            }
        }
        saveThemePreferences();
    }

    private void openThemeCustomization() {
        backgroundColor = JColorChooser.showDialog(frame, "Choose Background Color", backgroundColor);
        buttonColor = JColorChooser.showDialog(frame, "Choose Button Color", buttonColor);

        String fontName = JOptionPane.showInputDialog(frame, "Enter Font Name (e.g., Arial, Serif):", buttonFont.getFontName());
        if (fontName != null && !fontName.trim().isEmpty()) {
            int fontSize = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter Font Size:", buttonFont.getSize()));
            buttonFont = new Font(fontName, Font.BOLD, fontSize);
        }

        updateTheme();
    }

    private void saveThemePreferences() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(THEME_FILE))) {
            writer.write(backgroundColor.getRGB() + "\n");
            writer.write(buttonColor.getRGB() + "\n");
            writer.write(buttonFont.getFontName() + "\n");
            writer.write(buttonFont.getSize() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadThemePreferences() {
        try (BufferedReader reader = new BufferedReader(new FileReader(THEME_FILE))) {
            backgroundColor = new Color(Integer.parseInt(reader.readLine().trim()));
            buttonColor = new Color(Integer.parseInt(reader.readLine().trim()));
            String fontName = reader.readLine().trim();
            int fontSize = Integer.parseInt(reader.readLine().trim());
            buttonFont = new Font(fontName, Font.BOLD, fontSize);
        } catch (IOException e) {
            System.out.println("No theme preferences found. Using default theme.");
        }
    }

    private void buttonClicked(int row, int col) {
        if (buttons[row][col].getText().equals("-")) {
            buttons[row][col].setText(String.valueOf(currentPlayer));

            if (checkWin()) {
                JOptionPane.showMessageDialog(frame, "Player " + currentPlayer + " wins!");
                updateScore();
                resetBoard();
            } else if (isDraw()) {
                JOptionPane.showMessageDialog(frame, "It's a draw!");
                resetBoard();
            } else {
                switchPlayer();

                if (singlePlayer && currentPlayer == 'O') {
                    aiMove();
                }
            }
        }
    }

    private void aiMove() {
        int[] bestMove = minimaxMove();
        buttonClicked(bestMove[0], bestMove[1]);
    }

    private int[] minimaxMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[2];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("-")) {
                    buttons[i][j].setText("O");
                    int score = minimax(false, 'O');
                    buttons[i][j].setText("-");

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(boolean isMaximizing, char player) {
        if (checkWinForPlayer('O')) return 1;
        if (checkWinForPlayer('X')) return -1;
        if (isDraw()) return 0;

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("-")) {
                    buttons[i][j].setText(String.valueOf(player));
                    int score = minimax(!isMaximizing, (player == 'O') ? 'X' : 'O');
                    buttons[i][j].setText("-");

                    bestScore = isMaximizing
                            ? Math.max(score, bestScore)
                            : Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    private boolean checkWin() {
        return checkWinForPlayer(currentPlayer);
    }

    private boolean checkWinForPlayer(char player) {
        String playerSymbol = String.valueOf(player);

        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(playerSymbol) &&
                    buttons[i][1].getText().equals(playerSymbol) &&
                    buttons[i][2].getText().equals(playerSymbol)) {
                return true;
            }

            if (buttons[0][i].getText().equals(playerSymbol) &&
                    buttons[1][i].getText().equals(playerSymbol) &&
                    buttons[2][i].getText().equals(playerSymbol)) {
                return true;
            }
        }

        if (buttons[0][0].getText().equals(playerSymbol) &&
                buttons[1][1].getText().equals(playerSymbol) &&
                buttons[2][2].getText().equals(playerSymbol)) {
            return true;
        }

        if (buttons[0][2].getText().equals(playerSymbol) &&
                buttons[1][1].getText().equals(playerSymbol) &&
                buttons[2][0].getText().equals(playerSymbol)) {
            return true;
        }

        return false;
    }

    private boolean isDraw() {
        for (JButton[] row : buttons) {
            for (JButton button : row) {
                if (button.getText().equals("-")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateScore() {
        if (currentPlayer == 'X') {
            scoreX++;
        } else {
            scoreO++;
        }
        scoreLabel.setText(playerXName + ": " + scoreX + " | " + playerOName + ": " + scoreO);
    }

    private void resetBoard() {
        for (JButton[] row : buttons) {
            for (JButton button : row) {
                button.setText("-");
            }
        }
        currentPlayer = 'X';
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private void saveGameState() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GAME_STATE_FILE))) {
            writer.write(playerXName + "," + playerOName + "\n");
            writer.write(scoreX + "," + scoreO + "\n");
            writer.write(currentPlayer + "\n");
            writer.write(singlePlayer ? "1" : "0" + "\n");

            for (JButton[] row : buttons) {
                for (JButton button : row) {
                    writer.write(button.getText() + ",");
                }
                writer.write("\n");
            }
            JOptionPane.showMessageDialog(frame, "Game state saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGameState() {
        try (BufferedReader reader = new BufferedReader(new FileReader(GAME_STATE_FILE))) {
            String[] players = reader.readLine().split(",");
            playerXName = players[0];
            playerOName = players[1];

            String[] scores = reader.readLine().split(",");
            scoreX = Integer.parseInt(scores[0]);
            scoreO = Integer.parseInt(scores[1]);

            currentPlayer = reader.readLine().charAt(0);
            singlePlayer = reader.readLine().equals("1");

            for (int i = 0; i < 3; i++) {
                String[] row = reader.readLine().split(",");
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText(row[j]);
                }
            }
            scoreLabel.setText(playerXName + ": " + scoreX + " | " + playerOName + ": " + scoreO);
            JOptionPane.showMessageDialog(frame, "Game state loaded successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "No saved game found.");
        }
    }

    private void showLeaderboard() {
        JOptionPane.showMessageDialog(frame, "Leaderboard feature not included in this step.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGUI::new);
    }
}
