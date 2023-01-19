package com.shakhrashidov.virtual_news.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.shakhrashidov.virtual_news.R
import com.shakhrashidov.virtual_news.adapters.SavedRecAdapter
import com.shakhrashidov.virtual_news.model.Article
import com.shakhrashidov.virtual_news.util.*
import com.shakhrashidov.virtual_news.util.swipeDetector.ItemTouchHelperCallback
import com.shakhrashidov.virtual_news.util.swipeDetector.RecyclerViewSwipe
import kotlinx.android.synthetic.main.saved_fragment.*


class SavedFragment : BaseFragment(R.layout.saved_fragment) {
    lateinit var savedAdapter: SavedRecAdapter
    lateinit var itemTouchHelper: ItemTouchHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        observeTopBarState()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            when (viewModel.savedTopBarState.value) {
                is TopBarState.SelectionState -> clearSelection()
                else -> findNavController().popBackStack()
            }
        }

        viewModel.savedNewsLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                iv_no_saved_news.show()
            } else {
                iv_no_saved_news.hide()
            }
            savedAdapter.savedDiffer.submitList(it)
            savedAdapter.notifyDataSetChanged()
        })

        btn_delete_all.setOnClickListener {
            confirmDeleteAlert()
        }
    }

    private fun observeTopBarState() {
        viewModel.savedTopBarState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is TopBarState.SelectionState -> {
                    itemTouchHelper.attachToRecyclerView(null)
                    saved_top_bar.hide()
                    saved_selection_top_bar.show()
                    ib_clear_selection.setOnClickListener {
                        clearSelection()
                    }
                    btn_delete_selected.setOnClickListener {
                        val itemCount = savedAdapter.selectedItems.size
                        viewModel.deleteSelected(savedAdapter.selectedItems)
                        context?.toast("$itemCount article deleted!")
                        clearSelection()
                    }
                    tv_item_count.text = savedAdapter.selectedItems.size.toString()
                }

                else -> {
                    itemTouchHelper.attachToRecyclerView(saved_rec_view)
                    saved_top_bar.show()
                    saved_selection_top_bar.hide()
                }
            }
        })
    }

    private fun clearSelection() {
        viewModel.savedTopBarState.postValue(TopBarState.NormalState())
        savedAdapter.selectedItems.clear()
        findNavController().navigate(R.id.savedFragment)
    }

    private fun confirmDeleteAlert() =
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialDialog)
            .setIcon(R.drawable.ic_trash_2)
            .setTitle(R.string.on_delete)
            .setMessage(R.string.on_confirm_delete)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteAllArticle()
            }
            .setNegativeButton(R.string.no) { _, _ ->
            }
            .show()


    private fun setUpRecyclerView() {
        savedAdapter = SavedRecAdapter(viewModel)
        saved_rec_view.setUpWithAdapter(requireContext(), savedAdapter)
        val simpleCallBack = ItemTouchHelperCallback(object : RecyclerViewSwipe {
            override fun onSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
                val position = viewHolder.adapterPosition
                val currentArticle = savedAdapter.savedDiffer.currentList[position]
                deleteArticle(currentArticle)
            }

            override fun onSwipeRight(viewHolder: RecyclerView.ViewHolder) {
                val position = viewHolder.adapterPosition
                val currentArticle = savedAdapter.savedDiffer.currentList[position]
                requireContext().share(currentArticle)
                savedAdapter.notifyDataSetChanged()
            }

            override fun addSwipeLeftBackgroundColor(): Int = requireContext().getColor(R.color.red_400)

            override fun addSwipeRightBackgroundColor(): Int = requireContext().getColor(R.color.transparent)

            override fun addSwipeLeftActionIcon(): Int = R.drawable.ic_trash_2_white
        })

        itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(saved_rec_view)
    }

    private fun deleteArticle(article: Article) {
        viewModel.deleteArticle(article)
        view?.let {
            Snackbar.make(it, R.string.deleted, Snackbar.LENGTH_LONG).apply {
                setAction(R.string.undo) {
                    viewModel.saveArticle(article)
                    savedAdapter.notifyDataSetChanged()
                }
                show()
            }
        }
        savedAdapter.notifyDataSetChanged()
    }

}