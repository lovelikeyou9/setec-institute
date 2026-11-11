"""Download free stock product images (Unsplash) and attach them to products."""
from pathlib import Path
from urllib.request import Request, urlopen

from app import create_app
from extensions import db
from models import Product

OUT = Path(__file__).resolve().parent / "static" / "images" / "products"
OUT.mkdir(parents=True, exist_ok=True)

# Free Unsplash photos (tech/hardware). Query params request a square crop.
IMAGES = {
    "rtx5070.jpg": "https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=800&h=800&q=80",
    "intel-i7.jpg": "https://images.unsplash.com/photo-1555617981-dac3880eac6e?auto=format&fit=crop&w=800&h=800&q=80",
    "ryzen7.jpg": "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&h=800&q=80",
    "ddr5-ram.jpg": "https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=800&h=800&q=80",
    "samsung-990.jpg": "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=800&h=800&q=80",
    "asus-monitor.jpg": "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=800&h=800&q=80",
    "logitech-mouse.jpg": "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?auto=format&fit=crop&w=800&h=800&q=80",
    "mech-keyboard.jpg": "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?auto=format&fit=crop&w=800&h=800&q=80",
    "headset.jpg": "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=800&h=800&q=80",
    "psu-850.jpg": "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=800&h=800&q=80",
    "gaming-laptop.jpg": "https://images.unsplash.com/photo-1603302576837-37561b2e2302?auto=format&fit=crop&w=800&h=800&q=80",
    "gaming-pc.jpg": "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?auto=format&fit=crop&w=800&h=800&q=80",
    "placeholder.jpg": "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&h=800&q=80",
}

# Map product name -> image filename
PRODUCT_IMAGES = {
    "NVIDIA GeForce RTX 5070": "rtx5070.jpg",
    "Intel Core i7-14700K": "intel-i7.jpg",
    "AMD Ryzen 7 7800X3D": "ryzen7.jpg",
    "32GB DDR5 RAM Kit (2x16GB)": "ddr5-ram.jpg",
    "Samsung 990 PRO 2TB SSD": "samsung-990.jpg",
    'ASUS TUF Gaming 27" Monitor': "asus-monitor.jpg",
    "Logitech G Pro X Superlight": "logitech-mouse.jpg",
    "Mechanical Gaming Keyboard": "mech-keyboard.jpg",
    "HyperX Cloud III Gaming Headset": "headset.jpg",
    "850W Gold Modular Power Supply": "psu-850.jpg",
    "ASUS ROG Strix G16 Laptop": "gaming-laptop.jpg",
    "TechStore Phantom Gaming PC": "gaming-pc.jpg",
}


def download(url: str, dest: Path) -> bool:
    req = Request(url, headers={"User-Agent": "TechStoreImageFetcher/1.0"})
    try:
        with urlopen(req, timeout=30) as resp:
            data = resp.read()
        if len(data) < 1000:
            print(f"SKIP (too small): {dest.name}")
            return False
        dest.write_bytes(data)
        print(f"OK {dest.name} ({len(data)} bytes)")
        return True
    except Exception as exc:
        print(f"FAIL {dest.name}: {exc}")
        return False


def main():
    print("Downloading images...")
    for name, url in IMAGES.items():
        download(url, OUT / name)

    app = create_app()
    with app.app_context():
        updated = 0
        for product in Product.query.all():
            filename = PRODUCT_IMAGES.get(product.name)
            if filename and (OUT / filename).exists():
                product.image = filename
                updated += 1
        db.session.commit()
        print(f"Updated {updated} products in database.")


if __name__ == "__main__":
    main()
