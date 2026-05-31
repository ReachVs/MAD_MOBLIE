package com.example.mad_final.ui.screens.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mad_final.R
import com.example.mad_final.domain.models.WorkshopService

class LegacyCatalogAdapter(
    private var services: List<WorkshopService>,
    private val onServiceClick: (String) -> Unit
) : RecyclerView.Adapter<LegacyCatalogAdapter.ServiceViewHolder>() {

    fun updateData(newServices: List<WorkshopService>) {
        services = newServices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_legacy, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service, onServiceClick)
    }

    override fun getItemCount() = services.size

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.serviceTitle)
        private val price: TextView = itemView.findViewById(R.id.servicePrice)
        private val image: ImageView = itemView.findViewById(R.id.serviceImage)
        private val category: TextView = itemView.findViewById(R.id.serviceCategory)
        private val description: TextView = itemView.findViewById(R.id.serviceDescription)
        private val tag1: TextView = itemView.findViewById(R.id.serviceTag1)
        private val btnSpecs: View = itemView.findViewById(R.id.btnSpecs)

        fun bind(service: WorkshopService, onClick: (String) -> Unit) {
            title.text = service.title.uppercase()
            price.text = service.price
            
            val context = itemView.context
            if (service.imageUrl.startsWith("http")) {
                image.load(service.imageUrl)
            } else {
                val resId = context.resources.getIdentifier(service.imageUrl, "drawable", context.packageName)
                if (resId != 0) {
                    image.load(resId)
                } else {
                    image.load(service.imageUrl)
                }
            }

            category.text = service.category.uppercase()
            description.text = service.description
            
            if (service.tags.isNotEmpty()) {
                tag1.text = service.tags[0].uppercase()
                tag1.visibility = View.VISIBLE
                // Add border to mimic Compose technical card style
                tag1.setBackgroundResource(R.drawable.bg_technical_tag)
            } else {
                tag1.visibility = View.GONE
            }

            val clickAction = View.OnClickListener { onClick(service.id) }
            itemView.setOnClickListener(clickAction)
            btnSpecs.setOnClickListener(clickAction)
        }
    }
}
