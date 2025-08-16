package com.example.animols

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animols.ui.theme.AnimolsTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi


@Composable
fun AnimalSearchScreen(
    modifier: Modifier = Modifier,
    animalViewModel: AnimalViewModel = viewModel()
) {
    val query by animalViewModel.query.collectAsState()
    val animals by animalViewModel.animals.collectAsState()
    val error by animalViewModel.error.collectAsState()
    val isLoading by animalViewModel.isLoading.collectAsState()
    val selectedAnimal by animalViewModel.selectedAnimal.collectAsState()
    val showSuggestions by animalViewModel.showSuggestions.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // ---------- SEARCH MODE ----------
        if (selectedAnimal == null) {
            val fieldWidth = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Animols LaSalle",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))

                    // Caption
                    Text(
                        text = "Animal Info at Your Fingertips — For Lasallian Vet Students",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    // Search field
                    OutlinedTextField(
                        value = query,
                        onValueChange = { animalViewModel.updateQuery(it) },
                        label = { Text("Enter Animal Name") },
                        placeholder = { Text("e.g., lion, cheetah, eagle") },
                        modifier = fieldWidth,
                        singleLine = true,
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                IconButton(onClick = { animalViewModel.updateQuery("") }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )

                    // Autocomplete dropdown directly under field, same width
                    if (showSuggestions && animals.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = fieldWidth,
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 240.dp),
                            ) {
                                items(animals) { animal ->
                                    ListItem(
                                        headlineContent = { Text(animal.name) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                animalViewModel.onSuggestionClicked(animal.name)
                                            }
                                    )
                                    Divider()
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Get Info button
                    Button(
                        onClick = { animalViewModel.searchAnimal(query) },
                        modifier = fieldWidth,
                        enabled = !isLoading
                    ) {
                        Text("Get Info")
                    }

                    // Loading / error
                    Spacer(modifier = Modifier.height(12.dp))
                    if (isLoading) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                    }
                    error?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }


        // ---------- DETAILS MODE ----------
        selectedAnimal?.let { animal ->
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top App Bar style row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { animalViewModel.clearSearch() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Animols LaSalle",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(48.dp)) // balance for symmetry
                }

                // Animal details content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    AnimalDetailsCard(animal = animal)
                }
            }
        }

    }
}

// ---------- Helper UI ----------

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun LabelValueRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun Chip(text: String) {
    AssistChip(
        onClick = { /* no-op */ },
        label = { Text(text) }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipRow(items: List<String>) {
    if (items.isEmpty()) return
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { label ->
            AssistChip(
                onClick = { /* no-op */ },
                label = { Text(label) }
            )
        }
    }
}


@Composable
fun TaxonomyGrid(t: Taxonomy) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LabelValueRow("Kingdom", t.kingdom)
        LabelValueRow("Phylum", t.phylum)
        LabelValueRow("Class", t.`class`)
        LabelValueRow("Order", t.order)
        LabelValueRow("Family", t.family)
        LabelValueRow("Genus", t.genus)
        LabelValueRow("Scientific Name", t.scientific_name)
    }
}

@Composable
fun CharacteristicsBlock(c: Characteristics) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LabelValueRow("Diet", c.diet)
        LabelValueRow("Habitat", c.habitat)
        LabelValueRow("Top speed", c.top_speed)
        LabelValueRow("Lifespan", c.lifespan)
        LabelValueRow("Weight", c.weight)
        LabelValueRow("Height", c.height)
        LabelValueRow("Length", c.length)
        LabelValueRow("Color", c.color)
    }
}

@Composable
fun AnimalDetailsCard(animal: Animal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                animal.name,
                style = MaterialTheme.typography.headlineSmall
            )

            if (animal.locations.isNotEmpty()) {
                SectionTitle("Locations")
                ChipRow(animal.locations)
            }

            Divider()

            SectionTitle("Taxonomy")
            TaxonomyGrid(animal.taxonomy)

            Divider()

            SectionTitle("Characteristics")
            CharacteristicsBlock(animal.characteristics)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalSearchScreenPreview() {
    AnimolsTheme {
        AnimalSearchScreen()
    }
}
