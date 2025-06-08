@file:Suppress("unused")

package me.obsilabor.tpshud.hud

import com.mojang.blaze3d.opengl.GlStateManager
import me.obsilabor.tpshud.TpsTracker
import me.obsilabor.tpshud.config.ConfigManager
import me.obsilabor.tpshud.minecraft
import net.minecraft.client.gui.DrawContext
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

object TpsWidget {
    fun render(context: DrawContext) {
        val config = ConfigManager.config ?: return
        if(!config.isEnabled) return
        context.matrices.push()
        context.matrices.scale(ConfigManager.config?.scale?:1f, ConfigManager.config?.scale?:1f, 0f)
        if(config.backgroundEnabled) {
            GlStateManager._disableDepthTest()
            fillBackground(context, config.x.toFloat(), config.y.toFloat(), config.x+width.toFloat(), config.y+minecraft.textRenderer.fontHeight+1f, config.backgroundColor, config.backgroundOpacity)
            GlStateManager._enableDepthTest()
        }
        val text = ConfigManager.config?.text ?: "TPS: "
        val widthPartOne = minecraft.textRenderer.getWidth(text)
        context.drawText(minecraft.textRenderer, text, config.x, config.y, config.textColor, config.textShadow)
        context.drawText(minecraft.textRenderer, round(TpsTracker.INSTANCE.tickRate), config.x+widthPartOne, config.y, config.valueTextColor, config.textShadow)
        context.matrices.pop()
    }

    fun renderLivePreview(context: DrawContext, x: Int, y: Int) {
        val config = ConfigManager.config ?: return
        if(!config.isEnabled) return
        context.matrices.push()
        context.matrices.scale(ConfigManager.config?.scale?:1f, ConfigManager.config?.scale?:1f, 0f)
        if(config.backgroundEnabled) {
            GlStateManager._disableDepthTest()
            fillBackground(context, x.toFloat(), y.toFloat(), x+width.toFloat()+7f, y+ minecraft.textRenderer.fontHeight+1f, config.backgroundColor, config.backgroundOpacity)
            GlStateManager._enableDepthTest()
        }
        val text = ConfigManager.config?.text ?: "TPS: "
        val widthPartOne = minecraft.textRenderer.getWidth(text)
        context.drawText(minecraft.textRenderer, text, x, y, config.textColor, config.textShadow)
        context.drawText(minecraft.textRenderer, round(19.89f), x+widthPartOne, y, config.valueTextColor, config.textShadow)
        context.matrices.pop()
    }

    private fun round(tps: Float): String {
        var copy = tps.toDouble()
        if (copy !in 0.0..1000000.0) return "NaN"
        copy = if(ConfigManager.config?.satisfyTpsCount == true) {
            kotlin.math.round(copy)
        } else {
            BigDecimal(copy).setScale(2, RoundingMode.HALF_UP).toDouble()
        }
        return copy.toString()
    }

    val width: Int
        get() = minecraft.textRenderer.getWidth(ConfigManager.config?.text ?: "TPS: ")+minecraft.textRenderer.getWidth(round(TpsTracker.INSTANCE.tickRate))

    private fun fillBackground(drawContext: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, alpha: Float) {
        val rgb = Color(color)
        drawContext.fill(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), Color(rgb.red, rgb.blue, rgb.green, (alpha*255).roundToInt()).rgb)
    }
}
