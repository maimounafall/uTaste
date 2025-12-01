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

##  Deliverable 2 – Chef Features 

This deliverable focuses on the **Chef role**, introducing full database integration and CRUD operations for recipes and ingredients.

###  Features Implemented

#### 1. Recipe Management
- Create, modify, and delete recipes.  
- Each recipe includes a unique name, description, and local image.  
- Recipes are linked to the Chef who created them.

#### 2. Ingredient Management
- Add an ingredient by scanning a QR code and entering data(name, %) manually.  
- Define ingredient quantity in percentage (%).  
- Modify or remove existing ingredients.  
- Ingredients are stored in relation to their recipe using a junction table.

#### 3. Administrator Updates
- Reset the entire database to default state.  
- Reset a user’s password to its default value.  
- Update user profiles (name, email).
  
### Steps
1. **Clone the repository:**
git clone https://github.com/uOttawa-2025-2026-seg2505-projet/Groupe-06-repo.git
2. Open the project folder in **Android Studio**
3. Wait for Gradle to sync automtically
4. Build and run the project on:
   - An **Android emulator** ( for the project),or 
   - A **physical Android device** if you have one 
5. No external configuration is required, the database(SQLite) initializes automtically on first launch 


## Validation Scenario( Delivrable 2)
This validation scenario demonstrates all new features implemented in Deliverable 2, covering both the Chef and the Administrator functionalities.All steps can be reproduced on the Android emulator or a physical device.The database used is SQLite (Room) and all data remains persistent between app restarts.

A.Chef Functionalities 

 **Login as Chef**:
  1. Launch the uTaste application.
  2. On the login screen, enter the following credentials:
    - Email: chef@utaste.ca
    - Password: chef-pwd
  3. Tap Login
  4. The system authenticates the Chef and redirects to the Chef Home Page, displaying:
    - The Chef’s email address and profile information.
    - Two main options: Add Recipe and Manage Recipes.

 **Add a recipe**
  1. From the Chef home page, tap Add Recipe.
  2. In the recipe Name field, type : pancakes 
  3. In the description field, type : A light and fluffy breakfast pancake 
  4. Click select image and choose the right picture 
  5. Tape save recipe 
  6. Tap the back arrow(<-) to return to the chef Home page  

 **View Recipes List**
  1. On the Chef Home Page, tap Manage Recipes.
  2. The Manage Recipes screen opens, displaying one recipe card:
      - Recipe name: pancakes 
      - Buttons: Modify Recipe, Add Ingredient, Delete Recipe.
  3. Verify that the newly added recipe appears correctly in the list.

 **Add Ingredient to a recipe** 
   1. On the Manage Recipes page, click the "+" button below chocolate Cake 
   2. The Add ingredient screen appears
   3. you will find a folder with the name QrCode in the folder demonstrations   
   4. Tap the Qr code button 
   5. When prompted, click allow to give camera permission 
   6. Scan any Qr you want in the QrCode folder 
   7. After scanning, the Qr code value is captured automatically 
   8. In the ingredient Name field , type: flour 
   9. In the quantity(%) field type: 40
   10. tap save 
   11. you are redirected to the section manage recipes 
   12. repeat step 4 to 10 to add **sugar** with **quantity 30%** , **eggs** with **quantity 20%** and **milk** with **quantity 40%**

 **Modify Ingredient Quantity** 
   1. From the Manage Recipes page , tap the edit pencil icon beside the recipe 
   2. from the ingredient section , select the ingredient you want to modify ("milk") 
   3. Enter the new quantity ("50%") 
   4. click on the button save 

 **Delete An ingredient**  
   1. From the ingredient list  click on the trash icon from the ingredient you want to delete ("Sugar")
   2. A confirmation dialog appears:"Are you sure you want to delete this item ?"
   3. clik on delete 
   4. Confirm that sugar disappears from the ingredien list  

 **Modify recipe details**
   1. Always from the modify recipe click on the recipe name and change it to pancakes deluxe
   2. click on the description and change it: to A light and fluffy breakfast pancake. Perfect with syrup
   3. Tap save 
   4. return to the Manage Recipes page , the name appear as pancake delux

 **Delete a recipe** 
   1. On the manage recipe page, clik on the trash icon 
   2. A confirmation dialog appears: "Are you sure you want to delete this item ?"
   3. click on delete 
   4. the recipe and its ingredients are removec from the list 
   5. the Manage Recipes pages now appears empty again 

