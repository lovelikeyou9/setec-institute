@extends('master.template')
@section('pageTitle', "Categories Listing")
@section('rightTitle', "Categories Listing")
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/table.css') }}">
@endsection

@section('content')
    <div class="table">
        <div class="row rowHeading">
            <div class="column-01">ID</div>
            <div class="column-02" style="width: 10%;">Category Name</div>
            <div class="column-03" style="width: 35%;">Description</div>
            <div class="column-03" style="width: 5%;">Status</div>
            <div class="column-04">Detail</div>
            <div class="column-05">Edit</div>
            <div class="column-06">Delete</div>
        </div>
        @forelse ($categories as $category)
            <div class="row">
                <div class="column-01">{{ $category->id }}</div>
                <div class="column-02" style="width: 10%;">{{ $category->category_name }}</div>
                <div class="column-03" style="width: 35%;">{{ $category->description }}</div>
                <div class="column-03" style="width: 5%;">{{ $category->status }}</div>
                <div class="column-04"><a href="{{ route('categories.show', $category->id) }}">Detail</a></div>
                <div class="column-05"><a href="{{ route('categories.edit', $category->id) }}">Edit</a></div>
                <div class="column-06"><a href="{{ route('categories.tempDeletion', $category->id) }}">Delete</a></div>
            </div>
        @empty
            <h3>No data is available.</h3>
        @endforelse

    </div>
@endsection
