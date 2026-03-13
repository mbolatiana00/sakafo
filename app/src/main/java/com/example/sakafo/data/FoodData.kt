package com.example.sakafo.data

data class Dish(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val restaurant: String,
    val preparationTime: String,
    val category: String
)

data class Category(
    val name: String,
    val emoji: String
)

object FoodData {
    val categories = listOf(
        Category("Tout", "🍽️"),
        Category("Burger", "🍔"),
        Category("Pizza", "🍕"),
        Category("Sushi", "🍣"),
        Category("Salade", "🥗"),
        Category("Dessert", "🍰")
    )

    val dishes = listOf(
        Dish(
            id = "1",
            name = "Burger Déluxe",
            description = "Burger savoureux avec fromage et bacon",
            price = 12.99,
            rating = 4.8,
            restaurant = "Burger King",
            preparationTime = "15 min",
            category = "Burger"
        ),
        Dish(
            id = "2",
            name = "Pizza Margherita",
            description = "Pizza traditionnelle avec tomate et mozzarella",
            price = 10.99,
            rating = 4.6,
            restaurant = "Pizza Hut",
            preparationTime = "20 min",
            category = "Pizza"
        ),
        Dish(
            id = "3",
            name = "Sushi Mix",
            description = "Assortiment de sushis frais",
            price = 18.99,
            rating = 4.9,
            restaurant = "Sushi Place",
            preparationTime = "25 min",
            category = "Sushi"
        ),
        Dish(
            id = "4",
            name = "Salade César",
            description = "Salade fraîche avec poulet grillé",
            price = 9.99,
            rating = 4.5,
            restaurant = "Health Food",
            preparationTime = "10 min",
            category = "Salade"
        ),
        Dish(
            id = "5",
            name = "Gâteau Chocolat",
            description = "Gâteau au chocolat moelleux",
            price = 5.99,
            rating = 4.7,
            restaurant = "Pâtisserie",
            preparationTime = "5 min",
            category = "Dessert"
        ),
        Dish(
            id = "6",
            name = "Cheeseburger",
            description = "Burger classique avec fromage cheddar",
            price = 11.99,
            rating = 4.6,
            restaurant = "Burger King",
            preparationTime = "12 min",
            category = "Burger"
        ),
        Dish(
            id = "7",
            name = "Pizza Quatre Fromages",
            description = "Pizza avec 4 sortes de fromages",
            price = 12.99,
            rating = 4.7,
            restaurant = "Pizza Hut",
            preparationTime = "22 min",
            category = "Pizza"
        ),
        Dish(
            id = "8",
            name = "California Roll",
            description = "Sushi avec avocat et crabe",
            price = 8.99,
            rating = 4.8,
            restaurant = "Sushi Place",
            preparationTime = "15 min",
            category = "Sushi"
        )
    )

    fun getDishesByCategory(category: String): List<Dish> {
        return if (category == "Tout") {
            dishes
        } else {
            dishes.filter { it.category == category }
        }
    }

    fun searchDishes(query: String): List<Dish> {
        val lowerQuery = query.lowercase()
        return dishes.filter { dish ->
            dish.name.lowercase().contains(lowerQuery) ||
                    dish.description.lowercase().contains(lowerQuery) ||
                    dish.restaurant.lowercase().contains(lowerQuery)
        }
    }
}