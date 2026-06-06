package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

class GUI extends JFrame {

    Container cp;

    JPanel pnNorth = new JPanel();
    JPanel pnSouth = new JPanel();
    JPanel pnCenter = new JPanel();
    JPanel pnControls = new JPanel();
    JPanel pnProgress = new JPanel();

    JButton btPlay = new JButton("Play");
    JButton btPrev = new JButton("<<");
    JButton btNext = new JButton(">>");

    JSlider slProgress = new JSlider();

    // cores
    Color pnColor = new Color(18, 18, 18);
    Color btColor = new Color(40, 40, 40);
    Color hoverColor = new Color(60, 60, 60);
    Color activeColor = new Color(88, 93, 93);

    Color textColor = Color.WHITE;

    // labels
    JLabel lbArtist = new JLabel("Artista desconhecido...");
    JLabel lbMusic = new JLabel("Nenhuma música selecionada");
    JLabel lbCover = new JLabel();
    JLabel lbCurrentTime = new JLabel("0:00");
    JLabel lbTotalTime = new JLabel("1:40");

    // fontes
    Font titleFont = new Font("Arial", Font.BOLD, 22);
    Font artistFont = new Font("Arial", Font.PLAIN, 14);

    // estado do player
    boolean isPlaying = false;

    // timer da barra de progresso
    Timer musicTimer;
    int musicProgress = 0;

    Clip musicClip;

    public void styleButton(JButton button) {
        button.setBackground(btColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(80, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));

        // hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button == btPlay && isPlaying) {
                    button.setBackground(activeColor);
                } else {
                    button.setBackground(btColor);
                }
            }
        });
    }

    public void configureNorthPanel() {
        pnNorth.setLayout(new GridLayout(2, 1));
        pnNorth.add(lbMusic);
        pnNorth.add(lbArtist);

        lbMusic.setFont(titleFont);
        lbArtist.setFont(artistFont);
    }

    public void configureCenterPanel() {
        pnCenter.setLayout(new GridBagLayout());
        pnCenter.add(lbCover);

        lbCover.setBackground(btColor);
        lbCover.setPreferredSize(new Dimension(200, 200));
        lbCover.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        lbCover.setOpaque(true);
    }

    public void configureSouthPanel() {
        pnSouth.setLayout(new BorderLayout());
        pnSouth.add(pnProgress, BorderLayout.NORTH);
        pnSouth.add(pnControls, BorderLayout.SOUTH);
        pnSouth.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnProgress.setLayout(new BorderLayout(10, 0));
        pnProgress.add(lbCurrentTime, BorderLayout.WEST);
        pnProgress.add(slProgress, BorderLayout.CENTER);
        pnProgress.add(lbTotalTime, BorderLayout.EAST);

        // slider
        slProgress.setPreferredSize(new Dimension(450, 8));
        slProgress.setPaintTicks(false);
        slProgress.setPaintLabels(false);
        slProgress.setFocusable(false);
        slProgress.setValue(0);

        // controles
        pnControls.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 15));
        pnControls.add(btPrev);
        pnControls.add(btPlay);
        pnControls.add(btNext);

        slProgress.setMinimum(0);
        slProgress.setMaximum(100);
    }

    public void applyStyles() {
        pnCenter.setBackground(pnColor);
        pnSouth.setBackground(pnColor);
        pnNorth.setBackground(pnColor);
        pnControls.setBackground(pnColor);
        slProgress.setBackground(pnColor);
        pnProgress.setBackground(pnColor);

        lbCurrentTime.setForeground(textColor);
        lbTotalTime.setForeground(textColor);
        lbCurrentTime.setFont(artistFont);
        lbTotalTime.setFont(artistFont);

        lbMusic.setForeground(textColor);
        lbArtist.setForeground(textColor);

        styleButton(btPlay);
        styleButton(btPrev);
        styleButton(btNext);

        // alinhamentos
        lbMusic.setHorizontalAlignment(JLabel.CENTER);
        lbArtist.setHorizontalAlignment(JLabel.CENTER);
        lbCover.setHorizontalAlignment(JLabel.CENTER);
    }

    public void updatePlayerState() {
        if (isPlaying) {
            btPlay.setText("Pause");
            btPlay.setBackground(activeColor);
        } else {
            btPlay.setText("Play");
            btPlay.setBackground(btColor);
        }
    }

    public void setupEvents() {
        btPlay.addActionListener(e -> {
            isPlaying = !isPlaying;

            if (isPlaying) {
                musicClip.start();
                musicTimer.start();
            } else {
                musicClip.stop();
                musicTimer.stop();
            }
            updatePlayerState();
        });
    }

    public void setupTimer() {
        musicTimer = new Timer(1000, e -> {
            musicProgress++;
            slProgress.setValue(musicProgress);
            lbCurrentTime.setText(formatTime(musicProgress));

            if (musicProgress >= 100) {
                musicTimer.stop();
                isPlaying = false;
                musicProgress = 0;
                lbCurrentTime.setText("0:00");
                slProgress.setValue(0);
                updatePlayerState();
            }
        });
    }

    public String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format(
                "%d:%02d",
                minutes,
                remainingSeconds
        );
    }

    public void loadCover() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/aiDentro.jpg"));
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        lbCover.setIcon(new ImageIcon(scaledImage));
    }

    public void loadMusic() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/music/musica.wav"));
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GUI() {

        cp = getContentPane();
        cp.setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cp.add(pnNorth, BorderLayout.NORTH);
        cp.add(pnSouth, BorderLayout.SOUTH);
        cp.add(pnCenter, BorderLayout.CENTER);

        configureNorthPanel();
        configureCenterPanel();
        configureSouthPanel();
        applyStyles();
        loadCover();
        loadMusic();
        setupTimer();
        setupEvents();

        setTitle("Player de Música");
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
}
