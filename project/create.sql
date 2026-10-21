CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'manager') NOT NULL
);

CREATE TABLE Pizzas (
    pizza_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category ENUM('Classic Pizza', 'Pan Pizza', 'Oil Pizza') NOT NULL,
    small_price DECIMAL(10,2) NOT NULL,
    medium_price DECIMAL(10,2) NOT NULL,
    large_price DECIMAL(10,2) NOT NULL,
    extra_large_price DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 0
);

CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    cust_name VARCHAR(30) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    payment_mode ENUM('Cash', 'Online', 'UPI') DEFAULT 'Cash',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE Order_Details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    pizza_id INT NOT NULL,
    pizza_name VARCHAR(100) NOT NULL,
    size ENUM('Small', 'Medium', 'Large', 'XL') NOT NULL,
    category ENUM('Classic Pizza', 'Pan Pizza', 'Oil Pizza') NOT NULL,
    quantity INT NOT NULL,
    toppings VARCHAR(255),
    cold_drinks VARCHAR(100),
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (pizza_id) REFERENCES Pizzas(pizza_id) ON DELETE CASCADE
);
