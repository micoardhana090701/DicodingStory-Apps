package com.example.dicodingapi

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat


class EditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearInput: Drawable
    private lateinit var emailInput: Drawable
    private lateinit var passwordInput: Drawable
    private lateinit var usernameInput: Drawable
    private lateinit var borderInput: Drawable
    private var inUsername: Boolean = false
    private var inEmail: Boolean = false
    private var inPassword: Boolean = false

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setPadding(32, 32, 32, 32)
        background = borderInput
        gravity = Gravity.CENTER_VERTICAL
        compoundDrawablePadding = 16
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int = 0) {
        val cons = context.obtainStyledAttributes(attrs, R.styleable.EditText, defStyleAttr, 0)

        inEmail = cons.getBoolean(R.styleable.EditText_email, false)
        inPassword = cons.getBoolean(R.styleable.EditText_password, false)
        inUsername = cons.getBoolean(R.styleable.EditText_username, false)
        borderInput = ContextCompat.getDrawable(context, R.drawable.border_input) as Drawable
        clearInput = ContextCompat.getDrawable(context, R.drawable.baseline_clear_24) as Drawable
        emailInput =
            ContextCompat.getDrawable(context, R.drawable.baseline_mail_outline_24) as Drawable
        passwordInput = ContextCompat.getDrawable(context, R.drawable.baseline_lock_24) as Drawable
        usernameInput =
            ContextCompat.getDrawable(context, R.drawable.baseline_person_24) as Drawable

        if (inEmail) {
            setButton(startOfTheText = emailInput)
        } else if (inPassword) {
            setButton(startOfTheText = passwordInput)
        } else if (inUsername) {
            setButton(startOfTheText = usernameInput)
        }
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val errorEmail = "Your Email Format Is Wrong"
                val errorPassword = "8 minimum character"

                if(s.toString().isNotEmpty()) showClearButton() else hideClearButton()
                error =
                    if (inPassword && input.length < 8 && input.isNotEmpty()) {
                        errorPassword
                    } else if (inEmail && !input.isValidEmail()) {
                        errorEmail
                    } else {
                        null
                    }
            }

            override fun afterTextChanged(s: Editable?) {

            }


        })
    }


    private fun setButton(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var clearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearInput.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> clearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearInput.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> clearButtonClicked = true
                }
            }
            if (clearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearInput = ContextCompat.getDrawable(
                            context,
                            R.drawable.baseline_clear_24
                        ) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearInput = ContextCompat.getDrawable(
                            context,
                            R.drawable.baseline_clear_24
                        ) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            }
            else return false
        }
        return false
    }

    private fun hideClearButton() {
        when {
            inEmail -> setButton(startOfTheText = emailInput)
            inPassword -> setButton(startOfTheText = passwordInput)
            inUsername -> setButton(startOfTheText = usernameInput)
            else -> setButton()
        }
    }

    private fun showClearButton() {
        when {
            inEmail -> setButton(
                startOfTheText = emailInput,
                endOfTheText = clearInput
            )
            inPassword -> setButton(
                startOfTheText = passwordInput,
                endOfTheText = clearInput
            )
            inUsername -> setButton(
                startOfTheText = usernameInput,
                endOfTheText = clearInput
            )
            else -> setButton(endOfTheText = clearInput)
        }
    }

}



