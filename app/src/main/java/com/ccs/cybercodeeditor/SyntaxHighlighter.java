// SyntaxHighlighter.java
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
    private static final Pattern PATTERN = Pattern.compile(
            "(\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|" +
                    "default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|" +
                    "import|instanceof|int|interface|long|native|new|package|private|protected|public|" +
                    "return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|" +
                    "try|void|volatile|while)\\b)|(;)|(\".*\")|(//.*)|(/\\*.*?\\*/)|(\\b\\d+\\b)"
    );

    private EditText codeEditText;
    private boolean isFormatting;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isFormatting) return;

        isFormatting = true;
        highlightSyntax(editable);
        isFormatting = false;
    }

    private void highlightSyntax(Editable editable) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(editable);
        clearSpans(spannable);

        Matcher matcher = PATTERN.matcher(spannable);
        while (matcher.find()) {
            int color = Color.BLACK;
            if (matcher.group(1) != null) { // Keywords
                color = Color.BLUE;
            } else if (matcher.group(3) != null) { // Strings
                color = Color.rgb(0, 150, 0);
            } else if (matcher.group(4) != null || matcher.group(5) != null) { // Comments
                color = Color.GRAY;
            } else if (matcher.group(6) != null) { // Numbers
                color = Color.RED;
            }

            spannable.setSpan(
                    new ForegroundColorSpan(color),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        codeEditText.removeTextChangedListener(this);
        codeEditText.setText(spannable);
        codeEditText.setSelection(spannable.length());
        codeEditText.addTextChangedListener(this);
    }

    private void clearSpans(Spannable spannable) {
        ForegroundColorSpan[] spans = spannable.getSpans(
                0, spannable.length(), ForegroundColorSpan.class
        );
        for (ForegroundColorSpan span : spans) {
            spannable.removeSpan(span);
        }
    }
}