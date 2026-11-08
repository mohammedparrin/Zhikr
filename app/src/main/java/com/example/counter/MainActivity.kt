package com.example.counter

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private val cats = allCats()
    private val done = cats.map { IntArray(it.items.size) }
    private var inList = true
    private var cur = 0
    private var idx = 0
    private val bg = Color.parseColor("#14181B")
    private val green = Color.parseColor("#2ECC71")
    private val gray = Color.parseColor("#8A9399")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        showList()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun tv(t: String, size: Float, color: Int = Color.WHITE) =
        TextView(this).apply { text = t; textSize = size; setTextColor(color) }

    private fun showList() {
        inList = true
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(28), dp(20), dp(24))
        }
        col.addView(tv("ذِكْرك", 32f, green).apply {
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(12))
        })
        cats.forEachIndexed { i, c ->
            val total = c.items.sumOf { it.times }
            val d = done[i].sum()
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, dp(14), 0, dp(14))
                setOnClickListener { open(i) }
            }
            row.addView(tv(c.name, 18f))
            row.addView(tv("قراءة اليوم $d/$total", 13f, gray))
            col.addView(row)
            col.addView(View(this).apply {
                setBackgroundColor(Color.parseColor("#262C30"))
                layoutParams = LinearLayout.LayoutParams(-1, dp(1))
            })
        }
        val sv = ScrollView(this).apply {
            setBackgroundColor(bg)
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            addView(col)
        }
        setContentView(sv)
    }

    private fun open(i: Int) {
        cur = i
        inList = false
        val items = cats[i].items
        idx = items.indices.firstOrNull { done[i][it] < items[it].times } ?: 0
        showZekr()
    }

    private fun showZekr() {
        val c = cats[cur]
        val z = c.items[idx]
        val d = done[cur][idx]
        val finished = d >= z.times

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(20), dp(28), dp(20), dp(20))
        }
        root.addView(tv(c.name, 20f, green).apply { gravity = Gravity.CENTER })
        root.addView(tv("${idx + 1} / ${c.items.size}", 14f, gray).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(4), 0, dp(12))
        })

        val text = tv(z.text, 22f).apply {
            gravity = Gravity.CENTER
            setLineSpacing(0f, 1.5f)
        }
        root.addView(ScrollView(this).apply {
            addView(text)
            layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
        })

        val circle = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(if (finished) Color.parseColor("#2A3238") else Color.parseColor("#1E7A46"))
        }
        val tap = tv(if (finished) "تم ✓" else "${z.times - d}", 44f).apply {
            gravity = Gravity.CENTER
            background = circle
            layoutParams = LinearLayout.LayoutParams(dp(160), dp(160)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dp(16)
                bottomMargin = dp(8)
            }
            setOnClickListener {
                if (!finished) {
                    done[cur][idx]++
                    if (done[cur][idx] >= z.times && idx < c.items.size - 1) idx++
                    showZekr()
                }
            }
        }
        root.addView(tap)
        root.addView(tv(if (finished) "" else "المطلوب: ${z.times} مرة", 13f, gray).apply {
            gravity = Gravity.CENTER
        })

        val nav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val prev = Button(this).apply {
            text = "السابق"
            isEnabled = idx > 0
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            setOnClickListener { idx--; showZekr() }
        }
        val next = Button(this).apply {
            text = "التالي"
            isEnabled = idx < c.items.size - 1
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            setOnClickListener { idx++; showZekr() }
        }
        nav.addView(prev)
        nav.addView(next)
        root.addView(nav)
        setContentView(root)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!inList) showList() else super.onBackPressed()
    }
}
