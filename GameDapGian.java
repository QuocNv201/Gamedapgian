import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class GameDapGian extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private Timer timer;
    private ArrayList<Gian> gians;
    private GameManager gameManager;
    private Random random;

    private boolean isMousePressed = false;
    private Image backgroundImage; // Hình ảnh nền
    private Image cursorImage; // Hình ảnh con chuột
    private Image gianImage; // Hình ảnh gián
    private Image gianSmashedImage; // Hình ảnh gián bị đập
    private Image shootImage; // Hình ảnh vùng va chạm

    private boolean isGamePaused = false; // Biến kiểm tra trạng thái game
    private boolean isCountdownActive = false; // Biến kiểm tra trạng thái đếm ngược
    private int countdownTime = 0; // Thời gian đếm ngược còn lại

    private Rectangle pauseButtonRect; // Vùng của nút tạm dừng

    public GameDapGian() {
        gians = new ArrayList<>();
        gameManager = new GameManager();
        random = new Random();

        // Tải hình ảnh nền
        try {
            backgroundImage = new ImageIcon(getClass().getResource("bgr.jpg")).getImage();
            gianImage = new ImageIcon(getClass().getResource("gias.gif")).getImage();
            gianSmashedImage = new ImageIcon(getClass().getResource("gianbidap.png")).getImage();
            cursorImage = new ImageIcon(getClass().getResource("vot.png")).getImage();
            shootImage = new ImageIcon(getClass().getResource("vot.png")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1); // Thoát chương trình nếu không thể tải hình ảnh
        }

        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        timer = new Timer(20, this);
        timer.start();

        // Cập nhật con chuột mặc định
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Custom Cursor"));

        // Thêm lắng nghe phím để dừng game
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });

        // Khởi tạo vùng nút tạm dừng
        int pauseButtonWidth = 40;
        int pauseButtonHeight = 40;
        int pauseButtonX = getWidth() - pauseButtonWidth - 10; // 10 là khoảng cách từ cạnh khung hình
        int pauseButtonY = 10; // Khoảng cách từ cạnh trên của khung hình
        pauseButtonRect = new Rectangle(pauseButtonX, pauseButtonY, pauseButtonWidth, pauseButtonHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Nếu game đang dừng, vẽ nền và các gián như bình thường
        if (isGamePaused) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Vẽ gián
            for (Gian gian : gians) {
                Rectangle rect = gian.getRectangle();
                g.drawImage(gian.isSmashed() ? gianSmashedImage : gianImage, rect.x, rect.y, rect.width, rect.height, this);
            }

            // Vẽ thời gian đếm ngược nếu đang đếm ngược
            if (isCountdownActive) {
                int secondsLeft = countdownTime / 1000 + 1; // Chuyển đổi thời gian đếm ngược sang giây và làm tròn lên
                g.setFont(new Font("Arial", Font.BOLD, 100));
                g.setColor(Color.RED);
                g.drawString(String.valueOf(secondsLeft), getWidth() / 2 - 30, getHeight() / 2);
            } else {
                // Vẽ biểu tượng tạm dừng (nút tam giác nghiêng)
                g.setColor(Color.WHITE);
                g.fillPolygon(new int[]{getWidth() / 2 - 20, getWidth() / 2 + 20, getWidth() / 2 - 20},
                        new int[]{getHeight() / 2 - 20, getHeight() / 2, getHeight() / 2 + 20}, 3);
            }

            // Vẽ điểm số, kỷ lục và số gián trượt khi game dừng
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Điểm: " + gameManager.getScore(), 10, 20);
            g.drawString("Trượt: " + gameManager.getMissedGians(), 10, 40);
            g.drawString("Kỷ lục: " + gameManager.getHighScore(), 10, 60);

            return;  // Dừng vẽ thêm những thứ khác nếu game đang dừng
        }

        // Vẽ nền và các đối tượng game khi game đang chơi
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Vẽ gián
        for (Gian gian : gians) {
            Rectangle rect = gian.getRectangle();
            g.drawImage(gian.isSmashed() ? gianSmashedImage : gianImage, rect.x, rect.y, rect.width, rect.height, this);
        }

        // Vẽ vùng va chạm (đạn)
        Point mousePos = getMousePosition();
        if (mousePos != null && isMousePressed) {
            g.drawImage(shootImage, mousePos.x - shootImage.getWidth(null) / 2, mousePos.y - shootImage.getHeight(null) / 2, this);
        }

        // Vẽ điểm số, kỷ lục và số gián trượt khi game đang chơi
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Điểm: " + gameManager.getScore(), 10, 20);
        g.drawString("Trượt: " + gameManager.getMissedGians(), 10, 40);
        g.drawString("Kỷ lục: " + gameManager.getHighScore(), 10, 60);

        // Vẽ nút tạm dừng
        int lineHeight = 40;  // Chiều dài của mỗi đường thẳng
        int lineWidth = 10;    // Độ rộng của mỗi đường thẳng (có thể điều chỉnh)
        int margin = 10; // Khoảng cách từ cạnh trên và phải của khung hình
        g.fillRect(getWidth() - margin - lineWidth * 3, margin, lineWidth, lineHeight);
        g.fillRect(getWidth() - margin - lineWidth * 1, margin, lineWidth, lineHeight);

        // Cập nhật vị trí vùng nút tạm dừng
        pauseButtonRect.setLocation(getWidth() - 10 - pauseButtonRect.width, 10);

        // Game Over screen
        if (gameManager.isGameOver()) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.RED);
            g.drawString("Game Over!", getWidth() / 2 - 100, getHeight() / 2 - 40);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            g.drawString("Click to Restart", getWidth() / 2 - 75, getHeight() / 2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGamePaused) {
            if (isCountdownActive) {
                countdownTime -= 20; // Giảm thời gian đếm ngược (20 milliseconds)
                if (countdownTime <= 0) {
                    isCountdownActive = false;
                    isGamePaused = false; // Tiếp tục trò chơi
                }
            }
            repaint();
            return;
        }

        if (gameManager.isGameOver()) return;

        moveGians();
        spawnGians();
        repaint();
    }

    private void moveGians() {
        // Di chuyển gián
        Iterator<Gian> gianIterator = gians.iterator();
        while (gianIterator.hasNext()) {
            Gian gian = gianIterator.next();

            if (gian.isSmashed()) {
                if (System.currentTimeMillis() - gian.getSmashedTime() > 300) {
                    gianIterator.remove(); // Loại bỏ gián đã bị đập sau 0,3 giây
                }
                continue;
            }

            gian.move(gameManager.getGianSpeed());

            // Nếu gián ra ngoài màn hình, loại bỏ gián và kiểm tra thua
            if (gian.isOutOfBounds(getHeight())) {
                gianIterator.remove();
                gameManager.incrementMissedGians();
                if (gameManager.getMissedGians() >= 5) { // Nếu có 5 gián ra ngoài, kết thúc game
                    gameManager.endGame();
                }
            }
        }
    }

    private void spawnGians() {
        // Tạo gián mới với xác suất
        int spawnChance = 1 + (gameManager.getScore() / 50); // Tăng xác suất mỗi khi có 50 điểm

        // Kiểm tra và spawn gián không chồng lấn
        if (random.nextInt(100) < spawnChance) {
            int randomX;
            boolean positionOccupied;
            do {
                randomX = random.nextInt(getWidth() - 50); // Vị trí X ngẫu nhiên (gián có chiều rộng 50px)
                positionOccupied = false;

                // Kiểm tra xem gián có chồng lên nhau không
                for (Gian gian : gians) {
                    if (Math.abs(gian.getRectangle().x - randomX) < 50) { // Kiểm tra xem gián đã có chưa (gián có chiều rộng 50px)
                        positionOccupied = true;
                        break;
                    }
                }
            } while (positionOccupied); // Lặp lại cho đến khi tìm được vị trí không bị chồng lên

            gians.add(new Gian(randomX, 0)); // Tạo gián mới với vị trí X không chồng lên
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameManager.isGameOver()) {
            restartGame();
            return;
        }

        Point clickPoint = e.getPoint();
        if (pauseButtonRect.contains(clickPoint)) {
            togglePause();
        } else if (isGamePaused && !isCountdownActive) {
            startCountdown();
        } else {
            shootAt(clickPoint);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isMousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMousePressed = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isMousePressed) {
            shootAt(e.getPoint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void shootAt(Point point) {
        boolean shotHit = false;

        // Lấy kích thước của shootImage (đạn) để tạo vùng va chạm
        int shootWidth = shootImage.getWidth(null);
        int shootHeight = shootImage.getHeight(null);

        // Tạo một vùng va chạm xung quanh hình ảnh bắn (đạn)
        Rectangle shootArea = new Rectangle(point.x - shootWidth / 2, point.y - shootHeight / 2, shootWidth, shootHeight);

        // Kiểm tra va chạm với gián trong vùng này
        Iterator<Gian> gianIterator = gians.iterator();
        while (gianIterator.hasNext()) {
            Gian gian = gianIterator.next();
            if (shootArea.intersects(gian.getRectangle())) {  // Kiểm tra xem vùng bắn có giao với gián hay không
                gian.smash(); // Đánh dấu gián bị đập
                gameManager.increaseScore(10);  // Tăng điểm số
                shotHit = true;
                break;
            }
        }

        repaint();  // Vẽ lại màn hình
    }

    private void restartGame() {
        gameManager.resetGame();
        gians.clear();
        repaint();
    }

    private void togglePause() {
        isGamePaused = !isGamePaused;  // Đổi trạng thái tạm dừng
        isCountdownActive = false; // Hủy bỏ đếm ngược nếu đang đếm ngược
        repaint();  // Vẽ lại màn hình khi dừng hoặc tiếp tục trò chơi
    }

    private void startCountdown() {
        countdownTime = 3000; // 3 giây (3000 milliseconds)
        isCountdownActive = true;
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game đập Gián");
        GameDapGian game = new GameDapGian();
        frame.add(game);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
