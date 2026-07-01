package com.example.mad_final.ui.components.legacy

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_final.domain.models.WorkshopService

@Composable
fun LegacyServiceList(
    services: List<WorkshopService>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val adapter = remember(selectedIds) {
        ServiceAdapter(onToggle, selectedIds)
    }

    AndroidView(
        factory = { context ->
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
            }
        },
        update = { recyclerView ->
            (recyclerView.adapter as? ServiceAdapter)?.submitList(services)
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 2000.dp)
    )
}
