package returns.mingleday.app.util

import android.content.Context
import androidx.core.graphics.toColorInt
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

object ColorUtil {
    fun getColorInt(code: String): Int {
        return "#$code".toColorInt()
    }

    fun showColorPicker(
        context: Context,
        onColorSelected: (String) -> Unit
    ) {
        ColorPickerDialog.Builder(context)
            .setTitle("색상 선택")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton(
                "확인",
                ColorEnvelopeListener { envelope, _ ->
                    onColorSelected(envelope.hexCode)
                }
            )
            .setNegativeButton("취소") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .attachAlphaSlideBar(false)
            .attachBrightnessSlideBar(true)
            .show()
    }

    fun isHexColor(value: String): Boolean {
        return Regex("^[0-9A-Fa-f]{6}$").matches(value)
    }
}