@extends('master.template')
@section('pageTitle', "Products Listing")
@section('rightTitle', "Products Listing")
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/table.css') }}">
@endsection

@section('content')
    Id: <b>{{ $brand->id }}</b><br><br>
    Brand name: <b>{{ $brand->brand_name }}</b><br><br>
    Description: <b>{{ $brand->description }}</b><br><br>
    Date posted: <b>{{ $brand->created_at }}</b><br><br>
    Last edited date: <b>{{ $brand->updated_at }}</b><br><br>

    <form class="product-form" action="{{ route('brands.destroy', $brand->id) }}" method="POST" enctype="multipart/form-data">
            @csrf
            @method('delete')
            <input type="submit" value="Delete" name="buttonDelete">
    </form>
@endsection
