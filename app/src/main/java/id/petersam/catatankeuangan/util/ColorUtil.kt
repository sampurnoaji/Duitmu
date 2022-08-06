package id.petersam.catatankeuangan.util

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Color
import id.petersam.catatankeuangan.R
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


@Throws(IOException::class, XmlPullParserException::class)
fun getAllMaterialColors(context: Context): List<Int> {
    val xrp: XmlResourceParser = context.resources.getXml(R.xml.material_design_colors)
    val allColors: MutableList<Int> = ArrayList()
    var nextEvent: Int
    while (xrp.next().also { nextEvent = it } != XmlResourceParser.END_DOCUMENT) {
        val s = xrp.name
        if ("color" == s) {
            val color = xrp.nextText()
            allColors.add(Color.parseColor(color))
        }
    }
    return allColors
}