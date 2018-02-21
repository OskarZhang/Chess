package Chess.Views;

import javax.swing.*;
import java.awt.*;

/**
 * Static onboarding view for showing welcome message and instructions.
 */
public class OnboardingPanel extends JPanel {
    public OnboardingPanel() {
        JLabel welcomeMessageLabel = new JLabel("<html><center><br>Welcome!<br><br>To start game, first enter players' nicknames on the left side of this window.</center></html>", SwingConstants.CENTER);
        this.add(welcomeMessageLabel);
        this.setBackground(Color.white);
    }
}
