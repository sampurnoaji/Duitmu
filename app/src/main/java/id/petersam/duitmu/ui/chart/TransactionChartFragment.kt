package id.petersam.duitmu.ui.chart

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.FragmentTransactionChartBinding
import id.petersam.duitmu.model.DatePeriod
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.ui.filter.TransactionFilterModalFragment
import id.petersam.duitmu.ui.home.MainViewModel
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.getAllMaterialColors
import id.petersam.duitmu.util.toReadableString
import id.petersam.duitmu.util.toRupiah
import id.petersam.duitmu.util.toShortRupiah
import id.petersam.duitmu.util.viewBinding
import java.util.Random
import kotlin.math.roundToLong


class TransactionChartFragment : DialogFragment(R.layout.fragment_transaction_chart) {

    private val binding by viewBinding(FragmentTransactionChartBinding::bind)
    private val vm by activityViewModels<MainViewModel>()

    private val legendListAdapter by lazy { CategoryChartLegendListAdapter() }

    companion object {
        const val TAG = "TransactionChartFragment"
        fun newInstance(manager: FragmentManager): TransactionChartFragment? {
            manager.findFragmentByTag(TAG) ?: return TransactionChartFragment()
            return null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }


    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            dialog.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLineChart()
        initPieChart()
        observeVm()
        setupActionView()
    }

    private fun setupActionView() {
        binding.toggleButton.apply {
            addOnButtonCheckedListener { _, _, _ ->
                if (binding.btnIncome.isChecked) vm.onTypeChanged(Transaction.Type.INCOME)
                if (binding.btnExpense.isChecked) vm.onTypeChanged(Transaction.Type.EXPENSE)
            }
            check(
                if (vm.type.value == Transaction.Type.INCOME) binding.btnIncome.id
                else binding.btnExpense.id
            )
        }

        binding.etPeriod.setOnClickListener {
            val modal = TransactionFilterModalFragment.newInstance(childFragmentManager)
            modal?.show(childFragmentManager, TransactionFilterModalFragment.TAG)
        }
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
            description.isEnabled = false

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)

            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)

            holeRadius = 58f
            transparentCircleRadius = 61f

            setDrawCenterText(true)

            rotationAngle = 180f

            animateY(1400, Easing.EaseInOutQuad)

            legend.isEnabled = false

            // entry label styling
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        val data = it as PieEntry
                        centerText = "${data.label}\n${data.value.toLong().toRupiah()}"
                    }
                }

                override fun onNothingSelected() {
                    centerText = ""
                }
            })
        }
        with(binding.rvPieChartLegend) {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = legendListAdapter
        }
    }

    private fun observeVm() {
        vm.type.observe(this) {
            if (it == Transaction.Type.EXPENSE) {
                insertTransactionsToChart(expenseTrx = vm.expensesAmount)
                insertCategoriesToChart(expenseTrx = vm.expensesCategories)
            } else {
                insertTransactionsToChart(incomeTrx = vm.incomesAmount)
                insertCategoriesToChart(incomeTrx = vm.incomeCategories)
            }
            binding.pieChart.centerText = ""
        }

        vm.datePeriod.observe(this) {
            binding.etPeriod.setText(
                if (it == DatePeriod.CUSTOM) {
                    val startDate = vm.startDate.value ?: return@observe
                    val endDate = vm.endDate.value ?: return@observe
                    "${startDate.toReadableString(DatePattern.DMY_SHORT)} - " +
                            endDate.toReadableString(DatePattern.DMY_SHORT)
                } else it.readable
            )
        }
    }

    private fun insertTransactionsToChart(
        expenseTrx: List<Pair<String, Long>>? = null,
        incomeTrx: List<Pair<String, Long>>? = null
    ) {
        with(binding.lineChart) {
            clear()
            //now draw bar chart with dynamic data
            val xLabels = mutableListOf<String>()

            val expenses = expenseTrx?.mapIndexed { index, pair ->
                xLabels.add(pair.first)
                Entry(index.toFloat(), pair.second.toFloat())
            }

            val incomes = incomeTrx?.mapIndexed { index, pair ->
                xLabels.add(pair.first)
                Entry(index.toFloat(), pair.second.toFloat())
            }

            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index < 0 || index >= xLabels.size) "" else xLabels[index]
                }
            }

            val expensesDataSet = LineDataSet(expenses, "").apply {
                val color = ContextCompat.getColor(context, R.color.red_text)
                setCircleColor(color)
                this.color = color
                lineWidth = 2f
                valueFormatter = ShortRupiahValueFormatter()
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(context, R.drawable.chart_gradient_expense)
            }
            val incomesDataSet = LineDataSet(incomes, "").apply {
                val color =
                    ContextCompat.getColor(context, R.color.green_text)
                setCircleColor(color)
                this.color = color
                lineWidth = 2f
                valueFormatter = ShortRupiahValueFormatter()
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(context, R.drawable.chart_gradient_income)
            }

            data = LineData(expensesDataSet, incomesDataSet)
            invalidate()
        }
    }

    private fun insertCategoriesToChart(
        expenseTrx: List<Pair<String, Long>>? = null,
        incomeTrx: List<Pair<String, Long>>? = null
    ) {
        with(binding.pieChart) {
            val allColors = getAllMaterialColors(context)
            val colors = mutableListOf<Int>()
            val legends = mutableListOf<CategoryChartLegendListAdapter.Item>()

            val entries = if (incomeTrx != null) {
                val percentages = vm.getIncomesCategoryPercentageLabels()

                incomeTrx.mapIndexed { index, pair ->
                    val randomIndex = Random().nextInt(allColors.size)
                    val color = allColors[randomIndex]
                    colors.add(color)

                    legends.add(
                        CategoryChartLegendListAdapter.Item(
                            category = pair.first,
                            amount = pair.second,
                            color = color,
                            percent = percentages?.get(index)
                        )
                    )
                    PieEntry(pair.second.toFloat(), pair.first)
                }
            } else {
                val percentages = vm.getExpensesCategoryPercentageLabels()

                expenseTrx?.mapIndexed { index, pair ->
                    val randomIndex = Random().nextInt(allColors.size)
                    val color = allColors[randomIndex]
                    colors.add(color)

                    legends.add(
                        CategoryChartLegendListAdapter.Item(
                            category = pair.first,
                            amount = pair.second,
                            color = color,
                            percent = percentages?.get(index)
                        )
                    )
                    PieEntry(pair.second.toFloat(), pair.first)
                }
            }

            val dataSet = PieDataSet(entries, "").apply {
                sliceSpace = 3f
                selectionShift = 5f
                this.colors = colors
                setDrawValues(false)
            }

            val data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter())
                setValueTextSize(11f)
                setValueTextColor(Color.WHITE)
            }
            setData(data)
            invalidate()
            legendListAdapter.submitList(legends)
            binding.tvAmountTotal.text = legends.sumOf { it.amount }.toRupiah()
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