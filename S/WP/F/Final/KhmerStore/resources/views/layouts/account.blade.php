<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>@yield('title', 'My Account — MMT')</title>
  <meta name="csrf-token" content="{{ csrf_token() }}"/>
  <link rel="stylesheet" href="{{ asset('css/storefront.css') }}?v=demo10"/>
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

<main class="main-content dash-shell">
  <div class="dash-layout">
    @include('partials.dash-sidebar')
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
