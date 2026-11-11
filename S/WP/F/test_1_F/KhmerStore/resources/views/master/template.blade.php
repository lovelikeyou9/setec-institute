<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('pageTitle')</title>
    <link rel="stylesheet" href="{{ asset('css/template.css') }}">
    <link rel="stylesheet" href="{{ asset('css/photos.css') }}">
    @yield('headerBlock')
</head>
<body>
    <div id="wrapper">
        <div id="left">
            <h1>Admin Panel</h1>
            <nav>
                <ul>
                    <li><a href="{{ route('home') }}">Home</a>|</li>
                    <li><a href="{{ route('store.home') }}" target="_blank">Website</a>|</li>
                    <li>
                        <form action="{{ route('logout') }}" method="POST" style="display:inline;">
                            @csrf
                            <button type="submit" class="logout-btn">Logout</button>
                        </form>
                    </li>
                </ul>
            </nav>
            @auth
                <p class="logged-user">Hi, {{ Auth::user()->name }}</p>
            @endauth

            <div class="box">
                <h2>Product Manager</h2>
                <ul>
                    <li><a href="{{ route('products.index') }}">Products Listing</a></li>
                    <li><a href="{{ route('products.create') }}">Add New Product</a></li>
                    <li><a href="{{ route('products.search') }}">Products Searching</a></li>
                </ul>
            </div>

            <div class="box">
                <h2>User Manager</h2>
                <ul>
                    <li><a href="#">User Listing</a></li>
                    <li><a href="#">Add New User</a></li>
                </ul>
            </div>
            <div class="box">
                <h2>Category Manager</h2>
                <ul>
                    <li><a href="{{ route('categories.index') }}">Categories Listing</a></li>
                    <li><a href="{{ route('categories.create') }}">Add New Category</a></li>
                </ul>
            </div>
            <div class="box">
                <h2>Brand Manager</h2>
                <ul>
                    <li><a href="{{ route('brands.index') }}">Brands Listing</a></li>
                    <li><a href="{{ route('brands.create') }}">Add New Brand</a></li>
                </ul>
            </div>
        </div>
        <div id="right">
            <div id="rightHeader">
                <div id="hamburgerMenu">
                    <input type="checkbox" name="chkHamburgerMenu" id="chkHamburgerMenu">
                    <label for="chkHamburgerMenu">
                        <div class="line-01"></div>
                        <div class="line-02"></div>
                        <div class="line-03"></div>
                    </label>
                    <div id="wholeLinks">
                        <div class="links">
                            <div class="box">
                                <h2>Category Manager</h2>
                                <ul>
                                    <li><a href="#">Desktops</a></li>
                                    <li><a href="#">Laptops</a></li>
                                    <li><a href="#">Photocopies</a></li>
                                    <li><a href="#">Printers</a></li>
                                    <li><a href="#">Accessories</a></li>
                                </ul>
                            </div>
                            <div class="box">
                                <h2>Category Manager</h2>
                                <ul>
                                    <li><a href="#">Desktops</a></li>
                                    <li><a href="#">Laptops</a></li>
                                    <li><a href="#">Photocopies</a></li>
                                    <li><a href="#">Printers</a></li>
                                    <li><a href="#">Accessories</a></li>
                                </ul>
                            </div>
                            <div class="box">
                                <h2>Category Manager</h2>
                                <ul>
                                    <li><a href="#">Desktops</a></li>
                                    <li><a href="#">Laptops</a></li>
                                    <li><a href="#">Photocopies</a></li>
                                    <li><a href="#">Printers</a></li>
                                    <li><a href="#">Accessories</a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <h1>@yield("rightTitle")</h1>
            </div>
            <div id="toolbar">@yield("toolbar")</div>
            <main>
                @yield("content")
            </main>
            <footer></footer>
        </div>
    </div>

    @if(session('success') || session('error'))
        <div class="flash-popup {{ session('success') ? 'success' : 'error' }}" id="flashPopup">
            <button type="button" class="flash-close" onclick="document.getElementById('flashPopup').remove()">&times;</button>
            {{ session('success') ?? session('error') }}
        </div>
        <script>
            setTimeout(function () {
                var el = document.getElementById('flashPopup');
                if (el) el.remove();
            }, 3500);
        </script>
    @endif

    @yield('footerBlock')
</body>
</html>
