package com.danhha.xauudssmartsignal

import android.app.Activity
import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class MainActivity : Activity() {
    private lateinit var price: EditText
    private lateinit var atr: EditText
    private lateinit var rsi: EditText
    private lateinit var h4: Spinner
    private lateinit var h1: Spinner
    private lateinit var m15: Spinner
    private lateinit var m5: Spinner
    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16, 36, 58)
        window.navigationBarColor = android.graphics.Color.rgb(16, 36, 58)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 20, 22, 28)
            setBackgroundColor(android.graphics.Color.rgb(245, 247, 250))
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)

        content.addView(text("XAUUSD SMART SIGNAL", 23f, true, 0xFF10243A.toInt()))
        content.addView(text("Phân tích đa khung H4 • H1 • M15 • M5", 14f, false, 0xFF526273.toInt()))
        content.addView(space(14))
        content.addView(text("DỮ LIỆU ĐẦU VÀO", 16f, true, 0xFF10243A.toInt()))
        price = field("Giá XAUUSD hiện tại (ví dụ 2350.50)")
        atr = field("ATR(14) khung M15 (ví dụ 2.50)")
        rsi = field("RSI(14) khung M15 (0–100)")
        content.addView(price); content.addView(atr); content.addView(rsi)
        h4 = spinner("Tăng", "Giảm", "Đi ngang")
        h1 = spinner("Vùng cầu / hỗ trợ", "Vùng cung / kháng cự", "Giữa vùng / chưa rõ")
        m15 = spinner("CHoCH/BOS tăng", "CHoCH/BOS giảm", "Chưa xác nhận")
        m5 = spinner("Sweep đáy + nến tăng xác nhận", "Sweep đỉnh + nến giảm xác nhận", "Chưa có tín hiệu")
        addLabeledSpinner(content, "Xu hướng H4", h4)
        addLabeledSpinner(content, "Vị trí giá H1", h1)
        addLabeledSpinner(content, "Xác nhận cấu trúc M15", m15)
        addLabeledSpinner(content, "Kích hoạt vào lệnh M5", m5)

        val button = Button(this).apply {
            text = "PHÂN TÍCH TÍN HIỆU"
            textSize = 16f
            setOnClickListener { analyze() }
        }
        content.addView(button, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 14 })
        result = TextView(this).apply {
            text = "Chưa có tín hiệu. Nhập dữ liệu và nhấn Phân tích."
            textSize = 15f
            setTextColor(0xFF263746.toInt())
            setPadding(16, 16, 16, 16)
            setBackgroundColor(android.graphics.Color.WHITE)
        }
        content.addView(result, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 14 })
        content.addView(space(12))
        content.addView(text("Quản trị rủi ro", 16f, true, 0xFF10243A.toInt()))
        content.addView(text("• Chỉ xem xét tín hiệu sau khi nến M5 đóng.\n• R:R mục tiêu tối thiểu 1:2.\n• Tối đa 3 tín hiệu/ngày; dừng sau 2 lệnh thua.\n• Không giữ lệnh qua đêm.\n• Với tài khoản 15 USD, không vào lệnh nếu khối lượng tối thiểu 0.01 lot làm mức lỗ dự kiến vượt rủi ro cho phép.", 14f, false, 0xFF34495E.toInt()))
        content.addView(space(12))
        content.addView(text("Lưu ý: Đây là công cụ hỗ trợ phân tích dựa trên dữ liệu bạn nhập, không phải dữ liệu giá trực tiếp và không bảo đảm lợi nhuận. Hãy tự kiểm tra spread, giá trị pip và khối lượng trên HFM trước khi giao dịch.", 12f, false, 0xFF8A4B08.toInt()))
        root.addView(scroll)
        setContentView(root)
    }

    private fun text(s: String, size: Float, bold: Boolean, color: Int) = TextView(this).apply {
        text = s; textSize = size; setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
        setPadding(0, 5, 0, 5)
    }
    private fun space(h: Int) = TextView(this).apply { height = h }
    private fun field(hintText: String) = EditText(this).apply {
        hint = hintText; textSize = 15f; setSingleLine(true)
        inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        setPadding(12, 10, 12, 10)
        background = getDrawable(android.R.drawable.edit_text)
        layoutParams = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 7 }
    }
    private fun spinner(vararg items: String) = Spinner(this).apply {
        adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, items.toList())
    }
    private fun addLabeledSpinner(parent: LinearLayout, label: String, sp: Spinner) {
        parent.addView(text(label, 14f, true, 0xFF34495E.toInt()))
        parent.addView(sp)
    }

    private fun analyze() {
        val p = price.text.toString().toDoubleOrNull()
        val a = atr.text.toString().toDoubleOrNull()
        val r = rsi.text.toString().toDoubleOrNull()
        if (p == null || a == null || r == null || p <= 0 || a <= 0 || r !in 0.0..100.0) {
            result.text = "Vui lòng nhập giá, ATR và RSI hợp lệ."
            return
        }
        val h4Up = h4.selectedItemPosition == 0
        val h4Down = h4.selectedItemPosition == 1
        val atDemand = h1.selectedItemPosition == 0
        val atSupply = h1.selectedItemPosition == 1
        val m15Up = m15.selectedItemPosition == 0
        val m15Down = m15.selectedItemPosition == 1
        val m5Buy = m5.selectedItemPosition == 0
        val m5Sell = m5.selectedItemPosition == 1

        val buyScore = listOf(h4Up, atDemand, m15Up, m5Buy, r in 45.0..65.0).count { it }
        val sellScore = listOf(h4Down, atSupply, m15Down, m5Sell, r in 35.0..55.0).count { it }
        val direction = when {
            buyScore >= 4 && h4Up && m15Up && m5Buy && atDemand -> "BUY"
            sellScore >= 4 && h4Down && m15Down && m5Sell && atSupply -> "SELL"
            else -> "WAIT"
        }
        if (direction == "WAIT") {
            result.text = "TÍN HIỆU: WAIT\nChưa đủ đồng thuận đa khung. BUY score: $buyScore/5 | SELL score: $sellScore/5\nKhông nên vào lệnh chỉ vì giá đang chạy. Chờ cấu trúc M15 và kích hoạt M5 rõ ràng sau khi nến đóng."
            return
        }
        val slDistance = max(a * 1.5, 0.5)
        val tpDistance = slDistance * 2.0
        val entry = p
        val sl = if (direction == "BUY") entry - slDistance else entry + slDistance
        val tp = if (direction == "BUY") entry + tpDistance else entry - tpDistance
        val fmt = { v: Double -> String.format(Locale.US, "%.2f", v) }
        val rationale = if (direction == "BUY") "H4 tăng, giá ở vùng cầu H1, M15 xác nhận tăng và M5 có sweep đáy kèm nến tăng." else "H4 giảm, giá ở vùng cung H1, M15 xác nhận giảm và M5 có sweep đỉnh kèm nến giảm."
        result.text = "TÍN HIỆU: $direction\nEntry tham chiếu: ${fmt(entry)}\nStop Loss: ${fmt(sl)}\nTake Profit: ${fmt(tp)}\nR:R dự kiến: 1:2\n\nCơ sở: $rationale\nRSI M15: ${fmt(r)} | ATR M15: ${fmt(a)}\n\nLưu ý: Entry là giá tham chiếu tại thời điểm bạn nhập, không phải lệnh thị trường. Tính lại SL/khối lượng theo spread và giá trị pip thực tế của tài khoản trước khi vào lệnh."
    }
}
