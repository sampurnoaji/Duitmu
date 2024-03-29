package id.petersam.catatankeuangan.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import id.petersam.catatankeuangan.R
import id.petersam.catatankeuangan.databinding.FragmentChartBinding
import id.petersam.catatankeuangan.model.DatePeriod
import id.petersam.catatankeuangan.model.Transaction
import id.petersam.catatankeuangan.ui.filter.TransactionFilterModalFragment
import id.petersam.catatankeuangan.ui.main.MainActivity
import id.petersam.catatankeuangan.ui.main.MainViewModel
import id.petersam.catatankeuangan.util.DatePattern
import id.petersam.catatankeuangan.util.getAllMaterialColors
import id.petersam.catatankeuangan.util.toReadableString
import id.petersam.catatankeuangan.util.toRupiah
import id.petersam.catatankeuangan.util.toShortRupiah
import id.petersam.catatankeuangan.util.viewBinding
import java.util.Random
import kotlin.math.roundToLong

class ChartFragment : Fragment(R.layout.fragment_chart) {

    private val binding by viewBinding(FragmentChartBinding::bind)
    private val vm by activityViewModels<MainViewModel>()

    private val legendListAdapter by lazy { CategoryChartLegendListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
        setupActionView()
        initLineChart()
        initPieChart()
        observeVm()
    }

    private fun setupActionView() {
        binding.btnIncome.setOnClickListener {
            vm.onTypeChanged(Transaction.Type.INCOME)
        }
        binding.btnExpense.setOnClickListener {
            vm.onTypeChanged(Transaction.Type.EXPENSE)
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
            setHoleColor(Color.TRANSPARENT)

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
        vm.type.observe(viewLifecycleOwner) {
            binding.toggleButton.check(
                if (it == Transaction.Type.INCOME) binding.btnIncome.id
                else binding.btnExpense.id
            )
            binding.pieChart.centerText = ""
        }

        vm.datePeriod.observe(viewLifecycleOwner) {
            binding.etPeriod.setText(
                if (it == DatePeriod.CUSTOM) {
                    val startDate = vm.startDate.value ?: return@observe
                    val endDate = vm.endDate.value ?: return@observe
                    "${startDate.toReadableString(DatePattern.DMY_SHORT)} - " +
                            endDate.toReadableString(DatePattern.DMY_SHORT)
                } else it.readable
            )
        }

        vm.lineChartData.observe(viewLifecycleOwner) {
            insertTransactionsToChart(vm.prependEmpty(it))
        }

        vm.pieChartData.observe(viewLifecycleOwner) {
            insertCategoriesToChart(it)
        }
    }

    private fun insertTransactionsToChart(trxs: List<Pair<String, Long>>) {
        with(binding.lineChart) {
            clear()
            val xLabels = mutableListOf<String>()

            val entries = trxs.mapIndexed { index, pair ->
                xLabels.add(pair.first)
                Entry(index.toFloat(), pair.second.toFloat())
            }

            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index < 0 || index >= xLabels.size) "" else xLabels[index]
                }
            }

            val dataSet = LineDataSet(entries, "").apply {
                val color = ContextCompat.getColor(
                    context,
                    if (vm.type.value == Transaction.Type.INCOME) R.color.green_text
                    else R.color.red_text
                )
                setCircleColor(color)
                this.color = color
                lineWidth = 2f
                valueFormatter = ShortRupiahValueFormatter()
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(
                    context,
                    if (vm.type.value == Transaction.Type.INCOME) R.drawable.chart_gradient_income
                    else R.drawable.chart_gradient_expense
                )
            }

            data = LineData(dataSet)
            invalidate()
        }
    }

    private fun insertCategoriesToChart(
        trxs: List<Pair<String, Long>>
    ) {
        with(binding.pieChart) {
            val allColors = getAllMaterialColors(context)
            val colors = mutableListOf<Int>()
            val legends = mutableListOf<CategoryChartLegendListAdapter.Item>()

            val percentages = vm.getCategoryPercentageLabels()
            val entries = trxs.mapIndexed { index, pair ->
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
            legendListAdapter.submitList(legends.sortedByDescending { it.amount })
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