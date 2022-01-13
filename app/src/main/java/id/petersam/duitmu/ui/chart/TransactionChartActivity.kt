package id.petersam.duitmu.ui.chart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.ActivityTransactionChartBinding
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.LoadState
import id.petersam.duitmu.util.toShortRupiah
import id.petersam.duitmu.util.viewBinding

@AndroidEntryPoint
class TransactionChartActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityTransactionChartBinding::inflate)
    private val vm by viewModels<TransactionChartViewModel>()

    companion object {
        const val INCOME_BUTTON_INDEX = 0
        const val EXPENSE_BUTTON_INDEX = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        initLineChart()
        observeVm()
        setupActionView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initLineChart() {
        with(binding.lineChart) {
            setExtraOffsets(0f, 0f, 0f, 8f)
            //hide grid lines
            axisLeft.apply {
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return value.toLong().toShortRupiah()
                    }
                }
            }

            //remove right y-axis
            axisRight.isEnabled = false

            //remove legend
            legend.isEnabled = false

            //remove description label
            description.isEnabled = false

            //add animation
            animateX(1000, Easing.EaseInSine)

            // to draw label on xAxis
            val xAxis: XAxis = xAxis
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawLabels(true)
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = +90f
        }
    }

    private fun observeVm() {
        vm.type.observe(this) {
            if (it == Transaction.Type.EXPENSE) insertTransactionsToChart(expenseTrx = vm.expenseData)
            else insertTransactionsToChart(incomeTrx = vm.incomeData)
        }
    }

    private fun setupActionView() {
        binding.toggleButton.apply {
            addOnButtonCheckedListener { _, _, _ ->
                if (binding.btnIncome.isChecked) vm.onTypeChanged(INCOME_BUTTON_INDEX)
                if (binding.btnExpense.isChecked) vm.onTypeChanged(EXPENSE_BUTTON_INDEX)
            }
            //initial checked
            check(binding.btnExpense.id)
        }
    }

    private fun insertTransactionsToChart(
        expenseTrx: List<Pair<String, Long>>? = null,
        incomeTrx: List<Pair<String, Long>>? = null
    ) {
        with(binding.lineChart) {
            //now draw bar chart with dynamic data
            val xLabels = ArrayList<String>()
            val expenses: ArrayList<Entry> = ArrayList()
            val incomes: ArrayList<Entry> = ArrayList()

            expenseTrx?.let {
                for (i in it.indices) {
                    xLabels.add(it[i].first)
                    expenses.add(Entry(i.toFloat(), it[i].second.toFloat()))
                }
            }
            incomeTrx?.let {
                for (i in it.indices) {
                    xLabels.add(it[i].first)
                    incomes.add(Entry(i.toFloat(), it[i].second.toFloat()))
                }
            }

            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return xLabels[value.toInt()]
                }
            }

            val lineDataSet1 = LineDataSet(expenses, "").apply {
                val color = ContextCompat.getColor(this@TransactionChartActivity, R.color.red_text)
                setCircleColor(color)
                this.color = color
                lineWidth = 2f
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(
                    this@TransactionChartActivity,
                    R.drawable.chart_gradient_expense
                )
            }
            val lineDataSet2 = LineDataSet(incomes, "").apply {
                val color =
                    ContextCompat.getColor(this@TransactionChartActivity, R.color.green_text)
                setCircleColor(color)
                this.color = color
                lineWidth = 2f
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(
                    this@TransactionChartActivity,
                    R.drawable.chart_gradient_income
                )
            }

            val data = LineData(lineDataSet1, lineDataSet2)
            this.data = data
            invalidate()
        }
    }
}