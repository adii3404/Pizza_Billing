using System.Collections.Concurrent;
using PizzaBilling.Models;

namespace PizzaBilling.Services;

public interface IPizzaStoreService
{
    // Catalog & Stock
    IEnumerable<Pizza> GetAllPizzas();
    Pizza? GetPizzaById(int id);
    Pizza AddPizza(Pizza pizza);
    bool UpdatePizza(Pizza pizza);
    bool DeletePizza(int id);
    bool UpdateStock(int id, int quantity);

    // Orders & POS Billing
    Order PlaceOrder(CreateOrderRequest request);
    IEnumerable<Order> GetOrders();
    Order? GetOrderById(int orderId);
    bool CancelOrder(int orderId);
    bool DeleteOrder(int orderId);

    // Sales Reports
    IEnumerable<DailySalesSummary> GetDailySalesReport();
    IEnumerable<MonthlySalesSummary> GetMonthlySalesReport();
    IEnumerable<TopSellingPizzaSummary> GetTopSellingPizzas();

    // Staff & Managers
    IEnumerable<User> GetUsers();
    bool AddManager(string username, string fullName);
    bool DeleteManager(int id);
}

public class PizzaStoreService : IPizzaStoreService
{
    private readonly ConcurrentDictionary<int, Pizza> _pizzas = new();
    private readonly ConcurrentDictionary<int, Order> _orders = new();
    private readonly ConcurrentDictionary<int, User> _users = new();
    private int _nextPizzaId = 11;
    private int _nextOrderId = 1004;
    private int _nextUserId = 5;

    private const decimal TOPPING_PRICE = 40.0m;
    private const decimal DRINK_PRICE = 50.0m;
    private const decimal TAX_RATE = 0.025m; // CGST 2.5%, SGST 2.5%

    public PizzaStoreService()
    {
        SeedData();
    }

    private void SeedData()
    {
        // Staff and Manager records (Aditya Nimbalkar, Prathamesh, Vishwajit, Admin)
        _users.TryAdd(1, new User { Id = 1, Username = "admin", Role = "admin", FullName = "System Administrator" });
        _users.TryAdd(2, new User { Id = 2, Username = "aditya", Role = "manager", FullName = "Aditya Nimbalkar" });
        _users.TryAdd(3, new User { Id = 3, Username = "prathamesh", Role = "manager", FullName = "Prathamesh" });
        _users.TryAdd(4, new User { Id = 4, Username = "vishwajit", Role = "manager", FullName = "Vishwajit" });

        // Seed 10 Pizzas matching init.sql
        var seedPizzas = new List<Pizza>
        {
            new() { PizzaId = 1, Name = "Margherita", Category = "Classic Pizza", SmallPrice = 99m, MediumPrice = 199m, LargePrice = 399m, ExtraLargePrice = 499m, Quantity = 50, Description = "Classic delight with 100% real mozzarella cheese" },
            new() { PizzaId = 2, Name = "Farmhouse", Category = "Classic Pizza", SmallPrice = 149m, MediumPrice = 249m, LargePrice = 449m, ExtraLargePrice = 549m, Quantity = 30, Description = "Delightful combination of onion, capsicum, tomato & mushroom" },
            new() { PizzaId = 3, Name = "Peppy Paneer", Category = "Classic Pizza", SmallPrice = 159m, MediumPrice = 259m, LargePrice = 459m, ExtraLargePrice = 559m, Quantity = 25, Description = "Flavorful trio of juicy paneer, capsicum with red paprika" },
            new() { PizzaId = 4, Name = "Mexican Green Wave", Category = "Classic Pizza", SmallPrice = 169m, MediumPrice = 279m, LargePrice = 479m, ExtraLargePrice = 579m, Quantity = 20, Description = "Mexican herbs sprinkled on onion, capsicum, tomato & jalapeno" },
            new() { PizzaId = 5, Name = "Veggie Paradise", Category = "Classic Pizza", SmallPrice = 159m, MediumPrice = 259m, LargePrice = 459m, ExtraLargePrice = 559m, Quantity = 35, Description = "Golden corn, black olives, crisp capsicum & red paprika" },
            new() { PizzaId = 6, Name = "Pepper Barbecue Chicken", Category = "Pan Pizza", SmallPrice = 199m, MediumPrice = 319m, LargePrice = 519m, ExtraLargePrice = 619m, Quantity = 15, Description = "Pepper barbecue chicken for that smoky punch" },
            new() { PizzaId = 7, Name = "Chicken Golden Delight", Category = "Pan Pizza", SmallPrice = 219m, MediumPrice = 339m, LargePrice = 539m, ExtraLargePrice = 639m, Quantity = 10, Description = "Double barbecue chicken, golden corn and extra cheese" },
            new() { PizzaId = 8, Name = "Non Veg Supreme", Category = "Pan Pizza", SmallPrice = 249m, MediumPrice = 399m, LargePrice = 599m, ExtraLargePrice = 699m, Quantity = 12, Description = "Loaded with black olives, grilled chicken, hot 'n spicy chicken" },
            new() { PizzaId = 9, Name = "Chicken Dominator", Category = "Pan Pizza", SmallPrice = 269m, MediumPrice = 429m, LargePrice = 629m, ExtraLargePrice = 729m, Quantity = 8, Description = "Loaded with double pepper barbecue chicken, peri-peri chicken" },
            new() { PizzaId = 10, Name = "Spiced Double Chicken", Category = "Oil Pizza", SmallPrice = 229m, MediumPrice = 359m, LargePrice = 559m, ExtraLargePrice = 659m, Quantity = 10, Description = "Spiced double chicken cooked in signature herb oil" }
        };

        foreach (var p in seedPizzas)
        {
            _pizzas.TryAdd(p.PizzaId, p);
        }

        // Seed initial orders (cashiers: aditya, prathamesh, vishwajit)
        var order1 = new Order
        {
            OrderId = 1001,
            CustomerName = "Walk-in Guest 1",
            OrderDate = DateTime.UtcNow.AddDays(-1),
            PaymentMode = PaymentMode.Cash,
            CashierUsername = "aditya",
            DiscountPercent = 10,
            Subtotal = 289m,
            DiscountAmount = 28.9m,
            Cgst = 6.5m,
            Sgst = 6.5m,
            TotalPrice = 273.1m,
            Status = "Completed",
            Items = new List<OrderItem>
            {
                new() { PizzaId = 1, PizzaName = "Margherita", Category = "Classic Pizza", Size = PizzaSize.Medium, Quantity = 1, UnitPrice = 199m, Toppings = new() { "Cheese" }, ColdDrinks = new() { "Coca Cola" }, ItemTotal = 289m }
            }
        };

        var order2 = new Order
        {
            OrderId = 1002,
            CustomerName = "Takeaway Order 2",
            OrderDate = DateTime.UtcNow,
            PaymentMode = PaymentMode.UPI,
            UpiId = "guest@upi",
            CashierUsername = "prathamesh",
            DiscountPercent = 0,
            Subtotal = 628m,
            DiscountAmount = 0m,
            Cgst = 15.7m,
            Sgst = 15.7m,
            TotalPrice = 659.4m,
            Status = "Completed",
            Items = new List<OrderItem>
            {
                new() { PizzaId = 2, PizzaName = "Farmhouse", Category = "Classic Pizza", Size = PizzaSize.Medium, Quantity = 2, UnitPrice = 249m, Toppings = new() { "Onion", "Chilli" }, ColdDrinks = new() { "Sprite" }, ItemTotal = 628m }
            }
        };

        _orders.TryAdd(order1.OrderId, order1);
        _orders.TryAdd(order2.OrderId, order2);
    }

