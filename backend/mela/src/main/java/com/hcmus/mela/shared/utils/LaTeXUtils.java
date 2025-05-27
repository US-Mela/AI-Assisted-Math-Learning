package com.hcmus.mela.shared.utils;

/**
 * Utility class for LaTeX operations
 */
public class LaTeXUtils {

    /**
     * Normalizes LaTeX delimiters in a text string for proper rendering
     * Converts single-backslash delimiters to double-backslash delimiters
     * only if they aren't already doubled.
     *
     * @param text The text containing LaTeX expressions
     * @return Normalized text with properly escaped LaTeX delimiters
     */
    public static String normalizeLaTeX(String text) {
        if (text == null) return "";

        StringBuilder sb = new StringBuilder();
        boolean isInLaTeX = false;  // Check if we are in a LaTeX environment

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            // If we are in a LaTeX environment
            if (isInLaTeX) {
                // If we catch a closing delimiter \) or \], we exit the LaTeX environment
                if (currentChar == '\\' && i + 1 < text.length()
                        && (text.charAt(i + 1) == ')' || text.charAt(i + 1) == ']')) {
                    isInLaTeX = false;
                    if (i - 1 >= 0 && text.charAt(i - 1) != '\\') {
                        sb.append("\\");
                    }
                }
                else if (currentChar == '\\'
                        && i + 1 < text.length() && text.charAt(i + 1) != '\\'
                        && i - 1 >= 0 && text.charAt(i - 1) != '\\') {
                    if((text.charAt(i + 1) == 'n' || text.charAt(i + 1) == 't' || text.charAt(i + 1) == 'r')
                            && i + 2 < text.length() && (text.charAt(i + 2) == ' ' || text.charAt(i + 2) == '\\')) {
                        continue;
                    }
                    sb.append("\\");
                }
            }
            // If we are not in a LaTeX environment
            else {
                // If we catch an opening delimiter \[ or \(, we enter the LaTeX environment
                if (currentChar == '\\' && i + 1 < text.length()
                        && (text.charAt(i + 1) == '(' || text.charAt(i + 1) == '[')) {
                    isInLaTeX = true;
                    if (i - 1 >= 0 && text.charAt(i - 1) != '\\') {
                        sb.append("\\");  // Thêm dấu \ nếu cần
                    }
                }
            }

            sb.append(currentChar);
        }

        return sb.toString();
    }
}
