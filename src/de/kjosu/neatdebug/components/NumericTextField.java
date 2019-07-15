package de.kjosu.neatdebug.components;

import javafx.scene.control.TextField;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class NumericTextField extends TextField {

    private final NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
    private Number number;

    public NumericTextField() {
        super("0");
    }

    public NumericTextField(int value) {
        super(String.valueOf(value));
    }

	@Override
    public void replaceText(final int start, final int end, final String text)
    {
        String oldText = getText();

        if (getText().equals("0") && start == 1) {
            setText(text);
            positionCaret(1);
        } else {
            super.replaceText(start, end, text);
        }

        checkText();

        if (!validate(getText())) {
            setText(oldText);
        }
    }

    @Override
    public void replaceSelection(final String text)
    {
        super.replaceSelection(text);
    }

    private void checkText() {
        if (getText().isEmpty()) {
            setText("0");
            positionCaret(1);
        } else if (getText().startsWith("0") && getText().length() > 1) {
            setText(getText().substring(1));
        } else if (getText().equals("-")) {
        	setText("0");
        }
    }

    private boolean validate(final String text)
    {
        try {
            number = format.parse(text.replace(',', '.'));
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public double getValue() {
	    if (validate(getText())) {
	        return number.doubleValue();
        } else {
	        return 0;
        }
    }
}
