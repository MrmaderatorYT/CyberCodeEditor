package com.ccs.cybercodeeditor;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlighter implements TextWatcher {

    private EditText codeEditText;

    public SyntaxHighlighter(EditText codeEditText) {
        this.codeEditText = codeEditText;
    }

    @Override
    public void afterTextChanged(Editable s) {
        highlightSyntax(s);
    }

    private void highlightSyntax(Editable editable) {
        String code = editable.toString();
        SpannableStringBuilder spannable = new SpannableStringBuilder(code);

        String[] keywords = {"abstract", "assert", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "default", "do", "double", "else", "enum",
                "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
                "instanceof", "int", "interface", "long", "native", "new", "package", "private",
                "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
                "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile",
                "while"};

        // Підсвічування ключових слів
        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
            Matcher matcher = pattern.matcher(code);
            while (matcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(Color.BLUE),
                        matcher.start(), matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        // Підсвічування крапок з комою
        Pattern pattern = Pattern.compile(";");
        Matcher matcher = pattern.matcher(code);
        while (matcher.find()) {
            spannable.setSpan(new ForegroundColorSpan(Color.BLUE),
                    matcher.start(), matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Оновлення EditText
        codeEditText.removeTextChangedListener(this);
        codeEditText.setText(spannable);
        codeEditText.setSelection(spannable.length());
        codeEditText.addTextChangedListener(this);
    }

    // Інші необхідні методи інтерфейсу TextWatcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

}