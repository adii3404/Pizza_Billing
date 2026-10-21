namespace PizzaBilling.Models;

public enum PizzaSize
{
    Small,
    Medium,
    Large,
    ExtraLarge
}

public class Pizza
{
    public int PizzaId { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Category { get; set; } = "Classic Pizza";
    public decimal SmallPrice { get; set; }
    public decimal MediumPrice { get; set; }
    public decimal LargePrice { get; set; }
    public decimal ExtraLargePrice { get; set; }
    public int Quantity { get; set; }
    public string ImagePath { get; set; } = "/default_pizza.png";
    public string Description { get; set; } = string.Empty;

    public decimal GetPriceForSize(PizzaSize size) => size switch
    {
        PizzaSize.Small => SmallPrice,
        PizzaSize.Medium => MediumPrice,
        PizzaSize.Large => LargePrice,
        PizzaSize.ExtraLarge => ExtraLargePrice,
        _ => MediumPrice
    };
}