    public IEnumerable<Pizza> GetAllPizzas() => _pizzas.Values.OrderBy(p => p.PizzaId);

    public Pizza? GetPizzaById(int id) => _pizzas.TryGetValue(id, out var p) ? p : null;

    public Pizza AddPizza(Pizza pizza)
    {
        var id = Interlocked.Increment(ref _nextPizzaId);
        pizza.PizzaId = id;
        _pizzas[id] = pizza;
        return pizza;
    }

    public bool UpdatePizza(Pizza pizza)
    {
        if (!_pizzas.ContainsKey(pizza.PizzaId)) return false;
        _pizzas[pizza.PizzaId] = pizza;
        return true;
    }

    public bool DeletePizza(int id) => _pizzas.TryRemove(id, out _);

    public bool UpdateStock(int id, int quantity)
    {
        if (_pizzas.TryGetValue(id, out var pizza))
        {
            pizza.Quantity = Math.Max(0, quantity);
            return true;
        }
        return false;
    }

    public Order PlaceOrder(CreateOrderRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.CustomerName))
            throw new ArgumentException("Customer name cannot be empty");

        if (request.Items == null || request.Items.Count == 0)
            throw new ArgumentException("At least one pizza item must be selected");

        // Verify stock
        foreach (var itemReq in request.Items)
        {
            if (!_pizzas.TryGetValue(itemReq.PizzaId, out var pizza) || pizza.Quantity < itemReq.Quantity)
            {
                throw new InvalidOperationException($"Insufficient inventory for pizza ID {itemReq.PizzaId}");
            }
        }

        // Deduct inventory
        foreach (var itemReq in request.Items)
        {
            _pizzas[itemReq.PizzaId].Quantity -= itemReq.Quantity;
        }

        var orderItems = new List<OrderItem>();
        decimal subtotal = 0;

        foreach (var itemReq in request.Items)
        {
            var pizza = _pizzas[itemReq.PizzaId];
            var basePrice = pizza.GetPriceForSize(itemReq.Size);
            var toppingsTotal = itemReq.Toppings.Count * TOPPING_PRICE;
            var drinksTotal = itemReq.ColdDrinks.Count * DRINK_PRICE;
            var itemTotal = (basePrice * itemReq.Quantity) + toppingsTotal + drinksTotal;

            subtotal += itemTotal;

            orderItems.Add(new OrderItem
            {
                PizzaId = pizza.PizzaId,
                PizzaName = pizza.Name,
                Category = pizza.Category,
                Size = itemReq.Size,
                Quantity = itemReq.Quantity,
                UnitPrice = basePrice,
                Toppings = itemReq.Toppings,
                ColdDrinks = itemReq.ColdDrinks,
                ItemTotal = itemTotal
            });
        }

        var discountPercent = Math.Clamp(request.DiscountPercent, 0, 100);
        var discountAmount = Math.Round(subtotal * (discountPercent / 100m), 2);
        var discountedSubtotal = subtotal - discountAmount;
        var cgst = Math.Round(discountedSubtotal * TAX_RATE, 2);
        var sgst = Math.Round(discountedSubtotal * TAX_RATE, 2);
        var totalPrice = discountedSubtotal + cgst + sgst;

        var orderId = Interlocked.Increment(ref _nextOrderId);
        var order = new Order
        {
            OrderId = orderId,
            CustomerName = request.CustomerName.Trim(),
            OrderDate = DateTime.UtcNow,
            Items = orderItems,
            Subtotal = subtotal,
            DiscountPercent = discountPercent,
            DiscountAmount = discountAmount,
            Cgst = cgst,
            Sgst = sgst,
            TotalPrice = totalPrice,
            PaymentMode = request.PaymentMode,
            UpiId = request.UpiId,
            CashierUsername = request.CashierUsername,
            Status = "Completed"
        };

        _orders[orderId] = order;
        return order;
    }

    public IEnumerable<Order> GetOrders() => _orders.Values.OrderByDescending(o => o.OrderDate);

    public Order? GetOrderById(int orderId) => _orders.TryGetValue(orderId, out var o) ? o : null;

    public bool CancelOrder(int orderId)
    {
        if (_orders.TryGetValue(orderId, out var order) && order.Status != "Cancelled")
        {
            order.Status = "Cancelled";
            // Return stock to inventory
            foreach (var item in order.Items)
            {
                if (_pizzas.TryGetValue(item.PizzaId, out var pizza))
                {
                    pizza.Quantity += item.Quantity;
                }
            }
            return true;
        }
        return false;
    }

    public bool DeleteOrder(int orderId) => _orders.TryRemove(orderId, out _);

    public IEnumerable<DailySalesSummary> GetDailySalesReport()
    {
        return _orders.Values
            .Where(o => o.Status != "Cancelled")
            .GroupBy(o => new { Day = o.OrderDate.ToString("yyyy-MM-dd"), PaymentMode = o.PaymentMode.ToString() })
            .Select(g => new DailySalesSummary
            {
                OrderDay = g.Key.Day,
                PaymentMode = g.Key.PaymentMode,
                OrderCount = g.Count(),
                TotalEarnings = g.Sum(o => o.TotalPrice)
            })
            .OrderByDescending(s => s.OrderDay);
    }

    public IEnumerable<MonthlySalesSummary> GetMonthlySalesReport()
    {
        return _orders.Values
            .Where(o => o.Status != "Cancelled")
            .GroupBy(o => new { Month = o.OrderDate.ToString("yyyy-MM"), PaymentMode = o.PaymentMode.ToString() })
            .Select(g => new MonthlySalesSummary
            {
                OrderMonth = g.Key.Month,
                PaymentMode = g.Key.PaymentMode,
                OrderCount = g.Count(),
                TotalEarnings = g.Sum(o => o.TotalPrice)
            })
            .OrderByDescending(s => s.OrderMonth);
    }

    public IEnumerable<TopSellingPizzaSummary> GetTopSellingPizzas()
    {
        return _orders.Values
            .Where(o => o.Status != "Cancelled")
            .SelectMany(o => o.Items)
            .GroupBy(i => new { i.PizzaName, i.Category })
            .Select(g => new TopSellingPizzaSummary
            {
                PizzaName = g.Key.PizzaName,
                Category = g.Key.Category,
                TotalSold = g.Sum(i => i.Quantity),
                TotalRevenue = g.Sum(i => i.ItemTotal)
            })
            .OrderByDescending(s => s.TotalSold);
    }

    public IEnumerable<User> GetUsers() => _users.Values;

    public bool AddManager(string username, string fullName)
    {
        if (_users.Values.Any(u => u.Username.Equals(username, StringComparison.OrdinalIgnoreCase)))
            return false;

        var id = Interlocked.Increment(ref _nextUserId);
        var manager = new User
        {
            Id = id,
            Username = username,
            FullName = string.IsNullOrWhiteSpace(fullName) ? username : fullName,
            Role = "manager"
        };
        _users[id] = manager;
        return true;
    }

    public bool DeleteManager(int id)
    {
        if (_users.TryGetValue(id, out var user) && user.Role == "manager")
        {
            return _users.TryRemove(id, out _);
        }
        return false;
    }
}
