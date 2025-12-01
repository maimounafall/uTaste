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

This project is developed in **Java** using **Android Studio**, with **PlantUML** for UML modeling and **Github** for version control.





## How to Rebuild the Project

1. **Clone the repository:**
git clone https://github.com/uOttawa-2025-2026-seg2505-projet/Groupe-06-repo.git


2. Open the project folder in **Android Studio**
3. Wait for Gradle to sync automtically
4. Build and run the project on:
   - An **Android emulator** ( for the project),or 
   - A **physical Android device** if you have one 
5. No database configuration(SQ lite is optional for delivrable 1) is required for  **Delivrable 1** - all data is stored **in memory** using Java HasMap.

## Validation Scenario( Delivrable 1)
You can follow all these steps to verify that all required features work correctly:

1. Launch the application
2. On the login screen enter:
   - **Email:** 'admin@utaste.ca" ( Example)
   - **Password:** 'admin-pwd' (default password)
3. Login in as **Administrator**.
4. From the admin menu:
    - Add a new **Waiter**(full name:Waiter A, Email:waitera@utaste.ca, Password:waitera-pwd)
    - Modify an existing Waiter(Change the password to:waiteraa-pwdd)
    - Add a new **Waiter**(full name:Waiter B, Email:waiterb@utaste.ca, Password:waiterb-pwd)
    - Delete a waiter(Waiter B) 
5. Log out(top right button)
6. Log in using the newly created Waiter account:
   - **Email:** 'waitera@utaste.ca'
   - **Password:** 'Waiteraa-pwdd'
7. Log out again.
8. Log in as **Chef** using:
- **Email:** 'chef@utaste.ca'
- **Password:** 'chef-pwd
9.  Confirm that each user sees 
only their specific menu( Admin, Chef, Waiter).

**Expected Results:**
- Authentication works for all users.
- Admin can create, edit, and delete Waiters
- Each role accesses its own interface only.
- Password change works after authentification.
- All operations are temporary( in memory storage).


## Known Limitations

- **No databse persitence:** All data is stored in memory and lost when the app restarts\
- **Limited functionality fo non-admin roles:** Chef and waiter menus exist but without any implemented functions( it will be availaible in the future).
- **UI is functional but minimal:** The design focues on functionality rathet than the apperance for delivrable 1
- **No REST or JSON  network integration yet:** for the nutritonal informations. It will be implemented in future delivrables 

##  Project Status – Deliverable 1

| Feature | Status | Notes |
|----------|---------|-------|
| GitHub repository created and configured | ✅ | All members added |
| UML Diagrams (Class, Activity, Sequence) | ✅ | Stored in `/doc/uml` |
| User roles (Admin, Chef, Waiter) | ✅ | Implemented in memory |
| Authentication and logout | ✅ | Works for all users |
| Change password | ✅ | Implemented |
| Admin CRUD on Waiters | ✅ | Add / Modify / Delete Waiters |
| SQLite Database |❌  | Not required for Deliverable 1 |
| Recipes, Nutrition, Sales | ❌ | To be implemented in later deliverables |
| Demonstration video | ✅ | Included in `/demonstrations/` folder |

## Team Menbers and Roles

| Name | Role | Responsibilities |
|------|------|------------------|
| **Toure Idriss Hamad** | UML Designer / Documentation Lead | Class, Activity, and Sequence diagrams; documentation,video demo  |
| **Diagne,Mouhammad Habiballah** | Backend Developer | UserManager, authentication, logic implementation, video demo |
| **Fall Maïmouna** | Frontend Developer | Android UI design and navigation,video demo |
| **Mas'ud Habeebat** | Scrum Master / GitHub Manager | Repository setup, branching, integration, video demo |

 ##  Summary
Deliverable 1 focuses on the **core user management and authentication features**.  
The project now includes all required UML models, documentation, and a functional Android prototype with in-memory data storage.  
Future deliverables will extend the system with database support, recipe management, nutrition tracking, and sales recording.









 


