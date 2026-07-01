package com.example.mad_final.ui.components.legacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_final.R
import com.example.mad_final.domain.models.WorkshopService
import java.util.Locale

class ServiceAdapter(
    private val onToggle: (String) -> Unit,
    private val selectedIds: Set<String>
) : ListAdapter<WorkshopService, ServiceAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view, onToggle)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position), selectedIds.contains(getItem(position).id))
    }

    class ServiceViewHolder(
        itemView: View,
        private val onToggle: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.serviceTitle)
        private val duration: TextView = itemView.findViewById(R.id.serviceDuration)
        private val price: TextView = itemView.findViewById(R.id.servicePrice)
        private val checkbox: CheckBox = itemView.findViewById(R.id.serviceCheckbox)

        fun bind(service: WorkshopService, isSelected: Boolean) {
            title.text = service.title.uppercase()
            duration.text = "EST. ${service.duration.uppercase()}"
            price.text = String.format(Locale.US, "RM %.2f", service.price)
            
            checkbox.setOnCheckedChangeListener(null)
            checkbox.isChecked = isSelected
            
            itemView.setOnClickListener { onToggle(service.id) }
            checkbox.setOnClickListener { onToggle(service.id) }

            // Apply "Mad Ape" styling
            if (isSelected) {
                itemView.setBackgroundColor(android.graphics.Color.BLACK)
                title.setTextColor(android.graphics.Color.WHITE)
                duration.setTextColor(android.graphics.Color.LTGRAY)
                price.setTextColor(android.graphics.Color.WHITE)
            } else {
                itemView.setBackgroundColor(android.graphics.Color.WHITE)
                title.setTextColor(android.graphics.Color.BLACK)
                duration.setTextColor(android.graphics.Color.GRAY)
                price.setTextColor(android.graphics.Color.BLACK)
            }
        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<WorkshopService>() {
        override fun areItemsTheSame(oldItem: WorkshopService, newItem: WorkshopService): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WorkshopService, newItem: WorkshopService): Boolean {
            return oldItem == newItem
        }
    }
}
