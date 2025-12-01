# uTaste- Restaurant Management Application  

## Project Description
**uTaste** is an Android application developed for a gastronomic restaurant to help manage sales , nutritional information from ingredients, and recipes. The app allows multiple users to share the same device and authenticate with the specific roles 
**Administrator**
**Chef**
**Waiter**

Each role has acess to specific features:

- **Administrator :** manages user accounts(waiters) , can reset passwords and the database.
- **Chef :** manages recipes and ingredients, retrieves nutritional information, and calculates nutrition summaries.
- **Waiter:** Views recipes, records sales, and generates sales and reports.

Deliverable 3 – Chef Role (Part 2)

This deliverable extends the Chef role with nutrition-related features and automated testing.

 ## New Features Implemented

**1. Nutritional Information Retrieval**
   
- The Chef can now obtain real nutritional data for each ingredient    using the OpenFoodFacts API.

- The app uses a scanned barcode to identify the product and fetch its  nutrition facts (per 100 g).

- At least five key nutrition parameters are displayed, for example:

  Carbohydrates (100 g): 45.33 g  
  Fat (100 g): 3.33 g  
  Proteins (100 g): 9.33 g  
  Fibers (100 g): 6.67 g  
  Salt (100 g): 1.33 g  
  Calories (100 g): 210 kcal  


- Information is shown on a dedicated page: Nutrition Info Activity.

**2. Nutrition Summary Calculation**

- The Chef can view a global nutrition summary for each recipe.

- Totals for calories, carbohydrates, fat, and proteins are automatically calculated using ingredient proportions (quantityPercent).

- Results are displayed in the Recipe Nutrition Activity as a list or table.

**3. Automated Unit Tests (JUnit)**

- Automated testing was added to validate application logic:

- User / Vendor Management Tests (10+) – verify user creation, password resets, and data persistence.

- Ingredient and Nutrition Tests (10+) – validate correct API parsing, percentage handling, and nutrition-summary calculations.


**Validation Scenario – Deliverable 3**

The following steps demonstrate the new features introduced for the Chef role in Deliverable 3.

A. Chef – Nutritional Features

1. Add Ingredients to the Stock Using Barcode Scanning
 - Launch the uTaste application.
 - Log in as the Chef:
 - Email: chef@utaste.ca
 - Password: chef-pwd
 - On the Chef Home Page, tap Manage Stock.
 - Tap the Barcode Scan button .
 - Allow camera permissions if requested.
 - Scan any valid barcode from the demonstration folder.

The app retrieves:

Ingredient name

Nutrition values per 100 g (from OpenFoodFacts)

The ingredient is automatically added to the Stock List, which may contain:
- Milk
- Flour
- Sugar
- Nutella
- Salt

- Only 2–3 ingredients are needed for validation ( Milk, Sugar, Flour).

Expected Result:
Scanned ingredients appear in the Stock with proper name and nutrition facts.

2. Add Ingredients to a Recipe 

- Return to the Chef Home Page.
- Tap Manage Recipes.
- Select any existing recipe (e.g., Pasta, Burger, Pancakes).
- Tap Add Ingredient.
- The Add Ingredient screen appears with:

  - Ingredient

  - Quantity (%)

Tap the Ingredient Name field.

- You are redirected to the Stock List.
- Choose any previously scanned ingredient ( Milk).

- The Add Ingredient screen auto-fills:
  - Ingredient name
  - Barcode
  - Nutrition information

- Enter the quantity percentage (for example: 40).
- Tap Save.
- The ingredient now appears under the recipe.
- You may repeat this for:

   - Milk 40%
   - Flour 30%
   - Sugar 30%

Expected Result:
Ingredients from the Stock are correctly added to the recipe with percentages.

3. View Nutritional Information for Each Ingredient

- From Manage Recipes, open a recipe that contains ingredients.
- Tap any ingredient (e.g., Milk).
- Tap  the nutrition icon in front of any ingredient.
- The Nutrition Info Activity displays(like an example):

   - Carbohydrates (100 g)
   - Fat (100 g)
   - Protein (100 g)
   - Fibers (100 g)
   - Salt (100 g)
   -  Calories (100 g)

