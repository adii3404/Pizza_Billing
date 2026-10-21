namespace PizzaBilling.Models;

public enum PaymentMode
{
    Cash,
    Online,
    UPI
}

public class OrderItem
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    public int PizzaId { get; set; }
    public string PizzaName { get; set; } = string.Empty;
    public string Category { get; set; } = string.Empty;
    public PizzaSize Size { get; set; } = PizzaSize.Medium;
    public int Quantity { get; set; }
    public decimal UnitPrice { get; set; }
    public List<string> Toppings { get; set; } = new();
    public List<string> ColdDrinks { get; set; } = new();
    public decimal ItemTotal { get; set; }
}

public class Order
{
    public int OrderId { get; set; }
    public string CustomerName { get; set; } = string.Empty;
    public DateTime OrderDate { get; set; } = DateTime.UtcNow;
    public List<OrderItem> Items { get; set; } = new();
    public decimal Subtotal { get; set; }
    public decimal DiscountPercent { get; set; }
    public decimal DiscountAmount { get; set; }
    public decimal Cgst { get; set; }
    public decimal Sgst { get; set; }
    public decimal TotalPrice { get; set; }
    public PaymentMode PaymentMode { get; set; } = PaymentMode.Cash;
    public string? UpiId { get; set; }
    public string CashierUsername { get; set; } = "aditya";
    public string Status { get; set; } = "Completed";
}

public class CreateOrderRequest
{
    public string CustomerName { get; set; } = string.Empty;
    public List<OrderItemRequest> Items { get; set; } = new();
    public decimal DiscountPercent { get; set; }
    public PaymentMode PaymentMode { get; set; }
    public string? UpiId { get; set; }
    public string CashierUsername { get; set; } = "aditya";
}

public class OrderItemRequest
{
    public int PizzaId { get; set; }
    public PizzaSize Size { get; set; }
    public int Quantity { get; set; }
    public List<string> Toppings { get; set; } = new();
    public List<string> ColdDrinks { get; set; } = new();
}
