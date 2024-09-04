package com.example.tugaspertemuan4

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private lateinit var res: TextView
    private var input = ""
    private var displayInput = ""
    private var samadengan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.textView)
        res = findViewById(R.id.resultView)

        val buttons = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
            R.id.buttonPlus, R.id.buttonMinus, R.id.buttonMultiply, R.id.buttonDivide,
            R.id.buttonDot, R.id.buttonPercent, R.id.buttonEqual
        )

        for (button in buttons) {
            findViewById<Button>(button).setOnClickListener { handleInput(it as Button) }
        }

        findViewById<Button>(R.id.buttonAC).setOnClickListener {
            clear()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleInput(button: Button) {
        when (button.text) {
            "=" -> {
                if (input.isNotEmpty()) {
                    try {
                        val result = evaluateExpression(input)
                        res.text = result
                        input = result
                        displayInput = result
                        samadengan = true
                    } catch (e: Exception) {
                        res.text = "Error"
                        input = ""
                        displayInput = ""
                    }
                }
            }
            "%" -> {
                if (input.isNotEmpty()) {
                    displayInput += "%"
                    input += "*0.01"
                    display.text = displayInput
                }
            }
            "(" -> {
                displayInput += "("
                input += "("
                display.text = displayInput
            }
            ")" -> {
                displayInput += ")"
                input += ")"
                display.text = displayInput
            }
            else -> {
                if (samadengan) {
                    input = ""
                    displayInput = ""
                    samadengan = false
                }
                displayInput += button.text
                input += button.text
                display.text = displayInput
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun evaluateExpression(expression: String): String {
        return try {
            val result = evaluate(tokenize(expression))
            if (result % 1 == 0.0) {
                result.toInt().toString()
            } else {
                return String.format("%.10f", result).trimEnd('0').trimEnd('.')
            }
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun tokenize(expression: String): String {
        return expression.replace("x", "*").replace("÷", "/")
    }

    private fun evaluate(expression: String): Double {
        val tokens = expression
        val values = Stack<Double>()
        val operators = Stack<Char>()

        var i = 0
        while (i < tokens.length) {
            val token = tokens[i]

            when {
                token == '(' -> {
                    var j = i
                    var parentheses = 1
                    while (parentheses > 0) {
                        j++
                        if (tokens[j] == '(') parentheses++
                        else if (tokens[j] == ')') parentheses--
                    }
                    val subExpression = tokens.substring(i + 1, j)
                    values.push(evaluate(subExpression))
                    i = j
                }
                token.isDigit() || token == '.' -> {
                    val sb = StringBuilder()
                    while (i < tokens.length && (tokens[i].isDigit() || tokens[i] == '.')) {
                        sb.append(tokens[i++])
                    }
                    values.push(sb.toString().toDouble())
                    i-- // Step back to handle next operator
                }
                token == '+' || token == '-' || token == '*' || token == '/' -> {
                    while (operators.isNotEmpty() && precedence(operators.peek()) >= precedence(token)) {
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()))
                    }
                    operators.push(token)
                }
            }
            i++
        }

        while (operators.isNotEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    private fun precedence(op: Char): Int {
        return when (op) {
            '+', '-' -> 1
            '*', '/' -> 2
            else -> 0
        }
    }

    private fun applyOperator(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            else -> 0.0
        }
    }

    private fun clear() {
        input = ""
        displayInput = ""
        display.text = ""
        res.text = ""
        samadengan = false
    }
}
