@extends('master.template')
@section('pageTitle', "Products Listing")
@section('rightTitle', "Products Listing")
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/table.css') }}">
@endsection

@section('content')
    <div class="table">
        <div class="row rowHeading">
            <div class="column-01">ID</div>
            <div class="column-02">Model</div>
            <div class="column-03">Brand Name</div>
            <div class="column-04">Price</div>
            <div class="column-05">Photo</div>
            <div class="column-06">Detail</div>
            <div class="column-07">Edit</div>
            <div class="column-08">Delete</div>
        </div>

        <div class="row">
            <div class="column-01">1</div>
            <div class="column-02">Apple 001</div>
            <div class="column-03">Apple</div>
            <div class="column-04">300</div>
            <div class="column-05"><img src="{{ asset('images/teddy-bears.webp') }}" style="width: 60px;" alt=""></div>
            <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</a></div>
            <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</a></div>
            <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</a></div>
        </div>
        <div class="row">
            <div class="column-01">1</div>
            <div class="column-02">Apple 001</div>
            <div class="column-03">Apple</div>
            <div class="column-04">300</div>
            <div class="column-05"><img src="{{ asset('images/teddy-bears.webp') }}" style="width: 60px;" alt=""></div>
            <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</a></div>
            <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</a></div>
            <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</a></div>
        </div>
        <div class="row">
            <div class="column-01">1</div>
            <div class="column-02">Apple 001</div>
            <div class="column-03">Apple</div>
            <div class="column-04">300</div>
            <div class="column-05"><img src="{{ asset('images/teddy-bears.webp') }}" style="width: 60px;" alt=""></div>
            <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</a></div>
            <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</a></div>
            <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</a></div>
        </div>
        <div class="row">
            <div class="column-01">1</div>
            <div class="column-02">Apple 001</div>
            <div class="column-03">Apple</div>
            <div class="column-04">300</div>
            <div class="column-05"><img src="{{ asset('images/teddy-bears.webp') }}" style="width: 60px;" alt=""></div>
            <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</a></div>
            <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</a></div>
            <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</a></div>
        </div>
        <div class="row">
            <div class="column-01">1</div>
            <div class="column-02">Apple 001</div>
            <div class="column-03">Apple</div>
            <div class="column-04">300</div>
            <div class="column-05"><img src="{{ asset('images/teddy-bears.webp') }}" style="width: 60px;" alt=""></div>
            <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</a></div>
            <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</a></div>
            <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</a></div>
        </div>
        <div class="row">
            <div class="column-01">1</div>
            <div class="column-02">Apple 001</div>
            <div class="column-03">Apple</div>
            <div class="column-04">300</div>
            <div class="column-05"><img src="{{ asset('images/teddy-bears.webp') }}" style="width: 60px;" alt=""></div>
            <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</a></div>
            <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</a></div>
            <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</a></div>
        </div>
        <div class="row">
            <div class="column-01">1</div>
            <div class="column-02">Apple 001</div>
            <div class="column-03">Apple</div>
            <div class="column-04">300</div>
            <div class="column-05"><img src="{{ asset('images/teddy-bears.webp') }}" style="width: 60px;" alt=""></div>
            <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</a></div>
            <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</a></div>
            <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</a></div>
        </div>

    </div>
@endsection
