<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fluid And Responsive Layouts</title>
    <title>@yield('pageTitle')</title>
    <link rel="stylesheet" href="{{ asset('css/template.css') }}">
    @yield('headerBlock')
</head>
<body>
    <div id="wrapper">
        <div id="left">
            <h1>Admin Panel</h1>
            <nav>
                <ul>
                    <li><a href="{{ route('home') }}">Home</a>|</li>
                    <li><a href="#">Website</a>|</li>
                    <li><a href="#">Logout</a></li>
                </ul>
            </nav>
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
                    <li><a href="#">Desktops</a></li>
                    <li><a href="#">Laptops</a></li>
                    <li><a href="#">Photocopies</a></li>
                    <li><a href="#">Printers</a></li>
                    <li><a href="#">Accessories</a></li>
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
                <h2>@yield("rightTitle")</h2>
            </div>
            <div id="toolbar">@yield("toolbar")</div>
            @yield("content")
        </div>
    </div>
</body>
</html>




{{--
<div id="pagination">Start Previous 1 2 3 4 5...Next End</div>

<div class="table">
                <div class="row rowHeading">
                    <div class="column-01">ID</div>
                    <div class="column-02">Model</div>
                    <div class="column-03">Brand Name</div>
                    <div class="column-04">Price</div>
                    <div class="column-05">Description</div>
                </div>
                <div class="row">
                    <div class="column-01">1</div>
                    <div class="column-02">Apple 001</div>
                    <div class="column-03">Apple</div>
                    <div class="column-04">300</div>
                    <div class="column-05">Lorem ipsum, dolor sit amet consectetur adipisicing elit.</div>
                </div>
                <div class="row">
                    <div class="column-01">2</div>
                    <div class="column-02">Dell 001</div>
                    <div class="column-03">Dell</div>
                    <div class="column-04">300</div>
                    <div class="column-05">Lorem ipsum, dolor sit amet consectetur adipisicing elit.</div>
                </div>
                <div class="row">
                    <div class="column-01">3</div>
                    <div class="column-02">Dell 001</div>
                    <div class="column-03">Dell</div>
                    <div class="column-04">300</div>
                    <div class="column-05">Lorem ipsum, dolor sit amet consectetur adipisicing elit.</div>
                </div>
                <div class="row">
                    <div class="column-01">4</div>
                    <div class="column-02">Dell 001</div>
                    <div class="column-03">Dell</div>
                    <div class="column-04">300</div>
                    <div class="column-05">Lorem ipsum, dolor sit amet consectetur adipisicing elit.</div>
                </div>
                <div class="row">
                    <div class="column-01">5</div>
                    <div class="column-02">Dell 001</div>
                    <div class="column-03">Dell</div>
                    <div class="column-04">300</div>
                    <div class="column-05">Lorem ipsum, dolor sit amet consectetur adipisicing elit.</div>
                </div>
            </div> --}}
