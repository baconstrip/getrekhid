package com.huskygames.rekhid.slugger;

import com.huskygames.rekhid.Definitions;
import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.actor.Fighter;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private final Rekhid parent;

    public GamePanel(Rekhid parent) {
        this.parent = parent;
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public Dimension getPreferredSize() {
        return new Dimension(Definitions.DEFAULT_WIDTH, Definitions.DEFAULT_HEIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!g.getFont().equals(Definitions.DISPLAY_FONT)) {
            g.setFont(Definitions.DISPLAY_FONT);
        }

        // terrible lock contention, but it sure is easy
        synchronized (parent.getGameLock()) {
            // ALL GAME DRAWING CODE MUST BE IN HERE

            if (parent.getGameState() == Rekhid.GameState.MATCH) {
                parent.getWorld().draw((Graphics2D) g);
                drawScores((Graphics2D) g);
            } else if (parent.getGameState() == Rekhid.GameState.MENU) {
                parent.getMainMenu().draw((Graphics2D) g);
            } else if (parent.getGameState() == Rekhid.GameState.POST_MATCH) {
                parent.getWorld().draw((Graphics2D) g);
                drawScores((Graphics2D) g);
            } else if (parent.getGameState() == Rekhid.GameState.CONTROLLER_MENU) {
                parent.getControllerSelect().draw((Graphics2D) g);
            }


            // Draw FPS
            g.setFont(Definitions.FPS_FONT);
            g.drawString((int) (1000f / parent.getLastTickTime()) + " FPS", this.getWidth() - 60, 15);
        }
    }

    private void drawScores(Graphics2D g) {
        Color temp = g.getColor();

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // let's get some damage counters
        int winWidth = this.getWidth();
        int winHeight = this.getHeight();
        int scoreSpacing = winWidth / 5;
        int scorePos = winHeight - winHeight / 7;

        FontMetrics metrics = g.getFontMetrics(g.getFont());

        int x;
        int y;

        int i = 0;

        for (Fighter ply : parent.getWorld().getFighters()) {
            double damage = ply.getDamage();
            int centerX = scoreSpacing * (i + 1);
            String output = Definitions.DAMAGE_FORMATTER.format(damage);
            int outWidth = metrics.stringWidth(output);
            int outHeight = metrics.getHeight();

            x = centerX - outWidth / 2;
            y = scorePos - outHeight / 2;

            g.setColor(Definitions.SCORE_BACKGROUND);
            g.fillOval(centerX - Definitions.SCORE_BACKGROUND_SIZE.getX() / 2, y - outHeight,
                    Definitions.SCORE_BACKGROUND_SIZE.getX(),
                    Definitions.SCORE_BACKGROUND_SIZE.getY());
            textWithShadow(g, output, x, y);
            y = y + outHeight;
            x = centerX - (metrics.stringWidth(ply.getName()) / 2);
            textWithShadow(g, ply.getName(), x, y);

            output = "Lives: " + ply.getLives();
            y = y + outHeight;
            x = centerX - (metrics.stringWidth(output) / 2);
            textWithShadow(g, output, x, y);

            i++;
        }

        if (parent.getGameState() == Rekhid.GameState.POST_MATCH) {
            String endString = parent.getWorld().getWinner().getName() + " WINS!";
            x = winWidth / 2 - metrics.stringWidth(endString) / 2;
            y = winHeight / 2 - metrics.getHeight() / 2;
            textWithShadow(g, endString, x, y);
        }

        g.setColor(temp);
    }

    private void textWithShadow(Graphics g, String output, int x, int y) {
        g.setColor(Color.WHITE);
        g.drawString(output, x + 2, y + 2);
        g.setColor(Color.black);
        g.drawString(output, x, y);
    }
}
