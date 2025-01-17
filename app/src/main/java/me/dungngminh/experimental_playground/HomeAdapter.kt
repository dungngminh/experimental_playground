package me.dungngminh.experimental_playground

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.helper.widget.Carousel
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.dungngminh.experimental_playground.databinding.LayoutImageSwitcherBinding
import me.dungngminh.experimental_playground.databinding.LayoutTinderCardSwiperBinding


class HomeAdapter :
    ListAdapter<Layout, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Layout>() {
        override fun areItemsTheSame(oldItem: Layout, newItem: Layout): Boolean {
            return when {
                oldItem is Layout.TinderCardSwiper && newItem is Layout.TinderCardSwiper -> oldItem.items == newItem.items
                oldItem is Layout.Others && newItem is Layout.Others -> oldItem.items == newItem.items
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Layout, newItem: Layout): Boolean {
            return when {
                oldItem is Layout.TinderCardSwiper && newItem is Layout.TinderCardSwiper -> oldItem == newItem
                oldItem is Layout.Others && newItem is Layout.Others -> oldItem == newItem
                else -> false
            }
        }

    }) {

    private var recyclerView: RecyclerView? = null

    private var currentIndex = 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TOP_TEN -> TopTenViewHolder(
                LayoutTinderCardSwiperBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            OTHERS -> OthersViewHolder(
                LayoutImageSwitcherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as? MotionLayout)?.let {
            (recyclerView as? CustomRecyclerView)?.motionLayouts?.add(
                it
            )
        }
        when (holder) {
            is TopTenViewHolder -> {
                holder.bind((getItem(position) as Layout.TinderCardSwiper).items)
            }

            is OthersViewHolder -> {
                holder.bind((getItem(position) as Layout.Others).items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Layout.TinderCardSwiper -> TOP_TEN
            is Layout.Others -> OTHERS
            else -> {
                throw IllegalArgumentException("Unknown view type")
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val recyclerView = holder.itemView.parent as? CustomRecyclerView
        (holder.itemView as? MotionLayout)?.let { recyclerView?.motionLayouts?.remove(it) }
    }


    inner class TopTenViewHolder(private val binding: LayoutTinderCardSwiperBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(items: List<Item>) {
            Log.d("TestAdapter", "getItemViewType: $items")


            binding.tinderRightToLeftSlider.setAdapter(object : Carousel.Adapter {

                override fun count(): Int = items.size

                override fun populate(view: View, index: Int) {
                    Log.d("TestAdapter", "populate: $index with ${images[index]}")
                    val imageView = (view as? ViewGroup)?.getChildAt(0)
                    if (imageView is FrameLayout) {
                        (imageView.getChildAt(0) as? ImageView)?.load(images[index])
                    }
                    view.setOnClickListener {
                        if (binding.tinderRightToLeftSlider.currentIndex == index) {
                            Log.d("TestAdapter", "populate: $index clicked")
                        } else {
                            binding.tinderRightToLeftSlider.transitionToIndex(index, 300)
                        }

                    }
                }

                override fun onNewItem(index: Int) {
                    Log.d("TestAdapter", "onNewItem: $index")
                }
            })


        }
    }

    inner class OthersViewHolder(private val binding: LayoutImageSwitcherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(items: List<Item>) {
            binding.imageSwitcher.setFactory {
                val imageView = ImageView(binding.root.context)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                imageView.layoutParams = params
                imageView
            }// Returns the view to show Image
            // (Usually should use ImageView)

            binding.imageSwitcher.run {
                (this.nextView as? ImageView)?.load(images[currentIndex])
                showNext()
                currentIndex += 1
            }
            binding.btnNext.setOnClickListener {
                binding.imageSwitcher.run {
                    (this.nextView as? ImageView)?.load(images[currentIndex])
                    showNext()
                    currentIndex += 1
                }
                if (currentIndex == images.size) currentIndex = 0
            }
        }
    }

    companion object {
        const val TOP_TEN = 0
        const val OTHERS = 1

        val images = listOf(
            "https://images.unsplash.com/photo-1624976172336-54d765427b6b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=928&q=80",
            "https://images.unsplash.com/photo-1584060622420-0673aad46076?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=928&q=80",
            "https://images.unsplash.com/photo-1609703048009-d3576872b32c?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=930&q=80",
            "https://images.unsplash.com/photo-1561299593-7633f311838a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
            "https://images.pexels.com/photos/305070/pexels-photo-305070.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
            "https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
            "https://images.unsplash.com/photo-1628519592419-bf288f08cef5?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
            "https://images.unsplash.com/photo-1629450646456-b7a01cdec01a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=930&q=80",
            "https://images.unsplash.com/photo-1573074556015-71d2140a6372?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=930&q=80",
            "https://images.unsplash.com/photo-1627070160373-74a3ca062e1d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80"
        )

        var colors = intArrayOf(
            Color.parseColor("#ffd54f"),
            Color.parseColor("#ffca28"),
            Color.parseColor("#ffc107"),
            Color.parseColor("#ffb300"),
            Color.parseColor("#ffa000"),
            Color.parseColor("#ff8f00"),
            Color.parseColor("#ff6f00"),
            Color.parseColor("#c43e00")
        )
    }
}