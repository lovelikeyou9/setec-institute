<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>@yield('title', 'Seller Center — MMT')</title>
  <meta name="csrf-token" content="{{ csrf_token() }}"/>
  <link rel="stylesheet" href="{{ asset('css/storefront.css') }}?v=demo8"/>
  <link rel="icon" type="image/png" href="{{ asset('images/mme-icon.png') }}"/>
  @stack('head')
</head>
<body>
@include('partials.store-header')

@if(session('message'))
  <div class="container" style="padding-top:1rem"><div class="alert alert-success">{{ session('message') }}</div></div>
@endif
@if(session('error'))
  <div class="container" style="padding-top:1rem"><div class="alert alert-error">{{ session('error') }}</div></div>
@endif
@if(isset($errors) && $errors->any())
  <div class="container" style="padding-top:1rem">
    <div class="alert alert-error">
      <strong>Could not save:</strong>
      <ul style="margin:.35rem 0 0;padding-left:1.1rem">
        @foreach($errors->all() as $e)<li>{{ $e }}</li>@endforeach
      </ul>
    </div>
  </div>
@endif

<main class="main-content dash-shell">
  <div class="dash-layout">
    @include('partials.seller-sidebar')
    <div class="dash-main">
      @hasSection('heading')
        <h1 style="font-size:1.4rem;margin-bottom:.35rem">@yield('heading')</h1>
      @endif
      @yield('content')
    </div>
  </div>
</main>

@yield('modals')
@stack('modals')

@include('partials.store-footer')
</body>
</html>
