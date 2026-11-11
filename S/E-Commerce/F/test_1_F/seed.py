"""Seed categories, sample products, and default admin/customer accounts."""

from extensions import db
from models import User, Category, Product, Cart


CATEGORIES = [
    ("Laptops", "Portable computers for work and play", "bi-laptop"),
    ("Desktop PCs", "Ready-to-use desktop computers", "bi-pc-display"),
    ("Gaming PCs", "High-performance gaming systems", "bi-controller"),
    ("CPUs", "Processors from Intel and AMD", "bi-cpu"),
    ("Graphics Cards", "NVIDIA and AMD GPUs", "bi-gpu-card"),
    ("RAM", "System memory modules", "bi-memory"),
    ("SSD", "Solid state storage drives", "bi-device-ssd"),
    ("HDD", "Hard disk drives", "bi-hdd"),
    ("Motherboards", "Motherboard platforms", "bi-motherboard"),
    ("Power Supplies", "PSU units", "bi-lightning-charge"),
    ("PC Cases", "Computer cases and chassis", "bi-box"),
    ("Monitors", "Displays and gaming monitors", "bi-display"),
    ("Keyboards", "Mechanical and membrane keyboards", "bi-keyboard"),
    ("Mice", "Gaming and office mice", "bi-mouse"),
    ("Headsets", "Gaming and office headsets", "bi-headphones"),
    ("Speakers", "Desktop speakers", "bi-speaker"),
    ("Networking", "Routers, switches, and adapters", "bi-router"),
    ("Accessories", "Cables, stands, and more", "bi-usb-symbol"),
]


PRODUCTS = [
    {
        "name": "NVIDIA GeForce RTX 5070",
        "brand": "NVIDIA",
        "category": "Graphics Cards",
        "price": 649.00,
        "discount_price": 599.00,
        "stock": 25,
        "rating": 4.8,
        "sales": 120,
        "featured": True,
        "image": "rtx5070.jpg",
        "description": "Next-generation graphics performance for gaming and content creation.",
        "specifications": "CUDA Cores: High\nMemory: 12GB GDDR7\nInterface: PCIe 5.0\nOutputs: HDMI 2.1, DisplayPort 2.1",
    },
    {
        "name": "Intel Core i7-14700K",
        "brand": "Intel",
        "category": "CPUs",
        "price": 419.00,
        "discount_price": 389.00,
        "stock": 40,
        "rating": 4.7,
        "sales": 95,
        "featured": True,
        "image": "intel-i7.jpg",
        "description": "High-performance desktop processor for creators and gamers.",
        "specifications": "Cores: 20 (8P+12E)\nThreads: 28\nMax Turbo: 5.6 GHz\nSocket: LGA 1700",
    },
    {
        "name": "AMD Ryzen 7 7800X3D",
        "brand": "AMD",
        "category": "CPUs",
        "price": 449.00,
        "discount_price": None,
        "stock": 35,
        "rating": 4.9,
        "sales": 140,
        "featured": True,
        "image": "ryzen7.jpg",
        "description": "Gaming-focused CPU with 3D V-Cache technology.",
        "specifications": "Cores: 8\nThreads: 16\nCache: 96MB\nSocket: AM5",
    },
    {
        "name": "32GB DDR5 RAM Kit (2x16GB)",
        "brand": "Corsair",
        "category": "RAM",
        "price": 149.00,
        "discount_price": 129.00,
        "stock": 60,
        "rating": 4.6,
        "sales": 200,
        "featured": True,
        "image": "ddr5-ram.jpg",
        "description": "Fast DDR5 dual-channel memory kit for modern platforms.",
        "specifications": "Capacity: 32GB (2x16GB)\nSpeed: 6000 MT/s\nLatency: CL30\nRGB: Yes",
    },
    {
        "name": "Samsung 990 PRO 2TB SSD",
        "brand": "Samsung",
        "category": "SSD",
        "price": 199.00,
        "discount_price": 179.00,
        "stock": 50,
        "rating": 4.8,
        "sales": 175,
        "featured": True,
        "image": "samsung-990.jpg",
        "description": "Ultra-fast NVMe SSD for gaming and professional workloads.",
        "specifications": "Capacity: 2TB\nInterface: PCIe 4.0 NVMe\nRead: up to 7450 MB/s\nWrite: up to 6900 MB/s",
    },
    {
        "name": "ASUS TUF Gaming 27\" Monitor",
        "brand": "ASUS",
        "category": "Monitors",
        "price": 329.00,
        "discount_price": 299.00,
        "stock": 30,
        "rating": 4.5,
        "sales": 88,
        "featured": True,
        "image": "asus-monitor.jpg",
        "description": "27-inch IPS gaming monitor with high refresh rate.",
        "specifications": "Size: 27 inch\nResolution: 2560x1440\nRefresh: 165Hz\nPanel: IPS",
    },
    {
        "name": "Logitech G Pro X Superlight",
        "brand": "Logitech",
        "category": "Mice",
        "price": 159.00,
        "discount_price": 139.00,
        "stock": 45,
        "rating": 4.7,
        "sales": 210,
        "featured": False,
        "image": "logitech-mouse.jpg",
        "description": "Ultra-lightweight wireless gaming mouse.",
        "specifications": "Weight: <63g\nSensor: HERO 25K\nBattery: up to 70 hours\nConnectivity: LIGHTSPEED",
    },
    {
        "name": "Mechanical Gaming Keyboard",
        "brand": "Razer",
        "category": "Keyboards",
        "price": 149.00,
        "discount_price": 119.00,
        "stock": 55,
        "rating": 4.6,
        "sales": 160,
        "featured": False,
        "image": "mech-keyboard.jpg",
        "description": "RGB mechanical keyboard with tactile switches.",
        "specifications": "Switches: Tactile\nBacklight: RGB\nLayout: Full-size\nCable: Detachable USB-C",
    },
    {
        "name": "HyperX Cloud III Gaming Headset",
        "brand": "HyperX",
        "category": "Headsets",
        "price": 99.00,
        "discount_price": 79.00,
        "stock": 70,
        "rating": 4.5,
        "sales": 130,
        "featured": False,
        "image": "headset.jpg",
        "description": "Comfortable gaming headset with clear mic and spatial audio.",
        "specifications": "Drivers: 53mm\nConnection: 3.5mm / USB\nMic: Noise-cancelling\nCompatible: PC, console",
    },
    {
        "name": "850W Gold Modular Power Supply",
        "brand": "Seasonic",
        "category": "Power Supplies",
        "price": 149.00,
        "discount_price": None,
        "stock": 40,
        "rating": 4.8,
        "sales": 90,
        "featured": False,
        "image": "psu-850.jpg",
        "description": "Reliable fully modular 850W PSU with 80+ Gold efficiency.",
        "specifications": "Wattage: 850W\nEfficiency: 80+ Gold\nModular: Fully\nFan: 135mm",
    },
    {
        "name": "ASUS ROG Strix G16 Laptop",
        "brand": "ASUS",
        "category": "Laptops",
        "price": 1499.00,
        "discount_price": 1399.00,
        "stock": 15,
        "rating": 4.6,
        "sales": 55,
        "featured": True,
        "image": "gaming-laptop.jpg",
        "description": "Powerful gaming laptop with high-refresh display.",
        "specifications": "CPU: Intel Core i7\nGPU: RTX 4070\nRAM: 16GB\nStorage: 1TB SSD",
    },
    {
        "name": "TechStore Phantom Gaming PC",
        "brand": "TechStore",
        "category": "Gaming PCs",
        "price": 1899.00,
        "discount_price": 1749.00,
        "stock": 10,
        "rating": 4.9,
        "sales": 42,
        "featured": True,
        "image": "gaming-pc.jpg",
        "description": "Prebuilt gaming PC optimized for 1440p and 4K gaming.",
        "specifications": "CPU: Ryzen 7\nGPU: RTX 5070\nRAM: 32GB DDR5\nSSD: 2TB NVMe",
    },
]