**Expected Results**  
- The Chef can successfully create, edit, and delete recipes.
- Ingredients can be added, modified, and deleted.
- QR code scanning works correctly and data is stored in SQLite.
- All information remains visible after closing and reopening the app

B. Administrator new functionalities

 **Login as Administrator**
   1. Open the uTaste app.
   2. Enter:
      - Email: admin@utaste.ca
      - Password: admin-pwd
   3. Tap Login.
   4. The app opens the Administrator Home Page

 **Add a New waiter** 
   1. From the Admin menu, tap add waiters 
   2. Enter:
      - Full name: Waiter A 
      - Email: waitera@utaste.ca
      - password: waitera-pwd 
   3. Click on save waiter

 **Modify Waiter Information** 
   1. Click on the Manage Waiters button , then select the waiter A
   2. Click on edit and put the new password: waiteraa-pwd
   3. Click on save  

 **Reset a user's password**
   1. From the administrator profile, tap on Manage Users
   2. On the Manage Users page , tap on the user whose password you want to reset  
   3. A confirmation dialog will appear asking: " "
   4. Tap confirm 
   5. The system resets the user's password to their default password 
  


 **Reset the Database**
   1. From the Administrator main menu , click **reset database** 
   2. A warning dialog appears asking for confirmation
   3. tap 
   4. All recipes, ingredients, and user(execept Admin and Chef accounts) are erased 
   
  
    
   

##  Technical Overview

### Architecture

The project follows a layered structure:  
**UI → Service → DAO → Database**

| **Layer** | **Main Components** | **Description** |
|------------|---------------------|-----------------|
| **Entities (Room)** | UserEntity, RecipeEntity, IngredientEntity, RecipeIngredientXRef, Role | Represent the database tables. |
| **DAO (Data Access Objects)** | UserDao, RecipeDao, IngredientDao, RecipeIngredientDao | Define SQL queries and CRUD operations. |
| **Services (Business Logic)** | AuthService, AdminService, RecipeService | Contain business logic for authentication, admin, and chef features. |
| **Database (Room)** | AppDatabase, DatabaseProvider | Singleton that manages the Room database instance. |
| **Utilities** | PasswordHasher, SimplePasswordHasher, Converters | Handle password hashing and enum conversions. |


## Known Limitations

- Nutritional data (OpenFoodFacts API) not yet integrated → planned for Deliverable 3.
- Waiter features (sales tracking and reports) not implemented yet.
- UI remains functional but simple.


##  Project Status – Deliverable 2


| **Feature** | **Status** | **Notes** |
|--------------|------------|-----------|
| Recipe creation / edition / deletion | OK | Fully implemented. |
| Ingredient management (QR Code, percentages) | OK | Functional and linked to recipes. |
| SQLite (Room) database integration | OK| Replaces in-memory storage from Deliverable 1. |
| Administrator reset tools | OK | Database and password reset implemented. |
| Nutrition data integration (OpenFoodFacts) | NO | Planned for Deliverable 3. |
| Sales module (Waiter role) | NO | Scheduled for Deliverable 4. |
| UML diagrams (Deliverable 2) | OK | Available in `/doc`/uml/ folder. |
| Demonstration video | OK | Located in `/demonstrations/ |



## Team Menbers and Roles

| Name | Role | Responsibilities |
|------|------|------------------|
| **Toure Idriss Hamad** | UML Designer / Documentation Lead | Class, Activity, and Sequence diagrams; documentation,video demo  |
| **Diagne,Mouhammad Habiballah** | Backend Developer | Database(SQLite), logic implementation, video demo |
| **Fall Maïmouna** | Frontend Developer | Android UI design and navigation,video demo |
| **Mas'ud Habeebat** | Scrum Master / GitHub Manager | Repository setup, branching, integration, video demo |

## Summary 
Deliverable 2 transforms uTaste from a simple prototype into a real Android application with persistent data storage.
The Chef role now supports full recipe and ingredient management through a local database(SQLite), while the Administrator can maintain system integrity.
Next deliverables will focus on:

- Nutrition data integration via OpenFoodFacts API
- Automatic calculation of calories, proteins, carbs, fats
- The Waiter module for sales and statistics





