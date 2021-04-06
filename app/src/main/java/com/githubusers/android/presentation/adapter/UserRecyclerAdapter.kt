package com.githubusers.android.presentation.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.githubusers.android.databinding.UserListItemBinding
import com.githubusers.android.domain.model.User
import com.githubusers.android.presentation.util.DisplayImageOpts
import com.githubusers.android.presentation.util.UiUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList

class UserRecyclerAdapter :
    BaseRecyclerViewAdapter<User, UserListItemBinding>(),
    ViewHolderInitializer<User, UserListItemBinding>
{
    internal var itemsFull: ArrayList<User> = arrayListOf()
    var toUserDetailsScreenCb: ((User, Int) -> Unit)? = null

    init {
        viewBindingInitializer = this
    }

    override fun generateViewHolder(parent: ViewGroup) :
            BaseViewHolder<User, UserListItemBinding> {

        val itemBinding: UserListItemBinding
                = UserListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )

        return UserViewHolder(
                itemBinding,
                toUserDetailsScreenCb
        )
    }

    override fun submitList(listItems: List<User>) {
        val itemsToAdd = arrayListOf<User>()
        val newListItems = listItems as ArrayList<User>
        newListItems.forEach { newUser ->
            val existingPost = items.find {
                it.id == newUser.id
            }
            if (existingPost == null) {
                itemsToAdd.add(newUser)
            }
        }
        items.addAll(newListItems)
        itemsFull = ArrayList(items)
        notifyDataSetChanged()
    }

    val itemFilter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            val filteredList: MutableList<User> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(itemsFull)
            } else {
                val filterPattern = constraint.toString()
                        .toLowerCase(Locale.getDefault())
                        .trim { it <= ' ' }

                for (item in itemsFull) {
                    item.note?.toLowerCase()?.contains(filterPattern)

                    if (item.login.toLowerCase().contains(filterPattern) ||
                        (item.note != null && item.note!!.toLowerCase().contains(filterPattern))) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            items.clear()
            items.addAll(results.values as Collection<User>)
            notifyDataSetChanged()
        }
    }

    class TopSpacingDecoration(private val padding: Int): RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            if (parent.getChildAdapterPosition(view) > 0) {
                outRect.top = padding
            }
        }
    }
}

@ExperimentalCoroutinesApi
class UserViewHolder(
        viewBinding: UserListItemBinding,
        private val toUserDetailsScreenCb: ((User, Int) -> Unit)?
)
    : BaseViewHolder<User, UserListItemBinding>(viewBinding)
{
    private val itemContainer: CardView = viewBinding.userListItemContainer
    private val ivAvatar: ImageView = viewBinding.ivAvatar
    private val tvName: TextView = viewBinding.tvName
    private val tvType: TextView = viewBinding.tvType
    private val ivNoteIndicator: ImageView = viewBinding.ivNoteIndicator

    override fun setViews(item: User) {
        super.setViews(item)
        UiUtil.displayImage(itemView.context, item.avatarUrl, ivAvatar,
            DisplayImageOpts(
                isDisplayLoading = true,
                isDisplayErrorImage = false,
                isCircleCrop = true
            )
        )

        tvName.text = item.getUsername()
        tvType.text = item.type

        if (item.note != null) {
            ivNoteIndicator.visibility = View.VISIBLE
        }
        else {
            ivNoteIndicator.visibility = View.GONE
        }

        itemContainer.setOnClickListener {
            toUserDetailsScreenCb?.invoke(item, adapterPosition)
        }
    }
}
