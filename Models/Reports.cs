namespace PizzaBilling.Models;

public class User
{
    public int Id { get; set; }
    public string Username { get; set; } = string.Empty;
    public string Role { get; set; } = "manager"; // admin | manager
    public string FullName { get; set; } = string.Empty;
    public string CreatedDate { get; set; } = DateTime.UtcNow.ToString("yyyy-MM-dd");
}

public class DailySalesSummary
{
    public string OrderDay { get; set; } = string.Empty;
    public string PaymentMode { get; set; } = string.Empty;
    public int OrderCount { get; set; }
    public decimal TotalEarnings { get; set; }
}

public class MonthlySalesSummary
{
    public string OrderMonth { get; set; } = string.Empty;
    public string PaymentMode { get; set; } = string.Empty;
    public int OrderCount { get; set; }
    public decimal TotalEarnings { get; set; }
}

public class TopSellingPizzaSummary
{
    public string PizzaName { get; set; } = string.Empty;
    public string Category { get; set; } = string.Empty;
    public int TotalSold { get; set; }
    public decimal TotalRevenue { get; set; }
}
