package com.mileskrell.strikethrough

internal fun strikeText(input: CharSequence) = buildString {
    input.forEach {
        append(it)
        append('\u0336')
    }
}