def seed_database():
    # Admin
    admin = User.query.filter_by(email="admin@techstore.com").first()
    if not admin:
        admin = User(name="Admin", email="admin@techstore.com", role="admin")
        admin.set_password("admin123")
        db.session.add(admin)
        db.session.flush()
        db.session.add(Cart(user_id=admin.id))

    # Demo customer
    customer = User.query.filter_by(email="customer@techstore.com").first()
    if not customer:
        customer = User(name="Demo Customer", email="customer@techstore.com", role="customer")
        customer.set_password("customer123")
        db.session.add(customer)
        db.session.flush()
        db.session.add(Cart(user_id=customer.id))

    cat_map = {}
    for name, desc, icon in CATEGORIES:
        cat = Category.query.filter_by(name=name).first()
        if not cat:
            cat = Category(
                name=name,
                description=desc,
                slug=name.lower().replace(" ", "-"),
                icon=icon,
            )
            db.session.add(cat)
            db.session.flush()
        cat_map[name] = cat

    for data in PRODUCTS:
        existing = Product.query.filter_by(name=data["name"]).first()
        if existing:
            continue
        category = cat_map.get(data["category"])
        if not category:
            continue
        product = Product(
            name=data["name"],
            brand=data["brand"],
            description=data["description"],
            specifications=data["specifications"],
            price=data["price"],
            discount_price=data["discount_price"],
            stock_quantity=data["stock"],
            image=data["image"],
            rating=data["rating"],
            sales_count=data["sales"],
            is_featured=data["featured"],
            category_id=category.id,
        )
        db.session.add(product)

    db.session.commit()


if __name__ == "__main__":
    from app import create_app
    from extensions import db as _db

    application = create_app()
    with application.app_context():
        _db.create_all()
        seed_database()
        print("Database tables created and sample data seeded.")
