# Domino's Pizza POS & Billing System — .NET 8

Complete, production-ready Pizza Billing, Inventory POS, and Reporting System built entirely with **.NET 8 (ASP.NET Core)**.

## Architecture

- **Platform**: .NET 8.0 SDK (`Microsoft.NET.Sdk.Web`)
- **Server**: Kestrel web server serving RESTful Web API and responsive Domino's POS terminal
- **Project Structure**:
  - `PizzaBilling.sln`: Visual Studio solution
  - `PizzaBilling.csproj`: Web project definition
  - `Program.cs`: ASP.NET Core host, routing, Swagger, static files & Kestrel configuration
  - `Models/`:
    - `Pizza.cs`: Pizza entity, category enum, multi-tier pricing (`Small`, `Medium`, `Large`, `ExtraLarge`), stock quantities
    - `Order.cs`: Order & OrderItem entities, taxes (CGST 2.5%, SGST 2.5%), discounts, payment modes (`Cash`, `Online`, `UPI`)
    - `Reports.cs`: DTO models for Daily Sales, Monthly Sales, and Bestseller Leaderboards
    - `User.cs`: User & staff account models
  - `Services/`:
    - `IPizzaStoreService.cs` & `PizzaStoreService.cs`: Thread-safe concurrent storage service pre-seeded with 10 pizzas from `init.sql`, staff members (Manager: **Aditya Nimbalkar**, Staff: **Prathamesh**, **Vishwajit**), automatic stock deduction on checkout, and inventory rollback on cancellation
  - `Controllers/`:
    - `PizzasController.cs`: `/api/pizzas` (CRUD, category filtering, stock adjustments)
    - `OrdersController.cs`: `/api/orders` (POS checkout, order cancellation, order history)
    - `ReportsController.cs`: `/api/reports/daily-sales`, `/api/reports/monthly-sales`, `/api/reports/top-selling`
    - `UsersController.cs`: `/api/users` (Staff directory, add/delete staff)
    - `AuthController.cs`: `/api/auth/login`
  - `wwwroot/`:
    - Self-contained, responsive Domino's Pizza POS terminal client with thermal receipt generation and printable tax invoices
  - `appsettings.json`: Configuration settings and tax rates

## How to Run

```bash
# Restore packages
dotnet restore

# Build project
dotnet build

# Run application (starts on port 3000)
dotnet run
```

Open your browser to:
- `http://localhost:3000/` for the Domino's POS & Billing terminal
- `http://localhost:3000/swagger` for the interactive OpenAPI / Swagger API explorer
