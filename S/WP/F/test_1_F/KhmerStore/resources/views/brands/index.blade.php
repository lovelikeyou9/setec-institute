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
            <div class="column-02" style="width: 10%;">Brand Name</div>
            <div class="column-03" style="width: 40%;">Description</div>
            <div class="column-04">Detail</div>
            <div class="column-05">Edit</div>
            <div class="column-06">Delete</div>
        </div>
        @forelse ($brands as $brand)
            <div class="row">
                <div class="column-01">{{ $brand->id }}</div>
                <div class="column-02" style="width: 10%;">{{ $brand->brand_name }}</div>
                <div class="column-03" style="width: 40%;">{{ $brand->description }}</div>
                <div class="column-04"><a href="{{ route('brands.show', $brand->id) }}">Detail</a></div>
                <div class="column-05"><a href="{{ route('brands.edit', $brand->id) }}">Edit</a></div>
                <div class="column-06"><a href="{{ route('brands.tempDeletion', $brand->id) }}">Delete</a></div>
            </div>
        @empty
            <h3>No data is available.</h3>
        @endforelse

    </div>
@endsection
