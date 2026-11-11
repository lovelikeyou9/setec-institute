{{-- @extends('master.template')
@section('pageTitle')
    Welcome to My CPanel
@endsection

@section('rightTitle')
    Welcome toKhmerStore Dashboard
@endsection

@section('content')

@endsection --}}


@extends('master.template')
@section('pageTitle', 'Welcome to my CPanel')
@section('rightTitle', 'Welcome toKhmerStore Dashboard')
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
        <div class="column-05">Detail</div>
        <div class="column-06">Edit</div>
        <div class="column-07">Delete</div>
    </div>

    <div class="row">
        <div class="column-01">1</div>
        <div class="column-02">Apple 001</div>
        <div class="column-03">Apple</div>
        <div class="column-04">300</div>
        <div class="column-05"><img src="{{ asset('image/Battlefield_6_cover_art.jpg') }}" style="width: 60px;" alt="No Image"></div>
        <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</div>
        <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</div>
        <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</div>
    </div>


    <div class="row">
        <div class="column-01">1</div>
        <div class="column-02">Apple 001</div>
        <div class="column-03">Apple</div>
        <div class="column-04">300</div>
        <div class="column-05"><img src="{{ asset('image/Battlefield_6_cover_art.jpg') }}" style="width: 60px;" alt="No Image"></div>
        <div class="column-06"><a href="{{ route('products.show', 9) }}">Detail</div>
        <div class="column-07"><a href="{{ route('products.edit', 9) }}">Edit</div>
        <div class="column-08"><a href="{{ route('products.remove', 9) }}">Delete</div>
    </div>


</div>

@endsection
