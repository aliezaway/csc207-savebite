package savebite.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import savebite.interface_adapter.CategoryNames;
import savebite.interface_adapter.create_product.CreateProductController;
import savebite.interface_adapter.create_product.CreateProductState;
import savebite.interface_adapter.create_product.CreateProductViewModel;

/**
 * Form for creating a product. A barcode lookup can fill in the name and brand;
 * nothing is saved until the user clicks Save.
 */
public class CreateProductDialog extends JDialog implements PropertyChangeListener {
    private final CreateProductViewModel viewModel;
    private final CreateProductController controller;

    private final JTextField barcodeField = new JTextField(14);
    private final JButton lookupButton = new JButton("Look up");
    private final JTextField nameField = new JTextField(20);
    private final JTextField brandField = new JTextField(20);
    private final JComboBox<String> categoryBox = new JComboBox<>(CategoryNames.all().toArray(new String[0]));
    private final JTextField daysField = new JTextField(6);
    private final JLabel lookupLabel = new JLabel(" ");
    private final JLabel errorLabel = Forms.errorLabel();
    private final JButton saveButton = new JButton("Save");
    private String createdProductId;

    private CreateProductDialog(Window owner, CreateProductViewModel viewModel, CreateProductController controller) {
        super(owner, "New product", ModalityType.APPLICATION_MODAL);
        this.viewModel = viewModel;
        this.controller = controller;
        viewModel.setState(new CreateProductState());
        viewModel.addPropertyChangeListener(this);

        JPanel barcodeRow = new JPanel(new BorderLayout(4, 0));
        barcodeRow.add(barcodeField, BorderLayout.CENTER);
        barcodeRow.add(lookupButton, BorderLayout.EAST);
        lookupButton.addActionListener(event -> lookup());

        JPanel form = Forms.newForm();
        Forms.addRow(form, 0, "Barcode (optional)", barcodeRow);
        Forms.addWide(form, 1, lookupLabel);
        Forms.addRow(form, 2, "Name", nameField);
        Forms.addRow(form, 3, "Brand (optional)", brandField);
        Forms.addRow(form, 4, "Category", categoryBox);
        Forms.addRow(form, 5, "Default days (optional)", daysField);
        Forms.addWide(form, 6, new JLabel("Default days are only used to suggest a date, which you confirm."));
        Forms.addWide(form, 7, errorLabel);

        saveButton.addActionListener(event -> save(false));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(event -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancel);
        buttons.add(saveButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Shows the form and waits until it is closed.
     *
     * @return the id of the created product, or empty if the user cancelled
     */
    public static Optional<String> showDialog(Window owner, CreateProductViewModel viewModel,
                                              CreateProductController controller) {
        CreateProductDialog dialog = new CreateProductDialog(owner, viewModel, controller);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.createdProductId);
    }

    private void lookup() {
        String barcode = barcodeField.getText();
        lookupButton.setEnabled(false);
        saveButton.setEnabled(false);
        Forms.show(lookupLabel, "Looking up…");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.lookup(barcode);
                return null;
            }

            @Override
            protected void done() {
                lookupButton.setEnabled(true);
                saveButton.setEnabled(true);
                try {
                    get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Forms.show(lookupLabel, "The lookup failed unexpectedly. You can fill in the details yourself.");
                }
            }
        }.execute();
    }

    private void save(boolean confirmDuplicateName) {
        controller.execute(nameField.getText(), (String) categoryBox.getSelectedItem(), brandField.getText(),
                barcodeField.getText(), daysField.getText(), confirmDuplicateName);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> propertyChange(event));
            return;
        }
        CreateProductState state = viewModel.getState();
        switch (event.getPropertyName()) {
            case "lookup":
                if (state.getLookedUpName() != null) {
                    nameField.setText(state.getLookedUpName());
                }
                if (state.getLookedUpBrand() != null) {
                    brandField.setText(state.getLookedUpBrand());
                }
                Forms.show(lookupLabel, state.getLookupMessage());
                break;
            case "lookupError":
                Forms.show(lookupLabel, state.getLookupMessage());
                break;
            case "duplicate":
                int choice = JOptionPane.showConfirmDialog(this, state.getDuplicateWarning(), "Possible duplicate",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(() -> save(true));
                }
                break;
            case "created":
                createdProductId = state.getCreatedProductId();
                dispose();
                break;
            case "error":
                Forms.show(errorLabel, state.getError());
                break;
            default:
                break;
        }
    }

    @Override
    public void dispose() {
        viewModel.removePropertyChangeListener(this);
        super.dispose();
    }
}
