package utility;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Creates a custom list renderer designed to render checkboxes.
 * @author Dovydas Rupsys
 */
public class CheckboxListRenderer implements ListCellRenderer<JCheckBox> {
	//Components used by the renderer
	JCheckBox _checkbox = new JCheckBox();
	JPanel _panel = new JPanel(new BorderLayout());
	
	/**
	 * Constructs the check box list renderer.
	 */
	public CheckboxListRenderer() {
		_panel.add(_checkbox);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index, boolean isSelected, boolean cellHasFocus) {
		//updates the properties of the component to match the properties of the checkbox that is currently being drawn
		_checkbox.setText(value.getText());
		_checkbox.setBackground(list.getBackground());
		_checkbox.setForeground(list.getForeground());
		
		//if the list item's selection value is different from the checkboxe's selection value then update that value
		if (isSelected != value.isSelected())
			value.setSelected(isSelected);
		
		_checkbox.setSelected(value.isSelected());
		
		return _panel;
	}
}
