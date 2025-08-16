package com.example.animols

data class Animal(
    val name: String,
    val taxonomy: Taxonomy,
    val locations: List<String>,
    val characteristics: Characteristics
)

data class Taxonomy(
    val kingdom: String,
    val phylum: String,
    val `class`: String,   // backticks because "class" is reserved
    val order: String,
    val family: String,
    val genus: String,
    val scientific_name: String
)

data class Characteristics(
    val diet: String?,
    val habitat: String?,
    val lifespan: String?,
    val weight: String?,
    val top_speed: String?,
    val group_behavior: String?,
    val most_distinctive_feature: String?,
    val biggest_threat: String?,
    val gestation_period: String?,
    val litter_size: String?,
    val height: String?,
    val length: String?,
    val color: String?
)
