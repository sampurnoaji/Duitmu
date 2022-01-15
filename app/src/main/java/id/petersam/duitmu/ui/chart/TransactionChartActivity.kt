package id.petersam.duitmu.ui.chart

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.ActivityTransactionChartBinding
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.getAllMaterialColors
import id.petersam.duitmu.util.toShortRupiah
import id.petersam.duitmu.util.viewBinding
import java.util.Random
import kotlin.math.roundToLong

@AndroidEntryPoint
open class TransactionChartActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityTransactionChartBinding::inflate)
    private val vm by viewModels<TransactionChartViewModel>()

    companion object {
        const val INCOME_BUTTON_INDEX = 0
        const val EXPENSE_BUTTON_INDEX = 1

        private const val TAB_INDEX = "index"

        @JvmStatic
        fun start(context: Context, index: Int) {
            val starter = Intent(context, TransactionChartActivity::class.java)
                .putExtra(TAB_INDEX, index)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        initLineChart()
        initPieChart()
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
            setNoDataText(getString(R.string.empty_note))
            setExtraOffsets(0f, 0f, 0f, 8f)
            //hide grid lines
            axisLeft.apply {
                setDrawGridLines(true)
                valueFormatter = ShortRupiahValueFormatter()
            }

            //remove right y-axis
            axisRight.isEnabled = false

            //remove legend
            legend.isEnabled = false

            //remove description label
            description.isEnabled = false

            //add animation
            animateX(500, Easing.EaseInSine)

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

    private fun initPieChart() {
        with(binding.pieChart) {
            setUsePercentValues(true)
            description.isEnabled = false

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)

            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)

            holeRadius = 58f
            transparentCircleRadius = 61f

            setDrawCenterText(true)
            centerText = getString(R.string.label_category)
            setCenterTextSize(14f)

            rotationAngle = 180f
            setCenterTextOffset(0f, -20f)

            animateY(1400, Easing.EaseInOutQuad)

            legend.isEnabled = false

            // entry label styling
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
        }
    }

    private fun observeVm() {
        vm.type.observe(this) {
            if (it == Transaction.Type.EXPENSE) insertTransactionsToChart(expenseTrx = vm.expensesAmount)
            else insertTransactionsToChart(incomeTrx = vm.incomesAmount)
            insertCategoriesToChart(vm.incomeCategories)
        }
    }

    private fun setupActionView() {
        binding.toggleButton.apply {
            addOnButtonCheckedListener { _, _, _ ->
                if (binding.btnIncome.isChecked) vm.onTypeChanged(INCOME_BUTTON_INDEX)
                if (binding.btnExpense.isChecked) vm.onTypeChanged(EXPENSE_BUTTON_INDEX)
            }
            val tabIndex = intent.extras?.getInt(TAB_INDEX)
            check(
                if (tabIndex == INCOME_BUTTON_INDEX) binding.btnIncome.id
                else binding.btnExpense.id
            )
        }
    }

    private fun insertTransactionsToChart(
        expenseTrx: List<Pair<String, Long>>? = null,
        incomeTrx: List<Pair<String, Long>>? = null
    ) {
        with(binding.lineChart) {
            //now draw bar chart with dynamic data
            val xLabels = mutableListOf<String>()
            val expenses = mutableListOf<Entry>()
            val incomes = mutableListOf<Entry>()

            expenseTrx?.mapIndexed { index, pair ->
                xLabels.add(pair.first)
                expenses.add(Entry(index.toFloat(), pair.second.toFloat()))
            }

            incomeTrx?.mapIndexed { index, pair ->
                xLabels.add(pair.first)
                incomes.add(Entry(index.toFloat(), pair.second.toFloat()))
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
                valueFormatter = ShortRupiahValueFormatter()
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

    private fun insertCategoriesToChart(incomeTrx: List<Pair<String, Long>>? = null) {
        with(binding.pieChart) {
            val entries = incomeTrx?.map {
                PieEntry(it.second.toFloat(), it.first)
            }

            val allColors = getAllMaterialColors(this@TransactionChartActivity)
            val entryColors = incomeTrx?.map {
                val randomIndex = Random().nextInt(allColors.size)
                allColors[randomIndex]
            }

            val dataSet = PieDataSet(entries, "").apply {
                sliceSpace = 3f
                selectionShift = 5f
                colors = entryColors
            }

            val data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter())
                setValueTextSize(11f)
                setValueTextColor(Color.WHITE)
            }
            setData(data)
            invalidate()
        }
    }

    inner class ShortRupiahValueFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return value.roundToLong().toShortRupiah()
        }

        override fun getFormattedValue(value: Float): String {
            return value.roundToLong().toShortRupiah()
        }
    }
}