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
    private val cats: List<Cat> = allCats()
    private val done: List<IntArray> = cats.map { c: Cat -> IntArray(c.items.size) }
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

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun label(t: String, size: Float, color: Int = Color.WHITE): TextView {
        val v = TextView(this)
        v.text = t
        v.textSize = size
        v.setTextColor(color)
        return v
    }

    private fun showList() {
        inList = true
        val col = LinearLayout(this)
        col.orientation = LinearLayout.VERTICAL
        col.setPadding(dp(20), dp(28), dp(20), dp(24))

        val title = label("ذِكْرك", 32f, green)
        title.gravity = Gravity.CENTER
        title.setPadding(0, 0, 0, dp(12))
        col.addView(title)

        for (i in cats.indices) {
            val c = cats[i]
            var total = 0
            for (item in c.items) total += item.times
            val d = done[i].sum()

            val row = LinearLayout(this)
            row.orientation = LinearLayout.VERTICAL
            row.setPadding(0, dp(14), 0, dp(14))
            row.setOnClickListener { open(i) }
            row.addView(label(c.name, 18f))
            row.addView(label("قراءة اليوم $d/$total", 13f, gray))
            col.addView(row)

            val line = View(this)
            line.setBackgroundColor(Color.parseColor("#262C30"))
            line.layoutParams = LinearLayout.LayoutParams(-1, dp(1))
            col.addView(line)
        }

        val sv = ScrollView(this)
        sv.setBackgroundColor(bg)
        sv.layoutDirection = View.LAYOUT_DIRECTION_RTL
        sv.addView(col)
        setContentView(sv)
    }

    private fun open(i: Int) {
        cur = i
        inList = false
        val items = cats[i].items
        var first = 0
        for (k in items.indices) {
            if (done[i][k] < items[k].times) {
                first = k
                break
            }
        }
        idx = first
        showZekr()
    }

    private fun showZekr() {
        val c = cats[cur]
        val z = c.items[idx]
        val d = done[cur][idx]
        val finished = d >= z.times

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(bg)
        root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        root.setPadding(dp(20), dp(28), dp(20), dp(20))

        val head = label(c.name, 20f, green)
        head.gravity = Gravity.CENTER
        root.addView(head)

        val counter = label("${idx + 1} / ${c.items.size}", 14f, gray)
        counter.gravity = Gravity.CENTER
        counter.setPadding(0, dp(4), 0, dp(12))
        root.addView(counter)

        val body = label(z.text, 22f)
        body.gravity = Gravity.CENTER
        body.setLineSpacing(0f, 1.5f)
        val scroll = ScrollView(this)
        scroll.addView(body)
        scroll.layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
        root.addView(scroll)

        val circle = GradientDrawable()
        circle.shape = GradientDrawable.OVAL
        circle.setColor(
            if (finished) Color.parseColor("#2A3238") else Color.parseColor("#1E7A46")
        )

        val tap = label(if (finished) "تم ✓" else "${z.times - d}", 44f)
        tap.gravity = Gravity.CENTER
        tap.background = circle
        val tapParams = LinearLayout.LayoutParams(dp(160), dp(160))
        tapParams.gravity = Gravity.CENTER_HORIZONTAL
        tapParams.topMargin = dp(16)
        tapParams.bottomMargin = dp(8)
        tap.layoutParams = tapParams
        tap.setOnClickListener {
            if (!finished) {
                done[cur][idx]++
                if (done[cur][idx] >= z.times && idx < c.items.size - 1) idx++
                showZekr()
            }
        }
        root.addView(tap)

        val hint = label(if (finished) "" else "المطلوب: ${z.times} مرة", 13f, gray)
        hint.gravity = Gravity.CENTER
        root.addView(hint)

        val nav = LinearLayout(this)
        nav.orientation = LinearLayout.HORIZONTAL

        val prevBtn = Button(this)
        prevBtn.text = "السابق"
        prevBtn.isEnabled = idx > 0
        prevBtn.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        prevBtn.setOnClickListener {
            idx--
            showZekr()
        }

        val nextBtn = Button(this)
        nextBtn.text = "التالي"
        nextBtn.isEnabled = idx < c.items.size - 1
        nextBtn.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        nextBtn.setOnClickListener {
            idx++
            showZekr()
        }

        nav.addView(prevBtn)
        nav.addView(nextBtn)
        root.addView(nav)
        setContentView(root)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!inList) showList() else super.onBackPressed()
    }
}
