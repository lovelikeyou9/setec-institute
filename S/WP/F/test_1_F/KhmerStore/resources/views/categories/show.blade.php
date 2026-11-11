@extends('master.template')
@section('pageTitle')
    {{ $category->category_name }}
@endsection
@section('rightTitle')
    Detail information of <b>{{ {{ $category->category_name }} }}</b>
@endsection
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/table.css') }}">
@endsection

@section('content')
    Id: <b>{{ $category->id }}</b><br><br>
    Category name: <b>{{ $category->category_name }}</b><br><br>
    Description: <b>{{ $category->description }}</b><br><br>
    Status: <b>{{ $category->status }}</b><br><br>
    Date posted: <b>{{ $category->created_at }}</b><br><br>
    Last edited date: <b>{{ $category->updated_at }}</b><br><br>

    <form class="product-form" action="{{ route('categories.destroy', $category->id) }}" method="POST" enctype="multipart/form-data">
            @csrf
            @method('delete')
            <input type="submit" value="Delete" name="buttonDelete">
    </form>
@endsection