Expected Result:
Nutritional data appears exactly as returned from OpenFoodFacts.

4. View the Nutrition Summary of a Recipe

- Return to the recipe page.
- Tap Nutrition Summary icon.
- The system automatically computes totals using:
- nutrition values per 100 g
- ingredient percentages

The following totals are shown:

   - Total Calories
   - Total Carbohydrates
   - Total Fat
   - Total Protein

Updating or deleting an ingredient immediately updates the summary.

Expected Result:
The recipe's nutrition summary is calculated correctly and reflects real values.

5. User Management Automated Tests (JUnit)

Steps:
- Open Android Studio → Project → test/java/...
- Run UserServiceTest and RecipeServiceTest 
- Verify tests:
   - creation
   - validation
   - password reset
   - duplicates
   - persistence

Expected Result:
All tests pass successfully.

6. Ingredient + Nutrition Automated Tests (JUnit)

Steps: Run RecipeServiceTest 
Tests verify:
  - API parsing
  - JSON decoding
  - ingredient percentages
  - nutrition summary accuracy

Expected Result:
All tests pass.

## Technical Overview

### Architecture

The project continues to follow the layered structure:

**UI → Service → DAO → Database**

| **Layer** | **Main Components** | **Description** |
|------------|--------------------|-----------------|
| **Entities (Room)** | UserEntity, RecipeEntity, IngredientEntity, NutritionInfoEntity, RecipeIngredientXRef | Represent database tables, including nutrition info. |
| **DAO (Data Access Objects)** | UserDao, RecipeDao, IngredientDao, NutritionInfoDao | Handle all CRUD queries. |
| **Services (Business Logic)** | AuthService, AdminService, RecipeService, OpenFoodFactsService | Implement authentication, admin tools, recipe logic, and API communication. |
| **Database (Room)** | AppDatabase, DatabaseProvider | Manages the SQLite database instance. |
| **Utilities** | PasswordHasher, Converters, HttpHelper | Support password and JSON processing. |

## Known Limitations

- Internet connection required for OpenFoodFacts API.
- Waiter features (sales, reporting) planned for Deliverable 4.


## Project Status – Deliverable 3

| **Feature** | **Status** | **Notes** |
|--------------|------------|-----------|
| Recipe CRUD | OK | Same as Deliverable 2 |
| Ingredient CRUD | OK | Linked to recipes |
| Nutrition data (OpenFoodFacts) | OK | Fully integrated |
| Nutrition summary calculation | OK | Automatic & validated |
| SQLite (Room) database | OK | Persists between restarts |
| Admin reset tools | OK | Tested |
| Waiter sales module | NO | Planned (D4) |
| JUnit tests (users + ingredients) | OK | 20+ tests total |
| UML diagrams | OK | Updated (Class + Sequence in `/doc/uml/`) |
| Demonstration video | OK | Shows Chef workflow with API integration |


## Team Members and Roles

| **Name** | **Role** | **Responsibilities** |
|-----------|-----------|----------------------|
| **Toure Idriss Hamad** | UML Designer / Documentation Lead | Updated class & sequence diagrams; README; demo video |
| **Diagne Mouhammad Habiballah** | Backend Developer | API integration (OpenFoodFacts), Room logic, JUnit tests |
| **Fall Maïmouna** | Frontend Developer | UI design for Nutrition Info & Summary pages |
| **Mas'ud Habeebat** | Scrum Master / GitHub Manager | Repository management, branch integration, merge testing |

## Summary

Deliverable 3 enhances the Chef role by adding real nutritional-data integration and automatic calculation of recipe summaries.
The app now communicates with the OpenFoodFacts API, displays key nutrients per ingredient, and computes total caloric and macronutrient values per recipe. All core features are fully tested and stable, paving the way for Deliverable 4 (Waiter sales and reports).  

 