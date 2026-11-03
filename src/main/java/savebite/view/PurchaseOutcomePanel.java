package savebite.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import savebite.interface_adapter.mark_purchase_outcome.MarkOutcomeState;
import savebite.interface_adapter.mark_purchase_outcome.MarkOutcomeViewModel;
import savebite.interface_adapter.mark_purchase_outcome.MarkPurchaseOutcomeController;

/**
 * A row of buttons that mark the selected inventory batch as eaten or discarded, with a message beside them.
 * The message has its own space, so a long message never pushes the buttons out of view.
 */
public class PurchaseOutcomePanel extends JPanel implements PropertyChangeListener {
    private final MarkOutcomeViewModel viewModel;
    private final Runnable onChanged;
    private final JLabel messageLabel = new JLabel(" ");

    /**
     * @param selectedPurchaseId returns the selected batch id, or null if none is selected
     * @param onChanged          refreshes the inventory after a batch was finished
     */
    public PurchaseOutcomePanel(MarkOutcomeViewModel viewModel, MarkPurchaseOutcomeController controller,
                                Supplier<String> selectedPurchaseId, Runnable onChanged) {
        super(new BorderLayout(8, 0));
        this.viewModel = viewModel;
        this.onChanged = onChanged;

        JButton eaten = new JButton("Whole batch eaten");
        eaten.addActionListener(event -> controller.markEaten(selectedPurchaseId.get()));
        JButton discarded = new JButton("Whole batch discarded");
        discarded.addActionListener(event -> {
            String id = selectedPurchaseId.get();
            if (id == null) {
                controller.markDiscarded(null);
                return;
            }
            int choice = JOptionPane.showConfirmDialog(this,
                    "Discard the whole batch? Its full price will count as discarded.", "Discard batch",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                controller.markDiscarded(id);
            }
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(new JLabel("Selected batch:"));
        buttons.add(eaten);
        buttons.add(discarded);
        add(buttons, BorderLayout.WEST);
        add(messageLabel, BorderLayout.CENTER);
        viewModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        MarkOutcomeState state = viewModel.getState();
        if ("success".equals(event.getPropertyName())) {
            messageLabel.setForeground(getForeground());
            messageLabel.setText(state.getMessage());
            onChanged.run();
        } else if ("error".equals(event.getPropertyName())) {
            messageLabel.setForeground(Forms.ERROR_COLOR);
            messageLabel.setText(state.getError());
        }
    }
}
