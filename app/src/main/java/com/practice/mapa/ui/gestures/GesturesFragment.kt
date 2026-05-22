package com.practice.mapa.ui.gestures

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.mapa.R
import com.practice.mapa.databinding.FragmentGesturesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class GesturesFragment : Fragment() {

    private var _binding: FragmentGesturesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GesturesViewModel by viewModels()

    private lateinit var dragAdapter: DragAdapter
    private lateinit var scrollAdapter: ScrollAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGesturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTapButtons()
        setupSwipeDetector()
        setupDragList()
        setupZoomImage()
        setupScrollList()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.gesturesTextTapCount.text =
                    getString(R.string.gestures_tap_count_format, state.tapCount)
                binding.gesturesTextDoubleTapCount.text =
                    getString(R.string.gestures_double_tap_count_format, state.doubleTapCount)
                binding.gesturesTextLongPressCount.text =
                    getString(R.string.gestures_long_press_count_format, state.longPressCount)
                binding.gesturesTextSwipeResult.text = if (state.lastSwipe.isEmpty())
                    getString(R.string.gestures_swipe_none)
                else
                    getString(R.string.gestures_swipe_result_format, state.lastSwipe)
                binding.gesturesTextZoomScale.text =
                    getString(R.string.gestures_zoom_scale_format, state.zoomScale)
            }
        }
    }

    private fun setupTapButtons() {
        binding.gesturesButtonTap.setOnClickListener { viewModel.onTap() }

        val doubleTapDetector = GestureDetectorCompat(requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    viewModel.onDoubleTap()
                    return true
                }
            })
        binding.gesturesButtonDoubleTap.setOnTouchListener { v, event ->
            doubleTapDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) v.performClick()
            true
        }

        binding.gesturesButtonLongPress.setOnLongClickListener {
            viewModel.onLongPress()
            true
        }
    }

    private fun setupSwipeDetector() {
        val swipeDetector = GestureDetectorCompat(requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                private val SWIPE_THRESHOLD = 100
                private val SWIPE_VELOCITY = 100

                override fun onFling(
                    e1: MotionEvent?, e2: MotionEvent,
                    velocityX: Float, velocityY: Float
                ): Boolean {
                    val e1 = e1 ?: return false
                    val dx = e2.x - e1.x
                    val dy = e2.y - e1.y
                    val direction = when {
                        abs(dx) > abs(dy) && abs(dx) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY ->
                            if (dx > 0) "Right" else "Left"
                        abs(dy) > abs(dx) && abs(dy) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY ->
                            if (dy > 0) "Down" else "Up"
                        else -> return false
                    }
                    viewModel.onSwipe(direction)
                    return true
                }
            })

        binding.gesturesSwipeArea.setOnTouchListener { v, event ->
            swipeDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) v.performClick()
            true
        }
    }

    private fun setupDragList() {
        val items = viewModel.uiState.value.dragItems.toMutableList()
        dragAdapter = DragAdapter(items)
        binding.gesturesRecyclerDrag.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dragAdapter
        }

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(rv: RecyclerView, from: RecyclerView.ViewHolder, to: RecyclerView.ViewHolder): Boolean {
                dragAdapter.moveItem(from.adapterPosition, to.adapterPosition)
                viewModel.moveItem(from.adapterPosition, to.adapterPosition)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        touchHelper.attachToRecyclerView(binding.gesturesRecyclerDrag)
    }

    private fun setupZoomImage() {
        binding.gesturesTextZoomScale.text = getString(R.string.gestures_zoom_scale_format, 1.0f)

        val scaleListener = android.view.ScaleGestureDetector(requireContext(),
            object : android.view.ScaleGestureDetector.SimpleOnScaleGestureListener() {
                private var currentScale = 1f
                override fun onScale(detector: android.view.ScaleGestureDetector): Boolean {
                    currentScale = (currentScale * detector.scaleFactor).coerceIn(1f, 5f)
                    binding.gesturesImageZoom.scaleX = currentScale
                    binding.gesturesImageZoom.scaleY = currentScale
                    viewModel.onZoom(currentScale)
                    return true
                }
            })

        binding.gesturesImageZoom.setOnTouchListener { v, event ->
            scaleListener.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) v.performClick()
            true
        }
    }

    private fun setupScrollList() {
        scrollAdapter = ScrollAdapter()
        binding.gesturesRecyclerScroll.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scrollAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
