package savebite.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Small helpers for building label-and-field forms.
 */
final class Forms {
    static final Color ERROR_COLOR = new Color(0xB00020);

    private Forms() {
    }

    static JPanel newForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return form;
    }

    /**
     * Adds a label in the first column and a field in the second column of the given row.
     */
    static void addRow(JPanel form, int row, String label, JComponent field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.LINE_END;
        labelConstraints.insets = new Insets(4, 4, 4, 8);
        form.add(new JLabel(label), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1;
        fieldConstraints.insets = new Insets(4, 4, 4, 4);
        form.add(field, fieldConstraints);
    }

    /**
     * Adds a component spanning both columns of the given row.
     */
    static void addWide(JPanel form, int row, JComponent component) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4, 4, 4, 4);
        form.add(component, constraints);
    }

    static JLabel errorLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(ERROR_COLOR);
        return label;
    }

    /**
     * Shows text in a label; a blank message keeps the label's height.
     */
    static void show(JLabel label, String message) {
        label.setText(message == null || message.isBlank() ? " " : "<html>" + escape(message) + "</html>");
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
