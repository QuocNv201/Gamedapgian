public class GameManager {
    private int score;
    private int gianSpeed;
    private int missedGians;
    private boolean isGameOver;
    private int highScore; // Lưu trữ kỷ lục

    public GameManager() {
        score = 0;
        gianSpeed = 2;
        missedGians = 0;
        isGameOver = false;
        highScore = 0; // Kỷ lục ban đầu là 0
    }

    public void increaseScore(int points) {
        score += points;
        if (score % 50 == 0) {
            gianSpeed++; // Tăng tốc độ gián mỗi 50 điểm
        }
    }

    public void decreaseScore(int points) {
        score -= points;
        if (score < 0) {
            score = 0;  // Đảm bảo điểm không bị âm
        }
    }

    public void incrementMissedGians() {
        missedGians++;
    }

    public void resetGame() {
        // Cập nhật kỷ lục nếu cần trước khi reset trò chơi
        if (score > highScore) {
            highScore = score;
        }

        score = 0;
        gianSpeed = 2;
        missedGians = 0;
        isGameOver = false;
    }

    public void endGame() {
        isGameOver = true;
        // Cập nhật kỷ lục nếu điểm số hiện tại lớn hơn kỷ lục
        if (score > highScore) {
            highScore = score;
        }
    }

    public int getScore() {
        return score;
    }

    public int getGianSpeed() {
        return gianSpeed;
    }

    public int getMissedGians() {
        return missedGians;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getHighScore() {
        return highScore; // Trả về kỷ lục
    }
}