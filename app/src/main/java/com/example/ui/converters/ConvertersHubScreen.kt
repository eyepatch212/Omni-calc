package com.example.ui.converters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.converters.ConverterCategory

data class ConverterItem(
    val category: ConverterCategory,
    val icon: ImageVector,
    val group: String
)

val allConverterItems = listOf(
    ConverterItem(ConverterCategory.CURRENCY, Icons.Default.AttachMoney, "Finance"),
    ConverterItem(ConverterCategory.INCOME_TAX, Icons.Default.AccountBalance, "Finance"),
    ConverterItem(ConverterCategory.MORTGAGE, Icons.Default.Home, "Finance"),
    ConverterItem(ConverterCategory.LENGTH, Icons.Default.Straighten, "Measurements"),
    ConverterItem(ConverterCategory.MASS, Icons.Default.FitnessCenter, "Measurements"),
    ConverterItem(ConverterCategory.AREA, Icons.Default.AspectRatio, "Measurements"),
    ConverterItem(ConverterCategory.VOLUME, Icons.Default.LocalDrink, "Measurements"),
    ConverterItem(ConverterCategory.TIME, Icons.Default.AccessTime, "Everyday"),
    ConverterItem(ConverterCategory.SPEED, Icons.Default.Speed, "Everyday"),
    ConverterItem(ConverterCategory.TEMPERATURE, Icons.Default.Thermostat, "Everyday"),
    ConverterItem(ConverterCategory.MOLARITY, Icons.Default.Science, "Science"),
    ConverterItem(ConverterCategory.NUMERAL_SYSTEM, Icons.Default.Numbers, "Science"),
    ConverterItem(ConverterCategory.BMI, Icons.Default.MonitorWeight, "Health")
)

@Composable
fun ConvertersHubScreen(
    onSelectConverter: (ConverterCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("All") }

    val filterGroups = listOf("All", "Finance", "Measurements", "Science", "Everyday", "Health")

    val filteredList = remember(searchQuery, selectedGroup) {
        allConverterItems.filter { item ->
            val matchesSearch = item.category.title.contains(searchQuery, ignoreCase = true) ||
                    item.category.description.contains(searchQuery, ignoreCase = true)
            val matchesGroup = selectedGroup == "All" || item.group == selectedGroup
            matchesSearch && matchesGroup
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Title
        Text(
            text = "Converters & Calculators",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search 13 converters (tax, molarity, bmi...)") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("converter_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterGroups) { group ->
                FilterChip(
                    selected = selectedGroup == group,
                    onClick = { selectedGroup = group },
                    label = { Text(group) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid of Converters
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList, key = { it.category.id }) { item ->
                ConverterCard(
                    item = item,
                    onClick = { onSelectConverter(item.category) }
                )
            }
        }
    }
}

@Composable
fun ConverterCard(
    item: ConverterItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("converter_card_${item.category.id}")
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.category.title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.category.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.category.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
