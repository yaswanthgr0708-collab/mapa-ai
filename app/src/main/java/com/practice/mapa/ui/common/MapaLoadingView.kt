package com.practice.mapa.ui.common

// Animation: res/raw/loading_animation.json — hand-crafted Lottie JSON, original work.
// No third-party animation license required.

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.practice.mapa.R
import kotlin.math.roundToInt

class MapaLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    enum class Mode { OVERLAY, INLINE }

    private val dimView: View
    private val contentView: LinearLayout
    private val lottieView: LottieAnimationView
    private val captionView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_mapa_loading, this, true)
        dimView     = findViewById(R.id.common_loading_dim)
        contentView = findViewById(R.id.common_loading_content)
        lottieView  = findViewById(R.id.common_loading_lottie)
        captionView = findViewById(R.id.common_loading_caption)
        visibility  = GONE
        contentDescription = "loading_overlay_hidden"
        // Default to INLINE so callers only need setMode(OVERLAY) for the login overlay.
        setMode(Mode.INLINE)
    }

    fun setMode(mode: Mode) {
        when (mode) {
            Mode.OVERLAY -> {
                dimView.visibility = VISIBLE
                contentView.orientation = LinearLayout.VERTICAL
                val size = 120.dpPx
                lottieView.layoutParams = lottieView.layoutParams.apply {
                    width  = size
                    height = size
                }
            }
            Mode.INLINE -> {
                dimView.visibility = GONE
                contentView.orientation = LinearLayout.HORIZONTAL
                val size = 40.dpPx
                lottieView.layoutParams = lottieView.layoutParams.apply {
                    width  = size
                    height = size
                }
            }
        }
    }

    fun show(caption: String? = null) {
        if (caption != null) {
            captionView.text = caption
            captionView.visibility = VISIBLE
        } else {
            captionView.visibility = GONE
        }
        visibility = VISIBLE
        contentDescription = "loading_overlay_visible"
        lottieView.playAnimation()
    }

    fun hide() {
        lottieView.cancelAnimation()
        visibility = GONE
        contentDescription = "loading_overlay_hidden"
    }

    private val Int.dpPx: Int
        get() = (this * resources.displayMetrics.density).roundToInt()
}
