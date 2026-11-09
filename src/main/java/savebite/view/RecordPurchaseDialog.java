package savebite.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import savebite.interface_adapter.record_purchase.ProductChoice;
import savebite.interface_adapter.record_purchase.RecordPurchaseController;
import savebite.interface_adapter.record_purchase.RecordPurchaseState;
import savebite.interface_adapter.record_purchase.RecordPurchaseViewModel;

/**
 * Form for recording one purchase batch. An estimated date must be confirmed before saving.
 */
public class RecordPurchaseDialog extends JDialog implements PropertyChangeListener {
    private static final String[] UNITS = {"item", "pack", "bottle", "kg", "g", "L", "mL"};

    private final RecordPurchaseViewModel viewModel;
    private final RecordPurchaseController controller;
    private final Supplier<Optional<String>> createProductFlow;

    private final JComboBox<ProductChoice> productBox = new JComboBox<>();
    private final JTextField priceField = new JTextField(8);
    private final JTextField quantityField = new JTextField("1", 6);
    private final JComboBox<String> unitBox = new JComboBox<>(UNITS);
    private final JTextField purchaseDateField = new JTextField(10);
    private final JRadioButton packageButton = new JRadioButton("Date printed on the package", true);
    private final JRadioButton estimateButton = new JRadioButton("Estimate from the product default");
    private final JTextField targetDateField = new JTextField(10);
    private final JCheckBox confirmBox = new JCheckBox("I have checked this estimated date");
    private final JLabel hintLabel = new JLabel(" ");
    private final JLabel errorLabel = Forms.errorLabel();
    private String productToSelect;

    /**
     * @param createProductFlow opens the new product form and returns the new product id, if one was created
     * @param today             the default purchase date
     */
    public RecordPurchaseDialog(Window owner, RecordPurchaseViewModel viewModel, RecordPurchaseController controller,
                                Supplier<Optional<String>> createProductFlow, LocalDate today) {
        super(owner, "Record purchase", ModalityType.APPLICATION_MODAL);
        this.viewModel = viewModel;
        this.controller = controller;
        this.createProductFlow = createProductFlow;
        viewModel.setState(new RecordPurchaseState());
        viewModel.addPropertyChangeListener(this);

        unitBox.setEditable(true);
        purchaseDateField.setText(today.toString());
        ButtonGroup sourceGroup = new ButtonGroup();
        sourceGroup.add(packageButton);
        sourceGroup.add(estimateButton);
        confirmBox.setEnabled(false);

        JButton newProduct = new JButton("New product…");
        newProduct.addActionListener(event -> createProduct());
        JPanel productRow = new JPanel(new BorderLayout(4, 0));
        productRow.add(productBox, BorderLayout.CENTER);
        productRow.add(newProduct, BorderLayout.EAST);

        JPanel form = Forms.newForm();
        Forms.addRow(form, 0, "Product", productRow);
        Forms.addRow(form, 1, "Total price (CAD)", priceField);
        Forms.addRow(form, 2, "Quantity", quantityField);
        Forms.addRow(form, 3, "Unit", unitBox);
        Forms.addRow(form, 4, "Purchase date", purchaseDateField);
        Forms.addRow(form, 5, "Target date from", packageButton);
        Forms.addRow(form, 6, "", estimateButton);
        Forms.addRow(form, 7, "Target date", targetDateField);
        Forms.addRow(form, 8, "", confirmBox);
        Forms.addWide(form, 9, hintLabel);
        Forms.addWide(form, 10, errorLabel);

        JButton save = new JButton("Save");
        save.addActionListener(event -> save());
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(event -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancel);
        buttons.add(save);

        packageButton.addActionListener(event -> {
            confirmBox.setSelected(false);
            confirmBox.setEnabled(false);
            Forms.show(hintLabel, "Type the date printed on the package.");
        });
        estimateButton.addActionListener(event -> {
            confirmBox.setEnabled(true);
            requestSuggestion();
        });
        productBox.addActionListener(event -> {
            if (estimateButton.isSelected()) {
                requestSuggestion();
            }
        });
        purchaseDateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                if (estimateButton.isSelected()) {
                    requestSuggestion();
                }
            }
        });

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);

        controller.loadProducts();
        pack();
        setLocationRelativeTo(owner);
    }

    private void createProduct() {
        Optional<String> createdId = createProductFlow.get();
        if (createdId.isPresent()) {
            productToSelect = createdId.get();
            controller.loadProducts();
        }
    }

    private void requestSuggestion() {
        controller.suggestTargetDate(selectedProductId(), purchaseDateField.getText());
    }

    private void save() {
        controller.execute(selectedProductId(), priceField.getText(), quantityField.getText(),
                String.valueOf(unitBox.getSelectedItem()), purchaseDateField.getText(), targetDateField.getText(),
                packageButton.isSelected(), confirmBox.isSelected());
    }

    private String selectedProductId() {
        ProductChoice choice = (ProductChoice) productBox.getSelectedItem();
        return choice == null ? null : choice.getProductId();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        RecordPurchaseState state = viewModel.getState();
        switch (event.getPropertyName()) {
            case "products":
                productBox.removeAllItems();
                for (ProductChoice choice : state.getProductChoices()) {
                    productBox.addItem(choice);
                    if (choice.getProductId().equals(productToSelect)) {
                        productBox.setSelectedItem(choice);
                    }
                }
                break;
            case "suggestion":
                if (state.getSuggestedTargetDate() != null) {
                    targetDateField.setText(state.getSuggestedTargetDate());
                }
                confirmBox.setSelected(false);
                Forms.show(hintLabel, state.getSuggestionMessage());
                break;
            case "saved":
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
