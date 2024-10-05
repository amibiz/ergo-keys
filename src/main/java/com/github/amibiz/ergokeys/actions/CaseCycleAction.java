/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class CaseCycleAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(CaseCycleAction.class);

    /**
     * Converts a camelCase string to snake_case.
     *
     * @param str The camelCase string.
     * @return The snake_case converted string.
     */
    private static String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String regex = "([a-z0-9])([A-Z])";
        String replacement = "$1_$2";
        return str.replaceAll(regex, replacement).toLowerCase();
    }

    /**
     * Converts a snake_case string to kebab-case.
     *
     * @param str The snake_case string.
     * @return The kebab-case converted string.
     */
    private static String snakeToKebab(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.replace('_', '-');
    }

    /**
     * Converts a kebab-case string to PascalCase.
     *
     * @param str The kebab-case string.
     * @return The PascalCase converted string.
     */
    private static String kebabToPascal(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true; // Always capitalize first character

        for (char c : str.toCharArray()) {
            if (c == '-') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    /**
     * Converts a PascalCase string to camelCase.
     *
     * @param str The PascalCase string.
     * @return The camelCase converted string.
     */
    private static String pascalToCamel(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        if (!Character.isUpperCase(str.charAt(0))) {
            return str; // Not PascalCase
        }

        StringBuilder result = new StringBuilder(str);
        result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        return result.toString();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Obtain the editor and project from the event
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getProject();

        if (editor == null || project == null) {
            return;
        }

        // Get the caret and document
        Caret caret = editor.getCaretModel().getCurrentCaret();
        Document document = editor.getDocument();

        // Determine the range to operate on
        int startOffset;
        int endOffset;
        String textToTransform;
        boolean hasSelection = caret.hasSelection();

        if (hasSelection) {
            // If there is a selection, use it
            startOffset = caret.getSelectionStart();
            endOffset = caret.getSelectionEnd();
        } else {
            // No selection; get the word under the caret
            int caretOffset = caret.getOffset();
            int[] wordBounds = getWordBoundsAtCaret(editor, caretOffset);
            if (wordBounds == null) {
                return;
            }
            startOffset = wordBounds[0];
            endOffset = wordBounds[1];
        }
        textToTransform = document.getText(new TextRange(startOffset, endOffset));

        // Detect the case and convert accordingly
        String newText;
        if (isCamelCase(textToTransform)) {
            newText = camelToSnake(textToTransform);
        } else if (isSnakeCase(textToTransform)) {
            newText = snakeToKebab(textToTransform);
        } else if (isKebabCase(textToTransform)) {
            newText = kebabToPascal(textToTransform);
        } else if (isPascalCase(textToTransform)) {
            newText = pascalToCamel(textToTransform);
        } else {
            // Default to camelCase if case is unknown
            newText = textToTransform;
        }

        // Replace the text in a write action
        final String transformedText = newText;
        final int transformedEndOffset = startOffset + transformedText.length();
        Runnable runnable = () -> {
            document.replaceString(startOffset, endOffset, transformedText);
            if (hasSelection) {
                // Adjust selection to new text length
                caret.setSelection(startOffset, transformedEndOffset);
            } else {
                // Select the transformed word
                caret.removeSelection();
                caret.moveToOffset(startOffset);
                caret.setSelection(startOffset, transformedEndOffset);
            }
        };
        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    /**
     * Determines the start and end offsets of the word at the given caret position.
     *
     * @param editor The editor instance.
     * @param offset The caret offset.
     * @return An array with start and end offsets, or null if no word is found.
     */
    private int[] getWordBoundsAtCaret(Editor editor, int offset) {
        CharSequence text = editor.getDocument().getCharsSequence();
        int textLength = text.length();

        if (offset < 0 || offset > textLength) {
            return null;
        }

        // Move backward to find the start of the word
        int start = offset;
        while (start > 0 && (Character.isJavaIdentifierPart(text.charAt(start - 1)) || text.charAt(start - 1) == '-')) {
            start--;
        }

        // Move forward to find the end of the word
        int end = offset;
        while (end < textLength && (Character.isJavaIdentifierPart(text.charAt(end)) || text.charAt(end) == '-')) {
            end++;
        }

        if (start == end) {
            return null;
        }

        return new int[]{start, end};
    }

    /**
     * Checks if the string is in camelCase.
     *
     * @param str The input string.
     * @return True if camelCase, false otherwise.
     */
    private boolean isCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        if (str.contains("_") || str.contains("-")) {
            return false;
        }
        // Ensure the first character is lowercase
        if (!Character.isLowerCase(str.charAt(0))) {
            return false;
        }
        // Ensure there is at least one uppercase character
        boolean hasUppercase = false;
        for (int i = 1; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                hasUppercase = true;
                break;
            }
        }
        return hasUppercase;
    }

    /**
     * Checks if the string is in PascalCase.
     *
     * @param str The input string.
     * @return True if PascalCase, false otherwise.
     */
    private boolean isPascalCase(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        if (str.contains("_") || str.contains("-")) {
            return false;
        }
        // Ensure the first character is uppercase
        if (!Character.isUpperCase(str.charAt(0))) {
            return false;
        }
        // Ensure there is at least one uppercase character after the first character
        boolean hasAdditionalUppercase = false;
        for (int i = 1; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                hasAdditionalUppercase = true;
                break;
            }
        }
        return hasAdditionalUppercase;
    }

    /**
     * Checks if the string is in snake_case.
     *
     * @param str The input string.
     * @return True if snake_case, false otherwise.
     */
    private boolean isSnakeCase(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.contains("_") && !str.contains("-");
    }

    /**
     * Checks if the string is in kebab-case.
     *
     * @param str The input string.
     * @return True if kebab-case, false otherwise.
     */
    private boolean isKebabCase(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.contains("-") && !str.contains("_");
    }
}
