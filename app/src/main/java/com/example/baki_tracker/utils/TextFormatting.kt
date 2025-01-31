package com.example.baki_tracker.utils

import android.icu.text.DecimalFormatSymbols
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.Locale

private val symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH)
private val groupingSeparator = symbols.groupingSeparator
private val decimalSeparator = symbols.decimalSeparator

fun formatFloatingPoint(input: String): String {
    if (input.matches("\\D".toRegex())) return ""
    if (input.matches("0+".toRegex())) return "0"

    val sb = StringBuilder()

    var decimalPlaces = 0
    var hasDecimalSep = false

    for (char in input) {
        if (hasDecimalSep && char.isDigit()) decimalPlaces++
        if (char.isDigit() && decimalPlaces <= 2) {
            sb.append(char)
            continue
        }
        if (
            (char == decimalSeparator || char == groupingSeparator) &&
            !hasDecimalSep &&
            sb.isNotEmpty()
        ) {
            sb.append(char)
            hasDecimalSep = true
        }
    }

    return sb.toString()
}

fun formatFloatingPointForVisual(input: String): String {
    val split = input.split(decimalSeparator, groupingSeparator)

    val intPart = split[0]

    val fractionPart = split.getOrNull(1)

    return if (fractionPart == null) intPart else intPart + decimalSeparator + fractionPart
}

class DecimalInputVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val inputText = text.text
        val formattedNumber = formatFloatingPointForVisual(inputText)

        val newText =
            AnnotatedString(
                text = formattedNumber,
                spanStyles = text.spanStyles,
                paragraphStyles = text.paragraphStyles,
            )

        val offsetMapping =
            FixedCursorOffsetMapping(
                contentLength = inputText.length,
                formattedContentLength = formattedNumber.length,
            )

        return TransformedText(newText, offsetMapping)
    }
}

private class FixedCursorOffsetMapping(
    private val contentLength: Int,
    private val formattedContentLength: Int,
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int = formattedContentLength

    override fun transformedToOriginal(offset: Int): Int = contentLength
}