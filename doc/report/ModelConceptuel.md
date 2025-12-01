#  uTaste – UML Class Diagram (Conceptual Model)

##  Description
This file contains the **conceptual model** for the *uTaste* application – Deliverable 1.  
It represents the main classes, their attributes, methods, relationships, and hierarchy.  

 
##  UML Class Diagram – PlantUML Code

@startuml
title Class Diagram - uTaste  Conceptual Model


' Interfaces

interface IAuthenticationService {
  + login(email:String, password:String) : boolean
  + logout() : void
  + changePassword(user:User, newPwd:String) : void
}

interface IUserRepository {
  + addUser(u:User) : void
  + removeUser(email:String) : void
  + findUser(email:String) : User
  + getAllUsers() : List<User>
}


' Abstract Class User

abstract class User {
  - firstName : String
  - lastName : String
  - email : String <<unique>>
  - password : String
  - createdAt : DateTime <<auto>>
  - updatedAt : DateTime <<auto>>
  + getEmail() : String
  + getPassword() : String
  + updateTimestamp() : void
}
'User implements IAuthentificationService
User ..|> IAuthenticationService


' Subclasses

class Administrator {
  + createWaiter(email:String) : User
  + modifyWaiter(u:User, firstName:String, lastName:String, email:String) : void
  + deleteWaiter(u:User) : void
  + resetDatabase() : void
}

class Chef {
  + createRecipe() : void
  + modifyRecipe() : void
  + deleteRecipe() : void
  + addIngredient() : void
  + modifyIngredient() : void
  + getNutritionInfo() : void
  + calculateNutritionSummary() : void
}

class Waiter {
  + viewRecipes() : void
  + recordSale() : void
  + viewSalesReport() : void
}

' Heritages
User <|-- Administrator
User <|-- Chef
User <|-- Waiter


' Role & Permission

class Role {
  - name : String <<Admin, Chef, Waiter>>
}

class PermissionManager {
  + hasPermission(user:User, action:String) : boolean
}

User "1" -- "1" Role : has >
PermissionManager --> Role : checks >

' User Management

class UserManager {
  - users : Map<String,User>
  + addUser(u:User) : void
  + removeUser(email:String) : void
  + updateUser(u:User) : void
  + authenticate(email:String, password:String) : User
  + changePassword(u:User, newPassword:String) : void
  + getUsers() : Map<String,User>
}

UserManager --> IUserRepository : uses >
UserManager --> DataValidator : validates >

class DataValidator {
  + isValidEmail(email:String) : boolean
  + isValidPassword(pwd:String) : boolean
}

Administrator --> UserManager : uses >


' Session and Backend

class SessionManager {
  - activeUser : User
  + login(user:User) : void
  + logout() : void
  + getActiveUser() : User
}

SessionManager "1" -- "0..1" User : manages >

class UTasteBackend {
  - userManager : UserManager
  - sessionManager : SessionManager
  - permissionManager : PermissionManager
  + init() : void
}

UTasteBackend --> UserManager
UTasteBackend --> SessionManager
UTasteBackend --> PermissionManager

class UTasteApplication {
  + main() : void
}

UTasteApplication --> UTasteBackend : initializes >


' Functional domain classes

class Recipe {
  - id : String
  - title : String
  - description : String
  - createdAt : DateTime
  + calculateNutrition() : NutritionSummary
  + getAverageRating() : Float
}

class Ingredient {
  - name : String
  - per100g_calories : Float
  - per100g_protein : Float
  - per100g_fat : Float
  - per100g_carbs : Float
  + fetchNutritionData(code:String) : boolean
}

class NutritionSummary {
  - calories : Float
  - protein : Float
  - fat : Float
  - carbs : Float
  + asString() : String
}

class Sale {
  - date : DateTime
  - rating : Integer
  - comment : String
}

class SalesReport {
  - perRecipeCounts : Map<String,Integer>
  - perRecipeAverageRating : Map<String,Float>
  + generate() : void
}


' Associations in functional domain

Recipe "1" -- "1..*" Ingredient : contains >
Recipe --> NutritionSummary : calculates >
Waiter "1" -- "0..*" Sale : records >
Sale "1" -- "1" Recipe : concerns >
Waiter --> SalesReport : generates >

@enduml




##  UML Diagram Image
Below is the picture of the class diagram:

![Diagramme de classe](ClassDiagram.png)


##  Notes
- The model represents all core entities, managers, and relationships defined in the uTaste system.
  
- The **User abstract class** defines the common attributes and behaviors of all users (Administrator, Chef, Waiter), including authentication and profile management.
  
- The **IAuthenticationService** interface formalizes login, logout, and password change operations, implemented by user-related classes.
  
- The **UserManager** handles in-memory user management, validation through DataValidator, and will later connect to persistent storage via the IUserRepository interface.
  
- The **SessionManager** maintains the currently authenticated user, while the PermissionManager ensures that users only access actions allowed by their role.
  
- The **UTasteBackend** centralizes key components — UserManager, SessionManager, and PermissionManager — and is initialized by the **UTasteApplication**.
  
- The **domain layer** defines core business entities such as Recipe, Ingredient, NutritionSummary, Sale, and SalesReport, representing the restaurant’s operational logic.
  
- The **Chef** manages recipes and ingredients, the Waiter records sales and generates reports, and the Administrator manages waiter accounts and system resets.
  
- Future deliverables will extend this model with **SQLite-based** persistence, REST/JSON **API integration**, and complete UI interaction for each user role.




