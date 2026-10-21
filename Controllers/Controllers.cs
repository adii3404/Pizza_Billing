using Microsoft.AspNetCore.Mvc;
using PizzaBilling.Models;
using PizzaBilling.Services;

namespace PizzaBilling.Controllers;

[ApiController]
[Route("api/[controller]")]
public class PizzasController : ControllerBase
{
    private readonly IPizzaStoreService _service;

    public PizzasController(IPizzaStoreService service)
    {
        _service = service;
    }

    [HttpGet]
    public ActionResult<IEnumerable<Pizza>> GetAll() => Ok(_service.GetAllPizzas());

    [HttpGet("{id:int}")]
    public ActionResult<Pizza> GetById(int id)
    {
        var pizza = _service.GetPizzaById(id);
        return pizza != null ? Ok(pizza) : NotFound();
    }

    [HttpPost]
    public ActionResult<Pizza> Create([FromBody] Pizza pizza)
    {
        var created = _service.AddPizza(pizza);
        return CreatedAtAction(nameof(GetById), new { id = created.PizzaId }, created);
    }

    [HttpPut("{id:int}")]
    public IActionResult Update(int id, [FromBody] Pizza pizza)
    {
        pizza.PizzaId = id;
        return _service.UpdatePizza(pizza) ? NoContent() : NotFound();
    }

    [HttpDelete("{id:int}")]
    public IActionResult Delete(int id) => _service.DeletePizza(id) ? NoContent() : NotFound();

    [HttpPatch("{id:int}/stock")]
    public IActionResult UpdateStock(int id, [FromBody] int quantity)
    {
        return _service.UpdateStock(id, quantity) ? NoContent() : NotFound();
    }
}

[ApiController]
[Route("api/[controller]")]
public class OrdersController : ControllerBase
{
    private readonly IPizzaStoreService _service;

    public OrdersController(IPizzaStoreService service)
    {
        _service = service;
    }

    [HttpGet]
    public ActionResult<IEnumerable<Order>> GetAll() => Ok(_service.GetOrders());

    [HttpGet("{id:int}")]
    public ActionResult<Order> GetById(int id)
    {
        var order = _service.GetOrderById(id);
        return order != null ? Ok(order) : NotFound();
    }

    [HttpPost]
    public ActionResult<Order> PlaceOrder([FromBody] CreateOrderRequest request)
    {
        try
        {
            var order = _service.PlaceOrder(request);
            return CreatedAtAction(nameof(GetById), new { id = order.OrderId }, order);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { error = ex.Message });
        }
    }

    [HttpPost("{id:int}/cancel")]
    public IActionResult Cancel(int id) => _service.CancelOrder(id) ? Ok(new { success = true }) : NotFound();

    [HttpDelete("{id:int}")]
    public IActionResult Delete(int id) => _service.DeleteOrder(id) ? NoContent() : NotFound();
}

[ApiController]
[Route("api/[controller]")]
public class ReportsController : ControllerBase
{
    private readonly IPizzaStoreService _service;

    public ReportsController(IPizzaStoreService service)
    {
        _service = service;
    }

    [HttpGet("daily-sales")]
    public ActionResult<IEnumerable<DailySalesSummary>> GetDailySales() => Ok(_service.GetDailySalesReport());

    [HttpGet("monthly-sales")]
    public ActionResult<IEnumerable<MonthlySalesSummary>> GetMonthlySales() => Ok(_service.GetMonthlySalesReport());

    [HttpGet("top-selling")]
    public ActionResult<IEnumerable<TopSellingPizzaSummary>> GetTopSelling() => Ok(_service.GetTopSellingPizzas());
}

[ApiController]
[Route("api/[controller]")]
public class UsersController : ControllerBase
{
    private readonly IPizzaStoreService _service;

    public UsersController(IPizzaStoreService service)
    {
        _service = service;
    }

    [HttpGet]
    public ActionResult<IEnumerable<User>> GetAll() => Ok(_service.GetUsers());

    public record AddManagerDto(string Username, string FullName);

    [HttpPost("manager")]
    public IActionResult AddManager([FromBody] AddManagerDto dto)
    {
        var ok = _service.AddManager(dto.Username, dto.FullName);
        return ok ? Ok(new { success = true }) : Conflict(new { error = "Username already exists" });
    }

    [HttpDelete("manager/{id:int}")]
    public IActionResult DeleteManager(int id) => _service.DeleteManager(id) ? NoContent() : NotFound();
}

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly IPizzaStoreService _service;

    public AuthController(IPizzaStoreService service)
    {
        _service = service;
    }

    public record LoginRequest(string Username, string Role);

    [HttpPost("login")]
    public IActionResult Login([FromBody] LoginRequest req)
    {
        var users = _service.GetUsers();
        var match = users.FirstOrDefault(u => u.Username.Equals(req.Username, StringComparison.OrdinalIgnoreCase) && u.Role.Equals(req.Role, StringComparison.OrdinalIgnoreCase));
        if (match != null)
        {
            return Ok(match);
        }
        return Ok(new User
        {
            Id = 99,
            Username = req.Username,
            FullName = req.Username,
            Role = req.Role
        });
    }
}
